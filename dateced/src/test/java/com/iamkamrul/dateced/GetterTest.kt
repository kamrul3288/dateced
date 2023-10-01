package com.iamkamrul.dateced

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class GetterTest {
    private lateinit var zonedDateTime: ZonedDateTime
    private val getter = Getter
    @Before
    fun setup(){
        zonedDateTime = LocalDateTime.of(2023,1,1,1,1,1).atZone(ZoneId.systemDefault())
    }

    @Test
    fun millisecond() {
        val result = getter.millisecond(zonedDateTime)
        Assert.assertEquals(result,1000)
    }

    @Test
    fun second() {
        val result = getter.second(zonedDateTime)
        Assert.assertEquals(result,1)
    }

    @Test
    fun minute() {
        val result = getter.minute(zonedDateTime)
        Assert.assertEquals(result,1)
    }

    @Test
    fun hour() {
        val result = getter.hour(zonedDateTime)
        Assert.assertEquals(result,1)
    }

    @Test
    fun year() {
        val result = getter.year(zonedDateTime)
        Assert.assertEquals(result,2023)
    }

    @Test
    fun toMillisecond() {
        val result = getter.toMillisecond(zonedDateTime)
        Assert.assertEquals(result,zonedDateTime.toInstant().toEpochMilli())
    }

    @Test
    fun dayOfWeek() {
        val result = getter.dayOfWeek(zonedDateTime)
        Assert.assertEquals(result,DayOfWeek.SUNDAY)
    }

    @Test
    fun dayOfMonth() {
        val result = getter.dayOfMonth(zonedDateTime)
        Assert.assertEquals(result,1)
    }

    @Test
    fun dayOfYear() {
        val result = getter.second(zonedDateTime)
        Assert.assertEquals(result,1)
    }
}