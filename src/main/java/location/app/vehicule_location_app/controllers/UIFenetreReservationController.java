package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.models.*;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Reservation;
import location.app.vehicule_location_app.models.Vehicule;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static location.app.vehicule_location_app.controllers.Controller.reservationDao;

public class UIFenetreReservationController{

    @FXML
    private ImageView imageView;
    @FXML
    private Label marqueLabel;
    @FXML
    private Label modeleLabel;
    @FXML
    private Label immatriculationLabel;
    @FXML
    private Label nbJoursLabel;
    @FXML
    private Label statutLabel;
    @FXML
    private VBox reservationsListVBox;

    private Client clientConnecte;// à initialiser lors de la connexion
    private Vehicule vehicule;

    /**
     * Setter à appeler AVANT affichage pour initialiser le client connecté et afficher ses réservations.
     */
    public void setClientConnecte(Client client) {
        this.clientConnecte = client;
        afficherReservationsClient();
    }

    @FXML
    private void initialize() {
        // Ne rien faire ici pour la liste, elle sera chargée après setClientConnecte
    }

    public void afficherReservationsClient() {
        reservationsListVBox.getChildren().clear();
        if (clientConnecte == null) return;

        HibernateObjectDaoImpl<Reservation> reservationDao = new HibernateObjectDaoImpl<>(Reservation.class);
        List<Reservation> reservations = reservationDao.readAll();

        for (Reservation reservation : reservations) {
            if (reservation.getClient() != null && reservation.getClient().getId() == clientConnecte.getId()) {
                Vehicule vehicule = reservation.getVehicules().isEmpty() ? null : reservation.getVehicules().getFirst();
                String marque = vehicule != null ? vehicule.getMarque() : "";
                String modele = vehicule != null ? vehicule.getModele() : "";
                String immatriculation = vehicule != null ? vehicule.getImmatriculation() : "";
                String imageUrl = vehicule != null ? vehicule.getPhoto() : null;
                int nbJours = (int) java.time.temporal.ChronoUnit.DAYS.between(
                    reservation.getDateDebut(), reservation.getDateFin()
                );
                if (nbJours <= 0) nbJours = 1;

                // Crée dynamiquement la carte réservation
                VBox carte = new VBox(5);
                carte.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-background-color: #fafafa;");
                HBox hbox = new HBox(30);
                hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                // Image véhicule
                ImageView imgView = new ImageView();
                imgView.setFitHeight(120);
                imgView.setFitWidth(180);
                imgView.setPreserveRatio(true);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        String photoName = imageUrl;
                        if (photoName.contains("/")) photoName = photoName.substring(photoName.lastIndexOf('/') + 1);
                        else if (photoName.contains("\\")) photoName = photoName.substring(photoName.lastIndexOf('\\') + 1);
                        InputStream is = getClass().getResourceAsStream("/images/" + photoName);
                        if (is != null) imgView.setImage(new Image(is));
                        else imgView.setImage(new Image(imageUrl, true));
                    } catch (Exception e) {
                        imgView.setImage(null);
                    }
                }

                // Infos véhicule
                VBox infos = new VBox(8);
                infos.setAlignment(javafx.geometry.Pos.CENTER);
                Label marqueLbl = new Label("Marque : " + marque);
                marqueLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                Label modeleLbl = new Label("Modèle : " + modele);
                modeleLbl.setStyle("-fx-font-size: 14px;");
                Label immatLbl = new Label("Immatriculation : " + immatriculation);
                immatLbl.setStyle("-fx-font-size: 14px;");
                Label nbJoursLbl = new Label("Durée : " + nbJours + " jours (" +
                        reservation.getDateDebut() + " → " + reservation.getDateFin() + ")");
                nbJoursLbl.setStyle("-fx-font-size: 13px;");
                infos.getChildren().addAll(marqueLbl, modeleLbl, immatLbl, nbJoursLbl);

                // Ajout du/des chauffeurs
                if (!reservation.getChauffeurs().isEmpty()) {
                    Chauffeur chauffeur = reservation.getChauffeurs().getFirst();
                    Label chauffeurLbl = new Label("Chauffeur : " + chauffeur.getPrenom() + " " + chauffeur.getNom());
                    chauffeurLbl.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
                    infos.getChildren().add(chauffeurLbl);
                } else {
                    Label chauffeurLbl = new Label("Chauffeur : Aucun");
                    chauffeurLbl.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
                    infos.getChildren().add(chauffeurLbl);
                }
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Statut à droite
                VBox statutBox = new VBox(10);
                final Button payementBtn = new Button("Payer");
                statutBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                if (reservation.getStatut() == StatutReservation.PAYEMENT_EN_ATTENTE) {
                    payementBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
                    payementBtn.setOnAction(event -> {
                        System.out.println("Paiement en cours pour la réservation ID: " + reservation.getId());
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIFactureClient.fxml"));
                            Parent view = loader.load();
                            UIFactureClientController factureController = loader.getController();

                            factureController.setFacture(reservation);

                            Stage stage = new Stage();
                            stage.setTitle("Facture Client");
                            stage.setScene(new Scene(view));
                            stage.show();
                        }
                        catch (IOException e) {
                            System.err.println("Erreur lors du chargement de la vue de facture : " + e.getMessage());
                        }
                    });
                } else if (reservation.getStatut() == StatutReservation.APPROUVEE) {
                    payementBtn.setText("Facture");
                    payementBtn.setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-font-weight: bold;");
                    payementBtn.setOnAction(event -> {
                        System.out.println("Affichage de la facture pour la réservation ID: " + reservation.getId());
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIFacture.fxml"));
                            Parent view = loader.load();
                            UIFactureController factureController = loader.getController();

                            factureController.setFacture(reservation.getFacture());

                            Stage stage = new Stage();
                            stage.setTitle("Facture Client");
                            stage.setScene(new Scene(view));
                            stage.show();
                        }
                        catch (IOException e) {
                            System.err.println("Erreur lors du chargement de la vue de facture : " + e.getMessage());
                        }
                    });
                }
                else {
                    payementBtn.setVisible(false);
                }
                Label statutTitre = new Label("Statut de la demande :");
                statutTitre.setStyle("-fx-font-size: 13px;");
                Label statutLbl = new Label(reservation.getStatut().toString());
                String color = "#a5a5a3ff";
                if (reservation.getStatut() != null) {
                    color = switch (reservation.getStatut()) {
                        case EN_ATTENTE -> "#ff9800"; // Orange pour en attente
                        case MODIFICATION_EN_ATTENTE, ANNULATION_EN_ATTENTE -> "#6f42c1"; // Violet
                        case APPROUVEE -> "#43A047"; // Vert pour approuvée
                        case REJETEE -> "#e53935"; // Rouge pour rejetée
                        case PAYEMENT_EN_ATTENTE -> "#2196F3"; // Bleu pour paiement en attente
                        case ANNULEE -> "#e53935"; // Rouge pour annulée
                        default -> "#a5a5a3ff"; // Gris par défaut
                    };
                }
                statutLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
                statutBox.getChildren().addAll(statutTitre, statutLbl, payementBtn);

                hbox.getChildren().addAll(imgView, infos, spacer, statutBox);
                carte.getChildren().add(hbox);

                // Ajout du double-clic pour afficher le popup détail/modifier/annuler
                carte.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        showReservationDetailsPopup(reservation, vehicule);
                    }
                });

                reservationsListVBox.getChildren().add(carte);
            }
        }
    }

    // This method would replace your old 'afficherPopupReservation'
    private void showReservationDetailsPopup(Reservation reservation, Vehicule vehicule) {
        try {
            // Load the FXML for the new popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIReservationDetailPopup.fxml"));
            Parent root = loader.load();

            // Get the controller for the loaded FXML
            UIReservationDetailPopupController controller = loader.getController();

            // Create a new Stage for the popup
            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Make it modal
            popupStage.setTitle("Détail de la Réservation N°" + reservation.getId());

            // Pass the Stage to the controller so it can close itself
            controller.setDialogStage(popupStage);

            // Set the reservation and vehicle data in the popup controller
            controller.setReservationAndVehicule(reservation, vehicule);

            // Set the scene and show the popup
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.showAndWait(); // Show and wait until the popup is closed

            // After the popup is closed, you might want to refresh the parent view
            // if any changes were made (e.g., status update after cancellation/modification request)
            // For example:
            // refreshReservationsList(); // Call a method in your parent controller to refresh
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any errors during popup loading
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher les détails de la réservation.");
        }
    }

    // You would also need a showAlert method in this class if it doesn't exist
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
