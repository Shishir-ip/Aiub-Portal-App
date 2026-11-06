package com.shishir.aiubportal.gpa

import com.shishir.aiubportal.util.Http
import org.jsoup.Jsoup
import java.net.URL

data class CourseGrade(
    val code: String,
    val name: String,
    val semester: String,
    val grade: String
)

data class SemesterGrades(
    val name: String,
    val rows: List<CourseGrade>
) {
    /** Simple (unweighted) average of grade points across the semester's graded courses. */
    val averagePoints: Double
        get() {
            val pts = rows.mapNotNull { gradePoints[it.grade.trim().uppercase()] }
            return if (pts.isEmpty()) 0.0 else pts.sum() / pts.size
        }
}

data class GpaReport(
    val cgpa: String,
    val semesters: List<SemesterGrades>
)

class NotLoggedInException : Exception("Portal session expired")

val gradePoints = mapOf(
    "A+" to 4.00, "A" to 3.75, "A-" to 3.5, "B+" to 3.25, "B" to 3.0, "B-" to 2.75,
    "C+" to 2.5, "C" to 2.25, "C-" to 2.0, "D+" to 1.75, "D" to 1.5, "F" to 0.0
)

object GpaRepository {

    /** Fetches the dashboard to discover the grade-report link (session token). */
    fun gradeReportUrl(): String {
        val home = Http.get("https://portal.aiub.edu/Student")
        if (SessionGuard.isLoginPage(home.finalUrl, home.body)) throw NotLoggedInException()
        val doc = Jsoup.parse(home.body, "https://portal.aiub.edu")
        val link = doc.selectFirst("#navigation-bar a[href*='/Student/GradeReport/ByCurriculum']")
            ?: doc.selectFirst("a[href*='/Student/GradeReport/ByCurriculum']")
            ?: throw IllegalStateException("Grade report link not found")
        return link.attr("abs:href")
    }

    fun fetch(): GpaReport {
        val reportUrl = gradeReportUrl()
        val result = Http.get(reportUrl)
        if (SessionGuard.isLoginPage(result.finalUrl, result.body)) throw NotLoggedInException()
        return parse(result.body)
    }

    fun parse(html: String): GpaReport {
        val doc = Jsoup.parse(html, "https://portal.aiub.edu")

        var cgpa = "-"
        for (row in doc.select("tr")) {
            val cells = row.select("td, th").map { it.text().trim() }
            if (cells.any { it.equals("Cgpa", true) || it.equals("CGPA", true) }) {
                cgpa = cells.lastOrNull { it.isNotBlank() && it != ":" && !it.equals("Cgpa", true) } ?: "-"
                break
            }
        }

        /* Grade rows live in tables whose third column ends with "[Grade]".
           Semesters come from the "(2024-2025, Fall) [A+]" part of that cell. */
        val semesters = LinkedHashMap<String, MutableList<CourseGrade>>()
        for (row in doc.select("table tr")) {
            val cells = row.select("td")
            if (cells.size < 3) continue
            val third = cells[2].text().trim()
            val m = Regex("^\\(([^)]+)\\)\\s*\\[([^\\]]+)\\]$").find(third) ?: continue
            val semester = m.groupValues[1].trim()
            val grade = m.groupValues[2].trim()
            val code = cells[0].text().trim()
            val name = cells[1].text().trim()
            if (code.isBlank() || name.isBlank() || semester.isBlank()) continue
            semesters.getOrPut(semester) { mutableListOf() }
                .add(CourseGrade(code, name, semester, grade))
        }

        return GpaReport(
            cgpa = cgpa,
            semesters = semesters.entries.reversed().map { SemesterGrades(it.key, it.value) }
        )
    }
}

object SessionGuard {
    fun isLoginPage(url: String, body: String): Boolean {
        val path = runCatching { URL(url).path }.getOrDefault(url)
        if (path == "/" || path.startsWith("/Login") || path.startsWith("/Account/Login")) return true
        return body.contains("Sign in with your organizational")
    }
}
