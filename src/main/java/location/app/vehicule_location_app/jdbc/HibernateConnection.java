//package location.app.vehicule_location_app.jdbc;
//
//
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.boot.Metadata;
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//
//public class HibernateConnection {
//    private static final HibernateConnection instance = new HibernateConnection();
//    private final Session session;
//    private final SessionFactory factory;
//
//    private HibernateConnection() {
//        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
//                .configure("hibernate.cfg.xml")
//                .build();
//
//        Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();
//
//        // Creating Hibernate Session Factory Instance
//        factory = meta.getSessionFactoryBuilder().build();
//        // Creating The Hibernate's Session Object
//        session = factory.openSession();
//
//    }
//
//    public Session getSession() {
//        if (session == null) {
//            new HibernateConnection();
//        }
//        return session;
//    }
//
//    public static HibernateConnection getInstance() {
//        return instance;
//    }
//}
package location.app.vehicule_location_app.jdbc;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConnection {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Erreur d'initialisation de Hibernate : " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
