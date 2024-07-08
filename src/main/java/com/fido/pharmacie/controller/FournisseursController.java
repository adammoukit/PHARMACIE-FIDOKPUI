package com.fido.pharmacie.controller;


import com.fido.pharmacie.model.ProduitFournisseur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

import java.util.ResourceBundle;

public class FournisseursController implements Initializable {


    @FXML
    private TableColumn<ProduitFournisseur, String> codeBarreColumn;

    @FXML
    private TableColumn<ProduitFournisseur, String> libelleColumn;

    @FXML
    private AnchorPane mainContainer;

    @FXML
    private TableColumn<ProduitFournisseur, Double> prixCessionColumn;

    @FXML
    private TableColumn<ProduitFournisseur, Double> prixPublicColumn;

    @FXML
    private TableView<ProduitFournisseur> tableProduitUbipharm;


    private ObservableList<ProduitFournisseur> produits = FXCollections.observableArrayList();


    private void configureTableView() {
        codeBarreColumn.setCellValueFactory(new PropertyValueFactory<>("codeBarre"));
        libelleColumn.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        prixCessionColumn.setCellValueFactory(new PropertyValueFactory<>("prixCession"));
        prixPublicColumn.setCellValueFactory(new PropertyValueFactory<>("prixPublic"));

        tableProduitUbipharm.setItems(produits);
    }

    private void loadProductsFromExcel(String filePath) {
        try {
            InputStream file = getClass().getClassLoader().getResourceAsStream(filePath);
            if (file == null) {
                throw new IllegalArgumentException("File not found: " + filePath);
            }
            Workbook workbook = new HSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Commence à partir de la ligne 2
                Row row = sheet.getRow(i);
                String codeBarre = row.getCell(0).getStringCellValue();
                String libelle = row.getCell(1).getStringCellValue();
                double prixCession = row.getCell(3).getNumericCellValue();
                double prixPublic = row.getCell(4).getNumericCellValue();

                ProduitFournisseur product = new ProduitFournisseur(codeBarre, libelle, prixCession, prixPublic);
                produits.add(product);
            }

            workbook.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        configureTableView();
        loadProductsFromExcel("ListeProduits.xlsx.xls");
        tableProduitUbipharm.setItems(produits);

    }
}
