//package location.app.vehicule_location_app.models;
//
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class Notification {
//
//    public enum NotificationType {
//        NEW_CLIENT_REGISTRATION,
//        NEW_RESERVATION,
//        RESERVATION_MODIFICATION_REQUEST,
//        RESERVATION_CANCELLATION,
//        RESERVATION_CONFIRMATION,
//        PAYMENT_RECEIVED,
//
//    }
//
//    private final StringProperty title;
//    private final StringProperty message;
//    private final StringProperty timestamp;
//    private final BooleanProperty read;
//    private final NotificationType type;
//    private final String entityId; // ID of the related entity (e.g., client ID, reservation ID)
//
//    public Notification(String title, String message, NotificationType type, String entityId) {
//        this.title = new SimpleStringProperty(title);
//        this.message = new SimpleStringProperty(message);
//        this.timestamp = new SimpleStringProperty(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
//        this.read = new SimpleBooleanProperty(false); // Default to unread
//        this.type = type;
//        this.entityId = entityId;
//    }
//
//    // --- Properties for TableView/ListView ---
//    public StringProperty titleProperty() { return title; }
//    public StringProperty messageProperty() { return message; }
//    public StringProperty timestampProperty() { return timestamp; }
//    public BooleanProperty readProperty() { return read; }
//
//    // --- Getters ---
//    public String getTitle() { return title.get(); }
//    public String getMessage() { return message.get(); }
//    public String getTimestamp() { return timestamp.get(); }
//    public boolean isRead() { return read.get(); }
//    public NotificationType getType() { return type; }
//    public String getEntityId() { return entityId; }
//
//    // --- Setters ---
//    public void setRead(boolean read) { this.read.set(read); }
//
//    @Override
//    public String toString() {
//        return getTitle() + " - " + getMessage(); // Default display in ListView if no custom cell factory
//    }
//}
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

//    private boolean isRead;

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

//    public boolean isRead() { return isRead; }
//    public void setRead(boolean read) { this.isRead = read; }

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
