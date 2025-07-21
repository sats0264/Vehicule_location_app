package location.app.vehicule_location_app.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Reservation;
import location.app.vehicule_location_app.models.StatutReservation;
import location.app.vehicule_location_app.models.Vehicule;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static location.app.vehicule_location_app.controllers.Controller.reservationDao;

public class UIReservationController extends Observer {

    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableColumn<Reservation, String> resListStartDateColumn;
    @FXML
    private TableColumn<Reservation, String> resListEndDateColumn;
    @FXML
    private TableColumn<Reservation, String> resListStatutColumn;
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

    @FXML
    private ComboBox<StatutReservation> statusFilterComboBox;

    private ObservableList<Reservation> masterReservationList;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public UIReservationController() {
        this.subject = Subject.getInstance();
        this.subject.attach(this);
    }

    @FXML
    public void initialize() {
        resListStartDateColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDateDebut() != null
                        ? cellData.getValue().getDateDebut().format(dateFormatter)
                        : ""));

        resListEndDateColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDateFin() != null
                        ? cellData.getValue().getDateFin().format(dateFormatter)
                        : ""));

        resListStatutColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getStatut() != null
                        ? cellData.getValue().getStatut().toString()
                        : ""));

        resListFirstNameColumn.setCellValueFactory(cellData -> {
            Client c = cellData.getValue().getClient();
            return new ReadOnlyStringWrapper(c != null ? c.getPrenom() : "");
        });

        resListLastNameColumn.setCellValueFactory(cellData -> {
            Client c = cellData.getValue().getClient();
            return new ReadOnlyStringWrapper(c != null ? c.getNom() : "");
        });

        resListMatriculeColumn.setCellValueFactory(cellData -> {
            Vehicule v = getFirstVehicule(cellData.getValue());
            return new ReadOnlyStringWrapper(v != null ? v.getImmatriculation() : "");
        });

        resListModeleColumn.setCellValueFactory(cellData -> {
            Vehicule v = getFirstVehicule(cellData.getValue());
            return new ReadOnlyStringWrapper(v != null ? v.getModele() : "");
        });

        resListMarqueColumn.setCellValueFactory(cellData -> {
            Vehicule v = getFirstVehicule(cellData.getValue());
            return new ReadOnlyStringWrapper(v != null ? v.getMarque() : "");
        });

        statusFilterComboBox.getItems().add(null);
        statusFilterComboBox.setButtonCell(new ListCell<StatutReservation>() {
            @Override
            protected void updateItem(StatutReservation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Tous les statuts");
                } else {
                    setText(item.toString());
                }
            }
        });
        statusFilterComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(StatutReservation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Tous les statuts");
                } else {
                    setText(item.toString());
                }
            }
        });

        statusFilterComboBox.getItems().addAll(StatutReservation.values());
        statusFilterComboBox.setValue(null);

        statusFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filterReservations());

        loadReservationsAndFilter();

        Timeline timeLine = new Timeline(
                new KeyFrame(Duration.seconds(5), event ->{
                        loadReservationsAndFilter();
                }
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private void loadReservationsAndFilter() {
        try {
            masterReservationList = FXCollections.observableArrayList(reservationDao.list());
            filterReservations();
        } catch (DAOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger les réservations.");
        }
    }

    private void filterReservations() {
        StatutReservation selectedStatus = statusFilterComboBox.getValue();
        ObservableList<Reservation> filteredList = FXCollections.observableArrayList();

        if (selectedStatus == null) {
            filteredList.addAll(masterReservationList);
        } else {
            for (Reservation res : masterReservationList) {
                if (res.getStatut() == selectedStatus) {
                    filteredList.add(res);
                }
            }
        }
        reservationsTable.setItems(filteredList);
        reservationsTable.refresh();
    }


    public void selectReservationById(int reservationId) {
        if (reservationsTable == null || masterReservationList == null || masterReservationList.isEmpty()) {
            System.err.println("Table des réservations non initialisée ou vide. Impossible de sélectionner.");
            return;
        }
        Reservation reservationToSelect = masterReservationList.stream()
                .filter(r -> r.getId() == reservationId)
                .findFirst()
                .orElse(null);

        if (reservationToSelect != null) {
            if (statusFilterComboBox.getValue() != null && statusFilterComboBox.getValue() != reservationToSelect.getStatut()) {
                statusFilterComboBox.setValue(null);
            }
            reservationsTable.getSelectionModel().select(reservationToSelect);
            reservationsTable.scrollTo(reservationToSelect);
            System.out.println("Réservation sélectionnée: ID " + reservationId);
        } else {
            System.out.println("Réservation avec ID " + reservationId + " non trouvée dans la liste.");
        }
    }

    @FXML
    private void handleInspectReservationButton() {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            openInspectReservationView(selectedReservation);
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réservation à inspecter.");
        }
    }

    @FXML
    private void handleRefreshButton() {
        loadReservationsAndFilter();
        showAlert(Alert.AlertType.INFORMATION, "Rafraîchir", "Liste des réservations rafraîchie.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Vehicule getFirstVehicule(Reservation reservation) {
        if (reservation.getVehicules() != null && !reservation.getVehicules().isEmpty()) {
            return reservation.getVehicules().getFirst();
        }
        return null;
    }

    private void openInspectReservationView(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIInspectReservation.fxml"));
            Parent root = loader.load();

            UIInspectReservationController controller = loader.getController();
            controller.setReservation(reservation);

            Stage stage = new Stage();
            stage.setTitle("Détails de la Réservation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            update();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la vue de détails de la réservation.");
        }
    }
// ...

    @Override
    public void update() {
        loadReservationsAndFilter();
    }
}
