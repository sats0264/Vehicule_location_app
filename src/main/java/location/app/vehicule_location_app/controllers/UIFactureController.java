package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import location.app.vehicule_location_app.models.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UIFactureController {

    @FXML
    private Label factureIdLabel;
    @FXML
    private Label factureDateLabel;

    @FXML
    private Label clientNomPrenomLabel;
    @FXML
    private Label clientAdresseLabel;
    @FXML
    private Label clientTelephoneLabel;
    @FXML
    private Label clientEmailLabel;

    @FXML
    private Label reservationIdLabel;
    @FXML
    private Label dateDebutLabel;
    @FXML
    private Label dateFinLabel;
    @FXML
    private Label reservationStatutLabel;

    @FXML
    private VBox itemsGridContent;

    @FXML
    private Label sousTotalVehiculeLabel;
    @FXML
    private Label fraisChauffeurLabel;
    @FXML
    private Label autresFraisLabel;
    @FXML
    private Label totalMontantLabel;

    private static final double CHAUFFEUR_DAILY_FEE = 7000.0;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        factureDateLabel.setText(LocalDate.now().format(dateFormatter));
        sousTotalVehiculeLabel.setText(String.format("%.2f XOF", 0.0));
        fraisChauffeurLabel.setText(String.format("%.2f XOF", 0.0));
        autresFraisLabel.setText(String.format("%.2f XOF", 0.0));
        totalMontantLabel.setText(String.format("%.2f XOF", 0.0));
    }

    public void setFacture(Facture facture) {
        if (facture == null || facture.getReservation() == null || facture.getReservation().getClient() == null) {
            System.err.println("Facture, Reservation or Client is null. Cannot display details.");
            clearAllFields();
            return;
        }

        Reservation reservation = facture.getReservation();
        Client client = reservation.getClient();

        factureIdLabel.setText(String.valueOf(facture.getId()));

        clientNomPrenomLabel.setText(client.getPrenom() + " " + client.getNom());
        clientAdresseLabel.setText(client.getAdresse());
        clientTelephoneLabel.setText(client.getTelephone());
        clientEmailLabel.setText(client.getEmail());

        reservationIdLabel.setText(String.valueOf(reservation.getId()));
        dateDebutLabel.setText(reservation.getDateDebut().format(dateFormatter));
        dateFinLabel.setText(reservation.getDateFin().format(dateFormatter));
        reservationStatutLabel.setText(reservation.getStatut().toString());

        long durationInDays = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
        if (durationInDays < 1) durationInDays = 1;

        itemsGridContent.getChildren().clear();

        double currentVehiculeSubtotal = 0.0;
        if (reservation.getVehicules() != null && !reservation.getVehicules().isEmpty()) {
            for (Vehicule vehicule : reservation.getVehicules()) {
                double lineTotal = vehicule.getTarif() * durationInDays;
                currentVehiculeSubtotal += lineTotal;
                itemsGridContent.getChildren().add(createItemRow(
                        vehicule.getMarque() + " " + vehicule.getModele() + " (" + vehicule.getImmatriculation() + ")",
                        1,
                        vehicule.getTarif(),
                        lineTotal
                ));
            }
        }

        double currentChauffeurFees = 0.0;
        List<Chauffeur> chauffeurs = reservation.getChauffeurs();
        if (chauffeurs != null && !chauffeurs.isEmpty()) {
            currentChauffeurFees = chauffeurs.size() * CHAUFFEUR_DAILY_FEE * durationInDays;
            itemsGridContent.getChildren().add(createItemRow(
                    "Frais Chauffeur(s)",
                    chauffeurs.size(),
                    CHAUFFEUR_DAILY_FEE,
                    currentChauffeurFees
            ));
            for (Chauffeur chauffeur : chauffeurs) {
                Label chauffeurNameLabel = new Label("  - " + chauffeur.getPrenom() + " " + chauffeur.getNom() + " (Tél: " + chauffeur.getTelephone() + ")");
                chauffeurNameLabel.setPadding(new Insets(0, 0, 0, 20));
                itemsGridContent.getChildren().add(chauffeurNameLabel);
            }
        }

        sousTotalVehiculeLabel.setText(String.format("%.2f XOF", currentVehiculeSubtotal));
        fraisChauffeurLabel.setText(String.format("%.2f XOF", currentChauffeurFees));

        double autresFrais = 0.0;
        autresFraisLabel.setText(String.format("%.2f XOF", autresFrais));

        totalMontantLabel.setText(String.format("%.2f XOF", facture.getMontant()));
    }

    private HBox createItemRow(String description, int quantity, double unitPrice, double total) {
        HBox row = new HBox(15);
        row.setPadding(new Insets(5, 0, 5, 0));
        row.setPrefHeight(30.0);

        Label descLabel = new Label(description);
        descLabel.setPrefWidth(200.0);
        descLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(descLabel, Priority.ALWAYS);

        Label qtyLabel = new Label(String.valueOf(quantity));
        qtyLabel.setPrefWidth(80.0);
        qtyLabel.setAlignment(Pos.CENTER_RIGHT);

        Label unitPriceLabel = new Label(String.format("%.2f", unitPrice));
        unitPriceLabel.setPrefWidth(100.0);
        unitPriceLabel.setAlignment(Pos.CENTER_RIGHT);

        Label totalLabel = new Label(String.format("%.2f XOF", total));
        totalLabel.setPrefWidth(100.0);
        totalLabel.setAlignment(Pos.CENTER_RIGHT);
        totalLabel.setStyle("-fx-font-weight: bold;");

        row.getChildren().addAll(descLabel, qtyLabel, unitPriceLabel, totalLabel);
        return row;
    }

    private void clearAllFields() {
        factureIdLabel.setText("");
        factureDateLabel.setText(LocalDate.now().format(dateFormatter));
        clientNomPrenomLabel.setText("");
        clientAdresseLabel.setText("");
        clientTelephoneLabel.setText("");
        clientEmailLabel.setText("");
        reservationIdLabel.setText("");
        dateDebutLabel.setText("");
        dateFinLabel.setText("");
        reservationStatutLabel.setText("");
        itemsGridContent.getChildren().clear();
        sousTotalVehiculeLabel.setText(String.format("%.2f XOF", 0.0));
        fraisChauffeurLabel.setText(String.format("%.2f XOF", 0.0));
        autresFraisLabel.setText(String.format("%.2f XOF", 0.0));
        totalMontantLabel.setText(String.format("%.2f XOF", 0.0));
    }
}
