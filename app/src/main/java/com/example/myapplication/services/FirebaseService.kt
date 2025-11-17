package com.example.myapplication.services

import com.example.myapplication.models.Livrable
import com.example.myapplication.models.User
import java.util.*

class FirebaseService {

    // Simulation de données en mémoire
    private val livrablesTest = mutableListOf<Livrable>()
    private val usersTest = mutableListOf<User>()

    init {
        // Données de test initiales
        genererDonneesTest()
    }

    // === OPERATIONS LIVRABLES (SIMULATION) ===

    fun addLivrable(livrable: Livrable, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        try {
            livrablesTest.add(livrable)
            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun getAllLivrables(onSuccess: (List<Livrable>) -> Unit, onError: (Exception) -> Unit) {
        try {
            onSuccess(livrablesTest.toList())
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun getLivrablesByDepartement(
        departement: String,
        onSuccess: (List<Livrable>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val filtered = livrablesTest.filter { it.departement == departement }
            onSuccess(filtered)
        } catch (e: Exception) {
            onError(e)
        }
    }

    // === OPERATIONS UTILISATEURS (SIMULATION) ===

    fun saveUser(user: User, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        try {
            usersTest.removeAll { it.id == user.id }
            usersTest.add(user)
            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun getUser(userId: String, onSuccess: (User?) -> Unit, onError: (Exception) -> Unit) {
        try {
            val user = usersTest.find { it.id == userId }
            onSuccess(user)
        } catch (e: Exception) {
            onError(e)
        }
    }

    // === DONNÉES DE TEST ===

    private fun genererDonneesTest() {
        // Utilisateur de test
        val userTest = User(
            id = "user1",
            nom = "Utilisateur Test",
            email = "test@example.com",
            departement = "Développement",
            role = "utilisateur"
        )
        usersTest.add(userTest)

        // Livrables de test
        livrablesTest.addAll(
            listOf(
                Livrable(
                    id = "1",
                    nom = "MyApplication v1.0",
                    description = "Application de gestion de livrables",
                    departement = "Développement",
                    dateCreation = Date(),
                    deadline = Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000),
                    statut = "a_faire",
                    priorite = "Moyenne"
                ),
                Livrable(
                    id = "2",
                    nom = "Documentation technique",
                    description = "Documentation complète du projet",
                    departement = "Marketing",
                    dateCreation = Date(),
                    deadline = Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000),
                    statut = "en_cours",
                    priorite = "Haute"
                ),
                Livrable(
                    id = "3",
                    nom = "Rapport mensuel",
                    description = "Rapport d'activité du mois",
                    departement = "Commercial",
                    dateCreation = Date(),
                    deadline = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000),
                    statut = "a_faire",
                    priorite = "Urgente"
                )
            )
        )
    }

    // === STATISTIQUES ===

    fun getStatsGlobales(onSuccess: (Map<String, Any>) -> Unit, onError: (Exception) -> Unit) {
        try {
            val totalLivrables = livrablesTest.size
            val livrablesTermines = livrablesTest.count { it.statut == "termine" }
            val livrablesEnRetard = livrablesTest.count { it.estEnRetard() }
            val tauxCompletion = if (totalLivrables > 0) {
                (livrablesTermines * 100) / totalLivrables
            } else 0

            val stats = mapOf(
                "totalLivrables" to totalLivrables,
                "livrablesTermines" to livrablesTermines,
                "livrablesEnRetard" to livrablesEnRetard,
                "tauxCompletion" to tauxCompletion
            )
            onSuccess(stats)
        } catch (e: Exception) {
            onError(e)
        }
    }
}
