package com.iamkamrul.dateced

import com.iamkamrul.dateced.DateCedPattern.matchPattern
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


private const val secondInMillSecond = 1000L
private const val minInMillSecond: Long = 60 * secondInMillSecond
private const val hourInMillSecond: Long = 60 * minInMillSecond
private const val dayInMillSecond: Long = 24 * hourInMillSecond
private const val monthInMillSecond: Long = 30 * dayInMillSecond
private const val yearInMillSecond: Long = 12 * monthInMillSecond
private const val error  = "Error Occurred! Input Date Time Parse Error. Maybe Input Date time is empty"

class DateCed(stringDateTime : String = "", longDateTime:Long = 0L, pattern: String = "", timeZone:DateCedTimeZone =  DateCedTimeZone.LOCAL) {
    private var dateTime: Date? = null

    companion object{
        // return current date time
        fun toCurrentDateTime(): Date = Calendar.getInstance().time

        // return current date time in long format
        fun toLongCurrentDateTime():Long = Calendar.getInstance().time.time

        fun millisecondToMinutesAndSecond(milliseconds:Long): Pair<Long, Long> {
            val totalSeconds = milliseconds / 1000
            return Pair(totalSeconds/60,totalSeconds%60)
        }

        fun millisecondToHourAndMinutesAndSecond(milliseconds:Long): Triple<Long, Long, Long> {
            val totalSeconds = milliseconds / 1000
            return Triple(totalSeconds / 3600 , (totalSeconds % 3600) / 60, totalSeconds % 60)
        }
    }


    init {
        matchPatternsAndParse(
            inputDateTime = stringDateTime.replaceInput(),
            longDateTime = longDateTime,
            pattern = pattern,
            timeZone = timeZone
        )
    }


    fun init(stringDateTime : String = "", longDateTime:Long = 0L, pattern: String = "",timeZone: DateCedTimeZone = DateCedTimeZone.LOCAL):DateCed{
        matchPatternsAndParse(
            inputDateTime = stringDateTime.replaceInput(),
            longDateTime = longDateTime,
            pattern = pattern,
            timeZone = timeZone
        )
        return this
    }

    private fun matchPatternsAndParse(inputDateTime : String = "", longDateTime:Long = 0L, pattern: String = "",timeZone:DateCedTimeZone){
        dateTime = when{
            inputDateTime.isNotEmpty()  -> {
                val currentPattern = pattern.ifEmpty { matchPattern(inputDateTime) }
                val format =   SimpleDateFormat(currentPattern, Locale.US)
                when(timeZone){
                    DateCedTimeZone.UTC -> format.timeZone = TimeZone.getTimeZone("UTC")
                    DateCedTimeZone.GMT -> format.timeZone = TimeZone.getTimeZone("GMT")
                    else-> format.timeZone = TimeZone.getDefault()
                }
                format.parse(inputDateTime) ?: throw IllegalArgumentException("Opps!, $inputDateTime Parsed Failed, Pattern was: $currentPattern")
            }
            longDateTime != 0L -> Date(longDateTime)
            else-> toCurrentDateTime()
        }
    }


    /*
    * responsible for formatting input date after pattern match
    * If pattern doesn't match then throw an exception
    **/
    fun format(pattern:String):String{
        val outputPattern = SimpleDateFormat(pattern,Locale.US)
        return try {
            dateTime?.let { dateTime->
                outputPattern.format(dateTime)
            }?:throw IllegalArgumentException("")
        } catch (e: ParseException) {
            throw IllegalArgumentException("Error Occurred! $pattern incorrect")
        }
    }

    /*
    * responsible for converting date in specific time zone
    * If datetime is empty then throw an exception
    **/
    fun timeZone(timeZone: DateCedTimeZone):DateCed{
        val originalTimeZone = TimeZone.getDefault()

        when(timeZone){
            DateCedTimeZone.LOCAL ->{
                val timeZoneUtc = TimeZone.getTimeZone("UTC")
                val utcOffset = originalTimeZone.rawOffset - timeZoneUtc.rawOffset
                println(utcOffset)
                dateTime = Date(dateTime?.time?.plus(utcOffset) ?: throw IllegalArgumentException("Date time is null: $dateTime"))
            }
            DateCedTimeZone.UTC -> {
                val timeZoneUtc = TimeZone.getTimeZone("UTC")
                val utcOffset = timeZoneUtc.rawOffset - originalTimeZone.rawOffset
                println(utcOffset)
                dateTime = Date(dateTime?.time?.plus(utcOffset) ?: throw IllegalArgumentException("Date time is null: $dateTime"))
            }
            DateCedTimeZone.GMT -> {
                val timeZoneUtc = TimeZone.getTimeZone("GMT")
                val utcOffset = timeZoneUtc.rawOffset - originalTimeZone.rawOffset
                dateTime = Date(dateTime?.time?.plus(utcOffset) ?: throw IllegalArgumentException("Date time is null: $dateTime"))
            }
        }

        return this
    }



    /*
    * responsible for calculating previous time from now time
    * If pattern doesn't match then throw an exception
    **/
    fun fromNow(units: Units = Units.DEFAULT):Triple<Long,LocalizeUnit,String>{
        dateTime?.let { dateTime->

            val now = toLongCurrentDateTime()
            val diff = now - dateTime.time
            return when(units){
                Units.SECOND-> Triple(first = diff / secondInMillSecond, second = LocalizeUnit.SECONDS, third = "seconds ago")
                Units.MINUTES->{
                    if (diff < 2 * minInMillSecond) Triple(first=diff/ minInMillSecond,LocalizeUnit.MINUTE, third = "minute ago")
                    else Triple(first=diff/ minInMillSecond,LocalizeUnit.MINUTES, third = "minutes ago")
                }
                Units.HOUR->{
                    if ( diff < 2 * hourInMillSecond) Triple(first=diff/ hourInMillSecond, second = LocalizeUnit.HOUR, third = "hour ago")
                    else Triple(first=diff/ hourInMillSecond, second = LocalizeUnit.HOURS, third = "hours ago")
                }
                Units.DAY->{
                    if (diff < 2 * dayInMillSecond) Triple(first=diff/ dayInMillSecond,second=LocalizeUnit.DAY,third= "day ago")
                    else Triple(first=diff/ dayInMillSecond,second=LocalizeUnit.DAYS,third= "days ago")
                }
                Units.MONTH->{
                    if (diff < 2 * monthInMillSecond)  Triple(first=diff/ monthInMillSecond,second = LocalizeUnit.MONTH, third = "month ago")
                    else Triple(first=diff/ monthInMillSecond,second = LocalizeUnit.MONTHS, third = "months ago")
                }
                Units.YEAR->{
                    if (diff < 2 * yearInMillSecond) Triple(first = diff / yearInMillSecond,second = LocalizeUnit.YEAR, third = "year ago")
                    else Triple(first = diff / yearInMillSecond,second = LocalizeUnit.YEARS, third = "years ago")
                }
                else -> when{
                    diff < secondInMillSecond -> Triple(diff / secondInMillSecond,LocalizeUnit.SECONDS,"seconds ago")
                    diff < 2 * minInMillSecond -> Triple(first=diff/ minInMillSecond,LocalizeUnit.MINUTE, third = "minute ago")
                    diff < 60 * minInMillSecond ->  Triple(first=diff/ minInMillSecond,LocalizeUnit.MINUTES, third = "minutes ago")
                    diff < 2 * hourInMillSecond -> Triple(first=diff/ hourInMillSecond, second = LocalizeUnit.HOUR, third = "hour ago")
                    diff < 24 * hourInMillSecond -> Triple(first=diff/ hourInMillSecond, second = LocalizeUnit.HOURS, third = "hours ago")
                    diff < 2 * dayInMillSecond -> Triple(first=diff/ dayInMillSecond,second=LocalizeUnit.DAY,third= "day ago")
                    diff < 30 * dayInMillSecond -> Triple(first=diff/ dayInMillSecond,second=LocalizeUnit.DAYS,third= "days ago")
                    diff < 2 * monthInMillSecond ->  Triple(first=diff/ monthInMillSecond,second = LocalizeUnit.MONTH, third = "month ago")
                    diff < 12 * monthInMillSecond ->  Triple(first=diff/ monthInMillSecond,second = LocalizeUnit.MONTHS, third = "months ago")
                    diff < 2 * yearInMillSecond -> Triple(first = diff / yearInMillSecond,second = LocalizeUnit.YEAR, third = "year ago")
                    else -> Triple(first = diff / yearInMillSecond,second = LocalizeUnit.YEAR, third = "year ago")
                }
            }
        }?:throw IllegalArgumentException(error)
    }

    /*
   * responsible adding day minutes hour and month from given date
   * If pattern doesn't match then throw an exception
   **/
    fun add(days:Int = 0,month:Int = 0, hour:Int = 0, minutes:Int = 0):DateCed{
        return dateTime?.let {dateTime->
            val calender = Calendar.getInstance()
            calender.time = dateTime
            calender.add(Calendar.DATE,days)
            calender.add(Calendar.MONTH,month)
            calender.add(Calendar.HOUR,hour)
            calender.add(Calendar.MINUTE,minutes)
            this.dateTime = calender.time
            this
        }?:throw IllegalArgumentException(error)
    }

    /*
      * responsible subtract day minutes hour and month from given date
      * If pattern doesn't match then throw an exception
      **/
    fun subtract(days:Int = 0,month:Int = 0, hour:Int = 0, minutes:Int = 0):DateCed{
        return dateTime?.let {dateTime->
            val calender = Calendar.getInstance()
            calender.time = dateTime
            calender.add(Calendar.DATE, -abs(days))
            calender.add(Calendar.MONTH,-abs(month))
            calender.add(Calendar.HOUR,-abs(hour))
            calender.add(Calendar.MINUTE,-abs(minutes))
            this.dateTime = calender.time
            this
        }?:throw IllegalArgumentException(error)
    }

    // return given date in millisecond
    fun toMilliSecond():Long = dateTime?.time ?: throw IllegalArgumentException(error)

    // return given date in date object
    fun toDate():Date = dateTime ?: throw IllegalArgumentException(error)

    // compare two date
    fun isGreaterThan(date:Date):Boolean{
        return dateTime?.after(date) ?: throw IllegalArgumentException(error)
    }

    // compare two date
    fun lessThan(date:Date):Boolean{
        return dateTime?.before(date) ?: throw IllegalArgumentException(error)
    }

    //responsible for checking date time range
    fun isInsideTheRange(fromDateTime:String,toDateTime:String,pattern: String = ""):Boolean{
        val matchedPattern = pattern.ifEmpty { matchPattern(fromDateTime.replaceInput()) }
        val from = SimpleDateFormat(matchedPattern, Locale.US).parse(fromDateTime.replaceInput())?: throw IllegalArgumentException("Opps!, $fromDateTime Parsed Failed")
        val calender = Calendar.getInstance()
        calender.time = SimpleDateFormat(matchedPattern, Locale.US).parse(toDateTime.replaceInput())?: throw IllegalArgumentException("Opps!, $toDateTime Parsed Failed")
        return dateTime?.after(from) == true && dateTime?.before(calender.time) == true
    }


    //is to date is equal
    fun isSameDateTime(fromDateTime:String,pattern: String = ""):Boolean{
        val matchedPattern = pattern.ifEmpty { matchPattern(fromDateTime.replaceInput()) }
        val from = SimpleDateFormat(matchedPattern, Locale.US).parse(fromDateTime.replaceInput())?: throw IllegalArgumentException("Opps!, $fromDateTime Parsed Failed")
        return dateTime?.time == from.time
    }


    fun differenceBetween(date: Date,unit: DiffUnits = DiffUnits.MILLISECOND):Long{
        return dateTime?.let {dateTime->
            val difference = dateTime.time.minus(date.time)
            when(unit){
                DiffUnits.MILLISECOND -> difference
                DiffUnits.SECOND -> TimeUnit.MILLISECONDS.toSeconds(difference)
                DiffUnits.DAY -> TimeUnit.MILLISECONDS.toDays(difference)
                DiffUnits.HOUR -> TimeUnit.MILLISECONDS.toHours(difference)
                DiffUnits.MINUTES -> TimeUnit.MILLISECONDS.toMinutes(difference)
            }
        }?:0L

    }

    //predefined date time format
    val day get() = format("EEEE")
    val d get() = format("dd")
    val y get() = format("yyyy")
    val m get() = format("MMM")

    val dMy get() = format("dd MMM yyyy")
    val dM get() = format("dd MMM")
    val dMyHms get() = format("dd MMM yyyy hh:mm:ss")
    val dMyHmsA get() = format("dd MMM yyyy hh:mm:ss aa")
    val dMyHmA get() = format("dd MMM yyyy hh:mm aa")

    val dMyHms24 get() = format("dd MMM yyyy HH:mm:ss")
    val dMyHm24 get() = format("dd MMM yyyy HH:mm")


    val hM get() = format("hh:mm")
    val hMs get() = format("hh:mm:ss")
    val hMa get() = format("hh:mm aa")
    val hMsA get() = format("hh:mm:ss aa")

    val hMs24 get() = format("HH:mm:ss")
    val hM24 get() = format("HH:mm")
    val h24 get() = format("HH")


    val sqlYMd get() = format("yyyy-MM-dd")
    val sqlYMdHm get() = format("yyyy-MM-dd hh:mm")
    val sqlYMdHms get() = format("yyyy-MM-dd hh:mm:ss")

    val sqlYMd24Hm get() = format("yyyy-MM-dd HH:mm")
    val sqlYMd24Hms get() = format("yyyy-MM-dd HH:mm:ss")

    val hmADmY get() = format("hh:mm aa dd MMM yyyy")
    val hmsADmY get() = format("hh:mm:ss aa dd MMM yyyy")
    val hms24DmY get() = format("HH:mm:ss dd MMM yyyy")
    val hm24DmY get() = format("HH:mm dd MMM yyyy")

}
private fun String.replaceInput():String = this.replace("/","-")
