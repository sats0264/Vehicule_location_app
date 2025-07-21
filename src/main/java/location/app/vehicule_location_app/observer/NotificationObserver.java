package location.app.vehicule_location_app.observer;

import location.app.vehicule_location_app.dao.NotificationService;

public class NotificationObserver extends Observer {

    public NotificationObserver(NotificationService service) {
        this.subject = service;
        this.subject.attach(this);
    }

    @Override
    public void update() {
        System.out.println("🔔 Nouvelle notification reçue !");
    }
}
