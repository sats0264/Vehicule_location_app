package location.app.vehicule_location_app.dao;

import jakarta.persistence.Entity;
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
        Transaction transaction = null;
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DAOException("Erreur création : " + e.getMessage());
        }
    }

    @Override
    public T read(int id) throws DAOException {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            return session.find(type, id);
        } catch (Exception e) {
            throw new DAOException("Erreur lecture : " + e.getMessage());
        }
    }

    @Override
    public List<T> list() throws DAOException {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
//            String hql = "from " + type.getSimpleName();
            String hql = "from " + type.getAnnotation(Entity.class).name();

            return session.createQuery(hql, type).getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur listing : " + e.getMessage());
        }
    }

    @Override
    public void update(T entity) throws DAOException {
        Transaction transaction = null;
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DAOException("Erreur update : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws DAOException {
        Transaction transaction = null;
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            T entity = session.find(type, id);
            if (entity == null) throw new DAOException("Objet non trouvé");
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DAOException("Erreur suppression : " + e.getMessage());
        }
    }

    public int getId(T entity) throws DAOException {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            if (entity == null) throw new DAOException("L'entité ne peut pas être nulle");
            Integer id = (Integer) session.getIdentifier(entity);
            if (id == null) throw new DAOException("L'entité n'a pas d'identifiant");
            return id;
        } catch (Exception e) {
            throw new DAOException("Erreur récupération ID : " + e.getMessage());
        }
    }


    public int getAllEntitiesCount() throws DAOException {
        Transaction transaction = null;
        try(Session session = HibernateConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "select count(*) from " + type.getSimpleName();
            Long count = session.createQuery(hql, Long.class).uniqueResult();
            transaction.commit();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DAOException("Erreur comptage des entités : " + e.getMessage());
        }
    }
}