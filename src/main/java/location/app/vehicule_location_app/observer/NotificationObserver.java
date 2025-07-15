package location.app.vehicule_location_app.observer;

import location.app.vehicule_location_app.dao.NotificationService;

public class NotificationObserver extends Observer {

    public NotificationObserver(NotificationService service) {
        this.subject = service;
        this.subject.attach(this); // 🔗 S’attache au sujet (NotificationService)
    }

    @Override
    public void update() {
        // Code exécuté quand une nouvelle notification est ajoutée
        System.out.println("🔔 Nouvelle notification reçue !");
        // Tu peux ici recharger la UI si tu veux (ex: rafraîchir une TableView)
    }
}
