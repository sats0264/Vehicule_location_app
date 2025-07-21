package location.app.vehicule_location_app.factory;

public class ConcreteFactory {

    public static <T> T getFactory(Class<T> factoryClass) {

        AbstractFactory factory = new HibernateFactoryImpl();

        if(factoryClass == HibernateFactory.class){
            return factoryClass.cast(factory.getHibernateFactory());
        }

        throw new IllegalArgumentException("Unknown factory class: " + factoryClass.getName());

    }
}
