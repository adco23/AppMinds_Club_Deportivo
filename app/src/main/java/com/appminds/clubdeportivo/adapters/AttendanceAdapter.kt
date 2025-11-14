package com.appminds.clubdeportivo.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.AttendanceCardDto
import com.appminds.clubdeportivo.models.enums.AttendanceStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AttendanceAdapter(
    private val onSetPresent: suspend (AttendanceCardDto) -> Result<Unit>,
    private val onSetAbsent: suspend (AttendanceCardDto) -> Result<Unit>,
    private val externalScope: CoroutineScope
) : RecyclerView.Adapter<AttendanceAdapter.CardViewHolder>() {

    private var list: List<AttendanceCardDto> = emptyList()

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        private val tvActividad: TextView = itemView.findViewById(R.id.tvActividad)
        private val btnAusente: AppCompatButton = itemView.findViewById(R.id.btnAusente)
        private val btnPresente: AppCompatButton = itemView.findViewById(R.id.btnPresente)

        fun bind(item: AttendanceCardDto) {
            tvNombre.text = item.fullname
            tvEstado.text = item.status.label
            tvActividad.text = item.activity

            updateStatusColor(item.status)
            updateButtonStates(item.status)

            btnPresente.setOnClickListener {
                handleStatusChange(item, AttendanceStatus.PRESENTE, onSetPresent)
            }

            btnAusente.setOnClickListener {
                handleStatusChange(item, AttendanceStatus.AUSENTE, onSetAbsent)
            }
        }

        private fun updateStatusColor(status: AttendanceStatus) {
            val colorRes = when (status) {
                AttendanceStatus.PENDIENTE -> R.color.desabledTxt
                AttendanceStatus.PRESENTE -> R.color.green
                AttendanceStatus.AUSENTE -> R.color.red
            }
            tvEstado.setTextColor(ContextCompat.getColor(itemView.context, colorRes))
        }

        private fun updateButtonStates(status: AttendanceStatus) {
            val isEnabled = status == AttendanceStatus.PENDIENTE
            btnAusente.isEnabled = isEnabled
            btnPresente.isEnabled = isEnabled
        }

        private fun handleStatusChange(
            item: AttendanceCardDto,
            newStatus: AttendanceStatus,
            operation: suspend (AttendanceCardDto) -> Result<Unit>
        ) {
            // Deshabilitar botones inmediatamente para evitar clicks múltiples
            btnPresente.isEnabled = false
            btnAusente.isEnabled = false

            externalScope.launch {
                try {
                    val result = operation(item)

                    if (result.isSuccess) {
                        // Actualizar el item original
                        item.status = newStatus

                        withContext(Dispatchers.Main) {
                            notifyItemChanged(bindingAdapterPosition)
                        }
                    } else {
                        // En caso de error, restaurar el estado de los botones
                        withContext(Dispatchers.Main) {
                            updateButtonStates(item.status)
                            // Aquí podrías mostrar un Toast o Snackbar con el error
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        updateButtonStates(item.status)
                        // Manejar el error mostrándolo al usuario
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor_attendance, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun updateList(newList: List<AttendanceCardDto>) {
        val diffCallback = AttendanceDiffCallback(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        list = newList
        diffResult.dispatchUpdatesTo(this)
    }

    private class AttendanceDiffCallback(
        private val oldList: List<AttendanceCardDto>,
        private val newList: List<AttendanceCardDto>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}