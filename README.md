# Dateced

Meet Dateced: Your Android Time Maestro! 🌟 This sleek date-time package is your go-to for parsing, manipulating, querying, and formatting dates effortlessly

🚀 Easy Parsing: No more date confusion! Dateced understands date formats like magic, saving you time and stress.

🔄 Simple Manipulation: Want to add days or play with months? Dateced lets you do it effortlessly – no complicated tricks.

# How to
Step 1. Add the JitPack repository to your build file
```gradle
allprojects {
  repositories {
    maven {url 'https://jitpack.io'}
  }
}
```
Step 2. Add the dependency
```gradle
dependencies {
    implementation "com.github.kamrul3288:dateced:1.1.0"
}
 ```
---

### Format Dates
```kotlin
DateCed.Factory.now().dMy

DateCed.Factory.parse("2023-01-01").dMyHmA

DateCed.Factory.parse(1696147534242).dMyHmA

DateCed.Factory.parse("2023-01-01").dMyHmA

DateCed.Factory.parse("2023-01-01").toMillisecond()
```
---
### Getter

```kotlin
DateCed.Factory.parse("2023-01-01").dayOfWeek()

DateCed.Factory.now().minute()
```

---
### Manipulation
```kotlin
DateCed.Factory.now().plus(weeks = 1)

DateCed.Factory.parse("2023-10-01").minus( months = 1)

DateCed.Factory.parse("2023-10-01").fromNow(fromNowUnit = FromNowUnit.DEFAULT)

DateCed.Factory.parse("2023-10-01").timeDifference("2023-11-01", unit = TimeDifferenceUnit.DAY)
```

---
### UTC/Locale Time
```kotlin
DateCed.Factory.now().toUTC() // Locale Time To UTC

DateCed.Factory.now().toUTC().toMillisecond()

DateCed.Factory.parse("2023-10-01T08:18:59Z", zoneId = TimeZoneId.UTC).toLocal().format("dd MMM yyy")
```

---
### Query
```kotlin
DateCed.Factory.now().isBefore("2023-10-10")

DateCed.Factory.now().isAfter("2023-10-10")
        
DateCed.Factory.parse("2023-10-10").isEqual("2023-10-10")
        
DateCed.Factory.parse("2023-10-01").isBetween("2023-09-10","2023-11-01")
        
DateCed.Factory.now().isTodayBetweenDaysOfWeek(listOf(DayOfWeek.FRIDAY,DayOfWeek.SATURDAY))
        
DateCed.Factory.now().isisLeapYear(2024)
```
























