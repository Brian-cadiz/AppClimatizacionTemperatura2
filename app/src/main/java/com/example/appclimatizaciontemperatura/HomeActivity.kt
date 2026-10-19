package com.example.appclimatizaciontemperatura

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvTemperatura: TextView
    private lateinit var tvHumedad: TextView
    private lateinit var tvAlerta: TextView
    private lateinit var tvRele: TextView
    private lateinit var tvNodoActivo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = Firebase.auth
        db = Firebase.firestore

        tvTemperatura = findViewById(R.id.tvTemperatura)
        tvHumedad = findViewById(R.id.tvHumedad)
        tvAlerta = findViewById(R.id.tvAlerta)
        tvRele = findViewById(R.id.tvRele)
        tvNodoActivo = findViewById(R.id.tvNodoActivo)

        val btnAgregarDispositivo: Button = findViewById(R.id.btnAgregarDispositivo)
        val btnVerHistorial: Button = findViewById(R.id.btnVerHistorial)
        val btnCerrarSesion: Button = findViewById(R.id.btnCerrarSesion)

        // Escuchar datos en tiempo real desde Firestore
        escucharUltimaLectura()

        // --- NAVEGACIÓN A AGREGAR DISPOSITIVO ---
        btnAgregarDispositivo.setOnClickListener {
            val intent = Intent(this, AddDeviceActivity::class.java)
            startActivity(intent)
        }

        // --- NAVEGACIÓN A REGISTRO HISTÓRICO ---
        btnVerHistorial.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // --- CERRAR SESIÓN ---
        btnCerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun escucharUltimaLectura() {
        db.collection("lecturas_clima")
            .orderBy("fecha_hora", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al cargar datos: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    val doc = snapshots.documents[0]
                    val temp = doc.getDouble("temperatura") ?: 0.0
                    val hum = doc.getDouble("humedad") ?: 0.0
                    val nodo = doc.getString("nodo_id") ?: "Nodo 1"
                    val releActivo = doc.getBoolean("rele_activo") ?: false

                    tvTemperatura.text = String.format("%.1f °C", temp)
                    tvHumedad.text = "Humedad: ${hum.toInt()}%"
                    tvNodoActivo.text = "Nodo Activo: $nodo"

                    if (temp <= 2.0) {
                        tvAlerta.text = "¡ALERTA: RIESGO DE HELADA!"
                        tvAlerta.setTextColor(getColor(android.R.color.white))
                        tvAlerta.setBackgroundColor(getColor(android.R.color.holo_red_dark))
                    } else {
                        tvAlerta.text = "Estado: TEMP. OK"
                        tvAlerta.setTextColor(getColor(android.R.color.black))
                        tvAlerta.setBackgroundColor(getColor(android.R.color.holo_green_light))
                    }

                    tvRele.text = if (releActivo) "Relé: ACTIVADO (Mitigación)" else "Relé: INACTIVO"
                } else {
                    tvTemperatura.text = "-- °C"
                    tvHumedad.text = "Sin lecturas aún"
                }
            }
    }
}