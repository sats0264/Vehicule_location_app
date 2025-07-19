package location.app.vehicule_location_app.runtime;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args){
        launch(args);
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
