package com.example.myapplication.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.Livrable
import com.example.myapplication.models.User
import java.text.SimpleDateFormat
import java.util.*

class LivrableAdapter(
    private val currentUser: User,
    private val onItemClick: (Livrable) -> Unit,
    private val onItemLongClick: (Livrable) -> Boolean = { false }
) : ListAdapter<Livrable, LivrableAdapter.LivrableViewHolder>(DiffCallback) {

    private var showDepartement: Boolean = true
    private var showProgress: Boolean = true

    fun setShowDepartement(show: Boolean) {
        showDepartement = show
        notifyDataSetChanged()
    }

    fun setShowProgress(show: Boolean) {
        showProgress = show
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivrableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_livrable, parent, false)
        return LivrableViewHolder(view)
    }

    override fun onBindViewHolder(holder: LivrableViewHolder, position: Int) {
        val livrable = getItem(position)
        holder.bind(livrable)

        holder.itemView.setOnClickListener {
            if (currentUser.peutVoirDepartement(livrable.departement)) {
                onItemClick(livrable)
            } else {
                // Afficher un message d'erreur si l'utilisateur n'a pas accès
                android.widget.Toast.makeText(
                    holder.itemView.context,
                    "Vous n'avez pas accès à ce département",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }

        holder.itemView.setOnLongClickListener {
            if (currentUser.peutModifierLivrable(livrable)) {
                onItemLongClick(livrable)
            } else {
                android.widget.Toast.makeText(
                    holder.itemView.context,
                    "Vous n'avez pas la permission de modifier ce livrable",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                false
            }
        }

        // Changer l'opacité si l'utilisateur n'a pas accès
        if (!currentUser.peutVoirDepartement(livrable.departement)) {
            holder.itemView.alpha = 0.6f
        } else {
            holder.itemView.alpha = 1.0f
        }
    }

    inner class LivrableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomText: TextView = itemView.findViewById(R.id.nomText)
        private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
        private val departementText: TextView = itemView.findViewById(R.id.departementText)
        private val deadlineText: TextView = itemView.findViewById(R.id.deadlineText)
        private val statutText: TextView = itemView.findViewById(R.id.statutText)
        private val prioriteText: TextView = itemView.findViewById(R.id.prioriteText)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val progressText: TextView = itemView.findViewById(R.id.progressText)
        private val joursRestantsText: TextView = itemView.findViewById(R.id.joursRestantsText)
        private val iconUrgent: ImageView = itemView.findViewById(R.id.iconUrgent)
        private val iconRetard: ImageView = itemView.findViewById(R.id.iconRetard)
        private val iconScan: ImageView = itemView.findViewById(R.id.iconScan)

        fun bind(livrable: Livrable) {
            // Nom et description
            nomText.text = livrable.nom
            descriptionText.text = livrable.description.ifEmpty { "Aucune description" }

            // Département (optionnel)
            if (showDepartement) {
                departementText.text = getDepartementIcone(livrable.departement)
                departementText.visibility = View.VISIBLE
            } else {
                departementText.visibility = View.GONE
            }

            // Date limite formatée
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            deadlineText.text = "📅 ${dateFormat.format(livrable.deadline)}"

            // Statut avec couleur
            val (statut, couleurRes) = livrable.getStatutAvecCouleur()
            statutText.text = statut
            statutText.setBackgroundColor(ContextCompat.getColor(itemView.context, couleurRes))
            statutText.setTextColor(Color.WHITE)

            // Priorité avec icône
            prioriteText.text = livrable.getPrioriteAvecIcone()
            prioriteText.setTextColor(getPrioriteColor(livrable.priorite))

            // Jours restants/retard
            val joursText = when {
                livrable.estEnRetard() -> {
                    val retard = livrable.calculerJoursRetard()
                    "En retard de $retard jour${if (retard > 1) "s" else ""}"
                }
                livrable.getJoursRestants() == 0 -> "Aujourd'hui"
                else -> "${livrable.getJoursRestants()} jour${if (livrable.getJoursRestants() > 1) "s" else ""} restant${if (livrable.getJoursRestants() > 1) "s" else ""}"
            }
            joursRestantsText.text = joursText
            joursRestantsText.setTextColor(getJoursRestantsColor(livrable))

            // Barre de progression
            if (showProgress) {
                progressBar.progress = livrable.getProgression()
                progressText.text = "${livrable.getProgression()}%"
                progressBar.visibility = View.VISIBLE
                progressText.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
                progressText.visibility = View.GONE
            }

            // Icônes d'alerte
            iconUrgent.visibility = if (livrable.necessiteAttention() && !livrable.estEnRetard()) View.VISIBLE else View.GONE
            iconRetard.visibility = if (livrable.estEnRetard()) View.VISIBLE else View.GONE
            iconScan.visibility = if (livrable.scanUrl != null) View.VISIBLE else View.GONE

            // Badge "Nouveau" pour les livrables récents
            val isRecent = System.currentTimeMillis() - livrable.dateCreation.time < 24 * 60 * 60 * 1000 // 24 heures
            if (isRecent && livrable.statut == "a_faire") {
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_livrable_recent)
            } else {
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_livrable_normal)
            }
        }

        private fun getDepartementIcone(departement: String): String {
            return when (departement) {
                "Direction Générale" -> "👑 $departement"
                "Direction Technique" -> "⚙️ $departement"
                "Développement" -> "💻 $departement"
                "Marketing" -> "📢 $departement"
                "Design" -> "🎨 $departement"
                "Commercial" -> "💰 $departement"
                "Ressources Humaines" -> "👥 $departement"
                "Finance" -> "📊 $departement"
                "Support Client" -> "🤝 $departement"
                else -> "📁 $departement"
            }
        }

        private fun getPrioriteColor(priorite: String): Int {
            return when (priorite) {
                "urgente" -> ContextCompat.getColor(itemView.context, R.color.priorite_urgente)
                "haute" -> ContextCompat.getColor(itemView.context, R.color.priorite_haute)
                "moyenne" -> ContextCompat.getColor(itemView.context, R.color.priorite_moyenne)
                "basse" -> ContextCompat.getColor(itemView.context, R.color.priorite_basse)
                else -> ContextCompat.getColor(itemView.context, R.color.priorite_moyenne)
            }
        }

        private fun getJoursRestantsColor(livrable: Livrable): Int {
            return when {
                livrable.estEnRetard() -> ContextCompat.getColor(itemView.context, R.color.statut_en_retard)
                livrable.getJoursRestants() <= 1 -> ContextCompat.getColor(itemView.context, R.color.priorite_urgente)
                livrable.getJoursRestants() <= 3 -> ContextCompat.getColor(itemView.context, R.color.priorite_haute)
                else -> ContextCompat.getColor(itemView.context, R.color.text_secondaire)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Livrable>() {
        override fun areItemsTheSame(oldItem: Livrable, newItem: Livrable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Livrable, newItem: Livrable): Boolean {
            return oldItem == newItem
        }
    }

    // Méthodes utilitaires pour le filtrage
    fun filtrerParDepartement(departement: String): List<Livrable> {
        return currentList.filter { it.departement == departement }
    }

    fun filtrerParStatut(statut: String): List<Livrable> {
        return currentList.filter { it.statut == statut }
    }

    fun filtrerParPriorite(priorite: String): List<Livrable> {
        return currentList.filter { it.priorite == priorite }
    }

    fun getLivrablesUrgents(): List<Livrable> {
        return currentList.filter { it.necessiteAttention() }
    }

    fun getStats(): Map<String, Int> {
        val livrables = currentList
        return mapOf(
            "total" to livrables.size,
            "en_retard" to livrables.count { it.estEnRetard() },
            "urgent" to livrables.count { it.getJoursRestants() <= 3 && !it.estEnRetard() },
            "termine" to livrables.count { it.statut == "termine" },
            "a_faire" to livrables.count { it.statut == "a_faire" },
            "en_cours" to livrables.count { it.statut == "en_cours" }
        )
    }

    // Tri des livrables
    fun trierParDeadline() {
        submitList(currentList.sortedBy { it.deadline })
    }

    fun trierParPriorite() {
        val order = mapOf("urgente" to 0, "haute" to 1, "moyenne" to 2, "basse" to 3)
        submitList(currentList.sortedBy { order[it.priorite] ?: 2 })
    }

    fun trierParStatut() {
        val order = mapOf("en_retard" to 0, "a_faire" to 1, "en_cours" to 2, "termine" to 3)
        submitList(currentList.sortedBy {
            order[if (it.estEnRetard()) "en_retard" else it.statut] ?: 1
        })
    }

    fun trierParDepartement() {
        submitList(currentList.sortedBy { it.departement })
    }

    // Recherche
    fun rechercher(query: String): List<Livrable> {
        if (query.isBlank()) return currentList

        return currentList.filter { livrable ->
            livrable.nom.contains(query, ignoreCase = true) ||
                    livrable.description.contains(query, ignoreCase = true) ||
                    livrable.departement.contains(query, ignoreCase = true) ||
                    livrable.tags.any { it.contains(query, ignoreCase = true) }
        }
    }
}