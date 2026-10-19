package com.example.appclimatizaciontemperatura

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddDeviceActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var etNombreNodo: EditText
    private lateinit var etMacNodo: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        db = FirebaseFirestore.getInstance()

        etNombreNodo = findViewById(R.id.etNombreNodo)
        etMacNodo = findViewById(R.id.etMacNodo)

        val btnGuardarNodo: Button = findViewById(R.id.btnGuardarNodo)
        val btnVolver: Button = findViewById(R.id.btnVolver)

        btnGuardarNodo.setOnClickListener {
            guardarDispositivo()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun guardarDispositivo() {
        val nombre = etNombreNodo.text.toString().trim()
        val mac = etMacNodo.text.toString().trim()

        if (nombre.isEmpty() || mac.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("dispositivos").get()
            .addOnSuccessListener { result ->
                val totalNodos = result.size()
                val rolAsignado = if (totalNodos == 0) "EMISOR" else "RECEPTOR"
                val nodoAnterior = if (totalNodos == 0) null else result.documents.last().getString("mac")

                val nuevoNodo = hashMapOf(
                    "nombre" to nombre,
                    "mac" to mac,
                    "rol" to rolAsignado,
                    "nodo_anterior" to nodoAnterior,
                    "fecha_registro" to FieldValue.serverTimestamp()
                )

                db.collection("dispositivos").add(nuevoNodo)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nodo ($rolAsignado) registrado con éxito", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}