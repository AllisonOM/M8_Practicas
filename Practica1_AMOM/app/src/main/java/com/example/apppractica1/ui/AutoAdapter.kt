package com.example.apppractica1.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppractica1.data.db.model.AutoEntity
import com.example.apppractica1.databinding.AutoElementBinding

class AutoAdapter(

    private var onAutoClick: (AutoEntity) -> Unit

): RecyclerView.Adapter<AutoViewHolder>() {

    private var autos: List<AutoEntity> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoViewHolder {
        val binding = AutoElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AutoViewHolder(binding)
    }

    override fun getItemCount(): Int = autos.size

    override fun onBindViewHolder(holder: AutoViewHolder, position: Int) {
        val auto = autos[position]

        holder.bind(auto)

        holder.itemView.setOnClickListener {
            // El click para cada elemento
            onAutoClick(auto)
        }
    }

    // Actualización del adapter para los nuevos elementos actualizados
    fun updateList(list: MutableList<AutoEntity>){
        autos = list
        notifyDataSetChanged()
    }
}