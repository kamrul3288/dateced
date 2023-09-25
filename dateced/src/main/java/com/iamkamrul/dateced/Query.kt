package com.iamkamrul.dateced

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal object Query {
    fun isBefore(
        firstDateTime:ZonedDateTime,
        secondDateTime:ZonedDateTime
    ):Boolean {
        return firstDateTime.isBefore(secondDateTime)
    }

    fun isEqualOrBefore(
        firstDateTime:ZonedDateTime,
        secondDateTime:ZonedDateTime
    ):Boolean{
        return firstDateTime.isBefore(secondDateTime) || firstDateTime.isEqual(secondDateTime)
    }

    fun isAfter(
        firstDateTime:ZonedDateTime,
        secondDateTime:ZonedDateTime
    ):Boolean{
        return firstDateTime.isAfter(secondDateTime)
    }

    fun isEqualOrAfter(
        firstDateTime:ZonedDateTime,
        secondDateTime:ZonedDateTime
    ):Boolean{
        return firstDateTime.isAfter(secondDateTime) || firstDateTime.isEqual(secondDateTime)
    }

    fun isEqual(
        firstDateTime:ZonedDateTime,
        secondDateTime:ZonedDateTime
    ):Boolean{
        return firstDateTime.isEqual(secondDateTime)
    }

    fun isBetween(
        firstDateTime:ZonedDateTime,
        secondDateTime:ZonedDateTime,
        thisDateTime:ZonedDateTime,
    ):Boolean{
        return firstDateTime.isAfter(secondDateTime) && firstDateTime.isBefore(thisDateTime)
    }


    fun isEqualOrBetween(
        firstDateTime:ZonedDateTime,
        secondDateTime:ZonedDateTime,
        thisDateTime:ZonedDateTime,
    ):Boolean{
        return isEqualOrAfter(firstDateTime,secondDateTime) && isEqualOrBefore(firstDateTime,thisDateTime)
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