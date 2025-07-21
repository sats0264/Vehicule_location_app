package location.app.vehicule_location_app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.*;
import location.app.vehicule_location_app.observer.Subject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static location.app.vehicule_location_app.controllers.Controller.updateObject;


public class UIInspectReservationController {

    private Reservation reservation;
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

    @FXML
    private ComboBox<Integer> chauffeurIdComboBox;
    @FXML
    private Label chauffeurLastNameField;
    @FXML
    private Label chauffeurFirstNameField;
    @FXML
    private Label chauffeurStatutField;

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker startDateModificationPicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private DatePicker endDateModificationPicker;
    @FXML
    private Label durationField;
    @FXML
    private Label amountField;


    @FXML
    private Button approuverButton;
    @FXML
    private Button rejeterButton;
    @FXML
    private Button fermerButton;

    @FXML
    private Button approuverModificationBtn;
    @FXML
    private Button rejeterModificationBtn;
    @FXML
    private Button approuverAnnulationBtn;
    @FXML
    private Button rejeterAnnulationBtn;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        approuverButton.setOnAction(e -> handleApprouver());
        rejeterButton.setOnAction(e -> handleRejeter());
        fermerButton.setOnAction(this::handleFermer);

        approuverModificationBtn.setOnAction(e -> handleApprouverModification());
        rejeterModificationBtn.setOnAction(e -> handleRejeterModification());
        approuverAnnulationBtn.setOnAction(e -> handleApprouverAnnulation());
        rejeterAnnulationBtn.setOnAction(e -> handleRejeterAnnulation());

        startDatePicker.setDisable(true);
        startDatePicker.setEditable(false);
        endDatePicker.setDisable(true);
        endDatePicker.setEditable(false);

        hideAllRequestButtons();
    }

    private void hideAllRequestButtons() {
        approuverModificationBtn.setVisible(false);
        approuverModificationBtn.setManaged(false);
        rejeterModificationBtn.setVisible(false);
        rejeterModificationBtn.setManaged(false);
        approuverAnnulationBtn.setVisible(false);
        approuverAnnulationBtn.setManaged(false);
        rejeterAnnulationBtn.setVisible(false);
        rejeterAnnulationBtn.setManaged(false);
        approuverButton.setVisible(false);
        approuverButton.setManaged(false);
        rejeterButton.setVisible(false);
        rejeterButton.setManaged(false);
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;

        if (reservation == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune réservation à afficher.");
            return;
        }

        Client c = reservation.getClient();
        if (c != null) {
            clientFirstNameField.setText(c.getPrenom());
            clientLastNameField.setText(c.getNom());
            clientPhoneField.setText(c.getTelephone());
            clientEmailField.setText(c.getEmail());
            clientFideliteField.setText(String.valueOf(c.getPointFidelite()));
        } else {
            clientFirstNameField.setText("N/A");
            clientLastNameField.setText("N/A");
            clientPhoneField.setText("N/A");
            clientEmailField.setText("N/A");
            clientFideliteField.setText("N/A");
        }

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

        startDatePicker.setValue(reservation.getDateDebut());
        endDatePicker.setValue(reservation.getDateFin());

        startDateModificationPicker.setVisible(false);
        startDateModificationPicker.setManaged(false);
        endDateModificationPicker.setVisible(false);
        endDateModificationPicker.setManaged(false);

        long duration = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
        durationField.setText(duration + " jour(s)");

        calculateAndDisplayAmount(duration);

        configureButtonsBasedOnStatus();

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

    private void updateVehiculeDetails(Vehicule vehicule) {
        voitureModeleField.setText(vehicule.getModele());
        voitureMarqueField.setText(vehicule.getMarque());
        voitureTarifField.setText(String.valueOf(vehicule.getTarif()) + " FCFA");
        voitureStatutField.setText(vehicule.getStatut().toString());
    }

    private void updateChauffeurDetails(Chauffeur chauffeur) {
        chauffeurLastNameField.setText(chauffeur.getNom());
        chauffeurFirstNameField.setText(chauffeur.getPrenom());
        chauffeurStatutField.setText(chauffeur.getStatut().toString());
    }

    private void calculateAndDisplayAmount(long duration) {
        double vehiculeTotalTarif = reservation.getVehicules().stream()
                .mapToDouble(Vehicule::getTarif)
                .sum();
        double montantTotal = vehiculeTotalTarif * duration;

        if (reservation.getChauffeurs() != null && !reservation.getChauffeurs().isEmpty()) {
            double chauffeurDailyFee = 7000.0;
            montantTotal += reservation.getChauffeurs().size() * chauffeurDailyFee * duration;
        }
        amountField.setText(String.format("%.0f FCFA", montantTotal));
    }

    private void configureButtonsBasedOnStatus() {
        hideAllRequestButtons();

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


    @FXML
    private void handleApprouver() {
        Optional<ButtonType> result = showAlertConfirmation("Approuver la Réservation", "Voulez-vous vraiment approuver cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.PAYEMENT_EN_ATTENTE);
                updateAssociatedEntitiesStatus(Statut.INDISPONIBLE);
                updateObject(reservation, Reservation.class);

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
        Optional<ButtonType> result = showAlertConfirmation("Rejeter la Réservation", "Voulez-vous vraiment rejeter cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.REJETEE);
                updateAssociatedEntitiesStatus(Statut.DISPONIBLE);
                updateObject(reservation, Reservation.class);

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
        Optional<ButtonType> result = showAlertConfirmation("Approuver la Modification", "Voulez-vous vraiment approuver la modification de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                LocalDate approvedStartDate = reservation.getProposedDateDebut();
                LocalDate approvedEndDate = reservation.getProposedDateFin();

                if (approvedStartDate == null || approvedEndDate == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Dates de modification proposées introuvables.");
                    return;
                }

                reservation.setDateDebut(approvedStartDate);
                reservation.setDateFin(approvedEndDate);
                reservation.setProposedDateDebut(null);
                reservation.setProposedDateFin(null);

                reservation.setStatut(StatutReservation.PAYEMENT_EN_ATTENTE);
                updateObject(reservation, Reservation.class);

                Subject.getInstance().notifyAllObservers();

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
        Optional<ButtonType> result = showAlertConfirmation("Rejeter la Modification", "Voulez-vous vraiment rejeter la modification de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.PAYEMENT_EN_ATTENTE);
                reservation.setProposedDateDebut(null);
                reservation.setProposedDateFin(null);

                updateObject(reservation, Reservation.class);

                Subject.getInstance().notifyAllObservers();

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
        Optional<ButtonType> result = showAlertConfirmation("Approuver l'Annulation", "Voulez-vous vraiment approuver l'annulation de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.ANNULEE);
                updateAssociatedEntitiesStatus(Statut.DISPONIBLE);
                updateObject(reservation, Reservation.class);

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
        Optional<ButtonType> result = showAlertConfirmation("Rejeter l'Annulation", "Voulez-vous vraiment rejeter l'annulation de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.PAYEMENT_EN_ATTENTE);
                updateObject(reservation, Reservation.class);

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

    private void sendReservationStatusNotification(String title, String message, NotificationType type) {
        try {
            Notification notification = new Notification(title, message, type, reservation.getId());
            NotificationService.getInstance().addNotification(notification);
        } catch (Exception e) {
            System.err.println("Error sending admin notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
