package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import com.fido.pharmacie.model.MedicamentStock;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;



public class DashboardController implements Initializable {
    @FXML
    private PieChart monPieChart;

    @FXML
    private BarChart<String, Number> areaChartAchat;

    @FXML
    private Label nbrProduit;

    @FXML
    private Label nbrFournisseurs;


    @FXML
    private TextField valeurTotalStock;

    @FXML
    private TextField caissePrice1;

    @FXML
    private ImageView arrowDown;

    @FXML
    private ImageView arrowDown1;

    @FXML
    private ImageView arrowUp;

    @FXML
    private ImageView arrowUp1;

    @FXML
    private TextField arrowDownTextField;

    @FXML
    private TextField arrowDownTextField1;

    @FXML
    private TextField arrowUpTextField;


    @FXML
    private TextField arrowUpTextField1;






    Connection connectDB = DatabaseConnection.getConnection();


    ObservableList<MedicamentStock> MedicamentStockObservableList = FXCollections.observableArrayList();









    private int getNombreTotalProduits() {
        int nombreTotal = 0;
        String countQuery = "SELECT COUNT(*) AS total FROM produits";

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




    private double calculerValeurTotaleStock() {
        String query = "SELECT  p.prix, s.quantite " +
                "FROM produits p " +
                "JOIN stock s ON p.code_barres = s.code_barres " ;

        double totalPrice = 0.0;

        try (
                PreparedStatement stmt = connectDB.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                double prix = rs.getDouble("prix");
                int quantite = rs.getInt("quantite");
                totalPrice += prix * quantite;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérez l'exception comme vous le souhaitez (par exemple, afficher un message d'erreur)
        }

        return totalPrice;
    }




    private String formatCurrency(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
        return formatter.format(amount);
    }








    public void populateAreaChart() {
        String selectQuery = "SELECT date_achat, SUM(total) as total_vente " +
                "FROM vente " +
                "GROUP BY date_achat " +
                "ORDER BY date_achat ASC";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();

            // Utiliser une TreeMap pour stocker les totaux des ventes par date
            Map<String, Double> venteParDate = new TreeMap<>();

            while (resultSet.next()) {
                // Convertir la date SQL en format lisible
                Timestamp timestamp = resultSet.getTimestamp("date_achat");

                // Utiliser DateTimeFormatter pour formater la date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                String formattedDate = timestamp.toLocalDateTime().toLocalDate().format(formatter);

                // Récupérer la somme totale de vente pour la date actuelle
                double totalVente = resultSet.getDouble("total_vente");

                // Ajouter le total de vente à la Map
                venteParDate.merge(formattedDate, totalVente, Double::sum);
            }

            // Obtenir les 10 dernières entrées
            List<Map.Entry<String, Double>> last10Entries = venteParDate.entrySet()
                    .stream()
                    .skip(Math.max(0, venteParDate.size() - 13))
                    .collect(Collectors.toList());

            // Parcourir les 10 dernières entrées et ajouter les données à la série
            last10Entries.forEach(entry -> {
                String date = entry.getKey();
                Double total = entry.getValue();

                // Afficher la somme des ventes dans la console (peut être commenté ou retiré au besoin)
              //  System.out.println("Date: " + date + ", Total Vente: " + total);

                // Ajouter les données à la série
                series.getData().add(new XYChart.Data<>(date, total));
            });

            areaChartAchat.getData().clear();
            areaChartAchat.getData().add(series);


            // Ajouter un ChangeListener pour appliquer le style une fois que le nœud est créé
            series.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-stroke: green; -fx-fill: rgba(0, 255, 0, 0.5);");
                }
            });

            areaChartAchat.setCategoryGap(2); // Optionnel : pour supprimer l'espace entre les catégories
            areaChartAchat.setAnimated(true); // Désactiver l'animation si nécessaire
            areaChartAchat.getXAxis().setLabel("Date d'Achat");
            areaChartAchat.getYAxis().setLabel("Total Vente");


            // Appliquer la rotation aux étiquettes de l'axe des abscisses
            rotateXAxisLabels((CategoryAxis) areaChartAchat.getXAxis());


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void rotateXAxisLabels(CategoryAxis xAxis) {
        xAxis.setTickLabelRotation(50); // Rotation des étiquettes de 45 degrés
    }



    private void populatePieChart() {
        // Classer les médicaments en catégories
        int stockCategorie1 = 5;
        int stockCategorie2 = 10;

        int countCategorie1 = 0;
        int countCategorie2 = 0;
        int countPerimes = 0;
        int countAutres = 0;

        for (MedicamentStock medicament : MedicamentStockObservableList) {
            int quantite = medicament.getQuantite();
            Date dateExpiration = medicament.getDateExpiration();

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


        caissePrice1.setStyle("-fx-text-fill: green;");





        String medicamenViewQuery = "SELECT p.code_barres, p.nom_produit, p.dosage, p.description, p.prix, p.categorie_produit, p.fournisseur,  p.instructions_utilisation" +
                ", s.numero_lot, s.date_expiration, s.date_reception, s.quantite  " +
                "FROM Produits p " +
                "JOIN Stocks s ON p.code_barres = s.code_barres";


        try {
            Statement statement = connectDB.createStatement();
            ResultSet Queryoutput = statement.executeQuery(medicamenViewQuery);

            while (Queryoutput.next()){
                String codeBarre = Queryoutput.getString("code_barres");
                String nomProduit = Queryoutput.getString("nom_produit");
                String dosage = Queryoutput.getString("dosage");
                String description = Queryoutput.getString("description");
                String fournisseur = Queryoutput.getString("fournisseur");
                double prixUnitaire = Queryoutput.getDouble("prix");
                String categorie = Queryoutput.getString("categorie_produit");

                String instructions = Queryoutput.getString("instructions_utilisation");
                int quantite = Queryoutput.getInt("quantite");

                MedicamentStock produit = new MedicamentStock(codeBarre, nomProduit, dosage, description, prixUnitaire, categorie, quantite, fournisseur, instructions);
                MedicamentStockObservableList.add(produit);


            }



            //INITIALISER LES VALEURS DANS AreaChart AU DEMARRAGE
            populateAreaChart();


            // INITIALISER LES VALEURS DANS PieChart AU DEMARRAGE
            populatePieChart();



            System.out.println("MedicamentStockObservableList size: " + MedicamentStockObservableList.size());


        }catch (SQLException e){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
}
