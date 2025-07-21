package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import location.app.vehicule_location_app.models.Vehicule;

import java.io.InputStream;

public class UIDetailVehiculeController {

    @FXML
    private TextField immatriculeField;

    @FXML
    private ComboBox<String> marqueComboBox;

    @FXML
    private ComboBox<String> modeleComboBox;

    @FXML
    private TextField tarifField;

    @FXML
    private TextField photoField;

    @FXML
    private ImageView photoImageView;

    @FXML
    private Button annulerButton;

    public void setVehicule(Vehicule vehicule) {
        if (vehicule == null) return;

        immatriculeField.setText(vehicule.getImmatriculation());
        tarifField.setText(String.valueOf(vehicule.getTarif()));
        photoField.setText(vehicule.getPhoto() != null ? vehicule.getPhoto() : "");

        marqueComboBox.getItems().setAll(vehicule.getMarque());
        marqueComboBox.setValue(vehicule.getMarque());

        modeleComboBox.getItems().setAll(vehicule.getModele());
        modeleComboBox.setValue(vehicule.getModele());

        photoImageView.setImage(null);
        try {
            InputStream is = getClass().getResourceAsStream(vehicule.getPhoto());

            Image image;
            if (is != null) {
                image = new Image(is);
            } else {
                image = new Image(vehicule.getPhoto(), true);
            }
            photoImageView.setImage(image);
        } catch (Exception e) {
            photoImageView.setImage(null);
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }
}
