package com.iamkamrul.dateced

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal object Query {
    fun<T:Any> isBefore(
        firstZonedDateTime:ZonedDateTime,
        secondDateTime:T,
        timeZoneId: TimeZoneId,
        pattern: String? = null,
    ):Boolean {
        val secondZonedDateTime = secondDateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        return firstZonedDateTime.isBefore(secondZonedDateTime)
    }

    fun<T:Any> isEqualOrBefore(
        firstZonedDateTime:ZonedDateTime,
        secondDateTime:T,
        timeZoneId: TimeZoneId,
        pattern: String? = null,
    ):Boolean{
        val secondZonedDateTime = secondDateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        return firstZonedDateTime.isBefore(secondZonedDateTime) || firstZonedDateTime.isEqual(secondZonedDateTime)
    }

    fun<T:Any> isAfter(
        firstZonedDateTime:ZonedDateTime,
        secondDateTime:T,
        timeZoneId: TimeZoneId,
        pattern: String? = null,
    ):Boolean{
        val secondZonedDateTime = secondDateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        return firstZonedDateTime.isAfter(secondZonedDateTime)
    }

    fun<T:Any> isEqualOrAfter(
        firstZonedDateTime:ZonedDateTime,
        secondDateTime:T,
        timeZoneId: TimeZoneId,
        pattern: String? = null,
    ):Boolean{
        val secondZonedDateTime = secondDateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        return firstZonedDateTime.isAfter(secondZonedDateTime) || firstZonedDateTime.isEqual(secondZonedDateTime)
    }

    fun<T:Any> isEqual(
        firstZonedDateTime:ZonedDateTime,
        secondDateTime:T,
        timeZoneId: TimeZoneId,
        pattern: String? = null,
    ):Boolean{
        val secondZonedDateTime = secondDateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        return firstZonedDateTime.isEqual(secondZonedDateTime)
    }

    fun<S:Any,T:Any> isBetween(
        firstZonedDateTime:ZonedDateTime,
        secondDateTime:S,
        thirdDateTime:T,
        timeZoneId: TimeZoneId,
        pattern: String? = null,
    ):Boolean{
        val secondZonedDateTime = secondDateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        val thirdZonedDateTime = thirdDateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        return firstZonedDateTime.isAfter(secondZonedDateTime) && firstZonedDateTime.isBefore(thirdZonedDateTime)
    }


    fun<S:Any,T:Any> isEqualOrBetween(
        firstZonedDateTime:ZonedDateTime,
        secondDateTime:S,
        thirdDateTime:T,
        timeZoneId: TimeZoneId,
        pattern: String? = null,
    ):Boolean{
        return isEqualOrAfter(
            firstZonedDateTime = firstZonedDateTime,
            secondDateTime = secondDateTime,
            timeZoneId = timeZoneId,
            pattern = pattern
        ) && isEqualOrBefore(
            firstZonedDateTime = firstZonedDateTime,
            secondDateTime = thirdDateTime,
            timeZoneId = timeZoneId,
            pattern = pattern
        )
    }

    fun isTodayBetweenDaysOfWeek(
        days:List<DayOfWeek>
    ):Boolean{
        val today = Getter.dayOfWeek(LocalDateTime.now().atZone(ZoneId.systemDefault()))
        return days.contains(today)
    }

    fun isisLeapYear(year:Int):Boolean{
        return (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0))
    }
}