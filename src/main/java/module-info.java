/**
 *
 */
module com.fido.pharmacie {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires kernel;
    requires layout;
    requires barcode4j;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.compress;


    opens com.fido.pharmacie to javafx.fxml;
    exports com.fido.pharmacie;
    exports com.fido.pharmacie.controller;
    opens com.fido.pharmacie.controller to javafx.fxml;
    exports com.fido.pharmacie.model;
    opens com.fido.pharmacie.model to javafx.fxml;


}