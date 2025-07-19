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

    // Constructeur: s'attache au service de notification dès la création de l'instance
    public UINotificationController() {
        this.subject = Subject.getInstance();
        this.subject.attach(this);
    }

    /**
     * Set the MainFenetreController instance. This is called by MainFenetreController after loading this FXML.
     * @param mainFenetreController The instance of MainFenetreController.
     */
    public void setMainFenetreController(MainFenetreController mainFenetreController) {
        this.mainFenetreController = mainFenetreController;
    }
    public void setUIFenetreClientController(UIFenetreClientController uiClientController) {
        this.uiFenetreClientController = uiClientController;
    }

    /**
     * Définit l'utilisateur actuellement connecté. Essentiel pour filtrer les notifications.
     * @param currentUser L'instance de l'utilisateur connecté.
     */
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
        // Initialisation du service de notification
        notificationService = NotificationService.getInstance();

        if (currentUser != null) {
            chargerNotifications();
        }else if (currentClient != null) {
            chargerNotificationsClient();
        } else {
            System.err.println("Aucun utilisateur ou client n'est défini pour charger les notifications.");
        }


    }

    /**
     * Charge et affiche les notifications dans la ListView.
     * Cette méthode doit être appelée chaque fois que les notifications doivent être rafraîchies.
     */
    public void chargerNotifications() {
        if (notificationService != null && currentUser != null) {
            System.out.println("Chargement des notifications pour l'utilisateur: " + currentUser.getLogin());
            ObservableList<Notification> notifications = notificationService.getNotifications();
            notificationListView.setItems(notifications);
        } else {
            System.err.println("Impossible de charger les notifications : NotificationService ou currentUser non initialisé.");
            notificationListView.getItems().clear();
        }

        chargerListView();
    }

    private void chargerNotificationsClient() {
        if (notificationService != null && currentClient != null) {
            System.out.println("Chargement des notifications pour le client: " + currentClient.getNom());
            ObservableList<Notification> notifications = notificationService.getNotifications();
            notificationListView.setItems(notifications);
        } else {
            System.err.println("Impossible de charger les notifications : NotificationService ou currentClient non initialisé.");
            notificationListView.getItems().clear();
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

                    if (currentUser != null) {
                        boolean isReadForCurrentUser = notification.getReceptions().stream()
                                        .anyMatch(r -> r.getUtilisateur() != null &&
                                                r.getUtilisateur().equals(currentUser) &&
                                                r.isRead());

                        if (isReadForCurrentUser) {
                            // style GRIS clair (déjà lu)
                            rootBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");
                            titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px; -fx-text-fill: #888888;");
                            messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
                            rootBox.setOnMouseClicked(null);
                        } else {
                            // style BLEU (non lu)
                            rootBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #2196f3; -fx-border-width: 2; -fx-border-radius: 5;");
                            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
                            messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
                        }

                        setGraphic(rootBox);
                    } else if (currentClient != null) {
                        // Si currentClient est défini, on peut aussi vérifier les notifications pour le client
                        boolean isReadForCurrentClient = notification.getReceptions().stream()
                                .anyMatch(r -> r.getClient() != null &&
                                        r.getClient().equals(currentClient) &&
                                        r.isRead());

                        if (isReadForCurrentClient) {
                            // style GRIS clair (déjà lu)
                            rootBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");
                            titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px; -fx-text-fill: #888888;");
                            messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
                            rootBox.setOnMouseClicked(null);
                        } else {
                            // style BLEU (non lu)
                            rootBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #2196f3; -fx-border-width: 2; -fx-border-radius: 5;");
                            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
                            messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
                        }

                        setGraphic(rootBox);
                    } else {
                        // Si aucun utilisateur ou client n'est défini, on affiche un style neutre
                        rootBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");
                        titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px;");
                        messageLabel.setStyle("-fx-font-size: 12px;");
                        setGraphic(rootBox);

                    }
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
        notificationService.markAsRead(notification);
        notificationListView.refresh();

        System.out.println("Notification clicked: " + notification.getTitle()
                + " (Type: " + notification.getType() + ", ID: " + notification.getEntityId() + ")");

        // Gérer les notifications côté client
        if (uiFenetreClientController != null) {
            switch (notification.getType()) {
                case CLIENT_NEW_RESERVATION:
                case CLIENT_RESERVATION_MODIFICATION_REQUEST:
                case CLIENT_RESERVATION_CANCELLATION:
                case CLIENT_RESERVATION_CONFIRMATION:
                    controller = uiFenetreClientController.loadView("/views/UIFenetreReservation.fxml");
                    showAlert(Alert.AlertType.INFORMATION, "Réservation",
                            "Naviguer vers la réservation ID " + notification.getEntityId());
                    break;
                case CLIENT_PAYMENT_SUCCESS:
                    // TODO: Naviguer vers facture ou confirmation de paiement si nécessaire
                    break;
                default:
                    showAlert(Alert.AlertType.INFORMATION, "Notification",
                            "Type de notification client non géré : " + notification.getType());
                    break;
            }
            return;
        }

        // Gérer les notifications côté admin
        if (mainFenetreController != null) {
            switch (notification.getType()) {
                case NEW_CLIENT_REGISTRATION:
                    controller = mainFenetreController.loadView("/views/UIClient.fxml");
                    if (controller instanceof UIClientController uiClientController) {
                        uiClientController.selectClientById(notification.getEntityId());
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Nouveau Client",
                            "Naviguer vers la gestion des clients pour " + notification.getEntityId());
                    break;

                case NEW_RESERVATION:
                case RESERVATION_MODIFICATION_REQUEST:
                case RESERVATION_CANCELLATION:
                case RESERVATION_CONFIRMATION:
                case RESERVATION_REFUSED:
                    controller = mainFenetreController.loadView("/views/UIReservation.fxml");
                    if (controller instanceof UIReservationController uiReservationController) {
                        uiReservationController.selectReservationById(notification.getEntityId());
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Réservation",
                            "Naviguer vers la réservation ID " + notification.getEntityId());
                    break;

                case PAYMENT_RECEIVED:
                    controller = mainFenetreController.loadView("/views/UIFactureFinal.fxml");
                    break;

                default:
                    showAlert(Alert.AlertType.INFORMATION, "Notification",
                            "Type de notification admin non géré : " + notification.getType());
                    break;
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation",
                    "Le contrôleur principal ou client n'est pas défini.");
        }
    }


    @FXML
    private void handleMarkAllAsRead() {
        notificationService.markAllAsRead();
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

    @Override
    public void update() {
        System.out.println("UINotificationController: Mise à jour reçue du NotificationService.");

        javafx.application.Platform.runLater(() -> {
            if (notificationService != null) {
                if (currentUser != null) {
                    chargerNotifications();
                } else if (currentClient != null) {
                    chargerNotificationsClient();
                } else {
                    System.err.println("Aucun utilisateur ou client connecté lors de la mise à jour.");
                    notificationListView.getItems().clear();
                }

                notificationListView.refresh();
            } else {
                System.err.println("UINotificationController: NotificationService est null lors de la mise à jour.");
            }
        });
    }

}