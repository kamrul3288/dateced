package com.iamkamrul.dateced

import java.lang.Exception
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal fun <T> T.zonedDateTime(
    pattern: String?,
    zoneId: DateCedTimeZone
):ZonedDateTime{
    return when(this){
        is String ->this.parseFromString(pattern,zoneId)
        is Long ->this.parseFromMilliSeconds(zoneId)
        is Int ->this.toLong().parseFromMilliSeconds(zoneId)
        else-> throw IllegalArgumentException("$this not supported: Support only Int, long and String")
    }
}

private fun String.parseFromString(pattern:String?, zoneId: DateCedTimeZone):ZonedDateTime{
    val formatter = DateTimeFormatter.ofPattern(pattern ?: DateCedPattern.matchPattern(this))
    return try {
        LocalDateTime.parse(this,formatter).atZone(getZoneId(zoneId))
    }catch(e:Exception){
        LocalDate.parse(this,formatter).atStartOfDay().atZone(getZoneId(zoneId))
    }
}

private fun Long.parseFromMilliSeconds(zoneId:DateCedTimeZone):ZonedDateTime {
    val instant = Instant.ofEpochMilli(this)
    return instant.atZone(getZoneId(zoneId))
}


private fun getZoneId(zone: DateCedTimeZone): ZoneId {
    return when(zone){
        DateCedTimeZone.LOCAL -> ZoneId.systemDefault()
        DateCedTimeZone.UTC -> ZoneId.of("UTC")
        DateCedTimeZone.GMT -> ZoneId.of("GMT")
    }
}


internal fun ZonedDateTime.format(pattern:String,zoneId: DateCedTimeZone):String{
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return this.withZoneSameInstant(getZoneId(zoneId)).format(formatter)
}
