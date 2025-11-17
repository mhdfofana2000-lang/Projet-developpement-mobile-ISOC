

# 📌 TaskFlow Manager

### **Application mobile de gestion et suivi des tâches par département**

---

## 📝 Présentation du projet

**TaskFlow Manager** est une application mobile développée en **Flutter**, destinée aux organisations souhaitant digitaliser la gestion interne des tâches, améliorer la coordination entre départements et renforcer le suivi global des missions.

Elle intègre **Firebase (Authentication & Realtime Database)** pour assurer :

* la synchronisation en temps réel ;
* la gestion multi-utilisateurs ;
* un stockage structuré et sécurisé.

Chaque chef de département gère les tâches de son service, tandis que la **Direction Technique** bénéficie d’une vue globale sur l'ensemble des départements.

---

## 🎯 Objectifs

* Digitaliser et structurer la gestion interne des tâches.
* Améliorer la fluidité et la coordination inter-départements.
* Remplacer les échanges WhatsApp non structurés & les fichiers Excel dispersés.
* Offrir une vue d’ensemble claire pour la Direction Technique.
* Permettre un suivi en temps réel, accessible depuis n’importe quel téléphone connecté.

---

## 🛠️ Fonctionnalités principales

### ✔️ Authentification (Firebase Auth)

* Connexion par **email / mot de passe**.
* Gestion des rôles :

  * **Chef de Département**
  * **Direction Technique (Admin)**

---

### ✔️ Gestion des Départements

Chaque chef ne voit **que son propre département**.
Exemples de départements :

* Études
* Technique
* Financier
* RH
* Suivi–Évaluation

---

### ✔️ Gestion complète des Tâches

* Création, édition, suppression
* Informations d’une tâche :

  * Titre
  * Description
  * Priorité
  * Statut (En cours, Fait, En attente)
  * Responsable
  * Département
  * Date limite
* Mise à jour en temps réel via Firebase

---

### ✔️ Vue globale pour la Direction Technique (Admin)

👤 Réservé à l’Admin :

* Tâches regroupées par département
* Statistiques globales
* Nombre de tâches par cellule
* Tâches en retard
* Prochaines échéances
* Filtres avancés (statut, date, service)

---

### ✔️ Mode Hors-ligne (Cache Firebase)

* Consultation des données déjà chargées sans connexion
* Synchronisation automatique dès le retour du réseau

---

### ✔️ Interface moderne (Flutter Material 3)

* UI propre et intuitive
* Composants réutilisables
* Navigation fluide
* Thème cohérent

---

## 🧱 Architecture du projet

```
lib/
├── main.dart
├── core/
│   ├── constants/
│   └── theme/
├── utils/
├── models/
│   ├── task_model.dart
│   └── user_model.dart
├── services/
│   ├── auth_service.dart
│   ├── task_service.dart
│   └── department_service.dart
├── screens/
│   ├── auth/
│   │   ├── login_screen.dart
│   │   └── register_screen.dart
│   ├── departments/
│   │   ├── department_list_screen.dart
│   │   └── department_tasks_screen.dart
│   ├── tasks/
│   │   ├── task_form_screen.dart
│   │   └── task_detail_screen.dart
│   └── admin/
│       ├── overview_screen.dart
│       └── stats_screen.dart
└── widgets/
    ├── task_card.dart
    ├── loading.dart
    └── custom_button.dart
```

---

## 🗄️ Structure de la base Firebase (Realtime Database)

```json
{
  "users": {
    "id123": {
      "name": "Mohamed Fofana",
      "email": "admin@company.com",
      "role": "admin",
      "department": null
    },
    "id456": {
      "name": "Yacouba",
      "email": "chef.tech@company.com",
      "role": "chef",
      "department": "Technique"
    }
  },

  "tasks": {
    "task001": {
      "title": "Préparer le rapport hebdomadaire",
      "department": "Études",
      "priority": "Haute",
      "status": "En cours",
      "deadline": "2025-02-10",
      "createdBy": "id456"
    }
  }
}
```

---

## 🚀 Installation & Lancement

### 1️⃣ Cloner le projet

```bash
git clone https://github.com/mhdfofana2000-lang/Projet-developpement-mobile-ISOC.git
cd Projet-developpement-mobile-ISOC
```

### 2️⃣ Installer les dépendances

```bash
flutter pub get
```

### 3️⃣ Configurer Firebase

1. Accéder à : [https://console.firebase.google.com](https://console.firebase.google.com)
2. Créer un projet Firebase
3. Activer :
   ✔ Authentication (email/mot de passe)
   ✔ Realtime Database
4. Télécharger **google-services.json**
5. Le placer dans :

```
android/app/
```

### 4️⃣ Lancer l'application

```bash
flutter run
```

---

## 📱 Tests sur émulateur / mobile

Lister les appareils :

```bash
flutter devices
```

Lancer un émulateur :

```bash
flutter emulators --launch <nom_avd>
```

Exécuter l'app :

```bash
flutter run
```

---

## 📦 Générer un APK (version finale)

```bash
flutter build apk --release
```

APK disponible dans :

```
build/app/outputs/flutter-apk/app-release.apk
```

---

## 📄 Licence

Ce projet est sous licence libre pour un usage éducatif, académique ou professionnel interne.

