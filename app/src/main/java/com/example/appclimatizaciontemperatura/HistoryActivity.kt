package com.example.appclimatizaciontemperatura

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HistoryActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var rvHistorial: RecyclerView
    private val listaLecturas = mutableListOf<Lectura>()
    private lateinit var adapter: LecturaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        db = FirebaseFirestore.getInstance()
        rvHistorial = findViewById(R.id.rvHistorial)
        rvHistorial.layoutManager = LinearLayoutManager(this)

        adapter = LecturaAdapter(listaLecturas)
        rvHistorial.adapter = adapter

        val btnVolver: Button = findViewById(R.id.btnVolverHistorial)
        btnVolver.setOnClickListener { finish() }

        cargarHistorial()
    }

    private fun cargarHistorial() {
        db.collection("lecturas_clima")
            .orderBy("fecha_hora", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al cargar historial: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    listaLecturas.clear()
                    for (doc in snapshots.documents) {
                        val lectura = doc.toObject(Lectura::class.java)
                        if (lectura != null) {
                            listaLecturas.add(lectura)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}