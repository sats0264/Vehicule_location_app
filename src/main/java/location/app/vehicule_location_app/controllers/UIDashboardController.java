package location.app.vehicule_location_app.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.*;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

import java.util.List;
import java.util.stream.Collectors;

import static location.app.vehicule_location_app.controllers.Controller.*;

@SuppressWarnings("ClassEscapesDefinedScope")
public class UIDashboardController extends Observer {

    // --- Summary Labels ---
    @FXML
    private Label chiffreAffairesLabel;
    @FXML
    private Label voituresCountLabel;
    @FXML
    private Label clientsCountLabel;

    // --- Reserved Cars Table ---
    @FXML
    private TableView<Reservation> reservedCarsTable;
    @FXML
    private TableColumn<Reservation, String> resMatriculeColumn;
    @FXML
    private TableColumn<Reservation, Integer> resCountColumn;
    @FXML
    private TableColumn<Reservation, String> resStartDateColumn;
    @FXML
    private TableColumn<Reservation, String> resEndDateColumn;
    @FXML
    private TableColumn<Reservation, String> resClientNameColumn;

    // --- Top Clients Table ---
    @FXML
    private TableView<Client> topClientsTable;
    @FXML
    private TableColumn<Client, String> topClientFirstNameColumn;
    @FXML
    private TableColumn<Client, String> topClientLastNameColumn;
    @FXML
    private TableColumn<Client, Double> topClientAmountColumn;

    public UIDashboardController() {
        this.subject = Subject.getInstance();
        this.subject.attach(this);
    }

    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
        System.out.println("Dashboard initialized");  // test
        System.out.println(">>> Nombre de véhicules : " + controllerVehiculeList.size());
        System.out.println(">>> Nombre de clients : " + controllerClientList.size());
        System.out.println(">>> Nombre de réservations : " + controllerReservationList.size());

        // --- Initialisation des labels de statistique ---
        chargerStatistique();

        resMatriculeColumn.setCellValueFactory(cellData -> {
            List<Vehicule> vehicules = cellData.getValue().getVehicules();
            String matricule = vehicules.isEmpty() ? "" : vehicules.getFirst().getImmatriculation();
            return new ReadOnlyStringWrapper(matricule);
        });

        resCountColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getVehicules().size()));

        resStartDateColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDateDebut() != null
                        ? cellData.getValue().getDateDebut().toString()
                        : ""));

        resEndDateColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDateFin() != null
                        ? cellData.getValue().getDateFin().toString()
                        : ""));

        resClientNameColumn.setCellValueFactory(cellData -> {
            Client c = cellData.getValue().getClient();
            return new ReadOnlyStringWrapper(c != null ? c.getPrenom() + " " + c.getNom() : "");
        });

        ObservableList<Reservation> reservedCars = FXCollections.observableArrayList(reservationListActif);
        reservedCarsTable.setItems(reservedCars);


        // --- Top Clients Table ---
        topClientFirstNameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getPrenom())
        );

        topClientLastNameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getNom())
        );

        topClientAmountColumn.setCellValueFactory(cellData -> {
            double total = cellData.getValue().getReservations().stream()
                    .filter(r -> r.getFacture() != null)
                    .mapToDouble(r -> r.getFacture().getMontant())
                    .sum();
            return new ReadOnlyObjectWrapper<>(total);
        });

        // Calculer le montant total des factures pour chaque client
        List<Client> topClients = controllerClientList.stream()
                .sorted((c1, c2) -> {
                    double total1 = c1.getReservations().stream()
                            .filter(r -> r.getFacture() != null)
                            .mapToDouble(r -> r.getFacture().getMontant())
                            .sum();
                    double total2 = c2.getReservations().stream()
                            .filter(r -> r.getFacture() != null)
                            .mapToDouble(r -> r.getFacture().getMontant())
                            .sum();
                    return Double.compare(total2, total1); // Tri décroissant
                })
                .limit(5)
                .collect(Collectors.toList());

        // Si tous les montants sont 0, on prend les 5 premiers clients
        boolean allZero = topClients.stream().allMatch(c ->
                c.getReservations().stream()
                        .filter(r -> r.getFacture() != null)
                        .mapToDouble(r -> r.getFacture().getMontant())
                        .sum() == 0.0);

        if (allZero) {
            topClients = controllerClientList.stream().limit(5).collect(Collectors.toList());
        }
        // Mettre à jour le TableView
        topClientsTable.setItems(FXCollections.observableArrayList(topClients));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> {
                    loadDashboardData();
                })
        );
    }

    private void chargerStatistique() {
        voituresCountLabel.setText(String.valueOf(controllerVehiculeList.size()));
        clientsCountLabel.setText(String.valueOf(controllerClientList.size()));

        double chiffreAffaires = controllerReservationList.stream()
            .filter(r -> r.getFacture() != null)
            .mapToDouble(r -> r.getFacture().getMontant())
            .sum();
        chiffreAffairesLabel.setText(String.format("%.0f FCFA", chiffreAffaires));
    }

    private void loadDashboardData() {
        // Recharger les données des véhicules, clients et réservations
        controllerVehiculeList = vehiculeDao.getAll();
        controllerClientList = clientDao.getAll();
        controllerReservationList = reservationDao.getAll();

        chargerStatistique();
    }

    @Override
    public void update() {
        // Recharger les statistiques ici
        loadDashboardData();
    }
}
