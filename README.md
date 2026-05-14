# DateCed

**A Kotlin Multiplatform date/time library inspired by PHP Carbon.**

DateCed makes date parsing, formatting, arithmetic, and relative-time display effortless across Android, iOS, and JVM — all with a clean, fluent API.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kamrul3288/dateced.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:io.github.kamrul3288)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform-orange.svg)](https://kotlinlang.org/docs/multiplatform.html)

---

## Platform Support

| Platform | Supported |
|----------|-----------|
| Android | ✅ |
| JVM (Desktop / Server) | ✅ |
| iOS (arm64, x64, simulatorArm64) | ✅ |

---

## Installation

### Core library — Android, iOS, JVM

Add to your `libs.versions.toml`:

```toml
[versions]
dateced = "2.1.0"

[libraries]
dateced        = { group = "io.github.kamrul3288", name = "dateced",         version.ref = "dateced" }
dateced-compose = { group = "io.github.kamrul3288", name = "dateced-compose", version.ref = "dateced" }
```

Add to your module's `build.gradle.kts`:

```kotlin
// All platforms — Android, iOS, JVM
implementation(libs.dateced)
```

### Jetpack Compose extensions (Android only)

```kotlin
// Core library is included automatically
implementation(libs.dateced.compose)
```

> `dateced-compose` provides `rememberCurrentTime()`, `rememberFromNow()`, and `rememberTimeDifference()`.
> If you don't use Jetpack Compose, you only need `dateced`.

---

## Features

- **Immutable** — every operation returns a new instance; originals never change
- **Thread-safe** — no shared mutable state
- **Pure Kotlin** — no Android Gradle Plugin; works on Android, iOS, and JVM equally
- **Auto-detect parsing** — recognises 15+ date/time formats without explicit patterns
- **Type-safe timezone chain** — `toUTC()` returns `DateCedReadable`, preventing invalid chains like `.toUTC().toUTC()`
- **Carbon-inspired `fromNow`** — "3 days ago", "in 2 hours" with past/future direction
- **Boundary navigation** — `startOfDay`, `endOfMonth`, `startOfWeek`, etc.
- **Field setters** — `withYear`, `withMonth`, `withHour`, etc.
- **Compose-aware** — `rememberCurrentTime`, `rememberFromNow` live-updating state *(dateced-compose)*
- **JVM interop** — bidirectional conversion with `java.time` types

---

## Quick Start

### Create

```kotlin
val now    = DateCed.now()                             // current time, local zone
val utcNow = DateCed.now(TimeZoneId.UTC)

val parsed  = DateCed.parse("2025-12-25 10:30:00")     // auto-detect format
val byEpoch = DateCed.parse(1_700_000_000_000L)        // epoch milliseconds
val safe    = DateCed.tryParse("maybe-invalid")         // returns null on failure

// Custom timezone
val dhaka = DateCed.now(TimeZoneId.of("Asia/Dhaka"))
val ny    = DateCed.now(TimeZoneId.of("America/New_York"))
```

### Parse any format automatically

```kotlin
DateCed.parse("2025-12-25")               // yyyy-MM-dd
DateCed.parse("25-12-2025")               // dd-MM-yyyy
DateCed.parse("25/12/2025")               // dd/MM/yyyy
DateCed.parse("25 Dec 2025")              // dd MMM yyyy
DateCed.parse("2025-12-25T10:30:00Z")     // ISO 8601
DateCed.parse("2025-12-25 10:30:00")      // yyyy-MM-dd HH:mm:ss

// Or with an explicit pattern:
DateCed.parse("2025-12-25", "yyyy-MM-dd")
```

### Format

```kotlin
val d = DateCed.parse("2025-06-15 14:30:00")

d.sqlYMd        // "2025-06-15"
d.sqlYMdHms     // "2025-06-15 14:30:00"
d.dMyHmsA       // "15 Jun 2025 02:30:00 PM"
d.dMyHm         // "15 Jun 2025 14:30"
d.hms24         // "14:30:00"
d.hmA           // "02:30 PM"
d.dayName       // "Sunday"

// Custom pattern (Unicode TR35)
d.format("yyyy/MM/dd")

// JVM/Android only — full Java pattern set
d.formatJvm("EEEE, MMMM d, yyyy 'at' h:mm a")  // "Sunday, June 15, 2025 at 2:30 PM"
```

### Arithmetic

```kotlin
val d = DateCed.parse("2025-01-01")

d.plus(days = 30)
d.plus(months = 3, days = 15)
d.plus(years = 1, months = 6)
d.plus(hours = 2, minutes = 30)
d.minus(weeks = 2)

// Chainable
d.plus(months = 1).minus(days = 1)   // last day of January
```

### Boundary Navigation

```kotlin
val d = DateCed.parse("2025-06-15 14:30:45")

d.startOfDay()                   // 2025-06-15 00:00:00
d.endOfDay()                     // 2025-06-15 23:59:59.999999999
d.startOfMonth()                 // 2025-06-01 00:00:00
d.endOfMonth()                   // 2025-06-30 23:59:59.999999999
d.startOfYear()                  // 2025-01-01 00:00:00
d.endOfYear()                    // 2025-12-31 23:59:59.999999999
d.startOfWeek()                  // Monday midnight (ISO 8601)
d.startOfWeek(WeekStart.SUNDAY)  // Sunday midnight
```

### Field Setters

```kotlin
val d = DateCed.parse("2025-06-15 14:30:00")

d.withYear(2030)              // 2030-06-15 14:30:00
d.withMonth(12)               // 2025-12-15 14:30:00
d.withDayOfMonth(1)           // 2025-06-01 14:30:00
d.withHour(9)                 // 2025-06-15 09:30:00

// Day clamping — no invalid dates ever
DateCed.parse("2024-02-29").withYear(2025)  // → 2025-02-28
```

### Comparison

```kotlin
val a = DateCed.parse("2025-01-01")
val b = DateCed.parse("2025-06-15")

a.isBefore(b)                  // true
b.isAfter(a)                   // true
a.isBetween(a, b)              // false (exclusive)
a.isEqualOrBetween(a, b)       // true (inclusive)
a.isLeapYear()                 // false

// Compare with string directly
a.isBefore("2025-12-31")
```

### Boolean State Properties

```kotlin
val d = DateCed.now()

d.isToday       // true
d.isYesterday   // false
d.isTomorrow    // false
d.isPast        // false (it's now)
d.isFuture      // false
d.isWeekend     // depends on day
d.isWeekday     // depends on day
```

### Time Difference

```kotlin
val a = DateCed.parse("2025-01-01")
val b = DateCed.parse("2025-03-01")

a.timeDifference(b, TimeDifferenceUnit.DAY)   // 59
a.timeDifference(b, TimeDifferenceUnit.HOUR)  // 1416
```

### From Now — Relative Time

```kotlin
val past = DateCed.parse("2023-01-01")

// Simple pair
val (value, unit) = past.fromNow()
// → Pair(2, YEARS)

// With direction (past vs future)
val interval = past.fromNowInterval()
// → DateCedInterval(value=2, unit=YEARS, isPast=true)

val label = if (interval.isPast)
    "${interval.value} ${interval.unit.name.lowercase()} ago"
else
    "in ${interval.value} ${interval.unit.name.lowercase()}"
// → "2 years ago"
```

### Timezone Conversion

```kotlin
val local = DateCed.now()

val utcView   = local.toUTC()                                  // DateCedReadable — read-only zone view
val dhakaView = local.withTimeZone(TimeZoneId.of("Asia/Dhaka"))

// Convert back for further arithmetic
val back: DateCed = local.toUTC().toDateCed()
```

---

## Jetpack Compose Integration

> Requires the `dateced-compose` artifact. See [Installation](#installation).

### Live clock

```kotlin
@Composable
fun ClockWidget() {
    val now by rememberCurrentTime()
    Text(text = now.dMyHmsA)   // updates every second
}
```

### Countdown / elapsed time

```kotlin
@Composable
fun CountdownWidget() {
    val deadline = DateCed.parse("2026-01-01 00:00:00")
    val state by rememberFromNow(deadline)
    Text(
        text = if (state.isPast) "${state.value} ${state.unit.name.lowercase()} ago"
               else "in ${state.value} ${state.unit.name.lowercase()}"
    )
}
```

### Time difference display

```kotlin
@Composable
fun DaysUntilNewYear() {
    val newYear = DateCed.parse("2026-01-01")
    val days by rememberTimeDifference(newYear, TimeDifferenceUnit.DAY)
    Text("$days days until New Year")
}
```

---

## JVM / Android — Java Interop

```kotlin
// DateCed ↔ java.time
val zdt = date.toJavaZonedDateTime()
val ldt = date.toJavaLocalDateTime()
val fromZdt = DateCed.fromJavaZonedDateTime(myZdt)
val fromLdt = DateCed.fromJavaLocalDateTime(myLdt)

// Full Java pattern support
date.formatJvm("EEEE, MMMM d, yyyy 'at' h:mm a")
DateCed.parseJvm("Sunday, June 15, 2025", "EEEE, MMMM d, yyyy")
```

---

## Supported Auto-detect Formats

| Format | Example |
|--------|---------|
| `yyyy-MM-dd HH:mm:ss` | `2025-12-25 10:30:00` |
| `yyyy-MM-dd HH:mm` | `2025-12-25 10:30` |
| `yyyy-MM-dd` | `2025-12-25` |
| `dd-MM-yyyy HH:mm:ss` | `25-12-2025 10:30:00` |
| `dd-MM-yyyy` | `25-12-2025` |
| `dd/MM/yyyy HH:mm:ss` | `25/12/2025 10:30:00` |
| `dd/MM/yyyy` | `25/12/2025` |
| `yyyy/MM/dd HH:mm:ss` | `2025/12/25 10:30:00` |
| `yyyy/MM/dd` | `2025/12/25` |
| `dd MMM yyyy HH:mm:ss` | `25 Dec 2025 10:30:00` |
| `dd MMM yyyy` | `25 Dec 2025` |
| ISO 8601 with Z | `2025-12-25T10:30:00Z` |
| ISO 8601 no zone | `2025-12-25T10:30:00` |

---

## Requirements

| Component | Minimum |
|-----------|---------|
| Android | API 21 (Android 5.0) |
| iOS | iOS 13 |
| JVM | Java 17 |
| Kotlin | 2.0+ |
| kotlinx.datetime | 0.6+ |

---

## License

```
Copyright 2024 Kamrul Hasan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0
```

---

## Changelog

### 2.1.0
- **Pure Kotlin KMP** — removed Android Gradle Plugin from core module; follows the same pattern as `kotlinx-datetime`
- **iOS support** — added `iosArm64`, `iosX64`, `iosSimulatorArm64` targets
- **New `dateced-compose` artifact** — Jetpack Compose extensions extracted into a separate module
- `rememberCurrentTime`, `rememberFromNow`, `rememberTimeDifference` moved to `dateced-compose`

> **Migration from 2.0.0:** If you use Compose extensions, add `dateced-compose` to your dependencies. Core API is unchanged.

### 2.0.0
- `TimeZoneId` upgraded to sealed class — supports any IANA timezone via `TimeZoneId.of("Asia/Dhaka")`
- New boundary methods: `startOfDay`, `endOfDay`, `startOfMonth`, `endOfMonth`, `startOfYear`, `endOfYear`, `startOfWeek`
- New field setters: `withYear`, `withMonth`, `withDayOfMonth`, `withHour`, `withMinute`, `withSecond`
- New boolean properties: `isToday`, `isYesterday`, `isTomorrow`, `isWeekend`, `isWeekday`, `isPast`, `isFuture`
- New getters: `weekOfYear`, `daysInMonth`
- New `fromNowInterval()` — returns `DateCedInterval` with `isPast` direction
- New `rememberFromNow()` Composable
- Fixed `toDateCed()` — now correctly preserves timezone
- Fixed `fromNow` thresholds to match Carbon conventions
- Bounded `DateCedFormat` cache at 64 entries
- Internal classes marked `internal` — cleaner public API surface

### 1.1.1
- Fix `fromNow()` calculation bug

### 1.1.0
- KMP migration from Android-only library
