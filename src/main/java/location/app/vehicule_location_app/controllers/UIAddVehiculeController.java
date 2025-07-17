package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Vehicule;
import location.app.vehicule_location_app.observer.DashboardSubject;
import location.app.vehicule_location_app.observer.VehiculeSubject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static location.app.vehicule_location_app.controllers.Controller.ajouterObject;

public class UIAddVehiculeController{

    @FXML private TextField immatriculeField;
    @FXML private ComboBox<String> marqueComboBox;
    @FXML private ComboBox<String> modeleComboBox;
    @FXML private TextField tarifField;
    @FXML private TextField photoField;
    @FXML private ImageView photoImageView;

    @FXML private Button enregistrerButton;
    @FXML private Button annulerButton;
    @FXML private Button uploadPhotoButton;

    private File selectedPhotoFile;

    public UIAddVehiculeController() {
    }

    @FXML
    public void initialize() {
        // Exemple : Pré-remplissage des marques et modèles
        marqueComboBox.getItems().addAll("Renault", "Peugeot", "Citroen", "Toyota", "Hyundai","Mercedes", "BMW", "Audi", "Volkswagen");
        modeleComboBox.getItems().addAll("Clio", "208", "C3", "Yaris", "i20", "i30", "Tucson", "A-Class", "3 Series", "A4", "Golf");

        // Comportement de sélection de la marque : filtrer les modèles si besoin
        marqueComboBox.setOnAction(e -> {
            String selected = marqueComboBox.getValue();
            modeleComboBox.getItems().clear();
            if (selected == null) return;

            switch (selected) {
                case "Renault" -> modeleComboBox.getItems().addAll("Clio", "Megane", "Captur", "Koleos");
                case "Peugeot" -> modeleComboBox.getItems().addAll("208", "3008", "5008", "Partner");
                case "Citroen" -> modeleComboBox.getItems().addAll("C3", "C4", "Berlingo", "C5 Aircross");
                case "Toyota" -> modeleComboBox.getItems().addAll("Yaris", "Corolla", "Rav4", "C-HR", "Hilux");
                case "Hyundai" -> modeleComboBox.getItems().addAll("i20", "i30", "Tucson", "Kona", "Santa Fe", "i10");
                case "Mercedes" -> modeleComboBox.getItems().addAll("A-Class", "C-Class", "E-Class", "GLA", "GLC");
                case "BMW" -> modeleComboBox.getItems().addAll("1 Series", "3 Series", "5 Series", "X1", "X3");
                case "Audi" -> modeleComboBox.getItems().addAll("A1", "A3", "A4", "Q2", "Q3");
                case "Volkswagen" -> modeleComboBox.getItems().addAll("Golf", "Polo", "Tiguan", "Passat", "T-Roc");
                default -> modeleComboBox.getItems().add("Modèle inconnu");
            }
        });
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

                // On stocke uniquement le chemin classpath pour l'image
                selectedPhotoFile = new File(file.getName());
                photoField.setText(file.getName());

                // Chargement de l'image via classpath (recommandé)
                Image image = new Image(targetPath.toUri().toString());
                photoImageView.setImage(image);

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la copie de la photo : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEnregistrer() {
        try {
            Vehicule v = getVehicule();

            ajouterObject(v, Vehicule.class);
            DashboardSubject.getInstance().notifyAllObservers();
            VehiculeSubject.getInstance().notifyAllObservers();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Véhicule enregistré avec succès !");
            clearForm();
            Stage stage = (Stage) enregistrerButton.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le tarif doit être un nombre valide.");
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'enregistrement", e.getMessage());
        }
    }

    private Vehicule getVehicule() {
        String immatricule = immatriculeField.getText();
        String marque = marqueComboBox.getValue();
        String modele = modeleComboBox.getValue();
        double tarif = Double.parseDouble(tarifField.getText());

        String cheminPhoto = (selectedPhotoFile != null)
                ? "/images/" + selectedPhotoFile.getName()
                : "/images/car_logo.jpg";
        if (immatricule.isEmpty() || marque == null || modele == null || tarifField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return null;
        }

        return new Vehicule(immatricule, marque, modele, tarif, cheminPhoto);
    }

    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    private void clearForm() {
        immatriculeField.clear();
        if (marqueComboBox.getSelectionModel() != null)
            marqueComboBox.getSelectionModel().clearSelection();
        if (modeleComboBox.getSelectionModel() != null)
            modeleComboBox.getSelectionModel().clearSelection();
        tarifField.clear();
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
