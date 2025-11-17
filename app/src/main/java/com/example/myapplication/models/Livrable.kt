package com.example.myapplication.models

import java.text.SimpleDateFormat
import java.util.*

data class Livrable(
    val id: String = UUID.randomUUID().toString(),
    val nom: String = "",
    val description: String = "",
    val departement: String = "",
    val dateCreation: Date = Date(),
    val deadline: Date = Date(),
    val statut: String = "a_faire",
    val priorite: String = "moyenne",
    val createdBy: String = "",
    val scanUrl: String? = null,
    val joursRetard: Int = 0,
    val tags: List<String> = emptyList()
) {

    // Conversion pour Firestore
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "nom" to nom,
            "description" to description,
            "departement" to departement,
            "dateCreation" to dateCreation,
            "deadline" to deadline,
            "statut" to statut,
            "priorite" to priorite,
            "createdBy" to createdBy,
            "scanUrl" to (scanUrl ?: ""),
            "joursRetard" to joursRetard,
            "tags" to tags,
            "derniereModification" to Date()
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Livrable {
            return Livrable(
                id = map["id"] as? String ?: UUID.randomUUID().toString(),
                nom = map["nom"] as? String ?: "",
                description = map["description"] as? String ?: "",
                departement = map["departement"] as? String ?: "",
                dateCreation = (map["dateCreation"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                deadline = (map["deadline"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                statut = map["statut"] as? String ?: "a_faire",
                priorite = map["priorite"] as? String ?: "moyenne",
                createdBy = map["createdBy"] as? String ?: "",
                scanUrl = map["scanUrl"] as? String,
                joursRetard = (map["joursRetard"] as? Long)?.toInt() ?: 0,
                tags = map["tags"] as? List<String> ?: emptyList()
            )
        }

        // Créer un livrable à partir d'un scan
        fun fromScanData(
            nom: String,
            texteExtrait: String,
            departement: String = "Général",
            deadline: Date = Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)
        ): Livrable {
            return Livrable(
                nom = nom,
                description = "📸 Document scanné\n\n$texteExtrait",
                departement = departement,
                deadline = deadline,
                priorite = "moyenne",
                statut = "a_faire",
                tags = listOf("scanné", "automatique")
            )
        }
    }

    // === CALCULS ET STATUTS ===

    fun getJoursRestants(): Int {
        val now = Date()
        val difference = deadline.time - now.time
        return maxOf(0, (difference / (1000 * 60 * 60 * 24)).toInt())
    }

    fun estEnRetard(): Boolean {
        return deadline.before(Date()) && statut != "termine"
    }

    fun calculerJoursRetard(): Int {
        if (statut == "termine" || !estEnRetard()) return 0
        val now = Date()
        val difference = now.time - deadline.time
        return maxOf(0, (difference / (1000 * 60 * 60 * 24)).toInt())
    }

    // === STATUTS AVEC COULEURS ===

    fun getStatutAvecCouleur(): Pair<String, Int> {
        return when {
            estEnRetard() -> Pair("En retard 🚨", android.R.color.holo_red_light)
            statut == "termine" -> Pair("Terminé ✅", android.R.color.holo_green_light)
            getJoursRestants() <= 1 -> Pair("Urgent 🔥", android.R.color.holo_orange_dark)
            getJoursRestants() <= 3 -> Pair("Bientôt ⚠️", android.R.color.holo_orange_light)
            statut == "en_cours" -> Pair("En cours 🔄", android.R.color.holo_blue_light)
            else -> Pair("À faire 📝", android.R.color.holo_blue_light)
        }
    }

    fun getPrioriteAvecIcone(): String {
        return when (priorite) {
            "urgente" -> "🔥 Urgente"
            "haute" -> "⚡ Haute"
            "moyenne" -> "📋 Moyenne"
            "basse" -> "🌱 Basse"
            else -> "📋 Moyenne"
        }
    }

    // === INFORMATIONS FORMATÉES ===

    fun getDeadlineFormatee(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(deadline)
    }

    fun getDateCreationFormatee(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.getDefault())
        return dateFormat.format(dateCreation)
    }

    fun getDureeRestanteFormatee(): String {
        val joursRestants = getJoursRestants()
        return when {
            estEnRetard() -> {
                val retard = calculerJoursRetard()
                if (retard == 1) "En retard de 1 jour"
                else "En retard de $retard jours"
            }
            joursRestants == 0 -> "Aujourd'hui"
            joursRestants == 1 -> "Demain"
            joursRestants <= 7 -> "Dans $joursRestants jours"
            else -> {
                val semaines = joursRestants / 7
                if (semaines == 1) "Dans 1 semaine" else "Dans $semaines semaines"
            }
        }
    }

    // === VÉRIFICATIONS MÉTIER ===

    fun peutEtreModifie(): Boolean {
        return statut != "termine"
    }

    fun peutEtreSupprime(): Boolean {
        return true // Pour l'instant, tous les livrables peuvent être supprimés
    }

    fun estProcheDeadline(): Boolean {
        return getJoursRestants() <= 3 && !estEnRetard()
    }

    fun necessiteAttention(): Boolean {
        return estEnRetard() || estProcheDeadline() || priorite == "urgente"
    }

    // === MÉTHODES DE MISE À JOUR ===

    fun marquerCommeTermine(): Livrable {
        return this.copy(
            statut = "termine",
            joursRetard = 0
        )
    }

    fun marquerCommeEnCours(): Livrable {
        return this.copy(statut = "en_cours")
    }

    fun changerPriorite(nouvellePriorite: String): Livrable {
        return this.copy(priorite = nouvellePriorite)
    }

    fun mettreAJourDeadline(nouvelleDeadline: Date): Livrable {
        return this.copy(deadline = nouvelleDeadline)
    }

    fun ajouterTag(tag: String): Livrable {
        val nouveauxTags = tags.toMutableList()
        if (!nouveauxTags.contains(tag)) {
            nouveauxTags.add(tag)
        }
        return this.copy(tags = nouveauxTags)
    }

    // === CALCUL DE PROGRESSION ===

    fun getProgression(): Int {
        return when (statut) {
            "a_faire" -> 0
            "en_cours" -> 50
            "termine" -> 100
            else -> 0
        }
    }

    // === GÉNÉRATION DE DONNÉES DE TEST ===

    companion object TestData {
        fun genererDonneesTest(): List<Livrable> {
            val departements = listOf("Développement", "Marketing", "Design", "Commercial", "Direction")
            val statuts = listOf("a_faire", "en_cours", "termine")
            val priorities = listOf("basse", "moyenne", "haute", "urgente")

            return List(15) { index ->
                val joursOffset = (index - 5).toLong() // Certains en retard, certains à venir
                Livrable(
                    nom = "Livrable ${index + 1} - ${departements.random()}",
                    description = "Description détaillée du livrable ${index + 1}. Ceci est un exemple de description pour tester l'application.",
                    departement = departements.random(),
                    deadline = Date(System.currentTimeMillis() + joursOffset * 24 * 60 * 60 * 1000),
                    statut = statuts.random(),
                    priorite = priorities.random(),
                    createdBy = "user${(1..3).random()}",
                    tags = listOf("tag${(1..3).random()}", "projet")
                )
            }.sortedBy { it.deadline }
        }
    }

    override fun toString(): String {
        return "Livrable(nom='$nom', departement='$departement', deadline=${getDeadlineFormatee()}, statut=$statut, priorite=$priorite)"
    }

    // Méthode pour la comparaison dans les adapters
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Livrable

        if (id != other.id) return false
        if (nom != other.nom) return false
        if (description != other.description) return false
        if (departement != other.departement) return false
        if (deadline != other.deadline) return false
        if (statut != other.statut) return false
        if (priorite != other.priorite) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + nom.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + departement.hashCode()
        result = 31 * result + deadline.hashCode()
        result = 31 * result + statut.hashCode()
        result = 31 * result + priorite.hashCode()
        return result
    }
}