package location.app.vehicule_location_app.observer;

public class NotificationSubject extends Subject{

    private static NotificationSubject instance;

    private NotificationSubject() {}

    public static NotificationSubject getInstance() {
        if (instance == null) {
            instance = new NotificationSubject();
        }
        return instance;
    }
}
