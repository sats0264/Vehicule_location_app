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
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Utilisateur;

import java.io.IOException;

public class UILoginController extends Controller {

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

    private Utilisateur currentUser;
    private Client currentClient;

    public UILoginController() throws DAOException {
    }

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
        String loginInput = usernameField.getText();
        String password = passwordField.getText();

        if (loginInput.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez entrer votre identifiant et votre mot de passe.");
            return;
        }

        try {
            boolean authenticated = false;
            boolean isClient = false;

            if (isEmail(loginInput)) {
                authenticated = authenticateClient(loginInput, password);
                currentClient = rechercherObjectByString(loginInput, Client.class);
                isClient = true;
            } else {
                authenticated = authenticateUser(loginInput, password);
                currentUser = rechercherObjectByString(loginInput, Utilisateur.class);
            }

            if (authenticated  && !isClient) {
                showAlert(Alert.AlertType.INFORMATION, "Connexion réussie", "Bienvenue, " + loginInput + " !");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainFenetre.fxml"));
                Parent mainRoot = loader.load();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(mainRoot));
                stage.centerOnScreen();
                stage.setTitle("Accueil - Application de Location de Véhicules");
                stage.show();

                MainFenetreController controller = loader.getController();

                controller.setCurrentUser(currentUser);

            } else if (isClient && authenticated) {
                showAlert(Alert.AlertType.INFORMATION, "Connexion réussie", "Bienvenue, " + loginInput + " !");
                if (currentClient != null && currentClient.getEmail() != null) {
                    System.setProperty("client.email", currentClient.getEmail());
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIFenetreClient.fxml"));
                Parent mainRoot = loader.load();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(mainRoot));
                stage.centerOnScreen();
                stage.setTitle("Accueil - Application de Location de Véhicules");
                stage.show();

                UIFenetreClientController controller = loader.getController();

                controller.setCurrentClient(currentClient);
            }
            else {
                showAlert(Alert.AlertType.ERROR, "Échec de la connexion", "Identifiants incorrects.");
            }

        } catch (DAOException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'authentification : " + e.getMessage());
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
    private boolean isEmail(String input) {
        return input.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

}
