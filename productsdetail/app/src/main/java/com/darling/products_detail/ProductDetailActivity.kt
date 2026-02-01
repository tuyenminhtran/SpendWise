package com.darling.products_detail

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        findViewById<TextView>(R.id.txtName).text =
            intent.getStringExtra("name")

        findViewById<TextView>(R.id.txtPrice).text =
            intent.getStringExtra("price")
    }
}