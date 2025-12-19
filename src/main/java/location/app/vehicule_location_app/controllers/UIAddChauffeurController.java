package location.app.vehicule_location_app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Chauffeur;
import location.app.vehicule_location_app.observer.Subject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static location.app.vehicule_location_app.controllers.Controller.ajouterObject;

public class UIAddChauffeurController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField photoField;
    @FXML
    private ImageView photoImageView;
    @FXML
    private Button enregistrerButton;
    @FXML
    private Button annulerButton;
    @FXML
    private Button uploadPhotoButton;
    private File selectedPhotoFile;

    @FXML
    private void initialize() {
        clearForm();
    }
    @FXML
    public void handleEnregistrer(ActionEvent actionEvent) {
        try {
            Chauffeur chauffeur = getChauffeur();

            ajouterObject(chauffeur, Chauffeur.class);
            Controller.refreshChauffeurs();
            Subject.getInstance().notifyAllObservers();


            showAlert(Alert.AlertType.INFORMATION, "Chauffeur ajouté", "Le chauffeur a été ajouté avec succès.");
            clearForm();
            Stage stage = (Stage) enregistrerButton.getScene().getWindow();
            stage.close();
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'enregistrement", e.getMessage());
        }
    }

    private Chauffeur getChauffeur() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String telephone = telephoneField.getText();

        String cheminPhoto = (selectedPhotoFile != null)
                ? "/images/" + selectedPhotoFile.getName()
                : "/images/car_logo.jpg";

        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return null;
        }


        return new Chauffeur(nom, prenom, telephone, cheminPhoto);
    }
    @FXML
    public void handleAnnuler(ActionEvent actionEvent) {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                Path targetDir = Paths.get("src/main/resources/images");
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }

                Path targetPath = targetDir.resolve(file.getName());
                Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                selectedPhotoFile = new File(file.getName());
                photoField.setText(file.getName());

                Image image = new Image(targetPath.toUri().toString());
                photoImageView.setImage(image);

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la copie de la photo : " + e.getMessage());
            }
        }
    }

    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        telephoneField.clear();
        photoField.clear();
        photoImageView.setImage(null);
        selectedPhotoFile = null;
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
