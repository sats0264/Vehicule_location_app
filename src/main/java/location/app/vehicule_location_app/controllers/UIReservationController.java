package location.app.vehicule_location_app.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class UIReservationController {

    // --- Client Information ---
    @FXML
    private ComboBox<Client> clientComboBox;
    @FXML
    private TextField clientPhoneField;
    @FXML
    private TextField clientEmailField;

    // --- Car Information ---
    @FXML
    private ComboBox<Car> voitureComboBox; // Assuming 'Car' class from previous context
    @FXML
    private TextField voitureModeleField;
    @FXML
    private TextField voitureMarqueField;

    // --- Reservation Information ---
    @FXML
    private TextField codeField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField durationField;
    @FXML
    private TextField amountField;

    // Action Buttons
    @FXML
    private Button plusButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button enregistrerButton;
    @FXML
    private Button supprimerButton;

    // Navigation Buttons
    @FXML
    private Button firstButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button lastButton;

    // --- Reservations List Table ---
    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableColumn<Reservation, Integer> resListCodeColumn;
    @FXML
    private TableColumn<Reservation, String> resListStartDateColumn;
    @FXML
    private TableColumn<Reservation, String> resListEndDateColumn;
    @FXML
    private TableColumn<Reservation, String> resListFirstNameColumn;
    @FXML
    private TableColumn<Reservation, String> resListLastNameColumn;
    @FXML
    private TableColumn<Reservation, String> resListMatriculeColumn;
    @FXML
    private TableColumn<Reservation, String> resListModeleColumn;
    @FXML
    private TableColumn<Reservation, String> resListMarqueColumn;

    private ObservableList<Reservation> reservationList;
    private ObservableList<Client> clientList;
    private ObservableList<Car> carList; // Assuming you have a way to get car data

    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
        // --- Initialize ComboBoxes with dummy data ---
        clientList = FXCollections.observableArrayList(
                new Client("Anouar", "Dupont", "0612589362", "anouar.dupont@example.com"),
                new Client("Nour", "Salma", "0600112233", "nour.salma@example.com"),
                new Client("Ouadi", "Sabai", "0644556677", "ouadi.sabai@example.com")
        );
        clientComboBox.setItems(clientList);
        clientComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                clientPhoneField.setText(newVal.getPhone());
                clientEmailField.setText(newVal.getEmail());
            } else {
                clientPhoneField.clear();
                clientEmailField.clear();
            }
        });

        // Assuming you have a way to get your Car data (e.g., from a database or a shared list)
        carList = FXCollections.observableArrayList(
                new Car("1-A-45810", 2018, "Blanc", "7CV", 450.00, "Ibiza", "Seat"),
                new Car("1-A-36841", 2019, "Noir", "8CV", 500.00, "Focus", "Ford"),
                new Car("1-A-85471", 2017, "Gris", "6CV", 400.00, "Polo", "Volkswagen"),
                new Car("1-A-20145", 2016, "Rouge", "8CV", 500.00, "A4", "Audi")
        );
        voitureComboBox.setItems(carList);
        voitureComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                voitureModeleField.setText(newVal.getModele());
                voitureMarqueField.setText(newVal.getMarque());
            } else {
                voitureModeleField.clear();
                voitureMarqueField.clear();
            }
        });


        // --- Initialize DatePickers and Duration/Amount Calculation ---
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateDurationAndAmount());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateDurationAndAmount());

        // Set initial dummy dates
        startDatePicker.setValue(LocalDate.of(2016, 5, 6));
        endDatePicker.setValue(LocalDate.of(2016, 6, 12));
        codeField.setText("7"); // Initial dummy code

        // --- Initialize Reservations Table ---
        resListCodeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty().asObject());
        resListStartDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        resListEndDateColumn.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        resListFirstNameColumn.setCellValueFactory(cellData -> cellData.getValue().clientFirstNameProperty());
        resListLastNameColumn.setCellValueFactory(cellData -> cellData.getValue().clientLastNameProperty());
        resListMatriculeColumn.setCellValueFactory(cellData -> cellData.getValue().voitureMatriculeProperty());
        resListModeleColumn.setCellValueFactory(cellData -> cellData.getValue().voitureModeleProperty());
        resListMarqueColumn.setCellValueFactory(cellData -> cellData.getValue().voitureMarqueProperty());

        reservationList = FXCollections.observableArrayList(
                new Reservation(7, "06/05/2016", "12/06/2016", "Fatiha", "Anouar", "1-A-45810", "Ibiza", "Seat"),
                new Reservation(8, "04/12/2016", "26/12/2016", "Zineb", "Hadi", "1-A-85471", "Polo", "Volkswagen"),
                new Reservation(9, "25/03/2016", "09/04/2016", "Mohamed", "Jadi", "1-A-20145", "A4", "Audi"),
                new Reservation(10, "18/02/2016", "26/02/2016", "Noudi", "Aya", "1-A-36841", "Focus", "Ford"),
                new Reservation(11, "02/06/2016", "12/06/2016", "Najat", "Badii", "1-A-21201", "Polo", "Volkswagen")
        );
        reservationsTable.setItems(reservationList);

        // Select the first item in the table to display its details (optional)
        if (!reservationList.isEmpty()) {
            reservationsTable.getSelectionModel().selectFirst();
            displayReservationDetails(reservationsTable.getSelectionModel().getSelectedItem());
        }

        // Add listener for table selection to display details
        reservationsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayReservationDetails(newValue));
    }

    /**
     * Calculates duration and estimated amount based on selected dates and car.
     */
    @FXML
    private void calculateDurationAndAmount() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        Car selectedCar = voitureComboBox.getSelectionModel().getSelectedItem();

        if (startDate != null && endDate != null && startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; // +1 to include both start and end day
            durationField.setText(days + " jour(s)");

            if (selectedCar != null) {
                double amount = days * selectedCar.getCoutParJour();
                amountField.setText(String.format("%.2f DH", amount));
            } else {
                amountField.setText("0.00 DH");
            }
        } else {
            durationField.setText("0 jour(s)");
            amountField.setText("0.00 DH");
        }
    }

    /**
     * Displays details of a selected reservation in the input fields.
     */
    private void displayReservationDetails(Reservation reservation) {
        if (reservation != null) {
            codeField.setText(String.valueOf(reservation.getCode()));
            startDatePicker.setValue(LocalDate.parse(reservation.getStartDate(), java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            endDatePicker.setValue(LocalDate.parse(reservation.getEndDate(), java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            // Find and select the client in the ComboBox
            clientList.stream()
                    .filter(c -> c.getFirstName().equals(reservation.getClientFirstName()) && c.getLastName().equals(reservation.getClientLastName()))
                    .findFirst()
                    .ifPresent(clientComboBox.getSelectionModel()::select);

            // Find and select the car in the ComboBox
            carList.stream()
                    .filter(c -> c.getMatricule().equals(reservation.getVoitureMatricule()))
                    .findFirst()
                    .ifPresent(voitureComboBox.getSelectionModel()::select);

            calculateDurationAndAmount(); // Recalculate based on loaded dates
        } else {
            // Clear all fields
            codeField.clear();
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            durationField.clear();
            amountField.clear();
            clientComboBox.getSelectionModel().clearSelection();
            voitureComboBox.getSelectionModel().clearSelection();
        }
    }


    // --- Event Handlers for Buttons ---

    @FXML
    private void handleOkButton() {
        calculateDurationAndAmount();
    }

    @FXML
    private void handlePlusButton() {
        System.out.println("Plus button clicked!");
        displayReservationDetails(null); // Clear fields for new entry
        reservationsTable.getSelectionModel().clearSelection();
        codeField.setText(String.valueOf(reservationList.size() + 1)); // Suggest next code
    }

    @FXML
    private void handleRefreshButton() {
        System.out.println("Refresh button clicked!");
        // Logic to refresh data from source
        reservationsTable.setItems(null); // Clear first
        reservationsTable.setItems(reservationList); // Re-set (dummy refresh)
    }

    @FXML
    private void handleSearchButton() {
        System.out.println("Search button clicked!");
        // Logic for searching reservations
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

    @FXML
    private void handleEnregistrer() {
        System.out.println("Enregistrer button clicked!");
        // Logic to save/update reservation
        // Get data from fields: codeField, startDatePicker, endDatePicker, clientComboBox, voitureComboBox
        // Validate input, create a new Reservation object or update existing one
        showAlert(Alert.AlertType.INFORMATION, "Enregistrement", "Réservation enregistrée.");
    }

    @FXML
    private void handleSupprimer() {
        System.out.println("Supprimer button clicked!");
        handleDeleteButton(); // Re-use delete logic
    }

    // --- Navigation Button Handlers ---
    @FXML
    private void handleFirstButton() {
        if (!reservationList.isEmpty()) {
            reservationsTable.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handlePreviousButton() {
        int selectedIndex = reservationsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            reservationsTable.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    private void handleNextButton() {
        int selectedIndex = reservationsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < reservationList.size() - 1) {
            reservationsTable.getSelectionModel().select(selectedIndex + 1);
        }
    }

    @FXML
    private void handleLastButton() {
        if (!reservationList.isEmpty()) {
            reservationsTable.getSelectionModel().selectLast();
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

    // --- Data Model Classes for Tables and ComboBoxes ---

    /**
     * Modèle de données pour un Client.
     */
    public static class Client {
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty email;

        public Client(String firstName, String lastName, String phone, String email) {
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.phone = new SimpleStringProperty(phone);
            this.email = new SimpleStringProperty(email);
        }

        public String getFirstName() { return firstName.get(); }
        public String getLastName() { return lastName.get(); }
        public String getPhone() { return phone.get(); }
        public String getEmail() { return email.get(); }

        @Override
        public String toString() {
            return firstName.get() + " " + lastName.get(); // How client appears in ComboBox
        }
    }

    /**
     * Modèle de données pour une Voiture (réutilisé de Car.java si disponible, sinon défini ici).
     * Note: Si vous avez déjà une classe Car.java, assurez-vous qu'elle a les getters nécessaires.
     * Pour cet exemple, je la définis localement pour la complétude.
     */
    public static class Car {
        private final SimpleStringProperty matricule;
        private final SimpleIntegerProperty anneeModele; // Assuming year is int
        private final SimpleStringProperty couleur;
        private final SimpleStringProperty puissance;
        private final SimpleDoubleProperty coutParJour;
        private final SimpleStringProperty modele;
        private final SimpleStringProperty marque;

        public Car(String matricule, int anneeModele, String couleur, String puissance, double coutParJour, String modele, String marque) {
            this.matricule = new SimpleStringProperty(matricule);
            this.anneeModele = new SimpleIntegerProperty(anneeModele);
            this.couleur = new SimpleStringProperty(couleur);
            this.puissance = new SimpleStringProperty(puissance);
            this.coutParJour = new SimpleDoubleProperty(coutParJour);
            this.modele = new SimpleStringProperty(modele);
            this.marque = new SimpleStringProperty(marque);
        }

        public String getMatricule() { return matricule.get(); }
        public String getModele() { return modele.get(); }
        public String getMarque() { return marque.get(); }
        public double getCoutParJour() { return coutParJour.get(); } // Needed for amount calculation

        @Override
        public String toString() {
            return matricule.get() + " (" + modele.get() + ")"; // How car appears in ComboBox
        }
    }

    /**
     * Modèle de données pour une Réservation.
     */
    public static class Reservation {
        private final SimpleIntegerProperty code;
        private final SimpleStringProperty startDate;
        private final SimpleStringProperty endDate;
        private final SimpleStringProperty clientFirstName;
        private final SimpleStringProperty clientLastName;
        private final SimpleStringProperty voitureMatricule;
        private final SimpleStringProperty voitureModele;
        private final SimpleStringProperty voitureMarque;

        public Reservation(int code, String startDate, String endDate, String clientFirstName, String clientLastName, String voitureMatricule, String voitureModele, String voitureMarque) {
            this.code = new SimpleIntegerProperty(code);
            this.startDate = new SimpleStringProperty(startDate);
            this.endDate = new SimpleStringProperty(endDate);
            this.clientFirstName = new SimpleStringProperty(clientFirstName);
            this.clientLastName = new SimpleStringProperty(clientLastName);
            this.voitureMatricule = new SimpleStringProperty(voitureMatricule);
            this.voitureModele = new SimpleStringProperty(voitureModele);
            this.voitureMarque = new SimpleStringProperty(voitureMarque);
        }

        public IntegerProperty codeProperty() { return code; }
        public StringProperty startDateProperty() { return startDate; }
        public StringProperty endDateProperty() { return endDate; }
        public StringProperty clientFirstNameProperty() { return clientFirstName; }
        public StringProperty clientLastNameProperty() { return clientLastName; }
        public StringProperty voitureMatriculeProperty() { return voitureMatricule; }
        public StringProperty voitureModeleProperty() { return voitureModele; }
        public StringProperty voitureMarqueProperty() { return voitureMarque; }

        public int getCode() { return code.get(); }
        public String getStartDate() { return startDate.get(); }
        public String getEndDate() { return endDate.get(); }
        public String getClientFirstName() { return clientFirstName.get(); }
        public String getClientLastName() { return clientLastName.get(); }
        public String getVoitureMatricule() { return voitureMatricule.get(); }
        public String getVoitureModele() { return voitureModele.get(); }
        public String getVoitureMarque() { return voitureMarque.get(); }
    }
}
