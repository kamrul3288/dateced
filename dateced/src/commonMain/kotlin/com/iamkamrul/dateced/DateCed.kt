package com.iamkamrul.dateced

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * DateCed — A multiplatform date/time library inspired by PHP Carbon.
 *
 * ## Key design principles
 *
 * - **Immutable**: Every mutating operation returns a new [DateCed] instance.
 *   The original is never changed.
 * - **Thread-safe**: No shared mutable state. Safe to read from multiple coroutines.
 * - **Multiplatform**: Runs on Android (XML + Compose), JVM, iOS, and JS via
 *   [kotlinx.datetime] and [kotlin.time].
 * - **Type-safe zone chain**: [toUTC]/[toLocal]/[toGMT] return [DateCedReadable],
 *   which has no further conversion methods — preventing invalid chains like
 *   `now.toUTC().toUTC()`.
 *
 * ## Usage
 *
 * ```kotlin
 * // Create
 * val now    = DateCed.now()
 * val parsed = DateCed.parse("2025-12-25 10:30:00")
 * val epoch  = DateCed.parse(1_700_000_000_000L)
 *
 * // Arithmetic (returns new instance)
 * val future = now.plus(days = 30)
 * val past   = now.minus(months = 3)
 *
 * // Navigation
 * val monthStart = now.startOfMonth()
 * val nextYear   = now.withYear(now.year + 1)
 *
 * // Format
 * val label = now.dMyHmsA          // "25 Dec 2025 10:30:00 AM"
 * val sql   = now.sqlYMdHms        // "2025-12-25 10:30:00"
 *
 * // Zone conversion
 * val utcLabel = now.toUTC().dMyHmsA
 *
 * // Query
 * val diff = now.timeDifference(parsed, TimeDifferenceUnit.DAY)
 * val (val, unit) = now.fromNow()
 * val interval    = now.fromNowInterval()   // includes isPast
 * ```
 */
class DateCed private constructor(
    override val instant: Instant,
    override val timeZone: TimeZone,
) : DateCedConvertible {

    // Internal delegate that holds all read-only logic.
    // Kept as a field (not computed each time) so the lazy LocalDateTime cache is reused.
    private val delegate = DateCedReadableDelegate(instant, timeZone)

    // ======================================================================
    // Factory
    // ======================================================================

    companion object {

        /** Create a [DateCed] for the current moment in [zoneId]. */
        fun now(zoneId: TimeZoneId = TimeZoneId.LOCAL): DateCed =
            DateCed(instant = Clock.System.now(), timeZone = zoneId.toTimeZone())

        /**
         * Parse from a type-safe [DateInput].
         * Prefer this overload when the input type is not known at compile time.
         */
        fun parse(input: DateInput, zoneId: TimeZoneId = TimeZoneId.LOCAL): DateCed {
            val tz = zoneId.toTimeZone()
            val instant = when (input) {
                is DateInput.FromString      -> parseString(input.value, input.pattern, tz)
                is DateInput.FromEpochMillis -> Instant.fromEpochMilliseconds(input.value)
                is DateInput.FromInstant     -> input.value
            }
            return DateCed(instant = instant, timeZone = tz)
        }

        /**
         * Parse from a date/time string with an optional format [pattern].
         *
         * When [pattern] is null, the format is auto-detected from the string.
         * Supported auto-detected formats: yyyy-MM-dd, dd-MM-yyyy, dd/MM/yyyy,
         * yyyy/MM/dd, dd MMM yyyy — with optional " HH:mm" or " HH:mm:ss" suffix.
         *
         * @throws IllegalArgumentException if the string cannot be parsed.
         */
        fun parse(
            dateTimeString: String,
            pattern: String? = null,
            zoneId: TimeZoneId = TimeZoneId.LOCAL,
        ): DateCed = parse(DateInput.FromString(dateTimeString, pattern), zoneId)

        /** Parse from epoch milliseconds. */
        fun parse(epochMillis: Long, zoneId: TimeZoneId = TimeZoneId.LOCAL): DateCed =
            parse(DateInput.FromEpochMillis(epochMillis), zoneId)

        /** Wrap an existing [Instant] with a [TimeZoneId]. */
        fun from(instant: Instant, zoneId: TimeZoneId = TimeZoneId.LOCAL): DateCed =
            DateCed(instant = instant, timeZone = zoneId.toTimeZone())

        /** Wrap an existing [Instant] with a raw [TimeZone] (used internally by [toDateCed]). */
        fun from(instant: Instant, timeZone: TimeZone): DateCed =
            DateCed(instant = instant, timeZone = timeZone)

        /**
         * Safe parse — returns `null` instead of throwing.
         * Ideal for validating user input without try/catch at the call site.
         */
        fun tryParse(
            dateTimeString: String,
            pattern: String? = null,
            zoneId: TimeZoneId = TimeZoneId.LOCAL,
        ): DateCed? = try { parse(dateTimeString, pattern, zoneId) } catch (_: Exception) { null }

        /**
         * Parse from a date-time string using a pre-built [DateTimeFormat].
         *
         * Prefer this over the string-pattern overload when you need type safety,
         * localized names ([DayOfWeekNames], [MonthNames]), or optional sections
         * via the [optional] DSL block.
         */
        fun parse(
            input: String,
            pattern: DateTimeFormat<LocalDateTime>,
            zoneId: TimeZoneId = TimeZoneId.LOCAL,
        ): DateCed {
            val tz = zoneId.toTimeZone()
            return DateCed(instant = pattern.parse(input).toInstant(tz), timeZone = tz)
        }

        /**
         * Parse a date-only string using a pre-built [DateTimeFormat].
         * Time defaults to midnight (00:00:00).
         */
        fun parseDate(
            input: String,
            pattern: DateTimeFormat<LocalDate>,
            zoneId: TimeZoneId = TimeZoneId.LOCAL,
        ): DateCed {
            val tz = zoneId.toTimeZone()
            val ldt = LocalDateTime(pattern.parse(input), LocalTime(0, 0, 0))
            return DateCed(instant = ldt.toInstant(tz), timeZone = tz)
        }

        /**
         * Parse a time-only string using a pre-built [DateTimeFormat].
         * Date defaults to today in [zoneId].
         */
        fun parseTime(
            input: String,
            pattern: DateTimeFormat<LocalTime>,
            zoneId: TimeZoneId = TimeZoneId.LOCAL,
        ): DateCed {
            val tz = zoneId.toTimeZone()
            val today = Clock.System.now().toLocalDateTime(tz).date
            return DateCed(instant = LocalDateTime(today, pattern.parse(input)).toInstant(tz), timeZone = tz)
        }

        /** Safe variant of [parse] with a [DateTimeFormat] — returns `null` instead of throwing. */
        fun tryParse(
            input: String,
            format: DateTimeFormat<LocalDateTime>,
            zoneId: TimeZoneId = TimeZoneId.LOCAL,
        ): DateCed? = try { parse(input, format, zoneId) } catch (_: Exception) { null }

        // ---- Internal parsing ----

        private fun parseString(value: String, pattern: String?, tz: TimeZone): Instant {
            if (pattern != null) return parseWithPattern(value, pattern, tz)
            // Try ISO-8601 instant ("2025-01-01T10:30:00Z") first — fastest path.
            tryParseIso(value)?.let { return it }
            return DateCedPattern.parseAutoDetect(value).toInstant(tz)
        }

        private fun parseWithPattern(value: String, pattern: String, tz: TimeZone): Instant {
            // Try full date-time
            runCatching {
                return DateCedFormat.dateTimeFormat(pattern).parse(value).toInstant(tz)
            }
            // Try date-only (time defaults to midnight)
            runCatching {
                val date = DateCedFormat.dateFormat(pattern).parse(value)
                return LocalDateTime(date, LocalTime(0, 0, 0)).toInstant(tz)
            }
            // Try time-only (date defaults to today)
            runCatching {
                val time  = DateCedFormat.timeFormat(pattern).parse(value)
                val today = Clock.System.now().toLocalDateTime(tz).date
                return LocalDateTime(today, time).toInstant(tz)
            }
            throw IllegalArgumentException("Cannot parse '$value' with pattern '$pattern'")
        }

        private fun tryParseIso(value: String): Instant? =
            runCatching { Instant.parse(value) }.getOrNull()
    }

    // ======================================================================
    // Exposed read-only properties (delegated)
    // ======================================================================

    override val localDateTime: LocalDateTime get() = delegate.localDateTime
    override val localDate: LocalDate         get() = delegate.localDate
    override val localTime: LocalTime         get() = delegate.localTime

    override val millisecond: Int   get() = delegate.millisecond
    override val second: Int        get() = delegate.second
    override val minute: Int        get() = delegate.minute
    override val hour: Int          get() = delegate.hour
    override val year: Int          get() = delegate.year
    override val month: Month       get() = delegate.month
    override val monthNumber: Int   get() = delegate.monthNumber
    override val dayOfWeek: DayOfWeek get() = delegate.dayOfWeek
    override val dayOfMonth: Int    get() = delegate.dayOfMonth
    override val dayOfYear: Int     get() = delegate.dayOfYear
    override val epochMilliseconds: Long get() = delegate.epochMilliseconds
    override val weekOfYear: Int    get() = delegate.weekOfYear
    override val daysInMonth: Int   get() = delegate.daysInMonth

    override val isPast: Boolean      get() = delegate.isPast
    override val isFuture: Boolean    get() = delegate.isFuture
    override val isToday: Boolean     get() = delegate.isToday
    override val isYesterday: Boolean get() = delegate.isYesterday
    override val isTomorrow: Boolean  get() = delegate.isTomorrow
    override val isWeekend: Boolean   get() = delegate.isWeekend
    override val isWeekday: Boolean   get() = delegate.isWeekday

    // ======================================================================
    // Arithmetic
    // ======================================================================

    override fun plus(
        seconds: Long, minutes: Long, hours: Long,
        days: Long, weeks: Long, months: Int, years: Int,
    ): DateCed {
        if (seconds == 0L && minutes == 0L && hours == 0L &&
            days == 0L && weeks == 0L && months == 0 && years == 0) return this
        return DateCed(
            instant = Manipulator.plus(instant, timeZone, seconds, minutes, hours, days, weeks, months, years),
            timeZone = timeZone,
        )
    }

    override fun minus(
        seconds: Long, minutes: Long, hours: Long,
        days: Long, weeks: Long, months: Int, years: Int,
    ): DateCed {
        if (seconds == 0L && minutes == 0L && hours == 0L &&
            days == 0L && weeks == 0L && months == 0 && years == 0) return this
        return DateCed(
            instant = Manipulator.minus(instant, timeZone, seconds, minutes, hours, days, weeks, months, years),
            timeZone = timeZone,
        )
    }

    // ======================================================================
    // Boundary navigation
    // ======================================================================

    override fun startOfDay(): DateCed   = DateCed(Manipulator.startOfDay(instant, timeZone),   timeZone)
    override fun endOfDay(): DateCed     = DateCed(Manipulator.endOfDay(instant, timeZone),     timeZone)
    override fun startOfMonth(): DateCed = DateCed(Manipulator.startOfMonth(instant, timeZone), timeZone)
    override fun endOfMonth(): DateCed   = DateCed(Manipulator.endOfMonth(instant, timeZone),   timeZone)
    override fun startOfYear(): DateCed  = DateCed(Manipulator.startOfYear(instant, timeZone),  timeZone)
    override fun endOfYear(): DateCed    = DateCed(Manipulator.endOfYear(instant, timeZone),    timeZone)

    override fun startOfWeek(weekStart: WeekStart): DateCed =
        DateCed(Manipulator.startOfWeek(instant, timeZone, weekStart), timeZone)

    // ======================================================================
    // Field setters
    // ======================================================================

    override fun withYear(year: Int): DateCed         = DateCed(Manipulator.withYear(instant, timeZone, year),       timeZone)
    override fun withMonth(month: Int): DateCed       = DateCed(Manipulator.withMonth(instant, timeZone, month),     timeZone)
    override fun withDayOfMonth(day: Int): DateCed    = DateCed(Manipulator.withDayOfMonth(instant, timeZone, day),  timeZone)
    override fun withHour(hour: Int): DateCed         = DateCed(Manipulator.withHour(instant, timeZone, hour),       timeZone)
    override fun withMinute(minute: Int): DateCed     = DateCed(Manipulator.withMinute(instant, timeZone, minute),   timeZone)
    override fun withSecond(second: Int): DateCed     = DateCed(Manipulator.withSecond(instant, timeZone, second),   timeZone)

    // ======================================================================
    // Zone conversion
    // ======================================================================

    override fun withTimeZone(zoneId: TimeZoneId): DateCedReadable {
        val target = zoneId.toTimeZone()
        return if (timeZone == target) delegate
        else DateCedReadableDelegate(instant, target)
    }

    override fun withTimeZone(tz: TimeZone): DateCedReadable =
        if (timeZone == tz) delegate else DateCedReadableDelegate(instant, tz)

    override fun toLocal(): DateCedReadable = withTimeZone(TimeZoneId.LOCAL)
    override fun toUTC(): DateCedReadable   = withTimeZone(TimeZoneId.UTC)
    override fun toGMT(): DateCedReadable   = withTimeZone(TimeZoneId.GMT)

    // ======================================================================
    // Comparison (delegated)
    // ======================================================================

    override fun isBefore(other: DateCed): Boolean         = delegate.isBefore(other)
    override fun isAfter(other: DateCed): Boolean          = delegate.isAfter(other)
    override fun isEqual(other: DateCed): Boolean          = delegate.isEqual(other)
    override fun isEqualOrBefore(other: DateCed): Boolean  = delegate.isEqualOrBefore(other)
    override fun isEqualOrAfter(other: DateCed): Boolean   = delegate.isEqualOrAfter(other)
    override fun isBetween(start: DateCed, end: DateCed): Boolean        = delegate.isBetween(start, end)
    override fun isEqualOrBetween(start: DateCed, end: DateCed): Boolean = delegate.isEqualOrBetween(start, end)
    override fun isLeapYear(): Boolean = delegate.isLeapYear()

    override fun isBefore(dateTimeString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        delegate.isBefore(dateTimeString, pattern, zoneId)
    override fun isAfter(dateTimeString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        delegate.isAfter(dateTimeString, pattern, zoneId)
    override fun isEqual(dateTimeString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        delegate.isEqual(dateTimeString, pattern, zoneId)
    override fun isEqualOrBefore(dateTimeString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        delegate.isEqualOrBefore(dateTimeString, pattern, zoneId)
    override fun isEqualOrAfter(dateTimeString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        delegate.isEqualOrAfter(dateTimeString, pattern, zoneId)
    override fun isBetween(startString: String, endString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        delegate.isBetween(startString, endString, pattern, zoneId)
    override fun isEqualOrBetween(startString: String, endString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        delegate.isEqualOrBetween(startString, endString, pattern, zoneId)

    // ======================================================================
    // Time difference (delegated)
    // ======================================================================

    override fun timeDifference(other: DateCed, unit: TimeDifferenceUnit): Long =
        delegate.timeDifference(other, unit)
    override fun timeDifference(dateTimeString: String, unit: TimeDifferenceUnit, pattern: String?, zoneId: TimeZoneId): Long =
        delegate.timeDifference(dateTimeString, unit, pattern, zoneId)
    override fun timeDifference(epochMillis: Long, unit: TimeDifferenceUnit, zoneId: TimeZoneId): Long =
        delegate.timeDifference(epochMillis, unit, zoneId)

    // ======================================================================
    // From Now (delegated)
    // ======================================================================

    override fun fromNow(unit: FromNowUnit): Pair<Long, FromNowLocalizeUnit> = delegate.fromNow(unit)
    override fun fromNowInterval(unit: FromNowUnit): DateCedInterval = delegate.fromNowInterval(unit)

    // ======================================================================
    // Formatting (delegated)
    // ======================================================================

    override fun format(pattern: String, zoneId: TimeZoneId): String     = delegate.format(pattern, zoneId)
    override fun formatDate(pattern: String, zoneId: TimeZoneId): String = delegate.formatDate(pattern, zoneId)
    override fun formatTime(pattern: String, zoneId: TimeZoneId): String = delegate.formatTime(pattern, zoneId)

    override fun format(format: DateTimeFormat<LocalDateTime>, zoneId: TimeZoneId): String     = delegate.format(format, zoneId)
    override fun formatDate(format: DateTimeFormat<LocalDate>, zoneId: TimeZoneId): String     = delegate.formatDate(format, zoneId)
    override fun formatTime(format: DateTimeFormat<LocalTime>, zoneId: TimeZoneId): String     = delegate.formatTime(format, zoneId)

    override val dayName: String  get() = delegate.dayName
    override val d: String        get() = delegate.d
    override val y: String        get() = delegate.y
    override val m: String        get() = delegate.m
    override val dMy: String      get() = delegate.dMy
    override val dM: String       get() = delegate.dM
    override val sqlYMd: String   get() = delegate.sqlYMd
    override val sqlYMdHms: String get() = delegate.sqlYMdHms
    override val sqlYMdHm: String  get() = delegate.sqlYMdHm
    override val dMyHms: String   get() = delegate.dMyHms
    override val dMyHm: String    get() = delegate.dMyHm
    override val hms24: String    get() = delegate.hms24
    override val hm24: String     get() = delegate.hm24
    override val h24: String      get() = delegate.h24
    override val hmA: String      get() = delegate.hmA
    override val hmsA: String     get() = delegate.hmsA
    override val dMyHmsA: String  get() = delegate.dMyHmsA
    override val dMyHmA: String   get() = delegate.dMyHmA
    override val hmADmY: String   get() = delegate.hmADmY
    override val hmsADmY: String  get() = delegate.hmsADmY
    override val hms24DmY: String get() = delegate.hms24DmY
    override val hm24DmY: String  get() = delegate.hm24DmY

    // ======================================================================
    // Standard overrides
    // ======================================================================

    override fun toString(): String = delegate.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DateCed) return false
        return instant == other.instant && timeZone == other.timeZone
    }

    override fun hashCode(): Int = 31 * instant.hashCode() + timeZone.hashCode()
}
