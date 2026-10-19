package com.example.appclimatizaciontemperatura

data class Lectura(
    val nodo_id: String = "",
    val temperatura: Double = 0.0,
    val humedad: Double = 0.0,
    val rele_activo: Boolean = false,
    val fecha_hora: Any? = null
)
