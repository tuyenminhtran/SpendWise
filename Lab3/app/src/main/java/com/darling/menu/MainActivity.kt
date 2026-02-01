package com.darling.menu

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val listView: ListView = findViewById(R.id.listView)

        val foods = listOf(
            Food("Bún thị nướng", R.drawable.ic_bunthitnuong),
            Food("Bò nướng lá lốt", R.drawable.ic_bonuonglalot),
            Food("Cơm tấm", R.drawable.ic_comtam),
            Food("Hủ tiếu", R.drawable.ic_hutieu),
            Food("Bún bò", R.drawable.ic_bunbo),
            Food("Bún đậu", R.drawable.ic_bundau),
            Food("Bánh canh", R.drawable.ic_banhcanh),
            Food("Cá lóc nướng trui", R.drawable.ic_calocnuongtrui),
            Food("Cá kho tộ", R.drawable.ic_cakhoto),
            Food("Gà nướng", R.drawable.ic_ganuong),
            Food("Bánh xèo", R.drawable.ic_banhxeo),
            Food("Bánh khọt", R.drawable.ic_banhkhot),
            Food("Bánh đúc lá dứa", R.drawable.ic_banhduc),
            Food("Chè thốt nốt", R.drawable.ic_chethotnot),
            Food("Chuối nếp nướng", R.drawable.ic_chuoinepnuong),
            Food("Xôi lá cẩm", R.drawable.ic_xoilacam),
        )

        val adapter = FoodAdapter(this, foods)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _,->
            val food = foods[position]
            Toast.makeText(this, "Bạn đã chọn: $food",
                Toast.LENGTH_SHORT).show()
        }
    }
}