package location.app.vehicule_location_app.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.observer.NotificationObserver;
import location.app.vehicule_location_app.observer.Observer;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class UIFenetreClientController extends Observer implements Initializable {

    @FXML
    public Label notificationCountLabel;
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
    private NotificationService notificationService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        notificationService = NotificationService.getInstance();
        new NotificationObserver(notificationService);

        this.subject = notificationService;
        subject.attach(this);

        updateDateTimeLabel();
        loadView("/views/UIDashboardClient.fxml");
        setSelectedButton(dashboardButton);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> updateDateTimeLabel()),
                new KeyFrame(Duration.seconds(1))

        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        Timeline autoRefreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> {
                    if (notificationService != null) {
                        notificationService.reloadNotificationsFromDB();
                        update();
                    }
                })
        );
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();

        notificationCountLabel.textProperty().bind(notificationService.unreadCountProperty().asString());
        updateNotificationButtonStyle(notificationService.getUnreadCount());
        notificationService.unreadCountProperty().addListener((obs, oldVal, newVal) -> {
            updateNotificationButtonStyle(newVal.intValue());
        });

    }


    private void updateDateTimeLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateTimeLabel.setText(dateFormat.format(new Date()));
    }

    private void updateNotificationButtonStyle(int unreadCount) {
        if (unreadCount > 0) {
            notificationsButton.setStyle("-fx-alignment: BASELINE_LEFT; -fx-text-fill: white; -fx-background-color: #ff9800; -fx-font-size: 14px; -fx-font-weight: bold;"); // Highlight if unread
        } else {
            notificationsButton.setStyle("-fx-alignment: BASELINE_LEFT; -fx-text-fill: white; -fx-background-color: transparent; -fx-font-size: 14px;");
        }
    }

    Object loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            if ("/views/UIDashboardClient.fxml".equals(fxmlPath)) {
                Object controller = loader.getController();
                if (controller instanceof UIDashboardClientController) {
                    ((UIDashboardClientController) controller).setCurrentClient(currentClient);
                }
            }

            if ("/views/UIWallet.fxml".equals(fxmlPath)) {
                Object controller = loader.getController();
                if (controller instanceof UIWalletController && currentClient != null) {
                    ((UIWalletController) controller).setClientConnecte(currentClient);
                }
            }

            if ("/views/UIFenetreReservation.fxml".equals(fxmlPath)) {
                Object controller = loader.getController();
                if (controller instanceof UIFenetreReservationController && currentClient != null) {
                    ((UIFenetreReservationController) controller).setClientConnecte(currentClient);
                } else if (controller instanceof UINotificationController && currentClient != null) {
                    ((UINotificationController) controller).setCurrentClient(currentClient);
                    ((UINotificationController) controller).setUIFenetreClientController(this);

                }
            }

            contentArea.getChildren().setAll(view);

            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            return loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load view: " + fxmlPath);
            return null;
        }
    }
    Object loadNotificationView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UINotification.fxml"));
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UINotificationController && currentClient != null) {
                ((UINotificationController) controller).setCurrentClient(currentClient);
                ((UINotificationController) controller).setUIFenetreClientController(this);
            }

            contentArea.getChildren().setAll(view);

            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load notification view.");
            return null;
        }
    }

    private void setSelectedButton(Button selectedButton) {
        dashboardButton.getStyleClass().remove("selected-menu-btn");
        reservationsButton.getStyleClass().remove("selected-menu-btn");
        notificationsButton.getStyleClass().remove("selected-menu-btn");

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
        loadNotificationView();
    }

    @FXML
    private void handleWalletClick() {
        setSelectedButton(walletButton);
        loadView("/views/UIWallet.fxml");
    }

    @FXML
    private void handleLogoutClick() throws IOException {
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
        NotificationService.getInstance().setClient(currentClient);
    }

    @Override
    public void update() {
        if (notificationService != null) {
            int count = notificationService.getUnreadCount();
            updateNotificationButtonStyle(count);
        }
    }
}
