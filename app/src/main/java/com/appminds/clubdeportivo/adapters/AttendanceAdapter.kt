package com.appminds.clubdeportivo.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.AttendanceCardDto
import com.appminds.clubdeportivo.models.enums.AttendanceStatus

class AttendanceAdapter(private var list: List<AttendanceCardDto>) :
    RecyclerView.Adapter<AttendanceAdapter.CardViewHolder>() {
    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        val tvActividad: TextView = itemView.findViewById(R.id.tvActividad)
        val btnAusente: AppCompatButton = itemView.findViewById(R.id.btnAusente)
        val btnPresente: AppCompatButton = itemView.findViewById(R.id.btnPresente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor_attendance, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val person = list[position]

        holder.tvNombre.text = person.fullname
        holder.tvEstado.text = person.status.label
        holder.tvActividad.text = person.activity

        val colorRes = when (person.status) {
            AttendanceStatus.PENDIENTE -> R.color.desabledTxt
            AttendanceStatus.PRESENTE -> R.color.green
            AttendanceStatus.AUSENTE -> R.color.red
        }

        val color = ContextCompat.getColor(holder.itemView.context, colorRes)
        holder.tvEstado.setTextColor(color)

        val statusSelected = person.status != AttendanceStatus.PENDIENTE
        holder.btnAusente.isEnabled = !statusSelected
        holder.btnPresente.isEnabled = !statusSelected

        @SuppressLint("NotifyDataSetChanged")
        fun updateStatus(status: AttendanceStatus, position: Int) {
            list[position].status = status
            list = list.sortedBy {
                when (it.status) {
                    AttendanceStatus.PENDIENTE -> 0
                    AttendanceStatus.PRESENTE -> 1
                    AttendanceStatus.AUSENTE -> 2
                }
            }
            notifyDataSetChanged()
        }

        holder.btnAusente.setOnClickListener { updateStatus(AttendanceStatus.AUSENTE, position) }

        holder.btnPresente.setOnClickListener { updateStatus(AttendanceStatus.PRESENTE, position) }
    }
}