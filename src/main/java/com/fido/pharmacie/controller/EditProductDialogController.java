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
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.fido.pharmacie.controller.DatabaseConnection.connection;

public class EditProductDialogController implements Initializable {


    @FXML
    private ComboBox<String> FournisseurCombo;

    @FXML
    private Button btnAjouter;

    @FXML
    private ComboBox<String> categorieCombo;

    @FXML
    private TextField codeBarres;

    @FXML
    private DatePicker date_expiration;

    @FXML
    private DatePicker date_reception;

    @FXML
    private TextArea description;

    @FXML
    private TextField dosage;

    @FXML
    private Tab informationTab;

    @FXML
    private TextArea instructionTextArea;

    @FXML
    private TextField nomProduit;

    @FXML
    private TextField numero_lot;

    @FXML
    private Button precedent;

    @FXML
    private TextField prixProduit;

    @FXML
    private TextField quantite;

    @FXML
    private Tab stockTab;

    @FXML
    private Button suivantBtn;

    @FXML
    private TabPane tabPane;
    private MedicamentController medicamentController;
    private MedicamentStock selectedProduct;

    @FXML
    private Button btnModifier;

    public void setMedicamentController(MedicamentController medicamentController) {
        this.medicamentController = medicamentController;
    }

    private String medicamentID ;


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

    public void initData(MedicamentStock selectedItem) {
        this.selectedProduct = selectedItem;
        // Initialisez les champs avec les valeurs de l'objet sélectionné
        nomProduit.setText(selectedItem.getNomProduit());
        description.setText(selectedItem.getDescription());

        codeBarres.setText(selectedItem.getCodeBarre());

        dosage.setText(selectedItem.getDosage());
        prixProduit.setText(String.valueOf(selectedItem.getPrixUnitaire()));


        FournisseurCombo.setValue(selectedItem.getFournisseur());
        categorieCombo.setValue(selectedItem.getCategorie());

        instructionTextArea.setText(selectedItem.getInstructions());

        // Initialiser les autres champs
        quantite.setText(String.valueOf(selectedItem.getQuantite()));
        // Conversion de java.sql.Date en java.time.LocalDate
        if (selectedItem.getDateExpiration() != null) {
            date_expiration.setValue(selectedItem.getDateExpiration().toLocalDate());
        }
        if (selectedItem.getDateReception() != null) {
            date_reception.setValue(selectedItem.getDateReception().toLocalDate());
        }
        numero_lot.setText(selectedItem.getNumeroLot());



        medicamentID = selectedItem.getCodeBarre();

    }


    public String getMedicamentID() {
        return medicamentID;
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
              //  numero_lot.getText().isEmpty()||
              //  date_reception.getValue() == null||
               // date_expiration.getValue() == null||
                FournisseurCombo.getValue() == null;


    }


    private void showAlertChampsVide() {
        // Émettre un bip sonore
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Champ(s) vide(s)");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez remplir tous les champs!!!");
        alert.showAndWait();
    }private void showAlertDatesInvalides() {
        // Émettre un bip sonore
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("1 ou plusieurs dates invalides");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez remplir tous les champs des dates !!!");
        alert.showAndWait();
    }

    private boolean isNumeric(String str) {
        // Vérifier si la chaîne est numérique
        return str.matches("\\d+");
    }



    public MedicamentStock processUpdate() {
        if (areFieldsEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            showAlertChampsVide();
            return null;
        }

        // Obtenez les nouvelles valeurs des champs depuis les contrôles de la boîte de dialogue
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

       // LocalDate dateReception_ = date_reception.getValue();
        //LocalDate dateExpiration_ = date_expiration.getValue();

        // Vérifier si les champs prixProduit et quantite contiennent uniquement des chiffres
        if (!isNumeric(prixText_) || !isNumeric(quantiteText_)) {
            Toolkit.getDefaultToolkit().beep();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Les valeurs de prix ou de quantité ne sont pas valides. Veuillez entrer des chiffres.");
            alert.showAndWait();
            return null;
        }

        try {
            Double updatedPrix = Double.parseDouble(prixText_);
            Integer updatedQuantite = Integer.parseInt(quantiteText_);


            // Créer l'objet MedicamentStock avec les nouvelles valeurs
            MedicamentStock medicament = new MedicamentStock(
                    codeBarres_, productName_, dosage_, description_, updatedPrix, categorie_,
                     updatedQuantite, fournisseur_, instruction_
            );

            // Mettre à jour les données dans la base de données
            updateDataInDatabase(medicament);

            // Actualiser la TableView dans le MedicamentController
            medicamentController.actualiserTableViewModification();

            // Afficher un message de succès à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Le produit a été mis à jour avec succès dans la base de données.");
            alert.showAndWait();

            // Fermer la boîte de dialogue
            Stage stage = (Stage) nomProduit.getScene().getWindow();
            stage.close();

            return medicament; // Retourner l'objet mis à jour si nécessaire

        } catch (NumberFormatException e) {
            // Afficher une alerte si les valeurs de prix ou de quantité ne sont pas valides
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Les valeurs de prix ou de quantité ne sont pas valides. Erreur : " + e.getMessage());
            alert.showAndWait();
        }

        return null;
    }




    // Méthode pour mettre à jour les données dans les tables produits et stock
    public void updateDataInDatabase(MedicamentStock updatedMedicament) {
        // Obtenez l'identifiant du médicament à mettre à jour (à remplacer par votre méthode getMedicamentID)
        String medicamentID = updatedMedicament.getCodeBarre(); // Remplacez cette ligne par la façon dont vous obtenez l'ID du médicament

        String produitsQuery = "UPDATE produits SET nom_produit = ?, description = ?, dosage = ?, prix = ?, fournisseur = ?, instructions_utilisation = ?, categorie_produit = ?  WHERE code_barres = ?";
        String stockQuery = "UPDATE stocks SET  date_reception = ?, date_expiration = ?, numero_lot = ? WHERE code_barres = ?";

        try {
            connection.setAutoCommit(false); // Démarrez une transaction

            // Mettre à jour la table produits
            try (PreparedStatement produitsStatement = connection.prepareStatement(produitsQuery)) {
                produitsStatement.setString(1, updatedMedicament.getNomProduit());
                produitsStatement.setString(2, updatedMedicament.getDescription());
                produitsStatement.setString(3, updatedMedicament.getDosage());
                produitsStatement.setDouble(4, updatedMedicament.getPrixUnitaire());
                produitsStatement.setString(5, updatedMedicament.getFournisseur());
                produitsStatement.setString(6, updatedMedicament.getInstructions());
                produitsStatement.setString(7, updatedMedicament.getCategorie());
                produitsStatement.setString(8, medicamentID);

                produitsStatement.executeUpdate();
            }

            // Mettre à jour la table stock
            try (PreparedStatement stockStatement = connection.prepareStatement(stockQuery)) {
                //stockStatement.setInt(1, updatedMedicament.getQuantite());
                stockStatement.setDate(1, updatedMedicament.getDateReception());
                stockStatement.setDate(2, updatedMedicament.getDateExpiration());
                stockStatement.setString(3, updatedMedicament.getNumeroLot());
                stockStatement.setString(4, medicamentID);

                stockStatement.executeUpdate();
            }

            connection.commit(); // Valider la transaction

            System.out.println("Données mises à jour avec succès dans les tables produits et stock.");
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit et stock mis à jour avec succès.");

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // En cas d'échec, annuler la transaction
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour des données.");
        } finally {
            try {
                connection.setAutoCommit(true); // Rétablir le mode auto-commit par défaut
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // ... (Le reste de votre code pour l'affichage de l'alerte)
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
