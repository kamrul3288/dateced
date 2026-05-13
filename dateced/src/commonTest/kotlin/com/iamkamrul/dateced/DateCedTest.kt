package com.iamkamrul.dateced

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Common (multiplatform) test suite for DateCed.
 *
 * Tests use UTC timezone wherever possible to make assertions timezone-independent.
 * Every test is self-contained — no shared state between tests.
 */
class DateCedTest {

    private val utc = TimeZoneId.UTC

    // ======================================================================
    // Parsing
    // ======================================================================

    @Test
    fun `parse ISO 8601 with Z suffix`() {
        val d = DateCed.parse("2025-06-15T10:30:00Z", zoneId = utc)
        assertEquals(2025, d.year)
        assertEquals(6,    d.monthNumber)
        assertEquals(15,   d.dayOfMonth)
        assertEquals(10,   d.hour)
        assertEquals(30,   d.minute)
        assertEquals(0,    d.second)
    }

    @Test
    fun `parse yyyy-MM-dd HH_mm_ss auto-detect`() {
        val d = DateCed.parse("2025-01-20 14:30:45", zoneId = utc)
        assertEquals(2025, d.year)
        assertEquals(1,    d.monthNumber)
        assertEquals(20,   d.dayOfMonth)
        assertEquals(14,   d.hour)
        assertEquals(30,   d.minute)
        assertEquals(45,   d.second)
    }

    @Test
    fun `parse yyyy-MM-dd date only defaults to midnight`() {
        val d = DateCed.parse("2025-03-10", zoneId = utc)
        assertEquals(2025, d.year)
        assertEquals(3,    d.monthNumber)
        assertEquals(10,   d.dayOfMonth)
        assertEquals(0,    d.hour)
        assertEquals(0,    d.minute)
        assertEquals(0,    d.second)
    }

    @Test
    fun `parse dd-MM-yyyy format`() {
        val d = DateCed.parse("25-12-2025", zoneId = utc)
        assertEquals(2025, d.year)
        assertEquals(12,   d.monthNumber)
        assertEquals(25,   d.dayOfMonth)
    }

    @Test
    fun `parse dd MMM yyyy format`() {
        val d = DateCed.parse("15 Jan 2025", zoneId = utc)
        assertEquals(2025, d.year)
        assertEquals(1,    d.monthNumber)
        assertEquals(15,   d.dayOfMonth)
    }

    @Test
    fun `parse with explicit pattern`() {
        val d = DateCed.parse("2025/06/15", "yyyy/MM/dd", zoneId = utc)
        assertEquals(2025, d.year)
        assertEquals(6,    d.monthNumber)
        assertEquals(15,   d.dayOfMonth)
    }

    @Test
    fun `parse epoch milliseconds`() {
        val epochMs = 1_700_000_000_000L
        val d = DateCed.parse(epochMs, utc)
        assertEquals(epochMs, d.epochMilliseconds)
    }

    @Test
    fun `tryParse valid string returns DateCed`() {
        val d = DateCed.tryParse("2025-06-15", zoneId = utc)
        assertNotNull(d)
        assertEquals(2025, d.year)
    }

    @Test
    fun `tryParse invalid string returns null`() {
        val d = DateCed.tryParse("not-a-date")
        assertNull(d)
    }

    // ======================================================================
    // Getters
    // ======================================================================

    @Test
    fun `getters return correct values`() {
        val d = DateCed.parse("2024-02-29 08:05:03", zoneId = utc) // leap day
        assertEquals(2024, d.year)
        assertEquals(2,    d.monthNumber)
        assertEquals(29,   d.dayOfMonth)
        assertEquals(8,    d.hour)
        assertEquals(5,    d.minute)
        assertEquals(3,    d.second)
        assertEquals(60,   d.dayOfYear)
    }

    @Test
    fun `weekOfYear is within 1 to 53`() {
        val d = DateCed.parse("2025-12-31", zoneId = utc)
        val w = d.weekOfYear
        assertTrue(w in 1..53, "Expected weekOfYear in 1..53, got $w")
    }

    @Test
    fun `daysInMonth February non-leap`() {
        val d = DateCed.parse("2025-02-01", zoneId = utc)
        assertEquals(28, d.daysInMonth)
    }

    @Test
    fun `daysInMonth February leap`() {
        val d = DateCed.parse("2024-02-01", zoneId = utc)
        assertEquals(29, d.daysInMonth)
    }

    @Test
    fun `daysInMonth December`() {
        val d = DateCed.parse("2025-12-01", zoneId = utc)
        assertEquals(31, d.daysInMonth)
    }

    // ======================================================================
    // Formatting
    // ======================================================================

    @Test
    fun `sqlYMd formats correctly`() {
        val d = DateCed.parse("2025-06-05 08:30:00", zoneId = utc)
        assertEquals("2025-06-05", d.sqlYMd)
    }

    @Test
    fun `sqlYMdHms formats correctly`() {
        val d = DateCed.parse("2025-06-05 08:05:03", zoneId = utc)
        assertEquals("2025-06-05 08:05:03", d.sqlYMdHms)
    }

    @Test
    fun `hms24 pads correctly`() {
        val d = DateCed.parse("2025-01-01 09:05:03", zoneId = utc)
        assertEquals("09:05:03", d.hms24)
    }

    @Test
    fun `hmA midnight is 12 AM`() {
        val d = DateCed.parse("2025-01-01 00:30:00", zoneId = utc)
        assertEquals("12:30 AM", d.hmA)
    }

    @Test
    fun `hmA noon is 12 PM`() {
        val d = DateCed.parse("2025-01-01 12:00:00", zoneId = utc)
        assertEquals("12:00 PM", d.hmA)
    }

    @Test
    fun `hmA afternoon converts correctly`() {
        val d = DateCed.parse("2025-01-01 15:30:00", zoneId = utc)
        assertEquals("03:30 PM", d.hmA)
    }

    @Test
    fun `custom format via format()`() {
        val d = DateCed.parse("2025-06-15 10:30:00", zoneId = utc)
        assertEquals("2025-06-15", d.format("yyyy-MM-dd", utc))
    }

    // ======================================================================
    // Arithmetic — plus / minus
    // ======================================================================

    @Test
    fun `plus days`() {
        val d = DateCed.parse("2025-01-01", zoneId = utc)
        val result = d.plus(days = 10)
        assertEquals(11, result.dayOfMonth)
        assertEquals(1,  result.monthNumber)
    }

    @Test
    fun `plus months crosses year`() {
        val d = DateCed.parse("2025-11-01", zoneId = utc)
        val result = d.plus(months = 3)
        assertEquals(2026, result.year)
        assertEquals(2,    result.monthNumber)
    }

    @Test
    fun `plus years`() {
        val d = DateCed.parse("2024-02-29", zoneId = utc)
        // Adding 1 year to leap day clamps to Feb 28
        val result = d.plus(years = 1)
        assertEquals(2025, result.year)
        assertEquals(2,    result.monthNumber)
        // Day clamps because 2025-02-29 doesn't exist
        assertTrue(result.dayOfMonth in 28..29)
    }

    @Test
    fun `minus days`() {
        val d = DateCed.parse("2025-01-10", zoneId = utc)
        val result = d.minus(days = 5)
        assertEquals(5,  result.dayOfMonth)
        assertEquals(1,  result.monthNumber)
        assertEquals(2025, result.year)
    }

    @Test
    fun `minus hours`() {
        val d = DateCed.parse("2025-01-01 02:00:00", zoneId = utc)
        val result = d.minus(hours = 3)
        assertEquals(23, result.hour)
        assertEquals(31, result.dayOfMonth) // previous day
        assertEquals(12, result.monthNumber)
        assertEquals(2024, result.year)
    }

    @Test
    fun `plus with all zeros returns same instance`() {
        val d = DateCed.parse("2025-06-15", zoneId = utc)
        assertTrue(d === d.plus()) // identity check — no allocation
    }

    // ======================================================================
    // Boundary navigation
    // ======================================================================

    @Test
    fun `startOfDay resets time to midnight`() {
        val d = DateCed.parse("2025-06-15 14:30:45", zoneId = utc)
        val start = d.startOfDay()
        assertEquals(0, start.hour)
        assertEquals(0, start.minute)
        assertEquals(0, start.second)
        assertEquals(15, start.dayOfMonth)
    }

    @Test
    fun `endOfDay sets time to 23_59_59`() {
        val d = DateCed.parse("2025-06-15 08:00:00", zoneId = utc)
        val end = d.endOfDay()
        assertEquals(23, end.hour)
        assertEquals(59, end.minute)
        assertEquals(59, end.second)
    }

    @Test
    fun `startOfMonth sets day to 1 and time to midnight`() {
        val d = DateCed.parse("2025-06-15 14:30:00", zoneId = utc)
        val start = d.startOfMonth()
        assertEquals(1, start.dayOfMonth)
        assertEquals(0, start.hour)
        assertEquals(6, start.monthNumber)
    }

    @Test
    fun `endOfMonth sets day to last day of month`() {
        val d = DateCed.parse("2025-02-10", zoneId = utc) // Feb 2025, non-leap
        val end = d.endOfMonth()
        assertEquals(28, end.dayOfMonth)
        assertEquals(23, end.hour)
    }

    @Test
    fun `startOfYear is January 1 midnight`() {
        val d = DateCed.parse("2025-07-04", zoneId = utc)
        val start = d.startOfYear()
        assertEquals(1,    start.monthNumber)
        assertEquals(1,    start.dayOfMonth)
        assertEquals(0,    start.hour)
        assertEquals(2025, start.year)
    }

    @Test
    fun `endOfYear is December 31`() {
        val d = DateCed.parse("2025-07-04", zoneId = utc)
        val end = d.endOfYear()
        assertEquals(12,   end.monthNumber)
        assertEquals(31,   end.dayOfMonth)
        assertEquals(23,   end.hour)
        assertEquals(2025, end.year)
    }

    // ======================================================================
    // Field setters
    // ======================================================================

    @Test
    fun `withYear changes only year`() {
        val d = DateCed.parse("2025-06-15 10:30:00", zoneId = utc)
        val result = d.withYear(2030)
        assertEquals(2030, result.year)
        assertEquals(6,    result.monthNumber)
        assertEquals(15,   result.dayOfMonth)
    }

    @Test
    fun `withMonth changes only month`() {
        val d = DateCed.parse("2025-06-15", zoneId = utc)
        val result = d.withMonth(12)
        assertEquals(12,   result.monthNumber)
        assertEquals(2025, result.year)
        assertEquals(15,   result.dayOfMonth)
    }

    @Test
    fun `withDayOfMonth changes only day`() {
        val d = DateCed.parse("2025-06-15", zoneId = utc)
        val result = d.withDayOfMonth(1)
        assertEquals(1, result.dayOfMonth)
        assertEquals(6, result.monthNumber)
    }

    @Test
    fun `withHour changes only hour`() {
        val d = DateCed.parse("2025-06-15 10:30:00", zoneId = utc)
        val result = d.withHour(23)
        assertEquals(23, result.hour)
        assertEquals(30, result.minute)
    }

    // ======================================================================
    // Comparison
    // ======================================================================

    @Test
    fun `isBefore returns true for earlier date`() {
        val earlier = DateCed.parse("2025-01-01", zoneId = utc)
        val later   = DateCed.parse("2025-06-01", zoneId = utc)
        assertTrue(earlier.isBefore(later))
        assertFalse(later.isBefore(earlier))
    }

    @Test
    fun `isAfter returns true for later date`() {
        val earlier = DateCed.parse("2025-01-01", zoneId = utc)
        val later   = DateCed.parse("2025-06-01", zoneId = utc)
        assertTrue(later.isAfter(earlier))
    }

    @Test
    fun `isEqual is true for same instant`() {
        val a = DateCed.parse("2025-06-15 10:00:00", zoneId = utc)
        val b = DateCed.parse("2025-06-15 10:00:00", zoneId = utc)
        assertTrue(a.isEqual(b))
    }

    @Test
    fun `isBetween returns true for value between bounds`() {
        val start  = DateCed.parse("2025-01-01", zoneId = utc)
        val end    = DateCed.parse("2025-12-31", zoneId = utc)
        val middle = DateCed.parse("2025-06-15", zoneId = utc)
        assertTrue(middle.isBetween(start, end))
        assertFalse(start.isBetween(start, end))  // exclusive
    }

    @Test
    fun `isEqualOrBetween is inclusive at boundaries`() {
        val start = DateCed.parse("2025-01-01", zoneId = utc)
        val end   = DateCed.parse("2025-12-31", zoneId = utc)
        assertTrue(start.isEqualOrBetween(start, end))
        assertTrue(end.isEqualOrBetween(start, end))
    }

    @Test
    fun `isLeapYear identifies leap years correctly`() {
        assertTrue(DateCed.parse("2024-01-01", zoneId = utc).isLeapYear())
        assertFalse(DateCed.parse("2025-01-01", zoneId = utc).isLeapYear())
        assertFalse(DateCed.parse("1900-01-01", zoneId = utc).isLeapYear()) // century non-leap
        assertTrue(DateCed.parse("2000-01-01", zoneId = utc).isLeapYear())  // 400-year leap
    }

    // ======================================================================
    // Time difference
    // ======================================================================

    @Test
    fun `timeDifference in days`() {
        val a = DateCed.parse("2025-01-01", zoneId = utc)
        val b = DateCed.parse("2025-01-11", zoneId = utc)
        assertEquals(10L, a.timeDifference(b, TimeDifferenceUnit.DAY))
        assertEquals(10L, b.timeDifference(a, TimeDifferenceUnit.DAY)) // absolute
    }

    @Test
    fun `timeDifference in hours`() {
        val a = DateCed.parse("2025-01-01 00:00:00", zoneId = utc)
        val b = DateCed.parse("2025-01-01 05:00:00", zoneId = utc)
        assertEquals(5L, a.timeDifference(b, TimeDifferenceUnit.HOUR))
    }

    @Test
    fun `timeDifference in milliseconds`() {
        val a = DateCed.parse("2025-01-01 00:00:00", zoneId = utc)
        val b = DateCed.parse("2025-01-01 00:00:01", zoneId = utc)
        assertEquals(1_000L, a.timeDifference(b, TimeDifferenceUnit.MILLISECOND))
    }

    // ======================================================================
    // Boolean state properties
    // ======================================================================

    @Test
    fun `isPast for past date`() {
        val past = DateCed.parse("2000-01-01", zoneId = utc)
        assertTrue(past.isPast)
        assertFalse(past.isFuture)
    }

    @Test
    fun `isFuture for far future date`() {
        val future = DateCed.parse("2099-12-31", zoneId = utc)
        assertTrue(future.isFuture)
        assertFalse(future.isPast)
    }

    @Test
    fun `isWeekend for Saturday`() {
        // 2025-01-04 is a Saturday
        val sat = DateCed.parse("2025-01-04", zoneId = utc)
        assertTrue(sat.isWeekend)
        assertFalse(sat.isWeekday)
    }

    @Test
    fun `isWeekday for Monday`() {
        // 2025-01-06 is a Monday
        val mon = DateCed.parse("2025-01-06", zoneId = utc)
        assertTrue(mon.isWeekday)
        assertFalse(mon.isWeekend)
    }

    // ======================================================================
    // TimeZone
    // ======================================================================

    @Test
    fun `toUTC returns DateCedReadable with UTC zone`() {
        val d = DateCed.now(TimeZoneId.LOCAL)
        val utcView = d.toUTC()
        assertEquals(kotlinx.datetime.TimeZone.UTC, utcView.timeZone)
        // Same underlying instant
        assertEquals(d.epochMilliseconds, utcView.epochMilliseconds)
    }

    @Test
    fun `withTimeZone of custom zone`() {
        val d = DateCed.parse("2025-06-15 00:00:00", zoneId = utc)
        val dhaka = d.withTimeZone(TimeZoneId.of("Asia/Dhaka"))
        // Dhaka is UTC+6, so hour should be 6
        assertEquals(6, dhaka.hour)
    }

    @Test
    fun `toDateCed preserves timezone`() {
        val d      = DateCed.parse("2025-06-15T00:00:00Z", zoneId = utc)
        val utcView = d.toUTC()
        val back   = utcView.toDateCed()
        assertEquals(d.epochMilliseconds, back.epochMilliseconds)
        assertEquals(utcView.timeZone, back.timeZone)
    }

    // ======================================================================
    // From Now
    // ======================================================================

    @Test
    fun `fromNow for far past returns years`() {
        val d = DateCed.parse("2000-01-01", zoneId = utc)
        val (_, unit) = d.fromNow()
        assertEquals(FromNowLocalizeUnit.YEARS, unit)
    }

    @Test
    fun `fromNowInterval isPast true for past date`() {
        val d = DateCed.parse("2000-01-01", zoneId = utc)
        val interval = d.fromNowInterval()
        assertTrue(interval.isPast)
    }

    @Test
    fun `fromNowInterval isPast false for future date`() {
        val d = DateCed.parse("2099-01-01", zoneId = utc)
        val interval = d.fromNowInterval()
        assertFalse(interval.isPast)
    }

    // ======================================================================
    // Immutability
    // ======================================================================

    @Test
    fun `plus does not mutate the original`() {
        val d      = DateCed.parse("2025-01-01", zoneId = utc)
        val result = d.plus(days = 10)
        assertEquals(1,  d.dayOfMonth)       // original unchanged
        assertEquals(11, result.dayOfMonth)  // new instance updated
    }

    @Test
    fun `equals and hashCode are consistent`() {
        val a = DateCed.parse("2025-06-15 10:00:00", zoneId = utc)
        val b = DateCed.parse("2025-06-15 10:00:00", zoneId = utc)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    // ======================================================================
    // Edge cases
    // ======================================================================

    @Test
    fun `parse dd_MM_yyyy with time`() {
        val d = DateCed.parse("31-12-2025 23:59:59", zoneId = utc)
        assertEquals(31,   d.dayOfMonth)
        assertEquals(12,   d.monthNumber)
        assertEquals(2025, d.year)
        assertEquals(23,   d.hour)
    }

    @Test
    fun `daysInMonth for all months 2025`() {
        val expected = listOf(31,28,31,30,31,30,31,31,30,31,30,31)
        expected.forEachIndexed { idx, days ->
            val d = DateCed.parse("2025-${(idx+1).toString().padStart(2,'0')}-01", zoneId = utc)
            assertEquals(days, d.daysInMonth, "Failed for month ${idx+1}")
        }
    }
}
