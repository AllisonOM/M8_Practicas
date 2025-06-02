package com.example.apppractica2.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apppractica2.data.remote.model.AutoDto
import com.example.apppractica2.databinding.AutoElementBinding
import com.squareup.picasso.Picasso

class AutoViewHolder (
    private val binding: AutoElementBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(auto: AutoDto){

        // Se vinculan las vistas con la información correspondiente
        binding.tvName.text = auto.name
        binding.tvBrand.text = auto.brand
        binding.tvModel.text = auto.model

        // Con picasso
//        Picasso.get()
//            .load(auto.image)
//            .into(binding.ivThumbnail)

        // Con Glide
        Glide.with(binding.root.context)
            .load(auto.image)
            .into(binding.ivThumbnail)

    }

}