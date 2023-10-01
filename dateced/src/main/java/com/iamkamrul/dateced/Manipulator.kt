package com.iamkamrul.dateced

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime

internal object Manipulator {

    fun plus(
        zonedDateTime: ZonedDateTime,
        seconds: Long,
        minutes: Long,
        hours: Long,
        days: Long,
        weeks: Long,
        months:Long,
        years: Long,

        ): ZonedDateTime {
        return zonedDateTime.plusSeconds(seconds)
            .plusMinutes(minutes)
            .plusHours(hours)
            .plusDays(days)
            .plusWeeks(weeks)
            .plusMonths(months)
            .plusYears(years)
    }

    fun minus(
        zonedDateTime: ZonedDateTime,
        seconds: Long,
        minutes: Long,
        hours: Long,
        days: Long,
        weeks: Long,
        months:Long,
        years: Long,

        ):ZonedDateTime{
        return zonedDateTime.minusSeconds(seconds)
            .minusMinutes(minutes)
            .minusHours(hours)
            .minusDays(days)
            .minusWeeks(weeks)
            .minusMonths(months)
            .minusYears(years)
    }

    fun toLocal(zonedDateTime: ZonedDateTime):ZonedDateTime{
        val instant = Instant.ofEpochMilli(zonedDateTime.toEpochSecond() * 1000)
        return instant.atZone(ZoneId.systemDefault())
    }

    fun toUTC(zonedDateTime: ZonedDateTime):ZonedDateTime{
        val instant = Instant.ofEpochMilli(zonedDateTime.toEpochSecond() * 1000)
        return instant.atZone(ZoneId.of("UTC"))
    }

    fun toGMT(zonedDateTime: ZonedDateTime):ZonedDateTime{
        val instant = Instant.ofEpochMilli(zonedDateTime.toEpochSecond() * 1000)
        return instant.atZone(ZoneId.of("GMT"))
    }

    fun<T:Any> fromNow(
        dateTime: T,
        pattern: String? = null,
        timeZoneId: TimeZoneId,
        fromNowUnit: FromNowUnit
    ):Pair<Long, FromNowLocalizeUnit>{

        val now  = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val previous = dateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        val duration = Duration.between(now, previous).abs()
        val period = Period.between(now.toLocalDate(), previous.toLocalDate()).normalized()

        return when(fromNowUnit){
            FromNowUnit.DEFAULT -> when{
                duration.seconds < 60 ->  getSecond(duration)
                duration.toMinutes() < 60 ->  getMinutes(duration)
                duration.toHours() < 24 ->  getHours(duration)
                duration.toDays() < 30 ->  getDays(duration)
                period.toTotalMonths() < 12 ->  getMonths(period)
                else -> getYears(period)
            }
            FromNowUnit.SECOND -> getSecond(duration)
            FromNowUnit.DAY -> getDays(duration)
            FromNowUnit.HOUR -> getHours(duration)
            FromNowUnit.MINUTES -> getMinutes(duration)
            FromNowUnit.MONTH -> getMonths(period)
            FromNowUnit.YEAR -> getYears(period)
        }
    }

    
    fun<T:Any> calculateTimeDifference(
        firstZonedDateTime: ZonedDateTime,
        secondDateTime: T,
        timeZoneId: TimeZoneId,
        pattern: String? = null,
        units: TimeDifferenceUnit,
    ):Long{
        val secondDateTimeZone = secondDateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        val duration = Duration.between(firstZonedDateTime, secondDateTimeZone).abs()
        return when(units){
            TimeDifferenceUnit.MILLISECOND -> duration.seconds * 1000
            TimeDifferenceUnit.SECOND -> duration.seconds
            TimeDifferenceUnit.HOUR -> duration.toHours()
            TimeDifferenceUnit.MINUTES -> duration.toMinutes()
            TimeDifferenceUnit.DAY -> duration.toDays()
        }
    }

    private fun getSecond(duration: Duration) = Pair(first = duration.seconds, second = FromNowLocalizeUnit.SECONDS)

    private fun getMinutes(duration: Duration) = Pair(first = duration.toMinutes(), second = if (duration.toMinutes()>0) FromNowLocalizeUnit.MINUTE else FromNowLocalizeUnit.MINUTES)

    private fun getHours(duration: Duration) = Pair(first = duration.toHours(), second = if (duration.toHours()>0) FromNowLocalizeUnit.HOUR else FromNowLocalizeUnit.HOURS)

    private fun getDays(duration: Duration) = Pair(first = duration.toDays(), second = if (duration.toDays()>0) FromNowLocalizeUnit.DAY else FromNowLocalizeUnit.DAYS)

    private fun getMonths(period: Period) = Pair(first = period.toTotalMonths(), second = if (period.toTotalMonths()>0) FromNowLocalizeUnit.MONTH else FromNowLocalizeUnit.MONTHS)

    private fun getYears(period: Period) = Pair(first = period.toTotalMonths()/12, second = if (period.toTotalMonths()/12>0)FromNowLocalizeUnit.YEAR else FromNowLocalizeUnit.YEARS,)

}
