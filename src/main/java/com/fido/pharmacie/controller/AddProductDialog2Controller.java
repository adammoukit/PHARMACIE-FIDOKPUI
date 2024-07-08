package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import com.fido.pharmacie.model.MedicamentStock;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import java.awt.*;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.sql.Date;
import java.util.*;

import javafx.event.Event;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.fido.pharmacie.controller.DatabaseConnection.connection;
import static com.fido.pharmacie.controller.DatabaseConnection.showAlert;

public class AddProductDialog2Controller implements Initializable {





    @FXML
    private TextArea description;


    @FXML
    private TextField dosage;

    @FXML
    private TextField nomProduit;

    @FXML
    private TextField prixProduit;

    @FXML
    private ComboBox<String> categorieCombo;

    @FXML
    private TextField codeBarres;

    @FXML
    private TextArea instructionTextArea;

    @FXML
    private ComboBox<String> FournisseurCombo;

    @FXML
    private DatePicker date_expiration;

    @FXML
    private DatePicker date_reception;

    @FXML
    private TextField quantite;

    @FXML
    private TextField numero_lot;



    @FXML
    private Tab informationTab;

    @FXML
    private Tab stockTab;


    @FXML
    private Button precedent;

    @FXML
    private Button suivantBtn;

    @FXML
    private TabPane tabPane;



    @FXML
    private Button btnAjouter;






    private MedicamentController medicamentController;

    public void setMedicamentController(MedicamentController medicamentController) {
        this.medicamentController = medicamentController;
    }


    @FXML
    void handlePrecedent(ActionEvent event) {
        // Lorsque vous cliquez sur le bouton "precedent"
        tabPane.getSelectionModel().select(informationTab);
    }

    @FXML
    void handleSuivant(ActionEvent event) {
        // Lorsque vous cliquez sur le bouton "suivantBtn"
        tabPane.getSelectionModel().select(stockTab);
    }


    private List<String> searchProductsInDatabase(String searchText) {
        List<String> matchingProducts = new ArrayList<>();

        String query = "SELECT p.nomProduitF, p.dosageProduitF, f.NOM " +
                "FROM produitfournisseur p " +
                "LEFT JOIN fournisseurs f ON p.id_fournisseur = f.id_fournisseur " +
                "WHERE p.nomProduitF LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + searchText + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String productName = resultSet.getString("nomProduitF");
                String dosage = resultSet.getString("dosageProduitF");
                String supplierName = resultSet.getString("NOM");
                String displayText = productName + "  - Dosage: " + dosage + "  (- " + supplierName + ")";
                matchingProducts.add(displayText);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception SQL
        }

        return matchingProducts;
    }


    private void showAlertChampsVide() {
        // Émettre un bip sonore
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Champ(s) vide(s)");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez remplir tous les champs.");
        alert.showAndWait();
    }

    private boolean areFieldsEmpty() {
        return nomProduit.getText().isEmpty() ||
                description.getText().isEmpty() ||
                dosage.getText().isEmpty() ||
                prixProduit.getText().isEmpty() ||
                categorieCombo.getValue() == null ||
                codeBarres.getText().isEmpty()||
                instructionTextArea.getText().isEmpty()||
                quantite.getText().isEmpty()||
                numero_lot.getText().isEmpty()||
                date_reception.getValue() == null||
                date_expiration.getValue() == null||
                FournisseurCombo.getValue() == null;


    }


    @FXML
    private void handleAddButtonAction() {
        // Récupérer les données saisies par l'utilisateur
        MedicamentStock medicament = getAddProductData();

        // Si les données ne sont pas nulles, insérez-les dans la base de données
        if (medicament != null) {

            insertDataIntoDatabase(medicament);

            // Actualiser la TableView dans le MedicamentController
            medicamentController.actualiserTableView();

            // Fermer la boîte de dialogue après l'ajout réussi
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.close();

        }
    }


    public MedicamentStock getAddProductData() {
        String productName_ = nomProduit.getText().trim().toUpperCase();
        String description_ = description.getText().trim().toUpperCase();
        String dosage_ = dosage.getText().trim();
        String prixText_ = prixProduit.getText().trim();
        String codeBarres_ = codeBarres.getText().trim();
        String categorie_ = categorieCombo.getValue();
        String fournisseur_ = FournisseurCombo.getValue(); // Nouvel attribut
        String instruction_ = instructionTextArea.getText().trim();
       // String numeroLot_ = numero_lot.getText().trim();
        String quantiteText_ = quantite.getText().trim();

        //LocalDate dateReception_ = date_reception.getValue(); // Assurez-vous que dateReceptionPicker est défini
        //LocalDate dateExpiration_ = date_expiration.getValue(); // Assurez-vous que dateExpirationPicker est défini

        // Vérifier si les champs requis sont vides
        if (areFieldsEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            showAlertChampsVide();
            return null; // Retourner null si les champs sont vides
        }

        // Vérifier si les champs prixProduit et quantite contiennent uniquement des chiffres
        if (!isNumeric(prixText_) || !isNumeric(quantiteText_)) {
            Toolkit.getDefaultToolkit().beep();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Les valeurs de prix ou de quantité ne sont pas valides. Veuillez entrer des chiffres.");
            alert.showAndWait();
            return null; // Ou lève une exception si tu préfères
        }

        // Convertir les chaînes en nombres
        try {
            double prix = Double.parseDouble(prixText_);
            int quantite = Integer.parseInt(quantiteText_);

            // Convertir LocalDate en Date SQL
           // Date sqlDateReception = Date.valueOf(dateReception_);
           // Date sqlDateExpiration = Date.valueOf(dateExpiration_);

            // Créer et retourner l'objet MedicamentStock
            MedicamentStock medicament = new MedicamentStock(
                    codeBarres_, productName_,dosage_, description_, prix, categorie_,
                      quantite, fournisseur_, instruction_

            );

            return medicament;

        } catch (NumberFormatException e) {
            Toolkit.getDefaultToolkit().beep();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Les valeurs de prix ou de quantité ne sont pas valides. Veuillez entrer des chiffres.");
            alert.showAndWait();

            alert.setOnCloseRequest(Event::consume);

            return null; // Ou lève une exception si tu préfères
        }
    }


    private boolean isNumeric(String str) {
        // Vérifier si la chaîne est numérique
        return str.matches("\\d+");
    }










    void insertDataIntoDatabase(MedicamentStock medicament) {
        // Requête pour vérifier si le produit existe déjà
        String queryCheckProduct = "SELECT COUNT(*) FROM produits WHERE code_barres = ?";
        // Requête pour insérer dans la table produits
        String queryProduits = "INSERT INTO produits (code_barres, nom_produit, description, dosage, prix, fournisseur, instructions_utilisation, categorie_produit) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        // Requête pour insérer dans la table stock
        String queryStock = "INSERT INTO stocks (code_barres, quantite, date_reception, date_expiration, numero_lot) VALUES (?, ?, ?, ?, ?)";

        try (
                // Utilisation de la connexion partagée
                PreparedStatement preparedStatementCheckProduct = connection.prepareStatement(queryCheckProduct);
                PreparedStatement preparedStatementProduits = connection.prepareStatement(queryProduits);
                PreparedStatement preparedStatementStock = connection.prepareStatement(queryStock)
        ) {
            // Début de la transaction
            connection.setAutoCommit(false);

            // Vérifier si le produit existe déjà
            preparedStatementCheckProduct.setString(1, medicament.getCodeBarre());
            ResultSet resultSet = preparedStatementCheckProduct.executeQuery();
            resultSet.next();
            boolean productExists = resultSet.getInt(1) > 0;

            if (!productExists) {
                // Insérer dans la table produits si le produit n'existe pas
                preparedStatementProduits.setString(1, medicament.getCodeBarre());
                preparedStatementProduits.setString(2, medicament.getNomProduit());
                preparedStatementProduits.setString(3, medicament.getDescription());
                preparedStatementProduits.setString(4, medicament.getDosage());
                preparedStatementProduits.setDouble(5, medicament.getPrixUnitaire());
                preparedStatementProduits.setString(6, medicament.getFournisseur());
                preparedStatementProduits.setString(7, medicament.getInstructions());
                preparedStatementProduits.setString(8, medicament.getCategorie());

                preparedStatementProduits.executeUpdate();
            }

            // Insérer dans la table stock
            preparedStatementStock.setString(1, medicament.getCodeBarre());
            preparedStatementStock.setInt(2, medicament.getQuantite());
            preparedStatementStock.setDate(3, medicament.getDateReception());
            preparedStatementStock.setDate(4, medicament.getDateExpiration());
            preparedStatementStock.setString(5, medicament.getNumeroLot());

            preparedStatementStock.executeUpdate();

            // Valider la transaction
            connection.commit();

            // Afficher un message de succès
            System.out.println("Données insérées avec succès dans la base de données.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("SUCCESS");
            alert.setHeaderText(null);
            alert.setContentText("Produit Ajouté avec succès.");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();

            // Annuler la transaction en cas d'erreur
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }

            // Émettre un bip sonore
            Toolkit.getDefaultToolkit().beep();

            // Afficher une alerte d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'insertion");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'insertion des données dans la base de données.");
            alert.showAndWait();
        } finally {
            try {
                // Remettre l'auto-commit à true
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Ajoute une bordure Noir au DialogPane
        BorderStroke borderStroke = new BorderStroke(Color.GREY,
                BorderStrokeStyle.SOLID, null, new BorderWidths(2));
        Border border = new Border(borderStroke);
        //dialogPane.setBorder(border);


        // Liste des formes de produits
        List<String> formesListData = Arrays.asList(
                "Comprimés", "Comprimés à croquer", "Capsules", "Gélules",
                "Sirops", "Solutions", "Suspensions", "Gouttes",
                "Pommades", "Crèmes", "Patchs", "Suppositoires",
                "Inhalateurs", "Aérosols", "Injectables"
        );

        // Convertir les éléments en majuscules
        List<String> formesList = formesListData.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        // Ajout des formes à la ComboBox
        categorieCombo.setItems(FXCollections.observableArrayList(formesList));

        List<String> fournisseurData = Arrays.asList(
                "UNIPHART", "TEDIS", "GRP", "NV SANTE",
                "SLPT"
        );

        // Convertir les éléments en majuscules
        List<String> fournisseur = fournisseurData.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        // Ajout des formes à la ComboBox
        FournisseurCombo.setItems(FXCollections.observableArrayList(fournisseur));


    }



}


