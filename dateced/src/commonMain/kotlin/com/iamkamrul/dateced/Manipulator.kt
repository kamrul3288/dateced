package com.iamkamrul.dateced

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Pure arithmetic operations on [Instant] values.
 *
 * Every function returns a NEW [Instant] — nothing is mutated.
 * Calendar-aware operations (months, years, startOf/endOf) require a [TimeZone] so they
 * respect DST transitions and varying month lengths.
 *
 * Marked [internal] — callers use the fluent API on [DateCed] instead.
 */
internal object Manipulator {

    // ======================================================================
    // Addition / Subtraction
    // ======================================================================

    /**
     * Add mixed duration/calendar units to [instant].
     *
     * Duration-based units (seconds … weeks) are added first as wall-clock time.
     * Calendar-based units (months, years) are applied second in [timeZone] context,
     * ensuring correct handling of DST and month-length differences.
     */
    fun plus(
        instant: Instant,
        timeZone: TimeZone,
        seconds: Long = 0,
        minutes: Long = 0,
        hours: Long = 0,
        days: Long = 0,
        weeks: Long = 0,
        months: Int = 0,
        years: Int = 0,
    ): Instant {
        var result = instant
            .plus(seconds.seconds)
            .plus(minutes.minutes)
            .plus(hours.hours)
            .plus(days.days)
            .plus((weeks * 7L).days)

        if (months != 0 || years != 0) {
            result = result.plus(DateTimePeriod(months = months, years = years), timeZone)
        }
        return result
    }

    /** Subtract mixed duration/calendar units. Delegates to [plus] with negated values. */
    fun minus(
        instant: Instant,
        timeZone: TimeZone,
        seconds: Long = 0,
        minutes: Long = 0,
        hours: Long = 0,
        days: Long = 0,
        weeks: Long = 0,
        months: Int = 0,
        years: Int = 0,
    ): Instant = plus(instant, timeZone, -seconds, -minutes, -hours, -days, -weeks, -months, -years)

    // ======================================================================
    // Start-of / End-of helpers
    // ======================================================================

    /** Midnight (00:00:00.000) of the same calendar day in [tz]. */
    fun startOfDay(instant: Instant, tz: TimeZone): Instant {
        val ldt = instant.toLocalDateTime(tz)
        return LocalDateTime(ldt.year, ldt.month.number, ldt.day, 0, 0, 0)
            .toInstant(tz)
    }

    /** Last nanosecond (23:59:59.999999999) of the same calendar day in [tz]. */
    fun endOfDay(instant: Instant, tz: TimeZone): Instant {
        val ldt = instant.toLocalDateTime(tz)
        return LocalDateTime(ldt.year, ldt.month.number, ldt.day, 23, 59, 59, 999_999_999)
            .toInstant(tz)
    }

    /** Midnight on the 1st of the same month in [tz]. */
    fun startOfMonth(instant: Instant, tz: TimeZone): Instant {
        val ldt = instant.toLocalDateTime(tz)
        return LocalDateTime(ldt.year, ldt.month.number, 1, 0, 0, 0)
            .toInstant(tz)
    }

    /** Last nanosecond of the last day of the same month in [tz]. */
    fun endOfMonth(instant: Instant, tz: TimeZone): Instant {
        val ldt = instant.toLocalDateTime(tz)
        val lastDay = Getter.daysInMonth(ldt.year, ldt.month.number)
        return LocalDateTime(ldt.year, ldt.month.number, lastDay, 23, 59, 59, 999_999_999)
            .toInstant(tz)
    }

    /** January 1st midnight of the same year in [tz]. */
    fun startOfYear(instant: Instant, tz: TimeZone): Instant {
        val year = instant.toLocalDateTime(tz).year
        return LocalDateTime(year, 1, 1, 0, 0, 0).toInstant(tz)
    }

    /** December 31st last nanosecond of the same year in [tz]. */
    fun endOfYear(instant: Instant, tz: TimeZone): Instant {
        val year = instant.toLocalDateTime(tz).year
        return LocalDateTime(year, 12, 31, 23, 59, 59, 999_999_999).toInstant(tz)
    }

    /**
     * Midnight of the most-recent [weekStart] day at or before [instant]'s date in [tz].
     *
     * Example: if today is Wednesday and [weekStart] is [WeekStart.MONDAY], returns
     * last Monday midnight.
     */
    fun startOfWeek(instant: Instant, tz: TimeZone, weekStart: WeekStart): Instant {
        val ldt = instant.toLocalDateTime(tz)
        val currentIso = ldt.dayOfWeek.isoDayNumber  // Monday=1 … Sunday=7
        val targetIso  = weekStart.isoDayNumber
        val daysBack   = (currentIso - targetIso + 7) % 7
        return startOfDay(minus(instant, tz, days = daysBack.toLong()), tz)
    }

    // ======================================================================
    // Field setters (immutable — return a new Instant)
    // ======================================================================

    /**
     * Return a new [Instant] with only the year changed.
     * The day is clamped to the new month length to avoid invalid dates
     * (e.g. March 29 → February 28 when switching to a non-leap year).
     */
    fun withYear(instant: Instant, tz: TimeZone, year: Int): Instant {
        val ldt = instant.toLocalDateTime(tz)
        val maxDay = Getter.daysInMonth(year, ldt.month.number)
        return LocalDateTime(year, ldt.month.number, ldt.day.coerceAtMost(maxDay),
            ldt.hour, ldt.minute, ldt.second).toInstant(tz)
    }

    /**
     * Return a new [Instant] with only the month changed (1–12).
     * The day is clamped to the new month length.
     */
    fun withMonth(instant: Instant, tz: TimeZone, month: Int): Instant {
        require(month in 1..12) { "month must be 1–12, got $month" }
        val ldt = instant.toLocalDateTime(tz)
        val maxDay = Getter.daysInMonth(ldt.year, month)
        return LocalDateTime(ldt.year, month, ldt.day.coerceAtMost(maxDay),
            ldt.hour, ldt.minute, ldt.second).toInstant(tz)
    }

    /**
     * Return a new [Instant] with only the day-of-month changed.
     *
     * @param day 1-based day; must be valid for the current month/year.
     */
    fun withDayOfMonth(instant: Instant, tz: TimeZone, day: Int): Instant {
        val ldt = instant.toLocalDateTime(tz)
        val maxDay = Getter.daysInMonth(ldt.year, ldt.month.number)
        require(day in 1..maxDay) { "day must be 1–$maxDay for ${ldt.month} ${ldt.year}, got $day" }
        return LocalDateTime(ldt.year, ldt.month.number, day,
            ldt.hour, ldt.minute, ldt.second).toInstant(tz)
    }

    /** Return a new [Instant] with only the hour changed (0–23). */
    fun withHour(instant: Instant, tz: TimeZone, hour: Int): Instant {
        require(hour in 0..23) { "hour must be 0–23, got $hour" }
        val ldt = instant.toLocalDateTime(tz)
        return LocalDateTime(ldt.year, ldt.month.number, ldt.day,
            hour, ldt.minute, ldt.second).toInstant(tz)
    }

    /** Return a new [Instant] with only the minute changed (0–59). */
    fun withMinute(instant: Instant, tz: TimeZone, minute: Int): Instant {
        require(minute in 0..59) { "minute must be 0–59, got $minute" }
        val ldt = instant.toLocalDateTime(tz)
        return LocalDateTime(ldt.year, ldt.month.number, ldt.day,
            ldt.hour, minute, ldt.second).toInstant(tz)
    }

    /** Return a new [Instant] with only the second changed (0–59). */
    fun withSecond(instant: Instant, tz: TimeZone, second: Int): Instant {
        require(second in 0..59) { "second must be 0–59, got $second" }
        val ldt = instant.toLocalDateTime(tz)
        return LocalDateTime(ldt.year, ldt.month.number, ldt.day,
            ldt.hour, ldt.minute, second).toInstant(tz)
    }

    // ======================================================================
    // Time difference
    // ======================================================================

    /**
     * Absolute difference between two instants in the given [unit].
     * Always returns a non-negative value regardless of argument order.
     */
    fun timeDifference(first: Instant, second: Instant, unit: TimeDifferenceUnit): Long {
        val diffMs = (first - second).inWholeMilliseconds
        val abs = if (diffMs < 0) -diffMs else diffMs
        return when (unit) {
            TimeDifferenceUnit.MILLISECOND -> abs
            TimeDifferenceUnit.SECOND      -> abs / 1_000
            TimeDifferenceUnit.MINUTES     -> abs / 60_000
            TimeDifferenceUnit.HOUR        -> abs / 3_600_000
            TimeDifferenceUnit.DAY         -> abs / 86_400_000
        }
    }

    // ======================================================================
    // From Now
    // ======================================================================

    /**
     * Compute a human-readable relative-time description as a (value, unit) pair.
     *
     * Thresholds mirror Carbon's conventions — see [fromNowInterval] for details.
     */
    fun fromNow(instant: Instant, unit: FromNowUnit): Pair<Long, FromNowLocalizeUnit> {
        val r = fromNowInterval(instant, unit)
        return Pair(r.value, r.unit)
    }

    /**
     * Like [fromNow] but returns a [DateCedInterval] that also carries direction
     * ([DateCedInterval.isPast]).
     *
     * Thresholds (Carbon-inspired):
     * - < 45 s   → "X seconds"
     * - 45–90 s  → "a minute"
     * - < 45 m   → "X minutes"
     * - 45–90 m  → "an hour"
     * - < 22 h   → "X hours"
     * - 22–36 h  → "a day"
     * - < 26 d   → "X days"
     * - 26–46 d  → "a month"
     * - < 11 mo  → "X months"
     * - 11–18 mo → "a year"
     * - else     → "X years"
     */
    fun fromNowInterval(instant: Instant, unit: FromNowUnit): DateCedInterval {
        val now = Clock.System.now()
        val diffMs  = (instant - now).inWholeMilliseconds
        val isPast  = diffMs < 0
        val abs     = if (diffMs < 0) -diffMs else diffMs

        val totalSeconds  = abs / 1_000
        val totalMinutes  = abs / 60_000
        val totalHours    = abs / 3_600_000
        val totalDays     = abs / 86_400_000
        val approxMonths  = totalDays / 30
        val approxYears   = totalDays / 365

        val (value, localUnit) = when (unit) {
            FromNowUnit.DEFAULT -> autoSelect(
                totalSeconds, totalMinutes, totalHours, totalDays, approxMonths, approxYears
            )
            FromNowUnit.SECOND  -> plural(totalSeconds, FromNowLocalizeUnit.SECOND,  FromNowLocalizeUnit.SECONDS)
            FromNowUnit.MINUTES -> plural(totalMinutes, FromNowLocalizeUnit.MINUTE,  FromNowLocalizeUnit.MINUTES)
            FromNowUnit.HOUR    -> plural(totalHours,   FromNowLocalizeUnit.HOUR,    FromNowLocalizeUnit.HOURS)
            FromNowUnit.DAY     -> plural(totalDays,    FromNowLocalizeUnit.DAY,     FromNowLocalizeUnit.DAYS)
            FromNowUnit.MONTH   -> plural(approxMonths, FromNowLocalizeUnit.MONTH,   FromNowLocalizeUnit.MONTHS)
            FromNowUnit.YEAR    -> plural(approxYears,  FromNowLocalizeUnit.YEAR,    FromNowLocalizeUnit.YEARS)
        }
        return DateCedInterval(value, localUnit, isPast)
    }

    // ---- Private helpers ----

    private fun autoSelect(
        s: Long, m: Long, h: Long, d: Long, mo: Long, y: Long,
    ): Pair<Long, FromNowLocalizeUnit> = when {
        s  < 45  -> plural(s,  FromNowLocalizeUnit.SECOND, FromNowLocalizeUnit.SECONDS)
        s  < 90  -> Pair(1L, FromNowLocalizeUnit.MINUTE)
        m  < 45  -> plural(m,  FromNowLocalizeUnit.MINUTE, FromNowLocalizeUnit.MINUTES)
        m  < 90  -> Pair(1L, FromNowLocalizeUnit.HOUR)
        h  < 22  -> plural(h,  FromNowLocalizeUnit.HOUR,   FromNowLocalizeUnit.HOURS)
        h  < 36  -> Pair(1L, FromNowLocalizeUnit.DAY)
        d  < 26  -> plural(d,  FromNowLocalizeUnit.DAY,    FromNowLocalizeUnit.DAYS)
        d  < 46  -> Pair(1L, FromNowLocalizeUnit.MONTH)
        mo < 11  -> plural(mo, FromNowLocalizeUnit.MONTH,  FromNowLocalizeUnit.MONTHS)
        mo < 18  -> Pair(1L, FromNowLocalizeUnit.YEAR)
        else     -> plural(y,  FromNowLocalizeUnit.YEAR,   FromNowLocalizeUnit.YEARS)
    }

    private fun plural(
        value: Long,
        singular: FromNowLocalizeUnit,
        plural: FromNowLocalizeUnit,
    ): Pair<Long, FromNowLocalizeUnit> = Pair(value, if (value == 1L) singular else plural)
}
