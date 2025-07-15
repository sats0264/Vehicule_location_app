package location.app.vehicule_location_app.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Client;

public class UIClientController extends Controller {

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

    public UIClientController() throws DAOException {
    }

    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
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
