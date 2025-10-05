package com.appminds.clubdeportivo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.ActividadDto

class ActividadAdminAdapter(
    private val list: MutableList<ActividadDto>,
    private val onEdit: (ActividadDto) -> Unit,
    private val onDelete: (ActividadDto) -> Unit
) : RecyclerView.Adapter<ActividadAdminAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val btnEditar: AppCompatButton = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: AppCompatButton = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_actividad_admin, parent, false)
        return CardViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val act = list[position]
        holder.tvNombre.text = act.name
        // si querés, mapear ícono por nombre:
        // holder.imgIcon.setImageResource(getIconFor(act.name))

        holder.btnEditar.setOnClickListener { onEdit(act) }
        holder.btnEliminar.setOnClickListener { onDelete(act) }
    }
}
