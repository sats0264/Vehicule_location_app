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
    requires java.desktop;

    exports location.app.vehicule_location_app.runtime;
    opens location.app.vehicule_location_app.runtime to javafx.fxml;
    exports location.app.vehicule_location_app.controllers;
    opens location.app.vehicule_location_app.controllers to javafx.fxml;
    exports location.app.vehicule_location_app.models;
    opens location.app.vehicule_location_app.models to javafx.fxml;
    exports location.app.vehicule_location_app.dao;
    opens location.app.vehicule_location_app.dao to javafx.fxml;
    exports location.app.vehicule_location_app.factory;
    opens location.app.vehicule_location_app.factory to javafx.fxml;
    exports location.app.vehicule_location_app.observer;
    opens location.app.vehicule_location_app.observer to javafx.fxml;
    exports location.app.vehicule_location_app.exceptions;
    opens location.app.vehicule_location_app.exceptions to javafx.fxml;
    exports location.app.vehicule_location_app.jdbc;
    opens location.app.vehicule_location_app.jdbc to javafx.fxml;
}