package location.app.vehicule_location_app.controllers;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Vehicule;

import java.io.IOException;

public class UIVehiculeController extends Controller {

    @FXML
    private TableView<Vehicule> voituresTable;

    @FXML
    private TableColumn<Vehicule, String> immatriculeColumn;

    @FXML
    private TableColumn<Vehicule, Double> tarifColumn;

    @FXML
    private TableColumn<Vehicule, String> marqueColumn;

    @FXML
    private TableColumn<Vehicule, String> modeleColumn;

    @FXML
    private TableColumn<Vehicule, String> photoColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button detailButton;

    private ObservableList<Vehicule> vehiculeList = FXCollections.observableArrayList();

    public UIVehiculeController() throws DAOException {
    }

    @FXML
    private void initialize() {

        vehiculeList = FXCollections.observableArrayList(controllerVehiculeList);

        immatriculeColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getImmatriculation() != null
                        ? cellData.getValue().getImmatriculation() : ""));

        tarifColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getTarif())
        );

        marqueColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getMarque() != null
                        ? cellData.getValue().getMarque() : ""));

        modeleColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getModele() != null
                        ? cellData.getValue().getModele() : ""));

        photoColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getPhoto() != null
                        ? cellData.getValue().getPhoto() : ""));

        // Remplir la table
        voituresTable.setItems(vehiculeList);
    }

    @FXML
    private void handleAddVehicule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIAddVehicule.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un véhicule");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloque l'interaction avec la fenêtre principale
            stage.showAndWait(); // Attend la fermeture

            // Après fermeture, recharger la liste ?
            voituresTable.setItems(FXCollections.observableArrayList(controllerVehiculeList));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire d'ajout.");
        }
    }


    @FXML
    private void handleDetailVehicule() {
        Vehicule selected = voituresTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIDetailVehicule.fxml"));
                Parent root = loader.load();

                // Récupérer le contrôleur et passer le véhicule sélectionné
                UIDetailVehiculeController controller = loader.getController();
                controller.setVehicule(selected);

                Stage stage = new Stage();
                stage.setTitle("Détails du véhicule");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir les détails du véhicule.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun véhicule sélectionné", "Veuillez sélectionner un véhicule dans la table.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
