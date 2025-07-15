package location.app.vehicule_location_app.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Vehicule;

import java.util.*;

public class UIDashboardClientController {

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
    private VBox voituresListVBox;

    // Exemple de données : Map<Marque, Liste de modèles>
    private final Map<String, List<String>> marqueModelesMap = new HashMap<>();

    @FXML
    public void initialize() {
        // --- ToggleGroup pour rendre les RadioButton exclusifs ---
        ToggleGroup disponibiliteToggleGroup = new ToggleGroup();
        toutesRadio.setToggleGroup(disponibiliteToggleGroup);
        disponibleRadio.setToggleGroup(disponibiliteToggleGroup);
        nonDisponibleRadio.setToggleGroup(disponibiliteToggleGroup);

        // Exemple de données, à remplacer par vos données réelles
        marqueModelesMap.put("Renault", Arrays.asList("Clio", "Megane", "Captur"));
        marqueModelesMap.put("Peugeot", Arrays.asList("208", "308", "3008"));
        marqueModelesMap.put("Toyota", Arrays.asList("Yaris", "Corolla", "RAV4"));

        // Remplir le ComboBox des marques
        ObservableList<String> marques = FXCollections.observableArrayList(marqueModelesMap.keySet());
        marqueComboBox.setItems(marques);

        // Optionnel : sélectionner "Toutes" par défaut
        marqueComboBox.setPromptText("Toutes");

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

        // Désactiver le ComboBox des modèles tant qu'aucune marque n'est sélectionnée
        modeleComboBox.setDisable(true);

        // Exemple d'affichage de voitures (à remplacer par vos données réelles)
        voituresListVBox.getChildren().clear();
        addVoitureCard("Renault", "Clio", "AB-123-CD", "/images/car_logo.png");
        addVoitureCard("Peugeot", "208", "CD-456-EF", "/images/car_logo.png");
    }

    private void addVoitureCard(String marque, String modele, String immatriculation, String imageUrl) {
        // Image
        ImageView imageView;
        try {
            imageView = new ImageView(new Image(getClass().getResource(imageUrl).toExternalForm()));
        } catch (Exception e) {
            imageView = new ImageView(); // image par défaut si non trouvée
        }
        imageView.setFitHeight(180);
        imageView.setFitWidth(270);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, #888, 8, 0.2, 0, 2);");

        // Infos voiture
        VBox infosBox = new VBox(8,
                new Label(marque + " " + modele),
                new Label("Marque: " + marque),
                new Label("Modèle: " + modele),
                new Label("Immatriculation: " + immatriculation),
                new Label("Statut: Disponible")
        );
        infosBox.setAlignment(Pos.CENTER);
        infosBox.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        infosBox.getChildren().get(1).setStyle("-fx-font-size: 14px;");
        infosBox.getChildren().get(2).setStyle("-fx-font-size: 14px;");
        infosBox.getChildren().get(3).setStyle("-fx-font-size: 14px;");
        infosBox.getChildren().get(4).setStyle("-fx-text-fill: green; -fx-font-size: 14px;");

        // Prix et boutons à droite
        VBox prixBox = new VBox(10);
        prixBox.setAlignment(Pos.CENTER_RIGHT);

        HBox prixSansChauffeur = new HBox(5,
                new Label("Prix sans chauffeur:"),
                new Label("18 000 FCFA / jour")
        );
        prixSansChauffeur.setAlignment(Pos.CENTER_RIGHT);
        ((Label)prixSansChauffeur.getChildren().get(0)).setStyle("-fx-font-size: 13px;");
        ((Label)prixSansChauffeur.getChildren().get(1)).setStyle("-fx-font-weight: bold;");

        Button reserverBtn = new Button("Réserver");
        reserverBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        reserverBtn.setOnAction(e -> showReservationPopup(marque, modele, immatriculation, imageUrl, false));

        HBox prixAvecChauffeur = new HBox(5,
                new Label("Prix avec chauffeur:"),
                new Label("25 000 FCFA / jour")
        );
        prixAvecChauffeur.setAlignment(Pos.CENTER_RIGHT);
        ((Label)prixAvecChauffeur.getChildren().get(0)).setStyle("-fx-font-size: 13px;");
        ((Label)prixAvecChauffeur.getChildren().get(1)).setStyle("-fx-font-weight: bold;");

        Button reserverChauffeurBtn = new Button("Réserver Avec Chauffeur");
        reserverChauffeurBtn.setOnAction(e -> showReservationPopup(marque, modele, immatriculation, imageUrl, true));

        prixBox.getChildren().addAll(prixSansChauffeur, reserverBtn, prixAvecChauffeur, reserverChauffeurBtn);

        HBox card = new HBox(30, imageView, infosBox, prixBox);
        card.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-background-color: #fafafa;");
        card.setAlignment(Pos.CENTER_LEFT);

        voituresListVBox.getChildren().add(card);
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

            //Récupère le client connecté
            Client client = getCurrentClient();
            controller.setClient(client);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nouvelle réservation");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recherche un véhicule par ses infos (à adapter selon ta logique réelle).
     */
    private Vehicule findVehiculeByInfos(String marque, String modele, String immatriculation, String imageUrl) {
        try {
            // Utilise le DAO pour récupérer le véhicule existant
            location.app.vehicule_location_app.dao.HibernateObjectDaoImpl<Vehicule> vehiculeDao =
                new location.app.vehicule_location_app.dao.HibernateObjectDaoImpl<>(Vehicule.class);

            // Ici, on suppose que l'immatriculation est unique
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

    /**
     * Récupère le client connecté (à adapter selon ta logique d'authentification).
     */
    private Client getCurrentClient() {
        // À remplacer par la vraie récupération du client connecté
        Client client = new Client();
        client.setNom("ClientTest");
        return client;
    }
}
