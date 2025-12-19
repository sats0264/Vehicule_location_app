package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Chauffeur;
import location.app.vehicule_location_app.models.Statut;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static location.app.vehicule_location_app.controllers.Controller.chauffeurDao;

public class UIChauffeurSelectionController {

    @FXML
    private VBox chauffeursDisplayContainer;
    @FXML
    private Label noChauffeurMessage;
    @FXML
    private Button selectChauffeurBtn;
    @FXML
    private Button cancelBtn;

    private Chauffeur selectedChauffeur;

    private static final String DEFAULT_CHAUFFEUR_IMAGE_PATH = "/images/man.png";

    @FXML
    public void initialize() {
        selectChauffeurBtn.setOnAction(event -> handleSelectChauffeur());
        cancelBtn.setOnAction(event -> handleCancel());

        selectChauffeurBtn.setDisable(true);
        noChauffeurMessage.setVisible(false);
        noChauffeurMessage.setManaged(false);
    }

    public void initData(LocalDate startDate, LocalDate endDate) {
        loadAvailableChauffeurs();
    }

    private void loadAvailableChauffeurs() {
        chauffeursDisplayContainer.getChildren().clear();
        try {
            List<Chauffeur> available = chauffeurDao.list().stream()
                    .filter(c -> c.getStatut() == Statut.DISPONIBLE)
                    .toList();

            updateEmptyState(available.isEmpty());
            if (!available.isEmpty()) {
                populateChauffeurGrid(available);
            }
        } catch (DAOException e) {
            handleLoadError(e);
        }
    }

    private void updateEmptyState(boolean isEmpty) {
        noChauffeurMessage.setVisible(isEmpty);
        noChauffeurMessage.setManaged(isEmpty);
        selectChauffeurBtn.setDisable(isEmpty);
    }

    private void populateChauffeurGrid(List<Chauffeur> availableChauffeurs) {
        HBox currentRow = null;
        for (int i = 0; i < availableChauffeurs.size(); i++) {
            if (i % 3 == 0) {
                currentRow = createNewRow();
                chauffeursDisplayContainer.getChildren().add(currentRow);
            }

            Chauffeur chauffeur = availableChauffeurs.get(i);
            VBox chauffeurCard = createChauffeurCard(chauffeur);
            chauffeurCard.setUserData(chauffeur);
            chauffeurCard.setOnMouseClicked(event -> handleChauffeurSelection(chauffeur, chauffeurCard));

            if (currentRow != null) {
                currentRow.getChildren().add(chauffeurCard);
            }
        }
    }

    private HBox createNewRow() {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private void handleChauffeurSelection(Chauffeur chauffeur, VBox card) {
        if (selectedChauffeur == chauffeur) {
            deselectCurrent();
        } else {
            if (selectedChauffeur != null) {
                resetAllCardStyles();
            }
            selectNewChauffeur(chauffeur, card);
        }
    }

    private void deselectCurrent() {
        selectedChauffeur = null;
        resetAllCardStyles();
        selectChauffeurBtn.setDisable(true);
    }

    private void selectNewChauffeur(Chauffeur chauffeur, VBox card) {
        selectedChauffeur = chauffeur;
        card.setStyle("-fx-border-color: #007bff; -fx-border-width: 3px; -fx-border-radius: 5px; -fx-background-color: #e6f2ff;");
        selectChauffeurBtn.setDisable(false);
    }

    private void resetAllCardStyles() {
        String defaultStyle = "-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;";
        for (Node rowNode : chauffeursDisplayContainer.getChildren()) {
            if (rowNode instanceof HBox row) {
                for (Node cardNode : row.getChildren()) {
                    cardNode.setStyle(defaultStyle);
                }
            }
        }
    }

    private void handleLoadError(DAOException e) {
        System.err.println("Erreur lors du chargement des chauffeurs: " + e.getMessage());
        e.printStackTrace();
        showAlert();
    }
    private VBox createChauffeurCard(Chauffeur chauffeur) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(180, 220);
        card.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 10px;");

        ImageView photoView = new ImageView();
        photoView.setFitHeight(100);
        photoView.setFitWidth(100);
        photoView.setPreserveRatio(true);
        loadChauffeurImage(photoView, chauffeur.getPhoto());

        Label nameLabel = new Label(chauffeur.getPrenom() + " " + chauffeur.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nameLabel.setWrapText(true);

        Label phoneLabel = new Label(chauffeur.getTelephone());
        phoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        card.getChildren().addAll(photoView, nameLabel, phoneLabel);
        return card;
    }

    private void loadChauffeurImage(ImageView imageView, String imageUrl) {
        Image image = null;
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                InputStream is = getClass().getResourceAsStream(imageUrl);
                if (is != null) {
                    image = new Image(is);
                } else {
                    image = new Image(imageUrl, true);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image du chauffeur: " + e.getMessage());
        }

        if (image != null && !image.isError()) {
            imageView.setImage(image);
        } else {
            InputStream defaultIs = getClass().getResourceAsStream(DEFAULT_CHAUFFEUR_IMAGE_PATH);
            if (defaultIs != null) {
                imageView.setImage(new Image(defaultIs));
            } else {
                System.err.println("Default chauffeur image not found: " + DEFAULT_CHAUFFEUR_IMAGE_PATH);
            }
        }
    }


    private void handleSelectChauffeur() {
        closeWindow();
    }

    private void handleCancel() {
        selectedChauffeur = null;
        closeWindow();
    }

    public Chauffeur getSelectedChauffeur() {
        return selectedChauffeur;
    }

    private void closeWindow() {
        Stage stage = (Stage) selectChauffeurBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Erreur de chargement");
        alert.setHeaderText(null);
        alert.setContentText("Impossible de charger la liste des chauffeurs.");
        alert.showAndWait();
    }
}
