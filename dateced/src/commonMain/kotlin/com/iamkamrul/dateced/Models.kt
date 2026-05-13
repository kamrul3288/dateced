package com.iamkamrul.dateced

import kotlinx.datetime.TimeZone
import kotlin.time.Instant

// ===================== Timezone =====================

/**
 * Sealed hierarchy for timezone identification.
 *
 * Prefer the predefined objects ([LOCAL], [UTC], [GMT]) for common zones.
 * Use [Custom] or [of] for any IANA timezone string.
 *
 * ```kotlin
 * val dhaka = TimeZoneId.of("Asia/Dhaka")
 * val ny    = TimeZoneId.of("America/New_York")
 * val utc   = TimeZoneId.UTC
 * ```
 */
sealed class TimeZoneId {

    /** Device's local timezone — resolved at call time via [TimeZone.currentSystemDefault]. */
    data object LOCAL : TimeZoneId()

    /** UTC (Universal Coordinated Time). */
    data object UTC : TimeZoneId()

    /** GMT (Greenwich Mean Time). Functionally equivalent to UTC. */
    data object GMT : TimeZoneId()

    /**
     * Any IANA-compliant timezone string.
     * Examples: "Asia/Dhaka", "America/New_York", "Europe/London", "UTC+6".
     *
     * @throws IllegalTimeZoneException at runtime if [id] is not a valid IANA identifier.
     */
    data class Custom(val id: String) : TimeZoneId()

    /** Convert this identifier to a kotlinx [TimeZone]. */
    fun toTimeZone(): TimeZone = when (this) {
        LOCAL     -> TimeZone.currentSystemDefault()
        UTC       -> TimeZone.UTC
        GMT       -> TimeZone.of("GMT")
        is Custom -> TimeZone.of(id)
    }

    companion object {
        /**
         * Create a [TimeZoneId] from any IANA timezone string.
         * Shorthand for [Custom].
         */
        fun of(id: String): TimeZoneId = Custom(id)
    }
}

// ===================== Week =====================

/**
 * Defines which day begins the week, used by [DateCed.startOfWeek].
 * ISO 8601 (most of the world) starts on [MONDAY]; North America often uses [SUNDAY].
 */
enum class WeekStart(
    /** ISO day number: Monday = 1 … Sunday = 7. */
    val isoDayNumber: Int,
) {
    MONDAY(1),
    SUNDAY(7),
}

// ===================== From-Now =====================

/**
 * Granularity selector for [DateCed.fromNow] and [DateCed.fromNowInterval].
 * [DEFAULT] picks the most human-readable unit automatically.
 */
enum class FromNowUnit { DEFAULT, SECOND, MINUTES, HOUR, DAY, MONTH, YEAR }

/**
 * Localization keys returned by [DateCed.fromNow].
 * Singular/plural pairs exist for every unit so the caller can apply their own i18n rules.
 */
enum class FromNowLocalizeUnit {
    SECOND, SECONDS,
    MINUTE, MINUTES,
    HOUR, HOURS,
    DAY, DAYS,
    MONTH, MONTHS,
    YEAR, YEARS,
}

/**
 * Rich result from [DateCed.fromNowInterval] — carries value, unit, and direction.
 *
 * ```kotlin
 * val interval = date.fromNowInterval()
 * val label = if (interval.isPast)
 *     "${interval.value} ${interval.unit} ago"
 * else
 *     "in ${interval.value} ${interval.unit}"
 * ```
 */
data class DateCedInterval(
    val value: Long,
    val unit: FromNowLocalizeUnit,
    /** `true` when this date is in the past relative to now. */
    val isPast: Boolean,
)

// ===================== Time Difference =====================

/** Unit for [DateCed.timeDifference]. */
enum class TimeDifferenceUnit { MILLISECOND, SECOND, MINUTES, HOUR, DAY }

// ===================== Type-safe Input =====================

/**
 * Sealed input type for [DateCed.parse].
 * Eliminates unchecked generic casting and makes invalid states unrepresentable at compile time.
 */
sealed interface DateInput {
    /** Parse from a raw string with an optional Unicode format pattern. */
    data class FromString(val value: String, val pattern: String? = null) : DateInput

    /** Parse from epoch milliseconds. */
    data class FromEpochMillis(val value: Long) : DateInput

    /** Wrap an existing [Instant] directly — no parsing needed. */
    data class FromInstant(val value: Instant) : DateInput
}
