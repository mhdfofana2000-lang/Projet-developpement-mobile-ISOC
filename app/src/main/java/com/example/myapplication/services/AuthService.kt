package com.example.myapplication.services

import com.example.myapplication.models.User

class AuthService {

    companion object {
        const val COLLECTION_USERS = "users"
    }

    // === CONNEXION ===
    fun connecterAvecEmail(
        email: String,
        motDePasse: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        // Mock pour test sans Firebase
        if (email.isNotEmpty() && motDePasse.isNotEmpty()) {
            val mockUser = User(
                id = "test_id",
                nom = "Utilisateur Test",
                email = email,
                departement = "Département Test"
            )
            onSuccess(mockUser)
        } else {
            onError("Email ou mot de passe vide")
        }
    }

    // === INSCRIPTION ===
    fun inscrireUtilisateur(
        email: String,
        motDePasse: String,
        nom: String,
        departement: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        // Mock pour test sans Firebase
        if (email.isNotEmpty() && motDePasse.isNotEmpty()) {
            val mockUser = User(
                id = "test_id_${System.currentTimeMillis()}",
                nom = nom,
                email = email,
                departement = departement
            )
            onSuccess(mockUser)
        } else {
            onError("Email ou mot de passe vide")
        }
    }

    // === DÉCONNEXION ===
    fun deconnecter() {
        // Rien à faire pour le mock
    }

    // === VÉRIFICATIONS ===
    fun estConnecte(): Boolean {
        // Toujours connecté dans le mock
        return true
    }

    fun getUtilisateurActuel(): User {
        return User(
            id = "test_id",
            nom = "Utilisateur Test",
            email = "test@example.com",
            departement = "Département Test"
        )
    }

    fun getUserId(): String {
        return "test_id"
    }

    // === RÉINITIALISATION MOT DE PASSE ===
    fun reinitialiserMotDePasse(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isNotEmpty()) onSuccess()
        else onError("Email vide")
    }
}
