package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Reservation;
import location.app.vehicule_location_app.models.Vehicule;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

import java.io.InputStream;
import java.util.List;

public class UIFenetreReservationController{

    @FXML
    private ImageView imageView;
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
    @FXML
    private VBox reservationsListVBox;

    private Client clientConnecte;// à initialiser lors de la connexion
    private Vehicule vehicule;

    /**
     * À appeler après le chargement du FXML pour afficher les infos de la réservation.
     * Le statut est mis à "En attente" par défaut.
     */
    public void setReservationDetails(String marque, String modele, String immatriculation, String imageUrl, int nbJours, Subject subject) {
//        this.subject = subject;
//        this.subject.attach(this);
        marqueLabel.setText("Marque : " + marque);
        modeleLabel.setText("Modèle : " + modele);
        immatriculationLabel.setText("Immatriculation : " + immatriculation);
        nbJoursLabel.setText(String.valueOf(nbJours));
//        updateStatutFromState(subject.getState());
        String photoName = null;
        // Chargement de l'image depuis ressources/images si possible
        if (vehicule.getPhoto() != null && !vehicule.getPhoto().isEmpty()) {
            photoName = vehicule.getPhoto();

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
                    imageView.setImage(image);
                } else {
                    // Si non trouvé dans ressources, essayer d'utiliser directement l'URL dans vehicule.getPhoto()
                    Image image = new Image(vehicule.getPhoto(), true);
                    imageView.setImage(image);
                }
            } catch (Exception e) {
                imageView.setImage(null);
            }
        }
    }

    /**
     * Méthode appelée automatiquement par le Subject lors d'un changement d'état.
     */
//    @Override
//    public void update() {
//        if (subject != null) {
//            updateStatutFromState(subject.getState());
//        }
//    }

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
                updateStatut("En attente", "#a5a5a3ff");
        }
    }

    /**
     * Méthode utilitaire pour changer le texte et la couleur du statut.
     */
    public void updateStatut(String nouveauStatut, String color) {
        statutLabel.setText(nouveauStatut);
        statutLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }

    /**
     * Setter à appeler AVANT affichage pour initialiser le client connecté et afficher ses réservations.
     */
    public void setClientConnecte(Client client) {
        this.clientConnecte = client;
        afficherReservationsClient();
    }

    @FXML
    private void initialize() {
        // Ne rien faire ici pour la liste, elle sera chargée après setClientConnecte
    }

    public void afficherReservationsClient() {
        reservationsListVBox.getChildren().clear();
        if (clientConnecte == null) return;

        HibernateObjectDaoImpl<Reservation> reservationDao = new HibernateObjectDaoImpl<>(Reservation.class);
        List<Reservation> reservations = reservationDao.readAll();

        for (Reservation reservation : reservations) {
            if (reservation.getClient() != null && reservation.getClient().getId() == clientConnecte.getId()) {
                Vehicule vehicule = reservation.getVehicules().isEmpty() ? null : reservation.getVehicules().get(0);
                String marque = vehicule != null ? vehicule.getMarque() : "";
                String modele = vehicule != null ? vehicule.getModele() : "";
                String immatriculation = vehicule != null ? vehicule.getImmatriculation() : "";
                String imageUrl = vehicule != null ? vehicule.getPhoto() : null;
                int nbJours = (int) java.time.temporal.ChronoUnit.DAYS.between(
                    reservation.getDateDebut(), reservation.getDateFin()
                );
                if (nbJours <= 0) nbJours = 1;

                // Crée dynamiquement la carte réservation
                VBox carte = new VBox(5);
                carte.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-background-color: #fafafa;");
                HBox hbox = new HBox(30);
                hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                // Image véhicule
                ImageView imgView = new ImageView();
                imgView.setFitHeight(120);
                imgView.setFitWidth(180);
                imgView.setPreserveRatio(true);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        String photoName = imageUrl;
                        if (photoName.contains("/")) photoName = photoName.substring(photoName.lastIndexOf('/') + 1);
                        else if (photoName.contains("\\")) photoName = photoName.substring(photoName.lastIndexOf('\\') + 1);
                        InputStream is = getClass().getResourceAsStream("/images/" + photoName);
                        if (is != null) imgView.setImage(new Image(is));
                        else imgView.setImage(new Image(imageUrl, true));
                    } catch (Exception e) {
                        imgView.setImage(null);
                    }
                }

                // Infos véhicule
                VBox infos = new VBox(8);
                infos.setAlignment(javafx.geometry.Pos.CENTER);
                Label marqueLbl = new Label("Marque : " + marque);
                marqueLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                Label modeleLbl = new Label("Modèle : " + modele);
                modeleLbl.setStyle("-fx-font-size: 14px;");
                Label immatLbl = new Label("Immatriculation : " + immatriculation);
                immatLbl.setStyle("-fx-font-size: 14px;");
                Label nbJoursLbl = new Label("Durée : " + nbJours + " jours");
                nbJoursLbl.setStyle("-fx-font-size: 13px;");
                infos.getChildren().addAll(marqueLbl, modeleLbl, immatLbl, nbJoursLbl);

                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                // Statut à droite
                VBox statutBox = new VBox(10);
                statutBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                Label statutTitre = new Label("Statut de la demande :");
                statutTitre.setStyle("-fx-font-size: 13px;");
                Label statutLbl = new Label(reservation.getStatut().toString());
                String color = "#a5a5a3ff";
                if ("VALIDE".equalsIgnoreCase(reservation.getStatut().name())) color = "#43A047";
                else if ("REJETE".equalsIgnoreCase(reservation.getStatut().name())) color = "#e53935";
                statutLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
                statutBox.getChildren().addAll(statutTitre, statutLbl);

                hbox.getChildren().addAll(imgView, infos, spacer, statutBox);
                carte.getChildren().add(hbox);

                reservationsListVBox.getChildren().add(carte);
            }
        }
    }

//    public void loadReservation(int reservationId, Subject subject) {
//        try {
//            HibernateObjectDaoImpl<Reservation> reservationDao = new HibernateObjectDaoImpl<>(Reservation.class);
//            Reservation reservation = reservationDao.read(reservationId);
//
//            this.subject = subject;
//            this.subject.attach(this);
//
//            marqueLabel.setText("Marque : " + reservation.getVehicules().get(0).getMarque());
//            modeleLabel.setText("Modèle : " + reservation.getVehicules().get(0).getModele());
//            immatriculationLabel.setText("Immatriculation : " + reservation.getVehicules().get(0).getImmatriculation());
//            nbJoursLabel.setText(String.valueOf(
//                java.time.temporal.ChronoUnit.DAYS.between(
//                    reservation.getDateDebut(), reservation.getDateFin()
//                )
//            ));
//            updateStatutFromState(subject.getState());
//            try {
//                String imageUrl = reservation.getVehicules().get(0).getPhoto(); // Utilise getPhoto()
//                if (imageUrl != null) {
//                    voitureImage.setImage(new Image(getClass().getResource(imageUrl).toExternalForm()));
//                }
//            } catch (Exception e) {
//                voitureImage.setImage(null);
//            }
//        } catch (DAOException e) {
//            // Gérer l'erreur (affichage, log, etc.)
//        }
//    }
}
