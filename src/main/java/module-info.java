/**
 *
 */
module com.fido.pharmacie {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;



    opens com.fido.pharmacie to javafx.fxml;
    exports com.fido.pharmacie;
    exports com.fido.pharmacie.controller;
    opens com.fido.pharmacie.controller to javafx.fxml;
}