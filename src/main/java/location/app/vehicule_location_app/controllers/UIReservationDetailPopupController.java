package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Notification;
import location.app.vehicule_location_app.models.NotificationType;
import location.app.vehicule_location_app.models.Reservation;
import location.app.vehicule_location_app.models.StatutReservation;
import location.app.vehicule_location_app.models.Vehicule;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static location.app.vehicule_location_app.controllers.Controller.updateObject;

public class UIReservationDetailPopupController {

    @FXML
    private ImageView vehiculeImageView;
    @FXML
    private Label marqueLabel;
    @FXML
    private Label modeleLabel;
    @FXML
    private Label immatriculationLabel;

    @FXML
    private Label reservationIdLabel;
    @FXML
    private Label dateDebutDisplayLabel;
    @FXML
    private Label dateFinDisplayLabel;
    @FXML
    private Label statutLabel;

    @FXML
    private Label newDateDebutLabel; // Label pour "Nouvelle date début"
    @FXML
    private DatePicker dateDebutPicker; // DatePicker pour la modification
    @FXML
    private Label newDateFinLabel;   // Label pour "Nouvelle date fin"
    @FXML
    private DatePicker dateFinPicker;   // DatePicker pour la modification

    @FXML
    private Button requestModificationBtn;
    @FXML
    private Button requestCancellationBtn;
    @FXML
    private Button directCancelBtn;
    @FXML
    private Button saveModificationBtn;
    @FXML
    private Button closeBtn;

    private Stage dialogStage;
    private Reservation reservation;
    private Vehicule vehicule; // Le véhicule associé à la réservation
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Définit la scène (Stage) de ce popup.
     * @param dialogStage Le Stage de la fenêtre modale.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Initialise les données de la réservation et du véhicule dans l'interface.
     * @param reservation L'objet Reservation à afficher.
     * @param vehicule L'objet Vehicule associé à la réservation.
     */
    public void setReservationAndVehicule(Reservation reservation, Vehicule vehicule) {
        this.reservation = reservation;
        this.vehicule = vehicule;

        // Affichage des détails du véhicule
        if (vehicule != null) {
            marqueLabel.setText(vehicule.getMarque());
            modeleLabel.setText(vehicule.getModele());
            immatriculationLabel.setText(vehicule.getImmatriculation());
            loadVehiculeImage(vehicule.getPhoto());
        } else {
            marqueLabel.setText("N/A");
            modeleLabel.setText("N/A");
            immatriculationLabel.setText("N/A");
            vehiculeImageView.setImage(null);
        }

        // Affichage des détails de la réservation
        reservationIdLabel.setText(String.valueOf(reservation.getId()));
        dateDebutDisplayLabel.setText(reservation.getDateDebut().format(dateFormatter));
        dateFinDisplayLabel.setText(reservation.getDateFin().format(dateFormatter));
        statutLabel.setText(reservation.getStatut().toString());
        updateStatutLabelStyle(reservation.getStatut());

        // Initialiser les DatePickers avec les dates actuelles (cachés par défaut)
        dateDebutPicker.setValue(reservation.getDateDebut());
        dateFinPicker.setValue(reservation.getDateFin());

        // Configurer les actions et la visibilité des boutons selon le statut
        configureButtonsByStatus();
    }

    @FXML
    private void initialize() {
        closeBtn.setOnAction(event -> dialogStage.close());
        requestModificationBtn.setOnAction(event -> handleRequestModification());
        requestCancellationBtn.setOnAction(event -> handleRequestCancellation());
        directCancelBtn.setOnAction(event -> handleDirectCancel());
        saveModificationBtn.setOnAction(event -> handleSaveModification());

        // Cacher les DatePickers et leurs labels par défaut
        newDateDebutLabel.setVisible(false);
        newDateDebutLabel.setManaged(false);
        dateDebutPicker.setVisible(false);
        dateDebutPicker.setManaged(false);
        newDateFinLabel.setVisible(false);
        newDateFinLabel.setManaged(false);
        dateFinPicker.setVisible(false);
        dateFinPicker.setManaged(false);
    }

    /**
     * Configure la visibilité et l'action des boutons en fonction du statut de la réservation.
     */
    private void configureButtonsByStatus() {
        // Cacher tous les boutons d'action par défaut
        requestModificationBtn.setVisible(false);
        requestModificationBtn.setManaged(false);
        requestCancellationBtn.setVisible(false);
        requestCancellationBtn.setManaged(false);
        directCancelBtn.setVisible(false);
        directCancelBtn.setManaged(false);
        saveModificationBtn.setVisible(false);
        saveModificationBtn.setManaged(false);

        switch (reservation.getStatut()) {
            case EN_ATTENTE:
                directCancelBtn.setVisible(true);
                directCancelBtn.setManaged(true);
                requestModificationBtn.setVisible(true);
                requestModificationBtn.setManaged(true);
                break;
            case PAYEMENT_EN_ATTENTE:
                requestCancellationBtn.setVisible(true);
                requestCancellationBtn.setManaged(true);
                requestModificationBtn.setVisible(true);
                requestModificationBtn.setManaged(true);
                break;
            case APPROUVEE:
            case ANNULEE:
            case REJETEE:
            case MODIFICATION_EN_ATTENTE:
            case ANNULATION_EN_ATTENTE:
            default:
                break;
        }
    }

    /**
     * Met à jour le style du label de statut en fonction de la valeur.
     * @param statut Le statut de la réservation.
     */
    private void updateStatutLabelStyle(StatutReservation statut) {
        String style = "-fx-font-size: 16px; -fx-font-weight: bold;";
        switch (statut) {
            case EN_ATTENTE:
                style += " -fx-text-fill: #ffc107;"; // Jaune/Orange
                break;
            case APPROUVEE:
                style += " -fx-text-fill: #28a745;"; // Vert
                break;
            case ANNULEE:
            case REJETEE:
                style += " -fx-text-fill: #dc3545;"; // Rouge
                break;
            case PAYEMENT_EN_ATTENTE:
                style += " -fx-text-fill: #17a2b8;"; // Cyan/Bleu clair
                break;
            case MODIFICATION_EN_ATTENTE:
            case ANNULATION_EN_ATTENTE:
                style += " -fx-text-fill: #6f42c1;"; // Violet
                break;
            default:
                style += " -fx-text-fill: #007bff;"; // Bleu par défaut
                break;
        }
        statutLabel.setStyle(style);
    }


    /**
     * Gère la demande de modification de réservation.
     * Rend les DatePickers visibles et le bouton "Enregistrer la modification".
     */
    private void handleRequestModification() {
        // Afficher les DatePickers et cacher les labels d'affichage
        dateDebutDisplayLabel.setVisible(false);
        dateDebutDisplayLabel.setManaged(false);
        dateFinDisplayLabel.setVisible(false);
        dateFinDisplayLabel.setManaged(false);

        newDateDebutLabel.setVisible(true);
        newDateDebutLabel.setManaged(true);
        dateDebutPicker.setVisible(true);
        dateDebutPicker.setManaged(true);
        newDateFinLabel.setVisible(true);
        newDateFinLabel.setManaged(true);
        dateFinPicker.setVisible(true);
        dateFinPicker.setManaged(true);

        // Cacher les boutons de demande et afficher le bouton d'enregistrement
        requestModificationBtn.setVisible(false);
        requestModificationBtn.setManaged(false);
        requestCancellationBtn.setVisible(false);
        requestCancellationBtn.setManaged(false);
        directCancelBtn.setVisible(false);
        directCancelBtn.setManaged(false);

        saveModificationBtn.setVisible(true);
        saveModificationBtn.setManaged(true);
    }

    /**
     * Gère l'enregistrement de la modification de réservation.
     * Met à jour la réservation dans la base de données et envoie une notification.
     */
    private void handleSaveModification() {
        LocalDate newDateDebut = dateDebutPicker.getValue();
        LocalDate newDateFin = dateFinPicker.getValue();

        if (newDateDebut == null || newDateFin == null) {
            showAlert(Alert.AlertType.WARNING, "Dates invalides", "Veuillez choisir les deux dates.");
            return;
        }
        if (newDateFin.isBefore(newDateDebut)) {
            showAlert(Alert.AlertType.WARNING, "Dates invalides", "La date de fin doit être après la date de début.");
            return;
        }
        if (newDateDebut.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Date invalide", "La nouvelle date de début ne peut pas être dans le passé.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la modification");
        confirm.setHeaderText("Confirmer la modification de la réservation N°" + reservation.getId() + " ?");
        confirm.setContentText("Les dates seront changées du " + reservation.getDateDebut().format(dateFormatter) + " au " + reservation.getDateFin().format(dateFormatter) +
                "\nvers du " + newDateDebut.format(dateFormatter) + " au " + newDateFin.format(dateFormatter) + ".");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                reservation.setDateDebut(newDateDebut);
                reservation.setDateFin(newDateFin);
                reservation.setStatut(StatutReservation.MODIFICATION_EN_ATTENTE);

                updateObject(reservation, Reservation.class);

                // Envoyer une notification à l'administration
                Notification notification = new Notification(
                        "Demande de Modification Réservation",
                        "Le client " + reservation.getClient().getNom() + " " + reservation.getClient().getPrenom() +
                                " a demandé une modification pour la réservation N°" + reservation.getId() +
                                " aux dates du " + newDateDebut.format(dateFormatter) + " au " + newDateFin.format(dateFormatter) + ".",
                        NotificationType.RESERVATION_MODIFICATION_REQUEST,
                        reservation.getId()
                );
                NotificationService.getInstance().addNotification(notification);

                Notification notificationToClient = new Notification(
                        "Demande de Modification Reservation",
                        "Votre demande de modification de la réservation a été envoyée. Un administrateur la traitera bientôt.",
                        NotificationType.CLIENT_RESERVATION_MODIFICATION_REQUEST,
                        reservation.getClient().getId()
                );
                NotificationService.getInstance().addNotificationForClient(notificationToClient, reservation.getClient());

                showAlert(Alert.AlertType.INFORMATION, "Modification enregistrée", "Votre demande de modification a été envoyée. Un administrateur la traitera bientôt.");
                dialogStage.close();

            } catch (DAOException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur de modification", "Impossible d'enregistrer la modification : " + ex.getMessage());
            }
        }
    }

    /**
     * Gère la demande d'annulation de réservation (pour PAYEMENT_EN_ATTENTE).
     * Envoie une notification à l'administration.
     */
    private void handleRequestCancellation() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer l'annulation");
        confirm.setHeaderText("Confirmer la demande d'annulation de la réservation N°" + reservation.getId() + " ?");
        confirm.setContentText("Votre demande sera envoyée à l'administration pour traitement.");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                reservation.setStatut(StatutReservation.ANNULATION_EN_ATTENTE);
                updateObject(reservation, Reservation.class);

                // Envoyer une notification à l'administration
                Notification notification = new Notification(
                        "Demande d'Annulation Réservation",
                        "Le client " + reservation.getClient().getNom() + " " + reservation.getClient().getPrenom() +
                                " a demandé l'annulation de la réservation N°" + reservation.getId() + ".",
                        NotificationType.RESERVATION_CANCELLATION_REQUEST,
                        reservation.getId()
                );
                NotificationService.getInstance().addNotification(notification);

                // Envoyer une notification au client
                Notification notificationToClient = new Notification(
                        "Demande d'Annulation Réservation",
                        "Votre demande d'annulation de la réservation a été envoyée. Un administrateur la traitera bientôt.",
                        NotificationType.CLIENT_RESERVATION_ANNULATION_REQUEST,
                        reservation.getClient().getId()
                );
                NotificationService.getInstance().addNotificationForClient(notificationToClient, reservation.getClient());
                showAlert(Alert.AlertType.INFORMATION, "Demande d'annulation envoyée", "Votre demande d'annulation a été envoyée. Un administrateur la traitera bientôt.");
                dialogStage.close();

            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur d'annulation", "Impossible d'envoyer la demande d'annulation : " + e.getMessage());
            }
        }
    }

    /**
     * Gère l'annulation directe de réservation (pour EN_ATTENTE).
     * Met à jour le statut de la réservation à ANNULEE directement.
     */
    private void handleDirectCancel() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer l'annulation");
        confirm.setHeaderText("Confirmer l'annulation de la réservation N°" + reservation.getId() + " ?");
        confirm.setContentText("Cette action annulera définitivement la réservation.");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                reservation.setStatut(StatutReservation.ANNULEE);
                updateObject(reservation, Reservation.class);

                // Envoyer une notification à l'administration
                Notification notification = new Notification(
                        "Réservation Annulée par Client",
                        "Le client " + reservation.getClient().getNom() + " " + reservation.getClient().getPrenom() +
                                " a annulé directement la réservation N°" + reservation.getId() + ".",
                        NotificationType.RESERVATION_CANCELLATION_SUCCESS,
                        reservation.getId()
                );
                NotificationService.getInstance().addNotification(notification);

                // Envoyer une notification au client
                Notification notificationToClient = new Notification(
                        "Réservation Annulée",
                        "Votre réservation N°" + reservation.getId() + " a été annulée avec succès.",
                        NotificationType.CLIENT_RESERVATION_ANNULATION_SUCCESS,
                        reservation.getClient().getId()
                );
                NotificationService.getInstance().addNotificationForClient(notificationToClient, reservation.getClient());

                showAlert(Alert.AlertType.INFORMATION, "Réservation annulée", "La réservation N°" + reservation.getId() + " a été annulée avec succès.");
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Charge l'image du véhicule avec gestion d'erreur robuste.
     * @param imageUrl Le chemin ou l'URL de l'image.
     */
    private void loadVehiculeImage(String imageUrl) {
        String photoName = null;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            photoName = imageUrl;

            if (photoName.contains("/")) {
                photoName = photoName.substring(photoName.lastIndexOf('/') + 1);
            } else if (photoName.contains("\\")) {
                photoName = photoName.substring(photoName.lastIndexOf('\\') + 1);
            }

            try {
                InputStream is = getClass().getResourceAsStream("/images/" + photoName);
                if (is != null) {
                    vehiculeImageView.setImage(new Image(is));
                } else {
                    vehiculeImageView.setImage(new Image(imageUrl, true)); // Tente de charger directement l'URL
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image du véhicule: " + e.getMessage());
                vehiculeImageView.setImage(null);
            }
        } else {
            vehiculeImageView.setImage(null); // Pas de photo définie
        }
    }

    /**
     * Affiche une alerte.
     * @param alertType Type de l'alerte.
     * @param title Titre de l'alerte.
     * @param message Message de l'alerte.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
