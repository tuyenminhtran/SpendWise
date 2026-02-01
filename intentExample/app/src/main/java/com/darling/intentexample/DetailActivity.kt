package com.darling.intentexample

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //lấy liệu trong key EXTRA_NAME
        val name = intent.getStringExtra(MainActivity.EXTRA_NAME)
        //lấy liệu trong key EXTRA_AGE
        val age = intent.getIntExtra(MainActivity.EXTRA_AGE, -1)

        //hiển thị dữ liệu lên TextView Received
        val tv = findViewById<TextView>(R.id.tvReceived)
        tv.text = "Name: $name\nAge: $age"
    }
}