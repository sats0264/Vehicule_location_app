package location.app.vehicule_location_app.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

import javafx.util.Duration;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Notification;
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.models.Utilisateur;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

import java.net.URL;
import java.util.ResourceBundle;

public class UINotificationController extends Observer implements Initializable {

    @FXML
    private ListView<Notification> notificationListView;

    private NotificationService notificationService;
    private MainFenetreController mainFenetreController;

    private Object controller;

    private Utilisateur currentUser;
    private Client currentClient;
    private UIFenetreClientController uiFenetreClientController;

    public UINotificationController() {
        this.subject = Subject.getInstance();
        this.subject.attach(this);
    }

    public void setMainFenetreController(MainFenetreController mainFenetreController) {
        this.mainFenetreController = mainFenetreController;
    }
    public void setUIFenetreClientController(UIFenetreClientController uiClientController) {
        this.uiFenetreClientController = uiClientController;
    }

    public void setCurrentUser(Utilisateur currentUser) {
        this.currentUser = currentUser;
        NotificationService.getInstance().setUtilisateur(currentUser);
        chargerNotifications();
    }
    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
        NotificationService.getInstance().setClient(currentClient);
        chargerNotificationsClient();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        notificationService = NotificationService.getInstance();

        if (currentUser != null) {
            chargerNotifications();
        }else if (currentClient != null) {
            chargerNotificationsClient();
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10), event -> update())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    public void chargerNotifications() {
        if (notificationService != null && currentUser != null) {
            ObservableList<Notification> notifications = notificationService.getNotifications();
            notificationListView.setItems(notifications);
        } else {
            notificationListView.getItems().clear();
            showAlert(Alert.AlertType.WARNING, "Aucune notification",
                    "Aucune notification disponible pour l'utilisateur actuel.");
        }

        chargerListView();
    }

    private void chargerNotificationsClient() {
        if (notificationService != null && currentClient != null) {
            ObservableList<Notification> notifications = notificationService.getNotifications();
            notificationListView.setItems(notifications);
        } else {
            notificationListView.getItems().clear();
            showAlert(Alert.AlertType.WARNING, "Aucune notification",
                    "Aucune notification disponible pour le client actuel.");
        }

        chargerListView();
    }

    private void chargerListView() {
        notificationListView.setCellFactory(lv -> new ListCell<Notification>() {
            private final Label titleLabel = new Label();
            private final Label messageLabel = new Label();
            private final Label timestampLabel = new Label();
            private final VBox contentBox = new VBox(5);
            private final HBox rootBox = new HBox(10);

            {
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
                timestampLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999;");

                contentBox.getChildren().addAll(titleLabel, messageLabel, timestampLabel);
                HBox.setHgrow(contentBox, Priority.ALWAYS);

                rootBox.getChildren().addAll(contentBox);
                rootBox.setPadding(new Insets(10));
                rootBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");

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

                    if (currentUser != null) {
                        boolean isReadForCurrentUser = notification.getReceptions().stream()
                                        .anyMatch(r -> r.getUtilisateur() != null &&
                                                r.getUtilisateur().equals(currentUser) &&
                                                r.isRead());

                        if (isReadForCurrentUser) {
                            rootBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");
                            titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px; -fx-text-fill: #888888;");
                            messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
                            rootBox.setOnMouseClicked(null);
                        } else {
                            rootBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #2196f3; -fx-border-width: 2; -fx-border-radius: 5;");
                            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
                            messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
                        }

                        setGraphic(rootBox);
                    } else if (currentClient != null) {
                        boolean isReadForCurrentClient = notification.getReceptions().stream()
                                .anyMatch(r -> r.getClient() != null &&
                                        r.getClient().equals(currentClient) &&
                                        r.isRead());

                        if (isReadForCurrentClient) {
                            rootBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");
                            titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px; -fx-text-fill: #888888;");
                            messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
                            rootBox.setOnMouseClicked(null);
                        } else {
                            rootBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #2196f3; -fx-border-width: 2; -fx-border-radius: 5;");
                            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
                            messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
                        }

                        setGraphic(rootBox);
                    } else {
                        rootBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");
                        titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px;");
                        messageLabel.setStyle("-fx-font-size: 12px;");
                        setGraphic(rootBox);

                    }
                }
            }
        });
    }

    private void handleNotificationClick(Notification notification) {
        notificationService.markAsRead(notification);
        notificationListView.refresh();

        System.out.println("Notification clicked: " + notification.getTitle()
                + " (Type: " + notification.getType() + ", ID: " + notification.getEntityId() + ")");

        if (uiFenetreClientController != null) {
            switch (notification.getType()) {
                case CLIENT_NEW_RESERVATION,CLIENT_RESERVATION_MODIFICATION_REQUEST,
                     CLIENT_RESERVATION_ANNULATION_REQUEST,CLIENT_RESERVATION_CONFIRMATION->{

                    controller = uiFenetreClientController.loadView("/views/UIFenetreReservation.fxml");
                    showNavigationMessage("Réservation", "Accès à la réservation ID " + notification.getEntityId());
                }
                case CLIENT_PAYMENT_SUCCESS -> {
                    controller = uiFenetreClientController.loadView("/views/UIFenetreReservation.fxml");
                    showNavigationMessage("Paiement", "Accès à la facture ID " + notification.getEntityId());
                }
                default -> showAlert(Alert.AlertType.INFORMATION, "Notification",
                            "Type de notification client non géré : " + notification.getType());
            }
            return;
        }


        if (mainFenetreController != null) {
            switch (notification.getType()) {
                case NEW_CLIENT_REGISTRATION -> {
                    controller = mainFenetreController.loadView("/views/UIClient.fxml");
                    if (controller instanceof UIClientController uiClientController) {
                        uiClientController.selectClientById(notification.getEntityId());
                    }
                    showNavigationMessage("Nouveau Client", "Accès au client ID " + notification.getEntityId());
                }

                case NEW_RESERVATION, RESERVATION_MODIFICATION_REQUEST, CLIENT_RESERVATION_ANNULATION_REFUSED,
                     RESERVATION_CONFIRMATION, RESERVATION_REFUSED -> {
                    controller = mainFenetreController.loadView("/views/UIReservation.fxml");
                    if (controller instanceof UIReservationController uiReservationController) {
                        uiReservationController.selectReservationById(notification.getEntityId());
                    }
                    showNavigationMessage("Réservation", "Accès à la réservation ID " + notification.getEntityId());
                }

                case PAYMENT_RECEIVED -> {
                    controller = mainFenetreController.loadView("/views/UIClient.fxml");
                    if (controller instanceof UIReservationController uiReservationController) {
                        uiReservationController.selectReservationById(notification.getEntityId());
                    }
                    showNavigationMessage("Facture", "Accès à la facture ID " + notification.getEntityId());
                }

                default -> showAlert(Alert.AlertType.INFORMATION, "Notification",
                        "Type de notification non géré : " + notification.getType());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation",
                    "Le contrôleur principal n'est pas défini.");
        }
    }


    @FXML
    private void handleMarkAllAsRead() {
        notificationService.markAllAsRead();
        if(currentUser != null) {
            chargerNotifications();
        } else if (currentClient != null) {
            chargerNotificationsClient();
        }
        showAlert(Alert.AlertType.INFORMATION, "Notifications", "Toutes les notifications ont été marquées comme lues.");
    }

    @FXML
    private void handleClearAllNotifications() {
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

    private void showNavigationMessage(String titre, String contenu) {
        showAlert(Alert.AlertType.INFORMATION, titre, contenu + "\nNavigation effectuée.");
    }


    @Override
    public void update() {
        if (currentUser != null) {
            chargerNotifications();
        } else if (currentClient != null) {
            chargerNotificationsClient();
        } else {
            System.err.println("Aucun utilisateur ou client n'est défini pour mettre à jour les notifications.");
        }
    }

}