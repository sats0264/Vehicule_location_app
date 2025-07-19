package location.app.vehicule_location_app.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import location.app.vehicule_location_app.models.Chauffeur;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

import java.io.IOException;

import static location.app.vehicule_location_app.controllers.Controller.controllerChauffeurList;

public class UIChauffeurController extends Observer {
    @FXML
    private TableView<Chauffeur> chauffeursTable;
    @FXML
    private TableColumn<Chauffeur, String> nomColumn;
    @FXML
    private TableColumn<Chauffeur, String> prenomColumn;
    @FXML
    private TableColumn<Chauffeur, String> telephoneColumn;
    @FXML
    private TableColumn<Chauffeur, String> statutColumn;
    @FXML
    private TableColumn<Chauffeur, String> photoColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button detailButton;


    private ObservableList<Chauffeur> chauffeurList = FXCollections.observableArrayList();

    public UIChauffeurController() {
        this.subject = Subject.getInstance();
        this.subject.attach(this);
    }

    @FXML
    private void initialize() {
        chauffeurList = FXCollections.observableArrayList(controllerChauffeurList);

        nomColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getNom() != null
                        ? cellData.getValue().getNom() : ""));
        prenomColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getPrenom() != null
                        ? cellData.getValue().getPrenom() : ""));
        telephoneColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getTelephone() != null
                        ? cellData.getValue().getTelephone() : ""));
        statutColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getStatut() != null
                        ? cellData.getValue().getStatut().toString() : ""));
        photoColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getPhoto()));

        chauffeursTable.setItems(chauffeurList);
        chargerListChauffeurs();
    }

    private void chargerListChauffeurs() {
        chauffeurList.setAll(controllerChauffeurList);
        System.out.println("Chauffeurs list loaded with " + chauffeurList.size() + " chauffeurs.");
    }

    public void handleAddChauffeur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIAddChauffeur.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un chauffeur");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            chargerListChauffeurs();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleDetailChauffeur() {
        Chauffeur selectedChauffeur = chauffeursTable.getSelectionModel().getSelectedItem();
        if (selectedChauffeur != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIDetailChauffeur.fxml"));
                Parent root = loader.load();
                UIDetailChauffeurController detailController = loader.getController();
                detailController.setChauffeur(selectedChauffeur);

                Stage stage = new Stage();
                stage.setTitle("Détails du chauffeur");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update() {
        chargerListChauffeurs();
    }
}
