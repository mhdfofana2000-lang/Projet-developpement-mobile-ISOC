package com.example.myapplication.models

import com.google.firebase.auth.FirebaseUser

data class User(
    val id: String = "",
    val email: String = "",
    val nom: String = "",
    val departement: String = "",
    val role: String = "utilisateur",
    val dateCreation: Long = System.currentTimeMillis(),
    val dernierAcces: Long = System.currentTimeMillis(),
    val estActif: Boolean = true,
    val preferences: Map<String, Any> = emptyMap(),
    val photoUrl: String? = null,
    val telephone: String = ""
) {

    // === ROLES ET PERMISSIONS ===

    companion object Roles {
        const val ROLE_DIRECTION = "direction"
        const val ROLE_CHEF_DEPARTEMENT = "chef_departement"
        const val ROLE_UTILISATEUR = "utilisateur"
        const val ROLE_ADMIN_TECHNIQUE = "admin_technique"

        // Départements supportés
        val DEPARTEMENTS = listOf(
            "Direction Générale",
            "Direction Technique",
            "Développement",
            "Marketing",
            "Design",
            "Commercial",
            "Ressources Humaines",
            "Finance",
            "Support Client"
        )

        // Départements techniques (visibles par la direction technique)
        val DEPARTEMENTS_TECHNIQUES = listOf(
            "Direction Technique",
            "Développement",
            "Design"
        )
    }

    // === VÉRIFICATIONS DE PERMISSIONS ===

    fun estDirection(): Boolean {
        return role == ROLE_DIRECTION || departement == "Direction Générale"
    }

    fun estDirectionTechnique(): Boolean {
        return role == ROLE_ADMIN_TECHNIQUE || departement == "Direction Technique"
    }

    fun estChefDepartement(): Boolean {
        return role == ROLE_CHEF_DEPARTEMENT
    }

    fun peutVoirTousDepartements(): Boolean {
        return estDirection() || estDirectionTechnique()
    }

    fun peutModifierTousLivrables(): Boolean {
        return estDirection() || estDirectionTechnique() || estChefDepartement()
    }

    fun peutSupprimerLivrables(): Boolean {
        return estDirection() || estDirectionTechnique()
    }

    fun peutGererUtilisateurs(): Boolean {
        return estDirection() || estDirectionTechnique()
    }

    // Vérifie si l'utilisateur peut voir un département spécifique
    fun peutVoirDepartement(departementCible: String): Boolean {
        return when {
            peutVoirTousDepartements() -> true
            estDirectionTechnique() -> DEPARTEMENTS_TECHNIQUES.contains(departementCible) || departementCible == departement
            else -> departementCible == departement
        }
    }

    // Vérifie si l'utilisateur peut modifier un livrable
    fun peutModifierLivrable(livrable: Livrable): Boolean {
        return when {
            peutModifierTousLivrables() -> true
            livrable.createdBy == id -> true // Créateur du livrable
            livrable.departement == departement -> true // Même département
            else -> false
        }
    }

    // === GESTION DES DÉPARTEMENTS ===

    fun getDepartementsAccessibles(): List<String> {
        return when {
            estDirection() -> DEPARTEMENTS
            estDirectionTechnique() -> DEPARTEMENTS_TECHNIQUES + departement
            else -> listOf(departement)
        }.distinct()
    }

    fun estDansDepartementTechnique(): Boolean {
        return DEPARTEMENTS_TECHNIQUES.contains(departement)
    }

    // === CONVERSION FIREBASE/FIRESTORE ===

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "email" to email,
            "nom" to nom,
            "departement" to departement,
            "role" to role,
            "dateCreation" to dateCreation,
            "dernierAcces" to dernierAcces,
            "estActif" to estActif,
            "preferences" to preferences,
            "photoUrl" to (photoUrl ?: ""),
            "telephone" to telephone
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): User {
            return User(
                id = map["id"] as? String ?: "",
                email = map["email"] as? String ?: "",
                nom = map["nom"] as? String ?: "",
                departement = map["departement"] as? String ?: "",
                role = map["role"] as? String ?: "utilisateur",
                dateCreation = (map["dateCreation"] as? Long) ?: System.currentTimeMillis(),
                dernierAcces = (map["dernierAcces"] as? Long) ?: System.currentTimeMillis(),
                estActif = map["estActif"] as? Boolean ?: true,
                preferences = map["preferences"] as? Map<String, Any> ?: emptyMap(),
                photoUrl = map["photoUrl"] as? String,
                telephone = map["telephone"] as? String ?: ""
            )
        }

        // Création d'un utilisateur à partir de FirebaseUser
        fun fromFirebaseUser(firebaseUser: FirebaseUser, departement: String = ""): User {
            return User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                nom = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "Utilisateur",
                departement = departement,
                role = if (departement.contains("Direction")) ROLE_DIRECTION else ROLE_UTILISATEUR,
                photoUrl = firebaseUser.photoUrl?.toString(),
                dernierAcces = System.currentTimeMillis()
            )
        }
    }

    // === MÉTHODES DE MISE À JOUR ===

    fun mettreAJourAcces(): User {
        return this.copy(dernierAcces = System.currentTimeMillis())
    }

    fun changerDepartement(nouveauDepartement: String, nouveauRole: String = this.role): User {
        return this.copy(
            departement = nouveauDepartement,
            role = nouveauRole
        )
    }

    fun promouvoirChefDepartement(): User {
        return this.copy(role = ROLE_CHEF_DEPARTEMENT)
    }

    fun promouvoirDirectionTechnique(): User {
        return this.copy(
            role = ROLE_ADMIN_TECHNIQUE,
            departement = "Direction Technique"
        )
    }

    fun ajouterPreference(cle: String, valeur: Any): User {
        val nouvellesPreferences = preferences.toMutableMap()
        nouvellesPreferences[cle] = valeur
        return this.copy(preferences = nouvellesPreferences)
    }

    // === VÉRIFICATIONS MÉTIER ===

    fun peutCreerLivrablePourDepartement(departementCible: String): Boolean {
        return when {
            estDirection() -> true
            estDirectionTechnique() -> DEPARTEMENTS_TECHNIQUES.contains(departementCible)
            estChefDepartement() -> departementCible == departement
            else -> departementCible == departement
        }
    }

    fun peutAttribuerLivrable(departementCible: String): Boolean {
        return peutCreerLivrablePourDepartement(departementCible)
    }

    // === INFORMATIONS D'AFFICHAGE ===

    fun getNomAffichage(): String {
        return if (nom.isNotBlank()) nom else email.substringBefore("@")
    }

    fun getRoleAffichage(): String {
        return when (role) {
            ROLE_DIRECTION -> "👑 Direction"
            ROLE_ADMIN_TECHNIQUE -> "⚙️ Direction Technique"
            ROLE_CHEF_DEPARTEMENT -> "🎯 Chef de Département"
            else -> "👤 Utilisateur"
        }
    }

    fun getDepartementAffichage(): String {
        return when (departement) {
            "Direction Générale" -> "👑 Direction Générale"
            "Direction Technique" -> "⚙️ Direction Technique"
            "Développement" -> "💻 Développement"
            "Marketing" -> "📢 Marketing"
            "Design" -> "🎨 Design"
            "Commercial" -> "💰 Commercial"
            "Ressources Humaines" -> "👥 RH"
            "Finance" -> "📊 Finance"
            "Support Client" -> "🤝 Support Client"
            else -> departement
        }
    }

    fun estNouvelUtilisateur(): Boolean {
        val uneSemaineMs = 7 * 24 * 60 * 60 * 1000L
        return (System.currentTimeMillis() - dateCreation) < uneSemaineMs
    }

    // === DONNÉES DE TEST ===

    companion object TestData {
        fun genererUtilisateursTest(): List<User> {
            return listOf(
                User(
                    id = "user_dir_001",
                    email = "directeur@myapp.com",
                    nom = "Marie Dubois",
                    departement = "Direction Générale",
                    role = ROLE_DIRECTION
                ),
                User(
                    id = "user_tech_001",
                    email = "tech@myapp.com",
                    nom = "Pierre Martin",
                    departement = "Direction Technique",
                    role = ROLE_ADMIN_TECHNIQUE
                ),
                User(
                    id = "user_dev_001",
                    email = "dev@myapp.com",
                    nom = "Jean Dev",
                    departement = "Développement",
                    role = ROLE_CHEF_DEPARTEMENT
                ),
                User(
                    id = "user_mkt_001",
                    email = "marketing@myapp.com",
                    nom = "Sophie Marketing",
                    departement = "Marketing",
                    role = ROLE_UTILISATEUR
                ),
                User(
                    id = "user_des_001",
                    email = "design@myapp.com",
                    nom = "Luc Design",
                    departement = "Design",
                    role = ROLE_UTILISATEUR
                )
            )
        }

        fun getUtilisateurTestParDefaut(): User {
            return User(
                id = "test_user_001",
                email = "test@myapp.com",
                nom = "Utilisateur Test",
                departement = "Développement",
                role = ROLE_UTILISATEUR
            )
        }
    }

    override fun toString(): String {
        return "User(nom='$nom', email='$email', departement='$departement', role=$role)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (email != other.email) return false
        if (departement != other.departement) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + departement.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }
}