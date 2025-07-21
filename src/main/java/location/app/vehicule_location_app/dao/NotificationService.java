package location.app.vehicule_location_app.dao;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.factory.ConcreteFactory;
import location.app.vehicule_location_app.factory.HibernateFactory;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Notification;
import location.app.vehicule_location_app.models.NotificationReception;
import location.app.vehicule_location_app.models.Utilisateur;
import location.app.vehicule_location_app.observer.Subject;

import java.util.List;
import java.util.stream.Collectors;

import static location.app.vehicule_location_app.controllers.Controller.ajouterObject;

public class NotificationService extends Subject {

    private static NotificationService instance;
    private Utilisateur utilisateurActif;
    private Client clientActif;
    private final ObservableList<NotificationReception> receptions;
    private final IntegerProperty unreadCount;


    private NotificationService() {
        super();
        receptions = FXCollections.observableArrayList();
        unreadCount = new SimpleIntegerProperty(0);


        try {
            var userDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getHibernateObjectDaoImpl(Utilisateur.class);

            var receptionDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getHibernateObjectDaoImpl(NotificationReception.class);

        } catch (DAOException e) {
            throw new RuntimeException("Erreur lors de l'initialisation du NotificationService", e);
        }
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
    public void setClient(Client client) {
        this.clientActif = client;
        chargerNotificationsClient();
    }

    private void chargerNotificationsClient() {
        if (clientActif == null) return;

        try {
            var receptionDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getNotificationReceptionDaoImpl();

            List<NotificationReception> list = receptionDao.findByClient(clientActif);
            receptions.setAll(list);
            updateUnreadCount();
            notifyAllObservers();

        } catch (DAOException e) {
            throw new RuntimeException("Erreur lors du chargement des notifications client", e);
        }
    }

    private void chargerNotificationsUtilisateur() {
        if (utilisateurActif == null) return;

        try {
            var receptionDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getNotificationReceptionDaoImpl();

            List<NotificationReception> list = receptionDao.findByUtilisateur(utilisateurActif);
            receptions.setAll(list);
            updateUnreadCount();
            notifyAllObservers();

        } catch (DAOException e) {
            throw new RuntimeException("Erreur lors du chargement des notifications utilisateur", e);
        }
    }

    private void chargerNotificationsClientClient() {
        if (clientActif == null) return;

        try {
            var receptionDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getNotificationReceptionDaoImpl();

            List<NotificationReception> list = receptionDao.findByClient(clientActif);
            receptions.setAll(list);
            updateUnreadCount();
            notifyAllObservers();

        } catch (DAOException e) {
            throw new RuntimeException("Erreur lors du chargement des notifications client", e);
        }
    }

    public ObservableList<Notification> getNotifications() {
        return receptions.stream()
                .map(reception -> {
                    Notification notif = reception.getNotification();
                    notif.getReceptions().add(reception); // force le lien
                    return notif;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }


    public void markAsRead(Notification notification) {
        if (utilisateurActif != null) {
            try {
                var receptionDao = ConcreteFactory
                        .getFactory(HibernateFactory.class)
                        .getHibernateObjectDaoImpl(NotificationReception.class);

                NotificationReception reception = receptions.stream()
                        .filter(r -> r.getNotification().equals(notification) && r.getUtilisateur().equals(utilisateurActif))
                        .findFirst()
                        .orElse(null);

                if (reception != null && !reception.isRead()) {
                    reception.setRead(true);
                    receptionDao.update(reception);
                    updateUnreadCount();
                    notifyAllObservers();
                }

            } catch (DAOException e) {
                throw new RuntimeException("Erreur lors du marquage comme lu", e);
            }
        }
        else if (clientActif != null) {
            try {
                var receptionDao = ConcreteFactory
                        .getFactory(HibernateFactory.class)
                        .getNotificationReceptionDaoImpl();

                NotificationReception reception = receptions.stream()
                        .filter(r -> r.getNotification().equals(notification) && r.getClient().equals(clientActif))
                        .findFirst()
                        .orElse(null);

                if (reception != null && !reception.isRead()) {
                    reception.setRead(true);
                    receptionDao.update(reception);
                    updateUnreadCount();
                    notifyAllObservers();
                }

            } catch (DAOException e) {
                throw new RuntimeException("Erreur lors du marquage comme lu", e);
            }
        }
        else {
            throw new IllegalStateException("Aucun utilisateur ou client actif pour marquer la notification comme lue");
        }
    }

    public void markAllAsRead() {
        if (utilisateurActif != null) {
            try {
                var receptionDao = ConcreteFactory
                        .getFactory(HibernateFactory.class)
                        .getNotificationReceptionDaoImpl();

                for (NotificationReception reception : receptions) {
                    if (reception.getUtilisateur().equals(utilisateurActif) && !reception.isRead()) {
                        reception.setRead(true);
                        receptionDao.update(reception);
                    }
                }
                updateUnreadCount();
                notifyAllObservers();

            } catch (DAOException e) {
                throw new RuntimeException("Erreur lors du marquage de toutes les notifications comme lues", e);
            }
        }else if (clientActif != null) {
            try {
                var receptionDao = ConcreteFactory
                        .getFactory(HibernateFactory.class)
                        .getNotificationReceptionDaoImpl();

                for (NotificationReception reception : receptions) {
                    if (reception.getClient().equals(clientActif) && !reception.isRead()) {
                        reception.setRead(true);
                        receptionDao.update(reception);
                    }
                }
                updateUnreadCount();
                notifyAllObservers();

            } catch (DAOException e) {
                throw new RuntimeException("Erreur lors du marquage de toutes les notifications comme lues", e);
            }
        }
        else {
            throw new IllegalStateException("Aucun utilisateur actif ou client actif pour marquer les notifications comme lues");
        }
    }

    public void addNotification(Notification notification) {
        try {
            ajouterObject(notification, Notification.class);

            var userDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getHibernateObjectDaoImpl(Utilisateur.class);

            var receptionDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getNotificationReceptionDaoImpl();

            List<Utilisateur> users = userDao.list();

            for (Utilisateur user : users) {
                NotificationReception reception = new NotificationReception(notification, user);
                receptionDao.create(reception);
            }

            if (utilisateurActif != null) {
                chargerNotificationsUtilisateur();
            }
            notifyAllObservers();

        } catch (DAOException e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }
    }
    public void addNotificationForClient(Notification notification, Client client) {
        try {
            ajouterObject(notification, Notification.class);

            var receptionDao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getNotificationReceptionDaoImpl();

            NotificationReception reception = new NotificationReception(notification, client);
            receptionDao.create(reception);

            if (clientActif != null && clientActif.equals(client)) {
                chargerNotificationsClient();
            }
            notifyAllObservers();

        } catch (DAOException e) {
            throw new RuntimeException("Erreur lors de la création de la notification pour le client", e);
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

    public void reloadNotificationsFromDB() {
        if (utilisateurActif != null) {
            try {
                var receptionDao = ConcreteFactory
                        .getFactory(HibernateFactory.class)
                        .getNotificationReceptionDaoImpl();

                List<NotificationReception> list = receptionDao.findByUtilisateur(utilisateurActif);

                receptions.setAll(list);
                updateUnreadCount();
                notifyAllObservers();
            } catch (DAOException e) {
                throw new RuntimeException("Erreur lors du rechargement", e);
            }
        }else if (clientActif != null) {
            try {
                var receptionDao = ConcreteFactory
                        .getFactory(HibernateFactory.class)
                        .getNotificationReceptionDaoImpl();

                List<NotificationReception> list = receptionDao.findByClient(clientActif);

                receptions.setAll(list);
                updateUnreadCount();
                notifyAllObservers();
            } catch (DAOException e) {
                throw new RuntimeException("Erreur lors du rechargement", e);
            }
        }
        else {
            throw new IllegalStateException("Aucun utilisateur ou client actif pour recharger les notifications");
        }
    }

}