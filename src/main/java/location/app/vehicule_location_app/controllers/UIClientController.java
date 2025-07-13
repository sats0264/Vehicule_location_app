package location.app.vehicule_location_app.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UIClientController {

    // --- Client Information Fields ---
    @FXML
    private TextField cinField;
    @FXML
    private TextField permisField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField nomField;
    @FXML
    private ComboBox<String> sexeComboBox;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField emailField;

    // --- Action Buttons ---
    @FXML
    private Button plusButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button deleteEntryButton; // Renamed to avoid conflict with supprimerButton
    @FXML
    private Button folderButton;

    // --- Navigation Buttons ---
    @FXML
    private Button firstButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button lastButton;

    @FXML
    private Button enregistrerButton;
    @FXML
    private Button supprimerButton;

    // --- Clients List Table ---
    @FXML
    private TableView<Client> clientsTable;
    @FXML
    private TableColumn<Client, String> cinColumn;
    @FXML
    private TableColumn<Client, String> permisColumn;
    @FXML
    private TableColumn<Client, String> prenomColumn;
    @FXML
    private TableColumn<Client, String> nomColumn;
    @FXML
    private TableColumn<Client, String> sexeColumn;
    @FXML
    private TableColumn<Client, String> telephoneColumn;
    @FXML
    private TableColumn<Client, String> emailColumn;

    private ObservableList<Client> clientList;

    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
        // Initialize Sexe ComboBox
        sexeComboBox.setItems(FXCollections.observableArrayList("M", "F"));

        // Initialize TableView columns
        cinColumn.setCellValueFactory(cellData -> cellData.getValue().cinProperty());
        permisColumn.setCellValueFactory(cellData -> cellData.getValue().permisProperty());
        prenomColumn.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        sexeColumn.setCellValueFactory(cellData -> cellData.getValue().sexeProperty());
        telephoneColumn.setCellValueFactory(cellData -> cellData.getValue().telephoneProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        // Load some dummy data
        clientList = FXCollections.observableArrayList(
                new Client("A524178", "17/33583", "Zineb", "Hadi", "F", "Avenue Nour, Meknès", "0679552247", "hadi2020@gmail.com"),
                new Client("B15874", "15/586943", "Salma", "Fatiha", "F", "Rue 1, Casablanca", "0661225544", "salma1999@gmail.com"),
                new Client("BB15256", "15/201364", "Amine", "Taybi", "M", "Bd Mohammed V, Rabat", "0666224477", "amine2584@gmail.com"),
                new Client("JA14562", "10/529983", "Fatiha", "Anouar", "F", "Quartier Atlas, Fès", "0612589362", "fatihaanouar@gmail.com"),
                new Client("JB26511", "22/2698541", "Ali", "Fahd", "M", "Av. Hassan II, Marrakech", "0672331144", "amalfillali@gmail.com"),
                new Client("JBB5547", "20/24150", "Najat", "Badii", "M", "Rue de la Liberté, Agadir", "0696358743", "BadiiNajat@gmail.com"),
                new Client("IE52844", "96/698543", "Mourad", "Karadi", "M","Avenue Nour, Meknès", "0693256943", "Mourad252@gmail.com"),
                new Client("JM2584", "12/25864", "Mourad", "Saadi", "M", "0612255884","Mourad", "MouradSa@gmail.com"),
                new Client("JT18524", "39/1025871", "Sara", "Tourabi", "F", "0686254196","Sara Moud", "Sara55@gmail.com"),
                new Client("JZ1452", "14/25874", "Youssef", "Zouhair", "M", "Rue des Fleurs, Tanger", "0678452369", "YoussefZ@gmail.com"),
                new Client  ("JZ1452", "17/25874", "Khajhar", "Zoair", "M", "Rue des sentiers, Nger", "0568452369", "YoeZ@gmail.com")
        );
        clientsTable.setItems(clientList);

        // Select the first item to display its details (optional)
        if (!clientList.isEmpty()) {
            clientsTable.getSelectionModel().selectFirst();
            displayClientDetails(clientsTable.getSelectionModel().getSelectedItem());
        }

        // Add listener for table selection to display details
        clientsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayClientDetails(newValue));
    }

    /**
     * Displays details of a selected client in the input fields.
     */
    private void displayClientDetails(Client client) {
        if (client != null) {
            cinField.setText(client.getCin());
            permisField.setText(client.getPermis());
            prenomField.setText(client.getPrenom());
            nomField.setText(client.getNom());
            sexeComboBox.getSelectionModel().select(client.getSexe());
            adresseField.setText(client.getAdresse());
            telephoneField.setText(client.getTelephone());
            emailField.setText(client.getEmail());
        } else {
            // Clear all fields
            cinField.clear();
            permisField.clear();
            prenomField.clear();
            nomField.clear();
            sexeComboBox.getSelectionModel().clearSelection();
            adresseField.clear();
            telephoneField.clear();
            emailField.clear();
        }
    }

    // --- Event Handlers for Buttons ---

    @FXML
    private void handlePlusButton() {
        System.out.println("Plus button clicked!");
        displayClientDetails(null); // Clear fields for new entry
        clientsTable.getSelectionModel().clearSelection();
        // Optionally generate a new CIN/Permis code
    }

    @FXML
    private void handleRefreshButton() {
        System.out.println("Refresh button clicked!");
        // Logic to refresh data from source
        clientsTable.setItems(null); // Clear first
        clientsTable.setItems(clientList); // Re-set (dummy refresh)
    }

    @FXML
    private void handleSearchButton() {
        System.out.println("Search button clicked!");
        // Logic for searching clients
    }

    @FXML
    private void handleDeleteEntryButton() {
        System.out.println("Delete Entry button clicked!");
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            clientList.remove(selectedClient);
            showAlert(Alert.AlertType.INFORMATION, "Suppression", "Client supprimé avec succès.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un client à supprimer.");
        }
    }

    @FXML
    private void handleFolderButton() {
        System.out.println("Folder button clicked!");
        // Logic for file operations (e.g., import/export client list)
    }

    @FXML
    private void handleEnregistrer() {
        System.out.println("Enregistrer button clicked!");
        // Logic to save/update client
        // Get data from fields, validate, create new Client object or update existing one
        showAlert(Alert.AlertType.INFORMATION, "Enregistrement", "Client enregistré.");
    }

    @FXML
    private void handleSupprimer() {
        System.out.println("Supprimer button clicked!");
        handleDeleteEntryButton(); // Re-use delete logic
    }

    // --- Navigation Button Handlers ---
    @FXML
    private void handleFirstButton() {
        if (!clientList.isEmpty()) {
            clientsTable.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handlePreviousButton() {
        int selectedIndex = clientsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            clientsTable.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    private void handleNextButton() {
        int selectedIndex = clientsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < clientList.size() - 1) {
            clientsTable.getSelectionModel().select(selectedIndex + 1);
        }
    }

    @FXML
    private void handleLastButton() {
        if (!clientList.isEmpty()) {
            clientsTable.getSelectionModel().selectLast();
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

    // --- Data Model Class for Client ---

    /**
     * Modèle de données pour un Client.
     * Note: Si vous avez déjà une classe Client.java séparée, réutilisez-la.
     */
    public static class Client {
        private final SimpleStringProperty cin;
        private final SimpleStringProperty permis;
        private final SimpleStringProperty prenom;
        private final SimpleStringProperty nom;
        private final SimpleStringProperty sexe;
        private final SimpleStringProperty adresse;
        private final SimpleStringProperty telephone;
        private final SimpleStringProperty email;

        public Client(String cin, String permis, String prenom, String nom, String sexe, String adresse, String telephone, String email) {
            this.cin = new SimpleStringProperty(cin);
            this.permis = new SimpleStringProperty(permis);
            this.prenom = new SimpleStringProperty(prenom);
            this.nom = new SimpleStringProperty(nom);
            this.sexe = new SimpleStringProperty(sexe);
            this.adresse = new SimpleStringProperty(adresse);
            this.telephone = new SimpleStringProperty(telephone);
            this.email = new SimpleStringProperty(email);
        }

        // --- Getters for Properties ---
        public StringProperty cinProperty() { return cin; }
        public StringProperty permisProperty() { return permis; }
        public StringProperty prenomProperty() { return prenom; }
        public StringProperty nomProperty() { return nom; }
        public StringProperty sexeProperty() { return sexe; }
        public StringProperty adresseProperty() { return adresse; }
        public StringProperty telephoneProperty() { return telephone; }
        public StringProperty emailProperty() { return email; }

        // --- Getters for Values ---
        public String getCin() { return cin.get(); }
        public String getPermis() { return permis.get(); }
        public String getPrenom() { return prenom.get(); }
        public String getNom() { return nom.get(); }
        public String getSexe() { return sexe.get(); }
        public String getAdresse() { return adresse.get(); }
        public String getTelephone() { return telephone.get(); }
        public String getEmail() { return email.get(); }

        @Override
        public String toString() {
            return prenom.get() + " " + nom.get(); // How client appears in ComboBox (e.g., if used elsewhere)
        }
    }
}
