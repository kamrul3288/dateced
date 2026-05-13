package com.iamkamrul.dateced

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Shared implementation of all [DateCedReadable] operations.
 *
 * Used as a delegate inside [DateCed] (to avoid re-implementing everything) and returned
 * directly from zone-conversion methods like [DateCed.toUTC], which produce a read-only
 * view that intentionally cannot be converted again or manipulated.
 *
 * **Lazy initialisation**: [localDateTime] is computed once on first access and then cached
 * in the delegate field. All derived properties ([localDate], [localTime], getters) read
 * from [localDateTime] so the [Instant] → [LocalDateTime] conversion happens at most once.
 *
 * Marked [internal] — external callers only interact with [DateCedReadable] / [DateCed].
 */
internal class DateCedReadableDelegate(
    override val instant: Instant,
    override val timeZone: TimeZone,
) : DateCedReadable {

    // Computed once and cached (the most expensive operation in this class).
    override val localDateTime: LocalDateTime by lazy { instant.toLocalDateTime(timeZone) }

    override val localDate: LocalDate get() = localDateTime.date
    override val localTime: LocalTime get() = localDateTime.time

    // ---- Date/Time getters -----------------------------------------------

    override val millisecond: Int   get() = Getter.millisecond(instant)
    override val second: Int        get() = Getter.second(localDateTime)
    override val minute: Int        get() = Getter.minute(localDateTime)
    override val hour: Int          get() = Getter.hour(localDateTime)
    override val year: Int          get() = Getter.year(localDateTime)
    override val month: Month       get() = Getter.month(localDateTime)
    override val monthNumber: Int   get() = Getter.monthNumber(localDateTime)
    override val dayOfWeek: DayOfWeek get() = Getter.dayOfWeek(localDateTime)
    override val dayOfMonth: Int    get() = Getter.dayOfMonth(localDateTime)
    override val dayOfYear: Int     get() = Getter.dayOfYear(localDateTime)
    override val epochMilliseconds: Long get() = Getter.epochMilliseconds(instant)
    override val weekOfYear: Int    get() = Getter.weekOfYear(localDateTime)
    override val daysInMonth: Int   get() = Getter.daysInMonth(localDateTime)

    // ---- Boolean states -------------------------------------------------

    override val isPast: Boolean      get() = Query.isPast(instant)
    override val isFuture: Boolean    get() = Query.isFuture(instant)
    override val isToday: Boolean     get() = Query.isToday(instant, timeZone)
    override val isYesterday: Boolean get() = Query.isYesterday(instant, timeZone)
    override val isTomorrow: Boolean  get() = Query.isTomorrow(instant, timeZone)
    override val isWeekend: Boolean   get() = Query.isWeekend(instant, timeZone)
    override val isWeekday: Boolean   get() = Query.isWeekday(instant, timeZone)

    // ---- Custom formatting -----------------------------------------------

    override fun format(pattern: String, zoneId: TimeZoneId): String {
        val ldt = instant.toLocalDateTime(zoneId.toTimeZone())
        return DateCedFormat.dateTimeFormat(pattern).format(ldt)
    }

    override fun formatDate(pattern: String, zoneId: TimeZoneId): String {
        val date = instant.toLocalDateTime(zoneId.toTimeZone()).date
        return DateCedFormat.dateFormat(pattern).format(date)
    }

    override fun formatTime(pattern: String, zoneId: TimeZoneId): String {
        val time = instant.toLocalDateTime(zoneId.toTimeZone()).time
        return DateCedFormat.timeFormat(pattern).format(time)
    }

    // ---- Pre-built format shortcuts -------------------------------------

    override val dayName: String
        get() = dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }

    override val d: String   get() = dayOfMonth.pad()
    override val y: String   get() = year.toString()
    override val m: String   get() = month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    override val dMy: String get() = "$d $m $y"
    override val dM: String  get() = "$d $m"

    override val sqlYMd: String    get() = "$y-${monthNumber.pad()}-$d"
    override val sqlYMdHms: String get() = "$sqlYMd ${hour.pad()}:${minute.pad()}:${second.pad()}"
    override val sqlYMdHm: String  get() = "$sqlYMd ${hour.pad()}:${minute.pad()}"

    override val dMyHms: String get() = "$dMy ${hour.pad()}:${minute.pad()}:${second.pad()}"
    override val dMyHm: String  get() = "$dMy ${hour.pad()}:${minute.pad()}"

    override val hms24: String get() = "${hour.pad()}:${minute.pad()}:${second.pad()}"
    override val hm24: String  get() = "${hour.pad()}:${minute.pad()}"
    override val h24: String   get() = hour.pad()

    override val hmA: String get() {
        val (h, ampm) = to12Hour()
        return "${h.pad()}:${minute.pad()} $ampm"
    }

    override val hmsA: String get() {
        val (h, ampm) = to12Hour()
        return "${h.pad()}:${minute.pad()}:${second.pad()} $ampm"
    }

    override val dMyHmsA: String  get() = "$dMy $hmsA"
    override val dMyHmA: String   get() = "$dMy $hmA"
    override val hmADmY: String   get() = "$hmA $dMy"
    override val hmsADmY: String  get() = "$hmsA $dMy"
    override val hms24DmY: String get() = "$hms24 $dMy"
    override val hm24DmY: String  get() = "$hm24 $dMy"

    // ---- Comparison — DateCed overloads ----------------------------------

    override fun isBefore(other: DateCed): Boolean         = Query.isBefore(instant, other.instant)
    override fun isAfter(other: DateCed): Boolean          = Query.isAfter(instant, other.instant)
    override fun isEqual(other: DateCed): Boolean          = Query.isEqual(instant, other.instant)
    override fun isEqualOrBefore(other: DateCed): Boolean  = Query.isEqualOrBefore(instant, other.instant)
    override fun isEqualOrAfter(other: DateCed): Boolean   = Query.isEqualOrAfter(instant, other.instant)
    override fun isBetween(start: DateCed, end: DateCed): Boolean       = Query.isBetween(instant, start.instant, end.instant)
    override fun isEqualOrBetween(start: DateCed, end: DateCed): Boolean = Query.isEqualOrBetween(instant, start.instant, end.instant)
    override fun isLeapYear(): Boolean = Query.isLeapYear(year)

    // ---- Comparison — String overloads -----------------------------------

    override fun isBefore(dateTimeString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        isBefore(DateCed.parse(dateTimeString, pattern, zoneId))

    override fun isAfter(dateTimeString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        isAfter(DateCed.parse(dateTimeString, pattern, zoneId))

    override fun isEqual(dateTimeString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        isEqual(DateCed.parse(dateTimeString, pattern, zoneId))

    override fun isEqualOrBefore(dateTimeString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        isEqualOrBefore(DateCed.parse(dateTimeString, pattern, zoneId))

    override fun isEqualOrAfter(dateTimeString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        isEqualOrAfter(DateCed.parse(dateTimeString, pattern, zoneId))

    override fun isBetween(startString: String, endString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        isBetween(DateCed.parse(startString, pattern, zoneId), DateCed.parse(endString, pattern, zoneId))

    override fun isEqualOrBetween(startString: String, endString: String, pattern: String?, zoneId: TimeZoneId): Boolean =
        isEqualOrBetween(DateCed.parse(startString, pattern, zoneId), DateCed.parse(endString, pattern, zoneId))

    // ---- Time difference ------------------------------------------------

    override fun timeDifference(other: DateCed, unit: TimeDifferenceUnit): Long =
        Manipulator.timeDifference(instant, other.instant, unit)

    override fun timeDifference(dateTimeString: String, unit: TimeDifferenceUnit, pattern: String?, zoneId: TimeZoneId): Long =
        timeDifference(DateCed.parse(dateTimeString, pattern, zoneId), unit)

    override fun timeDifference(epochMillis: Long, unit: TimeDifferenceUnit, zoneId: TimeZoneId): Long =
        timeDifference(DateCed.parse(epochMillis, zoneId), unit)

    // ---- From Now -------------------------------------------------------

    override fun fromNow(unit: FromNowUnit): Pair<Long, FromNowLocalizeUnit> =
        Manipulator.fromNow(instant, unit)

    override fun fromNowInterval(unit: FromNowUnit): DateCedInterval =
        Manipulator.fromNowInterval(instant, unit)

    // ---- Helpers --------------------------------------------------------

    /** Zero-pad a single or double-digit integer to 2 characters. */
    private fun Int.pad(): String = toString().padStart(2, '0')

    /**
     * Convert 24-hour [hour] to a 12-hour value + AM/PM label.
     * Midnight (0) → 12 AM; Noon (12) → 12 PM.
     */
    private fun to12Hour(): Pair<Int, String> = when {
        hour == 0  -> Pair(12, "AM")
        hour < 12  -> Pair(hour, "AM")
        hour == 12 -> Pair(12, "PM")
        else       -> Pair(hour - 12, "PM")
    }

    // ---- Standard overrides ---------------------------------------------

    override fun toString(): String =
        "DateCed(instant=$instant, zone=${timeZone.id}, local=$localDateTime)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DateCedReadableDelegate) return false
        return instant == other.instant && timeZone == other.timeZone
    }

    override fun hashCode(): Int = 31 * instant.hashCode() + timeZone.hashCode()
}
