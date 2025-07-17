package location.app.vehicule_location_app.controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.Modality;
import javafx.stage.Stage;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Reservation; // Import your Reservation model
import location.app.vehicule_location_app.models.Client;     // Assuming you have a Client model
import location.app.vehicule_location_app.models.Vehicule;   // Assuming you have a Vehicule model
// Assuming you have a Statut enum

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class UIReservationController extends Controller{

    // --- Action Buttons (now acting on the table selection) ---
    @FXML
    private Button refreshButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button inspectReservationButton;

    // --- Reservations List Table ---
    @FXML
    private TableView<Reservation> reservationsTable;
    // Removed resListCodeColumn as it's no longer in the FXML
    @FXML
    private TableColumn<Reservation, String> resListStartDateColumn;
    @FXML
    private TableColumn<Reservation, String> resListEndDateColumn;
    @FXML
    private TableColumn<Reservation, String> resListStatutColumn; // New column for Statut
    @FXML
    private TableColumn<Reservation, String> resListFirstNameColumn; // From Client
    @FXML
    private TableColumn<Reservation, String> resListLastNameColumn;  // From Client
    @FXML
    private TableColumn<Reservation, String> resListMatriculeColumn; // From Vehicule
    @FXML
    private TableColumn<Reservation, String> resListModeleColumn;    // From Vehicule
    @FXML
    private TableColumn<Reservation, String> resListMarqueColumn;    // From Vehicule

    private ObservableList<Reservation> reservationList;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public UIReservationController() throws DAOException {
    }

    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
@FXML
public void initialize() {
    // 1. Charger les réservations depuis la base
    reservationList = FXCollections.observableArrayList(controllerReservationList);

    // 2. Remplir les colonnes avec les données extraites de Reservation
    resListStartDateColumn.setCellValueFactory(cellData ->
            new ReadOnlyStringWrapper(cellData.getValue().getDateDebut() != null
                    ? cellData.getValue().getDateDebut().format(dateFormatter)
                    : ""));

    resListEndDateColumn.setCellValueFactory(cellData ->
            new ReadOnlyStringWrapper(cellData.getValue().getDateFin() != null
                    ? cellData.getValue().getDateFin().format(dateFormatter)
                    : ""));

    resListStatutColumn.setCellValueFactory(cellData ->
            new ReadOnlyStringWrapper(cellData.getValue().getStatut() != null
                    ? cellData.getValue().getStatut().toString()
                    : ""));

    resListFirstNameColumn.setCellValueFactory(cellData -> {
        Client c = cellData.getValue().getClient();
        return new ReadOnlyStringWrapper(c != null ? c.getPrenom() : "");
    });

    resListLastNameColumn.setCellValueFactory(cellData -> {
        Client c = cellData.getValue().getClient();
        return new ReadOnlyStringWrapper(c != null ? c.getNom() : "");
    });

    resListMatriculeColumn.setCellValueFactory(cellData -> {
        Vehicule v = getFirstVehicule(cellData.getValue());
        return new ReadOnlyStringWrapper(v != null ? v.getImmatriculation() : "");
    });

    resListModeleColumn.setCellValueFactory(cellData -> {
        Vehicule v = getFirstVehicule(cellData.getValue());
        return new ReadOnlyStringWrapper(v != null ? v.getModele() : "");
    });

    resListMarqueColumn.setCellValueFactory(cellData -> {
        Vehicule v = getFirstVehicule(cellData.getValue());
        return new ReadOnlyStringWrapper(v != null ? v.getMarque() : "");
    });

    // 3. Afficher dans le tableau
    reservationsTable.setItems(reservationList);

}

    public void selectReservationById(int reservationId) {
        if (reservationsTable == null || reservationsTable.getItems().isEmpty()) {
            System.err.println("Table des réservations non initialisée ou vide. Impossible de sélectionner.");
            return;
        }
        for (Reservation reservation : reservationsTable.getItems()) {
            if (reservation.getId() == reservationId) { // Assurez-vous que Reservation a une méthode getId()
                reservationsTable.getSelectionModel().select(reservation);
                reservationsTable.scrollTo(reservation);
                System.out.println("Réservation sélectionnée: ID " + reservationId);
                break;
            }
        }
    }

    // --- Event Handlers for Buttons ---

@FXML
private void handleInspectReservationButton() {
    Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
    if (selectedReservation != null) {
        openInspectReservationView(selectedReservation);
    } else {
        showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réservation à inspecter.");
    }
}

@FXML
private void handleRefreshButton() {
    try {
        reservationList = FXCollections.observableArrayList(reservationDao.list());
        reservationsTable.setItems(reservationList);
        showAlert(Alert.AlertType.INFORMATION, "Rafraîchir", "Liste des réservations rafraîchie.");
    } catch (DAOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de rafraîchir les réservations.");
    }
}


@FXML
private void handleSearchButton() {
    System.out.println("Search button clicked!");
    showAlert(Alert.AlertType.INFORMATION, "Rechercher Réservation", "Logique pour rechercher des réservations.");
    // This would typically open a search dialog or filter the table.
}

@FXML
private void handleDeleteButton() {
    System.out.println("Delete button clicked!");
    Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
    if (selectedReservation != null) {
        reservationList.remove(selectedReservation);
        showAlert(Alert.AlertType.INFORMATION, "Suppression", "Réservation supprimée avec succès.");
    } else {
        showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réservation à supprimer.");
    }
}

    /**
     * Affiche une boîte de dialogue d'alerte.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Vehicule getFirstVehicule(Reservation reservation) {
        if (reservation.getVehicules() != null && !reservation.getVehicules().isEmpty()) {
            return reservation.getVehicules().get(0);
        }
        return null;
    }

    private void openInspectReservationView(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIInspectReservation.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur pour lui transmettre la réservation sélectionnée
            UIInspectReservationController controller = loader.getController();
            controller.setReservation(reservation); // méthode à créer dans le contrôleur

            Stage stage = new Stage();
            stage.setTitle("Détails de la Réservation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // bloque la fenêtre principale
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la vue d'inspection.");
        }
    }

}
