package location.app.vehicule_location_app.observer;

public class VehiculeSubject extends Subject{

    private static VehiculeSubject instance;

    private VehiculeSubject() {}

    public static VehiculeSubject getInstance() {
        if (instance == null) {
            instance = new VehiculeSubject();
        }
        return instance;
    }
}
