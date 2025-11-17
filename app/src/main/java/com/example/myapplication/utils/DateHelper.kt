package com.example.myapplication.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateHelper {

    // === FORMATS DE DATE ===
    private const val DATE_FORMAT_DISPLAY = "dd/MM/yyyy"
    private const val DATE_FORMAT_DB = "yyyy-MM-dd"
    private const val DATETIME_FORMAT_DISPLAY = "dd/MM/yyyy 'à' HH:mm"
    private const val DATETIME_FORMAT_DB = "yyyy-MM-dd HH:mm:ss"
    private const val TIME_FORMAT_DISPLAY = "HH:mm"
    private const val DAY_MONTH_FORMAT = "dd MMM"
    private const val MONTH_YEAR_FORMAT = "MMM yyyy"
    private const val FULL_DATE_FORMAT = "EEEE dd MMMM yyyy"
    private const val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    // === FORMATAGE DE BASE ===

    /**
     * Formate une date pour l'affichage (dd/MM/yyyy)
     */
    fun formatDate(date: Date): String {
        return SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault()).format(date)
    }

    /**
     * Formate une date pour la base de données (yyyy-MM-dd)
     */
    fun formatDateForDB(date: Date): String {
        return SimpleDateFormat(DATE_FORMAT_DB, Locale.getDefault()).format(date)
    }

    /**
     * Formate une date et heure pour l'affichage (dd/MM/yyyy à HH:mm)
     */
    fun formatDateTime(date: Date): String {
        return SimpleDateFormat(DATETIME_FORMAT_DISPLAY, Locale.getDefault()).format(date)
    }

    /**
     * Formate une date et heure pour la base de données (yyyy-MM-dd HH:mm:ss)
     */
    fun formatDateTimeForDB(date: Date): String {
        return SimpleDateFormat(DATETIME_FORMAT_DB, Locale.getDefault()).format(date)
    }

    /**
     * Formate seulement l'heure (HH:mm)
     */
    fun formatTime(date: Date): String {
        return SimpleDateFormat(TIME_FORMAT_DISPLAY, Locale.getDefault()).format(date)
    }

    /**
     * Formate en jour et mois (15 Janv)
     */
    fun formatDayMonth(date: Date): String {
        return SimpleDateFormat(DAY_MONTH_FORMAT, Locale.getDefault()).format(date)
    }

    /**
     * Formate en mois et année (Janv 2024)
     */
    fun formatMonthYear(date: Date): String {
        return SimpleDateFormat(MONTH_YEAR_FORMAT, Locale.getDefault()).format(date)
    }

    /**
     * Formate en date complète (Lundi 15 Janvier 2024)
     */
    fun formatFullDate(date: Date): String {
        return SimpleDateFormat(FULL_DATE_FORMAT, Locale.getDefault()).format(date)
    }

    /**
     * Formate en format ISO (pour les APIs)
     */
    fun formatISO(date: Date): String {
        return SimpleDateFormat(ISO_FORMAT, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(date)
    }

    // === PARSING ===

    /**
     * Parse une string en Date selon le format d'affichage
     */
    fun parseDate(dateString: String): Date? {
        return try {
            SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault()).parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse une string en Date selon le format de base de données
     */
    fun parseDateFromDB(dateString: String): Date? {
        return try {
            SimpleDateFormat(DATE_FORMAT_DB, Locale.getDefault()).parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse une string en Date selon un format personnalisé
     */
    fun parseDate(dateString: String, format: String): Date? {
        return try {
            SimpleDateFormat(format, Locale.getDefault()).parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse une string ISO en Date
     */
    fun parseISO(isoString: String): Date? {
        return try {
            SimpleDateFormat(ISO_FORMAT, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(isoString)
        } catch (e: Exception) {
            null
        }
    }

    // === DATES RELATIVES ===

    /**
     * Formate une date de manière relative (Aujourd'hui, Hier, Dans 3 jours...)
     */
    fun formatRelativeDate(date: Date): String {
        val now = Date()
        val difference = date.time - now.time
        val days = TimeUnit.MILLISECONDS.toDays(difference).toInt()

        return when {
            isToday(date) -> "Aujourd'hui"
            isTomorrow(date) -> "Demain"
            isYesterday(date) -> "Hier"
            days > 0 -> "Dans $days jour${if (days > 1) "s" else ""}"
            days < 0 -> "Il y a ${-days} jour${if (-days > 1) "s" else ""}"
            else -> formatDate(date)
        }
    }

    /**
     * Formate une deadline de manière contextuelle
     */
    fun formatDeadline(deadline: Date): String {
        val now = Date()
        val difference = deadline.time - now.time
        val days = TimeUnit.MILLISECONDS.toDays(difference).toInt()

        return when {
            deadline.before(now) -> {
                val retard = TimeUnit.MILLISECONDS.toDays(now.time - deadline.time).toInt()
                "En retard de $retard jour${if (retard > 1) "s" else ""}"
            }
            days == 0 -> "Aujourd'hui"
            days == 1 -> "Demain"
            days <= 7 -> "Dans $days jour${if (days > 1) "s" else ""}"
            else -> {
                val weeks = days / 7
                if (weeks == 1) "Dans 1 semaine" else "Dans $weeks semaines"
            }
        }
    }

    /**
     * Formate la durée entre deux dates
     */
    fun formatDuration(start: Date, end: Date): String {
        val duration = end.time - start.time
        val days = TimeUnit.MILLISECONDS.toDays(duration).toInt()
        val hours = TimeUnit.MILLISECONDS.toHours(duration) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60

        return when {
            days > 0 -> "${days}j ${hours}h"
            hours > 0 -> "${hours}h ${minutes}min"
            else -> "${minutes}min"
        }
    }

    // === VÉRIFICATIONS ===

    /**
     * Vérifie si une date est aujourd'hui
     */
    fun isToday(date: Date): Boolean {
        val calendar1 = Calendar.getInstance().apply { time = date }
        val calendar2 = Calendar.getInstance().apply { time = Date() }
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Vérifie si une date est demain
     */
    fun isTomorrow(date: Date): Boolean {
        val calendar1 = Calendar.getInstance().apply { time = date }
        val calendar2 = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Vérifie si une date est hier
     */
    fun isYesterday(date: Date): Boolean {
        val calendar1 = Calendar.getInstance().apply { time = date }
        val calendar2 = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Vérifie si une date est dans le passé
     */
    fun isPast(date: Date): Boolean {
        return date.before(Date())
    }

    /**
     * Vérifie si une date est dans le futur
     */
    fun isFuture(date: Date): Boolean {
        return date.after(Date())
    }

    /**
     * Vérifie si une date est dans la semaine en cours
     */
    fun isThisWeek(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        calendar.time = date
        return calendar.get(Calendar.WEEK_OF_YEAR) == currentWeek &&
                calendar.get(Calendar.YEAR) == currentYear
    }

    /**
     * Vérifie si une date est dans le mois en cours
     */
    fun isThisMonth(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        calendar.time = date
        return calendar.get(Calendar.MONTH) == currentMonth &&
                calendar.get(Calendar.YEAR) == currentYear
    }

    // === CALCULS DE DATE ===

    /**
     * Ajoute des jours à une date
     */
    fun addDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance().apply { time = date }
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }

    /**
     * Ajoute des semaines à une date
     */
    fun addWeeks(date: Date, weeks: Int): Date {
        val calendar = Calendar.getInstance().apply { time = date }
        calendar.add(Calendar.WEEK_OF_YEAR, weeks)
        return calendar.time
    }

    /**
     * Ajoute des mois à une date
     */
    fun addMonths(date: Date, months: Int): Date {
        val calendar = Calendar.getInstance().apply { time = date }
        calendar.add(Calendar.MONTH, months)
        return calendar.time
    }

    /**
     * Calcule la différence en jours entre deux dates
     */
    fun getDaysBetween(start: Date, end: Date): Int {
        val difference = end.time - start.time
        return TimeUnit.MILLISECONDS.toDays(difference).toInt()
    }

    /**
     * Calcule la différence en jours ouvrés entre deux dates
     */
    fun getBusinessDaysBetween(start: Date, end: Date): Int {
        var businessDays = 0
        val calendar = Calendar.getInstance().apply { time = start }

        while (calendar.time.before(end) || isSameDay(calendar.time, end)) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                businessDays++
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return businessDays
    }

    /**
     * Vérifie si deux dates sont le même jour
     */
    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    // === DATES SPÉCIALES ===

    /**
     * Obtient le début du jour (00:00:00)
     */
    fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance().apply { time = date }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * Obtient la fin du jour (23:59:59)
     */
    fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance().apply { time = date }
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    /**
     * Obtient le début de la semaine
     */
    fun getStartOfWeek(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            firstDayOfWeek = Calendar.MONDAY
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return getStartOfDay(calendar.time)
    }

    /**
     * Obtient le début du mois
     */
    fun getStartOfMonth(date: Date): Date {
        val calendar = Calendar.getInstance().apply { time = date }
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return getStartOfDay(calendar.time)
    }

    /**
     * Obtient la date d'aujourd'hui
     */
    fun today(): Date {
        return Date()
    }

    /**
     * Obtient la date de demain
     */
    fun tomorrow(): Date {
        return addDays(today(), 1)
    }

    /**
     * Obtient la date d'hier
     */
    fun yesterday(): Date {
        return addDays(today(), -1)
    }

    // === VALIDATION ===

    /**
     * Vérifie si une date est valide pour une deadline
     */
    fun isValidDeadline(date: Date): Boolean {
        return !isPast(getStartOfDay(date))
    }

    /**
     * Vérifie si une date est dans une plage donnée
     */
    fun isDateInRange(date: Date, start: Date, end: Date): Boolean {
        return !date.before(getStartOfDay(start)) && !date.after(getEndOfDay(end))
    }

    // === FORMATAGE POUR L'UI ===

    /**
     * Formate une date pour l'affichage dans les listes
     */
    fun formatForListItem(date: Date): String {
        return when {
            isToday(date) -> "Aujourd'hui"
            isTomorrow(date) -> "Demain"
            isYesterday(date) -> "Hier"
            isThisWeek(date) -> {
                val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
                dayName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }
            else -> formatDate(date)
        }
    }

    /**
     * Formate une période (date de début à date de fin)
     */
    fun formatPeriod(start: Date, end: Date): String {
        return if (isSameDay(start, end)) {
            "Le ${formatDate(start)}"
        } else {
            "Du ${formatDate(start)} au ${formatDate(end)}"
        }
    }

    /**
     * Formate l'âge d'une date (il y a X jours/semaines/mois)
     */
    fun formatAge(fromDate: Date): String {
        val now = Date()
        val diff = now.time - fromDate.time

        val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()
        val weeks = days / 7
        val months = days / 30

        return when {
            days == 0 -> "Aujourd'hui"
            days == 1 -> "Hier"
            days < 7 -> "Il y a $days jour${if (days > 1) "s" else ""}"
            weeks == 1 -> "Il y a 1 semaine"
            weeks < 4 -> "Il y a $weeks semaines"
            months == 1 -> "Il y a 1 mois"
            months < 12 -> "Il y a $months mois"
            else -> {
                val years = months / 12
                if (years == 1) "Il y a 1 an" else "Il y a $years ans"
            }
        }
    }


    // === EXTENSIONS POUR CALENDAR ===

    /**
     * Obtient le nombre de jours dans le mois d'une date
     */
    fun getDaysInMonth(date: Date): Int {
        val calendar = Calendar.getInstance().apply { time = date }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * Obtient le nom du mois
     */
    fun getMonthName(date: Date): String {
        return SimpleDateFormat("MMMM", Locale.getDefault()).format(date)
    }

    /**
     * Obtient le nom du jour de la semaine
     */
    fun getDayName(date: Date): String {
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    }

    // === GESTION DES FUSEAUX HORAIRES ===

    /**
     * Convertit une date en UTC
     */
    fun toUTC(date: Date): Date {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.time = date
        return calendar.time
    }

    /**
     * Convertit une date UTC en heure locale
     */
    fun fromUTC(utcDate: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = utcDate
        return calendar.time
    }
}

// === EXTENSIONS POUR DATE ===

/**
 * Extension pour formater une Date
 */
fun Date.formatToDisplay(): String = DateHelper.formatDate(this)
fun Date.formatToDB(): String = DateHelper.formatDateForDB(this)
fun Date.formatDateTime(): String = DateHelper.formatDateTime(this)
fun Date.formatRelative(): String = DateHelper.formatRelativeDate(this)
fun Date.formatDeadline(): String = DateHelper.formatDeadline(this)
fun Date.formatForListItem(): String = DateHelper.formatForListItem(this)
fun Date.formatAge(): String = DateHelper.formatAge(this)

/**
 * Extensions pour les vérifications
 */
fun Date.isToday(): Boolean = DateHelper.isToday(this)
fun Date.isTomorrow(): Boolean = DateHelper.isTomorrow(this)
fun Date.isYesterday(): Boolean = DateHelper.isYesterday(this)
fun Date.isPast(): Boolean = DateHelper.isPast(this)
fun Date.isFuture(): Boolean = DateHelper.isFuture(this)
fun Date.isThisWeek(): Boolean = DateHelper.isThisWeek(this)
fun Date.isThisMonth(): Boolean = DateHelper.isThisMonth(this)

/**
 * Extensions pour les calculs
 */
fun Date.addDays(days: Int): Date = DateHelper.addDays(this, days)
fun Date.addWeeks(weeks: Int): Date = DateHelper.addWeeks(this, weeks)
fun Date.addMonths(months: Int): Date = DateHelper.addMonths(this, months)
fun Date.daysUntil(other: Date): Int = DateHelper.getDaysBetween(this, other)
fun Date.isSameDay(other: Date): Boolean = DateHelper.isSameDay(this, other)

/**
 * Extensions pour les dates spéciales
 */
fun Date.startOfDay(): Date = DateHelper.getStartOfDay(this)
fun Date.endOfDay(): Date = DateHelper.getEndOfDay(this)
fun Date.startOfWeek(): Date = DateHelper.getStartOfWeek(this)
fun Date.startOfMonth(): Date = DateHelper.getStartOfMonth(this)