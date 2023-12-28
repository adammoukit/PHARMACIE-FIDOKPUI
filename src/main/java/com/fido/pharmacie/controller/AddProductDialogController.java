package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;



import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.Event;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.fido.pharmacie.controller.DatabaseConnection.connection;

public class AddProductDialogController implements Initializable {


    @FXML
    private DialogPane dialogPane;

    @FXML
    private DatePicker dateExpiration;

    @FXML
    private TextArea description;


    @FXML
    private TextField dosage;

    @FXML
    private TextField nomProduit;

    @FXML
    private TextField prixProduit;

    @FXML
    private TextField qteProduit;

    @FXML
    private TextField fournisseurTextField;


    private AutoCompletionBinding<String> binding;  // Declare AutoCompletionBinding as a member variable





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





    public MedicamentSearch getAddProductData() {
        String productName_ = nomProduit.getText().trim().toUpperCase();
        String description_ = description.getText().trim().toUpperCase();
        String dosage_ = dosage.getText().trim();
        String prixText_ = prixProduit.getText().trim();
        String quantiteText_ = qteProduit.getText().trim();
        LocalDate expirationDate_ = dateExpiration.getValue(); // Récupération de la date d'expiration depuis un composant DatePicker par exemple

        // Récupérer le nom du fournisseur sélectionné dans le ComboBox
       // String nomFournisseur = fournisseurComboBox.getValue();

        // Utiliser le nom du fournisseur pour trouver l'id_fournisseur associé dans la base de données
        //int idFournisseur = getIdFournisseurParNom(nomFournisseur);


        // Vérifier si les champs requis sont vides
        if (productName_.isEmpty() || description_.isEmpty() || dosage_.isEmpty() || prixText_.isEmpty() || quantiteText_.isEmpty()  || expirationDate_ == null) {
            // Afficher une alerte ou lever une exception, car un ou plusieurs champs sont vides
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez renseigner tous les champs. SVP");

            // Ne fermez pas la boîte de dialogue parente après la fermeture de l'alerte
            alert.setOnHiding(event -> {
                event.consume();
            });

            alert.showAndWait();

            // Ne fermez pas la boîte de dialogue parente après la fermeture de l'alerte
           // alert.setOnCloseRequest(Event::consume);

            return null; // Ou lève une exception si tu préfères
        }


        // Vérifier si les champs prixProduit et qteProduit contiennent uniquement des chiffres
        if (!isNumeric(prixText_) || !isNumeric(quantiteText_)) {
            // Afficher une alerte ou lever une exception, car les valeurs ne sont pas valides
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Les valeurs de prix ou de quantité ne sont pas valides. Veuillez entrer des chiffres.");
            alert.showAndWait();
            return null; // Ou lève une exception si tu préfères
        }


        // Convertir les chaînes en nombres
        try {
            Double prix = Double.parseDouble(prixText_);
            Integer quantite = Integer.parseInt(quantiteText_);

            // Créer et retourner l'objet MedicamentSearch
            MedicamentSearch medicament = new MedicamentSearch(null, productName_, description_, dosage_, prix, java.sql.Date.valueOf(expirationDate_), quantite);

            // Insérer les données dans la base de données
            insertDataIntoDatabase(medicament);

            return medicament;

        } catch (NumberFormatException e) {
            // Afficher une alerte ou lever une exception, car les valeurs ne sont pas valides
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Les valeurs de prix ou de quantité ne sont pas valides.");
            alert.showAndWait();

            // Ne fermez pas la boîte de dialogue parente après la fermeture de l'alerte
            alert.setOnCloseRequest(Event::consume);

            return null; // Ou lève une exception si tu préfères
        }
    }



    private boolean isNumeric(String str) {
        // Vérifier si la chaîne est numérique
        return str.matches("\\d+");
    }





    public static int getIdFournisseurParNom(String nomFournisseur) {
        int idFournisseur = -1; // Valeur par défaut si le fournisseur n'est pas trouvé

        String query = "SELECT ID_fournisseur FROM Fournisseurs WHERE Nom = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nomFournisseur);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                idFournisseur = resultSet.getInt("id_fournisseur");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérez les erreurs de requête ici
        }

        return idFournisseur;
    }




    private void insertDataIntoDatabase(MedicamentSearch medicament) {
        String query = "INSERT INTO medicament (Nom_medicament, description, dosage, prix, date_expiration, quantite) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                //J'AI COMMENTé CETTE PARTIE PARCE QUE LA VARIABLE CONNECTION EST STATIC ET N'APPARTIENT RIEN QU'A
                //  LA CLASS DatabaseConnection et qui est partage dans toute l'application , pour eviter de creer
                // a chaque fois des connexions.

                 /*
                 static Connection connection;

                public static Connection getConnection()

                 */


        //Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, medicament.getNom_medicament());
            preparedStatement.setString(2, medicament.getDescription());
            preparedStatement.setString(3, medicament.getDosage());
            preparedStatement.setDouble(4, medicament.getPrix());
            preparedStatement.setDate(5, medicament.getDate_expiration());
            preparedStatement.setInt(6, medicament.getQuantite());


            preparedStatement.executeUpdate();


            // Afficher un message de succès si nécessaire
            System.out.println("Données insérées avec succès dans la base de données.");
        } catch (SQLException e) {
            e.printStackTrace();

            // Afficher une alerte ou gérer l'erreur d'une autre manière en cas d'échec de l'insertion
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'insertion");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'insertion des données dans la base de données.");

        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Ajoute une bordure Noir au DialogPane
        BorderStroke borderStroke = new BorderStroke(Color.GREY,
                BorderStrokeStyle.SOLID, null, new BorderWidths(2));
        Border border = new Border(borderStroke);
        dialogPane.setBorder(border);








        // Appelez la méthode de MainController pour obtenir les noms des fournisseurs
        List<String> fournisseurs = MainController.getFournisseurs();

        // Ajoutez les noms des fournisseurs au ComboBox
        //fournisseurComboBox.getItems().addAll(fournisseurs);


       // Add a listener to the nomProduit TextField to handle auto-completion
        nomProduit.textProperty().addListener((observable, oldValue, newValue) -> {
            // Get a list of matching products from the database
            List<String> matchingProducts = searchProductsInDatabase(newValue);

            // Create a FilteredList to filter the suggestions based on user input
            FilteredList<String> filteredList = new FilteredList<>(FXCollections.observableArrayList(matchingProducts));

            // Bind the auto-completion to the TextField using the filtered suggestions
             binding = TextFields.bindAutoCompletion(nomProduit, filteredList);

            // Définir un gestionnaire pour la sélection d'un élément depuis la fenêtre contextuelle d'auto-complétion
            binding.setOnAutoCompleted(event -> {
                // Gérer le cas où l'utilisateur sélectionne une suggestion
                handleAutoCompletionSelection(event.getCompletion());
            });
        });







    }




    private void handleAutoCompletionSelection(String selectedText) {
        // Extraire le nom du médicament à partir du texte sélectionné
        String[] parts = selectedText.split(" - Dosage: ");
        if (parts.length == 2) {
            String nomMedicament = parts[0];

            // Définir la valeur du TextField nomProduit avec le nom du médicament
            nomProduit.setText(nomMedicament);

            // Vous avez maintenant le nom du médicament, faites ce que vous devez avec
            System.out.println("Médicament sélectionné : " + nomMedicament);
            // Vous pouvez utiliser le nom du médicament dans d'autres parties de votre application


            // Récupérer les détails du produit à partir de la base de données
            getProductDetailsFromDatabase(nomMedicament);
        }
    }



    private void getProductDetailsFromDatabase(String nomMedicament) {
        String query = "SELECT `nomProduitF`, `descriptionProduitF`, `dosageProduitF`, " +
                "`date_expirationProduitF`,  `prixProduitF`, f.`Nom` " +
                "FROM `produitfournisseur` p " +
                "LEFT JOIN `fournisseurs` f ON p.`id_fournisseur` = f.`ID_Fournisseur` " +
                "WHERE p.`nomProduitF` = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nomMedicament);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Récupérer les valeurs de la base de données
                String nomProduitF = resultSet.getString("nomProduitF");
                String descriptionProduitF = resultSet.getString("descriptionProduitF");
                String dosageProduitF = resultSet.getString("dosageProduitF");
                LocalDate dateExpirationProduitF = resultSet.getDate("date_expirationProduitF").toLocalDate();
                double prixProduitF = resultSet.getDouble("prixProduitF");
                String fournisseurNom = resultSet.getString("Nom");


                // Adjust the price by adding 15%
                double adjustedPrice = prixProduitF * 1.15;


                // Mettre à jour les champs avec les valeurs récupérées
                nomProduit.setText(nomProduitF);
                description.setText(descriptionProduitF);
                dosage.setText(dosageProduitF);
                dateExpiration.setValue(dateExpirationProduitF);

                // Set the adjusted price in the TextField
                prixProduit.setText(String.valueOf(adjustedPrice));

                fournisseurTextField.setText(fournisseurNom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer les erreurs de requête ici
        }
    }


}


