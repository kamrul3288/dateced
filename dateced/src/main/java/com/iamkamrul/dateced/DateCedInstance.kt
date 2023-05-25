package com.iamkamrul.dateced

private object DateCedInstance {
    val dateCed:DateCed = DateCed()
}

fun String.dateCed(pattern:String = "", timeZone: DateCedTimeZone = DateCedTimeZone.LOCAL):DateCed{
    DateCedInstance.dateCed.init(stringDateTime = this,pattern = pattern, timeZone = timeZone)
    return DateCedInstance.dateCed
}


fun Long.dateCed():DateCed{
    DateCedInstance.dateCed.init(longDateTime = this)
    return DateCedInstance.dateCed
}
