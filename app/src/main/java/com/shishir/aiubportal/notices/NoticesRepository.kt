package com.shishir.aiubportal.notices

import com.shishir.aiubportal.util.Http
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

data class Notice(
    val title: String,
    val desc: String,
    val date: String,
    val url: String
)

object NoticesRepository {

    const val LIST_URL = "https://www.aiub.edu/category/notices"
    const val SITE_BASE = "https://www.aiub.edu"

    /** Fetches and parses the official notice board. */
    fun fetch(): List<Notice> {
        val result = Http.get(LIST_URL)
        if (result.code !in 200..299) return emptyList()
        return parse(result.body)
    }

    fun parse(html: String): List<Notice> {
        val doc: Document = Jsoup.parse(html, SITE_BASE)
        val notices = ArrayList<Notice>()
        for (block in doc.select("div.notification")) {
            val title = block.selectFirst("h2.title")?.text()?.trim() ?: continue
            val desc = block.selectFirst("p.desc")?.text()?.trim() ?: ""
            val date = block.selectFirst(".date-custom")?.text()?.replace(Regex("\\s+"), " ")?.trim() ?: ""
            val link = block.closest("a[href]")?.attr("abs:href")
                ?: block.selectFirst("a.info-link")?.attr("abs:href") ?: continue
            notices.add(Notice(title = title, desc = desc, date = date, url = link))
        }
        return notices
    }
}
