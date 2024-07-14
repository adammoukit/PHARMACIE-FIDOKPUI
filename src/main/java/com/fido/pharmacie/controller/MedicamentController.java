package com.fido.pharmacie.controller;

import com.fido.pharmacie.controller.Authorization.Authorization;
import com.fido.pharmacie.controller.Permission.Permission;
import com.fido.pharmacie.model.MedicamentSearch;
import com.fido.pharmacie.model.MedicamentStock;
import com.fido.pharmacie.model.PanierItem;
import com.fido.pharmacie.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Toolkit;

import static com.fido.pharmacie.controller.DatabaseConnection.connection;
import static com.fido.pharmacie.controller.DatabaseConnection.showAlert;


public class MedicamentController implements Initializable{

    @FXML
    private TableView<MedicamentStock> TableMedicament;
    @FXML
    private TableColumn<MedicamentStock, String> code_barre_tableColumn;
    @FXML
    private TableColumn<MedicamentStock, String> nomMedicament_tableColumn;
    @FXML
    private TableColumn<MedicamentStock, String> description_column;
    @FXML
    private TableColumn<MedicamentStock, String> Dosage_tableColumn;
    @FXML
    private TableColumn<MedicamentStock, Double> Prix_tableColumn;
    @FXML
    private TableColumn<MedicamentStock, Date> DateExpiration_tableColumn;
    @FXML
    private TableColumn<MedicamentStock, Integer> Quantite_tableColumn;

    @FXML
    private TableColumn<MedicamentStock, String> categorieProduit;


    @FXML
    private TableColumn<MedicamentStock, String> numeroLot;

    @FXML
    private Button btnSupprimer;
    @FXML
    private Button boutonModifier;

    @FXML
    private TableColumn<MedicamentStock, Void> Action_tableColumn;


    @FXML
    private TextField keyWordTextField;

    @FXML
    private CheckBox checkBoxFiltrer;


    @FXML
    private CheckBox checkBoxFiltrerDate;


    @FXML
    private ImageView imgAjouter;


    @FXML
    private Button AjouterBtn;

    @FXML
    private ImageView imgModifier;

    @FXML
    private ImageView imgSupprimer;

    @FXML
    private Button clearButton;


    @FXML
    private TableColumn<MedicamentController, String> instructionColumn;


    @FXML
    private TableColumn<MedicamentStock, Date> DateReception_tableColumn;




    private User currentUser;

    public MedicamentController(User user) {
        this.currentUser = user;
    }





    //

    //

    /*

    * ICI JE VEUX SOULIGNE UNE ERREUR QUE J'AI RENCONTRE LORS DE LEXECUTION DE L'APPLICATION
    *
    *       VOICI L'ERREUR :
         ObservableList<MedicamentSearch> MedicamentSearchObservableList = FXCollections.emptyObservableList();

      Le problème dans votre code est que vous initialisez MedicamentSearchObservableList en tant que liste observable vide :
    *
    *      SOLUTION :

    *  Pour résoudre ce problème, initialisez MedicamentSearchObservableList en tant que FXCollections.
       observableArrayList() au lieu de FXCollections.emptyObservableList().
       Voici comment vous pouvez le faire :
    *

    *  -----ObservableList<MedicamentSearch> MedicamentSearchObservableList = FXCollections.observableArrayList();


    * */


    @FXML
    private void handleCreateButtonAction2() {
        if (!Authorization.hasPermission(currentUser, Permission.ADD_MEDICINE)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'autorisation");
            alert.setHeaderText("Vous n'avez pas l'autorisation d'ajouter un médicament.");
            alert.setContentText("Veuillez contacter l'administrateur pour obtenir les autorisations nécessaires.");
            alert.showAndWait();
            return;
        }
        try {
            // Charger le fichier FXML de la boîte de dialogue
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/AddProductDialog2.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la boîte de dialogue
            AddProductDialog2Controller dialogController = loader.getController();
            // Passer une référence du MedicamentController au AddProductDialog2Controller
            dialogController.setMedicamentController(this);

            // Créer une nouvelle scène et une nouvelle stage pour la boîte de dialogue
            Stage stage = new Stage();
            stage.setTitle("AJOUTER UN PRODUIT");
            String absolutePath = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Plus.png").toUri().toString();
            stage.getIcons().add(new Image(absolutePath));


            // Désactiver la redimension et la maximisation de la fenêtre
            stage.setResizable(false);


            // Récupérer la fenêtre principale
            Stage primaryStage = (Stage) TableMedicament.getScene().getWindow();

            // Appliquer un effet de flou à la scène principale
            GaussianBlur blur = new GaussianBlur(3);
            primaryStage.getScene().getRoot().setEffect(blur);



            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // Supprimer l'effet de flou lorsque la boîte de dialogue est fermée
            primaryStage.getScene().getRoot().setEffect(null);




        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<MedicamentStock> getAllProductsFromDatabase() {
        List<MedicamentStock> produits = new ArrayList<>();
        String query = "SELECT p.code_barres, p.nom_produit, p.dosage, p.description, p.prix, p.categorie_produit, p.fournisseur, p.instructions_utilisation, " +
                "s.numero_lot, s.date_expiration, s.date_reception, SUM(s.quantite) AS quantite_totale " +
                "FROM Produits p " +
                "JOIN Stocks s ON p.code_barres = s.code_barres " +
                "GROUP BY p.code_barres";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                String codeBarre = resultSet.getString("code_barres");
                String nomProduit = resultSet.getString("nom_produit");
                String dosage = resultSet.getString("dosage");
                String description = resultSet.getString("description");
                String fournisseur = resultSet.getString("fournisseur");
                double prixUnitaire = resultSet.getDouble("prix");
                String categorie = resultSet.getString("categorie_produit");

                String instructions = resultSet.getString("instructions_utilisation");
                int quantite = resultSet.getInt("quantite_totale");

                MedicamentStock produit = new MedicamentStock(codeBarre, nomProduit, dosage, description, prixUnitaire, categorie, quantite, fournisseur, instructions);
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'erreur de récupération des données de la base de données
        }

        return produits;
    }





    void actualiserTableView() {
        // Récupérer une nouvelle liste de produits depuis la base de données
        List<MedicamentStock> produits = getAllProductsFromDatabase();

        // Mettre à jour la liste observable et le TableView
        MedicamentStockObservableList.setAll(produits);

        // Rafraîchir le TableView
        TableMedicament.setItems(MedicamentStockObservableList);


        // Sélectionner le produit ajouté récemment (le dernier élément de la liste)
        if (!MedicamentStockObservableList.isEmpty()) {
            TableMedicament.getSelectionModel().select(MedicamentStockObservableList.size() - 1);

            // Scroll to the newly added item
            TableMedicament.scrollTo(TableMedicament.getSelectionModel().getSelectedIndex());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Produit Ajouté");
        alert.setHeaderText(null);
        alert.setContentText("Le produit a été ajouté à la base de données avec succès.");
        alert.showAndWait();
    }

    //cette methode d'actualiation de la tableview lorsque le produit a ete modifier pour eviter que cela
    //scroll jusqu'au dernier index
    void actualiserTableViewModification() {
        // Récupérer une nouvelle liste de produits depuis la base de données
        List<MedicamentStock> produits = getAllProductsFromDatabase();

        // Mettre à jour la liste observable et le TableView
        MedicamentStockObservableList.setAll(produits);

        // Rafraîchir le TableView
        TableMedicament.setItems(MedicamentStockObservableList);




    }










    // Méthode pour vérifier si un produit est déjà dans le panier
    static boolean isProductInCart(MedicamentStock product) {
        for (PanierItem item : panier) {
            if (item.getMedicament().equals(product)) {
                return true;
            }
        }
        return false;
    }






    // Méthode pour vérifier si un produit est déjà dans le panierItems
    static boolean isProductInPanierItems(PanierItem panierItem) {
        for (PanierItem item : panier) {
            if (item.getMedicament().getCodeBarre() == panierItem.getMedicament().getCodeBarre()) {
                return true; // Le produit est déjà dans le panierItems
            }
        }
        return false; // Le produit n'est pas dans le panierItems
    }








     ObservableList<MedicamentStock> MedicamentStockObservableList = FXCollections.observableArrayList();



    //DECLARATION DE L'OBJET DU PANIER
    public static List<PanierItem> panier = new ArrayList<PanierItem>();





    @FXML
    private void handleEditButtonAction() {
        // Récupérer le produit sélectionné dans la TableView
        MedicamentStock selectedProduct = TableMedicament.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            if (!Authorization.hasPermission(currentUser, Permission.ADD_MEDICINE)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'autorisation");
                alert.setHeaderText("Vous n'avez pas l'autorisation de modifier un Produit.");
                alert.setContentText("Veuillez contacter l'administrateur pour obtenir les autorisations nécessaires.");
                alert.showAndWait();
                return;
            }
            try {
                // Charger le fichier FXML de la boîte de dialogue de modification
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/EditProductDialog.fxml"));
                Parent root = loader.load();

                // Obtenir le contrôleur de la boîte de dialogue
                EditProductDialogController dialogController = loader.getController();

                // Passer une référence du MedicamentController et le produit sélectionné au contrôleur de la boîte de dialogue
                dialogController.setMedicamentController(this);
                dialogController.initData(selectedProduct);

                // Créer une nouvelle scène et une nouvelle stage pour la boîte de dialogue
                Stage stage = new Stage();
                stage.setTitle("MODIFIER LE PRODUIT");
                String absolutePath = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Edit.png").toUri().toString();
                stage.getIcons().add(new Image(absolutePath));
                stage.setResizable(false);

                // Récupérer la fenêtre principale
                Stage primaryStage = (Stage) TableMedicament.getScene().getWindow();

                // Appliquer un effet de flou à la scène principale
                GaussianBlur blur = new GaussianBlur(5);
                primaryStage.getScene().getRoot().setEffect(blur);

                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(primaryStage);
                stage.setScene(new Scene(root));
                stage.showAndWait();

                // Supprimer l'effet de flou lorsque la boîte de dialogue est fermée
                primaryStage.getScene().getRoot().setEffect(null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Afficher un message si aucun produit n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un produit à modifier.");
            alert.showAndWait();
        }
    }

    Connection connectDB = DatabaseConnection.getConnection();

    /*public void loadMedicamentStockData() {
        String medicamentViewQuery = "SELECT p.code_barres, p.nom_produit, p.dosage, p.description, p.prix, p.categorie_produit, p.fournisseur, p.instructions_utilisation, " +
                "s.numero_lot, s.date_expiration, s.date_reception, SUM(s.quantite) AS quantite_totale " +
                "FROM Produits p " +
                "JOIN Stocks s ON p.code_barres = s.code_barres " +
                "GROUP BY p.code_barres";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(medicamentViewQuery);

            while (queryOutput.next()) {
                String codeBarre = queryOutput.getString("code_barres");
                String nomProduit = queryOutput.getString("nom_produit");
                String dosage = queryOutput.getString("dosage");
                String description = queryOutput.getString("description");
                String fournisseur = queryOutput.getString("fournisseur");
                double prixUnitaire = queryOutput.getDouble("prix");
                String categorie = queryOutput.getString("categorie_produit");
                String instructions = queryOutput.getString("instructions_utilisation");
                int quantiteTotale = queryOutput.getInt("quantite_totale");

                // Création de l'objet MedicamentStock avec toutes les informations nécessaires
                MedicamentStock produit = new MedicamentStock(codeBarre, nomProduit, dosage, description, prixUnitaire, categorie, quantiteTotale, fournisseur, instructions);
                MedicamentStockObservableList.add(produit);
            }

            // Configuration des colonnes de la table
            code_barre_tableColumn.setCellValueFactory(new PropertyValueFactory<>("codeBarre"));
            nomMedicament_tableColumn.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
            description_column.setCellValueFactory(new PropertyValueFactory<>("description"));
            Prix_tableColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
            categorieProduit.setCellValueFactory(new PropertyValueFactory<>("categorie"));
            Quantite_tableColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
            Dosage_tableColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));
            instructionColumn.setCellValueFactory(new PropertyValueFactory<>("instructions"));

            // Assignation des données à la table
            TableMedicament.setItems(MedicamentStockObservableList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'erreur de récupération des données de la base de données
        }
    }*/

    public void loadMedicamentStockData() {
        // Requête SQL pour obtenir tous les produits et leur quantité totale en stock
        String medicamentViewQuery =
                "SELECT p.code_barres, p.nom_produit, p.dosage, p.description, p.prix, p.categorie_produit, p.fournisseur, p.instructions_utilisation, " +
                        "COALESCE(SUM(s.quantite), 0) AS quantite_totale " +
                        "FROM Produits p " +
                        "LEFT JOIN Stocks s ON p.code_barres = s.code_barres " +
                        "GROUP BY p.code_barres, p.nom_produit, p.dosage, p.description, p.prix, p.categorie_produit, p.fournisseur, p.instructions_utilisation";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(medicamentViewQuery);

            MedicamentStockObservableList.clear(); // Assurer que la liste est vide avant de la remplir

            while (queryOutput.next()) {
                String codeBarre = queryOutput.getString("code_barres");
                String nomProduit = queryOutput.getString("nom_produit");
                String dosage = queryOutput.getString("dosage");
                String description = queryOutput.getString("description");
                String fournisseur = queryOutput.getString("fournisseur");
                double prixUnitaire = queryOutput.getDouble("prix");
                String categorie = queryOutput.getString("categorie_produit");
                String instructions = queryOutput.getString("instructions_utilisation");
                int quantite = queryOutput.getInt("quantite_totale");

                MedicamentStock produit = new MedicamentStock(codeBarre, nomProduit, dosage, description, prixUnitaire, categorie, quantite, fournisseur, instructions);
                MedicamentStockObservableList.add(produit);
            }

            code_barre_tableColumn.setCellValueFactory(new PropertyValueFactory<>("codeBarre"));
            nomMedicament_tableColumn.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
            description_column.setCellValueFactory(new PropertyValueFactory<>("description"));
            Prix_tableColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
            categorieProduit.setCellValueFactory(new PropertyValueFactory<>("categorie"));
            Quantite_tableColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
            Dosage_tableColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));
            instructionColumn.setCellValueFactory(new PropertyValueFactory<>("instructions"));

            TableMedicament.setItems(MedicamentStockObservableList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'erreur de récupération des données de la base de données
        }
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
            keyWordTextField.clear();
        });


    }

    private void updateClearButtonVisibility(String text) {
        clearButton.setVisible(!text.isEmpty());
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {




        // Définissez la couleur de fond pour toutes les entêtes de colonne
        //String headerColorStyle = "-fx-background-color: #C9F4AA;";
        //for (TableColumn<MedicamentSearch, ?> column : TableMedicament.getColumns()) {
           // column.setStyle(headerColorStyle);
        //}

        // Set the font for the TableView, Application des styles CSS pout la couleur de la tableview
        //TableMedicament.setStyle("-fx-font-family: 'Courier New'; -fx-base: rgb(158, 152, 69);");






        clearButton.setVisible(false);
        setBouton();





        // Load the image for AjouterBtn into the existing ImageView (imgAjouter)
        String absolutePath1 = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Plus.png").toUri().toString();
        Image imageAjouter = new Image(absolutePath1);

        String absolutePath2 = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Remove.png").toUri().toString();
        Image image_A_Supprimer = new Image(absolutePath2);

        String absolutePath3 = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Edit_Product.png").toUri().toString();
        Image image_A_Modifier = new Image(absolutePath3);


        // Ajustez la taille de l'ImageView ici
        imgAjouter.setImage(imageAjouter);
        imgAjouter.setFitWidth(20); // Réglez la largeur souhaitée
        imgAjouter.setPreserveRatio(true); // Garantit que l'aspect ratio de l'image est conservé (le rapport largeur/hauteur)


        imgSupprimer.setImage(image_A_Supprimer);
        imgSupprimer.setFitWidth(20); // Réglez la largeur souhaitée
        imgSupprimer.setPreserveRatio(true); // Garantit que l'aspect ratio de l'image est conservé (le rapport largeur/hauteur)


        imgModifier.setImage(image_A_Modifier);
        imgModifier.setFitWidth(20); // Réglez la largeur souhaitée
        imgModifier.setPreserveRatio(true); // Garantit que l'aspect ratio de l'image est conservé (le rapport largeur/hauteur)


        btnSupprimer.setGraphic(imgSupprimer);
        AjouterBtn.setGraphic(imgAjouter);
        boutonModifier.setGraphic(imgModifier);


        //   ICI C'EST POUR INITIALISER LES BOUTONS SUPPRIMER ET MODIFIER A L'ETAT INACTIF
        btnSupprimer.setDisable(true);
        boutonModifier.setDisable(true);


        loadMedicamentStockData();




            // Vérifiez si un produit est déjà dans le panier
            for (MedicamentStock medicament : MedicamentStockObservableList) {
                if (isProductInCart(medicament)) {
                    showAlert(Alert.AlertType.WARNING, "Produit Déjà dans le Panier", "Le produit " + medicament.getNomProduit() + " est déjà dans le panier.");
                }
            }




            // Créez une liste filtrée liée à la liste observable des médicaments
            FilteredList<MedicamentStock> filteredList = new FilteredList<>(MedicamentStockObservableList, p -> true);

            // Liez le predicat du FilteredList à la propriété text du TextField
            keyWordTextField.textProperty().addListener((observable, oldValue, newValue) ->
                    filteredList.setPredicate(medicament -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // Affichez tous les éléments si le champ de texte est vide
                        }

                        // Convertissez la recherche en minuscules et vérifiez si elle correspond à certains champs du médicament
                        String lowerCaseFilter = newValue.toLowerCase();
                        updateClearButtonVisibility(newValue);
                        return medicament.getNomProduit().toLowerCase().contains(lowerCaseFilter)
                                //|| medicament.getDescription().toLowerCase().contains(lowerCaseFilter)
                                || medicament.getDosage().toLowerCase().contains(lowerCaseFilter);
                    }));


            // ICI C'EST LE FILTRE DU CHECKBOX POUR LES PRODUITS DONT LA QUANTITE EST <= 10
            // Ajoutez un écouteur à la propriété selected de la CheckBox
            checkBoxFiltrer.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    // Si la CheckBox est cochée, filtrez les médicaments dont la quantité est inférieure ou égale à 10
                    filteredList.setPredicate(medicament -> medicament.getQuantite() <= 10);
                } else {
                    // Si la CheckBox est décochée, affichez tous les médicaments
                    filteredList.setPredicate(medicament -> true);
                }
            });



            // ICI C'EST LE FILTRE DU CHECKBOX POUR AFFICHER LES PRODUITS DONT LA DATE D'EXPIRATION EST <= 7
            checkBoxFiltrerDate.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    // Si la CheckBox est cochée, filtrez les médicaments dont la date d'expiration est inférieure à 7 jours
                    filteredList.setPredicate(medicament -> {
                        Date currentDate = new Date();
                        long differenceInDays = (medicament.getDateExpiration().getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);
                        return differenceInDays <= 7;
                    });
                } else {
                    // Si la CheckBox est décochée, affichez tous les médicaments
                    filteredList.setPredicate(medicament -> true);
                }
            });





            // Créez une liste triée liée à la liste filtrée
            SortedList<MedicamentStock> sortedList = new SortedList<>(filteredList);

            // Liez la liste triée à la TableView
            TableMedicament.setItems(sortedList);

            // Définissez le comparateur pour trier la liste
            sortedList.comparatorProperty().bind(TableMedicament.comparatorProperty());






            // Personnalisation de l'apparence des cellules de la colonne "Quantité"
            // avec une couleur personnalisee. "ROUGE" si la quantite est <= 3
            Quantite_tableColumn.setCellFactory(column -> {
                return new TableCell<MedicamentStock, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item.toString());
                            setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule

                            if (item <= 3) {
                                // Si la quantité est inférieure ou égale à 3, définissez la couleur de fond en rouge
                                setTextFill(Color.WHITE);
                                setStyle("-fx-background-color: red; -fx-font-size: 14; -fx-font-weight: bold;");
                                setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                            } else if (item <= 10) {
                                // Si la quantité est inférieure ou égale à 10, définissez la couleur de fond en jaune
                                setTextFill(Color.BLACK); // Changez la couleur du texte en noir par exemple
                                setStyle("-fx-background-color: rgb(236, 196, 109);; -fx-font-size: 14; -fx-font-weight: bold;");
                                setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                            } else {
                                // Sinon, la couleur de fond est transparente
                                setStyle("-fx-text-fill: black;  -fx-font-size: 14; -fx-font-weight: bold;");

                            }

                        }
                    }
                };
            });






            /*
             *
             *      ICI C'EST LA FIN DE LA METHODE QUI PERMET DE CHANGER LE BACKGROUND-COLOR DES PRODUITS
             *              DONT LE STOCK SONT <= 3
             *
             *
             * */

            /*
             *
             *       ICI JE VAIS FAIRE UNE METHODE QUI VA ACTIVE OU DESACTIVE LES BOUTTON SUPPRIMER ET MODIFIER
             *             LORSQUE L'ON CLIQUE SUR LE TABLEAU
             *
             *  */



            // Écouteur pour surveiller les changements dans la sélection de la tableView
            TableMedicament.getSelectionModel().getSelectedItems().addListener((ListChangeListener<MedicamentStock>) c -> {
                if (c.getList().isEmpty()) {
                    // Aucun élément sélectionné, désactiver les boutons
                    btnSupprimer.setDisable(true);
                    boutonModifier.setDisable(true);
                } else {
                    // Au moins un élément sélectionné, activer les boutons
                    btnSupprimer.setDisable(false);
                    boutonModifier.setDisable(false);
                }
            });

            /*
             *
             *   ICI JE VEUX METRRE DES METHODES LORSQUE JE CLIQUE SUR UN BOUTON SA AFFICHE LA VUE CORRESPONDANTE
             * */





            // Créez une cellFactory pour la colonne "Action"
            TableColumn<MedicamentStock, Void> actionColumn = Action_tableColumn;
            actionColumn.setCellFactory(new Callback<>() {
                @Override
                public TableCell<MedicamentStock, Void> call(final TableColumn<MedicamentStock, Void> param) {
                    return new TableCell<>() {
                        private final HBox container = new HBox(); // Utilisez un conteneur HBox pour afficher plusieurs éléments horizontalement
                        private final Button actionButton = new Button();


                        // Créer un bouton pour l'icône d'alerte
                        private final Button alertButton = new Button();

                        String absolutePath1 = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Add_Shopping_Cart.png").toUri().toString();
                        String absolutePath2 = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/reaprovisionnementIcon.png").toUri().toString();
                        private final ImageView cartIcon = new ImageView(new Image(absolutePath1));
                        private final ImageView alertIcon = new ImageView(new Image(absolutePath2)); // Remplacez "chemin_vers_votre_icone.png" par le chemin réel de votre icône


                        {

                            // Ajustez la taille des ImageView ici
                            cartIcon.setFitWidth(16);
                            cartIcon.setPreserveRatio(true);
                            alertIcon.setFitWidth(16);
                            alertIcon.setPreserveRatio(true);

                            actionButton.setGraphic(cartIcon);

                            // Créer une info-bulle avec un délai d'affichage court (par exemple, 200 millisecondes)
                            Tooltip tooltip = new Tooltip("AJOUTER AU PANIER");
                            tooltip.setShowDelay(Duration.millis(200)); // Réglez le délai d'affichage ici

                            Tooltip.install(actionButton, tooltip);

                            // Ajoutez de l'espace entre les icônes
                            container.setSpacing(15); // Vous pouvez ajuster la valeur selon vos préférences



                            alertButton.setGraphic(alertIcon);

                            // Créer une info-bulle pour le bouton d'alerte
                            Tooltip alertTooltip = new Tooltip("REAPPROVISIONNER");
                            alertTooltip.setShowDelay(Duration.millis(200)); // Réglez le délai d'affichage ici

                            Tooltip.install(alertButton, alertTooltip);


                            alertButton.setOnAction(event -> {
                                // Code à exécuter lors du clic sur le bouton d'alerte
                                // Vous pouvez mettre ici le code pour le réapprovisionnement

                                System.out.println("reapprovisionnement reussi...");
                            });

                            container.getChildren().addAll(alertButton, actionButton);

                            // Centrez les éléments horizontalement dans la HBox
                            container.setAlignment(Pos.CENTER);

                            actionButton.setOnAction(event -> {
                                // Code à exécuter lors du clic sur le bouton dans la cellule
                                // Vous pouvez accéder aux données de la ligne actuelle avec getItem()
                                MedicamentStock selectedMedicament = getTableView().getItems().get(getIndex());


                                // Vérifiez si la quantité en stock est égale à 0
                                if (selectedMedicament.getQuantite() == 0) {

                                    // Émettre un bip sonore
                                    Toolkit.getDefaultToolkit().beep();
                                    showAlert(Alert.AlertType.WARNING, "Quantité Insuffisante", "Le produit est en rupture de stock.");

                                } else {

                                    // Vérifiez si le produit est déjà dans le panier
                                    if (isProductInCart(selectedMedicament)) {

                                        // Émettre un bip sonore
                                        Toolkit.getDefaultToolkit().beep();
                                        // Produit déjà dans le panier, affichez une alerte
                                        showAlert(Alert.AlertType.WARNING, "ERREUR AJOUT AU PANIER", "Le produit est déjà dans le panier.");
                                    } else {

                                        int quantiteChoisie = 1;
                                        Double totIndividuel = selectedMedicament.getPrixUnitaire() * quantiteChoisie;
                                        PanierItem panierItem = new PanierItem(selectedMedicament, quantiteChoisie, totIndividuel);

                                        // Vérifiez si le produit est déjà dans le panierItems
                                        if (isProductInPanierItems(panierItem)) {
                                            // Émettre un bip sonore
                                            Toolkit.getDefaultToolkit().beep();
                                            // Produit déjà dans le panierItems, affichez une alerte
                                            showAlert(Alert.AlertType.WARNING, "ERREUR AJOUT AU PANIER", "Le produit est déjà dans le panier.");
                                        } else {
                                            panier.add(panierItem);
                                            // Affichez un message de notification
                                            String message = "Produit ajouté au panier : " + selectedMedicament.getNomProduit();
                                            showAlert(Alert.AlertType.INFORMATION, "PRODUIT AJOUTE :", message);
                                            // Faites quelque chose avec l'objet de données, par exemple, mettez à jour l'interface utilisateur
                                            // ou effectuez d'autres actions liées à l'ajout du produit au panier
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        protected void updateItem(Void item, boolean empty) {


                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);

                            } else {



                                setGraphic(container);
                                // Ajoutez des marges intérieures à la cellule
                                setPadding(new Insets(3)); // Définissez les marges intérieures souhaitées ici
                                // Ajoutez un style CSS pour centrer le contenu de la cellule
                                setAlignment(Pos.CENTER);

                                MedicamentStock selectedMedicament = getTableView().getItems().get(getIndex());

                                // Mettez à jour l'icône et l'état du bouton en fonction de la quantité en stock
                                if (selectedMedicament.getQuantite() <= 10) {
                                    alertButton.setVisible(true);

                                    if (selectedMedicament.getQuantite() == 0) {
                                        actionButton.setStyle("-fx-opacity: 0.3;");
                                        actionButton.setDisable(true);
                                    } else {
                                        actionButton.setStyle("");
                                        actionButton.setDisable(false);
                                    }
                                } else {
                                    alertButton.setVisible(false);
                                    actionButton.setStyle("");
                                    actionButton.setDisable(false);
                                }

                            }
                        }
                    };
                }
            });












            // voici la methode pour ajouter CFA a la fin du prix dans la colonne prixColumn
            Prix_tableColumn.setCellFactory(col -> new TableCell<MedicamentStock, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Display the price with the symbol "FCFA"
                        setText(String.format("%.2f", item) + " FCFA");
                        //setStyle("-fx-alignment: CENTER; -fx-text-fill: green; -fx-font-size: 14; -fx-font-weight: bold;"); // Centrer le texte
                    }
                }
            });








            Dosage_tableColumn.setCellFactory(new Callback<TableColumn<MedicamentStock, String>, TableCell<MedicamentStock, String>>() {
                @Override
                public TableCell<MedicamentStock, String> call(TableColumn<MedicamentStock, String> param) {
                    return new TableCell<MedicamentStock, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                                setStyle("");
                            } else {
                                setText(item);
                               // setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                                //setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
                            }
                        }
                    };
                }
            });






            nomMedicament_tableColumn.setCellFactory(new Callback<TableColumn<MedicamentStock, String>, TableCell<MedicamentStock, String>>() {
                @Override
                public TableCell<MedicamentStock, String> call(TableColumn<MedicamentStock, String> param) {
                    return new TableCell<MedicamentStock, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                                setStyle("");
                            } else {
                                // Définir la limite de caractères pour passer à la ligne
                                int limit = 25; // Vous pouvez ajuster cette valeur en fonction de vos besoins

                                if (item.length() > limit) {
                                    // Si la chaîne est trop longue, insérer des retours à la ligne
                                    setText(item.substring(0, limit) + "\n" + item.substring(limit));
                                } else {
                                    setText(item);
                                }
                                //setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                               // setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: black;");
                            }
                        }
                    };
                }
            });

            categorieProduit.setCellFactory(new Callback<TableColumn<MedicamentStock, String>, TableCell<MedicamentStock, String>>() {
                @Override
                public TableCell<MedicamentStock, String> call(TableColumn<MedicamentStock, String> param) {
                    return new TableCell<MedicamentStock, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                                setStyle("");
                            } else {


                               // setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                                setText(item);

                                //setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                                //setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: black;");
                            }
                        }
                    };
                }
            });



            // Définissez une cell factory pour la colonne ID_Medicament_tableColumn
            code_barre_tableColumn.setCellFactory(new Callback<TableColumn<MedicamentStock, String>, TableCell<MedicamentStock, String>>() {
                @Override
                public TableCell<MedicamentStock, String> call(TableColumn<MedicamentStock, String> param) {
                    return new TableCell<MedicamentStock, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);

                            // Assurez-vous que la cellule n'est pas vide
                            if (item == null || empty) {
                                setText(null);
                                setStyle(""); // Réinitialisez le style de la cellule
                            } else {
                                setText(String.valueOf(item));

                                setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule

                                // Définissez la couleur de fond de la cellule
                               // setStyle("-fx-background-color: #7C96AB; -fx-font-size: 16; -fx-font-weight: bold;");
                            }
                        }
                    };
                }
            });








    }
}
