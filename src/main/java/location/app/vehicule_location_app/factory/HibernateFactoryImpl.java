package location.app.vehicule_location_app.factory;

import location.app.vehicule_location_app.dao.HibernateObjectDaoImpl;
import location.app.vehicule_location_app.exceptions.DAOException;

public class HibernateFactoryImpl extends AbstractFactory {

    @Override
    public HibernateFactory getHibernateFactory() {
        return new HibernateFactory(){
            @Override
            public <T> HibernateObjectDaoImpl<T> getHibernateObjectDaoImpl(Class<T> entityClass) throws DAOException {
                try {
                    return new HibernateObjectDaoImpl<>(entityClass);
                } catch (Exception e) {
                    throw new DAOException("Error creating HibernateObjectDaoImpl for class: " + entityClass.getName() + ": " + e.getMessage());
                }
            }
        };
    }
}
