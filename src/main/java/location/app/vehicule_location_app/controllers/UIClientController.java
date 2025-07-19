package location.app.vehicule_location_app.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Client;

import java.io.IOException;

public class UIClientController extends Controller {

    @FXML
    public Button historiqueClientButton;
    // --- Clients List Table ---
    @FXML
    private TableView<Client> clientsTable;
    @FXML
    private TableColumn<Client, String> prenomColumn;
    @FXML
    private TableColumn<Client, String> nomColumn;
    @FXML
    private TableColumn<Client, String> emailColumn;
    @FXML
    private TableColumn<Client, String> adresseColumn;
    @FXML
    private TableColumn<Client, String> telephoneColumn;
    @FXML
    private TableColumn<Client, Integer> fideliteColumn;

    private ObservableList<location.app.vehicule_location_app.models.Client> clientList;
    private MainFenetreController mainFenetreController;

    public UIClientController() throws DAOException {
    }

    public void setMainFenetreController(MainFenetreController mainFenetreController) {
        this.mainFenetreController = mainFenetreController;
    }

    @FXML
    public void initialize() {

        // Initialize TableView columns
        clientList = FXCollections.observableArrayList(controllerClientList);

        prenomColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getPrenom() != null
                        ? cellData.getValue().getPrenom() : ""));
        nomColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getNom() != null
                        ? cellData.getValue().getNom() : ""));
        emailColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getEmail() != null
                        ? cellData.getValue().getEmail() : ""));
        adresseColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getAdresse() != null
                        ? cellData.getValue().getAdresse() : ""));
        telephoneColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getTelephone() != null
                        ? cellData.getValue().getTelephone() : ""));
        fideliteColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getPointFidelite()));

        clientsTable.setItems(clientList);
    }

    public void selectClientById(int clientId) {
        if (clientsTable == null || clientsTable.getItems().isEmpty()) {
            System.err.println("Table des clients non initialisée ou vide. Impossible de sélectionner.");
            return;
        }
        for (Client client : clientsTable.getItems()) {
            if (client.getId() == clientId) { // Assurez-vous que Client a une méthode getId()
                clientsTable.getSelectionModel().select(client);
                clientsTable.scrollTo(client);
                System.out.println("Client sélectionné: " + client.getNom() + " (ID: " + clientId + ")");
                break;
            }
        }
    }

    public void handleHistoriqueClient(ActionEvent actionEvent) {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun client sélectionné",
                    "Veuillez sélectionner un client pour afficher son historique.");
            return;
        }
        try {
            mainFenetreController.showUIHistoriqueClient(selectedClient);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'affichage de l'historique du client", e);
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
}
