package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_Notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String message;
    private String timestamp;


    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @Column(name = "entityId", nullable = false)
    private int entityId;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<NotificationReception> receptions = new ArrayList<>();

    public Notification() {}

    public Notification(String title, String message, NotificationType type, int entityId) {
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
//        this.isRead = false;
        this.type = type;
        this.entityId = entityId;
    }

    public void addReception(NotificationReception reception) {
        receptions.add(reception);
        reception.setNotification(this);
    }

    // --- Getters & Setters classiques ---
    public int getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public int getEntityId() { return entityId; }
    public void setEntityId(int entityId) { this.entityId = entityId; }

    @Override
    public String toString() {
        return title + " - " + message;
    }

    public List<NotificationReception> getReceptions() {
        return receptions;
    }
    public void setReceptions(List<NotificationReception> receptions) {
        this.receptions = receptions;
    }
}
