package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class UILoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink createAccountLink;

    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
        // Vous pouvez ajouter ici une logique d'initialisation si nécessaire,
        // par exemple, préremplir des champs pour le débogage.
    }

    /**
     * Gère l'action du bouton "Connexion".
     * Récupère les informations d'identification et simule une tentative de connexion.
     */
    @FXML
    private void handleLoginButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Logique de validation simple (pour l'exemple)
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez entrer votre nom d'utilisateur/email et votre mot de passe.");
        } else {
            // Ici, vous intégreriez votre logique d'authentification réelle (base de données, API, etc.)
            System.out.println("Tentative de connexion avec :");
            System.out.println("Nom d'utilisateur/Email: " + username);
            System.out.println("Mot de passe: " + password);

            // Simulation d'une connexion réussie ou échouée
            if (username.equals("admin") && password.equals("passer")) {
                showAlert(Alert.AlertType.INFORMATION, "Connexion réussie", "Bienvenue, " + username + " !");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainFenetre.fxml"));
                    Parent mainRoot = loader.load();
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(new Scene(mainRoot));
                    stage.centerOnScreen();
                    stage.setTitle("Accueil - Application de Location de Véhicules");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la fenêtre principale.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Échec de la connexion", "Nom d'utilisateur ou mot de passe incorrect.");
            }
        }
    }

    /**
     * Gère l'action du bouton "Annuler".
     * Ferme la fenêtre de connexion ou réinitialise les champs.
     */
    @FXML
    private void handleCancelButton() {
        System.out.println("Bouton Annuler cliqué.");
         Stage stage = (Stage) cancelButton.getScene().getWindow();
         stage.close();
    }

    /**
     * Gère l'action du lien "Créer en un".
     * Simule la navigation vers un écran d'inscription.
     */
    @FXML
    private void handleCreateAccountLink() {
        System.out.println("Lien 'Créer en un' cliqué.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIInscription.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) createAccountLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inscription - Application de Location de Véhicules");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger l'écran d'inscription.");
        }
    }

    /**
     * Affiche une boîte de dialogue d'alerte.
     * @param alertType Le type d'alerte (INFORMATION, WARNING, ERROR, etc.)
     * @param title Le titre de la boîte de dialogue.
     * @param message Le message à afficher.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
