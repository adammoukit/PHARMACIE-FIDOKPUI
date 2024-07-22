package com.fido.pharmacie.controller;


import com.fido.pharmacie.model.Fournisseur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


import java.net.URL;

import java.sql.*;
import java.util.ResourceBundle;

public class PasserCommandeController implements Initializable {


    @FXML
    private DatePicker dateExpir;

    @FXML
    private TextArea descrip;

    @FXML
    private TextField dosa;

    @FXML
    private TextField fournisseurTextF;

    @FXML
    private TextField nomProd;

    @FXML
    private TextField prixPro;

    @FXML
    private TextField qteProd;

    @FXML
    private Label fournisseurLabel;

    @FXML
    private DialogPane dialogP;

    private Fournisseur fournisseur;  // Ajoutez une propriété pour stocker le fournisseur

    @FXML
    private ButtonType okButtonType;



    Connection connectDB = DatabaseConnection.getConnection();


    // Ajoutez une méthode pour recevoir le fournisseur de la fenêtre principale
    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
        // Mettez à jour l'interface utilisateur avec les informations du fournisseur si nécessaire
        if (fournisseur != null) {
            fournisseurLabel.setText(fournisseur.getNom());
        }
    }

    private void handleOKButtonClick(ActionEvent event) {
        // Logique de traitement pour le bouton OK
        System.out.println("Bouton OK cliqué!");

        // Vous pouvez ajouter ici la logique pour enregistrer la nouvelle commande
        // Utilisez les valeurs des champs (nomProd, dosa, prixPro, dateExpir, qteProd, descrip, fournisseurTextF) pour enregistrer les données.

        // Fermer la fenêtre de dialogue
        dialogP.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // Ajouter un gestionnaire d'événements pour le bouton OK
        Button okButton = (Button) dialogP.lookupButton(okButtonType);
        okButton.addEventFilter(ActionEvent.ACTION, this::handleOKButtonClick);

    }






}

