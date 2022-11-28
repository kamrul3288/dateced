package com.iamkamrul.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iamkamrul.dateced.DateCed
import com.iamkamrul.dateced.Units

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DateCed("2022-10-11").dMyHmsA //Output: 11 Oct 2022 12:00:00 AM

        DateCed("2022-10-11").format("dd MMM yyyy") //Output: 11 Oct 2022

        DateCed().toCurrentDateTime() //Output: Date Object

        DateCed().toLongCurrentDateLong() //Output: 1669311055052

        DateCed().currentDateTime().sqlYMd //Output: 2022-11-24

        DateCed().currentDateTime().format("dd MMM yyyy") //Output: 24 Nov 2022

        DateCed("2022-10-11").fromNow(Units.DAY) //Output: 44 days ago

        DateCed("2022-10-11").fromNow(Units.MINUTES) //Output: 64772 minutes ago

        DateCed("2022-10-11").subtract(days = 1).dMy //Output: 10 Oct 2022

        DateCed("2022-10-11").add(month = 2).dMy //Output: 11 Dec 2022

        DateCed("2022-10-11").toMilliSecond() //Output: 1665424800000

        DateCed("2022-10-11").toDate() //Output: Date Time object

        DateCed("2022-12-11").greaterThan(DateCed("2022-10-11").toDate()) //Output: true

        DateCed("2022-12-11").lessThan(DateCed("2022-10-11").toDate()) //Output: false

        DateCed("2022-11-28").fromDateTime("2022-28-27").toDateTime("2022-28-29").isInsideTheRange()
        DateCed("2022-11-28").isSameDateTime("2022-11-28") //output: true

        DateCed().millisecondToMs(second = 70L)
        DateCed().d //Output: Thursday

    }
}