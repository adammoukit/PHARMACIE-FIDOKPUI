package com.fido.pharmacie;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load() );
        stage.setTitle("PHARMACIE FIDOKKPUI");
        stage.setScene(scene);

        // Chargez l'icône de l'application
        String absolutePath = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Plus.png").toUri().toString();
        stage.getIcons().add(new Image(absolutePath));

        // Maximisez la fenêtre
       stage.setMaximized(false);


        stage.show();
    }

    public static void main(String[] args) {
        launch();


    }




}