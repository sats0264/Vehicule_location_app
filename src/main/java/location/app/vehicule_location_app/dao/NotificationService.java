package location.app.vehicule_location_app.dao;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import location.app.vehicule_location_app.controllers.Controller; // Make sure this import is correct
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.factory.ConcreteFactory;
import location.app.vehicule_location_app.factory.HibernateFactory;
import location.app.vehicule_location_app.models.Notification;
import location.app.vehicule_location_app.models.NotificationReception;
import location.app.vehicule_location_app.models.Utilisateur;
import location.app.vehicule_location_app.observer.Subject;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationService extends Subject {

    private static NotificationService instance;
    private Utilisateur utilisateurActif;
    private final ObservableList<NotificationReception> receptions;
    private final IntegerProperty unreadCount;

    private NotificationService() {
        receptions = FXCollections.observableArrayList();
        unreadCount = new SimpleIntegerProperty(0);
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateurActif = utilisateur;
        chargerNotificationsUtilisateur();
    }

    private void chargerNotificationsUtilisateur() {
        if (utilisateurActif == null) return;

        try {
            var receptionDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getNotificationReceptionDaoImpl(); // Assuming this specific DAO exists

            List<NotificationReception> list = receptionDao.findByUtilisateur(utilisateurActif); // Assuming this method exists
            receptions.setAll(list);
            updateUnreadCount();
            notifyAllObservers();

        } catch (DAOException e) {
            throw new RuntimeException("Erreur lors du chargement des notifications utilisateur", e);
        }
    }

    public ObservableList<Notification> getNotifications() {
        return receptions.stream()
                .map(NotificationReception::getNotification)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public void markAsRead(Notification notification) {
        if (utilisateurActif == null) return;

        try {
            var receptionDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getNotificationReceptionDaoImpl(); // Assuming this specific DAO exists

            NotificationReception reception = receptionDao.findByNotificationAndUser(notification, utilisateurActif); // Assuming this method exists
            if (reception != null && !reception.isRead()) {
                reception.setRead(true);
                receptionDao.update(reception);
                chargerNotificationsUtilisateur(); // Refresh the current user's notifications
            }

        } catch (DAOException e) {
            throw new RuntimeException("Erreur lors du marquage comme lu", e);
        }
    }

    public void markAllAsRead() {
        if (utilisateurActif == null) return;

        try {
            var receptionDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getHibernateObjectDaoImpl(NotificationReception.class);

            for (NotificationReception reception : receptions) {
                if (!reception.isRead()) {
                    reception.setRead(true);
                    receptionDao.update(reception);
                }
            }
            chargerNotificationsUtilisateur(); // Refresh for the current user

        } catch (DAOException e) {
            throw new RuntimeException("Erreur lors du marquage global comme lu", e);
        }
    }

    public void addNotification(Notification notification) {
        try {
            // 1. Persist the Notification object first, once.
            Controller.ajouterObject(notification, Notification.class);

            // Now that the notification is persisted, it has an ID and is managed by Hibernate.

            var userDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getHibernateObjectDaoImpl(Utilisateur.class);

            var receptionDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getHibernateObjectDaoImpl(NotificationReception.class);


            List<Utilisateur> users = userDao.list();

            for (Utilisateur user : users) {
                // Create NotificationReception, referencing the already persisted Notification
                NotificationReception reception = new NotificationReception(notification, user);

                // 2. Explicitly persist each NotificationReception.
                //    (Consider configuring CascadeType.ALL on Notification's 'receptions' collection
                //    in your entity mappings if you want this to be handled automatically when Notification is persisted.)
                receptionDao.create(reception);

                // These lines are for managing in-memory collections;
                // they are not strictly necessary for persistence if you explicitly save reception.
                // If you use CascadeType.ALL, these would trigger persistence.
                // notification.addReception(reception);
                // user.addNotification(reception);
            }

            // 3. After all database operations for this notification are complete,
            //    refresh the active user's view and notify observers.
            if (utilisateurActif != null) {
                chargerNotificationsUtilisateur();
            }
            notifyAllObservers();

        } catch (DAOException e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }
    }

    private void updateUnreadCount() {
        long count = receptions.stream().filter(r -> !r.isRead()).count();
        unreadCount.set((int) count);
    }

    public IntegerProperty unreadCountProperty() {
        return unreadCount;
    }

    public int getUnreadCount() {
        return unreadCount.get();
    }
}