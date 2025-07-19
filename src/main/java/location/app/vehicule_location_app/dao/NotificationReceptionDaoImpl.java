package location.app.vehicule_location_app.dao;

import jakarta.persistence.TypedQuery;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.jdbc.HibernateConnection;
import location.app.vehicule_location_app.models.Client;
import location.app.vehicule_location_app.models.Notification;
import location.app.vehicule_location_app.models.NotificationReception;
import location.app.vehicule_location_app.models.Utilisateur;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class NotificationReceptionDaoImpl extends HibernateObjectDaoImpl<NotificationReception> implements INotificationReceptionDao {

    public NotificationReceptionDaoImpl() {
        super(NotificationReception.class);
    }

    @Override
    public List<NotificationReception> findByUtilisateur(Utilisateur utilisateur) throws DAOException {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            String hql = "FROM NotificationReception r WHERE r.utilisateur = :utilisateur ORDER BY r.notification.timestamp DESC";
            TypedQuery<NotificationReception> query = session.createQuery(hql, NotificationReception.class);
            query.setParameter("utilisateur", utilisateur);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la récupération des notifications : " + e.getMessage());
        }
    }

    @Override
    public List<NotificationReception> findByClient(Client client) throws DAOException {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            String hql = "FROM NotificationReception r WHERE r.client = :client ORDER BY r.notification.timestamp DESC";
            TypedQuery<NotificationReception> query = session.createQuery(hql, NotificationReception.class);
            query.setParameter("client", client);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la récupération des notifications pour le client : " + e.getMessage());
        }
    }

    @Override
    public NotificationReception findByNotificationAndUser(Notification notification, Utilisateur utilisateur) throws DAOException {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            String hql = "FROM NotificationReception r WHERE r.utilisateur = :utilisateur AND r.notification = :notification";
            Query<NotificationReception> query = session.createQuery(hql, NotificationReception.class);
            query.setParameter("utilisateur", utilisateur);
            query.setParameter("notification", notification);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la récupération de la notification utilisateur : " + e.getMessage());
        }
    }

    @Override
    public NotificationReception findByNotificationAndClient(Notification notification, Client client) throws DAOException {
        try(Session session = HibernateConnection.getSessionFactory().openSession()) {
            String hql = "FROM NotificationReception r WHERE r.client = :client AND r.notification = :notification";
            Query<NotificationReception> query = session.createQuery(hql, NotificationReception.class);
            query.setParameter("client", client);
            query.setParameter("notification", notification);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la récupération de la notification client : " + e.getMessage());
        }
    }
}
