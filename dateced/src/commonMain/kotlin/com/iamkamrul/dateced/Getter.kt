package com.iamkamrul.dateced

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlin.time.Instant

/**
 * Pure functions for extracting individual date/time components.
 *
 * All functions are stateless and thread-safe — they take explicit parameters
 * and never touch shared mutable state.
 *
 * Marked [internal] because it is an implementation detail; callers use
 * the fluent properties on [DateCed] / [DateCedReadable] instead.
 */
internal object Getter {

    // ---- Time components ----

    /** Milliseconds within the current second (0–999). */
    fun millisecond(instant: Instant): Int = instant.nanosecondsOfSecond / 1_000_000

    fun second(ldt: LocalDateTime): Int = ldt.second
    fun minute(ldt: LocalDateTime): Int = ldt.minute
    fun hour(ldt: LocalDateTime): Int = ldt.hour

    // ---- Date components ----

    fun year(ldt: LocalDateTime): Int = ldt.year
    fun month(ldt: LocalDateTime): Month = ldt.month
    fun monthNumber(ldt: LocalDateTime): Int = ldt.month.number
    fun dayOfWeek(ldt: LocalDateTime): DayOfWeek = ldt.dayOfWeek
    fun dayOfMonth(ldt: LocalDateTime): Int = ldt.day
    fun dayOfYear(ldt: LocalDateTime): Int = ldt.dayOfYear

    // ---- Epoch ----

    fun epochMilliseconds(instant: Instant): Long = instant.toEpochMilliseconds()

    // ---- Derived ----

    /**
     * Simple week-of-year (1–53).
     * Uses a day-of-year / 7 calculation: week 1 = days 1-7, etc.
     * Not ISO 8601 strict — use for approximate display only.
     */
    fun weekOfYear(ldt: LocalDateTime): Int = (ldt.dayOfYear + 6) / 7

    /**
     * Number of days in the month, correctly handling leap years for February.
     *
     * @param year  4-digit year
     * @param month 1-based month number (1 = January)
     */
    fun daysInMonth(year: Int, month: Int): Int = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11            -> 30
        2                      -> if (isLeapYear(year)) 29 else 28
        else                   -> throw IllegalArgumentException("Invalid month: $month — must be 1..12")
    }

    fun daysInMonth(ldt: LocalDateTime): Int = daysInMonth(ldt.year, ldt.month.number)

    /** Gregorian leap-year test. */
    private fun isLeapYear(year: Int): Boolean =
        (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0))
}
