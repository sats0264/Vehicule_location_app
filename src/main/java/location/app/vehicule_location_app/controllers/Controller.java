package location.app.vehicule_location_app.controllers;

import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.factory.ConcreteFactory;
import location.app.vehicule_location_app.factory.HibernateFactory;
import location.app.vehicule_location_app.models.*;
import java.time.LocalDate;
import java.util.List;

public class Controller {

    protected HibernateObjectDaoImpl<Vehicule> vehiculeDao;
    protected HibernateObjectDaoImpl<Client> clientDao;
    protected HibernateObjectDaoImpl<Chauffeur> chauffeurDao;
    protected HibernateObjectDaoImpl<Reservation> reservationDao;
    protected HibernateObjectDaoImpl<Facture> factureDao;
    protected HibernateObjectDaoImpl<Utilisateur> utilisateurDao;
    protected static List<Vehicule> controllerVehiculeList;
    protected static List<Chauffeur> controllerChauffeurList;
    protected static List<Client> controllerClientList;
    protected static List<Reservation> controllerReservationList;

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

    public static void creerDemoDonnees() throws DAOException {

//        var client1 = new Client("Dupont", "Jean", "jean.dupont@email.com","1 rue de Paris", "0600000000", "password");
//        var client2 = new Client("Sarr", "Alice", "alice.martin@email.com","2 avenue Lyon", "0611111111", "password");
//        var client3 = new Client("Diallo", "Mamadou", "dio@gmail.com","3 boulevard Marseille", "0622222222", "password");
        var client4 = new Client("Traore", "Aissatou", "ad@dgfgf.com","4 place Toulouse", "0633333333", "password");


        var vehicule1 = new Vehicule("AA-123-BB","Renault", "Clio", 300.0, null);
        var vehicule2 = new Vehicule("CC-456-DD","Peugeot", "208", 350.0, null);
        var vehicule3 = new Vehicule("EE-789-FF", "Citroen", "C3", 320.0, null);
        var vehicule4 = new Vehicule("GG-101-HH", "Toyota", "Yaris", 4000.0, null);

//        var chauffeur1 = new Chauffeur("Sow", "Moussa", Statut.DISPONIBLE, null);
//        var chauffeur2 = new Chauffeur("Diallo", "Fatou", Statut.DISPONIBLE, null);
//        var chauffeur3 = new Chauffeur("Traore", "Aissatou", Statut.DISPONIBLE, null);



//        var reservationAvecChauffeur = new Reservation(LocalDate.now(), LocalDate.now().plusDays(3),
//                StatutReservation.EN_ATTENTE);
//
//        reservationAvecChauffeur.addVehicule(vehicule1);
//        reservationAvecChauffeur.addVehicule(vehicule2);
//        reservationAvecChauffeur.addChauffeur(chauffeur1);
//        reservationAvecChauffeur.addChauffeur(chauffeur2);
//
//        var reservationAvecChauffeur2 = new Reservation(LocalDate.now(), LocalDate.now().plusDays(15),
//                StatutReservation.EN_ATTENTE);
//        reservationAvecChauffeur2.addVehicule(vehicule4);
//        reservationAvecChauffeur2.addChauffeur(chauffeur3);
//
//        var reservationSansChauffeur = new Reservation(LocalDate.now(), LocalDate.now().plusDays(2),
//                StatutReservation.EN_ATTENTE);
//        reservationSansChauffeur.addVehicule(vehicule3);

        var reservationSansChauffeur = new Reservation(LocalDate.now(), LocalDate.now().plusDays(6),
                StatutReservation.EN_ATTENTE);
        reservationSansChauffeur.addVehicule(vehicule4);
        reservationSansChauffeur.addVehicule(vehicule3);

        var factureSansChauffeur = new Facture(reservationSansChauffeur);

        client4.addReservation(reservationSansChauffeur);
//
//        var facture1 = new Facture(reservationAvecChauffeur);
//        var facture2 = new Facture(reservationSansChauffeur);
//        var facture3 = new Facture(reservationAvecChauffeur2);
//
//        client1.addReservation(reservationAvecChauffeur);
//        client3.addReservation(reservationAvecChauffeur2);


//        ajouterObject(client1, Client.class);
//        ajouterObject(client2, Client.class);
//        ajouterObject(client3, Client.class);
        ajouterObject(client4, Client.class);
//        ajouterObject(facture1, Facture.class);
//        ajouterObject(reservationAvecChauffeur, Reservation.class);

    }
}
