# ArrasGameClient

## Accès à l'application
- **Application** : [Télécharger le .jar](https://github.com/eLux-info/AP3/releases/tag/1.0.0)

## Description
**ArrasGameClient** est une application Java qui combine une interface utilisateur graphique (Swing), une authentification Windows, et une gestion de base de données MySQL. Elle permet aux utilisateurs de se connecter via leurs identifiants Windows, d'entrer un code de forfait, et de gérer un compte à rebours associé à ces forfaits.

---

## Fonctionnalités principales
1. **Authentification Windows** :
   - Utilise la bibliothèque JNA pour authentifier les utilisateurs via leurs identifiants Windows.
   - Classe concernée : `WindowsAuth`.

2. **Connexion à la base de données** :
   - Utilise JDBC pour se connecter à une base de données MySQL.
   - Classe concernée : `DatabaseConnection`.

3. **Gestion des forfaits** :
   - Vérification des forfaits via un code unique.
   - Affichage d'un compte à rebours basé sur le temps restant.
   - Sauvegarde automatique du temps restant dans la base de données.

4. **Interface utilisateur graphique** :
   - Utilise Swing pour une interface intuitive.
   - Trois écrans principaux :
     - Écran de connexion pour identifier l'utilisateur.
     - Écran d'entrée pour saisir le code de forfait.
     - Écran de compte à rebours pour gérer le temps restant.

---

## Structure du projet
Le projet est organisé en plusieurs packages :

### 1. `com.arrasgame.auth`
- **WindowsAuth** : Authentifie les utilisateurs via leurs identifiants Windows.

### 2. `com.arrasgame.database`
- **DatabaseConnection** : Fournit une méthode utilitaire pour établir une connexion à la base de données MySQL.
- **TestConnection** : Classe de test pour vérifier la connexion à la base de données.

### 3. `com.arrasgame.ui`
- **ForfaitApp** : Fenêtre principale pour la gestion des forfaits.
- **LoginDialog** : Fenêtre de connexion pour l'authentification Windows.

### 4. `com.arrasgame.main`
- **Main** : Point d'entrée de l'application.

---

## Configuration requise

### 1. **Base de données**
- Une base de données MySQL avec une table `user_packages` contenant :
  - `code` (VARCHAR) : Code unique du forfait.
  - `remaining_time` (INT) : Temps restant en secondes.

### 2. **Fichiers de configuration**
- Les informations de connexion à la base de données sont définies dans `DatabaseConnection.java`.

### 3. **JVM**
- Java 11 ou supérieur.

---

## Dépendances
Le projet utilise les bibliothèques suivantes :
- **JNA 5.12.1** : Pour l'authentification Windows.
- **MySQL Connector/J 8.0.33** : Pour la connexion à la base de données MySQL.
- **Java Desktop** : Pour l'interface utilisateur Swing.

---

## Instructions pour exécuter le projet

1. **Configurer la base de données** :
   - Créez une base de données MySQL nommée `arras-game-lrv`.
   - Importez le fichier SQL se trouvant dans `database/`

2. **Compiler le projet** :
   - Ajoutez bien les librairies se trouvant dans `/libs` à votre projet
   - Assurez-vous que toutes les dépendances (JNA, MySQL Connector/J) sont disponibles dans le modulepath.

3. **Compilation du projet**

Pour compiler le projet avec les dépendances :

```bash
javac -cp "lib/jna-5.12.1.jar;lib/mysql-connector-j-8.0.33.jar" src/*.java
```

Pour créer un JAR exécutable :

```bash
jar cvfe app.jar Main -C bin/ .
```

4. **Exécuter l'application** :
   - Lancez la classe `com.arrasgame.main.Main`.

---

## Tests

Pour exécuter les tests unitaires :

```bash
java -cp "lib/*" org.junit.runner.JUnitCore TestConnection
```

## Dépannage

### Problèmes courants

1. Échec de l'authentification Windows
   - Vérifier que l'utilisateur Windows a les droits nécessaires
   - S'assurer que le service "Windows Authentication" est actif

2. Connexion à la base de données refusée
   - Vérifier les informations de connexion dans `DatabaseConnection.java`
   - S'assurer que MySQL est en cours d'exécution
   - Vérifier les droits de l'utilisateur dans MySQL

3. Erreur de chargement des bibliothèques
   - Vérifier que JNA et MySQL Connector/J sont dans le modulepath
   - Utiliser les versions compatibles mentionnées ci-dessus

---

## Points importants

- **Gestion des erreurs** :
  - Les erreurs de connexion à la base de données ou d'authentification sont gérées avec des messages d'erreur affichés à l'utilisateur.

- **Sauvegarde automatique** :
  - Le temps restant est sauvegardé dans la base de données toutes les 15 secondes ou lorsque le compte à rebours est mis en pause.

- **Sécurité** :
  - Les identifiants Windows ne sont pas stockés, ils sont uniquement utilisés pour l'authentification.

## Licence

Ce projet est sous licence [MIT](https://opensource.org/licenses/MIT).
