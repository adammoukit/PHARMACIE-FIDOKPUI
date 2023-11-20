package com.fido.pharmacie.controller;

import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/pharmacie?autoReconnect=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Charger le pilote JDBC
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Établir la connexion à la base de données
                connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                System.out.println("Connexion à la base de données établie.");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur de Connexion", "Erreur lors de la connexion à la base de données. verifier la connexion ou le serveur");


                throw new RuntimeException("Erreur lors de la connexion à la base de données.");
            }
        }
        return connection;
    }

    // ICI ON VA AJOUTER LA METHODE showAlert()
    //    POUR AFFICHER UN DIALOGUE DES MESSAGE D'ERREUR AU CAS OU IL N'YA PAS DE CONNEXION

    static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);


        // Charger l'icône personnalisée
       /* Image icon = new Image("C:/Users/DELL/IdeaProjects/Pharmacie/src/main/resources/Image/Plus.png");
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(48); // Largeur de l'icône
        imageView.setFitHeight(48); // Hauteur de l'icône
        alert.setGraphic(imageView);
        */

        // Récupérer le stage de l'alerte et définir l'icône de la fenêtre
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("C:/Users/DELL/IdeaProjects/Pharmacie/src/main/resources/Image/Plus.png"));

        alert.showAndWait();
    }



    public static void updateQuantiteInDatabase(int produitId, int nouvelleQuantite) {
        String query = "UPDATE medicament SET quantite = ? WHERE ID = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, nouvelleQuantite);
            preparedStatement.setInt(2, produitId);

            preparedStatement.executeUpdate();

            System.out.println("Quantité mise à jour avec succès dans la base de données.");
        } catch (SQLException e) {
            e.printStackTrace();

            // Affichez une alerte ou gérez l'erreur d'une autre manière en cas d'échec de la mise à jour
            // Ajoutez ici le code pour gérer l'erreur d'une manière appropriée dans votre application
        }
    }


}
