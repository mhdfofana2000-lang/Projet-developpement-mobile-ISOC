 Gestionnaire de flux de tâches

Application de gestion et de suivi des tâches par département – ​​avec rôles Chef / Direction

📌 Présentation du projet

TaskFlow Manager est une application mobile développée en Flutter, destinée aux organisations souhaitant améliorer la gestion interne des tâches, la coordination entre départements et le suivi global des dossiers. Elle permet à chaque chef de département de gérer ses activités quotidiennes, tandis que la Direction Technique dispose d'une vue consolidée de l'ensemble des services.

L'application utilise Firebase (Realtime Database + Authentication) pour synchroniser les données entre plusieurs utilisateurs et appareils en temps réel.

🎯 Objectifs

Digitaliser la gestion des tâches au sein d'une organisation.
Faciliter la coordination entre les départements.
Offrir une vue globale à la Direction sur l'état d'avancement général.
Permettre la consultation et la mise à jour des tâches depuis n'importe quel téléphone connecté.
Remplacer les échanges WhatsApp, Excel dispersés et le manque de suivi structuré.
🛠️ Fonctionnalités principales

✔️ **Authentification (Firebase Auth)

Connexion par email & mot de passe

Gestion des rôles :

Chef de département
Technique de direction (Admin)
✔️ Gestion des Départements

Les départements sont définis par l'organisation, par exemple :

Études
Technique
Financier
RH
Suivi-Évaluation
Chaque chef ne voit que son propre département .

✔️ Gestion des Tâches

Ajouter une tâche

Modifier / Supprimer

Définir :

Titre
Description
Priorité
Statut (En cours, Fait, En attente)
Responsable
Date limite
Mise à jour en temps réel via Firebase

✔️ Vue Globale pour la Direction Technique

Accessible uniquement aux administrateurs :

Liste consolidée de tous les départements

Nombre de tâches par département

Tâches en retard

Délais Prochaines

Statistiques générales

Possibilité de filtre par :

date
statut
département
✔️ Mode hors-ligne (cache Firebase)**

L'utilisateur peut consulter les données récemment chargées même sans Internet
Synchronisation automatique lorsque la connexion revient
✔️ Interface moderne (Flutter Material 3)

Écrans simples et fluides
Cohérence visuelle
Icônes, couleurs, transitions
🧱 Architecture du projet

lib/
│
├── main.dart
│
├── core/
│   ├── constants/
│   ├── utils/
│   └── theme/
│
├── models/
│   ├── task_model.dart
│   └── user_model.dart
│
├── services/
│   ├── auth_service.dart
│   ├── task_service.dart
│   └── department_service.dart
│
├── screens/
│   ├── auth/
│   │   ├── login_screen.dart
│   │   └── register_screen.dart
│   │
│   ├── departments/
│   │   ├── department_list_screen.dart
│   │   └── department_tasks_screen.dart
│   │
│   ├── tasks/
│   │   ├── task_form_screen.dart
│   │   └── task_detail_screen.dart
│   │
│   └── admin/
│       ├── overview_screen.dart
│       └── stats_screen.dart
│
└── widgets/
    ├── task_card.dart
    ├── loading.dart
    └── custom_button.dart
🗄️ Base de données Firebase (Structure)

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
🚀 Installation et lancement

1️⃣ Cloner le projet

git clone https://github.com/username/taskflow_manager.git
cd taskflow_manager
2️⃣ Installer les dépendances

flutter pub get
3️⃣ Configurer Firebase

Aller sur https://console.firebase.google.com

Créer un projet

Authentification Activer (courriel/mot de passe)

Base de données en temps réel Activer

Télécharger le fichiergoogle-services.json

Le placer dans :

android/app/
4️⃣ Lancer l'application

flutter run
📱 Tests de l'application

Tester sur :

Émulateur Android

flutter devices
flutter emulators --launch <nom>
flutter run
📦 Générer un APK

flutter build apk --release
APK final dans :

build/app/outputs/flutter-apk/app-release.apk
📄Permis
