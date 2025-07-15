package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Reservation;
import location.app.vehicule_location_app.models.StatutReservation;
import location.app.vehicule_location_app.models.Vehicule;
import location.app.vehicule_location_app.models.Client;

import java.time.LocalDate;

public class UIClientReservationController {

    @FXML
    private ImageView voitureImage;
    @FXML
    private Label marqueLabel;
    @FXML
    private Label modeleLabel;
    @FXML
    private Label immatriculationLabel;
    @FXML
    private Label typeReservationLabel;
    @FXML
    private Label prixLabel;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private Button validerBtn;
    @FXML
    private Button annulerBtn;

    private Stage getStage() {
        return (Stage) validerBtn.getScene().getWindow();
    }

    public void initReservation(String marque, String modele, String immatriculation, String imageUrl, boolean avecChauffeur) {
        marqueLabel.setText("Marque : " + marque);
        modeleLabel.setText("Modèle : " + modele);
        immatriculationLabel.setText("Immatriculation : " + immatriculation);
        typeReservationLabel.setText(avecChauffeur ? "Avec chauffeur" : "Sans chauffeur");
        prixLabel.setText(avecChauffeur ? "Prix : 25 000 FCFA / jour" : "Prix : 18 000 FCFA / jour");
        try {
            if (imageUrl != null) {
                voitureImage.setImage(new Image(getClass().getResource(imageUrl).toExternalForm()));
            }
        } catch (Exception e) {
            voitureImage.setImage(null); // ou une image par défaut
        }
    }

    private Vehicule vehicule; // à setter lors de l'ouverture de la popup
    private Client client;     // à setter lors de l'ouverture de la popup
    private int reservationId; // pour récupérer l'ID après création

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public int getReservationId() {
        return reservationId;
    }

    @FXML
    private void initialize() {
        validerBtn.setOnAction(e -> {
            try {
                // Vérification que le véhicule et le client sont bien définis
                if (vehicule == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Aucun véhicule sélectionné !");
                    alert.showAndWait();
                    return;
                }
                if (client == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Aucun client sélectionné !");
                    alert.showAndWait();
                    return;
                }
                // Création de la réservation
                Reservation reservation = new Reservation();
                reservation.addVehicule(vehicule);
                reservation.setClient(client);
                reservation.setDateDebut(dateDebutPicker.getValue());
                reservation.setDateFin(dateFinPicker.getValue());
                reservation.setStatut(StatutReservation.EN_ATTENTE);

                HibernateObjectDaoImpl<Reservation> reservationDao =
                        new HibernateObjectDaoImpl<>(Reservation.class);
                reservationDao.create(reservation);

                // L'ID est maintenant disponible via reservation.getId()
                reservationId = reservation.getId();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Réservation");
                alert.setHeaderText(null);
                alert.setContentText("Réservation validée !");
                alert.showAndWait();
                getStage().close();
            } catch (DAOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement : " + ex.getMessage());
                alert.showAndWait();
            }
        });

        annulerBtn.setOnAction(e -> getStage().close());
    }
}
