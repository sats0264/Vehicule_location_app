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
import location.app.vehicule_location_app.models.Utilisateur;
import location.app.vehicule_location_app.observer.NotificationObserver;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainFenetreController extends NotificationObserver implements Initializable {

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
    private Label notificationCountLabel; // Label to display unread count on the button
    @FXML
    private Button logoutButton;

    private Utilisateur currentUser;


    private NotificationService notificationService;

    public MainFenetreController() {
        super(NotificationService.getInstance()); // Attachez l'observateur au service de notification
    }


    public void setCurrentUser(Utilisateur currentUser) {
//        this.currentUser = currentUser;
        NotificationService.getInstance().setUtilisateur(currentUser);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Lier le label au nombre de notifications non lues
//        notificationCountLabel.textProperty().bind(NotificationService.getInstance().unreadCountProperty().asString());

        // Assurez-vous que l'utilisateur actif est défini pour le NotificationService
        // Ceci est un exemple, l'utilisateur doit être passé après la connexion
        // NotificationService.getInstance().setUtilisateur(utilisateurConnecte); // À appeler après la connexion
        notificationService = NotificationService.getInstance();
        new NotificationObserver(notificationService);


        this.subject = notificationService;
        subject.attach(this);

        updateDateTimeLabel();
        loadView("/views/UIDashboard.fxml");
        setSelectedButton(dashboardButton);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> updateDateTimeLabel()),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();


        notificationCountLabel.textProperty().bind(notificationService.unreadCountProperty().asString());
        updateNotificationButtonStyle(notificationService.getUnreadCount());
        notificationService.unreadCountProperty().addListener((obs, oldVal, newVal) -> {
            updateNotificationButtonStyle(newVal.intValue());
        });
    }
    /**
     * Méthode d'initialisation du contrôleur.
     * Appelée automatiquement après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
//        notificationService = NotificationService.getInstance();
//        new NotificationObserver(notificationService);
//
//
//        this.subject = notificationService;
//        subject.attach(this);
//
//        updateDateTimeLabel();
//        loadView("/views/UIDashboard.fxml");
//        setSelectedButton(dashboardButton);
//
//        Timeline timeline = new Timeline(
//                new KeyFrame(Duration.seconds(0), event -> updateDateTimeLabel()),
//                new KeyFrame(Duration.seconds(1))
//        );
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();
//
//
//        notificationCountLabel.textProperty().bind(notificationService.unreadCountProperty().asString());
//        updateNotificationButtonStyle(notificationService.getUnreadCount());
//        notificationService.unreadCountProperty().addListener((obs, oldVal, newVal) -> {
//            updateNotificationButtonStyle(newVal.intValue());
//        });
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

    /**
     * Helper method to load FXML content into the contentArea.
     * @param fxmlPath The path to the FXML file to load.
     */
    void loadView(String fxmlPath) {
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
    void loadNotificationView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);

            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            UINotificationController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            controller.setMainFenetreController(this);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load notification view: " + fxmlPath);
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
        loadNotificationView("/views/UINotification.fxml");

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

//    @Override
//    public void update() {
//        if (notificationService != null) {
//            int count = notificationService.getUnreadCount();
//            updateNotificationButtonStyle(count);
//        }
//    }
@Override
public void update() {
    // Cette méthode est appelée quand le NotificationService notifie ses observateurs.
    // Ici, vous pouvez déclencher un rafraîchissement de l'UI des notifications.
    System.out.println("MainFenetreController: Mise à jour des notifications reçue !");

    // Si UINotificationController est inclus et que vous avez une référence :
    // if (uiNotificationController != null) {
    //     uiNotificationController.chargerNotifications(); // Appelez une méthode de rafraîchissement
    // }

    // Si le label est déjà lié (bind), il se mettra à jour automatiquement.
    // Si vous avez d'autres éléments UI qui affichent les notifications directement,
    // vous devriez les rafraîchir ici.
}
}
