package location.app.vehicule_location_app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import location.app.vehicule_location_app.dao.NotificationService; // Assuming this exists
import location.app.vehicule_location_app.exceptions.DAOException; // Assuming this exists
import location.app.vehicule_location_app.models.*; // Import all necessary models
import location.app.vehicule_location_app.observer.Subject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static location.app.vehicule_location_app.controllers.Controller.updateObject;

// Assuming Controller is a base class that provides updateObject and other common methods
// If Controller class is not available or does not have updateObject,
// you might need to implement a DAO or pass a service here.
// For this example, I'll assume `updateObject` is accessible (e.g., as a static method or via injection).

public class UIInspectReservationController {

    private Reservation reservation;
    // --- Champs Client ---
    @FXML
    private Label clientLastNameField;
    @FXML
    private Label clientFirstNameField;
    @FXML
    private Label clientPhoneField;
    @FXML
    private Label clientEmailField;
    @FXML
    private Label clientFideliteField;

    // --- Champs Véhicule ---
    @FXML
    private ComboBox<String> voitureImmatriculationField;
    @FXML
    private Label voitureModeleField;
    @FXML
    private Label voitureMarqueField;
    @FXML
    private Label voitureTarifField;
    @FXML
    private Label voitureStatutField;

    // --- Champs Chauffeur ---
    @FXML
    private ComboBox<Integer> chauffeurIdComboBox;
    @FXML
    private Label chauffeurLastNameField;
    @FXML
    private Label chauffeurFirstNameField;
    @FXML
    private Label chauffeurStatutField;

    // --- Champs Durée et Montant ---
    @FXML
    private DatePicker startDatePicker; // Now a DatePicker
    @FXML
    private DatePicker startDateModificationPicker;
    @FXML
    private DatePicker endDatePicker;   // Now a DatePicker
    @FXML
    private DatePicker endDateModificationPicker;
    @FXML
    private Label durationField;
    @FXML
    private Label amountField;
    @FXML
    private Label currentDateLabel;
    @FXML
    private Label proposedDateLabel;


    // --- Boutons d'action principaux ---
    @FXML
    private Button approuverButton; // For EN_ATTENTE -> PAYEMENT_EN_ATTENTE
    @FXML
    private Button rejeterButton;   // For EN_ATTENTE -> REJETEE
    @FXML
    private Button fermerButton;

    // --- New buttons for requests ---
    @FXML
    private Button approuverModificationBtn;
    @FXML
    private Button rejeterModificationBtn;
    @FXML
    private Button approuverAnnulationBtn;
    @FXML
    private Button rejeterAnnulationBtn;

    // Removed proposed date fields and their container as they are not in the provided FXML
    // @FXML private Label proposedStartDateLabel;
    // @FXML private Label proposedEndDateLabel;
    // @FXML private VBox modificationRequestDetailsBox;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Initialize listeners for buttons
        approuverButton.setOnAction(e -> handleApprouver());
        rejeterButton.setOnAction(e -> handleRejeter());
        fermerButton.setOnAction(this::handleFermer);

        approuverModificationBtn.setOnAction(e -> handleApprouverModification());
        rejeterModificationBtn.setOnAction(e -> handleRejeterModification());
        approuverAnnulationBtn.setOnAction(e -> handleApprouverAnnulation());
        rejeterAnnulationBtn.setOnAction(e -> handleRejeterAnnulation());

        // DatePickers are disabled and non-editable for inspection as per FXML
        startDatePicker.setDisable(true);
        startDatePicker.setEditable(false);
        endDatePicker.setDisable(true);
        endDatePicker.setEditable(false);

        // Ensure all request buttons are hidden by default
        hideAllRequestButtons();

        // No modificationRequestDetailsBox in this FXML, so no need to hide/manage it here.
    }

    /**
     * Hides all buttons related to modification/cancellation requests.
     */
    private void hideAllRequestButtons() {
        approuverModificationBtn.setVisible(false);
        approuverModificationBtn.setManaged(false);
        rejeterModificationBtn.setVisible(false);
        rejeterModificationBtn.setManaged(false);
        approuverAnnulationBtn.setVisible(false);
        approuverAnnulationBtn.setManaged(false);
        rejeterAnnulationBtn.setVisible(false);
        rejeterAnnulationBtn.setManaged(false);
        approuverButton.setVisible(false); // Hide initial buttons as well
        approuverButton.setManaged(false);
        rejeterButton.setVisible(false);
        rejeterButton.setManaged(false);
    }

    /**
     * Sets the reservation to inspect and updates the interface.
     * Enables/disables action buttons based on status.
     * @param reservation The Reservation object to display.
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;

        if (reservation == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune réservation à afficher.");
            return;
        }

        // --- Client ---
        Client c = reservation.getClient();
        if (c != null) {
            clientFirstNameField.setText(c.getPrenom());
            clientLastNameField.setText(c.getNom());
            clientPhoneField.setText(c.getTelephone());
            clientEmailField.setText(c.getEmail());
            clientFideliteField.setText(String.valueOf(c.getPointFidelite()));
        } else {
            // Clear client fields if no client
            clientFirstNameField.setText("N/A");
            clientLastNameField.setText("N/A");
            clientPhoneField.setText("N/A");
            clientEmailField.setText("N/A");
            clientFideliteField.setText("N/A");
        }

        // --- Vehicles ---
        if (reservation.getVehicules() != null && !reservation.getVehicules().isEmpty()) {
            voitureImmatriculationField.getItems().setAll(
                    reservation.getVehicules().stream().map(Vehicule::getImmatriculation).toList()
            );
            Vehicule firstVehicule = reservation.getVehicules().getFirst();
            voitureImmatriculationField.setValue(firstVehicule.getImmatriculation());
            updateVehiculeDetails(firstVehicule);

            voitureImmatriculationField.setOnAction(event -> {
                String selectedImmatriculation = voitureImmatriculationField.getValue();
                Vehicule selectedVehicule = reservation.getVehicules()
                        .stream()
                        .filter(ve -> ve.getImmatriculation().equals(selectedImmatriculation))
                        .findFirst()
                        .orElse(null);
                if (selectedVehicule != null) {
                    updateVehiculeDetails(selectedVehicule);
                }
            });
        } else {
            voitureImmatriculationField.getItems().clear();
            voitureImmatriculationField.setPromptText("Aucun véhicule");
            voitureModeleField.setText("N/A");
            voitureMarqueField.setText("N/A");
            voitureTarifField.setText("N/A");
            voitureStatutField.setText("N/A");
        }

        // --- Chauffeur ---
        if (reservation.getChauffeurs() != null && !reservation.getChauffeurs().isEmpty()) {
            chauffeurIdComboBox.getItems().setAll(
                    reservation.getChauffeurs().stream().map(Chauffeur::getId).toList()
            );
            Chauffeur firstChauffeur = reservation.getChauffeurs().getFirst();
            chauffeurIdComboBox.setValue(firstChauffeur.getId());
            updateChauffeurDetails(firstChauffeur);

            chauffeurIdComboBox.setOnAction(event -> {
                Integer selectedId = chauffeurIdComboBox.getValue();
                Chauffeur selectedChauffeur = reservation.getChauffeurs()
                        .stream()
                        .filter(cha -> cha.getId() == selectedId)
                        .findFirst()
                        .orElse(null);
                if (selectedChauffeur != null) {
                    updateChauffeurDetails(selectedChauffeur);
                }
            });
        } else {
            chauffeurIdComboBox.setVisible(false);
            chauffeurIdComboBox.setManaged(false);
            chauffeurLastNameField.setText("N/A");
            chauffeurFirstNameField.setText("N/A");
            chauffeurStatutField.setText("N/A");
        }

        // --- Dates ---
        startDatePicker.setValue(reservation.getDateDebut());
        endDatePicker.setValue(reservation.getDateFin());

        // Cacher les DatePicker de modification par défaut
        startDateModificationPicker.setVisible(false);
        startDateModificationPicker.setManaged(false);
        endDateModificationPicker.setVisible(false);
        endDateModificationPicker.setManaged(false);

        // --- Duration ---
        long duration = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
        durationField.setText(duration + " jour(s)");

        // --- Amount ---
        calculateAndDisplayAmount(duration);

        // --- Configure button visibility based on status ---
        configureButtonsBasedOnStatus();

//        // Display proposed modification dates if status is MODIFICATION_EN_ATTENTE
//        // NOTE: The FXML provided does not have fields for proposed dates.
//        // If you want to display them, you need to add them back to the FXML.
//        // For now, this logic is removed as the FXML elements are missing.
//        if (reservation.getStatut() == StatutReservation.MODIFICATION_EN_ATTENTE) {
//            // Since proposed date labels are removed from FXML,
//            // we can display them in an Alert or log for now.
//            if (reservation.getProposedDateDebut() != null && reservation.getProposedDateFin() != null) {
//                showAlert(Alert.AlertType.INFORMATION, "Demande de Modification",
//                        "Dates actuelles: Du " + reservation.getDateDebut().format(dateFormatter) + " au " + reservation.getDateFin().format(dateFormatter) + "\n" +
//                                "Dates proposées par le client: Du " + reservation.getProposedDateDebut().format(dateFormatter) + " au " + reservation.getProposedDateFin().format(dateFormatter));
//            }
//        }
        // Si statut = MODIFICATION_EN_ATTENTE, on affiche les dates proposées
        if (reservation.getStatut() == StatutReservation.MODIFICATION_EN_ATTENTE) {
            if (reservation.getProposedDateDebut() != null && reservation.getProposedDateFin() != null) {
                startDateModificationPicker.setValue(reservation.getProposedDateDebut());
                endDateModificationPicker.setValue(reservation.getProposedDateFin());
                startDateModificationPicker.setVisible(true);
                startDateModificationPicker.setManaged(true);
                endDateModificationPicker.setVisible(true);
                endDateModificationPicker.setManaged(true);
            }
        }
    }

    /**
     * Updates the vehicle information labels.
     * @param vehicule The selected vehicle.
     */
    private void updateVehiculeDetails(Vehicule vehicule) {
        voitureModeleField.setText(vehicule.getModele());
        voitureMarqueField.setText(vehicule.getMarque());
        voitureTarifField.setText(String.valueOf(vehicule.getTarif()) + " FCFA");
        voitureStatutField.setText(vehicule.getStatut().toString());
    }

    /**
     * Updates the chauffeur information labels.
     * @param chauffeur The selected chauffeur.
     */
    private void updateChauffeurDetails(Chauffeur chauffeur) {
        chauffeurLastNameField.setText(chauffeur.getNom());
        chauffeurFirstNameField.setText(chauffeur.getPrenom());
        chauffeurStatutField.setText(chauffeur.getStatut().toString());
    }

    /**
     * Calculates and displays the total amount of the reservation.
     * @param duration The number of days of the reservation.
     */
    private void calculateAndDisplayAmount(long duration) {
        double vehiculeTotalTarif = reservation.getVehicules().stream()
                .mapToDouble(Vehicule::getTarif)
                .sum();
        double montantTotal = vehiculeTotalTarif * duration;

        if (reservation.getChauffeurs() != null && !reservation.getChauffeurs().isEmpty()) {
            double chauffeurDailyFee = 7000.0; // Define as a constant if not already
            montantTotal += reservation.getChauffeurs().size() * chauffeurDailyFee * duration;
        }
        amountField.setText(String.format("%.0f FCFA", montantTotal));
    }

    /**
     * Configures the visibility of buttons based on the reservation status.
     */
    private void configureButtonsBasedOnStatus() {
        hideAllRequestButtons(); // Hide everything first

        StatutReservation statut = reservation.getStatut();
        switch (statut) {
            case EN_ATTENTE:
                approuverButton.setVisible(true);
                approuverButton.setManaged(true);
                rejeterButton.setVisible(true);
                rejeterButton.setManaged(true);
                break;
            case MODIFICATION_EN_ATTENTE:
                approuverModificationBtn.setVisible(true);
                approuverModificationBtn.setManaged(true);
                rejeterModificationBtn.setVisible(true);
                rejeterModificationBtn.setManaged(true);
                break;
            case ANNULATION_EN_ATTENTE:
                approuverAnnulationBtn.setVisible(true);
                approuverAnnulationBtn.setManaged(true);
                rejeterAnnulationBtn.setVisible(true);
                rejeterAnnulationBtn.setManaged(true);
                break;
            default:
                break;
        }
    }


    // --- Handle button actions ---

    @FXML
    private void handleApprouver() {
        // Logic to approve the initial reservation (EN_ATTENTE -> PAYEMENT_EN_ATTENTE)
        Optional<ButtonType> result = showAlertConfirmation("Approuver la Réservation", "Voulez-vous vraiment approuver cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.PAYEMENT_EN_ATTENTE);
                // Update status of associated vehicles and chauffeurs to UNAVAILABLE
                updateAssociatedEntitiesStatus(Statut.INDISPONIBLE);
                updateObject(reservation, Reservation.class);

                // Notifications
                sendReservationStatusNotification(
                        "Réservation Approuvée",
                        "La réservation N°" + reservation.getId() + " a été approuvée.",
                        NotificationType.RESERVATION_CONFIRMATION
                );
                sendReservationStatusNotificationForClient(
                        "Réservation Approuvée",
                        "Votre réservation du " + reservation.getDateDebut().format(dateFormatter) + " au " + reservation.getDateFin().format(dateFormatter) + " a été approuvée.\nVeuillez procéder au paiement pour finaliser la réservation.",
                        NotificationType.CLIENT_RESERVATION_CONFIRMATION,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation approuvée et en attente de paiement.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'approbation de la réservation : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRejeter() {
        // Logic to reject the initial reservation (EN_ATTENTE -> REJETEE)
        Optional<ButtonType> result = showAlertConfirmation("Rejeter la Réservation", "Voulez-vous vraiment rejeter cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.REJETEE);
                // Set status of associated vehicles and chauffeurs back to AVAILABLE
                updateAssociatedEntitiesStatus(Statut.DISPONIBLE);
                updateObject(reservation, Reservation.class);

                // Notifications
                sendReservationStatusNotification(
                        "Réservation Rejetée",
                        "La réservation N°" + reservation.getId() + " a été rejetée.",
                        NotificationType.RESERVATION_REFUSED
                );
                sendReservationStatusNotificationForClient(
                        "Réservation Rejetée",
                        "Votre réservation du " + reservation.getDateDebut().format(dateFormatter) + " au " + reservation.getDateFin().format(dateFormatter) + " a été rejetée.",
                        NotificationType.CLIENT_RESERVATION_ANNULATION_REFUSED,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation rejetée.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du rejet de la réservation : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleApprouverModification() {
        // Logic to approve a modification request (MODIFICATION_EN_ATTENTE -> CONFIRMEE or previous status)
        Optional<ButtonType> result = showAlertConfirmation("Approuver la Modification", "Voulez-vous vraiment approuver la modification de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Apply the proposed new dates to the reservation object
                // These dates are stored in the Reservation object from the client's request
                LocalDate approvedStartDate = reservation.getProposedDateDebut();
                LocalDate approvedEndDate = reservation.getProposedDateFin();

                if (approvedStartDate == null || approvedEndDate == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Dates de modification proposées introuvables.");
                    return;
                }

                reservation.setDateDebut(approvedStartDate);
                reservation.setDateFin(approvedEndDate);
                // Clear proposed dates after approval
                reservation.setProposedDateDebut(null);
                reservation.setProposedDateFin(null);

                // Set status back to a relevant active state, e.g., PAYEMENT_EN_ATTENTE or CONFIRMEE
                reservation.setStatut(StatutReservation.PAYEMENT_EN_ATTENTE);
                updateObject(reservation, Reservation.class);

                Subject.getInstance().notifyAllObservers();

                // Notifications
                sendReservationStatusNotification(
                        "Modification de Réservation Approuvée",
                        "La demande de modification pour la réservation N°" + reservation.getId() + " a été approuvée. Nouvelles dates: Du " + approvedStartDate.format(dateFormatter) + " au " + approvedEndDate.format(dateFormatter) + ".",
                        NotificationType.RESERVATION_MODIFICATION_SUCCESS
                );
                sendReservationStatusNotificationForClient(
                        "Modification Approuvée",
                        "Votre demande de modification pour la réservation N°" + reservation.getId() + " a été approuvée. Nouvelles dates: Du " + approvedStartDate.format(dateFormatter) + " au " + approvedEndDate.format(dateFormatter) + ".",
                        NotificationType.CLIENT_RESERVATION_MODIFICATION_SUCCESS,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Modification de réservation approuvée.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'approbation de la modification : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRejeterModification() {
        // Logic to reject a modification request (MODIFICATION_EN_ATTENTE -> previous status)
        Optional<ButtonType> result = showAlertConfirmation("Rejeter la Modification", "Voulez-vous vraiment rejeter la modification de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Revert the reservation status to its previous state (e.g., PAYEMENT_EN_ATTENTE or CONFIRMEE)
                reservation.setStatut(StatutReservation.PAYEMENT_EN_ATTENTE); // Or the status before modification request
                // Clear proposed dates as modification is rejected
                reservation.setProposedDateDebut(null);
                reservation.setProposedDateFin(null);

                updateObject(reservation, Reservation.class);

                Subject.getInstance().notifyAllObservers();

                // Notifications
                sendReservationStatusNotification(
                        "Modification de Réservation Rejetée",
                        "La demande de modification pour la réservation N°" + reservation.getId() + " a été rejetée.",
                        NotificationType.RESERVATION_MODIFICATION_REFUSED
                );
                sendReservationStatusNotificationForClient(
                        "Modification Rejetée",
                        "Votre demande de modification pour la réservation N°" + reservation.getId() + " a été rejetée.",
                        NotificationType.CLIENT_RESERVATION_MODIFICATION_REFUSED,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Modification de réservation rejetée.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du rejet de la modification : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleApprouverAnnulation() {
        // Logic to approve a cancellation request (ANNULATION_EN_ATTENTE -> ANNULEE)
        Optional<ButtonType> result = showAlertConfirmation("Approuver l'Annulation", "Voulez-vous vraiment approuver l'annulation de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.ANNULEE);
                // Set status of associated vehicles and chauffeurs back to AVAILABLE
                updateAssociatedEntitiesStatus(Statut.DISPONIBLE);
                updateObject(reservation, Reservation.class);

                // Notifications
                sendReservationStatusNotification(
                        "Annulation de Réservation Approuvée",
                        "La demande d'annulation pour la réservation N°" + reservation.getId() + " a été approuvée.",
                        NotificationType.RESERVATION_CANCELLATION_SUCCESS
                );
                sendReservationStatusNotificationForClient(
                        "Annulation Approuvée",
                        "Votre demande d'annulation pour la réservation N°" + reservation.getId() + " a été approuvée.",
                        NotificationType.CLIENT_RESERVATION_ANNULATION_SUCCESS,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande d'annulation approuvée. Réservation annulée.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'approbation de l'annulation : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRejeterAnnulation() {
        // Logic to reject a cancellation request (ANNULATION_EN_ATTENTE -> previous status)
        Optional<ButtonType> result = showAlertConfirmation("Rejeter l'Annulation", "Voulez-vous vraiment rejeter l'annulation de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Revert the reservation status to its previous state (e.g., PAYEMENT_EN_ATTENTE or CONFIRMEE)
                reservation.setStatut(StatutReservation.PAYEMENT_EN_ATTENTE);
                updateObject(reservation, Reservation.class);

                // Notifications
                sendReservationStatusNotification(
                        "Annulation de Réservation Rejetée",
                        "La demande d'annulation pour la réservation N°" + reservation.getId() + " a été rejetée.",
                        NotificationType.RESERVATION_CANCELLATION_REFUSED
                );
                sendReservationStatusNotificationForClient(
                        "Annulation Rejetée",
                        "Votre demande d'annulation pour la réservation N°" + reservation.getId() + " a été rejetée.",
                        NotificationType.CLIENT_RESERVATION_ANNULATION_REFUSED,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande d'annulation rejetée.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du rejet de l'annulation : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleFermer(ActionEvent actionEvent) {
        closeStage();
    }

    /**
     * Updates the status of vehicles and chauffeurs associated with the reservation.
     * @param newStatus The new status to apply (DISPONIBLE or INDISPONIBLE).
     */
    private void updateAssociatedEntitiesStatus(Statut newStatus) throws DAOException {
        if (reservation.getVehicules() != null) {
            for (Vehicule vehicule : reservation.getVehicules()) {
                vehicule.setStatut(newStatus);
                updateObject(vehicule, Vehicule.class);
            }
        }
        if (reservation.getChauffeurs() != null) {
            for (Chauffeur chauffeur : reservation.getChauffeurs()) {
                chauffeur.setStatut(newStatus);
                updateObject(chauffeur, Chauffeur.class);
            }
        }
    }

    /**
     * Sends a notification to the administration.
     * @param title Notification title.
     * @param message Notification message.
     * @param type Notification type.
     */
    private void sendReservationStatusNotification(String title, String message, NotificationType type) {
        try {
            Notification notification = new Notification(title, message, type, reservation.getId());
            NotificationService.getInstance().addNotification(notification);
        } catch (Exception e) {
            System.err.println("Error sending admin notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends a specific notification to the client.
     * @param title Notification title.
     * @param message Notification message.
     * @param type Notification type.
     * @param client The client concerned.
     */
    private void sendReservationStatusNotificationForClient(String title, String message, NotificationType type, Client client) {
        try {
            Notification notification = new Notification(title, message, type, reservation.getId());
            NotificationService.getInstance().addNotificationForClient(notification, client); // Fallback to general notification
        } catch (Exception e) {
            System.err.println("Error sending client notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showAlertConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private void closeStage() {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
