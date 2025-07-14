package location.app.vehicule_location_app.controllers;

import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.factory.ConcreteFactory;
import location.app.vehicule_location_app.factory.HibernateFactory;
import location.app.vehicule_location_app.models.*;
import java.time.LocalDate;
import java.util.Arrays;

public class Controller {

    protected HibernateObjectDaoImpl<Vehicule> vehiculeDao;
    protected HibernateObjectDaoImpl<Client> clientDao;
    protected HibernateObjectDaoImpl<Chauffeur> chauffeurDao;
    protected HibernateObjectDaoImpl<Reservation> reservationDao;

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
    public static <T> T rechercherObject(int objectId, Class<T> entityClass) throws DAOException {
        HibernateObjectDaoImpl<T> hibernateDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(entityClass);

        return hibernateDao.read(objectId);
    }

    public static <T> void updateObject(T entity, Class<T> entityClass) throws DAOException {
        HibernateObjectDaoImpl<T> hibernateDao = ConcreteFactory
                .getFactory(HibernateFactory.class)
                .getHibernateObjectDaoImpl(entityClass);

        hibernateDao.update(entity);
    }

    public static void creerDemoDonnees() throws DAOException {

        var client1 = new Client("Dupont", "Jean", "jean.dupont@email.com","1 rue de Paris", "0600000000", "password");
        var client2 = new Client("Martin", "Alice", "alice.martin@email.com","2 avenue Lyon", "0611111111", "password");


        var vehicule1 = new Vehicule("Renault", "Clio", 300.0, Statut.DISPONIBLE, "AA-123-BB", null);

        var chauffeur1 = new Chauffeur("Sow", "Moussa", Statut.DISPONIBLE, null);

        var reservationAvecChauffeur = new Reservation(LocalDate.now(), LocalDate.now().plusDays(3),
                Statut.DISPONIBLE);


        reservationAvecChauffeur.addVehicule(vehicule1);
        reservationAvecChauffeur.addChauffeur(chauffeur1);
        client1.addReservation(reservationAvecChauffeur);

        ajouterObject(client1, Client.class);
        ajouterObject(client2, Client.class);
        ajouterObject(reservationAvecChauffeur, Reservation.class);

    }
}
