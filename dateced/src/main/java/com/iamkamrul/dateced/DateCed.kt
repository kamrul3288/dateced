package com.iamkamrul.dateced

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

private typealias SimpleDateFormatPattern = String
private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS
private const val error  = "Error Occurred! Input Date Time Parse Error. Maybe Input Date time is empty"

class DateCed(dateTimeString : String = "") {
    private val _dateTimeString = dateTimeString.replaceInput()
    private var dateTime: Date? = null
    private var fromDateTime:Date? = null
    private var toDateTime:Date? = null


    init {
        dateTime = if (_dateTimeString.isNotEmpty()){
            val pattern:SimpleDateFormatPattern = matchPattern(_dateTimeString)
            SimpleDateFormat(pattern, Locale.US).parse(_dateTimeString) ?: throw IllegalArgumentException("Opps!, $_dateTimeString Parsed Failed")
        }else{
            toCurrentDateTime()
        }
    }

    /*
    * this method is responsible for input date time pattern matching
    * Only Two pattern support right now / and -
    * If pattern doesn't match then throw an exception
    **/
    private fun matchPattern(_input:String):SimpleDateFormatPattern{
        return if (matchYMDStringDateAndTime(_input)){
            "yyyy-MM-dd HH:mm:ss"
        }else if (matchYMDStringDate(_input)){
            "yyyy-MM-dd"
        }else if (matchDMYStringDateAndTime(_input)){
            "dd-MM-yyyy HH:mm:ss"
        }else if (matchDMYStringDate(_input)){
            "dd-MM-yyyy"
        }else throw IllegalArgumentException("Error Occurred! $_dateTimeString: Format incorrect")
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

    // return current date time
    fun toCurrentDateTime(): Date = Calendar.getInstance().time

    // return current date time in long format
    fun toLongCurrentDateLong():Long = Calendar.getInstance().time.time

    // set current date time and return object
    fun currentDateTime():DateCed = this

    /*
    * responsible for calculating previous time from now time
    * If pattern doesn't match then throw an exception
    **/
    fun fromNow(units: Units = Units.DEFAULT):String{
        dateTime?.let { dateTime->
            val now = toLongCurrentDateLong()
            if (dateTime.time > now || dateTime.time <= 0){
                return "In the future"
            }
            val diff = now - dateTime.time
            return when (units) {
                Units.DAY -> "${diff / DAY_MILLIS} days ago"
                Units.HOUR -> "${diff / HOUR_MILLIS} hours ago"
                Units.MINUTES -> "${diff / MINUTE_MILLIS} minutes ago"
                else -> when{
                    diff < SECOND_MILLIS -> "moments ago"
                    diff < 2 * MINUTE_MILLIS -> "a minute ago"
                    diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
                    diff < 2 * HOUR_MILLIS -> "an hour ago"
                    diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
                    diff < 48 * HOUR_MILLIS -> "yesterday"
                    else -> "${diff / DAY_MILLIS} days ago"
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
    fun greaterThan(date:Date):Boolean{
        return dateTime?.after(date) ?: throw IllegalArgumentException(error)
    }

    // compare two date
    fun lessThan(date:Date):Boolean{
        return dateTime?.before(date) ?: throw IllegalArgumentException(error)
    }

    //responsible for checking date time range
    fun isInsideTheRange():Boolean{
        return dateTime?.after(fromDateTime) == true && dateTime?.before(toDateTime) == true
    }
    //set from Date Time
    fun fromDateTime(inputDateTime:String):DateCed{
        val pattern:SimpleDateFormatPattern = matchPattern(inputDateTime)
        fromDateTime = SimpleDateFormat(pattern, Locale.US).parse(inputDateTime)?: throw IllegalArgumentException("Opps!, $_dateTimeString Parsed Failed")
        return this
    }
    //set To Date Time
    fun toDateTime(inputDateTime:String):DateCed{
        val pattern:SimpleDateFormatPattern = matchPattern(inputDateTime)
        toDateTime = SimpleDateFormat(pattern, Locale.US).parse(inputDateTime)?: throw IllegalArgumentException("Opps!, $_dateTimeString Parsed Failed")
        return this
    }

    //predefined date time format
    val d get() = format("EEEE")
    val y get() = format("yyyy")
    val dMy get() = format("dd MMM yyyy")
    val dMyHms get() = format("dd MMM yyyy hh:mm:ss")
    val dMyHmsA get() = format("dd MMM yyyy hh:mm:ss aa")
    val hM get() = format("hh:mm")
    val hMs get() = format("hh:mm:ss")
    val hMsA get() = format("hh:mm:ss aa")
    val sqlYMd get() = format("yyyy-MM-dd")
    val sqlYMdHm get() = format("yyyy-MM-dd hh:mm")
    val sqlYMdHms get() = format("yyyy-MM-dd hh:mm:ss")

    val sqlYMd24Hm get() = format("yyyy-MM-dd HH:mm")
    val sqlYMd24Hms get() = format("yyyy-MM-dd HH:mm:ss")

}
private fun String.replaceInput():String = this.replace("/","-")
