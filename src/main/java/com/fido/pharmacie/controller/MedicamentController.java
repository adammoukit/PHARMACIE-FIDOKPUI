package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import com.fido.pharmacie.model.PanierItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    ObservableList<MedicamentSearch> MedicamentSearchObservableList = FXCollections.observableArrayList();



    //DECLARATION DE L'OBJET DU PANIER
    public static List<PanierItem> panier = new ArrayList<PanierItem>();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Set the font for the TableView
        TableMedicament.setStyle("-fx-font-family: 'Courier New';");


        Connection connectDB = DatabaseConnection.getConnection();

        String medicamenViewQuery = "SELECT ID, NOM_MEDICAMENT, DESCRIPTION, DOSAGE, PRIX, DATE_EXPIRATION, QUANTITE, id_fournisseur FROM medicament ";

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
                Integer queryIdFournisseur = Queryoutput.getInt("id_fournisseur");


                //remplir la liste observable

                MedicamentSearchObservableList.add(new MedicamentSearch(queryIdMedicament, queryNomMedicament, queryDescription, queryDosage, queryPrix, queryDateExpiration, queryQuantite, queryIdFournisseur));


            }

            ID_Medicament_tableColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
            nomMedicament_tableColumn.setCellValueFactory(new PropertyValueFactory<>("Nom_medicament"));
            Description_tableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            Dosage_tableColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));
            Prix_tableColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
            DateExpiration_tableColumn.setCellValueFactory(new PropertyValueFactory<>("date_expiration"));
            Quantite_tableColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));

            TableMedicament.setItems(MedicamentSearchObservableList);





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
                                setStyle("-fx-background-color: red;");
                                setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                            } else if (item <= 10) {
                                // Si la quantité est inférieure ou égale à 10, définissez la couleur de fond en jaune
                                setTextFill(Color.BLACK); // Changez la couleur du texte en noir par exemple
                                setStyle("-fx-background-color: yellow;");
                                setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
                            } else {
                                // Sinon, la couleur de fond est transparente
                                setStyle("-fx-background-color: transparent;");
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
                // Récupérez l'élément sélectionné dans la TableView
                //MedicamentSearch selectedMedicament = TableMedicament.getSelectionModel().getSelectedItem();

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
                        dialogController.processUpdate();

                        dialog.close();

                        TableMedicament.refresh();

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
                        private final Button actionButton = new Button();
                        private final ImageView imageView = new ImageView(new Image("file:/C:/Users/DELL/IdeaProjects/Pharmacie/src/main/resources/Image/Add_Shopping_Cart.png"));


                        {

                            // Ajustez la taille de l'ImageView ici
                            imageView.setFitWidth(16); // Réglez la largeur souhaitée
                            imageView.setPreserveRatio(true); // Garantit que l'aspect ratio de l'image est conservé (le rapport largeur/hauteur)


                            actionButton.setGraphic(imageView);
                            actionButton.setOnAction(event -> {
                                // Code à exécuter lors du clic sur le bouton dans la cellule
                                // Vous pouvez accéder aux données de la ligne actuelle avec getItem()
                                MedicamentSearch objet = getTableView().getItems().get(getIndex());
                                // Faites quelque chose avec l'objet de données
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

                                actionButton.setOnAction(event -> {
                                    // Code à exécuter lors du clic sur le bouton dans la cellule
                                    // Vous pouvez accéder aux données de la ligne actuelle avec getItem()
                                    MedicamentSearch selectedMedicament = getTableView().getItems().get(getIndex());
                                    int quantiteChoisie = 1; // Quantité fixe à 1

                                    Double totIndividuel = selectedMedicament.getPrix() * quantiteChoisie;

                                    // Créez un objet PanierItem avec le produit sélectionné et la quantité fixe à 1
                                    PanierItem panierItem = new PanierItem(selectedMedicament, quantiteChoisie, totIndividuel);

                                    panier.add(panierItem);
                                    // Affichez un message de notification
                                    String message = "Produit ajouté au panier : " + selectedMedicament.getNom_medicament();
                                    showAlert(Alert.AlertType.INFORMATION, "Produit ajouté :", message);
                                    // Faites quelque chose avec l'objet de données, par exemple, mettez à jour l'interface utilisateur
                                    // ou effectuez d'autres actions liées à l'ajout du produit au panier
                                });
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
                        setStyle("-fx-alignment: CENTER;"); // Centrer le texte
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
                                setText(item);
                                setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
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
                                setAlignment(javafx.geometry.Pos.CENTER); // Centrer le texte dans la cellule
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
