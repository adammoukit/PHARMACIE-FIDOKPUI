package com.fido.pharmacie.controller;

import com.fido.pharmacie.HelloApplication;
import com.fido.pharmacie.controller.Journal.Journal;
import com.fido.pharmacie.model.Role;
import com.fido.pharmacie.model.User;
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


    private HelloApplication mainApp;

    public void setMainApp(HelloApplication mainApp) {
        this.mainApp = mainApp;
    }

    private static User currentUser;





    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        String username = nomUtilisateur.getText();
        String password = mdpTextfield.getText();

        // Vérifier si les champs sont vides
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        // Authentification de l'utilisateur
        User authenticatedUser = authenticate(username, password);
        if (authenticatedUser != null) {

            showAlert(Alert.AlertType.INFORMATION, "REUSSI", "CONNEXON REUSSI.");
            // Authentification réussie, afficher le rôle de l'utilisateur dans la console
            String role = authenticatedUser.getRole().getName();
            System.out.println("Rôle de l'utilisateur : " +role );

            // Enregistrer les données de connexion dans le journal
            Journal.log(username,  role, "Connexion réussie");

            // Authentification réussie, fermer la fenêtre de connexion et afficher la nouvelle vue
            closeLoginWindow();
            mainApp.showMainView();
        } else {
            showAlert(Alert.AlertType.ERROR, "Échec de l'authentification", "Nom d'utilisateur ou mot de passe incorrect.");
        }
    }





    public static User getCurrentUser() {
        return currentUser;
    }



    public static User authenticate(String username, String password) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT u.id, u.username, u.password, r.id as role_id, r.name as role_name FROM utilisateur u JOIN roles r ON u.role_id = r.id WHERE u.username = ? AND u.password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {

                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        Role role = new Role();
                        role.setId(rs.getInt("role_id"));
                        role.setName(rs.getString("role_name"));
                        user.setRole(role);
                        currentUser = user; // Stocker l'utilisateur actuellement connecté
                        return user;

                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Erreur lors de l'authentification.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Erreur lors de l'authentification.");
        }
        return null;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String content) {
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
            //mainController.setPrimaryStage(primaryStage);
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
