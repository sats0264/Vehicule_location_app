package location.app.vehicule_location_app.controllers;

import static location.app.vehicule_location_app.controllers.Controller.ajouterObject;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.CarteBancaire;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.observer.Subject;

public class UIAddCardController {

    @FXML
    private TextField titulaireField;
    @FXML
    private TextField numeroCarteField;
    @FXML
    private TextField cvcField;
    @FXML
    private TextField expirationField;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonCancel;

    private Client clientConnecte;

    public void setClientConnecte(Client client) {
        this.clientConnecte = client;
        System.out.println("[DEBUG] Client connecté reçu dans UIAddCardController : " + 
            (client != null ? "id=" + client.getId() + ", nom=" + client.getNom() : "null"));
    }

    @FXML
    private void initialize() {
        // Numéro de carte : formatage et blocage à 16 chiffres
        numeroCarteField.textProperty().addListener((obs, oldText, newText) -> {
            String digitsOnly = newText.replaceAll("\\D", "");
            if (digitsOnly.length() > 16) {
                digitsOnly = digitsOnly.substring(0, 16);
            }
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digitsOnly.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    formatted.append(" ");
                }
                formatted.append(digitsOnly.charAt(i));
            }
            if (!newText.equals(formatted.toString())) {
                numeroCarteField.setText(formatted.toString());
            }
        });

        // CVC : blocage à 3 chiffres
        cvcField.textProperty().addListener((obs, oldText, newText) -> {
            String digitsOnly = newText.replaceAll("\\D", "");
            if (digitsOnly.length() > 3) {
                digitsOnly = digitsOnly.substring(0, 3);
            }
            if (!newText.equals(digitsOnly)) {
                cvcField.setText(digitsOnly);
            }
        });

        // Expiration : ajout automatique du slash après 2 chiffres (MM/AA)
        expirationField.textProperty().addListener((obs, oldText, newText) -> {
            String digitsOnly = newText.replaceAll("\\D", "");
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digitsOnly.length() && i < 4; i++) {
                if (i == 2) {
                    formatted.append("/");
                }
                formatted.append(digitsOnly.charAt(i));
            }
            if (!newText.equals(formatted.toString())) {
                expirationField.setText(formatted.toString());
            }
        });
    }

    @FXML
    private void handleAdd() {
        String titulaire = titulaireField.getText();
        String numeroCarte = numeroCarteField.getText().replaceAll(" ", "");
        String cvcStr = cvcField.getText();
        String expiration = expirationField.getText();

        if (titulaire.isEmpty() || numeroCarte.isEmpty() || cvcStr.isEmpty() || expiration.isEmpty()) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        // Vérification du format du numéro de carte (16 chiffres minimum)
        if (!numeroCarte.matches("\\d{16,}")) {
            showAlert("Le numéro de carte doit contenir au moins 16 chiffres.");
            return;
        }

        // Vérification du format du CVC (3 chiffres)
        if (!cvcStr.matches("\\d{3}")) {
            showAlert("Le CVC doit comporter 3 chiffres.");
            return;
        }

        // Vérifie que le client connecté a bien un id
        if (clientConnecte == null || clientConnecte.getId() == 0) {
            showAlert("Erreur : client non reconnu. Veuillez vous reconnecter.");
            return;
        }

        CarteBancaire carte = new CarteBancaire(
            titulaire,
            numeroCarteField.getText(),
            Integer.parseInt(cvcStr),
            expiration,
            clientConnecte,
            0.0
        );
        try {
            ajouterObject(carte,CarteBancaire.class);
            Subject.getInstance().notifyAllObservers();
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        showAlert("Carte ajoutée avec succès !");
        closeWindow();
    }

    @FXML
    private void handleAnnuler() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Annulation");
        alert.setHeaderText("Voulez-vous vraiment annuler l'ajout de la carte ?");
        alert.setContentText("Vous pouvez ajouter une carte plus tard depuis votre portefeuille.");
        alert.showAndWait();
        closeWindow();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) buttonAdd.getScene().getWindow();
        stage.close();
    }

}
