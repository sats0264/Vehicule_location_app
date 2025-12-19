package location.app.vehicule_location_app.controllers;

import javafx.animation.Animation;
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
import location.app.vehicule_location_app.models.*;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

import java.util.List;
import java.util.stream.Collectors;

import static location.app.vehicule_location_app.controllers.Controller.*;

public class UIDashboardController extends Observer {

    @FXML
    private Label chiffreAffairesLabel;
    @FXML
    private Label voituresCountLabel;
    @FXML
    private Label clientsCountLabel;

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

    @FXML
    private TableView<Client> topClientsTable;
    @FXML
    private TableColumn<Client, String> topClientFirstNameColumn;
    @FXML
    private TableColumn<Client, String> topClientLastNameColumn;
    @FXML
    private TableColumn<Client, Integer> topClientFideliteColumn;

    public UIDashboardController() {
        this.subject = Subject.getInstance();
        this.subject.attach(this);
    }

    @FXML
    public void initialize() {
        System.out.println("Dashboard initialized");
        System.out.println(">>> Nombre de véhicules : " + controllerVehiculeList.size());
        System.out.println(">>> Nombre de clients : " + controllerClientList.size());
        System.out.println(">>> Nombre de réservations : " + controllerReservationList.size());

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


        topClientFirstNameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getPrenom())
        );

        topClientLastNameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getNom())
        );

        topClientFideliteColumn.setCellValueFactory(cellData -> 
                new ReadOnlyObjectWrapper<>(cellData.getValue().getPointFidelite()));

        List<Client> topClients = TrierClient();

        boolean allZero = topClients.stream().allMatch(c ->
                c.getReservations().stream()
                        .filter(r -> r.getFacture() != null)
                        .mapToDouble(r -> r.getFacture().getMontant())
                        .sum() == 0.0);

        if (allZero) {
            topClients = controllerClientList.stream().limit(5).collect(Collectors.toList());
        }

        topClientsTable.setItems(FXCollections.observableArrayList(topClients));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> {
                    loadDashboardData();
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
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

    private List<Client> TrierClient() {

        return controllerClientList.stream()
                .sorted((c1, c2) -> {
                    double total1 = c1.getReservations().stream()
                            .filter(r -> r.getFacture() != null)
                            .mapToDouble(r -> r.getFacture().getMontant())
                            .sum();
                    double total2 = c2.getReservations().stream()
                            .filter(r -> r.getFacture() != null)
                            .mapToDouble(r -> r.getFacture().getMontant())
                            .sum();
                    return Double.compare(total2, total1);
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    private void loadDashboardData() {
        controllerVehiculeList = vehiculeDao.getAll();
        controllerClientList = clientDao.getAll();
        controllerReservationList = reservationDao.getAll();

        chargerStatistique();
    }

    @Override
    public void update() {
        loadDashboardData();
        ObservableList<Reservation> reservedCars = FXCollections.observableArrayList(reservationListActif);
        reservedCarsTable.setItems(reservedCars);

        List<Client> topClients = TrierClient();

        topClientsTable.setItems(FXCollections.observableArrayList(topClients));
    }
}
