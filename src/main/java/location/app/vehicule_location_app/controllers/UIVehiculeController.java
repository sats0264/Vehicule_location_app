package location.app.vehicule_location_app.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UIVehiculeController {

    // --- Vehicle Information Fields ---
    @FXML
    private TextField matriculeField;
    @FXML
    private TextField anneeModeleField;
    @FXML
    private ComboBox<String> couleurComboBox;
    @FXML
    private TextField puissanceField;
    @FXML
    private TextField coutParJourField;
    @FXML
    private ComboBox<String> modeleComboBox;
    @FXML
    private ComboBox<String> carburantComboBox;

    // --- Action Buttons ---
    @FXML
    private Button plusButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button deleteEntryButton;
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

    // --- Vehicles List Table ---
    @FXML
    private TableView<Vehicule> voituresTable;
    @FXML
    private TableColumn<Vehicule, String> matriculeColumn;
    @FXML
    private TableColumn<Vehicule, Integer> anneeModeleColumn;
    @FXML
    private TableColumn<Vehicule, String> couleurColumn;
    @FXML
    private TableColumn<Vehicule, String> puissanceColumn;
    @FXML
    private TableColumn<Vehicule, Double> coutParJourColumn;
    @FXML
    private TableColumn<Vehicule, String> modeleColumn;
    @FXML
    private TableColumn<Vehicule, String> marqueColumn;

    private ObservableList<Vehicule> vehiculeList;

    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
        // Initialize ComboBoxes with sample data
        couleurComboBox.setItems(FXCollections.observableArrayList("Noir", "Blanc", "Gris", "Bleu", "Rouge", "Vert", "Jaune"));
        modeleComboBox.setItems(FXCollections.observableArrayList("A3", "A4", "A6", "C-Class", "E-Class", "X1", "X5", "Golf", "Passat", "Ibiza", "Focus", "Polo"));
        carburantComboBox.setItems(FXCollections.observableArrayList("Essence", "Diesel", "Electrique", "Hybride"));

        // Initialize TableView columns with CellValueFactory
        matriculeColumn.setCellValueFactory(cellData -> cellData.getValue().matriculeProperty());
        anneeModeleColumn.setCellValueFactory(cellData -> cellData.getValue().anneeModeleProperty().asObject());
        couleurColumn.setCellValueFactory(cellData -> cellData.getValue().couleurProperty());
        puissanceColumn.setCellValueFactory(cellData -> cellData.getValue().puissanceProperty());
        coutParJourColumn.setCellValueFactory(cellData -> cellData.getValue().coutParJourProperty().asObject());
        modeleColumn.setCellValueFactory(cellData -> cellData.getValue().modeleProperty());
        marqueColumn.setCellValueFactory(cellData -> cellData.getValue().marqueProperty());

        // Load some dummy data
        vehiculeList = FXCollections.observableArrayList(
                new Vehicule("I-A-20145", 2016, "Noir", "8CV", 500.00, "A4", "Audi", 50),
                new Vehicule("I-A-6247", 2017, "Blanc", "8CV", 500.00, "A3", "Audi", 70),
                new Vehicule("B-123-CD", 2020, "Bleu", "9CV", 650.00, "C-Class", "Mercedes-Benz", 65),
                new Vehicule("Z-987-XY", 2019, "Gris", "7CV", 480.00, "Golf", "Volkswagen", 80 ),
                new Vehicule("1-A-45810", 2018, "Blanc", "7CV", 450.00, "Ibiza", "Seat", 75),
                new Vehicule("1-A-36841", 2019, "Noir", "8CV", 500.00, "Focus", "Ford", 50),
                new Vehicule("1-A-85471", 2017, "Gris", "6CV", 400.00, "Polo", "Volkswagen", 60)
        );
        voituresTable.setItems(vehiculeList);

        // Select the first item to display its details (optional)
        if (!vehiculeList.isEmpty()) {
            voituresTable.getSelectionModel().selectFirst();
            displayVehiculeDetails(voituresTable.getSelectionModel().getSelectedItem());
        }

        // Add listener for table selection to display details
        voituresTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayVehiculeDetails(newValue));
    }

    /**
     * Displays details of a selected vehicle in the input fields.
     */
    private void displayVehiculeDetails(Vehicule vehicule) {
        if (vehicule != null) {
            matriculeField.setText(vehicule.getMatricule());
            anneeModeleField.setText(String.valueOf(vehicule.getAnneeModele()));
            couleurComboBox.getSelectionModel().select(vehicule.getCouleur());
            puissanceField.setText(vehicule.getPuissance());
            coutParJourField.setText(String.format("%.2f DH", vehicule.getCoutParJour()));
            modeleComboBox.getSelectionModel().select(vehicule.getModele());
            carburantComboBox.getSelectionModel().select(vehicule.getCarburant());
        } else {
            // Clear all fields
            matriculeField.clear();
            anneeModeleField.clear();
            couleurComboBox.getSelectionModel().clearSelection();
            puissanceField.clear();
            coutParJourField.clear();
            modeleComboBox.getSelectionModel().clearSelection();
            carburantComboBox.getSelectionModel().clearSelection();
        }
    }

    // --- Event Handlers for Buttons ---

    @FXML
    private void handlePlusButton() {
        System.out.println("Plus button clicked!");
        displayVehiculeDetails(null); // Clear fields for new entry
        voituresTable.getSelectionModel().clearSelection();
        // Optionally generate a new matricule or code
    }

    @FXML
    private void handleRefreshButton() {
        System.out.println("Refresh button clicked!");
        // Logic to refresh data from source
        voituresTable.setItems(null); // Clear first
        voituresTable.setItems(vehiculeList); // Re-set (dummy refresh)
    }

    @FXML
    private void handleSearchButton() {
        System.out.println("Search button clicked!");
        // Logic for searching vehicles
    }

    @FXML
    private void handleDeleteEntryButton() {
        System.out.println("Delete Entry button clicked!");
        Vehicule selectedVehicule = voituresTable.getSelectionModel().getSelectedItem();
        if (selectedVehicule != null) {
            vehiculeList.remove(selectedVehicule);
            showAlert(Alert.AlertType.INFORMATION, "Suppression", "Véhicule supprimé avec succès.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un véhicule à supprimer.");
        }
    }

    @FXML
    private void handleFolderButton() {
        System.out.println("Folder button clicked!");
        // Logic for file operations (e.g., import/export vehicle list)
    }

    @FXML
    private void handleEnregistrer() {
        System.out.println("Enregistrer button clicked!");
        // Logic to save/update vehicle
        // Get data from fields, validate, create new Vehicule object or update existing one
        showAlert(Alert.AlertType.INFORMATION, "Enregistrement", "Véhicule enregistré.");
    }

    @FXML
    private void handleSupprimer() {
        System.out.println("Supprimer button clicked!");
        handleDeleteEntryButton(); // Re-use delete logic
    }

    // --- Navigation Button Handlers ---
    @FXML
    private void handleFirstButton() {
        if (!vehiculeList.isEmpty()) {
            voituresTable.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handlePreviousButton() {
        int selectedIndex = voituresTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            voituresTable.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    private void handleNextButton() {
        int selectedIndex = voituresTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < vehiculeList.size() - 1) {
            voituresTable.getSelectionModel().select(selectedIndex + 1);
        }
    }

    @FXML
    private void handleLastButton() {
        if (!vehiculeList.isEmpty()) {
            voituresTable.getSelectionModel().selectLast();
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

    // --- Data Model Class for Vehicule ---

    /**
     * Modèle de données pour un Véhicule.
     * Note: Si vous avez déjà une classe Car.java ou Vehicule.java séparée, réutilisez-la.
     */
    public static class Vehicule {
        private final SimpleStringProperty matricule;
        private final SimpleIntegerProperty anneeModele;
        private final SimpleStringProperty couleur;
        private final SimpleStringProperty puissance;
        private final SimpleDoubleProperty coutParJour;
        private final SimpleStringProperty modele;
        private final SimpleStringProperty marque;
        private final SimpleIntegerProperty carburant;

        public Vehicule(String matricule, int anneeModele, String couleur, String puissance, double coutParJour, String modele, String marque, int carburant) {
            this.matricule = new SimpleStringProperty(matricule);
            this.anneeModele = new SimpleIntegerProperty(anneeModele);
            this.couleur = new SimpleStringProperty(couleur);
            this.puissance = new SimpleStringProperty(puissance);
            this.coutParJour = new SimpleDoubleProperty(coutParJour);
            this.modele = new SimpleStringProperty(modele);
            this.marque = new SimpleStringProperty(marque);
            this.carburant = new SimpleIntegerProperty(carburant);
        }

        // --- Getters for Properties ---
        public StringProperty matriculeProperty() { return matricule; }
        public IntegerProperty anneeModeleProperty() { return anneeModele; }
        public StringProperty couleurProperty() { return couleur; }
        public StringProperty puissanceProperty() { return puissance; }
        public DoubleProperty coutParJourProperty() { return coutParJour; }
        public StringProperty modeleProperty() { return modele; }
        public StringProperty marqueProperty() { return marque; }
        public IntegerProperty carburantProperty() { return carburant; }

        // --- Getters for Values ---
        public String getMatricule() { return matricule.get(); }
        public int getAnneeModele() { return anneeModele.get(); }
        public String getCouleur() { return couleur.get(); }
        public String getPuissance() { return puissance.get(); }
        public double getCoutParJour() { return coutParJour.get(); }
        public String getModele() { return modele.get(); }
        public String getMarque() { return marque.get(); }
        public int getCarburant() { return carburant.get(); }

        @Override
        public String toString() {
            return matricule.get() + " (" + modele.get() + ")";
        }
    }
}
