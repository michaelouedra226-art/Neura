# Guide de Compilation GitHub Actions — NEURA AI

Ce projet intègre des workflows GitHub Actions prêts à l'emploi pour compiler automatiquement l'application Android et générer le fichier **APK** téléchargeable.

---

## 1. Workflows disponibles

Deux workflows sont configurés dans `.github/workflows/` :

1. **`build-apk.yml`** : Se déclenche automatiquement lors d'un `push` ou d'une `pull request` sur les branches `main` ou `master`, ainsi que manuellement via **workflow_dispatch**.
2. **`release-apk.yml`** : Se déclenche lors de la création d'un tag git (ex: `v1.0.0`) ou manuellement pour générer un artefact de release.

---

## 2. Comment lancer la compilation sur GitHub

1. Poussez votre code sur votre dépôt GitHub.
2. Rendez-vous dans l'onglet **Actions** de votre dépôt GitHub.
3. Dans la colonne de gauche, sélectionnez le workflow **"Build Android APK"**.
4. Cliquez sur le bouton **"Run workflow"** (à droite), choisissez la branche puis confirmez.
5. GitHub Actions configure automatiquement l'environnement (Ubuntu, Java 17 Temurin, Android SDK, Gradle 9.3.1) et exécute :
   ```bash
   ./gradlew testDebugUnitTest
   ./gradlew assembleDebug
   ```
6. Une fois le workflow terminé avec succès (coche verte), descendez dans la section **Artifacts** en bas de la page d'exécution.
7. Cliquez sur **`NEURA-AI-debug-apk`** pour télécharger directement le fichier APK prêt à être installé sur votre téléphone Android.

---

## 3. Configuration des secrets (optionnel)

Si vous souhaitez injecter votre clé Gemini ou Agnes AI dès la compilation dans GitHub Actions :
1. Allez dans **Settings > Secrets and variables > Actions** de votre dépôt GitHub.
2. Créez un secret appelé `GEMINI_API_KEY`.
3. Vous pouvez également configurer directement vos clés dans l'application via la section **Paramètres** de NEURA AI.
