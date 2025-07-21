package location.app.vehicule_location_app.controllers;

import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.factory.ConcreteFactory;
import location.app.vehicule_location_app.factory.HibernateFactory;
import location.app.vehicule_location_app.models.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    protected static HibernateObjectDaoImpl<Vehicule> vehiculeDao;
    protected static HibernateObjectDaoImpl<Client> clientDao;
    protected static HibernateObjectDaoImpl<Chauffeur> chauffeurDao;
    protected static HibernateObjectDaoImpl<Reservation> reservationDao;
    protected static HibernateObjectDaoImpl<Facture> factureDao;
    protected static HibernateObjectDaoImpl<Utilisateur> utilisateurDao;
    protected static List<Vehicule> controllerVehiculeList;
    protected static List<Chauffeur> controllerChauffeurList = new ArrayList<>();
    protected static List<Client> controllerClientList;
    protected static List<Reservation> controllerReservationList;
    protected static List<Reservation> reservationListActif;

    public Controller() throws DAOException {
        vehiculeDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(Vehicule.class);

        clientDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(Client.class);

        chauffeurDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(Chauffeur.class);

        reservationDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(Reservation.class);

        utilisateurDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(Utilisateur.class);

        factureDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(Facture.class);

        controllerVehiculeList = vehiculeDao.list();
        controllerClientList = clientDao.list();
        controllerReservationList = reservationDao.list();
        controllerChauffeurList = chauffeurDao.list();

        reservationListActif = controllerReservationList.stream()
                .filter(reservation -> reservation.getStatut() == StatutReservation.APPROUVEE )
                .toList();
    }

    public static <T> void ajouterObject(T entity, Class<T> entityClass) throws DAOException {
        HibernateObjectDaoImpl<T> hibernateDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(entityClass);
        hibernateDao.create(entity);
    }
    public static <T> void deleteObject(int objectId, Class<T> entityClass) throws DAOException {
        HibernateObjectDaoImpl<T> hibernateDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(entityClass);
        hibernateDao.delete(objectId);
    }
    public static <T> T rechercherObjectByInt(int objectId, Class<T> entityClass) throws DAOException {
        HibernateObjectDaoImpl<T> hibernateDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(entityClass);

        return hibernateDao.readById(objectId);
    }
    public static <T> T rechercherObjectByString(String value, Class<T> entityClass) throws DAOException {
        HibernateObjectDaoImpl<T> hibernateDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(entityClass);

        return hibernateDao.readByString(value);
    }
    public static <T> void updateObject(T entity, Class<T> entityClass) throws DAOException {
        HibernateObjectDaoImpl<T> hibernateDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(entityClass);

        hibernateDao.update(entity);
    }
    public static <T> List<T> listerObjects(Class<T> entityClass) throws DAOException {
        HibernateObjectDaoImpl<T> hibernateDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(entityClass);
        return hibernateDao.list();
    }
    public boolean authenticateClient(String email, String password) throws DAOException {
        List<Client> clients = clientDao.list();
        for (Client client : clients) {
            if (client.getEmail().equals(email) && client.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
    public boolean authenticateUser(String login, String password) throws DAOException {
        List<Utilisateur> users = utilisateurDao.list();
        for (Utilisateur user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public static void refreshChauffeurs() {
        try {
            var dao = ConcreteFactory
                    .getFactory(HibernateFactory.class)
                    .getHibernateObjectDaoImpl(Chauffeur.class);
            controllerChauffeurList = dao.list();
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }
}
