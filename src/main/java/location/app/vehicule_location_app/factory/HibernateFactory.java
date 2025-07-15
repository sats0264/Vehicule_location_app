package location.app.vehicule_location_app.factory;

import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.dao.NotificationReceptionDaoImpl;
import location.app.vehicule_location_app.exceptions.DAOException;

public interface HibernateFactory {
    <T> HibernateObjectDaoImpl<T> getHibernateObjectDaoImpl(Class<T> entityClass) throws DAOException;
    NotificationReceptionDaoImpl getNotificationReceptionDaoImpl() throws DAOException;
}
