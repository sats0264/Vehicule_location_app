package location.app.vehicule_location_app.observer;

public class DashboardSubject extends Subject {
    private static DashboardSubject instance;

    private DashboardSubject() {}

    public static DashboardSubject getInstance() {
        if (instance == null) {
            instance = new DashboardSubject();
        }
        return instance;
    }
}
