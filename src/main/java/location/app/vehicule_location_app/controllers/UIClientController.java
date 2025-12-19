package location.app.vehicule_location_app.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

import java.io.IOException;

import static location.app.vehicule_location_app.controllers.Controller.controllerClientList;

public class UIClientController extends Observer {

    @FXML
    public Button historiqueClientButton;
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

    private MainFenetreController mainFenetreController;

    public UIClientController() {
        this.subject = Subject.getInstance();
        this.subject.attach(this);
    }

    public void setMainFenetreController(MainFenetreController mainFenetreController) {
        this.mainFenetreController = mainFenetreController;
    }

    @FXML
    public void initialize() {

        ObservableList<Client> clientList = FXCollections.observableArrayList(controllerClientList);

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

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10),
                        event -> update())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void selectClientById(int clientId) {
        if (clientsTable == null || clientsTable.getItems().isEmpty()) {
            System.err.println("Table des clients non initialisée ou vide. Impossible de sélectionner.");
            return;
        }
        for (Client client : clientsTable.getItems()) {
            if (client.getId() == clientId) {
                clientsTable.getSelectionModel().select(client);
                clientsTable.scrollTo(client);
                System.out.println("Client sélectionné: " + client.getNom() + " (ID: " + clientId + ")");
                break;
            }
        }
    }

    public void handleHistoriqueClient() {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            showAlert(
            );
            return;
        }
        try {
            mainFenetreController.showUIHistoriqueClient(selectedClient);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'affichage de l'historique du client", e);
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aucun client sélectionné");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez sélectionner un client pour afficher son historique.");
        alert.showAndWait();
    }

    @Override
    public void update() {
        clientsTable.getItems().clear();
        ObservableList<Client> clientList = FXCollections.observableArrayList(controllerClientList);
        clientsTable.setItems(clientList);
    }
}
