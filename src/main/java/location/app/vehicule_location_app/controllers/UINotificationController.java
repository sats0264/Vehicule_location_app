//package location.app.vehicule_location_app.controllers;
//
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.geometry.Insets;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListCell;
//import javafx.scene.control.ListView;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.VBox;
//
//import location.app.vehicule_location_app.models.Notification;
//import location.app.vehicule_location_app.dao.NotificationService;
//import location.app.vehicule_location_app.models.Utilisateur;
//import location.app.vehicule_location_app.observer.NotificationObserver;
//
//import java.net.URL;
//import java.util.ResourceBundle;
//
//public class UINotificationController extends NotificationObserver implements Initializable {
//
//    @FXML
//    private ListView<Notification> notificationListView;
//    private NotificationService notificationService;
//    private MainFenetreController mainFenetreController;
//
//    private Utilisateur currentUser;
//
//    public UINotificationController() {
//        super(NotificationService.getInstance()); // S'attache au service de notification
//    }
//
//    /**
//     * Set the MainFenetreController instance. This is called by MainFenetreController after loading this FXML.
//     * @param mainFenetreController The instance of MainFenetreController.
//     */
//    public void setMainFenetreController(MainFenetreController mainFenetreController) {
//        this.mainFenetreController = mainFenetreController;
//    }
//    public void setCurrentUser(Utilisateur currentUser) {
//        this.currentUser = currentUser;
//    }
//
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        notificationService = NotificationService.getInstance();
//        new NotificationObserver(notificationService);
//
//
//        this.subject = notificationService;
//        subject.attach(this);
//        chargerNotifications(); // Charge les notifications au démarrage du contrôleur
//    }
//    public void chargerNotifications() {
//
//        notificationListView.setItems(notificationService.getNotifications());
//
//        // Custom cell factory to display notifications as tiles
//        notificationListView.setCellFactory(lv -> new ListCell<Notification>() {
//            private final Label titleLabel = new Label();
//            private final Label messageLabel = new Label();
//            private final Label timestampLabel = new Label();
//            private final VBox contentBox = new VBox(5); // Spacing for VBox children
//            private final HBox rootBox = new HBox(10); // Spacing for HBox children
//
//            {
//                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
//                messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
//                timestampLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999;");
//
//                contentBox.getChildren().addAll(titleLabel, messageLabel, timestampLabel);
//                HBox.setHgrow(contentBox, Priority.ALWAYS); // Allow content to grow horizontally
//
//                rootBox.getChildren().addAll(contentBox);
//                rootBox.setPadding(new Insets(10));
//                rootBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");
//
//                // Add a click listener to the cell
//                rootBox.setOnMouseClicked(event -> {
//                    Notification item = getItem();
//                    if (item != null) {
//                        handleNotificationClick(item);
//                    }
//                });
//            }
//
//            @Override
//            protected void updateItem(Notification notification, boolean empty) {
//                super.updateItem(notification, empty);
//                if (empty || notification == null) {
//                    setGraphic(null);
//                } else {
//                    titleLabel.setText(notification.getTitle());
//                    messageLabel.setText(notification.getMessage());
//                    timestampLabel.setText(notification.getTimestamp());
//
//                    // Style based on read status
//                    if (notification.getReceptions().stream().anyMatch(r -> r.getUtilisateur().equals(currentUser) && r.isRead())) {
//                        rootBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");
//                        titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px; -fx-text-fill: #888888;");
//                        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
//                    } else {
//                        rootBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #2196f3; -fx-border-width: 2; -fx-border-radius: 5;"); // Highlight unread
//                        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
//                        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
//                    }
//                    setGraphic(rootBox);
//                }
//            }
//        });
//    }
//
//    /**
//     * Handles the click event on a notification tile.
//     * Navigates to the relevant interface and marks the notification as read.
//     * @param notification The clicked notification.
//     */
//    private void handleNotificationClick(Notification notification) {
////        notificationService.markAsRead(notification, currentUser); // Mark as read
//        notificationService.markAsRead(notification);
//        System.out.println("Notification clicked: " + notification.getTitle() + " (Type: " + notification.getType() + ", ID: " + notification.getEntityId() + ")");
//
//        if (mainFenetreController != null) {
//            switch (notification.getType()) {
//                case NEW_CLIENT_REGISTRATION:
//                    mainFenetreController.loadView("/location/app/vehicule_location_app/views/UIClient.fxml");
//                    // Optional: In UIClientController, select the client by ID
//                    // This would require UIClientController to have a method like selectClientById(String id)
//                    showAlert(Alert.AlertType.INFORMATION, "Nouveau Client", "Naviguer vers la gestion des clients pour " + notification.getEntityId());
//                    break;
//                case NEW_RESERVATION:
//                    mainFenetreController.loadView("/location/app/vehicule_location_app/views/UIReservation.fxml");
//                    // Optional: In UIReservationController, select the reservation by ID
//                    // This would require UIReservationController to have a method like selectReservationById(String id)
//                    showAlert(Alert.AlertType.INFORMATION, "Nouvelle Réservation", "Naviguer vers la gestion des réservations pour " + notification.getEntityId());
//                    break;
//                case RESERVATION_MODIFICATION_REQUEST:
//                    mainFenetreController.loadView("/location/app/vehicule_location_app/views/UIReservation.fxml");
//                    // Optional: In UIReservationController, select the reservation by ID
//                    // This would require UIReservationController to have a method like selectReservationById(String id)
//                    showAlert(Alert.AlertType.INFORMATION, "Réservation", "Naviguer vers la gestion des réservations pour " + notification.getEntityId());
//                    break;
//                default:
//                    showAlert(Alert.AlertType.INFORMATION, "Notification", "Type de notification non géré : " + notification.getType());
//                    break;
//            }
//        } else {
//            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Le contrôleur principal n'est pas défini.");
//        }
//    }
//
//    @FXML
//    private void handleMarkAllAsRead() {
////        notificationService.markAllAsRead(currentUser);
//        notificationService.markAllAsRead();
//        showAlert(Alert.AlertType.INFORMATION, "Notifications", "Toutes les notifications ont été marquées comme lues.");
//    }
//
//    @FXML
//    private void handleClearAllNotifications() {
//        notificationService.getNotifications().clear();
//        showAlert(Alert.AlertType.INFORMATION, "Notifications", "Toutes les notifications ont été effacées.");
//    }
//
//    private void showAlert(Alert.AlertType alertType, String title, String message) {
//        Alert alert = new Alert(alertType);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
////    @Override
////    public void update() {
////        if (notificationService != null) {
////            notificationListView.setItems(notificationService.getNotifications());
////            notificationListView.refresh();
////        } else {
////            System.err.println("NotificationService is not initialized.");
////        }
////    }
//
//    @Override
//    public void update() {
//        if (notificationService != null) {
//            notificationListView.setItems(notificationService.getNotifications());
//            notificationListView.refresh();
//        } else {
//            System.out.println("UINotificationController: Mise à jour des notifications reçue !");
//            chargerNotifications();
//            System.err.println("NotificationService is not initialized.");
//        }
//    }
//}
//
package location.app.vehicule_location_app.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import location.app.vehicule_location_app.models.Notification;
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.models.Utilisateur;
import location.app.vehicule_location_app.observer.NotificationObserver;
import location.app.vehicule_location_app.observer.Observer; // Assurez-vous que cet import est correct

import java.net.URL;
import java.util.ResourceBundle;

public class UINotificationController extends NotificationObserver implements Initializable {

    @FXML
    private ListView<Notification> notificationListView;

    private NotificationService notificationService;
    private MainFenetreController mainFenetreController; // Si vous l'utilisez

    private Utilisateur currentUser; // L'utilisateur actuellement connecté

    // Constructeur: s'attache au service de notification dès la création de l'instance
    public UINotificationController() {
        super(NotificationService.getInstance());
    }

    /**
     * Set the MainFenetreController instance. This is called by MainFenetreController after loading this FXML.
     * @param mainFenetreController The instance of MainFenetreController.
     */
    public void setMainFenetreController(MainFenetreController mainFenetreController) {
        this.mainFenetreController = mainFenetreController;
    }

    /**
     * Définit l'utilisateur actuellement connecté. Essentiel pour filtrer les notifications.
     * @param currentUser L'instance de l'utilisateur connecté.
     */
    public void setCurrentUser(Utilisateur currentUser) {
        this.currentUser = currentUser;
        // Une fois l'utilisateur défini, assurez-vous que le NotificationService sait qui est l'utilisateur actif
        // et rafraîchissez les notifications pour cet utilisateur.
        NotificationService.getInstance().setUtilisateur(currentUser);
        chargerNotifications(); // Recharge les notifications avec le bon utilisateur
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation du service de notification
        notificationService = NotificationService.getInstance();

        // Plus besoin de `new NotificationObserver(notificationService);`
        // Ni de `this.subject = notificationService; subject.attach(this);` car le constructeur de la super-classe le fait déjà.

        // Chargement initial des notifications (elles seront vides si currentUser n'est pas encore défini)
        // Les notifications seront rechargées correctement une fois setCurrentUser() appelé.
        chargerNotifications();
    }

    /**
     * Charge et affiche les notifications dans la ListView.
     * Cette méthode doit être appelée chaque fois que les notifications doivent être rafraîchies.
     */
    public void chargerNotifications() {
        // Assurez-vous que notificationService est initialisé et qu'un utilisateur est défini
        if (notificationService != null && currentUser != null) {
            System.out.println("Chargement des notifications pour l'utilisateur: " + currentUser.getLogin());
            ObservableList<Notification> notifications = notificationService.getNotifications();
            notificationListView.setItems(notifications);
        } else {
            System.err.println("Impossible de charger les notifications : NotificationService ou currentUser non initialisé.");
            notificationListView.getItems().clear(); // Efface la liste si le service ou l'utilisateur n'est pas prêt
        }

        // Custom cell factory to display notifications as tiles
        notificationListView.setCellFactory(lv -> new ListCell<Notification>() {
            private final Label titleLabel = new Label();
            private final Label messageLabel = new Label();
            private final Label timestampLabel = new Label();
            private final VBox contentBox = new VBox(5); // Spacing for VBox children
            private final HBox rootBox = new HBox(10); // Spacing for HBox children

            {
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
                timestampLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999;");

                contentBox.getChildren().addAll(titleLabel, messageLabel, timestampLabel);
                HBox.setHgrow(contentBox, Priority.ALWAYS); // Allow content to grow horizontally

                rootBox.getChildren().addAll(contentBox);
                rootBox.setPadding(new Insets(10));
                rootBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");

                // Add a click listener to the cell
                rootBox.setOnMouseClicked(event -> {
                    Notification item = getItem();
                    if (item != null) {
                        handleNotificationClick(item);
                    }
                });
            }

            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);
                if (empty || notification == null) {
                    setGraphic(null);
                } else {
                    titleLabel.setText(notification.getTitle());
                    messageLabel.setText(notification.getMessage());
                    timestampLabel.setText(notification.getTimestamp());

                    // Style based on read status
                    // IMPORTANT: Assurez-vous que getReceptions() est bien initialisé et ne renvoie pas null.
                    // Assurez-vous que currentUser n'est pas null ici.
                    boolean isReadForCurrentUser = notification.getReceptions().stream()
                            .anyMatch(r -> currentUser != null && r.getUtilisateur() != null && r.getUtilisateur().equals(currentUser) && r.isRead());

                    if (isReadForCurrentUser) {
                        rootBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");
                        titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px; -fx-text-fill: #888888;");
                        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
                    } else {
                        rootBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #2196f3; -fx-border-width: 2; -fx-border-radius: 5;"); // Highlight unread
                        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
                        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
                    }
                    setGraphic(rootBox);
                }
            }
        });
    }

    /**
     * Handles the click event on a notification tile.
     * Navigates to the relevant interface and marks the notification as read.
     * @param notification The clicked notification.
     */
    private void handleNotificationClick(Notification notification) {
        notificationService.markAsRead(notification); // Mark as read
        System.out.println("Notification clicked: " + notification.getTitle() + " (Type: " + notification.getType() + ", ID: " + notification.getEntityId() + ")");

        if (mainFenetreController != null) {
            switch (notification.getType()) {
                case NEW_CLIENT_REGISTRATION:
                    mainFenetreController.loadView("/location/app/vehicule_location_app/views/UIClient.fxml");
                    showAlert(Alert.AlertType.INFORMATION, "Nouveau Client", "Naviguer vers la gestion des clients pour " + notification.getEntityId());
                    break;
                case NEW_RESERVATION:
                    mainFenetreController.loadView("/location/app/vehicule_location_app/views/UIReservation.fxml");
                    showAlert(Alert.AlertType.INFORMATION, "Nouvelle Réservation", "Naviguer vers la gestion des réservations pour " + notification.getEntityId());
                    break;
                case RESERVATION_MODIFICATION_REQUEST:
                    mainFenetreController.loadView("/location/app/vehicule_location_app/views/UIReservation.fxml");
                    showAlert(Alert.AlertType.INFORMATION, "Réservation", "Naviguer vers la gestion des réservations pour " + notification.getEntityId());
                    break;
                default:
                    showAlert(Alert.AlertType.INFORMATION, "Notification", "Type de notification non géré : " + notification.getType());
                    break;
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Le contrôleur principal n'est pas défini.");
        }
    }

    @FXML
    private void handleMarkAllAsRead() {
        notificationService.markAllAsRead();
        showAlert(Alert.AlertType.INFORMATION, "Notifications", "Toutes les notifications ont été marquées comme lues.");
    }

    @FXML
    private void handleClearAllNotifications() {
        // Attention: notificationService.getNotifications().clear() ne supprime pas de la base de données.
        // Cela efface seulement la liste ObservableList en mémoire du service.
        // Si vous voulez supprimer de la DB, il faut une méthode dans NotificationService.
        notificationService.getNotifications().clear();
        showAlert(Alert.AlertType.INFORMATION, "Notifications", "Toutes les notifications ont été effacées de l'affichage.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void update() {
        // Cette méthode est appelée par NotificationService quand il notifie ses observateurs.
        // Elle doit rafraîchir la liste affichée.
        System.out.println("UINotificationController: Mise à jour reçue du NotificationService.");
        // Assurez-vous que cette mise à jour se fait sur le thread d'application JavaFX
        javafx.application.Platform.runLater(() -> {
            if (notificationService != null) {
                // Recharger les notifications pour l'utilisateur actuel
                chargerNotifications();
                // Si la ListView est déjà liée à l'ObservableList du service, `refresh()` est parfois nécessaire
                // en plus de `setItems()` si la même liste est mise à jour mais les objets internes changent.
                notificationListView.refresh();
            } else {
                System.err.println("UINotificationController: NotificationService est null lors de la mise à jour.");
            }
        });
    }
}