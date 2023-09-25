package com.iamkamrul.dateced

import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.util.Date

internal object Getter {
    fun millisecond(zonedDateTime:ZonedDateTime) = zonedDateTime.second * 1000
    fun second(zonedDateTime:ZonedDateTime) = zonedDateTime.second
    fun minute(zonedDateTime:ZonedDateTime) = zonedDateTime.minute
    fun hour(zonedDateTime:ZonedDateTime) = zonedDateTime.hour
    fun year(zonedDateTime:ZonedDateTime) = zonedDateTime.year

    fun toMillisecond(zonedDateTime:ZonedDateTime) = zonedDateTime.toInstant().toEpochMilli()

    fun dayOfWeek(zonedDateTime: ZonedDateTime): DayOfWeek = zonedDateTime.dayOfWeek
    fun dayOfMonth(zonedDateTime: ZonedDateTime) = zonedDateTime.dayOfMonth
    fun dayOfYear(zonedDateTime: ZonedDateTime) = zonedDateTime.dayOfYear

}
