package com.iamkamrul.dateced

import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class QueryTest {

    private lateinit var firstZonedDateTime: ZonedDateTime
    private lateinit var secondZonedDateTime: ZonedDateTime
    private val query = Query
    @Before
    fun setup(){
        firstZonedDateTime = LocalDateTime.of(2023,1,1,1,1,1).atZone(ZoneId.systemDefault())
        secondZonedDateTime = LocalDateTime.of(2023,2,1,1,1,1).atZone(ZoneId.systemDefault())
    }

    @Test
    fun isBefore() {
        val result = query.isBefore(firstZonedDateTime,secondZonedDateTime, timeZoneId = TimeZoneId.LOCAL)
        assert(result)
    }

    @Test
    fun isEqualOrBefore() {
        val result = query.isEqualOrBefore(firstZonedDateTime,firstZonedDateTime, timeZoneId = TimeZoneId.LOCAL)
        assert(result)
    }

    @Test
    fun isAfter() {
        val result = query.isAfter(secondZonedDateTime,firstZonedDateTime, timeZoneId = TimeZoneId.LOCAL)
        assert(result)
    }

    @Test
    fun isEqualOrAfter() {
        val result = query.isEqualOrAfter(secondZonedDateTime,secondZonedDateTime, timeZoneId = TimeZoneId.LOCAL)
        assert(result)
    }

    @Test
    fun isEqual() {
        val result = query.isEqual(secondZonedDateTime,secondZonedDateTime, timeZoneId = TimeZoneId.LOCAL)
        assert(result)
    }

    @Test
    fun isBetween() {
        val result = query.isBetween(
            firstZonedDateTime = firstZonedDateTime,
            secondDateTime = secondZonedDateTime,
            thirdDateTime = "2023-03-01",
            timeZoneId = TimeZoneId.LOCAL
        )
        assertFalse(result)

        val result2 = query.isBetween(
            firstZonedDateTime = firstZonedDateTime,
            secondDateTime = "2022-12-30",
            thirdDateTime = secondZonedDateTime,
            timeZoneId = TimeZoneId.LOCAL
        )
        assert(result2)
    }

    @Test
    fun isEqualOrBetween() {
        val result = query.isEqualOrBetween(
            firstZonedDateTime = firstZonedDateTime,
            secondDateTime = firstZonedDateTime,
            thirdDateTime = secondZonedDateTime,
            timeZoneId = TimeZoneId.LOCAL
        )
        assert(result)
    }

    @Test
    fun isTodayBetweenDaysOfWeek() {
        val result = query.isTodayBetweenDaysOfWeek(listOf(DayOfWeek.SUNDAY))
        assert(result)
    }

    @Test
    fun isisLeapYear() {
        val result = query.isisLeapYear(2024)
        assert(result)
    }
}