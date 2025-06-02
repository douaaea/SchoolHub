# 📚 ScholarHub

[![Build Status](https://dev.azure.com/DouaaAmeziane/ScholarHub/_apis/build/status/douaaea.ScholarHub%20(5)?branchName=main)](https://dev.azure.com/DouaaAmeziane/ScholarHub/_build/latest?definitionId=5&branchName=main)

**ScholarHub** est une application multiplateforme (Web et Mobile) de gestion scolaire.  
Elle permet aux enseignants de créer et corriger des devoirs, aux élèves de les soumettre et consulter leurs notes, et au principal (administrateur) de gérer l'ensemble de la structure scolaire.

---

## 🚀 Fonctionnalités

### 👤 Élèves
- Connexion sécurisée
- Consultation des devoirs
- Soumission de fichiers pour les devoirs
- Visualisation des notes
- Consultation du profil personnel

### 👨‍🏫 Enseignants
- Création de devoirs et affectation à un groupe ou niveau
- Notation des devoirs soumis
- Ajout manuel des notes d’examens

### 🛠 Administrateur (Principal)
- Gestion des comptes utilisateurs (enseignants et élèves)
- Création de groupes et de niveaux scolaires
- Affectation des élèves à des groupes
- Tableau de bord de supervision avec statistiques globales

---
##screenshots
![image](https://github.com/user-attachments/assets/c31f5053-69bb-4842-bbc3-19cc2cf27774)
![image](https://github.com/user-attachments/assets/6d34ff9a-2f49-4ea5-bb49-f17d3922bfef)
![image](https://github.com/user-attachments/assets/54fdfbfb-52fe-4294-9bbd-2a85ee1ae4c5)
![image](https://github.com/user-attachments/assets/76b6dad9-c55a-48fb-9f8b-dcecace1f2fc)
![image](https://github.com/user-attachments/assets/c7a26e06-8786-4d00-a866-35b541538b5c)


## 🧰 Technologies utilisées

| Composant        | Technologie         |
|------------------|---------------------|
| Frontend Web     | React (Next.js 15)  |
| Frontend Mobile  | Java (Android)      |
| Backend API      | Spring Boot (Java 21) |
| Base de Données  | MySQL               |
| CI/CD            | Azure DevOps (YAML) |
| Conteneurisation | Docker & Docker Compose |
| Orchestration    | Docker Swarm        |

---

## ⚙️ CI/CD Pipeline

Le projet utilise **Azure DevOps** avec un pipeline complet :

- Build & tests du backend (`mvnw` dans `backend/demo`)
- Lint, build et tests du frontend (`frontendWeb`)
- Build de l’APK Android (`frontendMobile`)
- Déploiement local automatique via Docker Swarm

🖥️ Pipeline exécuté sur un **agent auto-hébergé local** `DESKTOP-CIJ61FT`.

---

## 🐳 Déploiement local avec Docker Swarm

### Prérequis :
- Docker Desktop installé
- Swarm initialisé : `docker swarm init`

### Commandes :
```bash
# Construire les images
docker build -t scholarhub-backend ./backend
docker build -t scholarhub-frontend ./frontendWeb

# Déployer les services avec Swarm
docker stack deploy -c docker-compose.yml scholarhub
