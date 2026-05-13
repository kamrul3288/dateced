package com.iamkamrul.dateced

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

// ======================================================================
// Read-only interface — everything that doesn't mutate state
// ======================================================================

/**
 * Read-only view of a moment in time.
 *
 * Exposes getters, all pre-built format shortcuts, comparison helpers,
 * time-difference calculation, and from-now description — but NOT
 * timezone conversion or arithmetic (those live in [DateCedConvertible]).
 *
 * Both [DateCed] and the result of [DateCed.toUTC] / [DateCed.toLocal] implement this
 * interface, so formatting works identically regardless of zone.
 */
interface DateCedReadable {

    // ---- Core values ----
    val instant: Instant
    val timeZone: TimeZone
    val localDateTime: LocalDateTime
    val localDate: LocalDate
    val localTime: LocalTime

    // ---- Date/Time components ----
    val millisecond: Int
    val second: Int
    val minute: Int
    val hour: Int
    val year: Int
    val month: Month
    val monthNumber: Int
    val dayOfWeek: DayOfWeek
    val dayOfMonth: Int
    val dayOfYear: Int
    val epochMilliseconds: Long

    /** Approximate week-of-year (1–53). Not ISO 8601 strict. */
    val weekOfYear: Int

    /** Number of days in the current month, accounting for leap years. */
    val daysInMonth: Int

    // ---- Boolean states ----

    /** True when this moment is strictly before [Clock.System.now()]. */
    val isPast: Boolean

    /** True when this moment is strictly after [Clock.System.now()]. */
    val isFuture: Boolean

    /** True when this date falls on today's calendar date in [timeZone]. */
    val isToday: Boolean

    /** True when this date falls on yesterday's calendar date in [timeZone]. */
    val isYesterday: Boolean

    /** True when this date falls on tomorrow's calendar date in [timeZone]. */
    val isTomorrow: Boolean

    /** True when [dayOfWeek] is Saturday or Sunday. */
    val isWeekend: Boolean

    /** True when [dayOfWeek] is Monday through Friday. */
    val isWeekday: Boolean

    // ---- Formatting — custom pattern ----

    /** Format as a full date-time string using a Unicode TR35 [pattern]. */
    fun format(pattern: String, zoneId: TimeZoneId = TimeZoneId.LOCAL): String

    /** Format as a date-only string using a Unicode TR35 [pattern]. */
    fun formatDate(pattern: String, zoneId: TimeZoneId = TimeZoneId.LOCAL): String

    /** Format as a time-only string using a Unicode TR35 [pattern]. */
    fun formatTime(pattern: String, zoneId: TimeZoneId = TimeZoneId.LOCAL): String

    // ---- Formatting — pre-built shortcuts ----

    /** Full day name, e.g. "Monday". */
    val dayName: String

    /** Zero-padded day of month, e.g. "05". */
    val d: String

    /** 4-digit year, e.g. "2025". */
    val y: String

    /** Abbreviated month name, e.g. "Jan". */
    val m: String

    /** "dd MMM yyyy" → "25 Jan 2025" */
    val dMy: String

    /** "dd MMM" → "25 Jan" */
    val dM: String

    /** "yyyy-MM-dd" → SQL date */
    val sqlYMd: String

    /** "yyyy-MM-dd HH:mm:ss" → SQL datetime */
    val sqlYMdHms: String

    /** "yyyy-MM-dd HH:mm" → SQL datetime without seconds */
    val sqlYMdHm: String

    /** "dd MMM yyyy HH:mm:ss" → "25 Jan 2025 14:30:00" */
    val dMyHms: String

    /** "dd MMM yyyy HH:mm" */
    val dMyHm: String

    /** "HH:mm:ss" 24-hour */
    val hms24: String

    /** "HH:mm" 24-hour */
    val hm24: String

    /** "HH" 24-hour */
    val h24: String

    /** "hh:mm AM/PM" 12-hour */
    val hmA: String

    /** "hh:mm:ss AM/PM" 12-hour */
    val hmsA: String

    /** "dd MMM yyyy hh:mm:ss AM/PM" */
    val dMyHmsA: String

    /** "dd MMM yyyy hh:mm AM/PM" */
    val dMyHmA: String

    /** "hh:mm AM/PM dd MMM yyyy" */
    val hmADmY: String

    /** "hh:mm:ss AM/PM dd MMM yyyy" */
    val hmsADmY: String

    /** "HH:mm:ss dd MMM yyyy" */
    val hms24DmY: String

    /** "HH:mm dd MMM yyyy" */
    val hm24DmY: String

    // ---- Comparison — DateCed overloads ----

    fun isBefore(other: DateCed): Boolean
    fun isAfter(other: DateCed): Boolean
    fun isEqual(other: DateCed): Boolean
    fun isEqualOrBefore(other: DateCed): Boolean
    fun isEqualOrAfter(other: DateCed): Boolean

    /** Strictly between [start] and [end] (exclusive). */
    fun isBetween(start: DateCed, end: DateCed): Boolean

    /** Between [start] and [end], inclusive at both ends. */
    fun isEqualOrBetween(start: DateCed, end: DateCed): Boolean

    /** Gregorian leap-year check for [year]. */
    fun isLeapYear(): Boolean

    // ---- Comparison — String overloads ----

    fun isBefore(dateTimeString: String, pattern: String? = null, zoneId: TimeZoneId = TimeZoneId.LOCAL): Boolean
    fun isAfter(dateTimeString: String, pattern: String? = null, zoneId: TimeZoneId = TimeZoneId.LOCAL): Boolean
    fun isEqual(dateTimeString: String, pattern: String? = null, zoneId: TimeZoneId = TimeZoneId.LOCAL): Boolean
    fun isEqualOrBefore(dateTimeString: String, pattern: String? = null, zoneId: TimeZoneId = TimeZoneId.LOCAL): Boolean
    fun isEqualOrAfter(dateTimeString: String, pattern: String? = null, zoneId: TimeZoneId = TimeZoneId.LOCAL): Boolean
    fun isBetween(startString: String, endString: String, pattern: String? = null, zoneId: TimeZoneId = TimeZoneId.LOCAL): Boolean
    fun isEqualOrBetween(startString: String, endString: String, pattern: String? = null, zoneId: TimeZoneId = TimeZoneId.LOCAL): Boolean

    // ---- Time difference ----

    /** Absolute difference from [other] in [unit]. Always non-negative. */
    fun timeDifference(other: DateCed, unit: TimeDifferenceUnit): Long
    fun timeDifference(dateTimeString: String, unit: TimeDifferenceUnit, pattern: String? = null, zoneId: TimeZoneId = TimeZoneId.LOCAL): Long
    fun timeDifference(epochMillis: Long, unit: TimeDifferenceUnit, zoneId: TimeZoneId = TimeZoneId.LOCAL): Long

    // ---- From Now ----

    /**
     * Returns a (value, unit) pair for human-readable relative time.
     * Does NOT indicate past vs future — use [fromNowInterval] for direction.
     */
    fun fromNow(unit: FromNowUnit = FromNowUnit.DEFAULT): Pair<Long, FromNowLocalizeUnit>

    /**
     * Returns a [DateCedInterval] with value, unit, and [DateCedInterval.isPast] direction.
     *
     * ```kotlin
     * val interval = date.fromNowInterval()
     * val label = if (interval.isPast) "${interval.value} ${interval.unit} ago"
     *             else "in ${interval.value} ${interval.unit}"
     * ```
     */
    fun fromNowInterval(unit: FromNowUnit = FromNowUnit.DEFAULT): DateCedInterval
}

// ======================================================================
// Convertible interface — zone conversion + arithmetic
// ======================================================================

/**
 * Extends [DateCedReadable] with timezone conversion and date arithmetic.
 *
 * Only [DateCed] implements this interface. The result of [toUTC]/[toLocal]/[toGMT]
 * returns a plain [DateCedReadable], which intentionally does NOT allow further
 * zone conversion — preventing nonsensical chains like `now.toUTC().toUTC()`.
 */
interface DateCedConvertible : DateCedReadable {

    // ---- Zone conversion ----

    /**
     * View this instant through a different timezone lens.
     * The underlying [instant] is unchanged — only the display zone changes.
     */
    fun withTimeZone(zoneId: TimeZoneId): DateCedReadable

    /** Overload accepting a raw kotlinx [TimeZone]. */
    fun withTimeZone(tz: TimeZone): DateCedReadable

    fun toLocal(): DateCedReadable
    fun toUTC(): DateCedReadable
    fun toGMT(): DateCedReadable

    // ---- Arithmetic ----

    /** Return a new [DateCed] with the given amounts added. No-op if all are zero. */
    fun plus(
        seconds: Long = 0, minutes: Long = 0, hours: Long = 0,
        days: Long = 0, weeks: Long = 0, months: Int = 0, years: Int = 0,
    ): DateCed

    /** Return a new [DateCed] with the given amounts subtracted. No-op if all are zero. */
    fun minus(
        seconds: Long = 0, minutes: Long = 0, hours: Long = 0,
        days: Long = 0, weeks: Long = 0, months: Int = 0, years: Int = 0,
    ): DateCed

    // ---- Boundary navigation ----

    /** Return a new [DateCed] set to 00:00:00 of the same calendar day. */
    fun startOfDay(): DateCed

    /** Return a new [DateCed] set to 23:59:59.999999999 of the same calendar day. */
    fun endOfDay(): DateCed

    /** Return a new [DateCed] set to the 1st of the same month at 00:00:00. */
    fun startOfMonth(): DateCed

    /** Return a new [DateCed] set to the last day of the same month at 23:59:59.999999999. */
    fun endOfMonth(): DateCed

    /** Return a new [DateCed] set to January 1st 00:00:00 of the same year. */
    fun startOfYear(): DateCed

    /** Return a new [DateCed] set to December 31st 23:59:59.999999999 of the same year. */
    fun endOfYear(): DateCed

    /**
     * Return a new [DateCed] set to midnight of the most-recent [weekStart] day.
     *
     * Default is [WeekStart.MONDAY] (ISO 8601).
     */
    fun startOfWeek(weekStart: WeekStart = WeekStart.MONDAY): DateCed

    // ---- Field setters ----

    /** Return a new [DateCed] with the year changed. Day is clamped to the new month length. */
    fun withYear(year: Int): DateCed

    /** Return a new [DateCed] with the month changed (1–12). Day is clamped if needed. */
    fun withMonth(month: Int): DateCed

    /** Return a new [DateCed] with the day-of-month changed. Must be valid for the current month. */
    fun withDayOfMonth(day: Int): DateCed

    /** Return a new [DateCed] with the hour changed (0–23). */
    fun withHour(hour: Int): DateCed

    /** Return a new [DateCed] with the minute changed (0–59). */
    fun withMinute(minute: Int): DateCed

    /** Return a new [DateCed] with the second changed (0–59). */
    fun withSecond(second: Int): DateCed
}

// ======================================================================
// Extension — convert any DateCedReadable back to DateCed
// ======================================================================

/**
 * Convert any [DateCedReadable] back to a mutable [DateCed].
 *
 * Useful when you receive the result of `toUTC()` / `withTimeZone()` (which returns
 * [DateCedReadable]) and need to perform further arithmetic.
 *
 * The original [DateCedReadable.timeZone] is preserved.
 */
fun DateCedReadable.toDateCed(): DateCed = when (this) {
    is DateCed -> this
    else       -> DateCed.from(instant, timeZone)
}
