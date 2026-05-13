package com.iamkamrul.dateced

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

// ======================================================================
// Compose-aware extensions — Android / Compose only
// ======================================================================

/**
 * A Compose [State] that holds the current time and updates every [intervalMs] milliseconds.
 *
 * The state is lifecycle-aware: it stops updating when the composable leaves composition.
 * Initial value is set synchronously to avoid an empty frame.
 *
 * ```kotlin
 * @Composable
 * fun ClockWidget() {
 *     val now by rememberCurrentTime()
 *     Text(text = now.dMyHmsA)
 * }
 * ```
 *
 * @param zoneId   Timezone for the returned [DateCedReadable]. Defaults to device local.
 * @param intervalMs Refresh interval in milliseconds. Default is 1 000 ms (1 second).
 */
@Composable
fun rememberCurrentTime(
    zoneId: TimeZoneId = TimeZoneId.LOCAL,
    intervalMs: Long = 1_000L,
): State<DateCedReadable> = produceState<DateCedReadable>(initialValue = DateCed.now(zoneId)) {
    while (isActive) {
        delay(intervalMs)
        value = DateCed.now(zoneId)
    }
}

/**
 * A Compose [State] that ticks every [intervalMs] ms and returns the relative time from
 * [target] to now as a [DateCedInterval] (value + unit + direction).
 *
 * Useful for countdowns ("in 3 days") and elapsed-time displays ("5 minutes ago").
 *
 * ```kotlin
 * @Composable
 * fun CountdownWidget() {
 *     val target = DateCed.parse("2026-12-25 00:00:00")
 *     val state by rememberFromNow(target)
 *     Text(
 *         text = if (state.isPast) "${state.value} ${state.unit} ago"
 *                else "in ${state.value} ${state.unit}"
 *     )
 * }
 * ```
 *
 * @param target     The [DateCedReadable] to measure from/to.
 * @param unit       Granularity; [FromNowUnit.DEFAULT] picks automatically.
 * @param intervalMs Refresh interval in milliseconds. Default is 1 000 ms.
 */
@Composable
fun rememberFromNow(
    target: DateCedReadable,
    unit: FromNowUnit = FromNowUnit.DEFAULT,
    intervalMs: Long = 1_000L,
): State<DateCedInterval> = produceState(initialValue = target.fromNowInterval(unit)) {
    while (isActive) {
        delay(intervalMs)
        value = target.fromNowInterval(unit)
    }
}

/**
 * A Compose [State] that ticks every [intervalMs] ms and returns the absolute
 * time difference between now and [target] in the given [unit].
 *
 * ```kotlin
 * @Composable
 * fun DaysUntilChristmas() {
 *     val target = DateCed.parse("2026-12-25")
 *     val days by rememberTimeDifference(target, TimeDifferenceUnit.DAY)
 *     Text("$days days until Christmas")
 * }
 * ```
 */
@Composable
fun rememberTimeDifference(
    target: DateCedReadable,
    unit: TimeDifferenceUnit,
    intervalMs: Long = 1_000L,
): State<Long> = produceState(initialValue = DateCed.now().timeDifference(target.toDateCed(), unit)) {
    while (isActive) {
        delay(intervalMs)
        value = DateCed.now().timeDifference(target.toDateCed(), unit)
    }
}
