package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import location.app.vehicule_location_app.models.*;
import location.app.vehicule_location_app.models.Statut;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class UIInspectReservationController{

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
            showAlert(Alert.AlertType.INFORMATION, "Réservation approuvée.");
        }
    }

    @FXML
    private void handleRejeter() {
        if (reservation != null) {
            reservation.setStatut(StatutReservation.REJETEE);
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
