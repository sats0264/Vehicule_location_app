package location.app.vehicule_location_app.dao;

import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.jdbc.HibernateConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class HibernateObjectDaoImpl<T> implements IDao<T> {

    private final Class<T> type;

    public HibernateObjectDaoImpl(Class<T> type) {
        this.type = type;
    }

    @Override
    public void create(T entity) throws DAOException {
        try {
            Session session = HibernateConnection.getInstance().getSession();
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            throw new DAOException("ERROR : " + e.getClass() + " : " + e.getMessage());
        }
    }

    @Override
    public T read(int id) throws DAOException {
        try {
            Session session = HibernateConnection.getInstance().getSession();
            return session.find(type, id);
        } catch (Exception e) {
            throw new DAOException("ERROR : " + e.getClass() + ":" + e.getMessage());
        }
    }

    @Override
    public List<T> list() throws DAOException {
        try {
            Session session = HibernateConnection.getInstance().getSession();
            String query = "from " + type.getSimpleName();
            return session.createQuery(query, type).getResultList();
        } catch (Exception e) {
            throw new DAOException("ERROR : " + e.getClass() + ":" + e.getMessage());
        }
    }

    @Override
    public void update(T entity) throws DAOException {
        try {
            Session session = HibernateConnection.getInstance().getSession();
            Transaction transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            throw new DAOException("ERROR : " + e.getClass() + ":" + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws DAOException {
        try {
            Session session = HibernateConnection.getInstance().getSession();
            Transaction transaction = session.beginTransaction();
            T entity = read(id);
            if (entity != null) {
                session.remove(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            throw new DAOException("ERROR : " + e.getClass() + ":" + e.getMessage());
        }
    }
}