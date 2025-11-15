package com.appminds.clubdeportivo.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.model.ActividadEntity
import java.text.Normalizer

class ActividadAdminAdapter(
    private val list: MutableList<ActividadEntity>,
    private val onEdit: (ActividadEntity) -> Unit,
    private val onDelete: (ActividadEntity) -> Unit
) : RecyclerView.Adapter<ActividadAdminAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val btnEditar: AppCompatButton = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: AppCompatButton = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad_admin, parent, false)
        return CardViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val act = list[position]
        holder.tvNombre.text = act.name
        holder.imgIcon.setImageResource(iconForActivity(act.name))

        // aplicar tinte verde
        ImageViewCompat.setImageTintList(
            holder.imgIcon,
            ColorStateList.valueOf(
                ContextCompat.getColor(holder.itemView.context, R.color.dark_green)
            )
        )

        holder.btnEditar.setOnClickListener { onEdit(act) }
        holder.btnEliminar.setOnClickListener { onDelete(act) }
    }

    private fun iconForActivity(name: String): Int {
        val normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "") // quita acentos
            .lowercase()

        return when {
            normalized.contains("yoga")        -> R.drawable.ic_yoga
            normalized.contains("funcional")   -> R.drawable.ic_funcional
            normalized.contains("spinning")    -> R.drawable.ic_bike
            normalized.contains("futbol")      -> R.drawable.ic_soccer
            normalized.contains("hockey")      -> R.drawable.ic_hockey
            normalized.contains("musculacion") -> R.drawable.ic_muscualcion
            else -> R.drawable.ic_user
        }
    }
}
