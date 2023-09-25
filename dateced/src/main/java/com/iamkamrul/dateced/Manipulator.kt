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
        years: Long,

    ):ZonedDateTime{
        zonedDateTime.plusSeconds(seconds)
        zonedDateTime.plusMinutes(minutes)
        zonedDateTime.plusHours(hours)
        zonedDateTime.plusDays(days)
        zonedDateTime.plusWeeks(weeks)
        zonedDateTime.plusYears(years)
        return zonedDateTime
    }

    fun minus(
        zonedDateTime: ZonedDateTime,
        seconds: Long,
        minutes: Long,
        hours: Long,
        days: Long,
        weeks: Long,
        years: Long,

        ):ZonedDateTime{
        zonedDateTime.minusSeconds(seconds)
        zonedDateTime.minusMinutes(minutes)
        zonedDateTime.minusHours(hours)
        zonedDateTime.minusDays(days)
        zonedDateTime.minusWeeks(weeks)
        zonedDateTime.minusYears(years)
        return zonedDateTime
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

    fun fromNow(zonedDateTime: ZonedDateTime, fromNowUnit: FromNowUnit):Pair<Long, FromNowLocalizeUnit>{
        val now  = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val duration = Duration.between(now, zonedDateTime).abs()
        val period = Period.between(now.toLocalDate(), zonedDateTime.toLocalDate()).normalized()

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

    fun calculateTimeDifference(firstDateTime: ZonedDateTime, secondDateTime: ZonedDateTime, units: TimeDifferenceUnit):Long{
        val duration = Duration.between(firstDateTime, secondDateTime).abs()
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
