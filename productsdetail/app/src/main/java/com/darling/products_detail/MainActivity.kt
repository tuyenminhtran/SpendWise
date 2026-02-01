package com.darling.products_detail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.rvProduct)

        val products = listOf(
            Product("Điện thoại", "10.000.000đ", R.drawable.ic_samsung),
            Product("Điện thoại", "14.000.000đ", R.drawable.ic_iqoo),
            Product("Laptop", "27.000.000đ", R.drawable.ic_laptop),
            Product("Máy ảnh ( Body )", "9.000.000đ", R.drawable.ic_mayanh)
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = ProductAdapter(products) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("name", product.name)
            intent.putExtra("price", product.price)
            startActivity(intent)
        }
    }
}