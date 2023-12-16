package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.Fournisseur;
import com.fido.pharmacie.model.MedicamentSearch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

public class FournisseursController implements Initializable {

    @FXML
    private TableColumn<Fournisseur, String> adresseColumn;

    @FXML
    private TableColumn<Fournisseur, String> emailColumn;

    @FXML
    private TableColumn<Fournisseur, Integer> idColumn;

    @FXML
    private TableColumn<Fournisseur, String> nomColumn;

    @FXML
    private TableView<Fournisseur> tableFournisseur;

    @FXML
    private TableColumn<Fournisseur, String> telephoneColumn;

    private ObservableList<Fournisseur> fournisseursList;

    @FXML
    private TableColumn<Fournisseur, Void> actionBtn;





    Connection connectDB = DatabaseConnection.getConnection();

    private void chargerDonneesFournisseurs() {

        String query = "SELECT * FROM fournisseurs";
        try (Statement statement = connectDB.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID_Fournisseur");
                String nom = resultSet.getString("Nom");
                String adresse = resultSet.getString("Adresse");
                String telephone = resultSet.getString("Telephone");
                String email = resultSet.getString("Email");

                Fournisseur fournisseur = new Fournisseur(id, nom, adresse, telephone, email);
                fournisseursList.add(fournisseur);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }



    private void ouvrirFenetrePasserCommande(Fournisseur fournisseur) {
        try {
            // Charger la vue passerCommande.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/PasserCommande.fxml"));
            Parent root = loader.load();

            // Passer le fournisseur à la nouvelle fenêtre (si nécessaire)
            PasserCommandeController passerCommandeController = loader.getController();
            passerCommandeController.setFournisseur(fournisseur);

            // Afficher la nouvelle vue dans une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // Set the font for the TableView, Application des styles CSS pout la couleur de la tableview
        tableFournisseur.setStyle("-fx-font-family: 'Courier New'; -fx-base: rgb(158, 152, 69);");


        // Initialisez votre liste observable
        fournisseursList = FXCollections.observableArrayList();

        // Configurez les colonnes pour qu'elles correspondent aux propriétés de la classe Fournisseur
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        adresseColumn.setCellValueFactory(cellData -> cellData.getValue().adresseProperty());
        telephoneColumn.setCellValueFactory(cellData -> cellData.getValue().telephoneProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        // Chargez les données des fournisseurs depuis la base de données
        chargerDonneesFournisseurs();

        // Associez la liste observable à la TableView
        tableFournisseur.setItems(fournisseursList);


        // Configurez la colonne d'action pour afficher le bouton "Commander"
        actionBtn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Fournisseur, Void> call(final TableColumn<Fournisseur, Void> param) {
                final TableCell<Fournisseur, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Commander");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Fournisseur fournisseur = getTableView().getItems().get(getIndex());
                            // Appeler la méthode pour commander avec le fournisseur sélectionné
                            // Par exemple, vuePreparerCommande(fournisseur);
                            ouvrirFenetrePasserCommande(fournisseur);

                        });

                        // Ajoutez du style CSS pour définir la couleur du bouton en rouge
                        btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });


    }
}
