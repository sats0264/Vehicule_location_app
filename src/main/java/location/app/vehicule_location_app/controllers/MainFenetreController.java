package location.app.vehicule_location_app.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Utilisateur;
import location.app.vehicule_location_app.observer.NotificationObserver;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainFenetreController extends Observer implements Initializable {

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
    private Button chauffeursButton;
    @FXML
    private Button notificationsButton;
    @FXML
    private Label notificationCountLabel;
    @FXML
    private Button logoutButton;

    private Utilisateur currentUser;

    private NotificationService notificationService;

    public MainFenetreController() {
        this.subject = Subject.getInstance();
        this.subject.attach(this);
    }

    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        NotificationService.getInstance().setUtilisateur(utilisateur);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        notificationService = NotificationService.getInstance();
        new NotificationObserver(notificationService);

        this.subject = notificationService;
        subject.attach(this);

        updateDateTimeLabel();
        loadView("/views/UIDashboard.fxml");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> updateDateTimeLabel()),
                new KeyFrame(Duration.seconds(1))

        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        Timeline autoRefreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> {
                    // Cette ligne va forcer le rafraîchissement du compteur de notifications
                    if (notificationService != null) {
                        notificationService.reloadNotificationsFromDB();
                        update();
                    }
                })
        );
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();

        notificationCountLabel.textProperty().bind(notificationService.unreadCountProperty().asString());
        updateNotificationButtonStyle(notificationService.getUnreadCount());
        notificationService.unreadCountProperty().addListener((obs, oldVal, newVal) -> {
            updateNotificationButtonStyle(newVal.intValue());
        });
    }
    /**
     * Updates the date and time label in the top bar.
     */
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
            Object controller = loader.getController();

            if (controller instanceof UIClientController uiClientController) {
                uiClientController.setMainFenetreController(this);
            } else if (controller instanceof UIHistoriqueClientController historiqueController) {
                historiqueController.setMainFenetreController(this);
            } else if (controller instanceof UINotificationController notificationController) {
                notificationController.setMainFenetreController(this);
                notificationController.setCurrentUser(currentUser);
            }

            contentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load view: " + fxmlPath);
            return null;
        }
    }

    @FXML
    private void handleDashboardClick() {
        loadView("/views/UIDashboard.fxml");
    }

    @FXML
    private void handleReservationsClick() {
        loadView("/views/UIReservation.fxml");
    }

    @FXML
    private void handleClientsClick() {
        loadView("/views/UIClient.fxml");
    }


    @FXML
    private void handleVoituresClick() {
        loadView("/views/UIVehicule.fxml");
    }

    @FXML
    private void handleChauffeursClick() {
        loadView("/views/UIChauffeur.fxml");
    }

    @FXML
    private void handleNotificationClick() {
        loadView("/views/UINotification.fxml");
    }

    public void showUIHistoriqueClient (Client selectedClient) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIHistoriqueClient.fxml"));
        Parent view = loader.load();
        contentArea.getChildren().setAll(view);
        UIHistoriqueClientController uiHistoriqueClientController = loader.getController();
        uiHistoriqueClientController.setMainFenetreController(this);
        uiHistoriqueClientController.setClient(selectedClient);
    }

    @FXML
    private void handleLogoutClick() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de Déconnexion");
        alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        alert.setContentText("Vous allez être redirigé vers la page de connexion.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Déconnexion...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UILogin.fxml"));
            Parent mainRoot = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(mainRoot));
            stage.centerOnScreen();
            stage.setTitle("Connexion - Application de Location de Véhicules");
            stage.show();
        } else {
            System.out.println("Déconnexion annulée.");
        }
    }

    @Override
    public void update() {
        if (notificationService != null) {
            int count = notificationService.getUnreadCount();
            updateNotificationButtonStyle(count);
        }
    }
}
