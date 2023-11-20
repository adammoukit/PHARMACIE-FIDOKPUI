package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;

import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.fido.pharmacie.controller.DatabaseConnection.connection;

public class DashboardController implements Initializable {
    @FXML
    private BarChart<String, Integer> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private LineChart<String, Number> LineChartAchat;


    ObservableList<MedicamentSearch> MedicamentSearchObservableList = FXCollections.observableArrayList();



    private void initialiserBarChart() {
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        for (MedicamentSearch medicament : MedicamentSearchObservableList) {
            series.getData().add(new XYChart.Data<>(medicament.getNom_medicament(), medicament.getQuantite()));
        }

        // Utilisez un NumberAxis pour l'axe Y
        yAxis.setAutoRanging(false); // Désactivez l'ajustement automatique des valeurs de l'axe Y
        yAxis.setLowerBound(0); // Définissez la valeur minimale de l'axe Y
        yAxis.setUpperBound(getMaxQuantite()); // Définissez la valeur maximale de l'axe Y (vous pouvez implémenter votre propre logique pour cela)

        barChart.getData().add(series);
    }

    // Méthode pour obtenir la valeur maximale de quantité dans la liste
    private double getMaxQuantite() {
        double max = 600;
        for (MedicamentSearch medicament : MedicamentSearchObservableList) {
            if (medicament.getQuantite() > max) {
                max = medicament.getQuantite();
            }
        }
        return max;
    }



    public void populateLineChart() {
            String selectQuery = "SELECT date_achat, total FROM achat";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                while (resultSet.next()) {
                    // Convertir la date SQL en format lisible
                    Timestamp timestamp = resultSet.getTimestamp("date_achat");
                    String formattedDate = timestamp.toLocalDateTime().toLocalDate().toString();

                    // Ajouter les données à la série
                    series.getData().add(new XYChart.Data<>(formattedDate, resultSet.getDouble("total")));
                }

                LineChartAchat.getData().add(series);

                // Configurer les axes
                LineChartAchat.getXAxis().setLabel("Date d'Achat");
                LineChartAchat.getYAxis().setLabel("Total");

            } catch (SQLException e) {
                e.printStackTrace();
            }

    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        Connection connectDB = DatabaseConnection.getConnection();

        String medicamenViewQuery = "SELECT ID, NOM_MEDICAMENT, DESCRIPTION, DOSAGE, PRIX, DATE_EXPIRATION, QUANTITE, id_fournisseur FROM medicament ";


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
                Integer queryIdFournisseur = Queryoutput.getInt("id_fournisseur");


                //remplir la liste observable

                MedicamentSearchObservableList.add(new MedicamentSearch(queryIdMedicament, queryNomMedicament, queryDescription, queryDosage, queryPrix, queryDateExpiration, queryQuantite, queryIdFournisseur));



            }

            //INITIALISER LES VALEURS DANS BartChart AU DEMARRAGE
            initialiserBarChart();


            //INITIALISER LES VALEURS DANS LineChart AU DEMARRAGE
            populateLineChart();

        }catch (SQLException e){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
}
