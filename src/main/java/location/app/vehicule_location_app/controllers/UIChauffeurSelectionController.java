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
            List<Chauffeur> allChauffeurs = chauffeurDao.list();

            List<Chauffeur> availableChauffeurs = allChauffeurs.stream()
                    .filter(c -> c.getStatut() == Statut.DISPONIBLE)
                    .toList();

            if (availableChauffeurs.isEmpty()) {
                noChauffeurMessage.setVisible(true);
                noChauffeurMessage.setManaged(true);
                selectChauffeurBtn.setDisable(true);
                return;
            }

            noChauffeurMessage.setVisible(false);
            noChauffeurMessage.setManaged(false);

            HBox currentRow = null;
            for (int i = 0; i < availableChauffeurs.size(); i++) {
                Chauffeur chauffeur = availableChauffeurs.get(i);

                if (i % 3 == 0) {
                    currentRow = new HBox(20);
                    currentRow.setAlignment(Pos.CENTER);
                    chauffeursDisplayContainer.getChildren().add(currentRow);
                }

                VBox chauffeurCard = createChauffeurCard(chauffeur);
                currentRow.getChildren().add(chauffeurCard);

                chauffeurCard.setOnMouseClicked(event -> {
                    if (selectedChauffeur == chauffeur) {
                        selectedChauffeur = null;
                        chauffeurCard.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;");
                        selectChauffeurBtn.setDisable(true);
                    } else {
                        if (selectedChauffeur != null) {
                            for (Node node : chauffeursDisplayContainer.getChildren()) {
                                if (node instanceof HBox) {
                                    for (Node card : ((HBox) node).getChildren()) {
                                        if (card instanceof VBox && card.getUserData() == selectedChauffeur) {
                                            card.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        selectedChauffeur = chauffeur;
                        chauffeurCard.setStyle("-fx-border-color: #007bff; -fx-border-width: 3px; -fx-border-radius: 5px; -fx-background-color: #e6f2ff;"); // Highlight selected
                        selectChauffeurBtn.setDisable(false);
                    }
                });
                chauffeurCard.setUserData(chauffeur);
            }

        } catch (DAOException e) {
            System.err.println("Erreur lors du chargement des chauffeurs: " + e.getMessage());
            e.printStackTrace();
            showAlert();
        }
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
