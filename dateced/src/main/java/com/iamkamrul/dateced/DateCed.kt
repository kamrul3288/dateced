package com.iamkamrul.dateced

import java.util.*

private typealias SimpleDateFormatPattern = String
class DateCed(private val dateTimeString : String = "") {
    private val _dateTimeString = dateTimeString.replaceInput()
    private var dateTime: Date? = null


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

}
private fun String.replaceInput():String = this.replace("/","-")
