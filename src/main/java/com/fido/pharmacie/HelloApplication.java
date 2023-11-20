package com.fido.pharmacie;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load() );
        stage.setTitle("PHARMACIE FIDOKKPUI");
        stage.setScene(scene);

        // Chargez l'icône de l'application
        stage.getIcons().add(new Image("C:/Users/DELL/IdeaProjects/Pharmacie/src/main/resources/Image/Plus.png")); // Remplacez "icon.png" par le nom de votre fichier d'icône

        // Maximisez la fenêtre
       // stage.setMaximized(true);


        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}