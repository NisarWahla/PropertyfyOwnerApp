package com.dzinemedia.ownerpropertyfyapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dzinemedia.ownerpropertyfyapp.R

class ImagePagerHorizontalAdapter(private val context: Context,  val imagesList: ArrayList<Any>) :
    RecyclerView.Adapter<ImagePagerHorizontalAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_slider_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imagesList[position])
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(imageResId: Any) {
            Glide.with(imageView.context).load(imageResId).into(imageView)
        }
    }
}