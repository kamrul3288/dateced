[![](https://jitpack.io/v/kamrul3288/dateced.svg)](https://jitpack.io/#kamrul3288/dateced)
# Dateced
Dateced is a android date time library for parsing, compare and formatting dates.
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
    implementation "com.github.kamrul3288:dateced:1.0.0"
}
 ```


# Usage

## Format Dates
```kotlin
 DateCed("2022-10-11").dMyHmsA //Output: 11 Oct 2022 12:00:00 AM
 DateCed("2022-10-11").format("dd MMM yyyy") //Output: Output: 11 Oct 2022
 DateCed().currentDateTime().sqlYMd //Output: 2022-11-24
 DateCed().currentDateTime().format("dd MMM yyyy") //Output: 24 Nov 2022
```

## Converts Dates
```kotlin
DateCed().toCurrentDateTime() //Output: Date Object
DateCed().toLongCurrentDateLong() //Output: 1669311055052
DateCed("2022-10-11").toMilliSecond() //Output: 1665424800000
DateCed("2022-10-11").toDate() //Output: Date Time object
```
## Relative Time
```kotlin
DateCed("2022-10-11").fromNow() //Output: 44 days ago
DateCed("2022-10-11").fromNow(Units.MINUTES) //Output: 64772 minutes ago
```

## Comparison Date Times
```kotlin
DateCed("2022-12-11").greaterThan(DateCed("2022-10-11").toDate()) //Output: true
DateCed("2022-12-11").lessThan(DateCed("2022-10-11").toDate()) //Output: false
```

## Mainipulation Date Times
```kotlin
DateCed("2022-10-11").subtract(days = 1).dMy //Output: 10 Oct 2022
DateCed("2022-10-11").add(month = 2).dMy //Output: 11 Dec 2022
```
