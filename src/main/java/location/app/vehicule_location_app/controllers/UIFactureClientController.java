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
import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.factory.ConcreteFactory;
import location.app.vehicule_location_app.factory.HibernateFactory;
import location.app.vehicule_location_app.models.*;
import location.app.vehicule_location_app.observer.Subject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static location.app.vehicule_location_app.controllers.Controller.ajouterObject;
import static location.app.vehicule_location_app.controllers.Controller.updateObject;

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

    private static final double CHAUFFEUR_DAILY_FEE = 7000.0;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Reservation reservation;

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
        this.reservation = reservation;

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
        // Demander confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de Paiement");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous confirmer le paiement de cette facture ?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // Récupération du montant total
        double montantTotal = 0.0;
        try {
            montantTotal = parseMontant(totalMontantLabel.getText());
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Montant invalide !");
            return;
        }


        // Demande à l'utilisateur de choisir une carte bancaire
        List<CarteBancaire> cartes = new HibernateObjectDaoImpl<>(CarteBancaire.class)
                .findByField("client.id", reservation.getClient().getId());

        if (cartes.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune carte", "Aucune carte n'est enregistrée pour ce client.");
            return;
        }

        // 2. Créer une Map entre affichage (String) et objet CarteBancaire
        Map<String, CarteBancaire> carteAffichageMap = new HashMap<>();
        List<String> affichages = new ArrayList<>();

        for (CarteBancaire carte : cartes) {
            String affichage = carte.getNumeroCarte() + " (Solde : " + String.format("%.2f", carte.getSolde()) + " FCFA)";
            affichages.add(affichage);
            carteAffichageMap.put(affichage, carte);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(affichages.get(0), affichages);
        dialog.setTitle("Choix de la carte");
        dialog.setHeaderText("Veuillez sélectionner une carte pour le paiement");

        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        CarteBancaire selectedCarte = carteAffichageMap.get(resultat.get());

        if (selectedCarte == null) {
            return;
        }

        // Vérification du solde
        if (selectedCarte.getSolde() < montantTotal) {
            showAlert(Alert.AlertType.WARNING, "Solde insuffisant",
                    String.format("Le solde de %.2f FCFA est insuffisant pour le paiement de %.2f FCFA.",
                            selectedCarte.getSolde(), montantTotal));

            return;
        }

        try {
            double newSolde = selectedCarte.getSolde() - montantTotal;
            selectedCarte.setSolde(newSolde);
//            var daoCarte = ConcreteFactory.getFactory(HibernateFactory.class)
//                    .getHibernateObjectDaoImpl(CarteBancaire.class);
            updateObject(selectedCarte, CarteBancaire.class);

            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Paiement effectué",
                    String.format("Le paiement de %.2f FCFA a été effectué avec succès avec la carte %s.",
                            montantTotal, selectedCarte.getNumeroCarte()));

            // Mettre à jour la réservation
            payerButton.setDisable(true);
            reservation.setStatut(StatutReservation.APPROUVEE);
            var facture = new Facture(reservation);
            facture.setMontant(montantTotal);
            ajouterObject(facture,Facture.class);
            updateObject(reservation, Reservation.class);
            int pointsActuels = reservation.getClient().getPointFidelite();
            int pointsGagnes = ((int) montantTotal / 10000) * 10;
            reservation.getClient().setPointFidelite(pointsActuels + pointsGagnes);
            updateObject(reservation.getClient(), Client.class);

            Subject.getInstance().notifyAllObservers();

            Notification notificationUser = new Notification(
                    "Paiement effectué",
                    "Le paiement de " + clientNomPrenomLabel.getText() + " pour la reservation de " + dateDebutLabel.getText() +
                            "->"+ dateFinLabel.getText()+" a été effectué avec succès.",
                    NotificationType.PAYMENT_RECEIVED,
                    reservation.getId());
            Notification notificationClient = new Notification(
                    "Paiement reçu",
                    "Votre paiement de " + montantTotal + " FCFA pour la réservation du " + dateDebutLabel.getText() +
                            " au " + dateFinLabel.getText() + " a été reçu avec succès.",
                    NotificationType.CLIENT_PAYMENT_SUCCESS,
                    reservation.getId());
            NotificationService.getInstance().addNotification(notificationUser);
            NotificationService.getInstance().addNotificationForClient(notificationClient, reservation.getClient());

            Subject.getInstance().notifyAllObservers();

        } catch (DAOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour de la carte.");
        }
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

    private double parseMontant(String montantStr) throws NumberFormatException {
        // Nettoyer : enlever les espaces, puis convertir la virgule en point
        String cleaned = montantStr
                .replace("\u202f", "") // supprime espace insécable (fréquent)
                .replace(" ", "")      // supprime espace normal
                .replace(",", ".")     // convertit séparateur décimal
                .replaceAll("[^\\d.]", ""); // garde uniquement chiffres et point
        return Double.parseDouble(cleaned);
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}