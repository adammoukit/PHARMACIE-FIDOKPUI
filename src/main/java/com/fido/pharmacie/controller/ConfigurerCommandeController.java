package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentStock;
import com.fido.pharmacie.model.PanierItem;
import com.fido.pharmacie.model.ProduitFournisseur;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Popup;
import javafx.util.Callback;

import java.awt.*;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import static com.fido.pharmacie.controller.DatabaseConnection.connection;

import static com.fido.pharmacie.controller.DatabaseConnection.showAlert;
import static com.fido.pharmacie.controller.MedicamentController.panier;

public class ConfigurerCommandeController implements Initializable {



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

    @FXML
    private TableColumn<ProduitFournisseur, Void> actionColumn;

    @FXML
    private TableColumn<ProduitFournisseur, Long> codeBarreColumn;

    @FXML
    private TableColumn<ProduitFournisseur, String> libelleColumn;

    @FXML
    private TableColumn<ProduitFournisseur, Double> pricPublicColumn;

    @FXML
    private TableColumn<ProduitFournisseur, Double> prixCessionColumn;

    @FXML
    private TableColumn<ProduitFournisseur, Integer> qteColumn;

    @FXML
    private TableView<ProduitFournisseur> tableProduit;


    @FXML
    private TableColumn<ProduitFournisseur, Double> totalColum;

    private FournisseursController fournisseurController; // Assurez-vous que vous avez une référence correcte à FournisseurController

    public void setFournisseurController(FournisseursController fournisseurController) {
        this.fournisseurController = fournisseurController;
    }




    @FXML
    private Label totalLabel;

    // Liste pour stocker les produits dans le panier
    private List<ProduitFournisseur> panierCommandeFournisseur = new ArrayList<>();





    // Méthode pour définir le nom du fournisseur
    public  void setNomFournisseur(String nom) {
        nomFournisseur.setText(nom);
    }

    private void updateTotalPanier() {
        double total = panierCommandeFournisseur.stream()
                .mapToDouble(p -> p.getPrixPublic() * p.getQte())
                .sum();
        totalLabel.setText(String.format(" %.2f FCFA", total));
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

    private boolean validateInput() {
        if (codeBarres.getText().isEmpty() ||
                libelleTxt.getText().isEmpty() ||
                prixCessionTxt.getText().isEmpty() ||
                prixPublicTxt.getText().isEmpty() ||
                qteCommanderTxt.getText().isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(codeBarres.getText());
            parsePrix(prixCessionTxt.getText());
            parsePrix(prixPublicTxt.getText());
            Integer.parseInt(qteCommanderTxt.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Double parsePrix(String prixText) throws NumberFormatException {
        // Suppression des symboles non numériques et des espaces
        String cleanedText = prixText.replaceAll("[^\\d.]", "").trim();
        return Double.parseDouble(cleanedText);
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
        tableView.prefHeightProperty().bind(rechercheProduitTxt.heightProperty().multiply(8)); // Multiplier par 4 pour obtenir une hauteur appropriée

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

    // Méthode pour afficher une boîte de dialogue d'alerte
    private void afficherAlerte(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
    @FXML
    private void handleAjouter() {

        if (!validateInput()) {
            // Afficher un message d'erreur à l'utilisateur
            // Gérer le cas où le champ est vide
            afficherAlerte("Champ(s) Vide(s)","Veuillez remplir tous les champs avec des valeurs valides.");
            System.out.println("Veuillez remplir tous les champs avec des valeurs valides.");
            return;
        }


        try {
            // Récupérer les valeurs des champs
            Long codeBarresValue = Long.parseLong(codeBarres.getText());
            String libelleValue = libelleTxt.getText();
            // Remplacer les virgules par des points pour le format français
            String prixCessionText = prixCessionTxt.getText().replace("FCFA ", "").replace(",", ".");
            String prixPublicText = prixPublicTxt.getText().replace("FCFA ", "").replace(",", ".");

            Double prixCessionValue = Double.parseDouble(prixCessionText);
            Double prixPublicValue = Double.parseDouble(prixPublicText);
            Integer qteValue = Integer.parseInt(qteCommanderTxt.getText());

            // Créer un nouvel objet ProduitFournisseur
            ProduitFournisseur produit = new ProduitFournisseur(codeBarresValue, libelleValue, prixCessionValue, prixPublicValue, qteValue);

            // Ajouter l'objet à la TableView
            tableProduit.getItems().add(produit);

            // Ajouter le produit au panier
            panierCommandeFournisseur.add(produit);

            updateTotalPanier();

            // Effacer les champs de texte
            codeBarres.clear();
            libelleTxt.clear();
            prixCessionTxt.clear();
            prixPublicTxt.clear();
           qteCommanderTxt.clear();
        } catch (NumberFormatException e) {
            // Gérer les exceptions de format de nombre
            e.printStackTrace();
        }
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






    private void setupActionColumn(){
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ProduitFournisseur, Void> call(final TableColumn<ProduitFournisseur, Void> param) {
                return new TableCell<>() {
                    private final Button actionButton = new Button();

                    String absolutePath1 = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Delete.png").toUri().toString();

                    private final ImageView imageView = new ImageView(new Image(absolutePath1));


                    {

                        // Ajustez la taille de l'ImageView ici
                        imageView.setFitWidth(16); // Réglez la largeur souhaitée
                        imageView.setPreserveRatio(true); // Garantit que l'aspect ratio de l'image est conservé (le rapport largeur/hauteur)


                        actionButton.setGraphic(imageView);
                        actionButton.setOnAction(event -> {
                            // Code à exécuter lors du clic sur le bouton dans la cellule
                            ProduitFournisseur objet = getTableView().getItems().get(getIndex());
                            // Supprimer le produit de la TableView
                            getTableView().getItems().remove(objet);
                            // Supprimer le produit du panier si vous avez une structure pour le panier
                            // panier.remove(objet); // Si vous avez une liste ou une structure de données pour le panier, retirez également l'objet de là

                            // Ajouter le produit au panier
                            panierCommandeFournisseur.remove(objet);

                            updateTotalPanier();




                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {


                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);

                        } else {



                            setGraphic(actionButton);
                            // Ajoutez des marges intérieures à la cellule
                            setPadding(new Insets(3)); // Définissez les marges intérieures souhaitées ici
                            // Ajoutez un style CSS pour centrer le contenu de la cellule
                            setAlignment(Pos.CENTER);


                        }
                    }
                };
            }
        });

    }


   //"" ****************************************************************************************




    private void ajouterCommande() throws SQLException {

        try {

            String numCommande = numero_commande.getText();
            LocalDate dateCommande = LocalDate.now();
            double totalCommande = calculerTotalCommande();

            // Insérer dans la table COMMANDE
            String insertCommandeSQL = "INSERT INTO commandefournisseur (num_commande, date_commande, montant_total) VALUES (?, ?, ?)";
            try (PreparedStatement pstmtCommande = connection.prepareStatement(insertCommandeSQL)) {
                pstmtCommande.setString(1, numCommande);
                pstmtCommande.setDate(2, java.sql.Date.valueOf(dateCommande));
                pstmtCommande.setDouble(3, totalCommande);
                pstmtCommande.executeUpdate();
            }

            // Insérer dans la table detailcommandefournisseur
            String insertDetailCommandeSQL = "INSERT INTO detailcommandefournisseur (num_commande, id_produitfournisseur, prixCession, prixPublic, quantite, total) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtDetailCommande = connection.prepareStatement(insertDetailCommandeSQL)) {
                for (ProduitFournisseur produit : panierCommandeFournisseur) {
                    pstmtDetailCommande.setString(1, numCommande);
                    pstmtDetailCommande.setLong(2, produit.getCodeBarres());
                    pstmtDetailCommande.setDouble(3, produit.getPrixCession());
                    pstmtDetailCommande.setDouble(4, produit.getPrixPublic());
                    pstmtDetailCommande.setInt(5, produit.getQte());
                    pstmtDetailCommande.setDouble(6, produit.getTotal());
                    pstmtDetailCommande.addBatch();
                }
                pstmtDetailCommande.executeBatch();
            }

            // Vider la TableView et le panier
            tableProduit.getItems().clear();
            panierCommandeFournisseur.clear();

            updateTotalPanier();

            String message = "COMMANDE AJOUTEE AVEC SUCCES";
            showAlert(Alert.AlertType.INFORMATION, "PRODUIT AJOUTE :", message);


            // Appeler loadCommandesFromDatabase depuis FournisseurController pour mettre à jour les commandes
            fournisseurController.loadCommandesFromDatabase();




        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    private double calculerTotalCommande() {
        return panierCommandeFournisseur.stream()
                .mapToDouble(ProduitFournisseur::getTotal)
                .sum();
    }


    //"" ****************************************************************************************



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //*********************************************************************************
        String numeroCommande = genererNumeroCommande();
        numero_commande.setText(numeroCommande);
        //*********************************************************************************

        setupAutoCompletion();
        //*********************************************************************************

        setBouton();
        //*********************************************************************************

        setupActionColumn();
        //*********************************************************************************

        codeBarreColumn.setCellValueFactory(new PropertyValueFactory<>("codeBarres"));
        libelleColumn.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        prixCessionColumn.setCellValueFactory(new PropertyValueFactory<>("prixCession"));
        pricPublicColumn.setCellValueFactory(new PropertyValueFactory<>("prixPublic"));
        qteColumn.setCellValueFactory(new PropertyValueFactory<>("qte"));
        totalColum.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject()
        );
        //*********************************************************************************

        btnValider.setOnAction(event -> {
            try {
                ajouterCommande();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        //*********************************************************************************

    }
}
