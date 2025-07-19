package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Reservation;
import location.app.vehicule_location_app.models.Vehicule;
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
                Label nbJoursLbl = new Label("Durée : " + nbJours + " jours (" +
                        reservation.getDateDebut() + " → " + reservation.getDateFin() + ")");
                nbJoursLbl.setStyle("-fx-font-size: 13px;");
                infos.getChildren().addAll(marqueLbl, modeleLbl, immatLbl, nbJoursLbl);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

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

                // Ajout du double-clic pour afficher le popup détail/modifier/annuler
                carte.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        afficherPopupReservation(reservation, vehicule);
                    }
                });

                reservationsListVBox.getChildren().add(carte);
            }
        }
    }

    /**
     * Affiche un popup avec les infos du véhicule et deux boutons : Modifier et Annuler la réservation.
     */
    private void afficherPopupReservation(Reservation reservation, Vehicule vehicule) {
        Stage popup = new Stage();
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        popup.setTitle("Détail de la réservation");

        VBox root = new VBox(18);
        root.setStyle("-fx-padding: 25; -fx-background-color: #fafafa;");
        root.setAlignment(javafx.geometry.Pos.CENTER);

        Label titre = new Label("Détail du véhicule");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Ajout de la photo du véhicule
        ImageView imgView = new ImageView();
        imgView.setFitHeight(150);
        imgView.setFitWidth(220);
        imgView.setPreserveRatio(true);
        if (vehicule != null && vehicule.getPhoto() != null && !vehicule.getPhoto().isEmpty()) {
            try {
                String photoName = vehicule.getPhoto();
                if (photoName.contains("/")) photoName = photoName.substring(photoName.lastIndexOf('/') + 1);
                else if (photoName.contains("\\")) photoName = photoName.substring(photoName.lastIndexOf('\\') + 1);
                InputStream is = getClass().getResourceAsStream("/images/" + photoName);
                if (is != null) imgView.setImage(new Image(is));
                else imgView.setImage(new Image(vehicule.getPhoto(), true));
            } catch (Exception e) {
                imgView.setImage(null);
            }
        }

        Label marque = new Label("Marque : " + (vehicule != null ? vehicule.getMarque() : ""));
        Label modele = new Label("Modèle : " + (vehicule != null ? vehicule.getModele() : ""));
        Label immat = new Label("Immatriculation : " + (vehicule != null ? vehicule.getImmatriculation() : ""));
        Label statut = new Label("Statut : " + reservation.getStatut());
        statut.setStyle("-fx-font-weight: bold;");

        // Champs de dates pour modification (cachés par défaut)
        javafx.scene.control.DatePicker dateDebutPicker = new javafx.scene.control.DatePicker();
        javafx.scene.control.DatePicker dateFinPicker = new javafx.scene.control.DatePicker();
        dateDebutPicker.setValue(reservation.getDateDebut());
        dateFinPicker.setValue(reservation.getDateFin());
        dateDebutPicker.setVisible(false);
        dateFinPicker.setVisible(false);

        Label dateDebutLabel = new Label("Nouvelle date début :");
        Label dateFinLabel = new Label("Nouvelle date fin :");
        dateDebutLabel.setVisible(false);
        dateFinLabel.setVisible(false);

        HBox boutons = new HBox(25);
        boutons.setAlignment(javafx.geometry.Pos.CENTER);


        javafx.scene.control.Button modifierBtn = new javafx.scene.control.Button("Modifier");
        javafx.scene.control.Button annulerBtn = new javafx.scene.control.Button("Annuler la réservation");
        javafx.scene.control.Button enregistrerBtn = new javafx.scene.control.Button("Enregistrer");
        enregistrerBtn.setVisible(false);

        // Action bouton Modifier
        modifierBtn.setOnAction(e -> {
            // Agrandir le popup et afficher les champs de modification
            popup.setWidth(500);
            popup.setHeight(600);
            dateDebutPicker.setVisible(true);
            dateFinPicker.setVisible(true);
            dateDebutLabel.setVisible(true);
            dateFinLabel.setVisible(true);
            enregistrerBtn.setVisible(true);
            modifierBtn.setDisable(true);
        });

        // Action bouton Enregistrer
        enregistrerBtn.setOnAction(e -> {
            if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
                statut.setText("Veuillez choisir les deux dates.");
                statut.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
                return;
            }
            if (dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
                statut.setText("La date de fin doit être après la date de début.");
                statut.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
                return;
            }
            // Met à jour la réservation dans la base
            try {
                HibernateObjectDaoImpl<Reservation> reservationDao = new HibernateObjectDaoImpl<>(Reservation.class);
                reservation.setDateDebut(dateDebutPicker.getValue());
                reservation.setDateFin(dateFinPicker.getValue());
                reservationDao.update(reservation);
                statut.setText("Modification enregistrée !");
                statut.setStyle("-fx-text-fill: #43A047; -fx-font-weight: bold;");
                enregistrerBtn.setDisable(true);
                // Fermer le popup et rafraîchir la liste principale
                popup.close();
                afficherReservationsClient();
            } catch (Exception ex) {
                statut.setText("Erreur lors de la modification.");
                statut.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
            }
        });

        // Action bouton Annuler
        annulerBtn.setOnAction(e -> {
            javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation d'annulation");
            confirm.setHeaderText("Annuler la réservation");
            confirm.setContentText("Voulez-vous vraiment annuler cette réservation ?");
            java.util.Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                try {
                    HibernateObjectDaoImpl<Reservation> reservationDao = new HibernateObjectDaoImpl<>(Reservation.class);
                    reservationDao.delete(reservation.getId());
                    statut.setText("Réservation annulée !");
                    statut.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                        javafx.application.Platform.runLater(() -> {
                            popup.close();
                            afficherReservationsClient();
                        });
                    }).start();
                } catch (Exception ex) {
                    statut.setText("Erreur lors de l'annulation.");
                    statut.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
                }
            }
            // Sinon, ne rien faire, on reste sur le popup
        });

        boutons.getChildren().addAll(modifierBtn, enregistrerBtn, annulerBtn);

        root.getChildren().addAll(
            titre, imgView, marque, modele, immat, statut,
            dateDebutLabel, dateDebutPicker,
            dateFinLabel, dateFinPicker,
            boutons
        );

        Scene scene = new Scene(root, 480, 520);
        popup.setScene(scene);
        popup.showAndWait();
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
