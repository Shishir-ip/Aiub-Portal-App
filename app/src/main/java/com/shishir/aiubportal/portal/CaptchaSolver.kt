package com.shishir.aiubportal.portal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color as GColor
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream

/**
 * On-device captcha solver. The portal's login captcha is a dotted-font math
 * equation like "19+23=?". The image is preprocessed the same way as the
 * browser extension (4x upscale → threshold → despeckle → blur → re-threshold)
 * and recognized with Tesseract restricted to digits/operators.
 *
 * The parser is strict: anything that is not a clean "num op num" is rejected
 * (the caller refreshes the captcha instead of filling a guessed answer).
 */
object CaptchaSolver {

    private var initialized = false
    private lateinit var tess: TessBaseAPI

    fun ensureData(context: Context) {
        val dir = File(context.filesDir, "tessdata")
        if (!dir.exists()) dir.mkdirs()
        val target = File(dir, "eng.traineddata")
        if (!target.exists() || target.length() == 0L) {
            context.assets.open("tessdata/eng.traineddata.gz").use { input ->
                GZIPInputStream(input).use { gz ->
                    FileOutputStream(target).use { out -> gz.copyTo(out) }
                }
            }
        }
    }

    @Synchronized
    private fun ensureApi(context: Context): TessBaseAPI {
        if (!initialized) {
            ensureData(context)
            tess = TessBaseAPI()
            val ok = tess.init(File(context.filesDir, "tessdata").absolutePath, "eng")
            check(ok) { "Tesseract init failed" }
            tess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE)
            initialized = true
        }
        return tess
    }

    /** Entry point: returns the solved number, or null when unreadable. */
    fun solve(context: Context, source: Bitmap): Int? {
        val api = ensureApi(context)
        val processed = preprocess(source)
        api.setImage(processed)
        api.setVariable("tessedit_char_whitelist", "0123456789+-=?")
        val text = api.utF8Text.trim()
        api.clear()
        android.util.Log.d("AiubCaptcha", "OCR read: ${"$"}text")
        return parseMath(text)
    }

    private fun preprocess(source: Bitmap): Bitmap {
        val w = source.width * 4
        val h = source.height * 4
        val bmp = Bitmap.createScaledBitmap(source, w, h, false).copy(Bitmap.Config.ARGB_8888, true)

        threshold(bmp, 150)
        despeckle(bmp, minArea = 50)

        val blurred = bmp.copy(Bitmap.Config.ARGB_8888, false)
        val canvas = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawColor(GColor.WHITE)
        canvas.drawBitmap(blurred, 0f, 0f, paint)
        threshold(bmp, 230)
        return bmp
    }

    private fun threshold(bmp: Bitmap, cut: Int) {
        val w = bmp.width
        val h = bmp.height
        val pixels = IntArray(w * h)
        bmp.getPixels(pixels, 0, w, 0, 0, w, h)
        for (i in pixels.indices) {
            val p = pixels[i]
            val r = (p shr 16) and 0xff
            val g = (p shr 8) and 0xff
            val b = p and 0xff
            val intensity = (r * 299 + g * 587 + b * 114) / 1000
            val v = if (intensity > cut) 0xffffff else 0x000000
            pixels[i] = (0xff shl 24) or v
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h)
    }

    private fun despeckle(bmp: Bitmap, minArea: Int) {
        val w = bmp.width
        val h = bmp.height
        val pixels = IntArray(w * h)
        bmp.getPixels(pixels, 0, w, 0, 0, w, h)
        val isDark = BooleanArray(w * h)
        for (i in pixels.indices) isDark[i] = (pixels[i] and 0xffffff) == 0
        val visited = BooleanArray(w * h)

        for (y in 0 until h) {
            for (x in 0 until w) {
                val idx = y * w + x
                if (!isDark[idx] || visited[idx]) continue
                val queue = ArrayDeque<Int>()
                queue.add(idx)
                visited[idx] = true
                val blob = ArrayList<Int>()
                while (queue.isNotEmpty()) {
                    val curr = queue.removeFirst()
                    blob.add(curr)
                    val cx = curr % w
                    val cy = curr / w
                    for (dy in -1..1) for (dx in -1..1) {
                        if (dx == 0 && dy == 0) continue
                        val nx = cx + dx
                        val ny = cy + dy
                        if (nx in 0 until w && ny in 0 until h) {
                            val nIdx = ny * w + nx
                            if (isDark[nIdx] && !visited[nIdx]) {
                                visited[nIdx] = true
                                queue.add(nIdx)
                            }
                        }
                    }
                }
                if (blob.size < minArea) {
                    for (b in blob) pixels[b] = 0xffffffff.toInt()
                }
            }
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h)
    }

    private fun parseMath(text: String): Int? {
        var prep = text.split("=").firstOrNull() ?: return null
        prep = prep.replace(Regex("[^0-9+\\-]"), "")
        val m = Regex("^(\\d{1,2})([+-])(\\d{1,2})$").find(prep) ?: return null
        val a = m.groupValues[1].toInt()
        val b = m.groupValues[3].toInt()
        val result = if (m.groupValues[2] == "+") a + b else a - b
        return if (result in 0..100) result else null
    }
}
