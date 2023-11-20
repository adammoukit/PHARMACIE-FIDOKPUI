package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
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
    private ComboBox<String> fournisseurComboBox;



    public MedicamentSearch getAddProductData() {
        String productName_ = nomProduit.getText().trim();
        String description_ = description.getText().trim();
        String dosage_ = dosage.getText().trim();
        String prixText_ = prixProduit.getText().trim();
        String quantiteText_ = qteProduit.getText().trim();
        LocalDate expirationDate_ = dateExpiration.getValue(); // Récupération de la date d'expiration depuis un composant DatePicker par exemple

        // Récupérer le nom du fournisseur sélectionné dans le ComboBox
        String nomFournisseur = fournisseurComboBox.getValue();

        // Utiliser le nom du fournisseur pour trouver l'id_fournisseur associé dans la base de données
        int idFournisseur = getIdFournisseurParNom(nomFournisseur);


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


        // Convertir les chaînes en nombres
        try {
            Double prix = Double.parseDouble(prixText_);
            Integer quantite = Integer.parseInt(quantiteText_);

            // Créer et retourner l'objet MedicamentSearch
            MedicamentSearch medicament = new MedicamentSearch(null, productName_, description_, dosage_, prix, java.sql.Date.valueOf(expirationDate_), quantite, idFournisseur);

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
        String query = "INSERT INTO medicament (Nom_medicament, description, dosage, prix, date_expiration, quantite, id_fournisseur) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
                //J'AI COMMENTE CETTE PARTIE PARCEQUE LA VARIABLE CONNECTION EST STATIC ET APPARTIENT RIEN QU'A
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
            preparedStatement.setInt(7, medicament.getId_fournisseur());


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
        fournisseurComboBox.getItems().addAll(fournisseurs);
    }

}
