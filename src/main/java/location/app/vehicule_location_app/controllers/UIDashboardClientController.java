package location.app.vehicule_location_app.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Reservation;
import location.app.vehicule_location_app.models.StatutReservation;
import location.app.vehicule_location_app.models.Vehicule;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static location.app.vehicule_location_app.controllers.Controller.controllerReservationList;

public class UIDashboardClientController extends Observer {

    public static final String TOUTES = "Toutes";
    public static final String FX_FONT_SIZE_14_PX = "-fx-font-size: 14px;";
    @FXML
    private ComboBox<String> marqueComboBox;

    @FXML
    private ComboBox<String> modeleComboBox;

    @FXML
    private RadioButton toutesRadio;

    @FXML
    private RadioButton disponibleRadio;

    @FXML
    private RadioButton nonDisponibleRadio;

    @FXML
    private ImageView imageView;

    @FXML
    private VBox voituresListVBox;

    private Client currentClient;

    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
    }

    private final Map<String, List<String>> marqueModelesMap = new HashMap<>();

    public UIDashboardClientController() {
        this.subject = Subject.getInstance();
        this.subject.attach(this);
    }

    @FXML
    public void initialize() {
        ToggleGroup disponibiliteToggleGroup = new ToggleGroup();
        toutesRadio.setToggleGroup(disponibiliteToggleGroup);
        disponibleRadio.setToggleGroup(disponibiliteToggleGroup);
        nonDisponibleRadio.setToggleGroup(disponibiliteToggleGroup);

        // Listener pour filtrer selon la disponibilité
        disponibiliteToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            afficherVoituresSelonDisponibilite();
        });

        marqueComboBox.setPromptText(TOUTES);

        // Listener pour filtrer les modèles selon la marque choisie
        marqueComboBox.setOnAction(event -> {
            String selectedMarque = marqueComboBox.getValue();
            if (selectedMarque != null && marqueModelesMap.containsKey(selectedMarque)) {
                modeleComboBox.setItems(FXCollections.observableArrayList(marqueModelesMap.get(selectedMarque)));
                modeleComboBox.setDisable(false);
            } else {
                modeleComboBox.setItems(FXCollections.observableArrayList());
                modeleComboBox.setDisable(true);
            }
            modeleComboBox.getSelectionModel().clearSelection();
            modeleComboBox.setPromptText("Tous");
        });

        modeleComboBox.setDisable(true);

        chargerMarquesEtModelesDepuisBase();
        toutesRadio.setSelected(true);
        afficherVoituresSelonDisponibilite();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10), event -> {
                    update();
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void addVoitureCard(String marque, String modele, String immatriculation, String imageUrl, String statut, double prixSansChauffeur, double prixAvecChauffeur) {
        ImageView imageView = new ImageView();
        try {
            Image image;
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    image = new Image(getClass().getResource("/images/" + imageUrl).toExternalForm());
                } catch (Exception e) {
                    image = new Image(imageUrl, true);
                }
            } else {
                image = new Image(getClass().getResource("/images/car_logo.png").toExternalForm());
            }
            imageView.setImage(image);
        } catch (Exception ex) {
            imageView.setImage(new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="));
        }

        imageView.setFitHeight(180);
        imageView.setFitWidth(270);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, #888, 8, 0.2, 0, 2);");

        Label statutLabel = new Label(statut);
        statutLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + getStatutColor(statut) + ";");
        statutLabel.setGraphic(new Circle(5, Color.web(getStatutColor(statut))));


        VBox infosBox = new VBox(8.0,
                (Node) new Label(marque + " " + modele),
                (Node) new Label("Marque: " + marque),
                (Node) new Label("Modèle: " + modele),
                (Node) new Label("Immatriculation: " + immatriculation),
                statutLabel

        );
        infosBox.setAlignment(Pos.CENTER);
        infosBox.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        infosBox.getChildren().get(1).setStyle(FX_FONT_SIZE_14_PX);
        infosBox.getChildren().get(2).setStyle(FX_FONT_SIZE_14_PX);
        infosBox.getChildren().get(3).setStyle(FX_FONT_SIZE_14_PX);
        infosBox.getChildren().get(4).setStyle("-fx-text-fill: " + getStatutColor(statut) + "; -fx-font-size: 14px;");

        VBox prixBox = new VBox(10);
        prixBox.setAlignment(Pos.CENTER_RIGHT);

        HBox prixSansChauffeurBox = new HBox(5,
                new Label("Prix sans chauffeur:"),
                new Label(String.valueOf(prixSansChauffeur) + " FCFA / jour")
        );
        prixSansChauffeurBox.setAlignment(Pos.CENTER_RIGHT);
        ((Label)prixSansChauffeurBox.getChildren().get(0)).setStyle("-fx-font-size: 13px;");
        ((Label)prixSansChauffeurBox.getChildren().get(1)).setStyle("-fx-font-weight: bold;");

        Button reserverBtn = new Button("Réserver");
        reserverBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        reserverBtn.setOnAction(e -> showReservationPopup(marque, modele, immatriculation, imageUrl, false));

        HBox prixAvecChauffeurBox = new HBox(5,
                new Label("Prix avec chauffeur:"),
                new Label(String.valueOf(prixAvecChauffeur) + " FCFA / jour")
        );
        prixAvecChauffeurBox.setAlignment(Pos.CENTER_RIGHT);
        ((Label)prixAvecChauffeurBox.getChildren().get(0)).setStyle("-fx-font-size: 13px;");
        ((Label)prixAvecChauffeurBox.getChildren().get(1)).setStyle("-fx-font-weight: bold;");

        Button reserverChauffeurBtn = new Button("Réserver Avec Chauffeur");
        reserverChauffeurBtn.setOnAction(e -> showReservationPopup(marque, modele, immatriculation, imageUrl, true));

        prixBox.getChildren().addAll(prixSansChauffeurBox, reserverBtn, prixAvecChauffeurBox, reserverChauffeurBtn);

        HBox card = new HBox(30, imageView, infosBox, prixBox);
        card.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-background-color: #fafafa;");
        card.setAlignment(Pos.CENTER_LEFT);

        voituresListVBox.getChildren().add(card);
    }

    private String getStatutColor(String statut) {
        if (statut == null) return "gray";

        return switch (statut.toUpperCase()) {
            case "DISPONIBLE" -> "green";
            case "INDISPONIBLE", "LOUE", "EN_MAINTENANCE" -> "red";
            case "RESERVE" -> "orange";
            default -> "gray";
        };
    }

    private void showReservationPopup(String marque, String modele, String immatriculation, String imageUrl, boolean avecChauffeur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIClientReservation.fxml"));
            Parent root = loader.load();

            UIClientReservationController controller = loader.getController();
            controller.initReservation(marque, modele, immatriculation, imageUrl, avecChauffeur);

            //Récupère le bon véhicule selon la carte sélectionnée
            Vehicule vehicule = findVehiculeByInfos(marque, modele, immatriculation, imageUrl);
            controller.setVehicule(vehicule);

            controller.setClient(currentClient);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nouvelle réservation");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Vehicule findVehiculeByInfos(String marque, String modele, String immatriculation, String imageUrl) {
        try {
            HibernateObjectDaoImpl<Vehicule> vehiculeDao =
                new HibernateObjectDaoImpl<>(Vehicule.class);

            List<Vehicule> vehicules = vehiculeDao.readAll();
            for (Vehicule v : vehicules) {
                if (v.getMarque().equals(marque)
                    && v.getModele().equals(modele)
                    && v.getImmatriculation().equals(immatriculation)) {
                    return v;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void chargerMarquesEtModelesDepuisBase() {
        HibernateObjectDaoImpl<Vehicule> vehiculeDao = new HibernateObjectDaoImpl<>(Vehicule.class);
        List<Vehicule> vehicules = vehiculeDao.readAll();

        Set<String> marques = new HashSet<>();
        Map<String, Set<String>> marqueModeleMap = new HashMap<>();

        for (Vehicule v : vehicules) {
            marques.add(v.getMarque());
            marqueModeleMap
                    .computeIfAbsent(v.getMarque(), k -> new HashSet<>())
                    .add(v.getModele());
        }

        List<String> marquesList = new ArrayList<>(marques);
        Collections.sort(marquesList);
        marquesList.addFirst(TOUTES);

        marqueComboBox.setItems(FXCollections.observableArrayList(marquesList));
        marqueComboBox.getSelectionModel().selectFirst();

        marqueComboBox.setOnAction(event -> {
            String selectedMarque = marqueComboBox.getValue();
            if (selectedMarque != null && !selectedMarque.equals(TOUTES) && marqueModeleMap.containsKey(selectedMarque)) {
                modeleComboBox.setItems(FXCollections.observableArrayList(marqueModeleMap.get(selectedMarque)));
                modeleComboBox.setDisable(false);
            } else {
                modeleComboBox.setItems(FXCollections.observableArrayList());
                modeleComboBox.setDisable(true);
            }
            modeleComboBox.getSelectionModel().clearSelection();
            modeleComboBox.setPromptText("Tous");

            afficherVoituresSelonDisponibilite();
        });

        modeleComboBox.setDisable(true);
    }
    private void afficherVoituresSelonDisponibilite() {
        voituresListVBox.getChildren().clear();

        HibernateObjectDaoImpl<Vehicule> vehiculeDao = new HibernateObjectDaoImpl<>(Vehicule.class);
        List<Vehicule> vehicules = vehiculeDao.readAll();

        vehicules.stream()
                .filter(this::estTypeStatutSelectionne)
                .filter(this::estMarqueEtModeleSelectionne)
                .forEach(this::creerEtAfficherComposantVehicule);
    }

    private boolean estTypeStatutSelectionne(Vehicule v) {
        String statutDynamique = getStatutDynamiqueVehicule(v);
        if (toutesRadio.isSelected()) return true;
        if (disponibleRadio.isSelected()) return statutDynamique.startsWith("Disponible");
        if (nonDisponibleRadio.isSelected()) return !statutDynamique.startsWith("Disponible");
        return false;
    }

    private boolean estMarqueEtModeleSelectionne(Vehicule v) {
        String marqueChoisie = marqueComboBox.getValue();
        String modeleChoisi = modeleComboBox.getValue();

        boolean marqueMatch = (marqueChoisie == null || "Toutes".equals(marqueChoisie) || marqueChoisie.equals(v.getMarque()));
        boolean modeleMatch = (modeleChoisi == null || "Tous".equals(modeleChoisi) || modeleChoisi.equals(v.getModele()));

        return marqueMatch && modeleMatch;
    }

    private void creerEtAfficherComposantVehicule(Vehicule v) {
        // Note: Use the existing logic for your UI component creation here
        // We call the image loader helper here
        Image vehiculeImage = loadVehiculeImage(v.getPhoto());

        // Example: card.setImageView(vehiculeImage);
        // voituresListVBox.getChildren().add(card);
    }

    private Image loadVehiculeImage(String photoPath) {
        if (photoPath == null || photoPath.isEmpty()) return null;

        try {
            // Extract filename from path
            String fileName = photoPath.substring(Math.max(photoPath.lastIndexOf('/'), photoPath.lastIndexOf('\\')) + 1);

            InputStream is = getClass().getResourceAsStream("/images/" + fileName);
            if (is != null) {
                return new Image(is);
            }
            // Fallback to absolute/URL path if not in resources
            return new Image(photoPath, true);
        } catch (Exception e) {
            return null;
        }
    }

    private String getStatutDynamiqueVehicule(Vehicule vehicule) {
        LocalDate today = LocalDate.now();

        List<Reservation> allReservations = controllerReservationList;

        List<Reservation> vehiculeReservations = allReservations.stream()
                .filter(r -> r.getVehicules() != null && r.getVehicules().contains(vehicule))
                .filter(r -> r.getStatut() == StatutReservation.APPROUVEE || r.getStatut() == StatutReservation.PAYEMENT_EN_ATTENTE) // Include PAYEMENT_EN_ATTENTE
                .toList();

        for (Reservation res : vehiculeReservations) {
            if (!today.isBefore(res.getDateDebut()) && !today.isAfter(res.getDateFin())) {
                return "En location (jusqu’au " + res.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")";
            }
        }

        Optional<Reservation> futureReservation = vehiculeReservations.stream()
                .filter(r -> today.isBefore(r.getDateDebut()))
                .min(Comparator.comparing(Reservation::getDateDebut));

        if (futureReservation.isPresent()) {
            LocalDate dateDebut = futureReservation.get().getDateDebut();
            return "Réservé pour le " + dateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        return "Disponible";
    }


    @Override
    public void update() {
        afficherVoituresSelonDisponibilite();
    }
}
