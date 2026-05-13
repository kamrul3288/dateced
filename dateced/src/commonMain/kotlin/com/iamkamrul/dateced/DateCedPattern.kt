package com.iamkamrul.dateced

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

/**
 * Auto-detects the format of a raw date/time string using regex matching,
 * then delegates parsing to [DateCedFormat].
 *
 * All regexes use [kotlin.text.Regex] — fully multiplatform (no java.util.regex).
 *
 * Patterns are checked in order from most specific to least specific.
 * Date-only strings always produce a [LocalDateTime] with time = 00:00:00.
 *
 * To support additional formats, add a [PatternEntry] to [patterns].
 *
 * Marked [internal] — callers use [DateCed.parse] instead.
 */
internal object DateCedPattern {

    private enum class ParseMode { DATE_TIME, DATE_ONLY }

    private data class PatternEntry(
        val regex: Regex,
        val unicodePattern: String,
        val mode: ParseMode,
    )

    // Ordered from most specific to least specific.
    // DATE_TIME entries must come before their DATE_ONLY counterparts for the same separator.
    private val patterns: List<PatternEntry> = listOf(

        // ── yyyy-MM-dd ──────────────────────────────────────────────────────────
        PatternEntry(Regex("""^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$"""),  "yyyy-MM-dd HH:mm:ss", ParseMode.DATE_TIME),
        PatternEntry(Regex("""^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$"""),        "yyyy-MM-dd HH:mm",    ParseMode.DATE_TIME),
        PatternEntry(Regex("""^\d{4}-\d{2}-\d{2}$"""),                     "yyyy-MM-dd",          ParseMode.DATE_ONLY),

        // ── dd-MM-yyyy ──────────────────────────────────────────────────────────
        PatternEntry(Regex("""^\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2}$"""),  "dd-MM-yyyy HH:mm:ss", ParseMode.DATE_TIME),
        PatternEntry(Regex("""^\d{2}-\d{2}-\d{4} \d{2}:\d{2}$"""),        "dd-MM-yyyy HH:mm",    ParseMode.DATE_TIME),
        PatternEntry(Regex("""^\d{2}-\d{2}-\d{4}$"""),                     "dd-MM-yyyy",          ParseMode.DATE_ONLY),

        // ── dd/MM/yyyy ──────────────────────────────────────────────────────────
        PatternEntry(Regex("""^\d{2}/\d{2}/\d{4} \d{2}:\d{2}:\d{2}$"""),  "dd/MM/yyyy HH:mm:ss", ParseMode.DATE_TIME),
        PatternEntry(Regex("""^\d{2}/\d{2}/\d{4} \d{2}:\d{2}$"""),        "dd/MM/yyyy HH:mm",    ParseMode.DATE_TIME),
        PatternEntry(Regex("""^\d{2}/\d{2}/\d{4}$"""),                     "dd/MM/yyyy",          ParseMode.DATE_ONLY),

        // ── yyyy/MM/dd ──────────────────────────────────────────────────────────
        PatternEntry(Regex("""^\d{4}/\d{2}/\d{2} \d{2}:\d{2}:\d{2}$"""),  "yyyy/MM/dd HH:mm:ss", ParseMode.DATE_TIME),
        PatternEntry(Regex("""^\d{4}/\d{2}/\d{2}$"""),                     "yyyy/MM/dd",          ParseMode.DATE_ONLY),

        // ── dd MMM yyyy (e.g. "25 Jan 2025") ────────────────────────────────────
        PatternEntry(Regex("""^\d{2} [A-Za-z]{3} \d{4} \d{2}:\d{2}:\d{2}$"""), "dd MMM yyyy HH:mm:ss", ParseMode.DATE_TIME),
        PatternEntry(Regex("""^\d{2} [A-Za-z]{3} \d{4} \d{2}:\d{2}$"""),       "dd MMM yyyy HH:mm",    ParseMode.DATE_TIME),
        PatternEntry(Regex("""^\d{2} [A-Za-z]{3} \d{4}$"""),                   "dd MMM yyyy",          ParseMode.DATE_ONLY),

        // ── ISO 8601 with T separator (no Z — handled before auto-detect) ───────
        PatternEntry(Regex("""^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$"""),  "yyyy-MM-dd'T'HH:mm:ss", ParseMode.DATE_TIME),
        PatternEntry(Regex("""^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$"""),        "yyyy-MM-dd'T'HH:mm",    ParseMode.DATE_TIME),
    )

    /**
     * Auto-detect the format and parse the input string into a [LocalDateTime].
     * Date-only inputs produce midnight (00:00:00) as their time component.
     *
     * @throws IllegalArgumentException if no known pattern matches [input].
     */
    fun parseAutoDetect(input: String): LocalDateTime {
        for (entry in patterns) {
            if (!entry.regex.matches(input)) continue
            return when (entry.mode) {
                ParseMode.DATE_TIME -> DateCedFormat.dateTimeFormat(entry.unicodePattern).parse(input)
                ParseMode.DATE_ONLY -> {
                    val date = DateCedFormat.dateFormat(entry.unicodePattern).parse(input)
                    LocalDateTime(date, LocalTime(0, 0, 0))
                }
            }
        }
        throw IllegalArgumentException(
            "Cannot auto-detect date format for: '$input'. " +
                "Supported patterns: yyyy-MM-dd, dd-MM-yyyy, dd/MM/yyyy, yyyy/MM/dd, " +
                "dd MMM yyyy, and their datetime variants with HH:mm or HH:mm:ss. " +
                "Pass an explicit pattern to DateCed.parse() if needed."
        )
    }

    /**
     * Returns only the detected Unicode pattern string without parsing.
     * Useful for re-formatting a string in a different pattern.
     *
     * @throws IllegalArgumentException if no known pattern matches [input].
     */
    fun matchPatternString(input: String): String {
        for (entry in patterns) {
            if (entry.regex.matches(input)) return entry.unicodePattern
        }
        throw IllegalArgumentException(
            "Cannot auto-detect date format for: '$input'. Provide an explicit pattern."
        )
    }
}
