package com.example.myapplication.utils

object Constants {

    // === APPLICATION ===
    const val APP_NAME = "MyApplication"
    const val APP_VERSION = "1.0.0"
    const val APP_BUILD = 1

    // === FIREBASE COLLECTIONS ===
    object Firestore {
        const val COLLECTION_LIVRABLES = "livrables"
        const val COLLECTION_USERS = "users"
        const val COLLECTION_NOTIFICATIONS = "notifications"
        const val COLLECTION_DEPARTEMENTS = "departements"
        const val COLLECTION_STATS = "statistiques"
        const val COLLECTION_LOGS = "logs_application"

        // Sous-collections
        const val SUBCOLLECTION_HISTORIQUE = "historique"
        const val SUBCOLLECTION_COMMENTAIRES = "commentaires"
    }

    // === FIREBASE STORAGE ===
    object Storage {
        const val FOLDER_SCANS = "scans"
        const val FOLDER_AVATARS = "avatars"
        const val FOLDER_DOCUMENTS = "documents"
        const val FOLDER_TEMP = "temp"

        const val MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB
        const val IMAGE_QUALITY = 80
    }

    // === ROLES UTILISATEURS ===
    object Roles {
        const val ROLE_DIRECTION = "direction"
        const val ROLE_ADMIN_TECHNIQUE = "admin_technique"
        const val ROLE_CHEF_DEPARTEMENT = "chef_departement"
        const val ROLE_UTILISATEUR = "utilisateur"
        const val ROLE_VISIONNEUR = "visionneur"

        val ALL_ROLES = listOf(
            ROLE_DIRECTION,
            ROLE_ADMIN_TECHNIQUE,
            ROLE_CHEF_DEPARTEMENT,
            ROLE_UTILISATEUR,
            ROLE_VISIONNEUR
        )
    }

    // === DÉPARTEMENTS ===
    object Departements {
        const val DIRECTION_GENERALE = "Direction Générale"
        const val DIRECTION_TECHNIQUE = "Direction Technique"
        const val DEVELOPPEMENT = "Développement"
        const val MARKETING = "Marketing"
        const val DESIGN = "Design"
        const val COMMERCIAL = "Commercial"
        const val RESSOURCES_HUMAINES = "Ressources Humaines"
        const val FINANCE = "Finance"
        const val SUPPORT_CLIENT = "Support Client"
        const val QUALITE = "Qualité"

        val ALL_DEPARTEMENTS = listOf(
            DIRECTION_GENERALE,
            DIRECTION_TECHNIQUE,
            DEVELOPPEMENT,
            MARKETING,
            DESIGN,
            COMMERCIAL,
            RESSOURCES_HUMAINES,
            FINANCE,
            SUPPORT_CLIENT,
            QUALITE
        )
    }

    // === STATUTS LIVRABLES ===
    object StatutsLivrable {
        const val BROUILLON = "brouillon"
        const val EN_COURS = "en_cours"
        const val EN_REVISION = "en_revision"
        const val VALIDE = "valide"
        const val REJETE = "rejete"
        const val TERMINE = "termine"

        val ALL_STATUTS = listOf(
            BROUILLON,
            EN_COURS,
            EN_REVISION,
            VALIDE,
            REJETE,
            TERMINE
        )
    }

    // === TYPES DE FICHIERS AUTORISÉS ===
    object FileTypes {
        const val PDF = "application/pdf"
        const val JPEG = "image/jpeg"
        const val PNG = "image/png"
        const val DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

        val ALLOWED_TYPES = listOf(PDF, JPEG, PNG, DOCX)
        val ALLOWED_EXTENSIONS = listOf("pdf", "jpg", "jpeg", "png", "docx")
    }

    // === CONFIGURATION NOTIFICATIONS ===
    object Notifications {
        const val CHANNEL_ID = "livrables_channel"
        const val CHANNEL_NAME = "Notifications Livrables"
        const val CHANNEL_DESCRIPTION = "Notifications pour les mises à jour des livrables"

        // Types de notifications
        const val TYPE_NOUVEAU_LIVRABLE = "nouveau_livrable"
        const val TYPE_MODIFICATION = "modification"
        const val TYPE_VALIDATION = "validation"
        const val TYPE_COMMENTAIRE = "commentaire"
    }

    // === PREFERENCES PARTAGEES ===
    object SharedPrefs {
        const val PREFS_NAME = "myapp_preferences"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_ROLE = "user_role"
        const val KEY_USER_DEPARTEMENT = "user_departement"
        const val KEY_FIRST_LAUNCH = "first_launch"
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    // === CODES REQUÊTES ===
    object RequestCodes {
        const val PICK_IMAGE = 1001
        const val CAPTURE_IMAGE = 1002
        const val PICK_DOCUMENT = 1003
        const val SCAN_DOCUMENT = 1004
    }

    // === MESSAGES D'ERREUR ===
    object ErrorMessages {
        const val GENERIC_ERROR = "Une erreur s'est produite"
        const val NETWORK_ERROR = "Problème de connexion"
        const val UPLOAD_ERROR = "Erreur lors du téléchargement"
        const val AUTH_ERROR = "Erreur d'authentification"
        const val PERMISSION_DENIED = "Permission refusée"
    }
}