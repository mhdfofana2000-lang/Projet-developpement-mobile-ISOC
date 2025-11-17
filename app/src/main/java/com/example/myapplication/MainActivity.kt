package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Version ULTRA SIMPLE
        val textView = TextView(this).apply {
            text = "Bienvenue dans MyApplication - Samsung A05S!\n\nApplication fonctionnelle!"
            textSize = 18f
            setPadding(50, 50, 50, 50)
        }
        setContentView(textView)

        Toast.makeText(this, "Application démarrée avec succès!", Toast.LENGTH_LONG).show()
    }
}