package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

@Entity
@Table(name = "T_notification_receptions")
public class NotificationReception {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Notification notification;

    @ManyToOne
    private Utilisateur utilisateur;

    @ManyToOne
    private Client client;

    private boolean isRead = false;

    public NotificationReception(Notification notification, Utilisateur user) {
        this.notification = notification;
        this.utilisateur = user;
    }

    public NotificationReception() {

    }

    public NotificationReception(Notification notification, Client client) {
        this.notification = notification;
        this.client = client;
    }

    //getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Notification getNotification() {
        return notification;
    }
    public void setNotification(Notification notification) {
        this.notification = notification;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public boolean isRead() {
        return isRead;
    }
    public void setRead(boolean read) {
        isRead = read;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client clients) {
        this.client = clients;
    }
}
