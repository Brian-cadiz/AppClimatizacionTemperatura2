package com.example.appclimatizaciontemperatura

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LecturaAdapter(private val listaLecturas: List<Lectura>) :
    RecyclerView.Adapter<LecturaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNodo: TextView = view.findViewById(R.id.tvItemNodo)
        val tvValores: TextView = view.findViewById(R.id.tvItemValores)
        val tvEstado: TextView = view.findViewById(R.id.tvItemEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lectura, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaLecturas[position]
        val context = holder.itemView.context

        holder.tvNodo.text = "Nodo: ${item.nodo_id}"
        holder.tvValores.text = "Temp: ${item.temperatura} °C | Humedad: ${item.humedad.toInt()}%"

        if (item.temperatura <= 2.0) {
            holder.tvEstado.text = "¡RIESGO DE HELADA! (Relé ${if (item.rele_activo) "ON" else "OFF"})"
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        } else {
            holder.tvEstado.text = "Estado Normal"
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
        }
    }

    override fun getItemCount() = listaLecturas.size
}