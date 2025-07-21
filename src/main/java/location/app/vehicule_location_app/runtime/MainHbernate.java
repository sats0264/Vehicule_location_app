package location.app.vehicule_location_app.runtime;

import location.app.vehicule_location_app.controllers.Controller;
import location.app.vehicule_location_app.exceptions.DAOException;

public class MainHbernate {
    public static void main(String[] args) {
        try {
            Controller.creerDemoDonnees();
            System.out.println("✔ Données de démonstration insérées avec succès !");
        } catch (DAOException e) {
            e.printStackTrace();
        } finally {}
    }
}
