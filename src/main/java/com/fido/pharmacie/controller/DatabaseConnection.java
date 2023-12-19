package com.fido.pharmacie.controller;

import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
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



    //LA METHODE CI PERMET DE SAUVEGARDER LES DONNER DE LA BASE
    public static void backupDatabase() {
        // Créer un FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir l'emplacement de sauvegarde");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers SQL", "*.sql"));

        // Afficher la boîte de dialogue de sauvegarde et obtenir le fichier sélectionné
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            // Utiliser le chemin complet de mysqldump pour la sauvegarde
            String MYSQLDUMP_PATH = "C:/xampp/mysql/bin/mysqldump"; // Modifiez le chemin selon votre installation
            String dumpCommand = MYSQLDUMP_PATH + " -u " + USERNAME + " -p" + PASSWORD + " --add-drop-database -B " + DATABASE_NAME + " -r " + selectedFile.getAbsolutePath();

            try {
                ProcessBuilder processBuilder = new ProcessBuilder(dumpCommand.split(" "));
                processBuilder.start();
                System.out.println("Base de données sauvegardée avec succès à l'emplacement : " + selectedFile.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Erreur de Sauvegarde", "Erreur lors de la sauvegarde de la base de données.");

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur de Sauvegarde", "Erreur lors de la sauvegarde de la base de données.");
            }
        }
    }



}
