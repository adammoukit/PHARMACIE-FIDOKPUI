package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.Achat;
import com.fido.pharmacie.model.DataPoint;
import com.fido.pharmacie.model.DetailVente;
import com.fido.pharmacie.model.MedicamentSearch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

// Importez les classes nécessaires
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;




public class RapportVenteController implements Initializable {

    @FXML
    private TableColumn<Achat, Hyperlink> actionColumn;


    @FXML
    private ComboBox<String> comboBoxPeriode;
    @FXML
    private TableColumn<Achat, Integer> id_venteColumn;

    @FXML
    private TableColumn<Achat, Timestamp> dateAchatColumn;

    @FXML
    private TableColumn<Achat, String> codeRecuColumn;

    @FXML
    private TableColumn<Achat, Double> totalColumn;

    @FXML
    private TableView<Achat> tableVente;


    @FXML
    private AnchorPane vueLineChart;

    private LineChart<String, Number> lineChart; // Change the type for Y to Number




    ObservableList<Achat> AchatObservableList = FXCollections.observableArrayList();

    Connection connectDB = DatabaseConnection.getConnection();


    private void afficherPrixVente(int venteId, double prix) {
        // Créer une boîte de dialogue d'information
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la vente");
        alert.setHeaderText("ID de vente: " + venteId);
        alert.setContentText("Prix de la vente: " + prix + " FCFA");

        // Afficher la boîte de dialogue
        alert.showAndWait();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // Set the font for the TableView, Application des styles CSS pout la couleur de la tableview
        tableVente.setStyle("-fx-font-family: 'Courier New'; -fx-base: rgb(158, 152, 69);");


        // Créer une liste observable pour stocker les valeurs
       /* ObservableList<String> periodes = FXCollections.observableArrayList("Quotidien", "Hebdomadaire", "Mensuel");

        // Ajouter la liste observable à la ComboBox
        comboBoxPeriode.setItems(periodes);

        // Définir une valeur par défaut (si nécessaire)
        comboBoxPeriode.setValue("Quotidien");

        */


        // Initialize the lineChart
        CategoryAxis xAxis = new CategoryAxis(); // Change this line
        NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis); // Keep this line





        String combinedQuery = "SELECT ID, DATE_ACHAT, CODE_RECU, TOTAL FROM achat ORDER BY DATE_ACHAT";

        try {
            Statement statement = connectDB.createStatement();


            ResultSet queryOutput = statement.executeQuery(combinedQuery);

            // Clear existing data in the LineChart
            lineChart.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>(); // Change the type for Y to Number


            series.setName("Flux des ventes");

            // Create SimpleDateFormat with your desired date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


            while (queryOutput.next()) {
                Integer queryIdVente = queryOutput.getInt("ID");
                java.sql.Timestamp queryDateAchat = queryOutput.getTimestamp("DATE_ACHAT");
                String queryCodeRecu = queryOutput.getString("CODE_RECU");
                Double queryTotal = queryOutput.getDouble("TOTAL");

                // Ajouter les données d'achat à la liste observable
                AchatObservableList.add(new Achat(queryIdVente, queryDateAchat, queryCodeRecu, queryTotal));


                // Ajouter les données au LineChart
                // Use the SimpleDateFormat to format the date
                String formattedDate = dateFormat.format(queryDateAchat);

                // Create a DataPoint and add it to the series
                DataPoint dataPoint = new DataPoint(queryIdVente, queryTotal);
                series.getData().add(new XYChart.Data<>(formattedDate, queryTotal, dataPoint));

            }



            id_venteColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            dateAchatColumn.setCellValueFactory(new PropertyValueFactory<>("dateAchat"));
            codeRecuColumn.setCellValueFactory(new PropertyValueFactory<>("codeRecu"));
            totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));


            // Ajouter une colonne "Action" avec des liens "Voir Plus"
            // Dans votre méthode initialize
            actionColumn.setCellFactory(new Callback<TableColumn<Achat, Hyperlink>, TableCell<Achat, Hyperlink>>() {
                @Override
                public TableCell<Achat, Hyperlink> call(TableColumn<Achat, Hyperlink> param) {
                    return new TableCell<Achat, Hyperlink>() {
                        final Hyperlink voirPlusLink = new Hyperlink("Voir Details");

                        {
                            voirPlusLink.setOnAction(event -> {
                                Achat achat = getTableView().getItems().get(getIndex());
                                System.out.println("Voir Plus pour l'achat ID : " + achat.getId());
                                // Ajoutez ici la logique pour afficher plus d'informations sur l'achat

                                // Charger la vue DetailsVente.fxml
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/DetailsVente.fxml"));
                                Parent root;
                                try {
                                    root = loader.load();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return;
                                }

                                // Récupérer le contrôleur associé à la vue DetailsVente.fxml
                                DetailsVenteController detailsController = loader.getController();

                                // Initialiser les données de la vente dans le contrôleur
                                detailsController.initializeData(achat.getId());

                                // Afficher la nouvelle vue dans une nouvelle fenêtre
                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.show();


                            });
                        }

                        @Override
                        protected void updateItem(Hyperlink item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(voirPlusLink);
                            }
                        }
                    };
                }
            });


            // Ajouter la liste observable à la TableView
            tableVente.setItems(AchatObservableList);

            totalColumn.setCellFactory(col -> new TableCell<Achat, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Display the price with the symbol "FCFA"
                        setText(String.format("%.2f", item) + " FCFA");
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: green; -fx-font-size: 15; -fx-font-weight: bold;"); // Centrer le texte
                    }
                }
            });



            // Nettoyer les enfants du AnchorPane avant d'ajouter le LineChart
            vueLineChart.getChildren().clear();

            lineChart.getData().add(series);
            lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
            lineChart.setLegendVisible(true);
            lineChart.setCreateSymbols(true);
            lineChart.setHorizontalGridLinesVisible(true);
            lineChart.setVerticalGridLinesVisible(true);
            lineChart.setAnimated(true);
            lineChart.setHorizontalZeroLineVisible(true);
            lineChart.setCreateSymbols(true);

            // Ajouter les propriétés de redimensionnement pour que le LineChart prenne les dimensions du parent
            lineChart.prefWidthProperty().bind(vueLineChart.widthProperty());
            lineChart.prefHeightProperty().bind(vueLineChart.heightProperty());


            //cette qui fais afficher l'alert qui affiche l'id et le prix de la vente au clic sur les points dans le lineChart
            lineChart.getData().stream().forEach(venteSeries -> {
                for (XYChart.Data<String, Number> data : venteSeries.getData()) {
                    Node node = data.getNode();
                    node.setOnMouseClicked(event -> {
                        DataPoint dataPoint = (DataPoint) data.getExtraValue();
                        afficherPrixVente(dataPoint.getVenteId(), dataPoint.getPrix());
                    });
                }
            });




            vueLineChart.getChildren().add(lineChart);








        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
