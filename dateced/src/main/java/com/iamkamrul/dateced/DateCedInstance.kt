package com.iamkamrul.dateced

private object DateCedInstance {
    val dateCed:DateCed = DateCed()
}

fun String.dateCed(pattern:String = ""):DateCed{
    DateCedInstance.dateCed.init(stringDateTime = this,pattern = pattern)
    return DateCedInstance.dateCed
}


fun Long.dateCed():DateCed{
    DateCedInstance.dateCed.init(longDateTime = this)
    return DateCedInstance.dateCed
}
