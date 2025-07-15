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

        // Chargement de l'image depuis ressources/images si possible
        if (vehicule.getPhoto() != null && !vehicule.getPhoto().isEmpty()) {
            String photoName = vehicule.getPhoto();

            // Extraire le nom de fichier si c'est un chemin complet (optionnel)
            if (photoName.contains("/")) {
                photoName = photoName.substring(photoName.lastIndexOf('/') + 1);
            } else if (photoName.contains("\\")) {
                photoName = photoName.substring(photoName.lastIndexOf('\\') + 1);
            }

            try {
                // Essayer de charger depuis classpath /images/
                InputStream is = getClass().getResourceAsStream("/images/" + photoName);

                if (is != null) {
                    Image image = new Image(is);
                    photoImageView.setImage(image);
                } else {
                    // Si non trouvé dans ressources, essayer d'utiliser directement l'URL dans vehicule.getPhoto()
                    Image image = new Image(vehicule.getPhoto(), true);
                    photoImageView.setImage(image);
                }
            } catch (Exception e) {
                photoImageView.setImage(null);
            }
        } else {
            photoImageView.setImage(null);
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
