package com.iamkamrul.dateced

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private typealias SimpleDateFormatPattern = String
private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS
private const val error  = "Error Occurred! Input Date Time Parse Error. Maybe Input Date time is empty"

class DateCed(private val dateTimeString : String = "") {
    private val _dateTimeString = dateTimeString.replaceInput()
    private var dateTime: Date? = null


    /*
    * this method is responsible for input date time pattern matching
    * Only Two pattern support right now / and -
    * If pattern doesn't match then throw an exception
    **/
    private fun matchPattern(_input:String):SimpleDateFormatPattern{
        return if (matchYMDStringDateAndTime(_input)){
            "yyyy-MM-dd hh:mm:ss"
        }else if (matchYMDStringDate(_input)){
            "yyyy-MM-dd"
        }else if (matchDMYStringDateAndTime(_input)){
            "dd-MM-yyyy hh:mm:ss"
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

}
private fun String.replaceInput():String = this.replace("/","-")
