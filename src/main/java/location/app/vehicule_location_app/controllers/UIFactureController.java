package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos; // Import for Pos
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority; // Import for Priority
import javafx.scene.layout.VBox;
import javafx.scene.Node; // Import for Node

import location.app.vehicule_location_app.models.*; // Import all your model classes
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
    private VBox itemsGridContent; // Container for items (vehicles and chauffeurs)

    @FXML
    private Label sousTotalVehiculeLabel;
    @FXML
    private Label fraisChauffeurLabel;
    @FXML
    private Label autresFraisLabel;
    @FXML
    private Label totalMontantLabel;

    // Example daily fee for a chauffeur (in XOF)
    private static final double CHAUFFEUR_DAILY_FEE = 5000.0;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Initialize the invoice date to the current date
        factureDateLabel.setText(LocalDate.now().format(dateFormatter));
        // Ensure cost labels are initialized to zero
        sousTotalVehiculeLabel.setText(String.format("%.2f XOF", 0.0));
        fraisChauffeurLabel.setText(String.format("%.2f XOF", 0.0));
        autresFraisLabel.setText(String.format("%.2f XOF", 0.0));
        totalMontantLabel.setText(String.format("%.2f XOF", 0.0));
    }

    /**
     * Main method to set the invoice data to be displayed in the interface.
     * @param facture The Facture object containing all necessary data.
     */
    public void setFacture(Facture facture) {
        // Basic null checks for essential data
        if (facture == null || facture.getReservation() == null || facture.getReservation().getClient() == null) {
            System.err.println("Facture, Reservation or Client is null. Cannot display details.");
            clearAllFields(); // Clear all fields if data is invalid
            return;
        }

        Reservation reservation = facture.getReservation();
        Client client = reservation.getClient();

        // 1. Invoice Information
        factureIdLabel.setText(String.valueOf(facture.getId()));
        // If the invoice has its own date, use it; otherwise, it defaults to current date from initialize()
        // if (facture.getDateFacture() != null) {
        //     factureDateLabel.setText(facture.getDateFacture().format(dateFormatter));
        // }

        // 2. Client Information
        clientNomPrenomLabel.setText(client.getPrenom() + " " + client.getNom());
        clientAdresseLabel.setText(client.getAdresse());
        clientTelephoneLabel.setText(client.getTelephone());
        clientEmailLabel.setText(client.getEmail());

        // 3. Reservation Details
        reservationIdLabel.setText(String.valueOf(reservation.getId()));
        dateDebutLabel.setText(reservation.getDateDebut().format(dateFormatter));
        dateFinLabel.setText(reservation.getDateFin().format(dateFormatter));
        reservationStatutLabel.setText(reservation.getStatut().toString());

        // Calculate duration in days
        long durationInDays = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
        if (durationInDays < 1) durationInDays = 1; // Minimum 1 day for calculation

        // 4. Rented Items (Vehicles and Chauffeurs)
        itemsGridContent.getChildren().clear(); // Clear previous items

        double currentVehiculeSubtotal = 0.0;
        if (reservation.getVehicules() != null && !reservation.getVehicules().isEmpty()) {
            for (Vehicule vehicule : reservation.getVehicules()) {
                double lineTotal = vehicule.getTarif() * durationInDays;
                currentVehiculeSubtotal += lineTotal;
                itemsGridContent.getChildren().add(createItemRow(
                        vehicule.getMarque() + " " + vehicule.getModele() + " (" + vehicule.getImmatriculation() + ")",
                        1, // Quantity is always 1 for a single vehicle
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
                    chauffeurs.size(), // Quantity of chauffeurs
                    CHAUFFEUR_DAILY_FEE,
                    currentChauffeurFees
            ));
            // Optionally: add chauffeur names below the fees for more detail
            for (Chauffeur chauffeur : chauffeurs) {
                Label chauffeurNameLabel = new Label("  - " + chauffeur.getPrenom() + " " + chauffeur.getNom() + " (Tél: " + chauffeur.getTelephone() + ")");
                chauffeurNameLabel.setPadding(new Insets(0, 0, 0, 20)); // Indent
                itemsGridContent.getChildren().add(chauffeurNameLabel);
            }
        }

        // 5. Cost Summary
        sousTotalVehiculeLabel.setText(String.format("%.2f XOF", currentVehiculeSubtotal));
        fraisChauffeurLabel.setText(String.format("%.2f XOF", currentChauffeurFees));

        // Other fees (implement if you have logic for this)
        double autresFrais = 0.0; // Currently zero
        autresFraisLabel.setText(String.format("%.2f XOF", autresFrais));

        // The total amount comes from the Facture object
        totalMontantLabel.setText(String.format("%.2f XOF", facture.getMontant()));
        // Alternatively, if you want to recalculate the total here:
        // double totalRecalcule = currentVehiculeSubtotal + currentChauffeurFees + autresFrais;
        // totalMontantLabel.setText(String.format("%.2f XOF", totalRecalcule));
    }

    /**
     * Creates an item row for the "Articles Loués" section.
     * @param description Description of the item (e.g., vehicle name, chauffeur fees).
     * @param quantity Quantity.
     * @param unitPrice Unit price.
     * @param total Total for this line item.
     * @return HBox representing the item row.
     */
    private HBox createItemRow(String description, int quantity, double unitPrice, double total) {
        HBox row = new HBox(15); // Spacing between columns
        row.setPadding(new Insets(5, 0, 5, 0)); // Small vertical padding
        row.setPrefHeight(30.0); // Fixed height for rows for consistent look

        Label descLabel = new Label(description);
        descLabel.setPrefWidth(200.0); // Fixed width for description
        descLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(descLabel, Priority.ALWAYS); // Allow description to grow

        Label qtyLabel = new Label(String.valueOf(quantity));
        qtyLabel.setPrefWidth(80.0);
        qtyLabel.setAlignment(Pos.CENTER_RIGHT); // Align quantity to the right

        Label unitPriceLabel = new Label(String.format("%.2f", unitPrice));
        unitPriceLabel.setPrefWidth(100.0);
        unitPriceLabel.setAlignment(Pos.CENTER_RIGHT); // Align unit price to the right

        Label totalLabel = new Label(String.format("%.2f XOF", total));
        totalLabel.setPrefWidth(100.0);
        totalLabel.setAlignment(Pos.CENTER_RIGHT); // Align total to the right
        totalLabel.setStyle("-fx-font-weight: bold;"); // Make total bold

        row.getChildren().addAll(descLabel, qtyLabel, unitPriceLabel, totalLabel);
        return row;
    }

    /**
     * Clears all fields in the invoice display.
     */
    private void clearAllFields() {
        factureIdLabel.setText("");
        factureDateLabel.setText(LocalDate.now().format(dateFormatter)); // Reset to current date
        clientNomPrenomLabel.setText("");
        clientAdresseLabel.setText("");
        clientTelephoneLabel.setText("");
        clientEmailLabel.setText("");
        reservationIdLabel.setText("");
        dateDebutLabel.setText("");
        dateFinLabel.setText("");
        reservationStatutLabel.setText("");
        itemsGridContent.getChildren().clear(); // Clear dynamic items
        sousTotalVehiculeLabel.setText(String.format("%.2f XOF", 0.0));
        fraisChauffeurLabel.setText(String.format("%.2f XOF", 0.0));
        autresFraisLabel.setText(String.format("%.2f XOF", 0.0));
        totalMontantLabel.setText(String.format("%.2f XOF", 0.0));
    }
}
