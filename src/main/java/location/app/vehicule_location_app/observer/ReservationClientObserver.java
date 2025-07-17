//package location.app.vehicule_location_app.observer;
//
//import location.app.vehicule_location_app.controllers.UIFenetreReservationController;
//
//public class ReservationClientObserver extends Observer {
//
////    private UIFenetreReservationController controller;
////
////    public ReservationClientObserver(UIFenetreReservationController controller) {
////        this.subject = subject;
////        this.controller = controller;
////        this.subject.attach(this);
////    }
////
////    @Override
////    public void update() {
////        int state = subject.getState();
////        // 0 = En attente, 1 = Validé, 2 = Rejeté
////        switch (state) {
////            case 1:
////                controller.updateStatut("Validé", "#43A047");
////                break;
////            case 2:
////                controller.updateStatut("Rejeté", "#e53935");
////                break;
////            default:
////                controller.updateStatut("En attente", "#FFA000");
////        }
////    }
//
//}
