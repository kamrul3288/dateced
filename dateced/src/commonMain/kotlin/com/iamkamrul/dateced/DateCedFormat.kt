package com.iamkamrul.dateced

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

/**
 * Bounded cache of kotlinx-datetime format objects, keyed by Unicode pattern string.
 *
 * Building a [DateTimeFormat] via [byUnicodePattern] involves regex compilation and is
 * moderately expensive. This cache reuses the result across calls with the same pattern.
 *
 * **Eviction policy**: When any cache reaches [MAX_SIZE] entries, all entries are cleared
 * before inserting the new one. This is simpler than LRU and correct for typical usage,
 * where apps use a small, stable set of patterns and [MAX_SIZE] is never reached.
 *
 * **Thread safety**: Not synchronized. In typical Android/KMP usage the UI thread is the
 * only caller; if you use this from multiple threads, wrap call sites in a mutex.
 *
 * Pattern syntax follows Unicode CLDR / TR35 (e.g. "yyyy-MM-dd HH:mm:ss a").
 */
@OptIn(FormatStringsInDatetimeFormats::class)
internal object DateCedFormat {

    private const val MAX_SIZE = 64

    private val dateTimeCache = HashMap<String, DateTimeFormat<LocalDateTime>>(MAX_SIZE)
    private val dateCache     = HashMap<String, DateTimeFormat<LocalDate>>(MAX_SIZE)
    private val timeCache     = HashMap<String, DateTimeFormat<LocalTime>>(MAX_SIZE)

    /** Get (or build and cache) a [LocalDateTime] format for the given [pattern]. */
    fun dateTimeFormat(pattern: String): DateTimeFormat<LocalDateTime> =
        cachedGet(dateTimeCache, pattern) { LocalDateTime.Format { byUnicodePattern(pattern) } }

    /** Get (or build and cache) a [LocalDate] format for the given [pattern]. */
    fun dateFormat(pattern: String): DateTimeFormat<LocalDate> =
        cachedGet(dateCache, pattern) { LocalDate.Format { byUnicodePattern(pattern) } }

    /** Get (or build and cache) a [LocalTime] format for the given [pattern]. */
    fun timeFormat(pattern: String): DateTimeFormat<LocalTime> =
        cachedGet(timeCache, pattern) { LocalTime.Format { byUnicodePattern(pattern) } }

    /** Evict all cached formats — useful in tests or when memory is constrained. */
    fun clearCache() {
        dateTimeCache.clear()
        dateCache.clear()
        timeCache.clear()
    }

    private inline fun <K, V> cachedGet(cache: MutableMap<K, V>, key: K, create: () -> V): V {
        cache[key]?.let { return it }
        if (cache.size >= MAX_SIZE) cache.clear()
        return create().also { cache[key] = it }
    }
}
