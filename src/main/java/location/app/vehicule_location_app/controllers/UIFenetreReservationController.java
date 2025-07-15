package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Reservation;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

public class UIFenetreReservationController extends Observer {

    @FXML
    private ImageView voitureImage;
    @FXML
    private Label marqueLabel;
    @FXML
    private Label modeleLabel;
    @FXML
    private Label immatriculationLabel;
    @FXML
    private Label nbJoursLabel;
    @FXML
    private Label statutLabel;

    private Subject subject;

    /**
     * À appeler après le chargement du FXML pour afficher les infos de la réservation.
     * Le statut est mis à "En attente" par défaut.
     */
    public void setReservationDetails(String marque, String modele, String immatriculation, String imageUrl, int nbJours, Subject subject) {
        this.subject = subject;
        this.subject.attach(this);
        marqueLabel.setText("Marque : " + marque);
        modeleLabel.setText("Modèle : " + modele);
        immatriculationLabel.setText("Immatriculation : " + immatriculation);
        nbJoursLabel.setText(String.valueOf(nbJours));
        updateStatutFromState(subject.getState());
        try {
            if (imageUrl != null) {
                voitureImage.setImage(new Image(getClass().getResource(imageUrl).toExternalForm()));
            }
        } catch (Exception e) {
            voitureImage.setImage(null);
        }
    }

    /**
     * Méthode appelée automatiquement par le Subject lors d'un changement d'état.
     */
    @Override
    public void update() {
        if (subject != null) {
            updateStatutFromState(subject.getState());
        }
    }

    /**
     * Met à jour le statut selon l'état du Subject.
     */
    private void updateStatutFromState(int state) {
        switch (state) {
            case 1:
                updateStatut("Validé", "#43A047");
                break;
            case 2:
                updateStatut("Rejeté", "#e53935");
                break;
            default:
                updateStatut("En attente", "#FFA000");
        }
    }

    /**
     * Méthode utilitaire pour changer le texte et la couleur du statut.
     */
    public void updateStatut(String nouveauStatut, String color) {
        statutLabel.setText(nouveauStatut);
        statutLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }

    public void loadReservation(int reservationId, Subject subject) {
        try {
            HibernateObjectDaoImpl<Reservation> reservationDao = new HibernateObjectDaoImpl<>(Reservation.class);
            Reservation reservation = reservationDao.read(reservationId);

            this.subject = subject;
            this.subject.attach(this);

            marqueLabel.setText("Marque : " + reservation.getVehicules().get(0).getMarque());
            modeleLabel.setText("Modèle : " + reservation.getVehicules().get(0).getModele());
            immatriculationLabel.setText("Immatriculation : " + reservation.getVehicules().get(0).getImmatriculation());
            nbJoursLabel.setText(String.valueOf(
                java.time.temporal.ChronoUnit.DAYS.between(
                    reservation.getDateDebut(), reservation.getDateFin()
                )
            ));
            updateStatutFromState(subject.getState());
            try {
                String imageUrl = reservation.getVehicules().get(0).getPhoto(); // Utilise getPhoto()
                if (imageUrl != null) {
                    voitureImage.setImage(new Image(getClass().getResource(imageUrl).toExternalForm()));
                }
            } catch (Exception e) {
                voitureImage.setImage(null);
            }
        } catch (DAOException e) {
            // Gérer l'erreur (affichage, log, etc.)
        }
    }
}
