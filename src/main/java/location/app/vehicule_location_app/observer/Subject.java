package location.app.vehicule_location_app.observer;

import java.util.ArrayList;

public class Subject{

    private static Subject instance;

    protected Subject() {}

    public static Subject getInstance() {
        if (instance == null) {
            instance = new Subject();
        }
        return instance;
    }

    private ArrayList<Observer> observers = new ArrayList<Observer>();
    public void attach(Observer observer) {
        observers.add(observer);
    }
    public void notifyAllObservers(){
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
