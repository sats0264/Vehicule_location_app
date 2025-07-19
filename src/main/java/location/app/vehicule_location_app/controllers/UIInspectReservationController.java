package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.*;
import location.app.vehicule_location_app.models.Statut;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class UIInspectReservationController extends Controller{

    private Reservation reservation;
    private List<Chauffeur> chauffeursSelectionnes;


    // --- Champs Client ---
    @FXML
    private TextField clientLastNameField;
    @FXML
    private TextField clientFirstNameField;
    @FXML
    private TextField clientPhoneField;
    @FXML
    private TextField clientEmailField;
    @FXML
    private TextField clientAddressField;

    // --- Champs Voiture ---
    @FXML
    private ComboBox<String> voitureImmatriculationField;
    @FXML
    private TextField voitureModeleField;
    @FXML
    private TextField voitureMarqueField;
    @FXML
    private TextField voitureTarifField;
    @FXML
    private TextField voitureStatutField;

    // --- Champs Chauffeur ---
    @FXML
    private ComboBox<Integer> chauffeurIdComboBox;
    @FXML
    private TextField chauffeurLastNameField;
    @FXML
    private TextField chauffeurFirstNameField;
    @FXML
    private TextField chauffeurStatutField;

    // --- Champs Durée et Montant ---
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField durationField;
    @FXML
    private TextField amountField;

    // --- Boutons ---
    @FXML
    private Button approuverButton;
    @FXML
    private Button rejeterButton;

    public UIInspectReservationController() throws DAOException {
    }

//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//
//    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;

        // --- Client ---
        Client c = reservation.getClient();
        if (c != null) {
            clientFirstNameField.setText(c.getPrenom());
            clientLastNameField.setText(c.getNom());
            clientPhoneField.setText(c.getTelephone());
            clientEmailField.setText(c.getEmail());
            clientAddressField.setText(String.valueOf(c.getPointFidelite()));
        }

        // --- Véhicules ---
        if (reservation.getVehicules() != null && !reservation.getVehicules().isEmpty()) {
            Vehicule v = reservation.getVehicules().get(0); // Juste le premier
            voitureImmatriculationField.getItems().addAll(
                    reservation.getVehicules().stream().map(Vehicule::getImmatriculation).toList()
            );
            voitureImmatriculationField.setOnAction(event -> {
                String selectedImmatriculation = voitureImmatriculationField.getValue();
                Vehicule selectedVehicule = reservation.getVehicules()
                        .stream()
                        .filter(ve -> ve.getImmatriculation().equals(selectedImmatriculation))
                        .findFirst()
                        .orElse(null);

                if (selectedVehicule != null) {
                    voitureModeleField.setText(selectedVehicule.getModele());
                    voitureMarqueField.setText(selectedVehicule.getMarque());
                    voitureTarifField.setText(String.valueOf(selectedVehicule.getTarif()) + " FCFA");
                    voitureStatutField.setText(selectedVehicule.getStatut().toString());
                }
            });

            voitureImmatriculationField.setValue(v.getImmatriculation());

            voitureModeleField.setText(v.getModele());
            voitureMarqueField.setText(v.getMarque());
            voitureTarifField.setText(String.valueOf(v.getTarif()) + " FCFA");
            voitureStatutField.setText(v.getStatut().toString());
        }


        // --- Chauffeur ---

        if (reservation.getChauffeurs() != null && !reservation.getChauffeurs().isEmpty()) {
            Chauffeur ch = reservation.getChauffeurs().get(0);
            chauffeurIdComboBox.getItems().addAll(
                    reservation.getChauffeurs().stream().map(Chauffeur::getId).toList()
            );
            chauffeurIdComboBox.setOnAction(event -> {
                Integer selectedId = chauffeurIdComboBox.getValue();
                Chauffeur selectedChauffeur = reservation.getChauffeurs()
                        .stream()
                        .filter(cha -> cha.getId()==selectedId)
                        .findFirst()
                        .orElse(null);

                if (selectedChauffeur != null) {
                    chauffeurLastNameField.setText(selectedChauffeur.getNom());
                    chauffeurFirstNameField.setText(selectedChauffeur.getPrenom());
                    chauffeurStatutField.setText(selectedChauffeur.getStatut().toString());
                }
            });
            chauffeurIdComboBox.setValue(ch.getId());
            chauffeurLastNameField.setText(ch.getNom());
            chauffeurFirstNameField.setText(ch.getPrenom());
            chauffeurStatutField.setText(ch.getStatut().toString());
        }



        // --- Dates ---
        startDatePicker.setValue(reservation.getDateDebut());
        endDatePicker.setValue(reservation.getDateFin());

        // --- Durée ---
        long duration = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
        durationField.setText(duration + " jour(s)");

        // --- Montant ---
        if (reservation.getFacture() != null) {
            amountField.setText(String.format("%.0f FCFA", reservation.getFacture().getMontant()* duration));
        } else {
            amountField.setText("0 FCFA");
        }
    }

    @FXML
    private void handleApprouver() {
        if (reservation != null) {
            reservation.setStatut(StatutReservation.APPROUVEE);
            if (reservation.getChauffeurs() != null && !reservation.getChauffeurs().isEmpty()) {
                for (Chauffeur chauffeur : reservation.getChauffeurs()) {
                    chauffeur.setStatut(Statut.INDISPONIBLE);
                }
            }
            if (reservation.getVehicules() != null && !reservation.getVehicules().isEmpty()) {
                for (Vehicule vehicule : reservation.getVehicules()) {
                    vehicule.setStatut(Statut.INDISPONIBLE);
                }
            }
            try {
                updateObject(reservation, Reservation.class);

                Subject.getInstance().notifyAllObservers();

                Notification notification = new Notification(
                        "Réservation Approuvée",
                        "La reservation de " + reservation.getClient().getPrenom() + " " + reservation.getClient().getNom() + " a été approuvée.",
                        NotificationType.RESERVATION_CONFIRMATION,
                        reservation.getClient().getId()
                );
                NotificationService.getInstance().addNotification(notification);

                Notification notificationToClient = new Notification(
                        "Réservation Approuvée",
                        "Votre réservation du " + reservation.getDateDebut() + " au " + reservation.getDateFin() + " a été approuvée."+
                        "\nVeuillez procéder au paiement pour finaliser la réservation.",
                        NotificationType.RESERVATION_CONFIRMATION,
                        reservation.getClient().getId()
                );
                NotificationService.getInstance().addNotificationForClient(notificationToClient, reservation.getClient());
                Stage stage = (Stage) approuverButton.getScene().getWindow();
                stage.close();
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
            showAlert(Alert.AlertType.INFORMATION, "Réservation approuvée.");
        }
    }

    @FXML
    private void handleRejeter() {
        if (reservation != null) {
            reservation.setStatut(StatutReservation.REJETEE);
            if (reservation.getChauffeurs() != null && !reservation.getChauffeurs().isEmpty()) {
                for (Chauffeur chauffeur : reservation.getChauffeurs()) {
                    chauffeur.setStatut(Statut.DISPONIBLE);
                }
            }
            if (reservation.getVehicules() != null && !reservation.getVehicules().isEmpty()) {
                for (Vehicule vehicule : reservation.getVehicules()) {
                    vehicule.setStatut(Statut.DISPONIBLE);
                }
            }
            try {
                updateObject(reservation, Reservation.class);

                Subject.getInstance().notifyAllObservers();

                //Envoyer une notification au client
                Notification notification = new Notification(
                        "Réservation Rejetée",
                        "La reservation de " + reservation.getClient().getPrenom() + " " + reservation.getClient().getNom() + " a été rejetée.",
                        NotificationType.RESERVATION_REFUSED,
                        reservation.getClient().getId()
                );
                NotificationService.getInstance().addNotification(notification);
                Notification notificationToClient = new Notification(
                        "Réservation Rejetée",
                        "Votre réservation du " + reservation.getDateDebut() + " au " + reservation.getDateFin() + " a été rejetée.",
                        NotificationType.RESERVATION_REFUSED,
                        reservation.getClient().getId()
                );
                NotificationService.getInstance().addNotificationForClient(notificationToClient, reservation.getClient());
                Stage stage = (Stage) rejeterButton.getScene().getWindow();
                stage.close();
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
            showAlert(Alert.AlertType.INFORMATION, "Réservation rejetée.");
        }
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
