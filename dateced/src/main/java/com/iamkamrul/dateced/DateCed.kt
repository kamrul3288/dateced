package com.iamkamrul.dateced

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private typealias SimpleDateFormatPattern = String
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

}
private fun String.replaceInput():String = this.replace("/","-")
