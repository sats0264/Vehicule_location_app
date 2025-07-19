package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.beans.property.ReadOnlyStringWrapper;
import location.app.vehicule_location_app.models.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UIFactureClientController {

    @FXML
    private Label factureIdLabel;
    @FXML
    private Label factureDateLabel;
    @FXML
    private Button payerButton; // Bouton pour l'action de paiement

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
    private TableView<Vehicule> vehiculesTable;
    @FXML
    private TableColumn<Vehicule, String> marqueColumn;
    @FXML
    private TableColumn<Vehicule, String> modeleColumn;
    @FXML
    private TableColumn<Vehicule, String> immatriculationColumn;
    @FXML
    private TableColumn<Vehicule, String> tarifJourColumn;

    @FXML
    private VBox chauffeurDetailsBox; // Conteneur pour les détails du chauffeur
    @FXML
    private GridPane chauffeurGrid; // GridPane dynamique pour les chauffeurs

    @FXML
    private Label sousTotalVehiculeLabel;
    @FXML
    private Label fraisChauffeurLabel;
    @FXML
    private Label autresFraisLabel; // Si vous avez d'autres frais
    @FXML
    private Label totalMontantLabel;

    private static final double CHAUFFEUR_DAILY_FEE = 5000.0; // Exemple de frais journaliers pour un chauffeur (en XOF)

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Initialisation des CellValueFactory pour la TableView des véhicules
        marqueColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getMarque()));
        modeleColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getModele()));
        immatriculationColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getImmatriculation()));
        tarifJourColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.format("%.2f XOF", cellData.getValue().getTarif())));

        // Cache la section chauffeur par défaut
        chauffeurDetailsBox.setVisible(false);
        chauffeurDetailsBox.setManaged(false); // N'occupe pas d'espace si invisible

        // Événement pour le bouton Payer (à implémenter)
        payerButton.setOnAction(event -> handlePayerButton());

        // Initialiser la date de facture à la date du jour
        factureDateLabel.setText(LocalDate.now().format(dateFormatter));
    }

    public void setFacture(Reservation reservation) {
        if (reservation == null || reservation.getClient() == null) {
            // Gérer le cas où les données sont nulles (afficher un message d'erreur ou vider les champs)
            System.err.println("Réservation ou client non trouvé.");
            clearAllFields(); // Méthode à créer pour vider tous les labels/champs
            return;
        }

//        Reservation reservation = facture.getReservation();
        Client client = reservation.getClient();

        // 1. Informations de la Facture
//        factureIdLabel.setText(String.valueOf(facture.getId()));
        // factureDateLabel est déjà initialisé dans initialize() à la date du jour.
        // Si la facture a une date propre, utilisez-la :
        // factureDateLabel.setText(facture.getDateFacture().format(dateFormatter));

        // 2. Informations Client
        clientNomPrenomLabel.setText(client.getPrenom() + " " + client.getNom());
        clientAdresseLabel.setText(client.getAdresse());
        clientTelephoneLabel.setText(client.getTelephone());
        clientEmailLabel.setText(client.getEmail());

        // 3. Détails de la Réservation
        reservationIdLabel.setText(String.valueOf(reservation.getId()));
        dateDebutLabel.setText(reservation.getDateDebut().format(dateFormatter));
        dateFinLabel.setText(reservation.getDateFin().format(dateFormatter));
        reservationStatutLabel.setText(reservation.getStatut().toString());

        // Population de la TableView des véhicules
        vehiculesTable.setItems(FXCollections.observableArrayList(reservation.getVehicules()));

        // Calcul de la durée en jours
        long durationInDays = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
        if (durationInDays < 1) durationInDays = 1; // Minimum 1 jour pour le calcul

        // 4. Calcul et affichage du Récapitulatif des coûts
        long finalDurationInDays = durationInDays;
        double sousTotalVehicule = reservation.getVehicules().stream()
                .mapToDouble(v -> v.getTarif() * finalDurationInDays)
                .sum();
        sousTotalVehiculeLabel.setText(String.format("%.2f XOF", sousTotalVehicule));

        double fraisChauffeur = 0.0;
        List<Chauffeur> chauffeurs = reservation.getChauffeurs();
        if (chauffeurs != null && !chauffeurs.isEmpty()) {
            chauffeurDetailsBox.setVisible(true);
            chauffeurDetailsBox.setManaged(true);
            fraisChauffeur = chauffeurs.size() * CHAUFFEUR_DAILY_FEE * durationInDays;
            fraisChauffeurLabel.setText(String.format("%.2f XOF", fraisChauffeur));

            // Ajout dynamique des détails des chauffeurs au GridPane
            chauffeurGrid.getChildren().clear(); // Nettoie les anciens chauffeurs
            for (int i = 0; i < chauffeurs.size(); i++) {
                Chauffeur chauffeur = chauffeurs.get(i);
                Label nameLabel = new Label("Nom Chauffeur " + (i + 1) + ":");
                Label nameValue = new Label(chauffeur.getPrenom() + " " + chauffeur.getNom());
                Label phoneLabel = new Label("Tél Chauffeur " + (i + 1) + ":");
                Label phoneValue = new Label(chauffeur.getTelephone());

                GridPane.setConstraints(nameLabel, 0, i * 2);
                GridPane.setConstraints(nameValue, 1, i * 2);
                GridPane.setConstraints(phoneLabel, 0, i * 2 + 1);
                GridPane.setConstraints(phoneValue, 1, i * 2 + 1);

                chauffeurGrid.getChildren().addAll(nameLabel, nameValue, phoneLabel, phoneValue);
                chauffeurGrid.setVgap(5); // Espacement entre les lignes de chauffeur
                chauffeurGrid.setHgap(10); // Espacement entre colonnes
                chauffeurGrid.setPadding(new Insets(5,0,5,0)); // Padding interne pour chaque chauffeur
            }

        } else {
            chauffeurDetailsBox.setVisible(false);
            chauffeurDetailsBox.setManaged(false);
            fraisChauffeurLabel.setText(String.format("%.2f XOF", fraisChauffeur));
        }

        // Autres frais (à implémenter si vous avez une logique pour cela)
        double autresFrais = 0.0; // Pour l'instant, zéro
        autresFraisLabel.setText(String.format("%.2f XOF", autresFrais));

        // Le montant total provient de l'objet Facture
        totalMontantLabel.setText(String.format("%.2f XOF", reservation.getVehicules().stream()
                .mapToDouble(v -> v.getTarif() * finalDurationInDays).sum() + fraisChauffeur + autresFrais));
        // Alternativement, si vous voulez recalculer le total ici :
        // double totalRecalcule = sousTotalVehicule + fraisChauffeur + autresFrais;
        // totalMontantLabel.setText(String.format("%.2f XOF", totalRecalcule));

        // Activer/Désactiver le bouton Payer selon le statut de la facture/réservation
        // Par exemple, si la facture est déjà payée
        // payerButton.setDisable(facture.isPaid()); // Si vous avez un champ isPaid dans Facture
    }

    private void handlePayerButton() {
        System.out.println("Bouton Payer cliqué !");

        showAlert(Alert.AlertType.INFORMATION, "Paiement", "Fonctionnalité de paiement à implémenter.");
    }

    private void clearAllFields() {
        // Méthode utilitaire pour réinitialiser l'interface
        factureIdLabel.setText("");
        // ... (vider tous les autres labels)
        vehiculesTable.getItems().clear();
        chauffeurDetailsBox.setVisible(false);
        chauffeurDetailsBox.setManaged(false);
        chauffeurGrid.getChildren().clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}