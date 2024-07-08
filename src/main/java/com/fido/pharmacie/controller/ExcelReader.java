package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.ProduitFournisseur;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    public static List<ProduitFournisseur> readProductsFromExcel(String filePath) throws IOException {
        List<ProduitFournisseur> products = new ArrayList<>();


        FileInputStream file = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Commence à partir de la ligne 2
            Row row = sheet.getRow(i);
            String codeBarres = row.getCell(0).getStringCellValue();
            String libelle = row.getCell(1).getStringCellValue();
            double prixCession = row.getCell(3).getNumericCellValue();
            double prixPublic = row.getCell(4).getNumericCellValue();

            ProduitFournisseur product = new ProduitFournisseur(codeBarres, libelle, prixCession, prixPublic);
            products.add(product);
        }

        workbook.close();
        file.close();

        return products;
    }
}
