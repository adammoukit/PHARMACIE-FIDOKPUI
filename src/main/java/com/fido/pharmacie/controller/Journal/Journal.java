package com.fido.pharmacie.controller.Journal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Journal {
    private static final String LOG_FILE_PATH = "journal.txt";

    public static void log(String utilisateur, String role, String action) {
        String message = String.format("%s - Utilisateur: %s, Role: %s, Action: %s%n", LocalDateTime.now(), utilisateur, role, action);
        writeToLogFile(message);
    }

    private static void writeToLogFile(String message) {
        FileWriter writer = null;
        try {
            // Vérifier si le fichier journal existe
            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.exists()) {
                // Si le fichier journal n'existe pas, le créer
                logFile.createNewFile();
            }
            // Écrire les données de connexion dans le fichier journal
            writer = new FileWriter(LOG_FILE_PATH, true);
            writer.write(message);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture dans le fichier de journal : " + e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
