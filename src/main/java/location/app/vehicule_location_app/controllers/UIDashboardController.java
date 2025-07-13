package location.app.vehicule_location_app.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class UIDashboardController {

    // --- Summary Labels ---
    @FXML
    private Label chiffreAffairesLabel;
    @FXML
    private Label voituresCountLabel;
    @FXML
    private Label clientsCountLabel;

    // --- Reserved Cars Table ---
    @FXML
    private TableView<ReservedCar> reservedCarsTable;
    @FXML
    private TableColumn<ReservedCar, String> resMatriculeColumn;
    @FXML
    private TableColumn<ReservedCar, Integer> resCountColumn;
    @FXML
    private TableColumn<ReservedCar, String> resStartDateColumn;
    @FXML
    private TableColumn<ReservedCar, String> resEndDateColumn;
    @FXML
    private TableColumn<ReservedCar, String> resClientNameColumn;

    // --- Top Clients Table ---
    @FXML
    private TableView<TopClient> topClientsTable;
    @FXML
    private TableColumn<TopClient, String> topClientFirstNameColumn;
    @FXML
    private TableColumn<TopClient, String> topClientLastNameColumn;
    @FXML
    private TableColumn<TopClient, Double> topClientAmountColumn;

    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
        // --- Populate Summary Labels ---
        chiffreAffairesLabel.setText("301100 DH");
        voituresCountLabel.setText("23");
        clientsCountLabel.setText("17");

        // --- Initialize Reserved Cars Table ---
        resMatriculeColumn.setCellValueFactory(cellData -> cellData.getValue().matriculeProperty());
        resCountColumn.setCellValueFactory(cellData -> cellData.getValue().countProperty().asObject());
        resStartDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        resEndDateColumn.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        resClientNameColumn.setCellValueFactory(cellData -> cellData.getValue().clientNameProperty());

        ObservableList<ReservedCar> reservedCars = FXCollections.observableArrayList(
                new ReservedCar("1-A-36543", 5, "11/12/2020", "28/12/2020", "Nour Salma"),
                new ReservedCar("1-A-69854", 15, "25/12/2020", "27/12/2020", "Ouadi Sabai")
        );
        reservedCarsTable.setItems(reservedCars);

        // --- Initialize Top Clients Table ---
        topClientFirstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        topClientLastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        topClientAmountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        ObservableList<TopClient> topClients = FXCollections.observableArrayList(
                new TopClient("Sara", "Tourabi", 35100.00),
                new TopClient("Amal", "Fillali", 29300.00),
                new TopClient("Khalid", "Tabali", 25200.00),
                new TopClient("Fatiha", "Anouar", 24500.00),
                new TopClient("Ali", "Fahd", 20800.00)
        );
        topClientsTable.setItems(topClients);
    }

    // --- Data Model Classes for Tables ---

    /**
     * Modèle de données pour une voiture actuellement réservée.
     */
    public static class ReservedCar {
        private final SimpleStringProperty matricule;
        private final SimpleIntegerProperty count;
        private final SimpleStringProperty startDate;
        private final SimpleStringProperty endDate;
        private final SimpleStringProperty clientName;

        public ReservedCar(String matricule, int count, String startDate, String endDate, String clientName) {
            this.matricule = new SimpleStringProperty(matricule);
            this.count = new SimpleIntegerProperty(count);
            this.startDate = new SimpleStringProperty(startDate);
            this.endDate = new SimpleStringProperty(endDate);
            this.clientName = new SimpleStringProperty(clientName);
        }

        public StringProperty matriculeProperty() { return matricule; }
        public IntegerProperty countProperty() { return count; }
        public StringProperty startDateProperty() { return startDate; }
        public StringProperty endDateProperty() { return endDate; }
        public StringProperty clientNameProperty() { return clientName; }
    }

    /**
     * Modèle de données pour un client top.
     */
    public static class TopClient {
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleDoubleProperty amount;

        public TopClient(String firstName, String lastName, double amount) {
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.amount = new SimpleDoubleProperty(amount);
        }

        public StringProperty firstNameProperty() { return firstName; }
        public StringProperty lastNameProperty() { return lastName; }
        public DoubleProperty amountProperty() { return amount; }
    }
}
