package com.appminds.clubdeportivo.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.ProfesorDto
import com.appminds.clubdeportivo.profesor.DetailProfesorActivity

class PlantelAdapter(private var list: List<ProfesorDto>) :
    RecyclerView.Adapter<PlantelAdapter.CardViewHolder>() {
    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvActividad: TextView = itemView.findViewById(R.id.tvActividad)
        val btnDetalles: ImageButton = itemView.findViewById(R.id.btnDetalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plantel, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val person = list[position]

        "${person.firstname} ${person.lastname}".also { holder.tvNombre.text = it }
        holder.tvActividad.text = person.activity

        holder.btnDetalles.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailProfesorActivity::class.java).apply {
                putExtra("profesor", person)
            }

            holder.itemView.context.startActivity(intent)
        }
    }
}