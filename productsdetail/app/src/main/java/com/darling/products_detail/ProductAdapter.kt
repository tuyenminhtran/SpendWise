package com.darling.products_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val list: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgProduct)
        val name: TextView = view.findViewById(R.id.txtName)
        val price: TextView = view.findViewById(R.id.txtPrice)
        val btnBuy: Button = view.findViewById(R.id.btnBuy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]

        holder.img.setImageResource(product.imageRes)
        holder.name.text = product.name
        holder.price.text = product.price

        holder.btnBuy.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Đã mua: ${product.name}",
                Toast.LENGTH_SHORT
            ).show()
        }

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
    }
}