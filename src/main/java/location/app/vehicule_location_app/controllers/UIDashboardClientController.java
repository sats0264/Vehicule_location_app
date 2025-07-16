package location.app.vehicule_location_app.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
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
import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Vehicule;

import java.io.IOException;
import java.io.InputStream;
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
    private ImageView imageView;

    @FXML
    private VBox voituresListVBox;

    private Client currentClient;

    // Exemple de données : Map<Marque, Liste de modèles>
    private final Map<String, List<String>> marqueModelesMap = new HashMap<>();

    @FXML
    public void initialize() {
        // --- ToggleGroup pour rendre les RadioButton exclusifs ---
        ToggleGroup disponibiliteToggleGroup = new ToggleGroup();
        toutesRadio.setToggleGroup(disponibiliteToggleGroup);
        disponibleRadio.setToggleGroup(disponibiliteToggleGroup);
        nonDisponibleRadio.setToggleGroup(disponibiliteToggleGroup);

        // Listener pour filtrer selon la disponibilité
        disponibiliteToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            afficherVoituresSelonDisponibilite();
        });

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

        chargerMarquesEtModelesDepuisBase();
        toutesRadio.setSelected(true);
        afficherVoituresSelonDisponibilite(); // Affichage initial
    }

    private void addVoitureCard(String marque, String modele, String immatriculation, String imageUrl, String statut, double prixSansChauffeur, double prixAvecChauffeur) {
        ImageView imageView = new ImageView();
        try {
            Image image;
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Essaye d'abord depuis les ressources (classpath)
                try {
                    image = new Image(getClass().getResource("/images/" + imageUrl).toExternalForm());
                } catch (Exception e) {
                    // Si échec, essaye comme URL/fichier absolu
                    image = new Image(imageUrl, true);
                }
            } else {
                // Image par défaut
                image = new Image(getClass().getResource("/images/car_logo.png").toExternalForm());
            }
            imageView.setImage(image);
        } catch (Exception ex) {
            // Si tout échoue, image vide
            imageView.setImage(new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="));
        }

        imageView.setFitHeight(180);
        imageView.setFitWidth(270);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, #888, 8, 0.2, 0, 2);");

        // Reste du code inchangé...
        VBox infosBox = new VBox(8,
                new Label(marque + " " + modele),
                new Label("Marque: " + marque),
                new Label("Modèle: " + modele),
                new Label("Immatriculation: " + immatriculation),
                new Label("Statut: " + statut)
        );
        infosBox.setAlignment(Pos.CENTER);
        infosBox.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        infosBox.getChildren().get(1).setStyle("-fx-font-size: 14px;");
        infosBox.getChildren().get(2).setStyle("-fx-font-size: 14px;");
        infosBox.getChildren().get(3).setStyle("-fx-font-size: 14px;");
        infosBox.getChildren().get(4).setStyle("-fx-text-fill: " + getStatutColor(statut) + "; -fx-font-size: 14px;");

        // Prix et boutons à droite
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

        switch (statut.toUpperCase()) {
            case "DISPONIBLE":
                return "green";
            case "INDISPONIBLE":
            case "LOUE":
            case "EN_MAINTENANCE":
                return "red";
            case "RESERVE":
                return "orange";
            default:
                return "gray";
        }
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
            if (client == null) {
                // Affiche une alerte et ne pas ouvrir la fenêtre si aucun client connecté
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Aucun client connecté");
                alert.setContentText("Veuillez vous connecter pour effectuer une réservation.");
                alert.showAndWait();
                return;
            }
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
     * Récupère le client connecté depuis la base de données via Hibernate.
     * À adapter selon ta logique d'authentification (exemple ici avec un email stocké dans une propriété système).
     */
    private Client getCurrentClient() {
        if (currentClient != null) {
            return currentClient;
        }
        // Exemple : récupération de l'email du client connecté (à adapter selon ton appli)
        String emailConnecte = System.getProperty("client.email");
        if (emailConnecte != null && !emailConnecte.isEmpty()) {
            try (org.hibernate.Session session = location.app.vehicule_location_app.jdbc.HibernateConnection.getSessionFactory().openSession()) {
                // Requête HQL pour récupérer le client par email
                Client client = session.createQuery(
                        "from T_Client where lower(email) = :email", Client.class)
                        .setParameter("email", emailConnecte.toLowerCase())
                        .uniqueResult();
                if (client != null) {
                    currentClient = client;
                    return client;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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
        marquesList.add(0, "Toutes"); // Ajoute "Toutes" en première position

        marqueComboBox.setItems(FXCollections.observableArrayList(marquesList));
        marqueComboBox.getSelectionModel().selectFirst(); // Sélectionne "Toutes" par défaut

        marqueComboBox.setOnAction(event -> {
            String selectedMarque = marqueComboBox.getValue();
            if (selectedMarque != null && !selectedMarque.equals("Toutes") && marqueModeleMap.containsKey(selectedMarque)) {
                modeleComboBox.setItems(FXCollections.observableArrayList(marqueModeleMap.get(selectedMarque)));
                modeleComboBox.setDisable(false);
            } else {
                modeleComboBox.setItems(FXCollections.observableArrayList());
                modeleComboBox.setDisable(true);
            }
            modeleComboBox.getSelectionModel().clearSelection();
            modeleComboBox.setPromptText("Tous");

            afficherVoituresSelonDisponibilite(); // re-filtre après choix
        });

        modeleComboBox.setDisable(true);
    }

    // Nouvelle méthode pour afficher selon la disponibilité
    private void afficherVoituresSelonDisponibilite() {
        voituresListVBox.getChildren().clear();
        HibernateObjectDaoImpl<Vehicule> vehiculeDao = new HibernateObjectDaoImpl<>(Vehicule.class);
        List<Vehicule> vehicules = vehiculeDao.readAll();

        String marqueChoisie = marqueComboBox.getValue();
        String modeleChoisi = modeleComboBox.getValue();

        for (Vehicule v : vehicules) {
            // Filtrage par disponibilité AVANT l'affichage
            boolean afficher = false;
            if (toutesRadio.isSelected()) {
                afficher = true;
            } else if (disponibleRadio.isSelected() && v.getStatut() != null && v.getStatut().name().equalsIgnoreCase("DISPONIBLE")) {
                afficher = true;
            } else if (nonDisponibleRadio.isSelected() && v.getStatut() != null && v.getStatut().name().equalsIgnoreCase("INDISPONIBLE")) {
                afficher = true;
            }

            // Filtrage par marque/modèle si sélectionnés
            if (afficher) {
                if (marqueChoisie != null && !marqueChoisie.equals("Toutes") && !marqueChoisie.equals(v.getMarque())) continue;
                if (modeleChoisi != null && !modeleChoisi.equals("Tous") && !modeleChoisi.equals(v.getModele())) continue;

                // Récupération des prix
                double prixSansChauffeur = v.getTarif();
                double prixAvecChauffeur = v.getTarif() * 1.2;

                String statut = (v.getStatut() != null) ? v.getStatut().name() : "INCONNU";
                String photoName = null;
                // Chargement de l'image depuis ressources/images si possible
                if (v.getPhoto() != null && !v.getPhoto().isEmpty()) {
                    photoName = v.getPhoto();

                    // Extraire le nom de fichier si c'est un chemin complet (optionnel)
                    if (photoName.contains("/")) {
                        photoName = photoName.substring(photoName.lastIndexOf('/') + 1);
                    } else if (photoName.contains("\\")) {
                        photoName = photoName.substring(photoName.lastIndexOf('\\') + 1);
                    }

                    try {
                        // Essayer de charger depuis classpath /images/
                        InputStream is = getClass().getResourceAsStream("/images/" + photoName);

                        if (is != null) {
                            Image image = new Image(is);
                            imageView.setImage(image);
                        } else {
                            // Si non trouvé dans ressources, essayer d'utiliser directement l'URL dans vehicule.getPhoto()
                            Image image = new Image(v.getPhoto(), true);
                            imageView.setImage(image);
                        }
                    } catch (Exception e) {
                        imageView.setImage(null);
                    }
                }

                // Affichage UNE SEULE FOIS avec tous les paramètres
                addVoitureCard(v.getMarque(), v.getModele(), v.getImmatriculation(),
                        photoName, statut, prixSansChauffeur, prixAvecChauffeur);
            }
        }
    }
}
