package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.CarteBancaire;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.observer.Observer;
import location.app.vehicule_location_app.observer.Subject;

import java.util.List;

import static location.app.vehicule_location_app.controllers.Controller.updateObject;

public class UIWalletController extends Observer {

    @FXML
    private VBox cartesListVBox;
    @FXML
    private Button buttonRefreshCard;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonDepot;
    @FXML
    private Button buttonRetrait;

    @FXML
    private Label soldeCompteLabel; // Ajoute ce champ dans ton FXML avec fx:id="soldeCompteLabel"

    private Client clientConnecte;

    public void setClientConnecte(Client client) {
        this.clientConnecte = client;
        afficherCartesClient();
    }

    private ToggleGroup activationGroup = new ToggleGroup();
    private List<CarteBancaire> cartes;

    public UIWalletController() {
        this.subject = Subject.getInstance();
        this.subject.attach(this);
    }

    @FXML
    private void initialize() {
        buttonRefreshCard.setOnAction(e -> afficherCartesClient());
        buttonAdd.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold;"); // bleu
        buttonDepot.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-weight: bold;"); // vert
        buttonRetrait.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;"); // rouge

        buttonAdd.setOnAction(e -> handleAdd());
        buttonRefreshCard.setOnAction(e -> handleRefresh());
        buttonDepot.setOnAction(e -> handleDepotSelection());
        buttonRetrait.setOnAction(e -> handleRetraitSelection());

        // Initialise le solde au démarrage
        updateSoldeCompteLabel(null);
    }

    private void afficherCartesClient() {
        cartesListVBox.getChildren().clear();
        if (clientConnecte == null) return;

        HibernateObjectDaoImpl<CarteBancaire> carteDao = new HibernateObjectDaoImpl<>(CarteBancaire.class);
        cartes = carteDao.findByField("client.id", clientConnecte.getId());

        activationGroup = new ToggleGroup();

        for (CarteBancaire carte : cartes) {
            VBox carteBox = new VBox(5);
            carteBox.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-background-color: #fafafa;");
            HBox hbox = new HBox(30);
            hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            VBox infos = new VBox(8);
            infos.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label numeroLbl = new Label("Numéro : " + carte.getNumeroCarte());
            numeroLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            Label cvcLbl = new Label("CVC : " + carte.getCvc());
            cvcLbl.setStyle("-fx-font-size: 14px;");
            Label expLbl = new Label("Expiration : " + carte.getDateExpiration());
            expLbl.setStyle("-fx-font-size: 14px;");
            infos.getChildren().addAll(numeroLbl, cvcLbl, expLbl);

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            // RadioButton pour activer une seule carte
            RadioButton activerRadio = new RadioButton("Activer");
            activerRadio.setToggleGroup(activationGroup);
            activerRadio.setUserData(carte);
            activerRadio.setSelected(carte.equals(getCarteActive()));

            activerRadio.setOnAction(e -> {
                setCarteActive(carte);
            });

            // Sélection par clic sur la carte (pour dépôt/retrait)
            carteBox.setOnMouseClicked(e -> {
                selectedCarte = carte;
                updateSoldeCompteLabel(carte);
                // Met la couleur de fond sélectionnée uniquement sur la carte sélectionnée
                for (javafx.scene.Node node : cartesListVBox.getChildren()) {
                    if (node instanceof VBox) {
                        if (node == carteBox) {
                            node.setStyle(carteBox.getStyle() + ";-fx-background-color:#e3f2fd;");
                        } else {
                            node.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-background-color: #fafafa;");
                        }
                    }
                }
            });

            hbox.getChildren().addAll(infos, spacer, activerRadio);
            carteBox.getChildren().add(hbox);

            cartesListVBox.getChildren().add(carteBox);
        }
    }

    private void updateSoldeCompteLabel(CarteBancaire carte) {
        if (soldeCompteLabel == null) return;
        if (carte != null) {
            soldeCompteLabel.setText("Solde de la carte sélectionnée : " + String.format("%.2f FCFA", carte.getSolde()));
        } else {
            soldeCompteLabel.setText("Sélectionnez une carte pour afficher le solde.");
        }
    }

    // Méthode pour obtenir la carte active (activée par le RadioButton)
    private CarteBancaire carteActive = null;
    private CarteBancaire getCarteActive() {
        return carteActive;
    }
    private void setCarteActive(CarteBancaire carte) {
        this.carteActive = carte;
    }

    private CarteBancaire selectedCarte = null;

    private CarteBancaire getCarteSelectionnee() {
        // Priorité à la carte sélectionnée par clic
        if (selectedCarte != null) {
            return selectedCarte;
        }
        // Sinon, retourne la carte active
        if (carteActive != null) {
            return carteActive;
        }
        showAlert("Veuillez sélectionner une carte à utiliser.");
        return null;
    }

    private void ouvrirPopupTransaction(CarteBancaire carte, boolean isDepot) {
        javafx.stage.Stage popupStage = new javafx.stage.Stage();
        popupStage.setTitle(isDepot ? "Dépôt sur la carte" : "Retrait sur la carte");

        VBox root = new VBox(35);
        root.setStyle("-fx-padding: 20;");
        root.setAlignment(javafx.geometry.Pos.CENTER);

        // Infos carte
        Label titulaireLbl = new Label("Titulaire : " + carte.getTitulaire());
        Label numeroLbl = new Label("Numéro : " + carte.getNumeroCarte());
        Label cvcLbl = new Label("CVC : " + carte.getCvc());
        Label expLbl = new Label("Expiration : " + carte.getDateExpiration());
        Label soldeLbl = new Label("Solde actuel : " + String.format("%.2f FCFA", carte.getSolde()));
        soldeLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #43a047;");

        VBox infosBox = new VBox(8, titulaireLbl, numeroLbl, cvcLbl, expLbl, soldeLbl);
        infosBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Champ montant
        Label montantLbl = new Label("Montant à " + (isDepot ? "créditer" : "déduire") + " :");
        TextField montantField = new TextField();
        montantField.setPromptText("Montant en FCFA");

        Label nouveauSoldeLbl = new Label();
        nouveauSoldeLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1976d2;");

        Button validerBtn = new Button("Valider");
        validerBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold;");
        Button annulerBtn = new Button("Annuler");
        annulerBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox btnBox = new HBox(15, validerBtn, annulerBtn);
        btnBox.setAlignment(javafx.geometry.Pos.CENTER);

        root.getChildren().addAll(infosBox, montantLbl, montantField, nouveauSoldeLbl, btnBox);

        montantField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double montant = Double.parseDouble(newVal);
                double nouveauSolde = isDepot ? carte.getSolde() + montant : carte.getSolde() - montant;
                nouveauSoldeLbl.setText("Nouveau solde : " + String.format("%.2f FCFA", nouveauSolde));
            } catch (NumberFormatException ex) {
                nouveauSoldeLbl.setText("");
            }
        });

        validerBtn.setOnAction(e -> {
            String montantStr = montantField.getText();
            try {
                double montant = Double.parseDouble(montantStr);
                if (montant <= 0) {
                    showAlert("Veuillez saisir un montant positif.");
                    return;
                }
                if (isDepot) {
                    carte.setSolde(carte.getSolde() + montant);
                } else {
                    if (carte.getSolde() < montant) {
                        showAlert("Solde insuffisant pour le retrait.");
                        return;
                    }
                    carte.setSolde(carte.getSolde() - montant);
                }
                try {
                    updateObject(carte, CarteBancaire.class);
                    Subject.getInstance().notifyAllObservers();
                } catch (DAOException ex) {
                    throw new RuntimeException(ex);
                }
                showAlert("Opération réussie !");
                popupStage.close();
                afficherCartesClient();
            } catch (NumberFormatException ex) {
                showAlert("Veuillez saisir un montant valide.");
            }
        });

        annulerBtn.setOnAction(e -> popupStage.close());

        popupStage.setScene(new javafx.scene.Scene(root));
        popupStage.show();
    }

    @FXML
    private void handleAdd() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/UIAddCard.fxml"));
            javafx.scene.Parent root = loader.load();

            UIAddCardController addCardController = loader.getController();
            addCardController.setClientConnecte(clientConnecte);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Ajout d'une nouvelle carte");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        afficherCartesClient();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleDepotSelection() {
        CarteBancaire carte = getCarteSelectionnee();
        if (carte != null) {
            ouvrirPopupTransaction(carte, true);
        }
    }

    private void handleRetraitSelection() {
        CarteBancaire carte = getCarteSelectionnee();
        if (carte != null) {
            ouvrirPopupTransaction(carte, false);
        }
    }

    @Override
    public void update() {
        afficherCartesClient();
        updateSoldeCompteLabel(carteActive);
    }
}
