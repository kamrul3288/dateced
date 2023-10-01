package com.iamkamrul.dateced

import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ManipulatorTest {
    private lateinit var firstZonedDateTime: ZonedDateTime
    private lateinit var secondZonedDateTime: ZonedDateTime
    private val manipulator = Manipulator
    @Before
    fun setup(){
        firstZonedDateTime = LocalDateTime.of(2023,1,1,1,1,1).atZone(ZoneId.systemDefault())
        secondZonedDateTime = LocalDateTime.of(2023,2,1,1,1,1).atZone(ZoneId.systemDefault())
    }

    @Test
    fun plus() {
        val result = manipulator.plus(
            zonedDateTime= firstZonedDateTime,
            seconds = 1L,
            minutes = 1L,
            hours = 1L,
            days = 1L,
            weeks = 1L,
            months = 1L,
            years = 1L
        )
        val expectedResult = LocalDateTime.of(2024,2,9,2,2,2).atZone(ZoneId.systemDefault())
        assertEquals(result,expectedResult)
    }

    @Test
    fun minus() {
        val result = manipulator.minus(
            zonedDateTime= firstZonedDateTime,
            seconds = 1L,
            minutes = 2L,
            hours = 2,
            days = 1L,
            weeks = 1L,
            months = 1L,
            years = 1L
        )
        val expectedResult = LocalDateTime.of(2021,11,23,22,59,0).atZone(ZoneId.systemDefault())
        assertEquals(result,expectedResult)
    }

    @Test
    fun toLocal() {
        val result = manipulator.toLocal(LocalDateTime.of(2023,1,1,1,1,1).atZone(ZoneId.of("UTC")))
        val expectedResult = LocalDateTime.of(2023,1,1,7,1,1).atZone(ZoneId.systemDefault())
        assertEquals(result,expectedResult)
    }

    @Test
    fun toUTC() {
        val result = manipulator.toUTC(LocalDateTime.of(2023,1,1,7,1,1).atZone(ZoneId.systemDefault()))
        val expectedResult = LocalDateTime.of(2023,1,1,1,1,1).atZone(ZoneId.of("UTC"))
        assertEquals(result,expectedResult)
    }

    @Test
    fun toGMT() {
        val result = manipulator.toGMT(LocalDateTime.of(2023,1,1,7,1,1).atZone(ZoneId.systemDefault()))
        val expectedResult = LocalDateTime.of(2023,1,1,1,1,1).atZone(ZoneId.of("GMT"))
        assertEquals(result,expectedResult)
    }

    @Test
    fun fromNow() {
        val result = manipulator.fromNow("2024-10-02", timeZoneId = TimeZoneId.LOCAL, fromNowUnit = FromNowUnit.YEAR)
        assertEquals(result.first,1)
    }

    @Test
    fun calculateTimeDifference() {
        val result = manipulator.calculateTimeDifference(
            firstZonedDateTime,"2023-02-02 01:01:00", timeZoneId = TimeZoneId.LOCAL, units = TimeDifferenceUnit.DAY)
        assertEquals(result,31)
    }
}