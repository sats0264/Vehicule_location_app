package location.app.vehicule_location_app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.*;
import location.app.vehicule_location_app.observer.Subject; // Assurez-vous que Subject.getInstance() est disponible

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional; // Import pour Optional

public class UIInspectReservationController extends Controller {

    private Reservation reservation;
    private List<Chauffeur> chauffeursSelectionnes; // Pas utilisé directement dans le FXML fourni, mais gardé

    // --- Champs Client ---
    @FXML
    private Label clientLastNameField;
    @FXML
    private Label clientFirstNameField;
    @FXML
    private Label clientPhoneField;
    @FXML
    private Label clientEmailField;
    @FXML
    private Label clientFideliteField; // Renommé pour correspondre au FXML

    // --- Champs Véhicule ---
    @FXML
    private ComboBox<String> voitureImmatriculationField;
    @FXML
    private Label voitureModeleField;
    @FXML
    private Label voitureMarqueField;
    @FXML
    private Label voitureTarifField;
    @FXML
    private Label voitureStatutField;

    // --- Champs Chauffeur ---
    @FXML
    private ComboBox<Integer> chauffeurIdComboBox;
    @FXML
    private Label chauffeurLastNameField;
    @FXML
    private Label chauffeurFirstNameField;
    @FXML
    private Label chauffeurStatutField;

    // --- Champs Durée et Montant ---
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label durationField;
    @FXML
    private Label amountField;

    // --- Boutons d'action principaux ---
    @FXML
    private Button approuverButton; // Pour EN_ATTENTE -> PAYEMENT_EN_ATTENTE
    @FXML
    private Button rejeterButton;   // Pour EN_ATTENTE -> REJETEE
    @FXML
    private Button fermerButton;

    // --- Nouveaux boutons pour les demandes ---
    @FXML
    private Button approuverModificationBtn;
    @FXML
    private Button rejeterModificationBtn;
    @FXML
    private Button approuverAnnulationBtn;
    @FXML
    private Button rejeterAnnulationBtn;

    public UIInspectReservationController() throws DAOException {
        // Le constructeur par défaut de Controller est appelé
    }

    @FXML
    public void initialize() {
        // Initialisation des listeners pour les boutons
        approuverButton.setOnAction(e -> handleApprouver());
        rejeterButton.setOnAction(e -> handleRejeter());
        fermerButton.setOnAction(this::handleFermer);

        approuverModificationBtn.setOnAction(e -> handleApprouverModification());
        rejeterModificationBtn.setOnAction(e -> handleRejeterModification());
        approuverAnnulationBtn.setOnAction(e -> handleApprouverAnnulation());
        rejeterAnnulationBtn.setOnAction(e -> handleRejeterAnnulation());

        // Les DatePickers sont désactivés pour l'inspection
        startDatePicker.setDisable(true);
        endDatePicker.setDisable(true);

        // Assurer que tous les boutons de demande sont cachés par défaut
        hideAllRequestButtons();
    }

    /**
     * Cache tous les boutons liés aux demandes de modification/annulation.
     */
    private void hideAllRequestButtons() {
        approuverModificationBtn.setVisible(false);
        approuverModificationBtn.setManaged(false);
        rejeterModificationBtn.setVisible(false);
        rejeterModificationBtn.setManaged(false);
        approuverAnnulationBtn.setVisible(false);
        approuverAnnulationBtn.setManaged(false);
        rejeterAnnulationBtn.setVisible(false);
        rejeterAnnulationBtn.setManaged(false);
        approuverButton.setVisible(false); // Cacher les boutons initiaux aussi
        approuverButton.setManaged(false);
        rejeterButton.setVisible(false);
        rejeterButton.setManaged(false);
    }

    /**
     * Définit la réservation à inspecter et met à jour l'interface.
     * Active/désactive les boutons d'action en fonction du statut.
     * @param reservation L'objet Reservation à afficher.
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        if (reservation == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune réservation à afficher.");
            return;
        }

        // --- Client ---
        Client c = reservation.getClient();
        if (c != null) {
            clientFirstNameField.setText(c.getPrenom());
            clientLastNameField.setText(c.getNom());
            clientPhoneField.setText(c.getTelephone());
            clientEmailField.setText(c.getEmail());
            clientFideliteField.setText(String.valueOf(c.getPointFidelite())); // Utilise le nouveau fx:id
        }

        // --- Véhicules ---
        if (reservation.getVehicules() != null && !reservation.getVehicules().isEmpty()) {
            // Remplir la ComboBox avec les immatriculations
            voitureImmatriculationField.getItems().setAll(
                    reservation.getVehicules().stream().map(Vehicule::getImmatriculation).toList()
            );
            // Sélectionner le premier véhicule par défaut
            Vehicule firstVehicule = reservation.getVehicules().getFirst();
            voitureImmatriculationField.setValue(firstVehicule.getImmatriculation());
            updateVehiculeDetails(firstVehicule);

            // Listener pour la ComboBox des véhicules
            voitureImmatriculationField.setOnAction(event -> {
                String selectedImmatriculation = voitureImmatriculationField.getValue();
                Vehicule selectedVehicule = reservation.getVehicules()
                        .stream()
                        .filter(ve -> ve.getImmatriculation().equals(selectedImmatriculation))
                        .findFirst()
                        .orElse(null);
                if (selectedVehicule != null) {
                    updateVehiculeDetails(selectedVehicule);
                }
            });
        }

        // --- Chauffeur ---
        if (reservation.getChauffeurs() != null && !reservation.getChauffeurs().isEmpty()) {
            // Remplir la ComboBox avec les IDs des chauffeurs
            chauffeurIdComboBox.getItems().setAll(
                    reservation.getChauffeurs().stream().map(Chauffeur::getId).toList()
            );
            // Sélectionner le premier chauffeur par défaut
            Chauffeur firstChauffeur = reservation.getChauffeurs().getFirst();
            chauffeurIdComboBox.setValue(firstChauffeur.getId());
            updateChauffeurDetails(firstChauffeur);

            // Listener pour la ComboBox des chauffeurs
            chauffeurIdComboBox.setOnAction(event -> {
                Integer selectedId = chauffeurIdComboBox.getValue();
                Chauffeur selectedChauffeur = reservation.getChauffeurs()
                        .stream()
                        .filter(cha -> cha.getId() == selectedId)
                        .findFirst()
                        .orElse(null);
                if (selectedChauffeur != null) {
                    updateChauffeurDetails(selectedChauffeur);
                }
            });
        } else {
            // Cacher les champs chauffeur si aucun chauffeur
            chauffeurIdComboBox.setVisible(false);
            chauffeurIdComboBox.setManaged(false);
            chauffeurLastNameField.setText("N/A");
            chauffeurFirstNameField.setText("N/A");
            chauffeurStatutField.setText("N/A");
        }

        // --- Dates ---
        startDatePicker.setValue(reservation.getDateDebut());
        endDatePicker.setValue(reservation.getDateFin());

        // --- Durée ---
        long duration = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
        durationField.setText(duration + " jour(s)");

        // --- Montant ---
        calculateAndDisplayAmount(duration);

        // --- Visibilité des boutons selon le statut ---
        configureButtonsBasedOnStatus();
    }

    /**
     * Met à jour les labels d'information du véhicule.
     * @param vehicule Le véhicule sélectionné.
     */
    private void updateVehiculeDetails(Vehicule vehicule) {
        voitureModeleField.setText(vehicule.getModele());
        voitureMarqueField.setText(vehicule.getMarque());
        voitureTarifField.setText(String.valueOf(vehicule.getTarif()) + " FCFA");
        voitureStatutField.setText(vehicule.getStatut().toString());
    }

    /**
     * Met à jour les labels d'information du chauffeur.
     * @param chauffeur Le chauffeur sélectionné.
     */
    private void updateChauffeurDetails(Chauffeur chauffeur) {
        chauffeurLastNameField.setText(chauffeur.getNom());
        chauffeurFirstNameField.setText(chauffeur.getPrenom());
        chauffeurStatutField.setText(chauffeur.getStatut().toString());
    }

    /**
     * Calcule et affiche le montant total de la réservation.
     * @param duration Le nombre de jours de la réservation.
     */
    private void calculateAndDisplayAmount(long duration) {
        double vehiculeTotalTarif = reservation.getVehicules().stream()
                .mapToDouble(Vehicule::getTarif)
                .sum();
        double montantTotal = vehiculeTotalTarif * duration;

        if (reservation.getChauffeurs() != null && !reservation.getChauffeurs().isEmpty()) {
            // Supposons un tarif fixe par jour par chauffeur
            double chauffeurDailyFee = 7000.0; // À définir comme constante si ce n'est pas déjà fait
            montantTotal += reservation.getChauffeurs().size() * chauffeurDailyFee * duration;
        }
        amountField.setText(String.format("%.0f FCFA", montantTotal));
    }

    /**
     * Configure la visibilité des boutons en fonction du statut de la réservation.
     */
    private void configureButtonsBasedOnStatus() {
        hideAllRequestButtons(); // Cache tout d'abord

        StatutReservation statut = reservation.getStatut();
        switch (statut) {
            case EN_ATTENTE:
                approuverButton.setVisible(true);
                approuverButton.setManaged(true);
                rejeterButton.setVisible(true);
                rejeterButton.setManaged(true);
                break;
            case MODIFICATION_EN_ATTENTE:
                approuverModificationBtn.setVisible(true);
                approuverModificationBtn.setManaged(true);
                rejeterModificationBtn.setVisible(true);
                rejeterModificationBtn.setManaged(true);
                break;
            case ANNULATION_EN_ATTENTE:
                approuverAnnulationBtn.setVisible(true);
                approuverAnnulationBtn.setManaged(true);
                rejeterAnnulationBtn.setVisible(true);
                rejeterAnnulationBtn.setManaged(true);
                break;
            default:
                // Pour tous les autres statuts (CONFIRMEE, PAYEE, ANNULEE, REFUSEE, TERMINEE),
                // aucun bouton d'action n'est visible à part "Fermer".
                break;
        }
    }

    // --- Gestion des actions des boutons ---

    @FXML
    private void handleApprouver() {
        // Logique pour approuver la réservation initiale (EN_ATTENTE -> PAYEMENT_EN_ATTENTE)
        Optional<ButtonType> result = showAlertConfirmation("Approuver la réservation", "Voulez-vous vraiment approuver cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.PAYEMENT_EN_ATTENTE);
                // Mettre à jour le statut des véhicules et chauffeurs à INDISPONIBLE
                updateAssociatedEntitiesStatus(Statut.INDISPONIBLE);
                updateObject(reservation, Reservation.class);

                // Notifications
                sendReservationStatusNotification(
                        "Réservation Approuvée",
                        "La réservation N°" + reservation.getId() + " a été approuvée.",
                        NotificationType.RESERVATION_CONFIRMATION
                );
                sendReservationStatusNotificationForClient(
                        "Réservation Approuvée",
                        "Votre réservation du " + reservation.getDateDebut() + " au " + reservation.getDateFin() + " a été approuvée.\nVeuillez procéder au paiement pour finaliser la réservation.",
                        NotificationType.RESERVATION_CONFIRMATION,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation approuvée et en attente de paiement.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'approbation de la réservation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRejeter() {
        // Logique pour rejeter la réservation initiale (EN_ATTENTE -> REJETEE)
        Optional<ButtonType> result = showAlertConfirmation("Rejeter la réservation", "Voulez-vous vraiment rejeter cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.REJETEE);
                // Remettre le statut des véhicules et chauffeurs à DISPONIBLE
                updateAssociatedEntitiesStatus(Statut.DISPONIBLE);
                updateObject(reservation, Reservation.class);

                // Notifications
                sendReservationStatusNotification(
                        "Réservation Rejetée",
                        "La réservation N°" + reservation.getId() + " a été rejetée.",
                        NotificationType.RESERVATION_REFUSED
                );
                sendReservationStatusNotificationForClient(
                        "Réservation Rejetée",
                        "Votre réservation du " + reservation.getDateDebut() + " au " + reservation.getDateFin() + " a été rejetée.",
                        NotificationType.RESERVATION_REFUSED,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation rejetée.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du rejet de la réservation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleApprouverModification() {
        // Logique pour approuver une demande de modification (MODIFICATION_EN_ATTENTE -> CONFIRMEE ou EN_ATTENTE)
        Optional<ButtonType> result = showAlertConfirmation("Approuver la modification", "Voulez-vous vraiment approuver la modification de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Ici, vous devriez avoir accès aux nouvelles dates de modification
                // Si la modification a déjà été appliquée à l'objet reservation lors de la demande
                // par le client, il suffit de changer le statut.
                // Sinon, vous devrez récupérer les nouvelles dates de la notification ou d'un autre champ.

                reservation.setStatut(StatutReservation.EN_ATTENTE); // Ou au statut précédent si c'était EN_ATTENTE
                updateObject(reservation, Reservation.class);

                // Notifications
                sendReservationStatusNotification(
                        "Modification Réservation Approuvée",
                        "La demande de modification pour la réservation N°" + reservation.getId() + " a été approuvée.",
                        NotificationType.RESERVATION_MODIFICATION_SUCCESS // Nouveau type
                );
                sendReservationStatusNotificationForClient(
                        "Modification Approuvée",
                        "Votre demande de modification pour la réservation N°" + reservation.getId() + " a été approuvée.",
                        NotificationType.CLIENT_RESERVATION_MODIFICATION_SUCCESS,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Modification de réservation approuvée.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'approbation de la modification: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRejeterModification() {
        // Logique pour rejeter une demande de modification (MODIFICATION_EN_ATTENTE -> statut précédent)
        Optional<ButtonType> result = showAlertConfirmation("Rejeter la modification", "Voulez-vous vraiment rejeter la modification de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Remettre le statut de la réservation à son état précédent (ex: EN_ATTENTE ou CONFIRMEE)
                // Cela dépend de votre logique métier. Ici, je la remets à EN_ATTENTE par défaut.
                reservation.setStatut(StatutReservation.EN_ATTENTE);
                updateObject(reservation, Reservation.class);

                // Notifications
                sendReservationStatusNotification(
                        "Modification Réservation Rejetée",
                        "La demande de modification pour la réservation N°" + reservation.getId() + " a été rejetée.",
                        NotificationType.RESERVATION_MODIFICATION_REFUSED // Nouveau type
                );
                sendReservationStatusNotificationForClient(
                        "Modification Rejetée",
                        "Votre demande de modification pour la réservation N°" + reservation.getId() + " a été rejetée.",
                        NotificationType.CLIENT_RESERVATION_MODIFICATION_REFUSED,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Modification de réservation rejetée.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du rejet de la modification: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleApprouverAnnulation() {
        // Logique pour approuver une demande d'annulation (ANNULATION_EN_ATTENTE -> ANNULEE)
        Optional<ButtonType> result = showAlertConfirmation("Approuver l'annulation", "Voulez-vous vraiment approuver l'annulation de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatut(StatutReservation.ANNULEE);
                // Remettre le statut des véhicules et chauffeurs à DISPONIBLE
                updateAssociatedEntitiesStatus(Statut.DISPONIBLE);
                updateObject(reservation, Reservation.class);

                // Notifications
                sendReservationStatusNotification(
                        "Annulation Réservation Approuvée",
                        "La demande d'annulation pour la réservation N°" + reservation.getId() + " a été approuvée.",
                        NotificationType.RESERVATION_CANCELLATION_SUCCESS
                );
                sendReservationStatusNotificationForClient(
                        "Annulation Approuvée",
                        "Votre demande d'annulation pour la réservation N°" + reservation.getId() + " a été approuvée.",
                        NotificationType.RESERVATION_CANCELLATION_SUCCESS,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande d'annulation approuvée. Réservation annulée.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'approbation de l'annulation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRejeterAnnulation() {
        // Logique pour rejeter une demande d'annulation (ANNULATION_EN_ATTENTE -> statut précédent)
        Optional<ButtonType> result = showAlertConfirmation("Rejeter l'annulation", "Voulez-vous vraiment rejeter l'annulation de cette réservation ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Remettre le statut de la réservation à son état précédent (ex: PAYEMENT_EN_ATTENTE ou CONFIRMEE)
                // Cela dépend de votre logique métier. Ici, je la remets à PAYEMENT_EN_ATTENTE par défaut.
                reservation.setStatut(StatutReservation.PAYEMENT_EN_ATTENTE);
                updateObject(reservation, Reservation.class);

                // Notifications
                sendReservationStatusNotification(
                        "Annulation Réservation Rejetée",
                        "La demande d'annulation pour la réservation N°" + reservation.getId() + " a été rejetée.",
                        NotificationType.RESERVATION_CANCELLATION_REFUSED // Nouveau type
                );
                sendReservationStatusNotificationForClient(
                        "Annulation Rejetée",
                        "Votre demande d'annulation pour la réservation N°" + reservation.getId() + " a été rejetée.",
                        NotificationType.RESERVATION_CANCELLATION_REFUSED,
                        reservation.getClient()
                );

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande d'annulation rejetée.");
                closeStage();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du rejet de l'annulation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleFermer(ActionEvent actionEvent) {
        closeStage();
    }

    /**
     * Met à jour le statut des véhicules et chauffeurs associés à la réservation.
     * @param newStatus Le nouveau statut à appliquer (DISPONIBLE ou INDISPONIBLE).
     */
    private void updateAssociatedEntitiesStatus(Statut newStatus) throws DAOException {
        if (reservation.getVehicules() != null) {
            for (Vehicule vehicule : reservation.getVehicules()) {
                vehicule.setStatut(newStatus);
                updateObject(vehicule, Vehicule.class);
            }
        }
        if (reservation.getChauffeurs() != null) {
            for (Chauffeur chauffeur : reservation.getChauffeurs()) {
                chauffeur.setStatut(newStatus);
                updateObject(chauffeur, Chauffeur.class);
            }
        }
    }

    /**
     * Envoie une notification à l'administration.
     * @param title Titre de la notification.
     * @param message Message de la notification.
     * @param type Type de notification.
     */
    private void sendReservationStatusNotification(String title, String message, NotificationType type) {
        try {
            Notification notification = new Notification(title, message, type, reservation.getId());
            NotificationService.getInstance().addNotification(notification);
            Subject.getInstance().notifyAllObservers(); // Notifie les observateurs de l'admin
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Envoie une notification spécifique au client.
     * @param title Titre de la notification.
     * @param message Message de la notification.
     * @param type Type de notification.
     * @param client Le client concerné.
     */
    private void sendReservationStatusNotificationForClient(String title, String message, NotificationType type, Client client) {
        try {
            Notification notification = new Notification(title, message, type, reservation.getId());
            NotificationService.getInstance().addNotificationForClient(notification, client);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showAlertConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private void closeStage() {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
