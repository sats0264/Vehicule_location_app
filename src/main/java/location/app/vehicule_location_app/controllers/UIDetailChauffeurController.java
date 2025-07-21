package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import location.app.vehicule_location_app.models.Chauffeur;

import java.io.InputStream;

public class UIDetailChauffeurController {

    public Label photoLabel;
    @FXML
    private Label nomLabel;
    @FXML
    private Label prenomLabel;
    @FXML
    private Label telephoneLabel;
    @FXML
    private Label statutLabel;
    @FXML
    private ImageView photoImageView;

    public void setChauffeur(Chauffeur chauffeur) {

        if (chauffeur != null) {
            nomLabel.setText(chauffeur.getNom());
            prenomLabel.setText(chauffeur.getPrenom());
            telephoneLabel.setText(chauffeur.getTelephone());
            statutLabel.setText(chauffeur.getStatut().toString());
            photoLabel.setText(chauffeur.getPhoto() != null ? chauffeur.getPhoto() : "Aucune photo disponible");

            photoImageView.setImage(null);
            try {
                InputStream is = getClass().getResourceAsStream(chauffeur.getPhoto());

                Image image;
                if (is != null) {
                    image = new Image(is);
                } else {
                    image = new Image(chauffeur.getPhoto(), true);
                }
                photoImageView.setImage(image);
            } catch (Exception e) {
                photoImageView.setImage(null);
                System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) nomLabel.getScene().getWindow();
        stage.close();
    }
}
