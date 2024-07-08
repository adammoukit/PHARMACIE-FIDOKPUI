package com.fido.pharmacie.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
   // private static final String JDBC_URL = "jdbc:mysql://localhost:3306/pharmacie?autoReconnect=true";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/pharmacie?autoReconnect=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static final String DATABASE_NAME = "pharmacie";

    static Connection connection;

    private static final String MYSQLDUMP_PATH = "C:/xampp/mysql/bin/mysqldump";


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

    public static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

// Chargement de l'icône depuis le chemin spécifié
        Image image = new Image("file:src/main/java/com/fido/pharmacie/controller/Image/Plus.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50); // Taille de l'icône, ajustez selon vos besoins
        imageView.setFitHeight(50);
        alert.setGraphic(imageView);

        alert.showAndWait();
    }








}
