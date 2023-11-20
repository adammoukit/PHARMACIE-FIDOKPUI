package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import com.fido.pharmacie.model.PanierItem;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;

import static com.fido.pharmacie.controller.DatabaseConnection.connection;
import static com.fido.pharmacie.controller.MedicamentController.panier;


public class PanierController implements Initializable {

    @FXML
    private TableView<PanierItem> panierTable;


    @FXML
    private ProgressIndicator loadingIcon;

    @FXML
    private TableColumn<PanierItem, Void> action_Column;

    @FXML
    private TableColumn<PanierItem, Date> dateColumn;

    @FXML
    private TableColumn<PanierItem, String> descriptionColumn;

    @FXML
    private TableColumn<PanierItem, String> dosageColumn;

    @FXML
    private TableColumn<PanierItem, Integer> idColumn;

    @FXML
    private TableColumn<PanierItem, String> nomColumn;

    @FXML
    private TableColumn<PanierItem, Double> prixColumn;

    @FXML
    private TableColumn<PanierItem, Integer> qteColumn;

    @FXML
    private TableColumn<PanierItem, Double> total_individuelColumn;

    @FXML
    private Label coutTotalLabel;

    @FXML
    private Button btnValider;



    // Ajoutez le code de reçu aléatoire
    String codeRecu = genererCodeRecu();


    // Ajoutez ceci au début de la méthode initialize
    private boolean isUpdating = false;


    private void showLoadingIcon() {
        loadingIcon.setVisible(true);

        loadingIcon.setManaged(true);

        // Center the loadingIcon
        loadingIcon.getParent().layout();
        double parentWidth = ((Pane) loadingIcon.getParent()).getWidth();
        double parentHeight = ((Pane) loadingIcon.getParent()).getHeight();
        double iconWidth = loadingIcon.getBoundsInLocal().getWidth();
        double iconHeight = loadingIcon.getBoundsInLocal().getHeight();
        double x = (parentWidth - iconWidth) / 2;
        double y = (parentHeight - iconHeight) / 2;

        loadingIcon.setLayoutX(x);
        loadingIcon.setLayoutY(y);

        // Increase the size of the loadingIcon
        loadingIcon.setScaleX(0.9);  // You can adjust the scale factor as needed
        loadingIcon.setScaleY(0.9);


    }

    private void hideLoadingIcon() {
        loadingIcon.setVisible(false);
    }

    private void updateTableAfterDelay() {
        new Thread(() -> {
            try {
                // Simulez une opération de mise à jour en cours
                Thread.sleep(1000);

                // Mettez à jour la table dans la file d'interface utilisateur
                Platform.runLater(() -> {
                    isUpdating = true;

                    // Update the TableView with items from the cart
                    ObservableList<PanierItem> panierItems = FXCollections.observableArrayList(panier);
                    panierTable.setItems(panierItems);

                    // Update total_individuelColumn based on the new quantities
                    for (PanierItem panierItem : panierItems) {
                        double total = panierItem.getMedicament().getPrix() * panierItem.getQte();
                        panierItem.setTotIndividuel(total);
                    }
                    hideLoadingIcon();
                    isUpdating = false;



                    // Recalculer le coût total du panier après la mise à jour
                    calculerCoutTotalPanier();



                    // Vérifiez si le panier est vide et désactivez le bouton en conséquence
                    btnValider.setDisable(panier.isEmpty());


                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private double calculerCoutTotalPanier() {
        double coutTotal = panier.stream()
                .mapToDouble(item -> item.getTotIndividuel())
                .sum();

        // Mettez à jour l'affichage du coût total dans le Label
        coutTotalLabel.setText(String.format("%.2f FCFA", coutTotal));
        return coutTotal;
    }



    /*@FXML
    private void handleValiderButtonAction(ActionEvent event) {
        // Générer le reçu avec les informations du panier
        String recu = genererRecu();

        // Afficher le reçu (vous pouvez définir votre propre méthode d'affichage)
        afficherRecu(recu);

        try {
            // Charger le fichier FXML du reçu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/Recu.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur du fichier FXML
            RecuController recuController = loader.getController();

            // Configurer le texte du reçu dans le contrôleur
            recuController.setRecuText(recu);

            // Créer une nouvelle scène pour afficher le reçu
            //Scene scene = new Scene(root, 60 * 8, 600); // 80mm * 8px/mm pour la largeur

            // Calculer la hauteur nécessaire en fonction du nombre de produits
            double neededHeight = calculateNeededHeight(panier.size());

            // Créer une nouvelle scène pour afficher le reçu
            Scene scene = new Scene(root, 60 * 8, neededHeight);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Reçu d'achat");
            stage.setScene(scene);

            // Afficher la fenêtre
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String genererRecu() {
        // Construire le reçu en utilisant les informations du panier
        StringBuilder recuBuilder = new StringBuilder();
        //recuBuilder.append("Recu d'achat :\n");

        // Ajouter les détails de chaque article dans le panier
        for (PanierItem panierItem : panier) {
            recuBuilder.append(String.format("Produit: %s\n", panierItem.getMedicament().getNom_medicament()));
            recuBuilder.append(String.format("Quantité: %d\n", panierItem.getQte()));
            recuBuilder.append(String.format("Prix unitaire: %.2f FCFA\n", panierItem.getMedicament().getPrix()));
            recuBuilder.append(String.format("Total individuel: %.2f FCFA\n", panierItem.getTotIndividuel()));
            recuBuilder.append("--------------------------");
            recuBuilder.append("\n");
        }

        // Ajouter le coût total

        recuBuilder.append("--------------------------");
        recuBuilder.append("\n");
        recuBuilder.append(String.format("Coût total: %.2f FCFA\n", getTotalCoutPanier()));
        recuBuilder.append("--------------------------");

        return recuBuilder.toString();
    }

    private double getTotalCoutPanier() {
        return panier.stream()
                .mapToDouble(item -> item.getTotIndividuel())
                .sum();
    }

    private void afficherRecu(String recu) {
        // Vous pouvez afficher le reçu de la manière que vous préférez,
        // par exemple, une boîte de dialogue, une nouvelle fenêtre, etc.
        System.out.println(recu);
        // Ajoutez ici le code pour afficher le reçu de manière appropriée dans votre application
    }


    private double calculateNeededHeight(int numberOfProducts) {
        // Ajustez cette valeur en fonction de la disposition de votre VBox
        double heightPerProduct = 30; // Hauteur approximative par produit

        // Ajoutez de l'espace supplémentaire pour les marges, etc., si nécessaire
        double additionalSpace = 50;

        return Math.min(numberOfProducts * heightPerProduct + additionalSpace, 600); // Limitez la hauteur à 600 (par exemple)
    }

     */


    private String genererCodeRecu() {
        // Générer un code de reçu aléatoire avec des lettres majuscules et des chiffres
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            char randomChar;
            if (random.nextBoolean()) {
                // Ajoutez une lettre majuscule
                randomChar = (char) (random.nextInt(26) + 'A');
            } else {
                // Ajoutez un chiffre
                randomChar = (char) (random.nextInt(10) + '0');
            }

            code.append(randomChar);
        }

        return code.toString();
    }


    private void handleValiderButton() {
        // Générer le reçu avec les informations des produits achetés
        genererRecu();

        // Vider la table du panier
        panier.clear();

        // Mettre à jour la TableView après avoir vidé le panier
        panierTable.getItems().clear();
        panierTable.refresh();

        btnValider.setDisable(true);
    }

    private void genererRecu() {
        // Logique pour générer le reçu
        // Vous pouvez utiliser une bibliothèque d'impression, comme JavaFX PrinterJob,
        // ou utiliser une bibliothèque externe pour l'impression, par exemple, Apache PDFBox pour générer un PDF.

        // Exemple d'utilisation de PrinterJob pour l'impression
       /*
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean success = job.printPage(createRecuNode());
            if (success) {
                job.endJob();
            }
        }

        */

        // Vérifier si la quantité dans le panier est disponible dans le stock
        boolean stockSuffisant = true;

        for (PanierItem panierItem : panier) {
            // Récupérer le produit du panier
            MedicamentSearch produit = panierItem.getMedicament();

            // Récupérer la quantité achetée
            int quantiteAchetee = panierItem.getQte();

            // Vérifier si la quantité achetée est disponible dans le stock
            if (quantiteAchetee <= produit.getQuantite()) {
                // Soustraire la quantité du panier de la quantité du stock
                int nouvelleQuantiteStock = produit.getQuantite() - quantiteAchetee;
                produit.setQuantite(nouvelleQuantiteStock);


                if (stockSuffisant) {
                    // Mise à jour de la quantité dans la base de données
                    updateQuantiteInDatabase(produit.getID(), nouvelleQuantiteStock);
                    // Autres actions à effectuer si la condition sur le produit est valide
                } else {
                    // Actions à effectuer si la condition sur le produit n'est pas valide
                }




            } else {

                stockSuffisant = false;
                // Gérer le cas où la quantité achetée est supérieure à celle du stock (vous pouvez afficher un message d'erreur, par exemple)
                //System.out.println("Stock insuffisant pour " + produit.getNom_medicament());
                //System.out.println("quantite en stock " + produit.getQuantite());
                // Vous pouvez ajouter une logique pour informer l'utilisateur du problème

                // Affichez un message d'erreur indiquant que la quantité dans le panier est supérieure à celle dans le stock
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur ");
                alert.setHeaderText(null);
                alert.setContentText("STOCK INSUFFISANT POUR: "+produit.getNom_medicament()+ ", QUANTITE EN STOCK: "+produit.getQuantite());
                alert.showAndWait();


            }



        }

        if (stockSuffisant) {


            // Insert purchase information into the "achat" table
            insertAchatData(codeRecu);



            //AFFICHER LE RECU ICI
            Node recuNode = createRecuNode();
            afficherRecu(recuNode);

        } else {





        }








    }

    /*
    private double getTotalCoutPanier() {
        return panier.stream()
                .mapToDouble(item -> item.getTotIndividuel())
                .sum();
    }

     */
    private Node createRecuNode() {
        VBox recuLayout = new VBox();
        recuLayout.setSpacing(10);

        // Partie Entête
        Label enteteLabel = new Label(
                     "Nom de l'entreprise: Pharmacie Fidokpui\n" +
                        "Adresse: Agoè-Zongo \n" +
                        "Quartier: Fidokpui\n" +
                        "Numéro de téléphone: +228 91264085\n"+
                        "---------------------------------------------\n"
        );
        enteteLabel.setStyle("-fx-font-weight: bold;");  // Style pour l'entête
        //enteteLabel.setStyle("-fx-font-family: 'Courier New';");
        recuLayout.getChildren().add(enteteLabel);

        // Partie Corps
        double totalGeneral = 0.0;  // Variable pour le total général

        for (PanierItem panierItem : panier) {
            Label produitLabel = new Label(
                    "Nom du produit: " + panierItem.getMedicament().getNom_medicament() + "\n" +
                            "Prix unitaire: " + panierItem.getMedicament().getPrix() + " FCFA\n" +
                            "Quantité: " + panierItem.getQte() + "\n" +
                            "Total individuel: " + panierItem.getTotIndividuel() + " FCFA\n"+
                            "---------------------------------------------\n"
            );
            produitLabel.setStyle("-fx-font-family: 'Courier New';");
            recuLayout.getChildren().add(produitLabel);

            totalGeneral += panierItem.getTotIndividuel();
        }



        // Ajoutez le total général à la fin du reçu
        Label totalLabel = new Label(
                     "---------------------------------------------\n"+
                        "Total général: " + totalGeneral + " FCFA \n"+
                        "---------------------------------------------\n"
        );
        recuLayout.getChildren().add(totalLabel);

        // Ajoutez la date et l'heure de l'achat à droite
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dateHeureAchat = now.format(formatter);

        Label dateHeureLabel = new Label("Date et heure de l'achat: " + dateHeureAchat);

        recuLayout.getChildren().add(dateHeureLabel); // Ajoutez la date et l'heure à droite



        Label codeRecuLabel = new Label("Code de reçu: " + codeRecu);
        codeRecuLabel.setStyle("-fx-background-color: black; -fx-text-fill: white;"); // Style CSS
        recuLayout.getChildren().add(codeRecuLabel);


        // Partie Pied de Page
        Label piedDePageLabel = new Label("----------- MERCI DE VOTRE FIDELITE ------------");
        piedDePageLabel.setStyle("-fx-font-weight: bold;");  // Style pour le pied de page
        piedDePageLabel.setStyle("-fx-font-family: 'Courier New';");
        recuLayout.getChildren().add(piedDePageLabel);



        // Centrez le contenu de la VBox
        recuLayout.setAlignment(Pos.CENTER);

        // Créez une scène avec une largeur fixe de 80 mm
        Scene scene = new Scene(recuLayout, convertMmToPixels(80), 600); // La hauteur est arbitraire, vous pouvez ajuster au besoin


        // Ajoutez d'autres informations pertinentes, telles que la date, etc.

        return recuLayout;
    }

    //METHODE POUR DEFINIR LA LARGEUR DU PAPIER DU RECU A 80mm
    private double convertMmToPixels(double mm) {
        // Facteur de conversion approximatif pour la largeur de la scène
        return mm * 3.78;  // Vous pouvez ajuster ce facteur en fonction de votre contexte
    }


    private void afficherRecu(Node recuNode) {
        Stage recuStage = new Stage();
        recuStage.setTitle("Reçu de l'achat");
        recuStage.initModality(Modality.WINDOW_MODAL);

        Scene scene = new Scene(new Group(recuNode), convertMmToPixels(80), 600); // Ajustez la taille en conséquence
        recuStage.setScene(scene);

        recuStage.show();
    }


    public static void updateQuantiteInDatabase(int produitId, int nouvelleQuantite) {
        String query = "UPDATE medicament SET quantite = ? WHERE ID = ?";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, nouvelleQuantite);
            preparedStatement.setInt(2, produitId);

            preparedStatement.executeUpdate();

            System.out.println("Quantité mise à jour avec succès dans la base de données.");
        } catch (SQLException e) {
            e.printStackTrace();

            // Affichez une alerte ou gérez l'erreur d'une autre manière en cas d'échec de la mise à jour
            // Ajoutez ici le code pour gérer l'erreur d'une manière appropriée dans votre application
        }
    }




    //--------------------------------------------//
    private void insertAchatData(String codeRecu) {
        String insertQuery = "INSERT INTO achat (date_achat, code_recu, total) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Set the purchase date
            LocalDateTime now = LocalDateTime.now();
            preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(now));

            // Set the receipt code
            preparedStatement.setString(2, codeRecu);

            // Set the total cost of the purchase
            preparedStatement.setDouble(3, calculerCoutTotalPanier());

            // Execute the insert query
            preparedStatement.executeUpdate();

            System.out.println("Purchase information inserted into the 'achat' table successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception appropriately for your application
        }
    }









    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Set the font for the TableView
        panierTable.setStyle("-fx-font-family: 'Courier New';");


        // Ajouter un gestionnaire d'événements au bouton Valider
        //btnValider.setOnAction(this::handleValiderButtonAction);

        btnValider.setOnAction(event ->

                handleValiderButton()
        );


        /*
        //idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nomMedicament"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        //dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        //dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));
        //qteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));


         */

        // Appelez la méthode pour calculer le coût total du panier
        calculerCoutTotalPanier();


        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicament().getNom_medicament()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicament().getDescription()));
        prixColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getMedicament().getPrix()).asObject());

        //total_individuelColumn.setCellValueFactory(new PropertyValueFactory<>("totIndividuel"));

        // Mettez à jour la TableView avec les éléments du panier
        ObservableList<PanierItem> panierItems = FXCollections.observableArrayList(panier);
        panierTable.setItems(panierItems);



        // Créez une cellFactory pour la colonne "Action"
        TableColumn<PanierItem, Void> actionColumn = action_Column;
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PanierItem, Void> call(final TableColumn<PanierItem, Void> param) {
                return new TableCell<>() {
                    private final Button actionButton = new Button();
                    private final ImageView imageView = new ImageView(new Image("file:/C:/Users/DELL/IdeaProjects/Pharmacie/src/main/resources/Image/Delete.png"));


                    {

                        // Ajustez la taille de l'ImageView ici
                        imageView.setFitWidth(16); // Réglez la largeur souhaitée
                        imageView.setPreserveRatio(true); // Garantit que l'aspect ratio de l'image est conservé (le rapport largeur/hauteur)


                        actionButton.setGraphic(imageView);
                        actionButton.setOnAction(event -> {
                            // Code à exécuter lors du clic sur le bouton dans la cellule
                            // Vous pouvez accéder aux données de la ligne actuelle avec getItem()
                            PanierItem objet = getTableView().getItems().get(getIndex());
                            // methode pour supprimer un produit dans le panier
                            panier.remove(objet);
                            // Mettez à jour la TableView
                            panierTable.getItems().remove(objet);

                            showLoadingIcon(); // Show the loading icon during the update

                            updateTableAfterDelay();
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

        // voici la methode pour ajouter CFA a la fin du prix dans la colonne prixColumn
        prixColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getMedicament().getPrix()).asObject());
        prixColumn.setCellFactory(col -> new TableCell<PanierItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    // Display the price with the symbol "CFA"
                    setText(String.format("%.2f FCFA", item));
                    setStyle("-fx-alignment: CENTER;"); // Centrer le texte
                }
            }
        });




        // Configurer la colonne QTE avec un Textfield
        qteColumn.setCellValueFactory(new PropertyValueFactory<>("qte"));

        qteColumn.setCellFactory(col -> new TableCell<>() {
            private final TextField textField = new TextField();

            {
                textField.setAlignment(Pos.CENTER);
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (getTableRow() != null && newValue != null && !newValue.isEmpty()) {
                        try {
                            int newQte = Integer.parseInt(newValue);
                            PanierItem item = getTableView().getItems().get(getIndex());
                            item.setQte(newQte);

                            // Calculate and update the total individual value
                            double total = item.getMedicament().getPrix() * newQte;
                            item.setTotIndividuel(total);

                            showLoadingIcon(); // Show the loading icon during the update
                            updateTableAfterDelay(); // Update the table after a delay

                        } catch (NumberFormatException e) {
                            // Handle the case where the input is not a valid integer
                            textField.setText(oldValue); // Revert to the old value
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    textField.setText(item.toString());
                    setGraphic(textField);
                }
            }
        });


        // Vérifiez si le panier est vide et désactivez le bouton en conséquence
        if (panier.isEmpty()) {
            btnValider.setDisable(true);
        }






    }
}
