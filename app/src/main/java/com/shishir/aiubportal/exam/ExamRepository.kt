package com.shishir.aiubportal.exam

import com.shishir.aiubportal.util.Http
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ExamEntry(
    val rowText: String,
    val dateTime: LocalDateTime?
)

data class ExamReport(
    val sections: List<Pair<String, List<ExamEntry>>>
)

class NotLoggedInException : Exception("Portal session expired")

object ExamRepository {

    fun examScheduleUrl(): String {
        val home = Http.get("https://portal.aiub.edu/Student")
        if (SessionGuard.isLoginPage(home.finalUrl, home.body)) throw NotLoggedInException()
        val doc = Jsoup.parse(home.body, "https://portal.aiub.edu")
        val link = doc.selectFirst("a[href*='ExamRoutine']")
            ?: doc.selectFirst("a[href*='ExamRoutineSchedule']")
            ?: throw IllegalStateException("Exam routine link not found")
        return link.attr("abs:href")
    }

    fun fetch(): ExamReport {
        val url = examScheduleUrl()
        val result = Http.get(url)
        if (SessionGuard.isLoginPage(result.finalUrl, result.body)) throw NotLoggedInException()
        return parse(result.body)
    }

    fun parse(html: String): ExamReport {
        val doc = Jsoup.parse(html, "https://portal.aiub.edu")
        val dateRegex = Regex("(\\d{1,2}[-/ ](?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*[-/ ]\\d{2,4})", RegexOption.IGNORE_CASE)
        val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mma", Locale.US)
        val formatterDateOnly = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US)

        val sections = ArrayList<Pair<String, List<ExamEntry>>>()
        val blocks = doc.select("table").ifEmpty { doc.select("div.panel") }

        for (table in blocks) {
            val rows = ArrayList<ExamEntry>()
            for (row in table.select("tr")) {
                val cells = row.select("td, th").map { it.text().trim() }.filter { it.isNotBlank() }
                if (cells.isEmpty() || cells.size < 2) continue
                val rowText = cells.joinToString("  ·  ")
                if (rowText.length < 8) continue

                val dateMatch = dateRegex.find(rowText)
                val timeMatch = Regex("(\\d{1,2}:\\d{2}\\s*[AP]M)", RegexOption.IGNORE_CASE).find(rowText)
                val dateTime = runCatching {
                    if (dateMatch != null && timeMatch != null) {
                        LocalDateTime.parse(
                            dateMatch.value.replace("/", "-") + " " +
                                timeMatch.value.replace(" ", "").uppercase(),
                            formatter
                        )
                    } else if (dateMatch != null) {
                        LocalDateTime.parse(dateMatch.value.replace("/", "-"), formatterDateOnly).atStartOfDay()
                    } else null
                }.getOrNull()

                rows.add(ExamEntry(rowText = rowText, dateTime = dateTime))
            }
            if (rows.isNotEmpty()) {
                val header = table.closest(".panel")?.select(".panel-title, .panel-heading")?.text()?.trim()
                    ?.takeIf { it.isNotBlank() } ?: "Exam schedule"
                sections.add(header to rows)
            }
        }
        return ExamReport(sections)
    }
}
