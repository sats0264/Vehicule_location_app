package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader; // Import for FXMLLoader
import javafx.scene.Parent; // Import for Parent
import javafx.scene.layout.HBox; // Import for HBox
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.models.*;
import location.app.vehicule_location_app.observer.Subject;
import org.hibernate.Transaction;

import java.io.IOException; // Import for IOException
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class UIClientReservationController {

    @FXML
    private ImageView imageView;
    @FXML
    private Label marqueLabel;
    @FXML
    private Label modeleLabel;
    @FXML
    private Label immatriculationLabel;
    @FXML
    private Label typeReservationLabel;
    @FXML
    private Label prixLabel;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private Button validerBtn;
    @FXML
    private Button annulerBtn;

    @FXML
    private HBox chauffeurSelectionBox; // Conteneur pour le bouton "Choisir un chauffeur"
    @FXML
    private Button chooseChauffeurBtn; // Bouton "Choisir un chauffeur"
    @FXML
    private Label selectedChauffeurLabel; // Label pour afficher le chauffeur sélectionné

    private Vehicule vehicule;
    private Client client;
    private boolean avecChauffeur;
    private int reservationId;
    private Chauffeur selectedChauffeur; // Pour stocker le chauffeur choisi

    private Stage getStage() {
        return (Stage) validerBtn.getScene().getWindow();
    }

    @FXML
    private void initialize() {
        // Initialisation des dates par défaut
        dateDebutPicker.setValue(LocalDate.now());
        dateFinPicker.setValue(LocalDate.now().plusDays(1));

        // Validation des dates
        dateDebutPicker.setOnAction(e -> validateDates());
        dateFinPicker.setOnAction(e -> validateDates());

        validerBtn.setOnAction(e -> validerReservation());
        annulerBtn.setOnAction(e -> getStage().close());

        // Action pour le bouton "Choisir un chauffeur"
        chooseChauffeurBtn.setOnAction(e -> handleChooseChauffeur());

        // Initialisation de la visibilité des éléments liés au chauffeur
        chauffeurSelectionBox.setVisible(false);
        chauffeurSelectionBox.setManaged(false);
        selectedChauffeurLabel.setVisible(false);
        selectedChauffeurLabel.setManaged(false);
    }

    /**
     * Initialise la réservation avec les données dynamiques du véhicule sélectionné.
     * Cette méthode est appelée depuis l'extérieur (ex: UIClientController).
     */
    public void initReservation(String marque, String modele, String immatriculation, String imageUrl, boolean avecChauffeur) {
        this.avecChauffeur = avecChauffeur;

        // Affichage des informations du véhicule
        marqueLabel.setText(marque); // Supprimé "Marque :" car le FXML le gère maintenant
        modeleLabel.setText(modele);
        immatriculationLabel.setText(immatriculation);
        typeReservationLabel.setText(avecChauffeur ? "Avec chauffeur" : "Sans chauffeur");

        // Gérer la visibilité du bouton "Choisir un chauffeur"
        chauffeurSelectionBox.setVisible(avecChauffeur);
        chauffeurSelectionBox.setManaged(avecChauffeur);
        selectedChauffeurLabel.setVisible(false); // Réinitialiser le label du chauffeur sélectionné
        selectedChauffeurLabel.setManaged(false);

        // Le prix sera mis à jour par setVehicule()
    }

    /**
     * Charge l'image du véhicule avec gestion d'erreur robuste
     */
    private void loadVehiculeImage(String imageUrl) {
        String photoName = null;
        if (vehicule != null && vehicule.getPhoto() != null && !vehicule.getPhoto().isEmpty()) {
            photoName = vehicule.getPhoto();

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
                    // Attention: les URL externes nécessitent une connexion internet
                    Image image = new Image(vehicule.getPhoto(), true);
                    imageView.setImage(image);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image du véhicule: " + e.getMessage());
                imageView.setImage(null); // Efface l'image en cas d'erreur
            }
        } else {
            imageView.setImage(null); // Pas de photo définie ou véhicule null
        }
    }

    /**
     * Définit le véhicule pour cette réservation et met à jour le prix.
     */
    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;

        // Mettre à jour le prix avec les données réelles
        if (vehicule != null) {
            double prixJour = vehicule.getTarif();
            if (avecChauffeur) {
                prixJour += 7000;
            }
            prixLabel.setText(String.format("%.0f FCFA / jour", prixJour)); // Supprimé "Prix :"
            loadVehiculeImage(vehicule.getPhoto());
        } else {
            prixLabel.setText("Prix : N/A");
            imageView.setImage(null);
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getReservationId() {
        return reservationId;
    }

    /**
     * Valide que les dates sélectionnées sont cohérentes
     */
    private void validateDates() {
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut == null || dateFin == null) {
            // Ne pas valider si une des dates est nulle, l'utilisateur est peut-être en train de choisir
            return;
        }

        if (dateDebut.isAfter(dateFin)) {
            showAlert(Alert.AlertType.WARNING, "Dates invalides", "La date de début ne peut pas être après la date de fin.");
            dateFinPicker.setValue(dateDebut.plusDays(1)); // Réinitialiser la date de fin
            return;
        }

        if (dateDebut.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Date invalide", "La date de début ne peut pas être dans le passé.");
            dateDebutPicker.setValue(LocalDate.now()); // Réinitialiser la date de début
        }
    }

    /**
     * Gère l'action du bouton "Choisir un chauffeur".
     * Ouvre une nouvelle fenêtre pour la sélection du chauffeur.
     */
    private void handleChooseChauffeur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIChauffeurSelection.fxml"));
            Parent root = loader.load();

            UIChauffeurSelectionController chauffeurController = loader.getController();
            // Passez les dates de réservation pour que le contrôleur de sélection puisse filtrer les chauffeurs disponibles
            chauffeurController.initData(dateDebutPicker.getValue(), dateFinPicker.getValue());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Sélectionner un Chauffeur");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attend la fermeture de la fenêtre de sélection

            // Après la fermeture de la fenêtre, récupère le chauffeur sélectionné
            Chauffeur chosenChauffeur = chauffeurController.getSelectedChauffeur();
            if (chosenChauffeur != null) {
                this.selectedChauffeur = chosenChauffeur;
                selectedChauffeurLabel.setText("Chauffeur sélectionné : " + chosenChauffeur.getPrenom() + " " + chosenChauffeur.getNom());
                selectedChauffeurLabel.setVisible(true);
                selectedChauffeurLabel.setManaged(true);
                System.out.println("Chauffeur sélectionné: " + chosenChauffeur.getNom());
            } else {
                this.selectedChauffeur = null;
                selectedChauffeurLabel.setText("Aucun chauffeur sélectionné.");
                selectedChauffeurLabel.setVisible(true); // Garder visible pour indiquer l'absence de sélection
                selectedChauffeurLabel.setManaged(true);
                System.out.println("Aucun chauffeur sélectionné.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger l'écran de sélection de chauffeur.");
        }
    }

    /**
     * Valide et enregistre la réservation dans la base de données
     */
    private void validerReservation() {
        Transaction transaction = null;
        try (org.hibernate.Session session = location.app.vehicule_location_app.jdbc.HibernateConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Vérifie et récupère le client connecté si besoin
            if (client == null) {
                // Tentative de récupération automatique du client connecté via email système
                String emailConnecte = System.getProperty("client.email");
                if (emailConnecte != null && !emailConnecte.isEmpty()) {
                    Client c = session.createQuery(
                                    "from T_Client where lower(email) = :email", Client.class)
                            .setParameter("email", emailConnecte.toLowerCase())
                            .uniqueResult();
                    if (c != null) {
                        client = c;
                    }
                }
            }

            if (!validateReservationData()) {
                if (transaction != null) transaction.rollback();
                return;
            }

            // Récupérer les entités attachées dans la même session
            Vehicule attachedVehicule = session.find(Vehicule.class, vehicule.getId());
            Client attachedClient = session.find(Client.class, client.getId());

            // Récupération de l'id du client connecté
            int clientId = client.getId();
            System.out.println("ID du client : " + clientId);

            Reservation reservation = new Reservation();
            reservation.addVehicule(attachedVehicule);
            reservation.setClient(attachedClient);
            reservation.setDateDebut(dateDebutPicker.getValue());
            reservation.setDateFin(dateFinPicker.getValue());
            reservation.setStatut(StatutReservation.EN_ATTENTE);

            // Ajouter le chauffeur si la réservation est avec chauffeur et un chauffeur a été sélectionné
            if (avecChauffeur && selectedChauffeur != null) {
                Chauffeur attachedChauffeur = session.find(Chauffeur.class, selectedChauffeur.getId());
                reservation.addChauffeur(attachedChauffeur);
            } else if (avecChauffeur && selectedChauffeur == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez choisir un chauffeur pour une réservation avec chauffeur.");
                if (transaction != null) transaction.rollback();
                return;
            }


            double prixJourVehicule = attachedVehicule.getTarif();
            long nombreJours = ChronoUnit.DAYS.between(dateDebutPicker.getValue(), dateFinPicker.getValue()) + 1;
            if (nombreJours <= 0) {
                nombreJours = 1;
            }

            double montantTotal = prixJourVehicule * nombreJours;

            // Ajouter les frais de chauffeur au montant total si applicable
//            if (avecChauffeur && selectedChauffeur != null) {
//                montantTotal += CHAUFFEUR_DAILY_FEE * nombreJours; // Frais de chauffeur journaliers
//            }

            // Note : Votre entité Reservation n'a pas de champ 'montantTotal' directement.
            // La facture est généralement créée après la réservation.
            // Pour l'instant, nous calculons le montant ici pour l'affichage dans l'alerte.

            session.persist(reservation); // Persiste la réservation (et les chauffeurs/véhicules si en cascade)
            transaction.commit();

            reservationId = reservation.getId();

            // Envoi de la notification aux utilisateurs
            Notification notificationToUser = new Notification(
                    "Nouvelle Demande de réservation",
                    "Le client " + attachedClient.getNom() + " " + attachedClient.getPrenom() +
                            " a fait une demande",
                    NotificationType.NEW_RESERVATION,
                    reservationId
            );
            Notification notificationToClient = new Notification(
                    "Demande de réservation enregistrée",
                    "Votre demande de réservation a été enregistrée avec succès.",
                    NotificationType.CLIENT_NEW_RESERVATION,
                    reservationId
            );
            Subject.getInstance().notifyAllObservers();
            NotificationService.getInstance().addNotification(notificationToUser);
            NotificationService.getInstance().addNotificationForClient(notificationToClient, client);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Réservation confirmée");
            alert.setHeaderText("Réservation N°" + reservationId);
            alert.setContentText(
                    "Votre réservation a été enregistrée avec succès !\n\n" +
                            "Détails :\n" +
                            "• Véhicule : " + attachedVehicule.getMarque() + " " + attachedVehicule.getModele() + "\n" +
                            "• Du : " + dateDebutPicker.getValue() + "\n" +
                            "• Au : " + dateFinPicker.getValue() + "\n" +
                            "• Durée : " + nombreJours + " jour(s)\n" +
                            "• Type : " + (avecChauffeur ? "Avec chauffeur" : "Sans chauffeur") +
                            (avecChauffeur && selectedChauffeur != null ? "\n• Chauffeur : " + selectedChauffeur.getPrenom() + " " + selectedChauffeur.getNom() : "") + "\n" +
                            "• Montant total estimé : " + String.format("%.0f", montantTotal) + " FCFA\n" +
                            "• Statut : En attente de validation"
            );
            alert.showAndWait();

            getStage().close();

        } catch (Exception ex) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            ex.printStackTrace(); // Imprime la stack trace pour le débogage
            showAlert(Alert.AlertType.ERROR, "Erreur d'enregistrement", "Impossible d'enregistrer la réservation : " + ex.getMessage());
        }
    }

    /**
     * Valide les données de la réservation avant enregistrement
     */
    private boolean validateReservationData() {
        // Vérification du véhicule
        if (vehicule == null) {
            showAlert(Alert.AlertType.WARNING, "Vehicule","Aucun vehicule sélectionné ");
            return false;
        }

        // Vérification du client
        if (client == null) {
            showAlert(Alert.AlertType.WARNING, "Client","Aucun client connecté !");
//            showError("Aucun client connecté !");
            return false;
        }

        // Vérification des dates
        if (dateDebutPicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Date","Veuillez sélectionner une date de début !");
//            showError("Veuillez sélectionner une date de début !");
            return false;
        }

        if (dateFinPicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Date","Veuillez sélectionner une date de fin !");
//            showError("Veuillez sélectionner une date de fin !");
            return false;
        }

        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut.isAfter(dateFin)) {
            showAlert(Alert.AlertType.WARNING, "Date", "La date de début ne peut pas être après la date de fin !");
//            showError("La date de début ne peut pas être après la date de fin !");
            return false;
        }

        if (dateDebut.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING,"Date", "La date de début ne peut pas être dans le passé !");
//            showError("La date de début ne peut pas être dans le passé !");
            return false;
        }

        // Si avec chauffeur, vérifier si un chauffeur a été sélectionné
        if (avecChauffeur && selectedChauffeur == null) {
            showAlert(Alert.AlertType.WARNING,"Chauffeur", "Veuillez choisir un chauffeur pour une réservation avec chauffeur !");
//            showError("Veuillez choisir un chauffeur pour une réservation avec chauffeur !");
            return false;
        }

        return true;
    }

    /**
     * Affiche une alerte d'erreur
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}