package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.Produit;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.fido.pharmacie.controller.DatabaseConnection.getConnection;

public class DetailsVenteController implements Initializable {

    @FXML
    private Label labelDetails;

    @FXML
    private TableView<Produit> tableProduits;

    @FXML
    private TableColumn<Produit, Integer> colProduitID;

    @FXML
    private TableColumn<Produit, String> nomProduit;

    @FXML
    private TableColumn<Produit, Integer> colQuantite;

    @FXML
    private TableColumn<Produit, Double> colPrixUnitaire;

    @FXML
    private TableColumn<Produit, Double> colMontantTotal;

    private int venteId;

    Connection connection = getConnection();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisez les colonnes de la TableView
        colProduitID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProduitID()).asObject());
        nomProduit.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomProduit()));
        colQuantite.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());
        colPrixUnitaire.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixUnitaire()).asObject());
        colMontantTotal.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getMontantTotal()).asObject());
    }

    public void initializeData(int venteId) {
        this.venteId = venteId;

        // Récupérer les données de la base de données en fonction de l'ID de vente
        try  {
            String query = "SELECT m.ID, m.NOM_MEDICAMENT, dv.Quantite, dv.PrixUnitaire, (dv.Quantite * dv.PrixUnitaire) AS MontantTotal " +
                    "FROM detailvente dv " +
                    "JOIN medicament m ON dv.IDProduit = m.ID " +
                    "WHERE dv.IDVente = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, venteId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Produit produit = new Produit(
                                resultSet.getInt("ID"),
                                resultSet.getString("NOM_MEDICAMENT"),
                                resultSet.getInt("Quantite"),
                                resultSet.getDouble("PrixUnitaire"),
                                resultSet.getDouble("MontantTotal")
                        );

                        // Ajouter le produit à la TableView
                        tableProduits.getItems().add(produit);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer les erreurs de la base de données selon vos besoins
        }
    }
}
