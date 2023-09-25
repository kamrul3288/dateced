package com.iamkamrul.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iamkamrul.dateced.DateCed


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(this, DateCed.Factory.now().dayOfWeek().toString(), Toast.LENGTH_SHORT).show()
    }
}
