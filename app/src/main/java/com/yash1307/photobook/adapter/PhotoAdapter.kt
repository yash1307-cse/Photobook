package com.yash1307.photobook.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.yash1307.photobook.R
import com.yash1307.photobook.models.Posts

class PhotoAdapter(val context: Context, val postsList: ArrayList<Posts>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = postsList[position]
        holder.descText.text = currentItem.description
        val setImageUri = Uri.parse(currentItem.imageUri)
       /* holder.postImageView.load(setImageUri) {
            crossfade(true)
            crossfade(10000)
        }*/
        Glide.with(context.applicationContext).load(setImageUri).into(holder.postImageView)
        //Picasso.get().load(setImageUri).into(holder.postImageView);
        holder.dateText.text = currentItem.postDate
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.postImages)
        val dateText: TextView = itemView.findViewById(R.id.textDate)
        val descText: TextView = itemView.findViewById(R.id.textDesc)
    }
}