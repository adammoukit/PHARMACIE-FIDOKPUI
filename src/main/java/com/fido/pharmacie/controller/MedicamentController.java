package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import com.fido.pharmacie.model.PanierItem;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Toolkit;

import static com.fido.pharmacie.controller.DatabaseConnection.showAlert;

public class MedicamentController implements Initializable{

    @FXML
    private TableView<MedicamentSearch> TableMedicament;
    @FXML
    private TableColumn<MedicamentSearch, Integer> ID_Medicament_tableColumn;
    @FXML
    private TableColumn<MedicamentSearch, String> nomMedicament_tableColumn;
    @FXML
    private TableColumn<MedicamentSearch, String> Description_tableColumn;
    @FXML
    private TableColumn<MedicamentSearch, String> Dosage_tableColumn;
    @FXML
    private TableColumn<MedicamentSearch, Double> Prix_tableColumn;
    @FXML
    private TableColumn<MedicamentSearch, Date> DateExpiration_tableColumn;
    @FXML
    private TableColumn<MedicamentSearch, Integer> Quantite_tableColumn;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button boutonModifier;

    @FXML
    private TableColumn<MedicamentSearch, Void> Action_tableColumn;


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







    public void handleAddButtonAction() {
        try {
            // Charger le fichier FXML du dialogue d'ajout de produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/AddProductDialog.fxml"));
            Dialog<MedicamentSearch> dialog = new Dialog<>();
            dialog.getDialogPane().setContent(loader.load());

            // Configurer les boutons du dialogue (Ajouter et Annuler)
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Définir le titre du dialogue modal
            dialog.setTitle("Ajouter un Produit");

            // Récupérer le contrôleur du dialogue
            AddProductDialogController dialogController = loader.getController();

            // Récupérer les résultats du dialogue lorsque l'utilisateur clique sur "Ajouter"
            dialog.setResultConverter(new Callback<ButtonType, MedicamentSearch>() {
                @Override
                public MedicamentSearch call(ButtonType buttonType) {
                    if (buttonType == ButtonType.OK) {
                        // L'utilisateur a cliqué sur "Ajouter", récupérez les données du dialogue
                        return dialogController.getAddProductData();
                    }
                    return null;
                }
            });


            // Récupérer la fenêtre du dialogue et définir l'icône
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            String absolutePath = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Plus.png").toUri().toString();
            stage.getIcons().add(new Image(absolutePath));


            // Afficher le dialogue et attendre que l'utilisateur agisse
            Optional<MedicamentSearch> result = dialog.showAndWait();

            if (result.isPresent()) {

                MedicamentSearch medicament = result.get();
                if (medicament != null) {
                    // L'utilisateur a cliqué sur "OK" et les données sont valides
                    // Faites quelque chose avec l'objet MedicamentSearch, par exemple, l'ajouter à une liste ou à une base de données


                    // Update the observable list
                    MedicamentSearchObservableList.add(medicament);

                    // Refresh the TableView
                    TableMedicament.setItems(null);
                    TableMedicament.setItems(MedicamentSearchObservableList);



                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Produit Ajouté");
                    alert.setHeaderText(null);
                    alert.setContentText("Le produit a été ajouté à la base de données avec succès.");
                    alert.showAndWait();
                } else {
                    // L'utilisateur a cliqué sur "OK" mais les données ne sont pas valides (des champs sont vides)
                    // Aucune action requise ici, l'alerte a déjà été affichée dans getAddProductData()
                }

            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement du dialogue ici
        }
    }





    // Méthode pour vérifier si un produit est déjà dans le panier
    private boolean isProductInCart(MedicamentSearch product) {
        for (PanierItem item : panier) {
            if (item.getMedicament().equals(product)) {
                return true;
            }
        }
        return false;
    }






    // Méthode pour vérifier si un produit est déjà dans le panierItems
    private boolean isProductInPanierItems(PanierItem panierItem) {
        for (PanierItem item : panier) {
            if (item.getMedicament().getID() == panierItem.getMedicament().getID()) {
                return true; // Le produit est déjà dans le panierItems
            }
        }
        return false; // Le produit n'est pas dans le panierItems
    }








    ObservableList<MedicamentSearch> MedicamentSearchObservableList = FXCollections.observableArrayList();



    //DECLARATION DE L'OBJET DU PANIER
    public static List<PanierItem> panier = new ArrayList<PanierItem>();

    public TableColumn<MedicamentSearch, Double> getPrix_tableColumn() {
        return Prix_tableColumn;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {





        // Set the font for the TableView, Application des styles CSS pout la couleur de la tableview
        //TableMedicament.setStyle("-fx-font-family: 'Courier New'; -fx-base: rgb(158, 152, 69);");

        TableMedicament.setStyle("-fx-font-family: 'Courier New';");




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



        Connection connectDB = DatabaseConnection.getConnection();

        String medicamenViewQuery = "SELECT ID, NOM_MEDICAMENT, DESCRIPTION, DOSAGE, PRIX, DATE_EXPIRATION, QUANTITE FROM medicament ";

        //   ICI C'EST POUR INITIALISER LES BOUTONS SUPPRIMER ET MODIFIER A L'ETAT INACTIF
        btnSupprimer.setDisable(true);
        boutonModifier.setDisable(true);

        try {
            Statement statement = connectDB.createStatement();
            ResultSet Queryoutput = statement.executeQuery(medicamenViewQuery);

            while (Queryoutput.next()){
                Integer queryIdMedicament = Queryoutput.getInt("ID");
                String  queryNomMedicament = Queryoutput.getString("NOM_MEDICAMENT");
                String  queryDescription = Queryoutput.getString("DESCRIPTION");
                String  queryDosage = Queryoutput.getString("DOSAGE");
                Double  queryPrix = Queryoutput.getDouble("PRIX");
                Date  queryDateExpiration = Queryoutput.getDate("DATE_EXPIRATION");
                Integer queryQuantite = Queryoutput.getInt("QUANTITE");
                //Integer queryIdFournisseur = Queryoutput.getInt("id_produitF");


                //remplir la liste observable
               // MedicamentSearchObservableList.add(new MedicamentSearch(queryIdMedicament, queryNomMedicament, queryDescription, queryDosage, queryPrix, queryDateExpiration, queryQuantite));

                MedicamentSearch medicament = new MedicamentSearch(queryIdMedicament, queryNomMedicament, queryDescription, queryDosage, queryPrix, queryDateExpiration, queryQuantite);
                MedicamentSearchObservableList.add(medicament);

            }

            ID_Medicament_tableColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
            nomMedicament_tableColumn.setCellValueFactory(new PropertyValueFactory<>("Nom_medicament"));
            Description_tableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            Dosage_tableColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));
            Prix_tableColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
            DateExpiration_tableColumn.setCellValueFactory(new PropertyValueFactory<>("date_expiration"));
            Quantite_tableColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));

            TableMedicament.setItems(MedicamentSearchObservableList);


            // Vérifiez si un produit est déjà dans le panier
            for (MedicamentSearch medicament : MedicamentSearchObservableList) {
                if (isProductInCart(medicament)) {
                    showAlert(Alert.AlertType.WARNING, "Produit Déjà dans le Panier", "Le produit " + medicament.getNom_medicament() + " est déjà dans le panier.");
                }
            }




            // Créez une liste filtrée liée à la liste observable des médicaments
            FilteredList<MedicamentSearch> filteredList = new FilteredList<>(MedicamentSearchObservableList, p -> true);

            // Liez le predicat du FilteredList à la propriété text du TextField
            keyWordTextField.textProperty().addListener((observable, oldValue, newValue) ->
                    filteredList.setPredicate(medicament -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // Affichez tous les éléments si le champ de texte est vide
                        }

                        // Convertissez la recherche en minuscules et vérifiez si elle correspond à certains champs du médicament
                        String lowerCaseFilter = newValue.toLowerCase();
                        return medicament.getNom_medicament().toLowerCase().contains(lowerCaseFilter)
                                || medicament.getDescription().toLowerCase().contains(lowerCaseFilter)
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
                        long differenceInDays = (medicament.getDate_expiration().getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);
                        return differenceInDays <= 7;
                    });
                } else {
                    // Si la CheckBox est décochée, affichez tous les médicaments
                    filteredList.setPredicate(medicament -> true);
                }
            });





            // Créez une liste triée liée à la liste filtrée
            SortedList<MedicamentSearch> sortedList = new SortedList<>(filteredList);

            // Liez la liste triée à la TableView
            TableMedicament.setItems(sortedList);

            // Définissez le comparateur pour trier la liste
            sortedList.comparatorProperty().bind(TableMedicament.comparatorProperty());






            // Personnalisation de l'apparence des cellules de la colonne "Quantité"
            // avec une couleur personnalisee. "ROUGE" si la quantite est <= 3
            Quantite_tableColumn.setCellFactory(column -> {
                return new TableCell<MedicamentSearch, Integer>() {
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
                                setStyle("-fx-background-color: yellow; -fx-font-size: 14; -fx-font-weight: bold;");
                                setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                            } else {
                                // Sinon, la couleur de fond est transparente
                                setStyle("-fx-text-fill: black; -fx-background-color: white; -fx-font-size: 14; -fx-font-weight: bold;");

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
             *       ICI JE VAIS FAIRE UNE METHODE QUI VA ACTIVE OU DESACTION LES BOUTTON SUPPRIMER ET MODIFIER
             *             LORSQUE L'ON CLIQUE SUR LE TABLEAU
             *
             *  */



            // Écouteur pour surveiller les changements dans la sélection de la tableView
            TableMedicament.getSelectionModel().getSelectedItems().addListener((ListChangeListener<MedicamentSearch>) c -> {
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



            boutonModifier.setOnAction(event -> {
               // Récupérez l'élément sélectionné dans TableView
                MedicamentSearch selectedItem = TableMedicament.getSelectionModel().getSelectedItem();


                if (selectedItem != null) {


                    // Créez une nouvelle boîte de dialogue avec les boutons "Ajouter" et "Annuler"
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.initModality(Modality.WINDOW_MODAL);
                    dialog.initOwner(boutonModifier.getScene().getWindow());
                    dialog.setTitle("Modifier un Produit");

                    // Chargez le fichier FXML de la boîte de dialogue de modification
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/EditProductDialog.fxml"));
                    Parent root;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                    // Obtenez le contrôleur de la boîte de dialogue
                    EditProductDialogController dialogController = loader.getController();





                    // Initialisez les champs de la boîte de dialogue avec les valeurs de l'élément sélectionné
                    dialogController.initData(selectedItem);

                    // Définissez le contenu de la boîte de dialogue
                    dialog.getDialogPane().setContent(root);

                    // Ajoutez les boutons "Ajouter" et "Annuler" à la boîte de dialogue
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                    // Obtenez les boutons "Ajouter" et "Annuler" pour y appliquer des actions si nécessaire
                    Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                    Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);

                     // Ajoutez des écouteurs d'événements aux boutons si nécessaire
                    okButton.setOnAction(evt -> {
                        // Logique pour le bouton "Ajouter" si nécessaire
                        // Par exemple, dialogController.processAdd();
                        // Appeler la méthode de mise à jour du contrôleur de boîte de dialogue


                        // Appeler la méthode de mise à jour du contrôleur de boîte de dialogue
                        //dialogController.processUpdate();


                        // Récupérez les données mises à jour du dialogue
                        MedicamentSearch updatedMedicament = dialogController.processUpdate();

                        // Mettez à jour l'objet sélectionné dans la liste observable
                        int selectedIndex = MedicamentSearchObservableList.indexOf(selectedItem);
                        if (selectedIndex != -1) {
                            MedicamentSearchObservableList.set(selectedIndex, updatedMedicament);


                            // Rafraîchissez la TableView
                            TableMedicament.setItems(null);
                            TableMedicament.setItems(MedicamentSearchObservableList);


                        }



                       // dialog.close();



                    });

                    cancelButton.setOnAction(evt -> {
                        // Logique pour le bouton "Annuler" si nécessaire
                        // Par exemple, dialogController.cancelAdd();
                        dialog.close();
                    });

                    // Affichez la fenêtre de dialogue et attendez jusqu'à ce qu'elle soit fermée
                    Optional<ButtonType> result = dialog.showAndWait();


                }
            });



            // Créez une cellFactory pour la colonne "Action"
            TableColumn<MedicamentSearch, Void> actionColumn = Action_tableColumn;
            actionColumn.setCellFactory(new Callback<>() {
                @Override
                public TableCell<MedicamentSearch, Void> call(final TableColumn<MedicamentSearch, Void> param) {
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
                                MedicamentSearch selectedMedicament = getTableView().getItems().get(getIndex());


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
                                        Double totIndividuel = selectedMedicament.getPrix() * quantiteChoisie;
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
                                            String message = "Produit ajouté au panier : " + selectedMedicament.getNom_medicament();
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

                                MedicamentSearch selectedMedicament = getTableView().getItems().get(getIndex());

                                // Mettez à jour l'icône et l'état du bouton en fonction de la quantité en stock
                                if (selectedMedicament.getQuantite() <= 10) {
                                    alertButton.setVisible(true);

                                    if (selectedMedicament.getQuantite() == 0) {
                                        actionButton.setStyle("-fx-opacity: 0.5;");
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
            Prix_tableColumn.setCellFactory(col -> new TableCell<MedicamentSearch, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Display the price with the symbol "FCFA"
                        setText(String.format("%.2f", item) + " FCFA");
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: green; -fx-font-size: 14; -fx-font-weight: bold;"); // Centrer le texte
                    }
                }
            });




            DateExpiration_tableColumn.setCellFactory(new Callback<TableColumn<MedicamentSearch, Date>, TableCell<MedicamentSearch, Date>>() {
                @Override
                public TableCell<MedicamentSearch, Date> call(TableColumn<MedicamentSearch, Date> param) {
                    return new TableCell<MedicamentSearch, Date>() {
                        @Override
                        protected void updateItem(Date item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                                setStyle("");
                            } else {
                                setText(item.toString()); // Assurez-vous d'avoir une représentation lisible de la date ici
                                setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                                setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
                            }
                        }
                    };
                }
            });



            Dosage_tableColumn.setCellFactory(new Callback<TableColumn<MedicamentSearch, String>, TableCell<MedicamentSearch, String>>() {
                @Override
                public TableCell<MedicamentSearch, String> call(TableColumn<MedicamentSearch, String> param) {
                    return new TableCell<MedicamentSearch, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                                setStyle("");
                            } else {
                                setText(item);
                                setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                                setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
                            }
                        }
                    };
                }
            });


            Description_tableColumn.setCellFactory(new Callback<TableColumn<MedicamentSearch, String>, TableCell<MedicamentSearch, String>>() {
                @Override
                public TableCell<MedicamentSearch, String> call(TableColumn<MedicamentSearch, String> param) {
                    return new TableCell<MedicamentSearch, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                                setStyle("");
                            } else {
                                // Définir la limite de caractères pour passer à la ligne
                                int limit = 30; // Vous pouvez ajuster cette valeur en fonction de vos besoins

                                if (item.length() > limit) {
                                    // Si la chaîne est trop longue, insérer des retours à la ligne
                                    setText(item.substring(0, limit) + "\n" + item.substring(limit));
                                } else {
                                    setText(item);
                                }

                                //setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                                setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
                            }
                        }
                    };
                }
            });




            nomMedicament_tableColumn.setCellFactory(new Callback<TableColumn<MedicamentSearch, String>, TableCell<MedicamentSearch, String>>() {
                @Override
                public TableCell<MedicamentSearch, String> call(TableColumn<MedicamentSearch, String> param) {
                    return new TableCell<MedicamentSearch, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                                setStyle("");
                            } else {
                                setText(item);
                                //setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                                setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: green;");
                            }
                        }
                    };
                }
            });





        }catch (SQLException e){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }

    }
}
