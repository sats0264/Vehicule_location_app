package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import location.app.vehicule_location_app.models.Chauffeur;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Facture;
import location.app.vehicule_location_app.models.Reservation;

import java.io.IOException;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class UIHistoriqueClientController {

    @FXML
    private ListView<Reservation> reservationListView;

    @FXML
    private Button backButton;

    private Client currentClient;
    private MainFenetreController mainFenetreController;

    public void setClient(Client client) {
        this.currentClient = client;
        loadReservations();
    }

    public void setMainFenetreController(MainFenetreController controller) {
        this.mainFenetreController = controller;
    }

    private void loadReservations() {
        if (currentClient != null) {
            List<Reservation> reservations = currentClient.getReservations();
            reservationListView.getItems().setAll(reservations);

            reservationListView.setCellFactory(list -> new ListCell<>() {
                private final ImageView imageView = new ImageView();
                private final Label dateDebutLabel = new Label();
                private final Label dateFinLabel = new Label();
                private final Label dureeLabel = new Label();
                private final Label prixLabel = new Label();
                private final ImageView imageViewChauffeur = new ImageView();
                private final Label chauffeurNomLabel = new Label();
                private final Label chauffeurPrenomLabel = new Label();
                private final Label chauffeurTelLabel = new Label();
                private final Label statutLabel = new Label();
                private final Button factureButton = new Button("Facture");
                private final VBox centerBox = new VBox(5);
                private final VBox chauffeurBox = new VBox(5);
                private final HBox rootBox = new HBox(10);

                {
                    // IMAGE
                    imageView.setFitWidth(120);
                    imageView.setFitHeight(80);
                    imageView.setStyle("-fx-effect: dropshadow(gaussian, lightgray, 5, 0, 0, 1); " +
                            "-fx-border-radius: 10; -fx-background-radius: 10;");

                    imageViewChauffeur.setFitWidth(120);
                    imageViewChauffeur.setFitHeight(80);
                    imageViewChauffeur.setStyle("-fx-effect: dropshadow(gaussian, lightgray, 5, 0, 0, 1); " +
                            "-fx-border-radius: 10; -fx-background-radius: 10;");

                    // CENTER INFO
                    dateDebutLabel.setStyle("-fx-font-weight: bold;");
                    dateFinLabel.setStyle("-fx-font-style: italic;");
                    dureeLabel.setStyle("-fx-font-weight: bold;");
                    prixLabel.setStyle("-fx-text-fill: green;");

                    centerBox.getChildren().addAll(dateDebutLabel, dateFinLabel,dureeLabel, prixLabel);
                    centerBox.setAlignment(Pos.TOP_LEFT);
                    centerBox.setMaxWidth(Double.MAX_VALUE);
                    centerBox.setStyle("-fx-spacing: 10;");
                    HBox.setHgrow(centerBox, Priority.ALWAYS);

                    // CHAUFFEUR INFO
                    chauffeurNomLabel.setStyle("-fx-font-weight: bold;");
                    chauffeurPrenomLabel.setStyle("-fx-font-style: italic;");
                    chauffeurTelLabel.setStyle("-fx-text-fill: gray;");
                    chauffeurBox.getChildren().addAll(chauffeurNomLabel, chauffeurTelLabel);


                    // STATUT
                    statutLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                    statutLabel.setMinWidth(150);
                    statutLabel.setAlignment(Pos.CENTER_RIGHT);

                    factureButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-font-weight: bold;");

                    // ROOT CONTAINER
                    rootBox.setPadding(new Insets(10));
                    rootBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 1; -fx-border-radius: 8;");
                    rootBox.setAlignment(Pos.CENTER);
                    rootBox.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(rootBox, Priority.ALWAYS);


                    // Add a click listener to the cell
                    rootBox.setOnMouseClicked(event -> {
                        Reservation selectedReservation = getItem();
                        if (selectedReservation != null) {
                            handleReservationClick(selectedReservation);
                        }
                    });
                }

                @Override
                protected void updateItem(Reservation reservation, boolean empty) {
                    super.updateItem(reservation, empty);
                    if (empty || reservation == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // INFO

                        long duree = ChronoUnit.DAYS.between(reservation.getDateDebut(),reservation.getDateFin()) +1;

                        dateDebutLabel.setText("Début : " + reservation.getDateDebut());
                        dateFinLabel.setText("Fin : " + reservation.getDateFin());
                        dureeLabel.setText("Durée : " + duree + " jours");

                        double prixTotal = reservation.getVehicules()
                                .stream()
                                .mapToDouble(v -> (Double) v.getTarif())
                                .sum()* duree;
                        prixLabel.setText("Prix : " + prixTotal + " FCFA");

                        // STATUT & COULEUR
                        String statut = reservation.getStatut().toString();
                        statutLabel.setText(statut);

                        String color = switch (statut) {
                            case "EN_ATTENTE" -> "orange";
                            case "APPROUVEE" -> "green";
                            case "REJETEE", "ANNULEE" -> "red";
                            case "PAYEMENT_EN_ATTENTE" -> "blue";
                            default -> "black";
                        };
                        statutLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

                        // IMAGE
                        if (!reservation.getVehicules().isEmpty()) {
                            String imagePath = reservation.getVehicules().getFirst().getPhoto();
                            URL resource = getClass().getResource(imagePath);
                            if (resource != null) {
                                imageView.setImage(new Image(resource.toExternalForm()));
                            } else {
                                imageView.setImage(null);
                                System.err.println("Image non trouvée : " + imagePath);
                            }
                        } else {
                            imageView.setImage(null); // ou une image par défaut
                            System.err.println("Aucun véhicule associé à cette réservation.");
                        }

                        // CHAUFFEURS
                        List<Chauffeur> chauffeurs = reservation.getChauffeurs();
                        if (chauffeurs != null && !chauffeurs.isEmpty()) {
                            for (Chauffeur chauffeur : chauffeurs) {
                                Label nom = new Label("Nom : " + chauffeur.getNom());
                                nom.setStyle("-fx-font-weight: bold;");
                                Label tel = new Label("Tél : " + chauffeur.getTelephone());
                                tel.setStyle("-fx-text-fill: gray;");

                                VBox infoChauffeurBox = new VBox(5, nom, tel);

                                ImageView img = new ImageView();
                                img.setFitWidth(100);
                                img.setFitHeight(70);
                                if (chauffeur.getPhoto() != null) {
                                    URL chauffeurPhoto = getClass().getResource(chauffeur.getPhoto());
                                    if (chauffeurPhoto != null) {
                                        img.setImage(new Image(chauffeurPhoto.toExternalForm()));
                                    } else {
                                        System.err.println("Image chauffeur non trouvée : " + chauffeur.getPhoto());
                                    }
                                }

                                HBox chauffeurItemBox = new HBox(10, img, infoChauffeurBox);
                                chauffeurBox.getChildren().add(chauffeurItemBox);
                            }
                        }

                        // Composition finale
                        rootBox.getChildren().clear();
                        rootBox.getChildren().addAll(imageView, centerBox);

                        if (!chauffeurBox.getChildren().isEmpty()) {
                            rootBox.getChildren().add(chauffeurBox);
                        }

                        if ("APPROUVEE".equals(statut)) {
                            rootBox.getChildren().addAll(statutLabel, factureButton);
                            factureButton.setOnAction(e -> {
                                try {
                                    handleVoirFacture(reservation.getFacture());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        } else {
                            rootBox.getChildren().add(statutLabel);
                        }

                        setGraphic(rootBox);
                    }
                }
            });
        }
    }

    @FXML
    private void handleBack() {
        mainFenetreController.loadView("/views/UIClient.fxml");
    }

    private void handleVoirFacture(Facture facture) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIFacture.fxml"));
        Parent view = loader.load();
        UIFactureController factureController = loader.getController();

        factureController.setFacture(facture);

        Stage stage = new Stage();
        stage.setTitle("Facture Client");
        stage.setScene(new Scene(view));
        stage.show();
    }

    private void handleReservationClick(Reservation selectedReservation) {
    }
}
