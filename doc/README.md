# Dateced
## Table of content
- [Parsing](#Parsing)
- [Format Date Time](#Format Date Time)
- [Get String date time in Long value](#Get String date time in Long value)
- [Get String date time in Date Object](#Get String date time in Date Object)
- [Add Date Time](#Add Date Time)
- [Subtract Date Time](#Subtract Date Time)
- [Date In Millisecond](#Date In Millisecond)
- [Compare Date Time](#Compare Date Time)
- [Time from now](#Time from now)
- [Format Millisecond to Hour Min Second](#Format Millisecond to Hour Min Second)
- [Available Predefined Date Time format](#Available Predefined Date Time format)

###Parsing
```kotlin
 Dateced() // Returns Dateced Instance with current date time
 Dateced("2022-11-28") // Returns Dateced Instance with input date time
 Dateced().dMy // returns current date time with format ex: 28 Nov 2022
```
### Format Date Time
```kotlin
   Dateced().dMy // output: 28 Nov 2022
   Dateced("2022-11-28").format("dd MMM yyyy") // output: 28 Nov 2022
   Dateced("2022-11-28 14:00:00").format("dd MMM yyyy hh:mm aa") // output: 28 Nov 2022 02:00 PM

```

### Get String date time in Long value
```kotlin
   Dateced().toLongCurrentDateLong() // return current date time in long value
   Dateced("2022-11-28").toLongCurrentDateLong()
```

### Get String date time in Date Object
```kotlin
   Dateced("2022-11-28").toCurrentDateTime()
```

### Add Date Time
```kotlin
   Dateced("2022-11-28").add(month=1)
   Dateced("2022-11-28 13:00:00").add(hour=1,minutes=20)
```

### Subtract Date Time
```kotlin
   Dateced("2022-11-28").subtract(month=1)
   Dateced("2022-11-28 13:00:00").subtract(hour=1,minutes=20)
```

### Date In Millisecond
```kotlin
   Dateced("2022-11-28").toMilliSecond()
```
### Compare Date Time
```kotlin
   DateCed("2022-12-11").greaterThan(DateCed("2022-10-11").toDate()) //Output: true
   DateCed("2022-12-11").lessThan(DateCed("2022-10-11").toDate()) //Output: false
   DateCed("2022-11-28").fromDateTime("2022-28-27").toDateTime("2022-28-29").isInsideTheRange() // output: true
   DateCed("2022-11-28").isSameDateTime("2022-11-28") //output: true
```
### Time from now
```kotlin
   DateCed("2022-10-11").fromNow() //Output: 44 days ago
   DateCed("2022-10-11").fromNow(Units.DAY)
   DateCed("2022-10-11").fromNow(Units.MONTH)
   DateCed("2022-10-11").fromNow(Units.YEAR)
//also Second Minutes
```
### Format Millisecond to Hour Min Second
```kotlin
   DateCed().millisecondToHms(second = 3600L) // Output: 01:00:00
   DateCed().millisecondToMs(second = 70L) //Output: 01:10
```
### Available Predefined Date Time format
```kotlin
Dateced().d // call like this it automatically return formatted date time
val d get() = format("EEEE")
val y get() = format("yyyy")
val dMy get() = format("dd MMM yyyy")
val dM get() = format("dd MMM")
val dMyHms get() = format("dd MMM yyyy hh:mm:ss")
val dMyHmsA get() = format("dd MMM yyyy hh:mm:ss aa")
val dMyHmA get() = format("dd MMM yyyy hh:mm aa")
val hM get() = format("hh:mm")
val hMs get() = format("hh:mm:ss")
val hMa get() = format("hh:mm aa")
val hMsA get() = format("hh:mm:ss aa")
val sqlYMd get() = format("yyyy-MM-dd")
val sqlYMdHm get() = format("yyyy-MM-dd hh:mm")
val sqlYMdHms get() = format("yyyy-MM-dd hh:mm:ss")
val sqlYMd24Hm get() = format("yyyy-MM-dd HH:mm")
val sqlYMd24Hms get() = format("yyyy-MM-dd HH:mm:ss")
val hM24 get() = format("HH:mm")
val hMaDmY get() = format("hh:mm aa dd MMM yyyy")
```




























