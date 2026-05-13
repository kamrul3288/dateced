package com.iamkamrul.dateced

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Pure comparison and query functions operating on [Instant] values.
 *
 * All functions are stateless — they take explicit parameters and return immutable values.
 * Marked [internal] because callers use the methods on [DateCedReadable] directly.
 */
internal object Query {

    // ---- Ordering -------------------------------------------------------

    fun isBefore(a: Instant, b: Instant): Boolean = a < b
    fun isAfter(a: Instant, b: Instant): Boolean  = a > b
    fun isEqual(a: Instant, b: Instant): Boolean  = a == b

    fun isEqualOrBefore(a: Instant, b: Instant): Boolean = a <= b
    fun isEqualOrAfter(a: Instant, b: Instant): Boolean  = a >= b

    /** Strict: [value] must be strictly after [start] and strictly before [end]. */
    fun isBetween(value: Instant, start: Instant, end: Instant): Boolean =
        value > start && value < end

    /** Inclusive at both bounds. */
    fun isEqualOrBetween(value: Instant, start: Instant, end: Instant): Boolean =
        value in start..end

    // ---- Calendar -------------------------------------------------------

    /** Gregorian leap-year rule. */
    fun isLeapYear(year: Int): Boolean =
        (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0))

    // ---- Relative to today (uses device clock) ---------------------------

    /** True if [instant] falls on today's calendar date in [tz]. */
    fun isToday(instant: Instant, tz: TimeZone): Boolean {
        val today = Clock.System.now().toLocalDateTime(tz).date
        return instant.toLocalDateTime(tz).date == today
    }

    /** True if [instant] falls on yesterday's calendar date in [tz]. */
    fun isYesterday(instant: Instant, tz: TimeZone): Boolean {
        val yesterday = Clock.System.now().toLocalDateTime(tz).date.minus(1, DateTimeUnit.DAY)
        return instant.toLocalDateTime(tz).date == yesterday
    }

    /** True if [instant] falls on tomorrow's calendar date in [tz]. */
    fun isTomorrow(instant: Instant, tz: TimeZone): Boolean {
        val tomorrow = Clock.System.now().toLocalDateTime(tz).date.plus(1, DateTimeUnit.DAY)
        return instant.toLocalDateTime(tz).date == tomorrow
    }

    // ---- Day-of-week ----------------------------------------------------

    /** True if [instant]'s day-of-week is Saturday or Sunday in [tz]. */
    fun isWeekend(instant: Instant, tz: TimeZone): Boolean {
        val dow = instant.toLocalDateTime(tz).dayOfWeek
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY
    }

    /** True if [instant]'s day-of-week is Monday through Friday in [tz]. */
    fun isWeekday(instant: Instant, tz: TimeZone): Boolean = !isWeekend(instant, tz)

    // ---- Past / Future --------------------------------------------------

    /** True if [instant] is strictly before the current moment. */
    fun isPast(instant: Instant): Boolean = instant < Clock.System.now()

    /** True if [instant] is strictly after the current moment. */
    fun isFuture(instant: Instant): Boolean = instant > Clock.System.now()
}
