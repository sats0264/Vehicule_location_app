package location.app.vehicule_location_app.dao;

import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.models.Notification;
import location.app.vehicule_location_app.models.NotificationReception;
import location.app.vehicule_location_app.models.Utilisateur;

import java.util.List;

public interface INotificationReceptionDao extends IDao<NotificationReception> {

    List<NotificationReception> findByUtilisateur(Utilisateur utilisateur) throws DAOException;

    NotificationReception findByNotificationAndUser(Notification notification, Utilisateur utilisateur) throws DAOException;
}
