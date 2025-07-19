package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Chauffeur;
import location.app.vehicule_location_app.models.Statut;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static location.app.vehicule_location_app.controllers.Controller.chauffeurDao;

public class UIChauffeurSelectionController {

    @FXML
    private VBox chauffeursDisplayContainer; // Container to add HBoxes of chauffeur cards
    @FXML
    private Label noChauffeurMessage; // Message for no available chauffeurs
    @FXML
    private Button selectChauffeurBtn;
    @FXML
    private Button cancelBtn;

    private Chauffeur selectedChauffeur;
    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;

    private static final String DEFAULT_CHAUFFEUR_IMAGE_PATH = "/images/man.png";

    @FXML
    public void initialize() {
        // Handle button actions
        selectChauffeurBtn.setOnAction(event -> handleSelectChauffeur());
        cancelBtn.setOnAction(event -> handleCancel());

        // Initially disable the select button
        selectChauffeurBtn.setDisable(true);
        noChauffeurMessage.setVisible(false);
        noChauffeurMessage.setManaged(false);
    }

    /**
     * Initializes data for the controller, including reservation dates for filtering.
     * This method should be called by the parent controller (e.g., UIClientReservationController)
     * before showing this view.
     * @param startDate The start date of the reservation.
     * @param endDate The end date of the reservation.
     */
    public void initData(LocalDate startDate, LocalDate endDate) {
        this.reservationStartDate = startDate;
        this.reservationEndDate = endDate;
        loadAvailableChauffeurs();
    }

    /**
     * Loads available chauffeurs from the database and filters them based on availability
     * and displays them in the UI.
     */
    private void loadAvailableChauffeurs() {
        chauffeursDisplayContainer.getChildren().clear(); // Clear existing chauffeur cards
        try {
            List<Chauffeur> allChauffeurs = chauffeurDao.list();

            // Filter chauffeurs: only 'DISPONIBLE' status and no overlapping reservations
            List<Chauffeur> availableChauffeurs = allChauffeurs.stream()
                    .filter(c -> c.getStatut() == Statut.DISPONIBLE)
                    // TODO: Add more robust availability check here:
                    // Check if chauffeur's existing reservations overlap with new reservation dates
                    // This would require fetching reservations for each chauffeur and comparing dates.
                    // For now, we assume if Statut is DISPONIBLE, they are available.
                    .toList();

            if (availableChauffeurs.isEmpty()) {
                noChauffeurMessage.setVisible(true);
                noChauffeurMessage.setManaged(true);
                selectChauffeurBtn.setDisable(true);
                return;
            }

            noChauffeurMessage.setVisible(false);
            noChauffeurMessage.setManaged(false);

            HBox currentRow = null;
            for (int i = 0; i < availableChauffeurs.size(); i++) {
                Chauffeur chauffeur = availableChauffeurs.get(i);

                // Start a new HBox for every 3 chauffeurs
                if (i % 3 == 0) {
                    currentRow = new HBox(20); // Spacing between chauffeur cards
                    currentRow.setAlignment(Pos.CENTER);
                    chauffeursDisplayContainer.getChildren().add(currentRow);
                }

                VBox chauffeurCard = createChauffeurCard(chauffeur);
                currentRow.getChildren().add(chauffeurCard);

                // Add click listener to select the chauffeur
                chauffeurCard.setOnMouseClicked(event -> {
                    if (selectedChauffeur == chauffeur) {
                        // Deselect if already selected
                        selectedChauffeur = null;
                        chauffeurCard.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;");
                        selectChauffeurBtn.setDisable(true);
                    } else {
                        // Deselect previous if any
                        if (selectedChauffeur != null) {
                            // Find the VBox of the previously selected chauffeur and reset its style
                            for (Node node : chauffeursDisplayContainer.getChildren()) {
                                if (node instanceof HBox) {
                                    for (Node card : ((HBox) node).getChildren()) {
                                        if (card instanceof VBox && card.getUserData() == selectedChauffeur) {
                                            card.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // Select new chauffeur
                        selectedChauffeur = chauffeur;
                        chauffeurCard.setStyle("-fx-border-color: #007bff; -fx-border-width: 3px; -fx-border-radius: 5px; -fx-background-color: #e6f2ff;"); // Highlight selected
                        selectChauffeurBtn.setDisable(false);
                    }
                });
                chauffeurCard.setUserData(chauffeur); // Store chauffeur object in the card for easy retrieval
            }

        } catch (DAOException e) {
            System.err.println("Erreur lors du chargement des chauffeurs: " + e.getMessage());
            e.printStackTrace();
            // Show an alert to the user
            showAlert();
        }
    }

    /**
     * Creates a VBox card for a single chauffeur.
     * @param chauffeur The Chauffeur object.
     * @return A VBox representing the chauffeur's card.
     */
    private VBox createChauffeurCard(Chauffeur chauffeur) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(180, 220); // Fixed size for each card
        card.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 10px;");

        ImageView photoView = new ImageView();
        photoView.setFitHeight(100);
        photoView.setFitWidth(100);
        photoView.setPreserveRatio(true);
        loadChauffeurImage(photoView, chauffeur.getPhoto());

        Label nameLabel = new Label(chauffeur.getPrenom() + " " + chauffeur.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nameLabel.setWrapText(true); // Allow text to wrap

        Label phoneLabel = new Label(chauffeur.getTelephone());
        phoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        card.getChildren().addAll(photoView, nameLabel, phoneLabel);
        return card;
    }

    /**
     * Loads the chauffeur's image or a default placeholder.
     * @param imageView The ImageView to set the image on.
     * @param imageUrl The URL/path to the chauffeur's photo.
     */
    private void loadChauffeurImage(ImageView imageView, String imageUrl) {
        Image image = null;
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Try to load from classpath (e.g., /images/chauffeur1.png)
                InputStream is = getClass().getResourceAsStream(imageUrl);
                if (is != null) {
                    image = new Image(is);
                } else {
                    // Fallback to direct URL if not found in classpath (e.g., external URL)
                    image = new Image(imageUrl, true);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image du chauffeur: " + e.getMessage());
        }

        if (image != null && !image.isError()) {
            imageView.setImage(image);
        } else {
            // Load default placeholder image if actual image is null or failed to load
            InputStream defaultIs = getClass().getResourceAsStream(DEFAULT_CHAUFFEUR_IMAGE_PATH);
            if (defaultIs != null) {
                imageView.setImage(new Image(defaultIs));
            } else {
                System.err.println("Default chauffeur image not found: " + DEFAULT_CHAUFFEUR_IMAGE_PATH);
            }
        }
    }

    /**
     * Handles the selection of a chauffeur.
     */
    private void handleSelectChauffeur() {
        // selectedChauffeur is already set by the click listener on the card
        closeWindow();
    }

    /**
     * Handles the cancellation of chauffeur selection.
     */
    private void handleCancel() {
        selectedChauffeur = null; // No chauffeur selected
        closeWindow();
    }

    /**
     * Returns the selected chauffeur.
     * @return The selected Chauffeur object, or null if none was selected.
     */
    public Chauffeur getSelectedChauffeur() {
        return selectedChauffeur;
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) selectChauffeurBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * Displays an alert message.
     */
    private void showAlert() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Erreur de chargement");
        alert.setHeaderText(null);
        alert.setContentText("Impossible de charger la liste des chauffeurs.");
        alert.showAndWait();
    }
}
