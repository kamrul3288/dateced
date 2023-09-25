package com.iamkamrul.dateced

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DateCed private  constructor(){
    private lateinit var zonedDateTime: ZonedDateTime

    private companion object{
        private var instance: DateCed? = null
        operator fun invoke() = synchronized(this){
            if (instance == null){
                instance = DateCed()
            }
            instance!!
        }
    }

    object Factory{
        fun now(): DateCed {
            val instance = invoke()
            instance. zonedDateTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
            return instance
        }
        fun<T> parse(
            dateTime:T,
            pattern:String? = null,
            zoneId:DateCedTimeZone = DateCedTimeZone.LOCAL
        ): DateCed {
            val instance = invoke()
            instance.zonedDateTime = dateTime.zonedDateTime(pattern = pattern,zoneId = zoneId)
            return instance
        }
    }


    //----------getter---------------------
    fun millisecond() = Getter.millisecond(zonedDateTime)
    fun second() = Getter.second(zonedDateTime)
    fun minute() = Getter.minute(zonedDateTime)
    fun hour() = Getter.hour(zonedDateTime)
    fun year() = Getter.year(zonedDateTime)

    fun dayOfWeek(): DayOfWeek = Getter.dayOfWeek(zonedDateTime)
    fun dayOfMonth() = Getter.dayOfMonth(zonedDateTime)
    fun dayOfYear() = Getter.dayOfYear(zonedDateTime)
    fun dateTime() = zonedDateTime
    fun toMillisecond() = Getter.toMillisecond(zonedDateTime)

    //---------------- Manipulator------------------------
    fun plus(seconds: Long = 0, minutes: Long = 0, hours: Long = 0, days: Long = 0, weeks: Long = 0, years: Long = 0): DateCed {
        zonedDateTime = Manipulator.plus(
            zonedDateTime = zonedDateTime,
            seconds = seconds,
            minutes = minutes,
            hours = hours,
            days = days,
            weeks = weeks,
            years = years
        )
        return this
    }

    fun minus(seconds: Long, minutes: Long, hours: Long, days: Long, weeks: Long, years: Long): DateCed {
        zonedDateTime = Manipulator.minus(
            zonedDateTime = zonedDateTime,
            seconds = seconds,
            minutes = minutes,
            hours = hours,
            days = days,
            weeks = weeks,
            years = years
        )
        return this
    }

    fun toLocal(): DateCed {
        zonedDateTime = Manipulator.toLocal(zonedDateTime = zonedDateTime)
        return this
    }

    fun toUTC(): DateCed {
        zonedDateTime = Manipulator.toUTC(zonedDateTime = zonedDateTime)
        return this
    }

    fun toGMT(): DateCed {
        zonedDateTime = Manipulator.toGMT(zonedDateTime = zonedDateTime)
        return this
    }

    fun<T> fromNow(
        dateTime: T,
        dateCedTimeZone: DateCedTimeZone = DateCedTimeZone.LOCAL,
        pattern: String? = null,
        fromNowUnit: FromNowUnit
    ):Pair<Long,FromNowLocalizeUnit>{
        val dateTimeZone = dateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        return Manipulator.fromNow(dateTimeZone, fromNowUnit)
    }

    fun<T> timeDifference(
        dateTime: T,
        dateCedTimeZone: DateCedTimeZone = DateCedTimeZone.LOCAL,
        pattern: String? = null,
        unit: TimeDifferenceUnit
    ):Long{
        val dateTimeZone = dateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        return Manipulator.calculateTimeDifference(zonedDateTime, dateTimeZone, unit)
    }


    //-------------- Query--------------------------
    fun<T> isBefore(
        dateTime:T,
        dateCedTimeZone: DateCedTimeZone = DateCedTimeZone.LOCAL,
        pattern: String? = null,
    ):Boolean {
        val dateTimeZone = dateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        return Query.isBefore(
            firstDateTime = zonedDateTime,
            secondDateTime = dateTimeZone,
        )
    }

    fun<T> isEqualOrBefore(
        dateTime:T,
        dateCedTimeZone: DateCedTimeZone = DateCedTimeZone.LOCAL,
        pattern: String? = null,
    ):Boolean{
        val dateTimeZone = dateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        return Query.isEqualOrBefore(
            firstDateTime = zonedDateTime,
            secondDateTime = dateTimeZone,
        )
    }

    fun<T> isAfter(
        dateTime:T,
        dateCedTimeZone: DateCedTimeZone = DateCedTimeZone.LOCAL,
        pattern: String? = null,
    ):Boolean {
        val dateTimeZone = dateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        return Query.isAfter(
            firstDateTime = zonedDateTime,
            secondDateTime = dateTimeZone,
        )
    }

    fun<T> isEqualOrAfter(
        dateTime:T,
        dateCedTimeZone: DateCedTimeZone = DateCedTimeZone.LOCAL,
        pattern: String? = null,
    ):Boolean{
        val dateTimeZone = dateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        return Query.isEqualOrAfter(
            firstDateTime = zonedDateTime,
            secondDateTime = dateTimeZone,
        )
    }

    fun<T> isEqual(
        dateTime:T,
        dateCedTimeZone: DateCedTimeZone = DateCedTimeZone.LOCAL,
        pattern: String? = null,
    ):Boolean{
        val dateTimeZone = dateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        return Query.isEqualOrAfter(
            firstDateTime = zonedDateTime,
            secondDateTime = dateTimeZone,
        )
    }

    fun<S,T> isBetween(
        secondDateTime:S,
        thirdDateTime:T,
        dateCedTimeZone: DateCedTimeZone = DateCedTimeZone.LOCAL,
        pattern: String? = null,
    ):Boolean{
        val secondDateTimeZone = secondDateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        val thirdDateTimeZone = thirdDateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        return Query.isBetween(
            firstDateTime = zonedDateTime,
            secondDateTime = secondDateTimeZone,
            thisDateTime = thirdDateTimeZone
        )
    }


    fun<S,T> isEqualOrBetween(
        secondDateTime:S,
        thirdDateTime:T,
        dateCedTimeZone: DateCedTimeZone = DateCedTimeZone.LOCAL,
        pattern: String? = null,
    ):Boolean{
        val secondDateTimeZone = secondDateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        val thirdDateTimeZone = thirdDateTime.zonedDateTime(pattern = pattern, zoneId = dateCedTimeZone)
        return Query.isEqualOrBetween(
            firstDateTime = zonedDateTime,
            secondDateTime = secondDateTimeZone,
            thisDateTime = thirdDateTimeZone
        )
    }

    fun isTodayBetweenDaysOfWeek(days:List<DayOfWeek>) = Query.isTodayBetweenDaysOfWeek(days)

    fun isisLeapYear(year:Int) = Query.isisLeapYear(year)

    //--------------format date------------------
    fun format(pattern:String,zoneId: DateCedTimeZone = DateCedTimeZone.LOCAL):String = zonedDateTime.format(pattern = pattern,zoneId = zoneId)

    val day get() = format("EEEE")
    val d get() = format("dd")
    val y get() = format("yyyy")
    val m get() = format("MMM")

    val dMy get() = format("dd MMM yyyy")
    val dM get() = format("dd MMM")
    val dMyHms get() = format("dd MMM yyyy hh:mm:ss")
    val dMyHmsA get() = format("dd MMM yyyy hh:mm:ss a")
    val dMyHmA get() = format("dd MMM yyyy hh:mm a")

    val dMyHms24 get() = format("dd MMM yyyy HH:mm:ss")
    val dMyHm24 get() = format("dd MMM yyyy HH:mm")


    val hM get() = format("hh:mm")
    val hMs get() = format("hh:mm:ss")
    val hMa get() = format("hh:mm a")
    val hMsA get() = format("hh:mm:ss a")

    val hMs24 get() = format("HH:mm:ss")
    val hM24 get() = format("HH:mm")
    val h24 get() = format("HH")


    val sqlYMd get() = format("yyyy-MM-dd")
    val sqlYMdHm get() = format("yyyy-MM-dd hh:mm")
    val sqlYMdHms get() = format("yyyy-MM-dd hh:mm:ss")

    val sqlYMd24Hm get() = format("yyyy-MM-dd HH:mm")
    val sqlYMd24Hms get() = format("yyyy-MM-dd HH:mm:ss")

    val hmADmY get() = format("hh:mm a dd MMM yyyy")
    val hmsADmY get() = format("hh:mm:ss a dd MMM yyyy")
    val hms24DmY get() = format("HH:mm:ss dd MMM yyyy")
    val hm24DmY get() = format("HH:mm dd MMM yyyy")
}
