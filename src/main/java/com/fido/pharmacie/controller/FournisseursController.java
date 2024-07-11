package com.fido.pharmacie.controller;


import com.fido.pharmacie.model.MedicamentStock;
import com.fido.pharmacie.model.ProduitFournisseur;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FournisseursController implements Initializable {


    @FXML
    private TableColumn<ProduitFournisseur, String> libelleColumn;

    @FXML
    private TableColumn<ProduitFournisseur, Double> prix_cession_Column;

    @FXML
    private TableColumn<ProduitFournisseur, Double> prix_public_column;

    @FXML
    private TableColumn<ProduitFournisseur, Long> produitColumn;


    @FXML
    private TableView<ProduitFournisseur> tableProduitUbipharm;

    @FXML
    private TextField rechercheTextfield;

    @FXML
    private Label nbr_produit_label;

    @FXML
    private Button btnSupprimer;


    private ObservableList<ProduitFournisseur> produits = FXCollections.observableArrayList();


    private void updateClearButtonVisibility(String text) {
        btnSupprimer.setVisible(!text.isEmpty());
    }

    private void rechercherProduit(){
        // Créez une liste filtrée liée à la liste observable des médicaments
        FilteredList<ProduitFournisseur> filteredList = new FilteredList<>(produits, p -> true);

        // Liez le predicat du FilteredList à la propriété text du TextField
        rechercheTextfield.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(medicament -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true; // Affichez tous les éléments si le champ de texte est vide
                    }

                    // Convertissez la recherche en minuscules et vérifiez si elle correspond à certains champs du médicament
                    String lowerCaseFilter = newValue.toLowerCase();
                    // Afficher ou masquer le bouton d'effacement en fonction de la saisie
                    updateClearButtonVisibility(newValue);
                    return medicament.getLibelle().toLowerCase().contains(lowerCaseFilter);
                            //|| medicament.getDescription().toLowerCase().contains(lowerCaseFilter)
                           // || medicament.getDosage().toLowerCase().contains(lowerCaseFilter);

                }));



        // Créez une liste triée liée à la liste filtrée
        SortedList<ProduitFournisseur> sortedList = new SortedList<>(filteredList);

        // Ajouter un écouteur à la liste triée pour mettre à jour le nombre de produits
        sortedList.addListener((ListChangeListener<ProduitFournisseur>) change -> {
            updateNombreProduitsLabel();
        });
        // Liez la liste triée à la TableView
        tableProduitUbipharm.setItems(sortedList);

        // Définissez le comparateur pour trier la liste
        sortedList.comparatorProperty().bind(tableProduitUbipharm.comparatorProperty());

    }




    private void configureTableView() {
        produitColumn.setCellValueFactory(new PropertyValueFactory<>("codeBarres"));
        libelleColumn.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        prix_cession_Column.setCellValueFactory(new PropertyValueFactory<>("prixCession"));
        prix_public_column.setCellValueFactory(new PropertyValueFactory<>("prixPublic"));

        tableProduitUbipharm.setItems(produits);
    }

    String filePath = "src/main/resources/liste_produits2.xlsx"; // Chemin vers votre fichier Excel

    private void loadProductsFromExcel(String filePath) {
        try (InputStream file = new FileInputStream(filePath)) {
            if (file == null) {
                throw new IllegalArgumentException("File not found: " + filePath);
            }
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Long codeBarre = (long) row.getCell(0).getNumericCellValue();
                String libelle = row.getCell(1).getStringCellValue();
                double prixCession = row.getCell(3).getNumericCellValue();
                double prixPublic = row.getCell(4).getNumericCellValue();

                ProduitFournisseur product = new ProduitFournisseur(codeBarre, libelle, prixCession, prixPublic);
                produits.add(product);
            }
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error loading Excel file", e);
        }
    }

    // Méthode pour mettre à jour le label du nombre total de produits
    private void updateNombreProduitsLabel() {
        System.out.println("Updating label...");
        nbr_produit_label.setText(Integer.toString(tableProduitUbipharm.getItems().size()));

    }



    private void setBouton(){
        String absolutePath1 = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Close.png").toUri().toString();

        final ImageView CloseIcon = new ImageView(new Image(absolutePath1));

        CloseIcon.setFitWidth(20);
        CloseIcon.setPreserveRatio(true);
        btnSupprimer.setGraphic(CloseIcon);

        // Ajouter un gestionnaire d'événements sur le bouton clearButton
        btnSupprimer.setOnAction(event -> {
            // Effacer le contenu du TextField rechercherTxtF
            rechercheTextfield.clear();
        });


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTableView();
        loadProductsFromExcel(filePath);
        rechercherProduit();
        updateNombreProduitsLabel();
        // Ajouter un écouteur à la liste observable de produits
        produits.addListener((ListChangeListener<ProduitFournisseur>) change -> {
            while (change.next()) {
                updateNombreProduitsLabel();
            }
        });

        // Rendre le bouton invisible à l'initialisation
        btnSupprimer.setVisible(false);
        setBouton();




    }
}
