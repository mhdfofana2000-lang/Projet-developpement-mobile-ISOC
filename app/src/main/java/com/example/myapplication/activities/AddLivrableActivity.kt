package com.example.myapplication.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.models.Livrable
import com.example.myapplication.services.FirebaseService
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class AddLivrableActivity : AppCompatActivity() {

    private lateinit var nomEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var departementSpinner: Spinner
    private lateinit var prioriteSpinner: Spinner
    private lateinit var dateButton: Button
    private lateinit var sauvegarderButton: MaterialButton
    private lateinit var scannerButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val firebaseService = FirebaseService()
    private var selectedDate: Date = Date()
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_livrable)

        initViews()
        setupSpinners()
        setupDatePicker()
        setupButtons()
    }

    private fun initViews() {
        nomEditText = findViewById(R.id.nomEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        departementSpinner = findViewById(R.id.departementSpinner)
        prioriteSpinner = findViewById(R.id.prioriteSpinner)
        dateButton = findViewById(R.id.dateButton)
        sauvegarderButton = findViewById(R.id.sauvegarderButton)
        scannerButton = findViewById(R.id.scannerButton)
        progressBar = findViewById(R.id.progressBar)

        // Définir la date par défaut (7 jours à partir d'aujourd'hui)
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        selectedDate = calendar.time
        updateDateButton()
    }

    private fun setupSpinners() {
        // Spinner Département
        val departements = arrayOf(
            "Développement",
            "Marketing",
            "Design",
            "Commercial",
            "Direction",
            "Ressources Humaines"
        )
        val departementAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departements)
        departementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        departementSpinner.adapter = departementAdapter

        // Spinner Priorité
        val priorities = arrayOf("Basse", "Moyenne", "Haute", "Urgente")
        val prioriteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        prioriteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioriteSpinner.adapter = prioriteAdapter

        // Sélectionner une valeur par défaut
        prioriteSpinner.setSelection(1) // Moyenne
    }

    private fun setupDatePicker() {
        dateButton.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.time
                updateDateButton()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Définir la date minimale (aujourd'hui)
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private fun updateDateButton() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateButton.text = "📅 ${dateFormat.format(selectedDate)}"
    }

    private fun setupButtons() {
        sauvegarderButton.setOnClickListener {
            sauvegarderLivrable()
        }

        scannerButton.setOnClickListener {
            // TODO: Ouvrir l'activité de scan
            showToast("Fonction scan à implémenter")
        }
    }

    private fun sauvegarderLivrable() {
        val nom = nomEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val departement = departementSpinner.selectedItem as String
        val priorite = prioriteSpinner.selectedItem as String

        // Validation
        if (nom.isEmpty()) {
            nomEditText.error = "Le nom est obligatoire"
            return
        }

        if (description.isEmpty()) {
            descriptionEditText.error = "La description est obligatoire"
            return
        }

        // Créer le livrable
        val livrable = Livrable(
            nom = nom,
            description = description,
            departement = departement,
            deadline = selectedDate,
            priorite = priorite.toLowerCase(Locale.getDefault()),
            statut = "a_faire"
        )

        // Sauvegarder dans Firebase
        progressBar.visibility = ProgressBar.VISIBLE
        sauvegarderButton.isEnabled = false

        firebaseService.addLivrable(
            livrable = livrable,
            onSuccess = {
                progressBar.visibility = ProgressBar.GONE
                sauvegarderButton.isEnabled = true
                showSuccessMessage()
            },
            onError = { error ->
                progressBar.visibility = ProgressBar.GONE
                sauvegarderButton.isEnabled = true
                showToast("Erreur: ${error.message}")
            }
        )
    }

    private fun showSuccessMessage() {
        AlertDialog.Builder(this)
            .setTitle("✅ Succès")
            .setMessage("Le livrable a été ajouté avec succès !")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish() // Retour au dashboard
            }
            .setCancelable(false)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Gestion des boutons de date rapide
    fun onQuickDateClick(view: android.view.View) {
        when (view.id) {
            R.id.btnDemain -> addDaysToDate(1)
            R.id.btnSemaine -> addDaysToDate(7)
            R.id.btnMois -> addDaysToDate(30)
        }
    }

    private fun addDaysToDate(days: Int) {
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, days)
        selectedDate = calendar.time
        updateDateButton()
    }
}