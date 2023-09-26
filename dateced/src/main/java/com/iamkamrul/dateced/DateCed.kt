package com.iamkamrul.dateced

import org.jetbrains.annotations.TestOnly
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DateCed private  constructor(){
    private lateinit var zonedDateTime: ZonedDateTime
    private var getter = Getter
    private var manipulator = Manipulator
    private var query = Query

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
            zoneId:TimeZoneId = TimeZoneId.LOCAL
        ): DateCed {
            val instance = invoke()
            instance.zonedDateTime = dateTime.zonedDateTime(pattern = pattern,zoneId = zoneId)
            return instance
        }
    }


    //----------getter---------------------
    fun millisecond() = getter.millisecond(zonedDateTime)
    fun second() = getter.second(zonedDateTime)
    fun minute() = getter.minute(zonedDateTime)
    fun hour() = getter.hour(zonedDateTime)
    fun year() = getter.year(zonedDateTime)

    fun dayOfWeek(): DayOfWeek = getter.dayOfWeek(zonedDateTime)
    fun dayOfMonth() = getter.dayOfMonth(zonedDateTime)
    fun dayOfYear() = getter.dayOfYear(zonedDateTime)
    fun dateTime() = zonedDateTime
    fun toMillisecond() = getter.toMillisecond(zonedDateTime)

    //---------------- Manipulator------------------------
    fun plus(seconds: Long = 0, minutes: Long = 0, hours: Long = 0, days: Long = 0, weeks: Long = 0, years: Long = 0): DateCed {
        zonedDateTime = manipulator.plus(
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

    fun minus(seconds: Long = 0, minutes: Long = 0, hours: Long = 0, days: Long = 0, weeks: Long = 0, years: Long = 0): DateCed {
        zonedDateTime = manipulator.minus(
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
        zonedDateTime = manipulator.toLocal(zonedDateTime = zonedDateTime)
        return this
    }

    fun toUTC(): DateCed {
        zonedDateTime = manipulator.toUTC(zonedDateTime = zonedDateTime)
        return this
    }

    fun<T:Any> fromNow(
        dateTime: T,
        timeZoneId: TimeZoneId = TimeZoneId.LOCAL,
        pattern: String? = null,
        fromNowUnit: FromNowUnit
    ):Pair<Long,FromNowLocalizeUnit>{
        return manipulator.fromNow(
            dateTime = dateTime,
            timeZoneId = timeZoneId,
            pattern = pattern,
            fromNowUnit = fromNowUnit
        )
    }

    fun<T:Any> timeDifference(
        dateTime: T,
        timeZoneId: TimeZoneId = TimeZoneId.LOCAL,
        pattern: String? = null,
        unit: TimeDifferenceUnit
    ):Long{
        return manipulator.calculateTimeDifference(
            firstZonedDateTime = zonedDateTime,
            secondDateTime = dateTime,
            timeZoneId = timeZoneId,
            pattern = pattern,
            units = unit
        )
    }


    //-------------- Query--------------------------
    fun<T:Any> isBefore(
        dateTime:T,
        timeZoneId: TimeZoneId = TimeZoneId.LOCAL,
        pattern: String? = null,
    ):Boolean {
        val dateTimeZone = dateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        return query.isBefore(
            firstZonedDateTime = zonedDateTime,
            secondDateTime = dateTimeZone,
            timeZoneId = timeZoneId,
            pattern = pattern
        )
    }

    fun<T:Any> isEqualOrBefore(
        dateTime:T,
        timeZoneId: TimeZoneId = TimeZoneId.LOCAL,
        pattern: String? = null,
    ):Boolean{
        return query.isEqualOrBefore(
            firstZonedDateTime = zonedDateTime,
            secondDateTime = dateTime,
            timeZoneId = timeZoneId,
            pattern = pattern
        )
    }

    fun<T:Any> isAfter(
        dateTime:T,
        timeZoneId: TimeZoneId = TimeZoneId.LOCAL,
        pattern: String? = null,
    ):Boolean {
        return query.isAfter(
            firstZonedDateTime = zonedDateTime,
            secondDateTime = dateTime,
            timeZoneId = timeZoneId,
            pattern = pattern
        )
    }

    fun<T:Any> isEqualOrAfter(
        dateTime:T,
        timeZoneId: TimeZoneId = TimeZoneId.LOCAL,
        pattern: String? = null,
    ):Boolean{
        val dateTimeZone = dateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        return query.isEqualOrAfter(
            firstZonedDateTime = zonedDateTime,
            secondDateTime = dateTime,
            timeZoneId = timeZoneId,
            pattern = pattern
        )
    }

    fun<T:Any> isEqual(
        dateTime:T,
        timeZoneId: TimeZoneId = TimeZoneId.LOCAL,
        pattern: String? = null,
    ):Boolean{
        val dateTimeZone = dateTime.zonedDateTime(pattern = pattern, zoneId = timeZoneId)
        return query.isEqualOrAfter(
            firstZonedDateTime = zonedDateTime,
            secondDateTime = dateTime,
            timeZoneId = timeZoneId,
            pattern = pattern
        )
    }

    fun<S:Any,T:Any> isBetween(
        secondDateTime:S,
        thirdDateTime:T,
        timeZoneId: TimeZoneId = TimeZoneId.LOCAL,
        pattern: String? = null,
    ):Boolean{
        return query.isBetween(
            firstZonedDateTime = zonedDateTime,
            secondDateTime = secondDateTime,
            thirdDateTime = thirdDateTime,
            timeZoneId = timeZoneId,
            pattern = pattern
        )
    }


    fun<S:Any,T:Any> isEqualOrBetween(
        secondDateTime:S,
        thirdDateTime:T,
        timeZoneId: TimeZoneId = TimeZoneId.LOCAL,
        pattern: String? = null,
    ):Boolean{
        return query.isEqualOrBetween(
            firstZonedDateTime = zonedDateTime,
            secondDateTime = secondDateTime,
            thirdDateTime = thirdDateTime,
            timeZoneId = timeZoneId,
            pattern = pattern
        )
    }

    fun isTodayBetweenDaysOfWeek(days:List<DayOfWeek>) = query.isTodayBetweenDaysOfWeek(days)

    fun isisLeapYear(year:Int) = query.isisLeapYear(year)

    //--------------format date------------------
    fun format(pattern:String,zoneId: TimeZoneId = TimeZoneId.LOCAL):String = zonedDateTime.format(pattern = pattern,zoneId = zoneId)

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

    @TestOnly
    internal fun setTestGetter(getter:Getter){
        this.getter = getter
    }

    @TestOnly
    internal fun setTestManipulator(manipulator:Manipulator){
        this.manipulator = manipulator
    }

    @TestOnly
    internal fun setTestManipulator(query:Query){
        this.query = query
    }

    @TestOnly
    internal fun setTestZonedDateTime(zonedDateTime: ZonedDateTime){
        this.zonedDateTime = zonedDateTime
    }
}
