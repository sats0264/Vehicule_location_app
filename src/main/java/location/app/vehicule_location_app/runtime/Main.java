package location.app.vehicule_location_app.runtime;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import location.app.vehicule_location_app.controllers.Controller;
import location.app.vehicule_location_app.exceptions.DAOException;
import location.app.vehicule_location_app.jdbc.HibernateConnection;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args){
        launch(args);
        try {
            Controller.creerDemoDonnees();
            System.out.println("✔ Données de démonstration insérées avec succès !");
        } catch (DAOException e) {
            e.printStackTrace();
        } finally {
            // Ferme proprement les ressources Hibernate
//            HibernateConnection.shutdown();
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/UIlogin.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Connexion - Application de Location de Véhicules");
        stage.setScene(scene);
        stage.setResizable(false); // Empêche le redimensionnement pour une interface de connexion fixe
        stage.show();
    }
}
