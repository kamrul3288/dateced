# Dateced

Dateced is a android date time library for parsing, compare and formatting dates.
### [Full Documentation](doc)
# How to
Step 1. Add the JitPack repository to your build file
```gradle
allprojects {
  repositories {Cancel changes
    maven {url 'https://jitpack.io'}
  }
}
```
Step 2. Add the dependency
```gradle
dependencies {
    implementation "com.github.kamrul3288:dateced:1.0.8"
}
 ```

# Format Dates
```kotlin
DateCed(stringDateTime = "2023-01-01 23:00:00").dMyHmsA //Output: 01 Jan 2023 11:00:00 PM

DateCed(longDateTime = 1680427720125).day //Output: Sunday

DateCed(stringDateTime = "2023-01-01 23:00:00").format(pattern = "dd EEE, MMM, yyyy") //Output: 01 Sun, Jan, 2023

DateCed(stringDateTime = "01 Jan 2023").hmADmY //Output: 12:00 AM 01 Jan 2023

DateCed(stringDateTime = "01 Sun, Jan, 2023", pattern = "dd EEE, MMM, yyyy").hmADmY //Output: 12:00 AM 01 Jan 2023

DateCed(stringDateTime = "2023-03-30T10:15:30.123Z").dMy //Output: 30 Mar 2023
```

## Converts Dates
```kotlin
DateCed.toCurrentDateTime() //Output: Sun Apr 02 23:29:20 BDT 2023
    
DateCed.toLongCurrentDateTime() //Output: 1680456560624

DateCed(stringDateTime = "2023-01-01 23:00:00").toDate() //Output: Sun Jan 01 23:00:00 BDT 2023

DateCed(stringDateTime = "2023-01-01 23:00:00").toMilliSecond() //Output: 1672592400000

val(minutes,seconds)  = DateCed.millisecondToMinutesAndSecond(milliseconds = 1100000)
println("$minutes minutes, $seconds second") //Output: 18 minutes, 20 second
    
val(hour,minute,sec)  = DateCed.millisecondToHourAndMinutesAndSecond(milliseconds = 1100000) 
println("$hour hour, $minute minutes, $sec second") //Output:0 hour, 18 minutes, 20 second
```
## Relative Time
```kotlin
val(days,localizeUnit,defaultLocalize) = DateCed(stringDateTime="10-11-2022").fromNow(Units.DAY)
println("$days $defaultLocalize") //Output: 143 days ago
```

## Comparison Date Times
```kotlin
DateCed(stringDateTime = "10-11-2022").isGreaterThan(DateCed.toCurrentDateTime()) //Output: false
DateCed(stringDateTime = "31-03-2023").isInsideTheRange(fromDateTime = "27-03-2023", toDateTime = "31-03-2023") //Output: true
```

## Mainipulation Date Times
```kotlin
DateCed(stringDateTime = "2023-01-01").add(days = 5).sqlYMd // Output: 2023-01-06
    
DateCed(stringDateTime = "2023-01-01").subtract(days = 5).sqlYMd // Output: 2022-12-27
```

## Extension Function
```kotlin
"2023-01-01".dateCed().day  // Output: Sunday
(167259240000).dateCed().dMyHmA  // Output: 21 Apr 1975 02:54 AM
```

