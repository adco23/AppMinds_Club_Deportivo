package com.appminds.clubdeportivo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.OverdueClientDto
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

class OverdueClientAdapter(private var list: List<OverdueClientDto>):
    RecyclerView.Adapter<OverdueClientAdapter.CardViewHolder>() {
    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClientName: TextView = itemView.findViewById(R.id.tvClientName)
        val tvClientStatus: TextView = itemView.findViewById(R.id.tvClientStatus)
        val tvClientDNI: TextView = itemView.findViewById(R.id.tvClientDNI)
        val tvClientDue: TextView = itemView.findViewById(R.id.tvClientDue)

        val btnRegisterPayment: androidx.appcompat.widget.AppCompatButton = itemView.findViewById(R.id.btnRegisterPayment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_overdue_client, parent, false)
        return CardViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: CardViewHolder,
        position: Int
    ) {
        val client = list[position]

        val color = ContextCompat.getColor(holder.itemView.context, R.color.red)
        holder.tvClientStatus.setTextColor(color)

        holder.tvClientName.text = client.name
        holder.tvClientStatus.text = client.status
        holder.tvClientDNI.text = client.dni

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(client.due)
        holder.tvClientDue.isAllCaps = true
        holder.tvClientDue.text = outputFormat.format(date)

        holder.btnRegisterPayment.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Pago registrado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = list.size
}