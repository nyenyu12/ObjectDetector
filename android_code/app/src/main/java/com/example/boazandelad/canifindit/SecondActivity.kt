package com.example.boazandelad.canifindit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.util.*

class SecondActivity : AppCompatActivity() {

    companion object {
        const val TOTAL_COUNT = "total count"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        showRandomNumber()
    }

    fun showRandomNumber() {
        val count = intent.getIntExtra(TOTAL_COUNT,0)

        val random = Random()
        var randomInt = 0

        if (count>0) {
            randomInt = random.nextInt(count+1)
        }

        findViewById<TextView>(R.id.textview_random).text = Integer.toString(randomInt)

        findViewById<TextView>(R.id.textview_label).text = getString(R.string.random_heading, count)

    }
}
