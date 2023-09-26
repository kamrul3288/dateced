package com.iamkamrul.dateced

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DateCedTest {
    private lateinit var dateCed:DateCed
    private val getter = mock<Getter>()
    private val manipulator = mock<Manipulator>()
    private lateinit var zonedDateTime:ZonedDateTime

    @Before
    fun setup(){
        dateCed = DateCed.Factory.now()
        zonedDateTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        dateCed.setTestGetter(getter)
        dateCed.setTestManipulator(manipulator)
    }

    @Test
    fun `millisecond() should invoke Getter_millisecond()`(){
        dateCed.millisecond()
        verify(getter).millisecond(any())
    }

    @Test
    fun `millisecond() should pass current ZonedDateTime to Getter_millisecond()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.millisecond()
        verify(getter).millisecond(eq(zonedDateTime))
    }


    @Test
    fun `second() should invoke Getter_second()`(){
        dateCed.second()
        verify(getter).second(any())
    }

    @Test
    fun `second() should pass current ZonedDateTime to Getter_second()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.second()
        verify(getter).second(eq(zonedDateTime))
    }

    @Test
    fun `minute() should invoke Getter_minute()`(){
        dateCed.minute()
        verify(getter).minute(any())
    }

    @Test
    fun `minute() should pass current ZonedDateTime to Getter_minute()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.minute()
        verify(getter).minute(eq(zonedDateTime))
    }

    @Test
    fun `hour() should invoke Getter_hour()`(){
        dateCed.hour()
        verify(getter).hour(any())
    }

    @Test
    fun `hour() should pass current ZonedDateTime to Getter_hour()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.hour()
        verify(getter).hour(eq(zonedDateTime))
    }

    @Test
    fun `year() should invoke Getter_year()`(){
        dateCed.year()
        verify(getter).year(any())
    }

    @Test
    fun `year() should pass current ZonedDateTime to Getter_year()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.year()
        verify(getter).year(eq(zonedDateTime))
    }

    @Test
    fun `dayOfWeek() should invoke Getter_dayOfWeek()`(){
        dateCed.dayOfWeek()
        verify(getter).dayOfWeek(any())
    }

    @Test
    fun `dayOfWeek() should pass current ZonedDateTime to Getter_dayOfWeek()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.dayOfWeek()
        verify(getter).dayOfWeek(eq(zonedDateTime))
    }

    @Test
    fun `dayOfMonth() should invoke Getter_dayOfMonth()`(){
        dateCed.dayOfMonth()
        verify(getter).dayOfMonth(any())
    }

    @Test
    fun `dayOfMonth() should pass current ZonedDateTime to Getter_dayOfMonth()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.dayOfMonth()
        verify(getter).dayOfMonth(eq(zonedDateTime))
    }

    @Test
    fun `dayOfYear() should invoke Getter_dayOfYear()`(){
        dateCed.dayOfYear()
        verify(getter).dayOfYear(any())
    }

    @Test
    fun `dayOfYear() should pass current ZonedDateTime to Getter_dayOfYear()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.dayOfYear()
        verify(getter).dayOfYear(eq(zonedDateTime))
    }

    @Test
    fun  `dateTime() should return ZonedDateTime`(){
        val dateCedMock = mock<DateCed>()
        whenever(dateCedMock.dateTime()).thenReturn(zonedDateTime)
        Assert.assertEquals(dateCedMock.dateTime(),zonedDateTime)
    }

    @Test
    fun `toMillisecond() should invoke Getter_toMillisecond()`(){
        dateCed.toMillisecond()
        verify(getter).toMillisecond(any())
    }

    @Test
    fun `toMillisecond() should pass current ZonedDateTime to Getter_toMillisecond()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.toMillisecond()
        verify(getter).toMillisecond(eq(zonedDateTime))
    }


    @Test
    fun `plus() should invoke Manipulator_plus()`(){
        dateCed.plus()
        verify(manipulator).plus(any(), any(),any(),any(), any(), any(), any())
    }

    @Test
    fun `plus() should Delegate To Manipulator_plus()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.plus(seconds = 0, minutes = 0, hours = 0, days = 0, weeks = 0, years = 1)
        verify(manipulator).plus(
            zonedDateTime = eq(zonedDateTime),
            seconds = eq(0),
            minutes = eq(0),
            hours = eq(0),
            days = eq(0),
            weeks = eq(0),
            years = eq(1),
        )
    }


    @Test
    fun `minus() should invoke Manipulator_minus()`(){
        dateCed.minus()
        verify(manipulator).minus(any(), any(),any(),any(), any(), any(), any())
    }

    @Test
    fun `minus() should Delegate To Manipulator_minus()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        dateCed.minus(seconds = 0, minutes = 10, hours = 0, days = 0, weeks = 0, years = 0)
        verify(manipulator).minus(
            zonedDateTime = eq(zonedDateTime),
            seconds = eq(0),
            minutes = eq(10),
            hours = eq(0),
            days = eq(0),
            weeks = eq(0),
            years = eq(0),
        )
    }

    @Test
    fun `toLocal() should invoke Manipulator toLocal()`(){
        dateCed.toLocal()
        verify(manipulator).toLocal(any())
    }

    @Test
    fun `toLocal() should Delegate to Manipulator toLocal()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        whenever(manipulator.toLocal(any())).thenReturn(zonedDateTime)
        Assert.assertEquals(dateCed.toLocal().dateTime(),zonedDateTime)
        verify(manipulator).toLocal(eq(zonedDateTime))
    }

    @Test
    fun `toUTC() should invoke Manipulator toUTC()`(){
        dateCed.toUTC()
        verify(manipulator).toUTC(any())
    }

    @Test
    fun `toUTC() should Delegate to Manipulator toUTC()`(){
        dateCed.setTestZonedDateTime(zonedDateTime)
        whenever(manipulator.toUTC(any())).thenReturn(zonedDateTime)
        Assert.assertEquals(dateCed.toUTC().dateTime(),zonedDateTime)
        verify(manipulator).toUTC(eq(zonedDateTime))
    }

    @Test
    fun `fromNow() should invoke Manipulator fromNow()`(){
        dateCed.fromNow(
            dateTime = "2023-01-01",
            fromNowUnit = FromNowUnit.DAY,
            timeZoneId = TimeZoneId.LOCAL
        )
        verify(manipulator).fromNow(
            dateTime = "2023-01-01",
            fromNowUnit =FromNowUnit.DAY,
            timeZoneId = TimeZoneId.LOCAL
        )
    }

}
