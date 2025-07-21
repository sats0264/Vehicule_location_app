package location.app.vehicule_location_app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Notification;
import location.app.vehicule_location_app.models.NotificationType;
import location.app.vehicule_location_app.observer.Subject;

import java.io.IOException;

public class UIInscriptionController extends Controller{


    @FXML
    private AnchorPane registrationAnchorPane;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField emailField;
    @FXML
    public TextField phoneField;
    @FXML
    public TextField adresseField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button cancelButton;
    @FXML
    private Button registerButton;
    @FXML
    private Hyperlink backToLoginLink;

    public UIInscriptionController() throws DAOException {
    }

    @FXML
    private void handleRegisterButton() {
        String lastName = lastNameField.getText();
        String firstName = firstNameField.getText();
        String email = emailField.getText();
        String adresse = adresseField.getText();
        String telephone = phoneField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Mots de passe non correspondants", "Les mots de passe ne correspondent pas.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Veuillez entrer une adresse email valide.");
            return;
        }

        try {
            var newClient = new Client(lastName, firstName, email, adresse, telephone, password);
            ajouterObject(newClient, Client.class);
            NotificationService notificationService = NotificationService.getInstance();

            Notification notif = new Notification(
                    "Nouvelle Inscription",
                    "Le client " + lastName+" "+firstName  + " s'est inscrit.",
                    NotificationType.NEW_CLIENT_REGISTRATION,
                    newClient.getId());

            notificationService.addNotification(notif);
            Subject.getInstance().notifyAllObservers();

            showAlert(Alert.AlertType.INFORMATION, "Inscription réussie", "Votre compte a été créé avec succès !");
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Tentative d'inscription avec :");
        System.out.println("Nom: " + lastName);
        System.out.println("Prénom: " + firstName);
        System.out.println("Email: " + email);
        System.out.println("Mot de passe: " + password);

        showAlert(Alert.AlertType.INFORMATION, "Inscription réussie", "Votre compte a été créé avec succès !");

        handleBackToLoginLink(registerButton);
    }

    @FXML
    private void handleCancelButton() {
        System.out.println("Bouton Annuler cliqué.");
        handleBackToLoginLink(cancelButton);
    }

    @FXML
    private void handleBackToLoginLink(Button back) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UILogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - Application de Location de Véhicules");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger l'écran de connexion.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleBackToLoginLink(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UILogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registrationAnchorPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - Application de Location de Véhicules");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger l'écran de connexion.");
        }
    }
}
