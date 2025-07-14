package location.app.vehicule_location_app.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainFenetreController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Label dateTimeLabel;

    @FXML
    private Button dashboardButton;
    @FXML
    private Button reservationsButton;
    @FXML
    private Button clientsButton;
    @FXML
    private Button voituresButton;
    @FXML
    private Button notificationsButton;
    @FXML
    private Button logoutButton;

    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
        updateDateTimeLabel();
        loadView("/views/UIDashboard.fxml");
        setSelectedButton(dashboardButton);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> updateDateTimeLabel()),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Updates the date and time label in the top bar.
     */
    private void updateDateTimeLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateTimeLabel.setText(dateFormat.format(new Date()));
    }

    /**
     * Helper method to load FXML content into the contentArea.
     * @param fxmlPath The path to the FXML file to load.
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);

            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load view: " + fxmlPath);
        }
    }

    private void setSelectedButton(Button selectedButton) {
        dashboardButton.getStyleClass().remove("selected-menu-btn");
        reservationsButton.getStyleClass().remove("selected-menu-btn");
        clientsButton.getStyleClass().remove("selected-menu-btn");
        voituresButton.getStyleClass().remove("selected-menu-btn");
        notificationsButton.getStyleClass().remove("selected-menu-btn");

        // Ajoute la classe au bouton sélectionné
        selectedButton.getStyleClass().add("selected-menu-btn");
    }

    @FXML
    private void handleDashboardClick() {
        setSelectedButton(dashboardButton);
        loadView("/views/UIDashboard.fxml");
    }

    @FXML
    private void handleReservationsClick() {
        setSelectedButton(reservationsButton);
        loadView("/views/UIReservation.fxml");
    }

    @FXML
    private void handleClientsClick() {
        setSelectedButton(clientsButton);
        loadView("/views/UIClient.fxml");
    }

    @FXML
    private void handleVoituresClick() {
        setSelectedButton(voituresButton);
        loadView("/views/UIVehicule.fxml");
    }

    @FXML
    private void handleNotificationClick() {
        setSelectedButton(notificationsButton);
        loadView("/views/UINotification.fxml");
    }
    @FXML
    private void handleLogoutClick() throws IOException {
        // Logique de déconnexion
        System.out.println("Déconnexion...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UILogin.fxml"));
        Parent mainRoot = loader.load();
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setScene(new Scene(mainRoot));
        stage.centerOnScreen();
        stage.setTitle("Connexion - Application de Location de Véhicules");
        stage.show();
    }
}
