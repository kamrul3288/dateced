package com.iamkamrul.dateced

private typealias SimpleDateFormatPattern = String

internal object DateCedPattern {
    fun matchPattern(_input:String):SimpleDateFormatPattern{
        return if (matchYMDStringDateAndTime(_input)){
            "yyyy-MM-dd HH:mm:ss"
        }else if (matchYMDStringDate(_input)){
            "yyyy-MM-dd"
        }else if (matchDMYStringDateAndTime(_input)){
            "dd-MM-yyyy HH:mm:ss"
        }else if (matchDMYStringDate(_input)){
            "dd-MM-yyyy"
        }else throw IllegalArgumentException("Error Occurred! $_input: Format incorrect")
    }

}

private fun  matchYMDStringDateAndTime(input:String):Boolean{
    return Regex("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$").matches(input)
}

private fun matchYMDStringDate(input:String):Boolean{
    return Regex("\\d{4}-\\d{1,2}-\\d{1,2}\$").matches(input)
}

private fun matchDMYStringDateAndTime(input:String):Boolean{
    return Regex("\\d{1,2}-\\d{1,2}-\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2}$").matches(input)
}

private fun matchDMYStringDate(input:String):Boolean{
    return Regex("\\d{1,2}-\\d{1,2}-\\d{4}\$").matches(input)
}