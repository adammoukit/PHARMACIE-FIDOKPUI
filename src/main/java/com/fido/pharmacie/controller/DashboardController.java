package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;



public class DashboardController implements Initializable {
    @FXML
    private PieChart monPieChart;

    @FXML
    private BarChart<String, Number> areaChartAchat;

    @FXML
    private Label nbrProduit;

    @FXML
    private Label nbrFournisseurs;


    Connection connectDB = DatabaseConnection.getConnection();


    ObservableList<MedicamentSearch> MedicamentSearchObservableList = FXCollections.observableArrayList();





    // Méthode pour obtenir la valeur maximale de quantité dans la liste



    private int getNombreTotalProduits() {
        int nombreTotal = 0;
        String countQuery = "SELECT COUNT(*) AS total FROM medicament";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(countQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                nombreTotal = resultSet.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombreTotal;
    }

    private int getNombreTotalFournisseurs() {
        int nombreTotal = 0;
        String countQuery = "SELECT COUNT(*) AS total FROM fournisseurs";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(countQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                nombreTotal = resultSet.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombreTotal;
    }




//String selectQuery = "SELECT date_achat, total FROM achat ORDER BY date_achat DESC LIMIT 20";
    // Limite les résultats aux 20 dernières entrées, par exemple.

    public void populateAreaChart() {
        String selectQuery = "SELECT date_achat, SUM(total) as total_vente " +
                "FROM achat " +
                "GROUP BY date_achat " +
                "ORDER BY date_achat ASC";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();

            // Utiliser une Map pour stocker les totaux des ventes par date
            //Map<String, Double> venteParDate = new HashMap<>();

            // Utiliser une TreeMap au lieu d'une HashMap pour trier les clés (dates)
            Map<String, Double> venteParDate = new TreeMap<>();


            while (resultSet.next()) {
                // Convertir la date SQL en format lisible
                Timestamp timestamp = resultSet.getTimestamp("date_achat");

                // Utiliser DateTimeFormatter pour formater la date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = timestamp.toLocalDateTime().toLocalDate().format(formatter);



                // Récupérer la somme totale de vente pour la date actuelle
                double totalVente = resultSet.getDouble("total_vente");

                // Ajouter le total de vente à la Map
                venteParDate.merge(formattedDate, totalVente, Double::sum);
            }

            // Parcourir la Map et ajouter les données à la série
            venteParDate.forEach((date, total) -> {
                // Afficher la somme des ventes dans la console (peut être commenté ou retiré au besoin)
                System.out.println("Date: " + date + ", Total Vente: " + total);

                // Ajouter les données à la série
                series.getData().add(new XYChart.Data<>(date, total));
            });

            areaChartAchat.getData().clear();
            areaChartAchat.getData().add(series);


            areaChartAchat.setCategoryGap(0); // Optionnel : pour supprimer l'espace entre les catégories
            areaChartAchat.setAnimated(false); // Désactiver l'animation si nécessaire
            areaChartAchat.getXAxis().setLabel("Date d'Achat");
            areaChartAchat.getYAxis().setLabel("Total Vente");
            //areaChartAchat.setBarGap(0); // Optionnel : pour supprimer l'espace entre les barres


            // Configurer les axes
            areaChartAchat.getXAxis().setLabel("Date d'Achat");
            areaChartAchat.getYAxis().setLabel("Total Vente");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private void populatePieChart() {
        // Classer les médicaments en catégories
        int stockCategorie1 = 5;
        int stockCategorie2 = 10;

        int countCategorie1 = 0;
        int countCategorie2 = 0;
        int countPerimes = 0;
        int countAutres = 0;

        for (MedicamentSearch medicament : MedicamentSearchObservableList) {
            int quantite = medicament.getQuantite();
            Date dateExpiration = medicament.getDate_expiration();

            // Vérifier si le produit est périmé
            if (dateExpiration != null && dateExpiration.before(new Date())) {
                countPerimes++;
            } else if (quantite <= stockCategorie1) {
                countCategorie1++;
            } else if (quantite <= stockCategorie2) {
                countCategorie2++;
            } else {
                countAutres++;
            }
        }

        // Créer les sections du PieChart
        PieChart.Data categorie1 = new PieChart.Data("Stock <= " + stockCategorie1, countCategorie1);
        PieChart.Data categorie2 = new PieChart.Data("Stock <= " + stockCategorie2, countCategorie2);
        PieChart.Data perimes = new PieChart.Data("Produits périmés", countPerimes);
        PieChart.Data autres = new PieChart.Data("Autres", countAutres);

        // Ajouter les données au PieChart
        monPieChart.getData().addAll(categorie1, categorie2, perimes, autres);

        // Configurer le titre du PieChart
        monPieChart.setTitle("Répartition du stock par catégorie");
    }







    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Obtenir le nombre total de produits
        int nombreTotalProduits = getNombreTotalProduits();

        // Afficher le nombre total de produits dans le label
        nbrProduit.setText( String.valueOf(nombreTotalProduits));

        // Obtenir le nombre total de fournisseurs
        int nombreTotalFournisseurs = getNombreTotalFournisseurs();

        // Afficher le nombre total de fournisseurs dans le label
        nbrFournisseurs.setText( String.valueOf(nombreTotalFournisseurs));





        String medicamenViewQuery = "SELECT ID, NOM_MEDICAMENT, DESCRIPTION, DOSAGE, PRIX, DATE_EXPIRATION, QUANTITE FROM medicament ";


        try {
            Statement statement = connectDB.createStatement();
            ResultSet Queryoutput = statement.executeQuery(medicamenViewQuery);

            while (Queryoutput.next()){
                Integer queryIdMedicament = Queryoutput.getInt("ID");
                String  queryNomMedicament = Queryoutput.getString("NOM_MEDICAMENT");
                String  queryDescription = Queryoutput.getString("DESCRIPTION");
                String  queryDosage = Queryoutput.getString("DOSAGE");
                Double  queryPrix = Queryoutput.getDouble("PRIX");
                Date queryDateExpiration = Queryoutput.getDate("DATE_EXPIRATION");
                Integer queryQuantite = Queryoutput.getInt("QUANTITE");


                //remplir la liste observable

                MedicamentSearchObservableList.add(new MedicamentSearch(queryIdMedicament, queryNomMedicament, queryDescription, queryDosage, queryPrix, queryDateExpiration, queryQuantite));



            }



            //INITIALISER LES VALEURS DANS AreaChart AU DEMARRAGE
            populateAreaChart();


            // INITIALISER LES VALEURS DANS PieChart AU DEMARRAGE
            populatePieChart();

        }catch (SQLException e){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
}
