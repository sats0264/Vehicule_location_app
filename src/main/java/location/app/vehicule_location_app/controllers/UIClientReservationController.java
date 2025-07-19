package location.app.vehicule_location_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Reservation;
import location.app.vehicule_location_app.models.StatutReservation;
import location.app.vehicule_location_app.models.Vehicule;
import org.hibernate.Transaction;

import java.io.InputStream;
import java.time.LocalDate;

public class UIClientReservationController {

    @FXML
    private ImageView voitureImage;
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
    private ImageView imageView;

    private Vehicule vehicule;
    private Client client;
    private boolean avecChauffeur;
    private int reservationId;

    private Stage getStage() {
        return (Stage) validerBtn.getScene().getWindow();
    }

    /**
     * Initialise la réservation avec les données dynamiques du véhicule sélectionné
     */
    public void initReservation(String marque, String modele, String immatriculation, String imageUrl, boolean avecChauffeur) {
        this.avecChauffeur = avecChauffeur;

        // Affichage des informations du véhicule
        marqueLabel.setText("Marque : " + marque);
        modeleLabel.setText("Modèle : " + modele);
        immatriculationLabel.setText("Immatriculation : " + immatriculation);
        typeReservationLabel.setText(avecChauffeur ? "Avec chauffeur" : "Sans chauffeur");

        // Calcul du prix dynamique basé sur le véhicule
        if (vehicule != null) {
            double prixJour = vehicule.getTarif();
            if (avecChauffeur) {
                // Prix avec chauffeur coûte 20% de plus
                prixJour = prixJour * 1.2;
            }
            prixLabel.setText("Prix : " + String.format("%.0f", prixJour) + " FCFA / jour");
        } else {
            // Fallback si le véhicule n'est pas encore défini
            prixLabel.setText(avecChauffeur ? "Prix : À calculer" : "Prix : À calculer");
        }
    }

    /**
     * Charge l'image du véhicule avec gestion d'erreur robuste
     */
    private void loadVehiculeImage(String imageUrl) {
        String photoName = null;
        // Chargement de l'image depuis ressources/images si possible
        if (vehicule.getPhoto() != null && !vehicule.getPhoto().isEmpty()) {
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
                    Image image = new Image(vehicule.getPhoto(), true);
                    imageView.setImage(image);
                }
            } catch (Exception e) {
                imageView.setImage(null);
            }
        }
    }

    /**
     * Met à jour le prix affiché quand le véhicule est défini
     */
    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;

        // Mettre à jour le prix avec les données réelles
        if (vehicule != null) {
            double prixJour = vehicule.getTarif();
            if (avecChauffeur) {
                prixJour = prixJour * 1.2; // 20% de plus avec chauffeur
            }
            prixLabel.setText("Prix : " + String.format("%.0f", prixJour) + " FCFA / jour");
            loadVehiculeImage(vehicule.getPhoto());
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getReservationId() {
        return reservationId;
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
    }

    /**
     * Valide que les dates sélectionnées sont cohérentes
     */
    private void validateDates() {
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut != null && dateFin != null) {
            if (dateDebut.isAfter(dateFin)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Dates invalides");
                alert.setHeaderText(null);
                alert.setContentText("La date de début ne peut pas être après la date de fin.");
                alert.showAndWait();

                // Réinitialiser la date de fin
                dateFinPicker.setValue(dateDebut.plusDays(1));
            }

            if (dateDebut.isBefore(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Date invalide");
                alert.setHeaderText(null);
                alert.setContentText("La date de début ne peut pas être dans le passé.");
                alert.showAndWait();

                // Réinitialiser la date de début
                dateDebutPicker.setValue(LocalDate.now());
            }
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

            double prixJour = attachedVehicule.getTarif();
            if (avecChauffeur) {
                prixJour = prixJour * 1.2;
            }

            long nombreJours = java.time.temporal.ChronoUnit.DAYS.between(
                    dateDebutPicker.getValue(),
                    dateFinPicker.getValue()
            );
            if (nombreJours <= 0) {
                nombreJours = 1;
            }

            double montantTotal = prixJour * nombreJours;
            // reservation.setMontantTotal(montantTotal);

            session.persist(reservation);
            transaction.commit();

            reservationId = reservation.getId();

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
                            "• Type : " + (avecChauffeur ? "Avec chauffeur" : "Sans chauffeur") + "\n" +
                            "• Montant total : " + String.format("%.0f", montantTotal) + " FCFA\n" +
                            "• Statut : En attente de validation"
            );
            alert.showAndWait();

            getStage().close();

        } catch (Exception ex) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'enregistrement");
            alert.setHeaderText("Impossible d'enregistrer la réservation");
            alert.setContentText("Erreur technique : " + ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Valide les données de la réservation avant enregistrement
     */
    private boolean validateReservationData() {
        // Vérification du véhicule
        if (vehicule == null) {
            showError("Aucun véhicule sélectionné !");
            return false;
        }

        // Vérification du client
        if (client == null) {
            showError("Aucun client sélectionné !");
            return false;
        }

        // Vérification des dates
        if (dateDebutPicker.getValue() == null) {
            showError("Veuillez sélectionner une date de début !");
            return false;
        }

        if (dateFinPicker.getValue() == null) {
            showError("Veuillez sélectionner une date de fin !");
            return false;
        }

        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut.isAfter(dateFin)) {
            showError("La date de début ne peut pas être après la date de fin !");
            return false;
        }

        if (dateDebut.isBefore(LocalDate.now())) {
            showError("La date de début ne peut pas être dans le passé !");
            return false;
        }

        return true;
    }

    /**
     * Affiche une alerte d'erreur
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentClient(Client currentClient) {
        this.client = currentClient;
        if (client != null) {
            // Mettre à jour les informations du client si nécessaire
            System.out.println("Client actuel : " + client.getNom() + " " + client.getPrenom());
        } else {
            System.out.println("Aucun client sélectionné.");
        }
    }
}