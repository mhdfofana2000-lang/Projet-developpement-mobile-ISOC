package com.example.myapplication.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.LivrableAdapter
import com.example.myapplication.models.Livrable
import com.example.myapplication.services.FirebaseService
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var departmentSpinner: Spinner
    private lateinit var fab: FloatingActionButton
    private lateinit var emptyState: LinearLayout

    private val firebaseService = FirebaseService()
    private lateinit var livrableAdapter: LivrableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initViews()
        setupRecyclerView()
        loadData()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        departmentSpinner = findViewById(R.id.departmentSpinner)
        fab = findViewById(R.id.fab)
        emptyState = findViewById(R.id.emptyState)

        setupSpinner()
        setupFab()
    }

    private fun setupSpinner() {
        val departments = arrayOf("Tous", "Développement", "Marketing", "Design", "Commercial")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departments)
        departmentSpinner.adapter = adapter
    }

    private fun setupRecyclerView() {
        livrableAdapter = LivrableAdapter { livrable ->
            showToast("Sélectionné: ${livrable.nom}")
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = livrableAdapter
    }

    private fun loadData() {
        progressBar.visibility = ProgressBar.VISIBLE

        firebaseService.getLivrables(
            onSuccess = { livrables ->
                progressBar.visibility = ProgressBar.GONE
                livrableAdapter.submitList(livrables)
                updateEmptyState(livrables.isEmpty())
            },
            onError = { error ->
                progressBar.visibility = ProgressBar.GONE
                showToast("Erreur: ${error.message}")
                // Données de démo
                loadDemoData()
            }
        )
    }

    private fun loadDemoData() {
        val demoLivrables = listOf(
            Livrable(
                nom = "MyApplication v1.0",
                departement = "Développement",
                deadline = java.util.Date()
            ),
            Livrable(
                nom = "Documentation technique",
                departement = "Marketing",
                deadline = java.util.Date()
            )
        )
        livrableAdapter.submitList(demoLivrables)
        updateEmptyState(false)
    }

    private fun setupFab() {
        fab.setOnClickListener {
            showToast("Ajouter un livrable")
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        emptyState.visibility = if (isEmpty) LinearLayout.VISIBLE else LinearLayout.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}