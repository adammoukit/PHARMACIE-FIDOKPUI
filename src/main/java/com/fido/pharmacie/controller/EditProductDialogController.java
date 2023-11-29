package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static com.fido.pharmacie.controller.DatabaseConnection.connection;

public class EditProductDialogController implements Initializable {


    @FXML
    private  DatePicker dateExpiration;

    @FXML
    private  TextArea description;

    @FXML
    private DialogPane dialogPane;

    @FXML
    private  TextField dosage;

    @FXML
    private  TextField nomProduit;

    @FXML
    private  TextField prixProduit;

    @FXML
    private  TextField qteProduit;

    @FXML
    private ComboBox<String> ED_fournisseurComboBox;





    private int medicamentID;




    public void initData(MedicamentSearch selectedItem) {
        // Initialisez les champs avec les valeurs de l'objet sélectionné
        nomProduit.setText(selectedItem.getNom_medicament());
        description.setText(selectedItem.getDescription());

        dosage.setText(selectedItem.getDosage());
        prixProduit.setText(String.valueOf(selectedItem.getPrix()));
        dateExpiration.setValue(selectedItem.getDate_expiration().toLocalDate());
        qteProduit.setText(String.valueOf(selectedItem.getQuantite()));
        // Initialisez d'autres champs de la même manière

         int id_fournisseur = selectedItem.getId_fournisseur();

        // Récupérer le nom du fournisseur en fonction de l'id_fournisseur
        String fournisseurNom = getFournisseurName(id_fournisseur);

        // Remplir le ComboBox avec le nom du fournisseur récupéré
        ED_fournisseurComboBox.setValue(fournisseurNom);


        //RECUPPERER L'IDENTIFIANT DE L'OBJET CLIQUER SUR LE TABLEAU MEDICAMENT POUR MODIFIER
        medicamentID = selectedItem.getID();

    }


    public int getMedicamentID() {
        return medicamentID;
    }



    // Méthode pour récupérer le nom du fournisseur en fonction de l'ID du fournisseur
    private String getFournisseurName(int idFournisseur) {
        String fournisseurNom = null;

            String sql = "SELECT Nom FROM fournisseurs WHERE ID_fournisseur = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idFournisseur);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        fournisseurNom = resultSet.getString("Nom");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        return fournisseurNom;
    }

    public void processUpdate() {
        // Obtenez les nouvelles valeurs des champs depuis les contrôles de la boîte de dialogue
        String updatedProductName = nomProduit.getText().trim();
        String updatedDescription = description.getText().trim();
        String updatedDosage = dosage.getText().trim();
        String updatedPrixText = prixProduit.getText().trim();
        String updatedQuantiteText = qteProduit.getText().trim();
        LocalDate updatedExpirationDate = dateExpiration.getValue(); // Récupération de la date d'expiration depuis un composant DatePicker par exemple

        // Récupérer le nom du fournisseur sélectionné dans le ComboBox
        String updatedNomFournisseur = ED_fournisseurComboBox.getValue();

        // Utiliser le nom du fournisseur pour trouver l'id_fournisseur associé dans la base de données
        int updatedIdFournisseur = getIdFournisseurParNom(updatedNomFournisseur);

        // Vérifier si les champs requis sont vides
        if (updatedProductName.isEmpty() || updatedDescription.isEmpty() || updatedDosage.isEmpty() || updatedPrixText.isEmpty() || updatedQuantiteText.isEmpty() || updatedExpirationDate == null) {
            // Afficher une alerte ou lever une exception, car un ou plusieurs champs sont vides
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez renseigner tous les champs.");
            alert.showAndWait();
            return;
        }

        // Convertir les chaînes en nombres
        try {
            Double updatedPrix = Double.parseDouble(updatedPrixText);
            Integer updatedQuantite = Integer.parseInt(updatedQuantiteText);

            // Créer et retourner l'objet MedicamentSearch
            MedicamentSearch updatedMedicament = new MedicamentSearch(null, updatedProductName, updatedDescription, updatedDosage, updatedPrix, java.sql.Date.valueOf(updatedExpirationDate), updatedQuantite, updatedIdFournisseur);

            // Insérer les données dans la base de données
            updateDataInDatabase(updatedMedicament);


            // Afficher une alerte ou lever une exception, car les valeurs ne sont pas valides
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("REUSSI");
            alert.setHeaderText(null);
            alert.setContentText("Modification réussi ." );
            alert.showAndWait();





        } catch (NumberFormatException e) {
            // Afficher une alerte ou lever une exception, car les valeurs ne sont pas valides
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Les valeurs de prix ou de quantité ne sont pas valides. Erreur : " + e.getMessage());
            alert.showAndWait();
        }
    }





    public int getIdFournisseurParNom(String nomFournisseur) {
        int idFournisseur = -1; // Valeur par défaut si le fournisseur n'est pas trouvé

        String query = "SELECT ID_fournisseur FROM fournisseurs WHERE NOM = ?";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, nomFournisseur);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Si le fournisseur est trouvé, récupérez son identifiant
                    idFournisseur = resultSet.getInt("id_fournisseur");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérez l'erreur d'une manière appropriée, affichez une alerte, etc.
        }

        return idFournisseur;
    }






    public void updateDataInDatabase(MedicamentSearch updatedMedicament) {


        // Obtenez l'identifiant stocké dans la propriété du contrôleur
        int medicamentID = getMedicamentID();

        String query = "UPDATE medicament SET NOM_MEDICAMENT = ?, description = ?, dosage = ?, prix = ?, date_expiration = ?, quantite = ?, id_fournisseur = ? WHERE ID = ?";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, updatedMedicament.getNom_medicament());
            preparedStatement.setString(2, updatedMedicament.getDescription());
            preparedStatement.setString(3, updatedMedicament.getDosage());
            preparedStatement.setDouble(4, updatedMedicament.getPrix());
            preparedStatement.setDate(5, updatedMedicament.getDate_expiration());
            preparedStatement.setInt(6, updatedMedicament.getQuantite());
            preparedStatement.setInt(7, updatedMedicament.getId_fournisseur());

            // Utilisez l'identifiant stocké dans la propriété du contrôleur
            preparedStatement.setInt(8, medicamentID);



            preparedStatement.executeUpdate();

            System.out.println("Données mises à jour avec succès dans la base de données.");


        } catch (SQLException e) {
            e.printStackTrace();

            // Affichez une alerte ou gérez l'erreur d'une autre manière en cas d'échec de la mise à jour
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour des données.");
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
        dialogPane.setBorder(border);

        // Appelez la méthode de MainController pour obtenir les noms des fournisseurs
        List<String> fournisseurs = MainController.getFournisseurs();

        // Ajoutez les noms des fournisseurs au ComboBox
        ED_fournisseurComboBox.getItems().addAll(fournisseurs);
    }




}
