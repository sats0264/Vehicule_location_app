package location.app.vehicule_location_app.observer;

public class ChauffeurSubject extends Subject {

    private static ChauffeurSubject instance;

    private ChauffeurSubject() {}

    public static ChauffeurSubject getInstance() {
        if (instance == null) {
            instance = new ChauffeurSubject();
        }
        return instance;
    }
}
