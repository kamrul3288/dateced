package com.iamkamrul.dateced

import kotlinx.datetime.toJavaLocalDateTime
import java.time.ZonedDateTime as JavaZonedDateTime
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.ZoneId as JavaZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

// =====================================================================
// Extensions to convert between DateCed and java.time types
// Available on: Android (XML + Compose), JVM Desktop, Server
// =====================================================================

/**
 * Convert to java.time.ZonedDateTime.
 * Useful when you need to pass to Android APIs that expect java.time.
 */
fun DateCed.toJavaZonedDateTime(): JavaZonedDateTime {
    return instant.toJavaInstant().atZone(JavaZoneId.of(timeZone.id))
}

/**
 * Convert to java.time.LocalDateTime.
 */
fun DateCed.toJavaLocalDateTime(): JavaLocalDateTime {
    return localDateTime.toJavaLocalDateTime()
}

/**
 * Create DateCed from a java.time.ZonedDateTime.
 */
fun DateCed.Companion.fromJavaZonedDateTime(
    zdt: JavaZonedDateTime,
    zoneId: TimeZoneId = TimeZoneId.LOCAL,
): DateCed {
    return from(
        instant = zdt.toInstant().toKotlinInstant(),
        zoneId = zoneId,
    )
}

/**
 * Create DateCed from a java.time.LocalDateTime.
 */
fun DateCed.Companion.fromJavaLocalDateTime(
    ldt: JavaLocalDateTime,
    zoneId: TimeZoneId = TimeZoneId.LOCAL,
): DateCed {
    val javaZdt = ldt.atZone(JavaZoneId.of(zoneId.toTimeZone().id))
    return fromJavaZonedDateTime(javaZdt, zoneId)
}

/**
 * Format using java.time.format.DateTimeFormatter.
 * Supports the FULL Java pattern set (AM/PM, era, week-of-year, etc.)
 * that kotlinx-datetime might not fully support yet.
 */
fun DateCed.formatJvm(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return toJavaZonedDateTime().format(formatter)
}

/**
 * Parse using java.time.format.DateTimeFormatter.
 * Supports any pattern that Java supports.
 */
fun DateCed.Companion.parseJvm(
    dateTimeString: String,
    pattern: String,
    zoneId: TimeZoneId = TimeZoneId.LOCAL,
): DateCed {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val javaZdt = try {
        JavaZonedDateTime.parse(dateTimeString, formatter.withZone(JavaZoneId.of(zoneId.toTimeZone().id)))
    } catch (_: Exception) {
        val ldt = JavaLocalDateTime.parse(dateTimeString, formatter)
        ldt.atZone(JavaZoneId.of(zoneId.toTimeZone().id))
    }
    return fromJavaZonedDateTime(javaZdt, zoneId)
}