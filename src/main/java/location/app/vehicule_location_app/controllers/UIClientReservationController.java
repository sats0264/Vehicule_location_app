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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import location.app.vehicule_location_app.dao.NotificationService;
import location.app.vehicule_location_app.models.*;
import location.app.vehicule_location_app.observer.Subject;
import org.hibernate.Transaction;

import java.io.IOException;
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
    private HBox chauffeurSelectionBox;
    @FXML
    private Button chooseChauffeurBtn;
    @FXML
    private Label selectedChauffeurLabel;

    private Vehicule vehicule;
    private Client client;
    private boolean avecChauffeur;
    private int reservationId;
    private Chauffeur selectedChauffeur;

    private Stage getStage() {
        return (Stage) validerBtn.getScene().getWindow();
    }

    @FXML
    private void initialize() {
        dateDebutPicker.setValue(LocalDate.now());
        dateFinPicker.setValue(LocalDate.now().plusDays(1));

        dateDebutPicker.setOnAction(e -> validateDates());
        dateFinPicker.setOnAction(e -> validateDates());

        validerBtn.setOnAction(e -> validerReservation());
        annulerBtn.setOnAction(e -> getStage().close());

        chooseChauffeurBtn.setOnAction(e -> handleChooseChauffeur());

        chauffeurSelectionBox.setVisible(false);
        chauffeurSelectionBox.setManaged(false);
        selectedChauffeurLabel.setVisible(false);
        selectedChauffeurLabel.setManaged(false);
    }

    public void initReservation(String marque, String modele, String immatriculation, String imageUrl, boolean avecChauffeur) {
        this.avecChauffeur = avecChauffeur;

        marqueLabel.setText(marque);
        modeleLabel.setText(modele);
        immatriculationLabel.setText(immatriculation);
        typeReservationLabel.setText(avecChauffeur ? "Avec chauffeur" : "Sans chauffeur");

        chauffeurSelectionBox.setVisible(avecChauffeur);
        chauffeurSelectionBox.setManaged(avecChauffeur);
        selectedChauffeurLabel.setVisible(false);
        selectedChauffeurLabel.setManaged(false);

    }

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

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;

        if (vehicule != null) {
            double prixJour = vehicule.getTarif();
            if (avecChauffeur) {
                prixJour += 7000;
            }
            prixLabel.setText(String.format("%.0f FCFA / jour", prixJour));
            loadVehiculeImage(vehicule.getPhoto());
        } else {
            prixLabel.setText("Prix : N/A");
            imageView.setImage(null);
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private void validateDates() {
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut == null || dateFin == null) {
            return;
        }

        if (dateDebut.isAfter(dateFin)) {
            showAlert(Alert.AlertType.WARNING, "Dates invalides", "La date de début ne peut pas être après la date de fin.");
            dateFinPicker.setValue(dateDebut.plusDays(1));
            return;
        }

        if (dateDebut.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Date invalide", "La date de début ne peut pas être dans le passé.");
            dateDebutPicker.setValue(LocalDate.now());
        }
    }

    private void handleChooseChauffeur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UIChauffeurSelection.fxml"));
            Parent root = loader.load();

            UIChauffeurSelectionController chauffeurController = loader.getController();
            chauffeurController.initData(dateDebutPicker.getValue(), dateFinPicker.getValue());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Sélectionner un Chauffeur");
            stage.setScene(new Scene(root));
            stage.showAndWait();

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
                selectedChauffeurLabel.setVisible(true);
                selectedChauffeurLabel.setManaged(true);
                System.out.println("Aucun chauffeur sélectionné.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger l'écran de sélection de chauffeur.");
        }
    }

    private void validerReservation() {
        Transaction transaction = null;
        try (org.hibernate.Session session = location.app.vehicule_location_app.jdbc.HibernateConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            if (client == null) {
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

            Vehicule attachedVehicule = session.find(Vehicule.class, vehicule.getId());
            Client attachedClient = session.find(Client.class, client.getId());

            int clientId = client.getId();
            System.out.println("ID du client : " + clientId);

            Reservation reservation = new Reservation();
            reservation.addVehicule(attachedVehicule);
            reservation.setClient(attachedClient);
            reservation.setDateDebut(dateDebutPicker.getValue());
            reservation.setDateFin(dateFinPicker.getValue());
            reservation.setStatut(StatutReservation.EN_ATTENTE);

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

            session.persist(reservation);
            transaction.commit();

            reservationId = reservation.getId();

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
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur d'enregistrement", "Impossible d'enregistrer la réservation : " + ex.getMessage());
        }
    }

    private boolean validateReservationData() {
        if (vehicule == null) {
            showAlert(Alert.AlertType.WARNING, "Vehicule","Aucun vehicule sélectionné ");
            return false;
        }

        if (client == null) {
            showAlert(Alert.AlertType.WARNING, "Client","Aucun client connecté !");
            return false;
        }

        if (dateDebutPicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Date","Veuillez sélectionner une date de début !");
            return false;
        }

        if (dateFinPicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Date","Veuillez sélectionner une date de fin !");
            return false;
        }

        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut.isAfter(dateFin)) {
            showAlert(Alert.AlertType.WARNING, "Date", "La date de début ne peut pas être après la date de fin !");
            return false;
        }

        if (dateDebut.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING,"Date", "La date de début ne peut pas être dans le passé !");
            return false;
        }

        if (avecChauffeur && selectedChauffeur == null) {
            showAlert(Alert.AlertType.WARNING,"Chauffeur", "Veuillez choisir un chauffeur pour une réservation avec chauffeur !");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}