package com.darling.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class FoodAdapter(private val context: Context, private val foods: List<Food>): BaseAdapter() {
    override fun getCount(): Int = foods.size
    override fun getItem(position: Int): Any = foods[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView: View = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_food,
            parent, false)
        val food = foods[position]

        val imageFood = rowView.findViewById<ImageView>(R.id.imageFood)
        val txtFoodName = rowView.findViewById<TextView>(R.id.txtFoodName)

        imageFood.setImageResource(food.imageRes)
        txtFoodName.text = food.name
        return rowView
    }

}