package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentStock;
import com.fido.pharmacie.model.PanierItem;
import com.fido.pharmacie.model.ProduitFournisseur;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Popup;

import java.awt.*;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import static com.fido.pharmacie.controller.DatabaseConnection.connection;

import static com.fido.pharmacie.controller.MedicamentController.panier;

public class ConfigurerCommandeController implements Initializable {

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAnnule;

    @FXML
    private Button btnValider;

    @FXML
    private TextField codeBarres;

    @FXML
    private TextField libelleTxt;

    @FXML
    private  TextField nomFournisseur;

    @FXML
    private TextField numero_commande;

    @FXML
    private TextField prixCessionTxt;

    @FXML
    private TextField prixPublicTxt;

    @FXML
    private TextField qteCommanderTxt;

    @FXML
    private TextField rechercheProduitTxt;


    @FXML
    private Button clearButton;




    // Méthode pour définir le nom du fournisseur
    public  void setNomFournisseur(String nom) {
        nomFournisseur.setText(nom);
    }

    // Méthode pour générer un numéro de commande
    private String genererNumeroCommande() {
        // Obtenir la date et l'heure actuelles
        LocalDateTime maintenant = LocalDateTime.now();

        // Formatter les parties de la date
        int annee = maintenant.getYear();
        int mois = maintenant.getMonthValue();
        int jour = maintenant.getDayOfMonth();

        Random random = new Random();
        String partieAleatoire1 = genererPartieAleatoire(random, 2); // Générer 2 caractères aléatoires
        String partieAleatoire2 = genererPartieAleatoire(random, 2); // Générer 2 autres caractères aléatoires

        // Construire le numéro de commande
        String numeroCommande = String.format("CMDn°%s%d%02d%02d%s",
                partieAleatoire1, annee, mois, jour, partieAleatoire2);

        return numeroCommande;
    }
    // Méthode pour générer une chaîne de caractères aléatoires
    private String genererPartieAleatoire(Random random, int longueur) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(longueur);
        for (int i = 0; i < longueur; i++) {
            int index = random.nextInt(alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }


    // Méthode pour mettre à jour la visibilité du bouton d'effacement
    private void updateClearButtonVisibility(String text) {
        clearButton.setVisible(!text.isEmpty());
    }

    private void setupAutoCompletion() {
        // Créer le TableView et le Popup une seule fois
        TableView<ProduitFournisseur> tableView = new TableView<>();
        Popup popup = new Popup();

        // Créer les colonnes du TableView
        TableColumn<ProduitFournisseur, Long> Id_column = new TableColumn<>("#");
        Id_column.setCellValueFactory(new PropertyValueFactory<>("codeBarres"));

        TableColumn<ProduitFournisseur, String> produitColumn = new TableColumn<>("Libelle");
        produitColumn.setCellValueFactory(new PropertyValueFactory<>("libelle"));

        TableColumn<ProduitFournisseur, Double> prixCession = new TableColumn<>("Prix Cession");
        prixCession.setCellValueFactory(new PropertyValueFactory<>("prixCession"));

        TableColumn<ProduitFournisseur, Double> prixPublic = new TableColumn<>("Prix Pubic");
        prixPublic.setCellValueFactory(new PropertyValueFactory<>("prixPublic"));



        // Ajouter les colonnes au TableView
        tableView.getColumns().addAll(Id_column, produitColumn, prixCession, prixPublic );

        // Ajouter le TableView au Popup
        popup.getContent().add(tableView);

        // Lier la largeur du TableView à celle du TextField
        tableView.prefWidthProperty().bind(rechercheProduitTxt.widthProperty());
        tableView.prefHeightProperty().bind(rechercheProduitTxt.heightProperty().multiply(4)); // Multiplier par 4 pour obtenir une hauteur appropriée

        // Ajouter un gestionnaire d'événements au TextField rechercherTxtF pour l'autocomplétion
        rechercheProduitTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            // Obtenir une liste de produits correspondants depuis la base de données
            List<ProduitFournisseur> matchingProducts = searchProductsInDatabase2(newValue);

            // Ajouter les données au TableView
            tableView.setItems(FXCollections.observableArrayList(matchingProducts));

            // Afficher ou masquer le bouton d'effacement en fonction de la saisie
            updateClearButtonVisibility(newValue);

            // Afficher ou masquer le Popup
            if (!newValue.isEmpty() && !matchingProducts.isEmpty()) {
                // Lier l'affichage de la fenêtre contextuelle à la position du TextField
                Platform.runLater(() -> {
                    popup.show(rechercheProduitTxt,
                            rechercheProduitTxt.localToScreen(rechercheProduitTxt.getBoundsInLocal()).getMinX(),
                            rechercheProduitTxt.localToScreen(rechercheProduitTxt.getBoundsInLocal()).getMaxY());
                    // Lier la largeur du Popup à celle du TableVirechercheProduitTxtew
                    popup.setWidth(tableView.getWidth());
                });
            } else {
                popup.hide();
            }
        });

        // Définir un gestionnaire pour la sélection d'un élément depuis le TableView
        tableView.setOnMouseClicked(event -> {
            ProduitFournisseur medicamentSelectionne = tableView.getSelectionModel().getSelectedItem();
            if (medicamentSelectionne != null) {
                // Afficher l'objet dans la console
                System.out.println("code_barres : " + medicamentSelectionne.getCodeBarres());
                System.out.println("Médicament sélectionné : " + medicamentSelectionne.getLibelle());
                System.out.println("prix cession : " + medicamentSelectionne.getPrixCession());
                System.out.println("prix public : " + medicamentSelectionne.getPrixPublic());

                // Initialiser les champs avec les valeurs du médicament sélectionné
                Long codeBarresValue = medicamentSelectionne.getCodeBarres();
                if (codeBarresValue != null) {
                    codeBarres.setText(String.valueOf(codeBarresValue));
                } else {
                    codeBarres.setText(""); // Gestion du cas où le code barres est null
                }
                libelleTxt.setText(medicamentSelectionne.getLibelle());

                // Vérifier et initialiser le champ prixCession
                Double prixCessionValue = medicamentSelectionne.getPrixCession();
                if (prixCessionValue != null) {
                    prixCessionTxt.setText("FCFA " + String.format("%.2f", prixCessionValue)); // Formatage avec deux décimales
                } else {
                    prixCessionTxt.setText(""); // Gestion du cas où le prix de cession est null
                }

                // Vérifier et initialiser le champ prixPublic
                Double prixPublicValue = medicamentSelectionne.getPrixPublic();
                if (prixPublicValue != null) {
                    prixPublicTxt.setText("FCFA " + String.format("%.2f", prixPublicValue)); // Formatage avec deux décimales
                } else {
                    prixPublicTxt.setText(""); // Gestion du cas où le prix public est null
                }

            }
            popup.hide();
        });
    }

    private void setBouton(){
        String absolutePath1 = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Close.png").toUri().toString();

        final ImageView CloseIcon = new ImageView(new Image(absolutePath1));

        CloseIcon.setFitWidth(20);
        CloseIcon.setPreserveRatio(true);
        clearButton.setGraphic(CloseIcon);

        // Ajouter un gestionnaire d'événements sur le bouton clearButton
        clearButton.setOnAction(event -> {
            // Effacer le contenu du TextField rechercherTxtF
            rechercheProduitTxt.clear();
        });


    }

    // Méthode de recherche de produits dans la base de données
    private List<ProduitFournisseur> searchProductsInDatabase2(String searchText) {
        List<ProduitFournisseur> medicamentStocks = new ArrayList<>();

        String query = "SELECT * FROM produitfournisseur WHERE libelle LIKE ? OR produit LIKE ?";

        try (
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Remplir les paramètres de la requête
            String searchPattern = "%" + searchText + "%";
            preparedStatement.setString(1, searchPattern);
            preparedStatement.setString(2, searchPattern);

            // Exécuter la requête
            ResultSet resultSet = preparedStatement.executeQuery();

            // Parcourir les résultats de la requête
            while (resultSet.next()) {
                Long codeBarres = resultSet.getLong("produit");
                String nomProduit = resultSet.getString("libelle");

                double prixCession = resultSet.getDouble("prixCession");
                double prixPublic = resultSet.getDouble("prixPublic");

                // Créer un objet MedicamentStock et l'ajouter à la liste
                ProduitFournisseur medicamentStock = new ProduitFournisseur(codeBarres, nomProduit, prixCession, prixPublic);
                medicamentStocks.add(medicamentStock);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medicamentStocks;
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String numeroCommande = genererNumeroCommande();
        numero_commande.setText(numeroCommande);

        setupAutoCompletion();

        setBouton();
    }
}
