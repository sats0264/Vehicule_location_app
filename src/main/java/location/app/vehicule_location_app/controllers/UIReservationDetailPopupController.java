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
import location.app.vehicule_location_app.observer.Subject;

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
    private Label newDateDebutLabel;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private Label newDateFinLabel;
    @FXML
    private DatePicker dateFinPicker;

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
    private Vehicule vehicule;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setReservationAndVehicule(Reservation reservation, Vehicule vehicule) {
        this.reservation = reservation;
        this.vehicule = vehicule;

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

        reservationIdLabel.setText(String.valueOf(reservation.getId()));
        dateDebutDisplayLabel.setText(reservation.getDateDebut().format(dateFormatter));
        dateFinDisplayLabel.setText(reservation.getDateFin().format(dateFormatter));
        statutLabel.setText(reservation.getStatut().toString());
        updateStatutLabelStyle(reservation.getStatut());

        dateDebutPicker.setValue(reservation.getDateDebut());
        dateFinPicker.setValue(reservation.getDateFin());

        configureButtonsByStatus();
    }

    @FXML
    private void initialize() {
        closeBtn.setOnAction(event -> dialogStage.close());
        requestModificationBtn.setOnAction(event -> handleRequestModification());
        requestCancellationBtn.setOnAction(event -> handleRequestCancellation());
        directCancelBtn.setOnAction(event -> handleDirectCancel());
        saveModificationBtn.setOnAction(event -> handleSaveModification());

        newDateDebutLabel.setVisible(false);
        newDateDebutLabel.setManaged(false);
        dateDebutPicker.setVisible(false);
        dateDebutPicker.setManaged(false);
        newDateFinLabel.setVisible(false);
        newDateFinLabel.setManaged(false);
        dateFinPicker.setVisible(false);
        dateFinPicker.setManaged(false);
    }

    private void configureButtonsByStatus() {
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

    private void updateStatutLabelStyle(StatutReservation statut) {
        String style = "-fx-font-size: 16px; -fx-font-weight: bold;";
        switch (statut) {
            case EN_ATTENTE:
                style += " -fx-text-fill: #ffc107;";
                break;
            case APPROUVEE:
                style += " -fx-text-fill: #28a745;";
                break;
            case ANNULEE:
            case REJETEE:
                style += " -fx-text-fill: #dc3545;";
                break;
            case PAYEMENT_EN_ATTENTE:
                style += " -fx-text-fill: #17a2b8;";
                break;
            case MODIFICATION_EN_ATTENTE:
            case ANNULATION_EN_ATTENTE:
                style += " -fx-text-fill: #6f42c1;";
                break;
            default:
                style += " -fx-text-fill: #007bff;";
                break;
        }
        statutLabel.setStyle(style);
    }

    private void handleRequestModification() {
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

        requestModificationBtn.setVisible(false);
        requestModificationBtn.setManaged(false);
        requestCancellationBtn.setVisible(false);
        requestCancellationBtn.setManaged(false);
        directCancelBtn.setVisible(false);
        directCancelBtn.setManaged(false);

        saveModificationBtn.setVisible(true);
        saveModificationBtn.setManaged(true);
    }

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
        confirm.setTitle("Confirmer la demande de modification");
        confirm.setHeaderText("Confirmer la demande de modification pour la réservation N°" + reservation.getId() + " ?");
        confirm.setContentText("Votre demande de modification des dates du " + reservation.getDateDebut().format(dateFormatter) + " au " + reservation.getDateFin().format(dateFormatter) +
                "\nvers du " + newDateDebut.format(dateFormatter) + " au " + newDateFin.format(dateFormatter) + " sera envoyée à l'administration.");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setProposedDateDebut(newDateDebut);
                reservation.setProposedDateFin(newDateFin);

                reservation.setStatut(StatutReservation.MODIFICATION_EN_ATTENTE);
                updateObject(reservation, Reservation.class);

                Subject.getInstance().notifyAllObservers();

                Notification notificationToAdmin = new Notification(
                        "Demande de Modification Réservation",
                        "Le client " + reservation.getClient().getNom() + " " + reservation.getClient().getPrenom() +
                                " a demandé une modification pour la réservation N°" + reservation.getId() + ".\n" +
                                "Dates actuelles: Du " + reservation.getDateDebut().format(dateFormatter) + " au " + reservation.getDateFin().format(dateFormatter) + ".\n" +
                                "Nouvelles dates proposées: Du " + newDateDebut.format(dateFormatter) + " au " + newDateFin.format(dateFormatter) + ".",
                        NotificationType.RESERVATION_MODIFICATION_REQUEST,
                        reservation.getId()
                );
                NotificationService.getInstance().addNotification(notificationToAdmin);

                Notification notificationToClient = new Notification(
                        "Demande de Modification Réservation",
                        "Votre demande de modification de la réservation N°" + reservation.getId() + " a été envoyée. Un administrateur la traitera bientôt.",
                        NotificationType.CLIENT_RESERVATION_MODIFICATION_REQUEST,
                        reservation.getClient().getId()
                );
                NotificationService.getInstance().addNotificationForClient(notificationToClient, reservation.getClient());

                showAlert(Alert.AlertType.INFORMATION, "Demande enregistrée", "Votre demande de modification a été envoyée. Un administrateur la traitera bientôt.");
                dialogStage.close();
            } catch (DAOException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur de demande", "Impossible d'envoyer la demande de modification : " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void handleRequestCancellation() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la demande d'annulation");
        confirm.setHeaderText("Confirmer la demande d'annulation de la réservation N°" + reservation.getId() + " ?");
        confirm.setContentText("Votre demande sera envoyée à l'administration pour traitement.");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.ANNULATION_EN_ATTENTE);
                updateObject(reservation, Reservation.class);
                Subject.getInstance().notifyAllObservers();

                Notification notificationToAdmin = new Notification(
                        "Demande d'Annulation Réservation",
                        "Le client " + reservation.getClient().getNom() + " " + reservation.getClient().getPrenom() +
                                " a demandé l'annulation de la réservation N°" + reservation.getId() + ".",
                        NotificationType.RESERVATION_CANCELLATION_REQUEST,
                        reservation.getId()
                );
                NotificationService.getInstance().addNotification(notificationToAdmin);

                Notification notificationToClient = new Notification(
                        "Demande d'Annulation Réservation",
                        "Votre demande d'annulation de la réservation N°" + reservation.getId() + " a été envoyée. Un administrateur la traitera bientôt.",
                        NotificationType.CLIENT_RESERVATION_ANNULATION_REQUEST,
                        reservation.getClient().getId()
                );
                NotificationService.getInstance().addNotificationForClient(notificationToClient, reservation.getClient());

                showAlert(Alert.AlertType.INFORMATION, "Demande d'annulation envoyée", "Votre demande d'annulation a été envoyée. Un administrateur la traitera bientôt.");
                dialogStage.close();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur d'annulation", "Impossible d'envoyer la demande d'annulation : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

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

                Subject.getInstance().notifyAllObservers();

                Notification notificationToAdmin = new Notification(
                        "Réservation Annulée par Client",
                        "Le client " + reservation.getClient().getNom() + " " + reservation.getClient().getPrenom() +
                                " a annulé directement la réservation N°" + reservation.getId() + ".",
                        NotificationType.RESERVATION_CANCELLATION_SUCCESS,
                        reservation.getId()
                );
                NotificationService.getInstance().addNotification(notificationToAdmin);

                Notification notificationToClient = new Notification(
                        "Réservation Annulée",
                        "Votre réservation N°" + reservation.getId() + " a été annulée avec succès.",
                        NotificationType.CLIENT_RESERVATION_ANNULATION_SUCCESS,
                        reservation.getClient().getId()
                );
                NotificationService.getInstance().addNotificationForClient(notificationToClient, reservation.getClient());

                showAlert(Alert.AlertType.INFORMATION, "Réservation annulée", "La réservation N°" + reservation.getId() + " a été annulée avec succès.");
                dialogStage.close();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur d'annulation", "Impossible d'annuler la réservation : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /**
     * Loads the vehicle image with robust error handling.
     * @param imageUrl The path or URL of the image.
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
                    vehiculeImageView.setImage(new Image(imageUrl, true)); // Tries to load directly from URL
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image du véhicule: " + e.getMessage());
                vehiculeImageView.setImage(null);
            }
        } else {
            vehiculeImageView.setImage(null); // No photo defined
        }
    }

    /**
     * Displays an alert.
     * @param alertType Type of alert.
     * @param title Alert title.
     * @param message Alert message.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
