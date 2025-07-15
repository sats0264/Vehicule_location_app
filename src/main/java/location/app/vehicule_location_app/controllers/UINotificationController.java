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
import location.app.vehicule_location_app.observer.Observer;

import java.net.URL;
import java.util.ResourceBundle;

public class UINotificationController extends NotificationObserver implements Initializable {

    @FXML
    private ListView<Notification> notificationListView;

    private NotificationService notificationService;
    private MainFenetreController mainFenetreController;

    private Utilisateur currentUser;

    public UINotificationController() {
        super(NotificationService.getInstance()); // S'attache au service de notification
    }

    /**
     * Set the MainFenetreController instance. This is called by MainFenetreController after loading this FXML.
     * @param mainFenetreController The instance of MainFenetreController.
     */
    public void setMainFenetreController(MainFenetreController mainFenetreController) {
        this.mainFenetreController = mainFenetreController;
    }
    public void setCurrentUser(Utilisateur currentUser) {
        this.currentUser = currentUser;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        notificationService = NotificationService.getInstance();
        new NotificationObserver(notificationService);


        this.subject = notificationService;
        subject.attach(this);
        chargerNotifications(); // Charge les notifications au démarrage du contrôleur
    }
    public void chargerNotifications() {
//        // Récupère la liste des notifications de l'utilisateur actif
//        ObservableList<Notification> notifications = NotificationService.getInstance().getNotifications();
//
//        // Lie la liste à votre ListView/TableView
//        notificationListView.setItems(notifications);
//
//        // Optionnel: configurer comment les notifications sont affichées dans la liste
//         notificationListView.setCellFactory(param -> new ListCell<Notification>() {
//             @Override
//             protected void updateItem(Notification item, boolean empty) {
//                 super.updateItem(item, empty);
//                 if (empty || item == null) {
//                     setText(null);
//                 } else {
//                     setText(item.getMessage() + " (" + item.getTimestamp() + ")");
//                 }
//             }
//         });

        notificationListView.setItems(notificationService.getNotifications());

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
                    if (notification.getReceptions().stream().anyMatch(r -> r.getUtilisateur().equals(currentUser) && r.isRead())) {
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
//    @FXML
//    public void initialize() {
//        notificationService = NotificationService.getInstance();
//        new NotificationObserver(notificationService);
//
//
//        this.subject = notificationService;
//        subject.attach(this);
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

    /**
     * Handles the click event on a notification tile.
     * Navigates to the relevant interface and marks the notification as read.
     * @param notification The clicked notification.
     */
    private void handleNotificationClick(Notification notification) {
//        notificationService.markAsRead(notification, currentUser); // Mark as read
        notificationService.markAsRead(notification);
        System.out.println("Notification clicked: " + notification.getTitle() + " (Type: " + notification.getType() + ", ID: " + notification.getEntityId() + ")");

        if (mainFenetreController != null) {
            switch (notification.getType()) {
                case NEW_CLIENT_REGISTRATION:
                    mainFenetreController.loadView("/location/app/vehicule_location_app/views/UIClient.fxml");
                    // Optional: In UIClientController, select the client by ID
                    // This would require UIClientController to have a method like selectClientById(String id)
                    showAlert(Alert.AlertType.INFORMATION, "Nouveau Client", "Naviguer vers la gestion des clients pour " + notification.getEntityId());
                    break;
                case NEW_RESERVATION:
                    mainFenetreController.loadView("/location/app/vehicule_location_app/views/UIReservation.fxml");
                    // Optional: In UIReservationController, select the reservation by ID
                    // This would require UIReservationController to have a method like selectReservationById(String id)
                    showAlert(Alert.AlertType.INFORMATION, "Nouvelle Réservation", "Naviguer vers la gestion des réservations pour " + notification.getEntityId());
                    break;
                case RESERVATION_MODIFICATION_REQUEST:
                    mainFenetreController.loadView("/location/app/vehicule_location_app/views/UIReservation.fxml");
                    // Optional: In UIReservationController, select the reservation by ID
                    // This would require UIReservationController to have a method like selectReservationById(String id)
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
//        notificationService.markAllAsRead(currentUser);
        notificationService.markAllAsRead();
        showAlert(Alert.AlertType.INFORMATION, "Notifications", "Toutes les notifications ont été marquées comme lues.");
    }

    @FXML
    private void handleClearAllNotifications() {
        notificationService.getNotifications().clear();
        showAlert(Alert.AlertType.INFORMATION, "Notifications", "Toutes les notifications ont été effacées.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

//    @Override
//    public void update() {
//        if (notificationService != null) {
//            notificationListView.setItems(notificationService.getNotifications());
//            notificationListView.refresh();
//        } else {
//            System.err.println("NotificationService is not initialized.");
//        }
//    }
    @Override
    public void update() {
        if (notificationService != null) {
            notificationListView.setItems(notificationService.getNotifications());
            notificationListView.refresh();
        } else {
            System.out.println("UINotificationController: Mise à jour des notifications reçue !");
            chargerNotifications();
            System.err.println("NotificationService is not initialized.");
        }
    }
}

