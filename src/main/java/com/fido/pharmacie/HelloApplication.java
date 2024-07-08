/*package com.fido.pharmacie;

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




}*/

package com.fido.pharmacie;

import com.fido.pharmacie.controller.LoginPageController;
import com.fido.pharmacie.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

import static com.fido.pharmacie.controller.DatabaseConnection.showAlert;

public class HelloApplication extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        showLoginView();



    }

    public  void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("PHARMACIE FIDOKKPUI ");
        primaryStage.setScene(scene);



        // Chargez l'icône de l'application
        String absolutePath = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Plus.png").toUri().toString();
        primaryStage.getIcons().add(new Image(absolutePath));

        primaryStage.setMaximized(false);
        primaryStage.show();

        // Accès au contrôleur de la page de login
        LoginPageController loginController = fxmlLoader.getController();
        loginController.setMainApp(this);
    }

    public void showMainView() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/MainView.fxml"));
            Parent root = loader.load();

            // Obtenir la référence au contrôleur MainController
            MainController mainController = loader.getController();

            // Appeler une méthode du contrôleur pour mettre à jour les informations utilisateur
            //mainController.updateUserInfo(username);
            mainController.setPrimaryStage(primaryStage);

            // Passer l'instance de HelloApplication à MainController
            mainController.setMainApp(this);

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("PHARMACIE FIDOKPUI 1.0.0");
            stage.setScene(scene);

            // Chargez l'icône de l'application
            String absolutePath = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Plus.png").toUri().toString();
            stage.getIcons().add(new Image(absolutePath));


            // Afficher la nouvelle fenêtre
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la vue principale.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
