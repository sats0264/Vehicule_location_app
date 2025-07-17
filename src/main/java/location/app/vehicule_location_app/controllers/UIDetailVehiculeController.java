package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import location.app.vehicule_location_app.models.Vehicule;

import java.io.InputStream;

public class UIDetailVehiculeController {

    // Champs FXML liés à la vue
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

    /**
     * Méthode pour injecter les données d’un véhicule dans les champs
     */
    public void setVehicule(Vehicule vehicule) {
        if (vehicule == null) return;

        immatriculeField.setText(vehicule.getImmatriculation());
        tarifField.setText(String.valueOf(vehicule.getTarif()));
        photoField.setText(vehicule.getPhoto() != null ? vehicule.getPhoto() : "");

        marqueComboBox.getItems().setAll(vehicule.getMarque());
        marqueComboBox.setValue(vehicule.getMarque());

        modeleComboBox.getItems().setAll(vehicule.getModele());
        modeleComboBox.setValue(vehicule.getModele());

        photoImageView.setImage(null); // Force le rafraîchissement
        try {
            // Essayer de charger depuis classpath /images/
            InputStream is = getClass().getResourceAsStream("/images/" + vehicule.getPhoto());

            Image image;
            if (is != null) {
                image = new Image(is);
            } else {
                // Si non trouvé dans les ressources, essayer d'utiliser directement l'URL dans vehicule.getPhoto()
                image = new Image(vehicule.getPhoto(), true);
            }
            photoImageView.setImage(image);
        } catch (Exception e) {
            photoImageView.setImage(null);
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }


    /**
     * Ferme la fenêtre actuelle
     */
    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }
}
