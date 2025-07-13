package location.app.vehicule_location_app.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainFenetreController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private AnchorPane contentArea; // The area where dynamic content will be loaded

    @FXML
    private Label dateTimeLabel; // For the date and time in the top bar

    // Sidebar menu buttons
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

    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
        // Set initial date and time
        updateDateTimeLabel();

        // Load the initial view (e.g., Dashboard) into the content area
        // You'll need to create a UIDashboard.fxml and its controller for this
        loadView("/views/UIDashboard.fxml"); // Assuming a 'views' subpackage for content FXMLs
        // Or, if you want to reuse the 'voitures' interface as initial content:
        // loadView("/hello-view.fxml");

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
            contentArea.getChildren().setAll(view); // Replace existing content

            // Optional: Adjust the loaded view to fill the AnchorPane
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle error, e.g., show an alert
            System.err.println("Failed to load view: " + fxmlPath);
        }
    }

    // --- Event Handlers for Sidebar Menu Buttons ---

    @FXML
    private void handleDashboardClick() {
        System.out.println("Dashboard clicked!");
        // Load the dashboard FXML
        loadView("/views/UIDashboard.fxml");
        // You'll need to create UIDashboard.fxml and its controller
    }

    @FXML
    private void handleReservationsClick() {
        System.out.println("Reservations clicked!");
        // Load the reservations FXML
        loadView("/views/UIReservation.fxml");
    }

    @FXML
    private void handleClientsClick() {
        System.out.println("Clients clicked!");
        // Load the clients FXML
        loadView("/views/UIClient.fxml");
    }

    @FXML
    private void handleVoituresClick() {
        System.out.println("Voitures clicked!");
        // Load the existing voiture management FXML
        loadView("/views/UIVehicule.fxml");
    }

    @FXML
    private void handleNotificationClick() {
        System.out.println("Notification clicked!");
        // Load the notification FXML
        loadView("/views/UINotification.fxml");
    }
}
