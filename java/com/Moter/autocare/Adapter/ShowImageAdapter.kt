package com.Moter.autocare.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.Moter.autocare.Model.ImageModel
import com.Moter.autocare.databinding.ImageLayoutBinding

open class ShowImageAdapter(val context : Context, options: FirebaseRecyclerOptions<ImageModel>) : FirebaseRecyclerAdapter<ImageModel, ShowImageAdapter.onViewHolder>(
    options
) {

    class onViewHolder(val binding : ImageLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): onViewHolder {
        val view = ImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return onViewHolder(view)
    }

    override fun onBindViewHolder(holder: onViewHolder, position: Int, model: ImageModel) {
        Glide.with(context).load(model.Url).into(holder.binding.image)
    }
}