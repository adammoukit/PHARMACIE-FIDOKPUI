/**
 *
 */
module com.fido.pharmacie {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;


    opens com.fido.pharmacie to javafx.fxml;
    exports com.fido.pharmacie;
    exports com.fido.pharmacie.controller;
    opens com.fido.pharmacie.controller to javafx.fxml;
    exports com.fido.pharmacie.model;
    opens com.fido.pharmacie.model to javafx.fxml;
}