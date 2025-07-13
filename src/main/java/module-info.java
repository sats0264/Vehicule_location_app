module location.app.vehicule_location_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;

    exports location.app.vehicule_location_app.runtime;
    opens location.app.vehicule_location_app.runtime to javafx.fxml;
    exports location.app.vehicule_location_app.controllers;
    opens location.app.vehicule_location_app.controllers to javafx.fxml;

    exports location.app.vehicule_location_app;
    exports location.app.vehicule_location_app.jdbc;
    opens location.app.vehicule_location_app.jdbc to javafx.fxml;
}