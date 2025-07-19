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
import javafx.stage.Stage;
import javafx.util.Duration;
import location.app.vehicule_location_app.models.Client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UIFenetreClientController {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Label dateTimeLabel;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button reservationsButton;

    @FXML
    private Button notificationsButton;

    @FXML
    private Button walletButton;

    @FXML
    private Button logoutButton;


    private Client currentClient;

    @FXML
    public void initialize() {
        updateDateTimeLabel();
        loadView("/views/UIDashboardClient.fxml");
        setSelectedButton(dashboardButton);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> updateDateTimeLabel()),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateDateTimeLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateTimeLabel.setText(dateFormat.format(new Date()));
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Passe le client connecté au contrôleur du portefeuille
            if ("/views/UIWallet.fxml".equals(fxmlPath)) {
                Object controller = loader.getController();
                if (controller instanceof UIWalletController && currentClient != null) {
                    ((UIWalletController) controller).setClientConnecte(currentClient);
                }
            }

            // Passe le client connecté au contrôleur des réservations
            if ("/views/UIFenetreReservation.fxml".equals(fxmlPath)) {
                Object controller = loader.getController();
                if (controller instanceof UIFenetreReservationController && currentClient != null) {
                    ((UIFenetreReservationController) controller).setClientConnecte(currentClient);
                }
            }

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
        notificationsButton.getStyleClass().remove("selected-menu-btn");

        // Ajoute la classe au bouton sélectionné
        selectedButton.getStyleClass().add("selected-menu-btn");
    }

    @FXML
    private void handleDashboardClick() {
        setSelectedButton(dashboardButton);
        loadView("/views/UIDashboardClient.fxml");
    }

    @FXML
    private void handleReservationsClick() {
        setSelectedButton(reservationsButton);
        loadView("/views/UIFenetreReservation.fxml");
    }

    @FXML
    private void handleNotificationClick() {
        setSelectedButton(notificationsButton);
        loadView("/views/UINotification.fxml");
    }

    @FXML
    private void handleWalletClick() {
        setSelectedButton(walletButton);
        loadView("/views/UIWallet.fxml");
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

    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
        //NotificationService.getInstance().setUtilisateur(currentClient);
    }
}
