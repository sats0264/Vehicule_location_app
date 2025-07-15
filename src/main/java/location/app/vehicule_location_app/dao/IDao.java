package location.app.vehicule_location_app.dao;

import location.app.vehicule_location_app.exceptions.DAOException;

import java.util.List;

public interface IDao <T>{

    public void create (T entity) throws DAOException;

    public T readById (int id) throws DAOException;

    public T readByString (String value) throws DAOException;

    public List<T> list() throws DAOException;

    public void update (T entity) throws DAOException;

    public void delete (int id) throws DAOException;
//    public void delete (int id, T type) throws DAOException;
}
