package com.iamkamrul.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iamkamrul.dateced.DateCed
import com.iamkamrul.dateced.DateCedTimeZone
import com.iamkamrul.dateced.DiffUnits
import com.iamkamrul.dateced.Units
import com.iamkamrul.dateced.dateCed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        example()
    }

    private fun example(){
        println(DateCed.toCurrentDateTime())
        println(DateCed.toLongCurrentDateTime())

        val (minutes,seconds) = DateCed.millisecondToMinutesAndSecond(milliseconds = 201000)
        println("$minutes Minutes $seconds Seconds")

        val (h,m,s) = DateCed.millisecondToHourAndMinutesAndSecond(milliseconds = 201000)
        println("$h Hours $m Minutes $s Seconds")

        println(DateCed(stringDateTime = "2022-10-11").dMyHms)

        val(days,localizeUnit,defaultLocalize) = DateCed(stringDateTime="10-11-2022").fromNow(Units.DAY)
        println("$days $defaultLocalize")

        println(DateCed().add(days = 2).dMy)
        println(DateCed().subtract(days = 2).dMy)

        println(DateCed().toMilliSecond())
        println(DateCed().toDate())

        val isGreaterThan = DateCed("10-11-2022").isGreaterThan(DateCed.toCurrentDateTime())
        println(isGreaterThan)

        val isInside = DateCed(stringDateTime = "31-03-2023").isInsideTheRange(fromDateTime = "27-03-2023", toDateTime = "31-03-2023")
        println(isInside)

        val isSameDate = DateCed(stringDateTime = "31-03-2023").isSameDateTime(fromDateTime = "27-03-2023")
        println(isSameDate)


        println(DateCed("2023-01-01 23:00:00").dMyHmsA)
        println(DateCed("2023-01-01 23:00").dMyHmsA)

        println(DateCed("2023-01-01 11:00:00 AM").dMyHmsA)
        println(DateCed("2023-01-01 11:00 AM").dMyHmsA)

        println(DateCed("2023-01-01").dMyHmsA)
        println(DateCed("01-01-2023").dMyHmsA)

        println(DateCed("01-01-2023 23:00:00").dMyHmsA)
        println(DateCed("01-01-2023 23:00").dMyHmsA)

        println(DateCed("01-01-2023 11:00:00 AM").dMyHmsA)
        println(DateCed("01-01-2023 11:00 AM").dMyHmsA)


        println(DateCed("01 Jan 2023").dMyHmsA)
        println(DateCed("01 Jan 2023 11:00:00 AM").dMyHmsA)
        println(DateCed("01 Jan 2023 11:00 AM").dMyHmsA)

        println(DateCed("01 Jan 2023 23:00:00").dMyHmsA)
        println(DateCed("01 Jan 2023 23:00").dMyHmsA)


        println(DateCed("23:00:00").dMyHmsA)
        println(DateCed("23:00").dMyHmsA)

        println(DateCed("11:00:00 PM").hM24)
        println(DateCed("11:00 PM").hM24)

        println(DateCed("2023-03-30T10:15:30.123Z").dMyHmsA)
    }
}
