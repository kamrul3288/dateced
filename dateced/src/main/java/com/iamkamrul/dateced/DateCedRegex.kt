package com.iamkamrul.dateced

internal fun  matchYMDStringDateAndTime(input:String):Boolean{
    return Regex("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$").matches(input)
}

internal fun matchYMDStringDate(input:String):Boolean{
    return Regex("\\d{4}-\\d{1,2}-\\d{1,2}\$").matches(input)
}

internal fun matchDMYStringDateAndTime(input:String):Boolean{
    return Regex("\\d{1,2}-\\d{1,2}-\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2}$").matches(input)
}

internal fun matchDMYStringDate(input:String):Boolean{
    return Regex("\\d{1,2}-\\d{1,2}-\\d{4}\$").matches(input)
}