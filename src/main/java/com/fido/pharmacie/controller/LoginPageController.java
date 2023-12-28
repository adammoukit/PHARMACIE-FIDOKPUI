package com.fido.pharmacie.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.fido.pharmacie.controller.DatabaseConnection.showAlert;

public class LoginPageController  implements Initializable {

    @FXML
    private Button ValiderBtn;

    @FXML
    private PasswordField mdpTextfield;

    @FXML
    private TextField nomUtilisateur;







    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = nomUtilisateur.getText();
        String password = mdpTextfield.getText();

        // Vérifier si les champs sont vides
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        // Authentification de l'utilisateur
        if (authenticateUser(username, password)) {

            // Authentification réussie, fermer la fenêtre de connexion et afficher la nouvelle vue
            closeLoginWindow();
            showMainView(username);
        } else {
            showAlert(Alert.AlertType.ERROR, "Échec de l'authentification", "Nom d'utilisateur ou mot de passe incorrect.");
        }
    }







    private boolean authenticateUser(String username, String password) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM utilisateurs WHERE username = ? AND mot_de_passe = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();

                // Si l'utilisateur existe dans la base de données, l'authentification est réussie
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Erreur lors de l'authentification.");
        }
        return false;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);



        alert.showAndWait();
    }


    private void closeLoginWindow() {
        // Obtenez la référence à la scène actuelle et fermez la fenêtre
        Stage stage = (Stage) ValiderBtn.getScene().getWindow();
        stage.close();
    }

    private void showMainView(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/MainView.fxml"));
            Parent root = loader.load();

            // Obtenir la référence au contrôleur MainController
            MainController mainController = loader.getController();

            // Appeler une méthode du contrôleur pour mettre à jour les informations utilisateur
            mainController.updateUserInfo(username);

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("PHARMACIE FIDOKPUI");
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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
