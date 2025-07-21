package location.app.vehicule_location_app.dao;

import jakarta.persistence.Entity;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.jdbc.HibernateConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
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
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Erreur rollback : " + rollbackEx.getMessage());
                }
            }
            throw new DAOException("Erreur création : " + e.getMessage());
        }
    }


    @Override
    public T readById(int id) throws DAOException {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            return session.find(type, id);
        } catch (Exception e) {
            throw new DAOException("Erreur lecture : " + e.getMessage());
        }
    }

    @Override
    public T readByString(String value) throws DAOException {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            String entityName = type.getAnnotation(Entity.class).name();
            String hql;
            if (value.contains("@")) {
                hql = "from " + entityName + " where email = :value";
            } else {
                hql = "from " + entityName + " where login = :value";
            }
            return session.createQuery(hql, type)
                    .setParameter("value", value)
                    .uniqueResult();
        } catch (Exception e) {
            throw new DAOException("Erreur lecture par chaîne : " + e.getMessage());
        }
    }

    @Override
    public List<T> list() throws DAOException {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
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

    public List<T> readAll() {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            String hql = "from " + type.getAnnotation(Entity.class).name();
            return session.createQuery(hql, type).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<T> getAll() {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            String hql = "from " + type.getAnnotation(Entity.class).name();
            return session.createQuery(hql, type).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<T> findByField(String fieldName, int id) {
        try (Session session = HibernateConnection.getSessionFactory().openSession()) {
            String entityName = type.getAnnotation(Entity.class).name();
            String hql = "from " + entityName + " where " + fieldName + " = :id";
            return session.createQuery(hql, type)
                    .setParameter("id", id)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

}