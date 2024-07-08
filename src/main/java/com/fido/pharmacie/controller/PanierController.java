package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import com.fido.pharmacie.model.MedicamentStock;
import com.fido.pharmacie.model.PanierItem;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;


import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import static com.fido.pharmacie.controller.DatabaseConnection.connection;
import static com.fido.pharmacie.controller.MedicamentController.*;


public class PanierController implements Initializable {

    @FXML
    private TableView<PanierItem> panierTable;


    // Mettez à jour la TableView avec les éléments du panier
    ObservableList<PanierItem> panierItems = FXCollections.observableArrayList(panier);

    @FXML
    private ProgressIndicator loadingIcon;

    @FXML
    private TableColumn<PanierItem, Void> action_Column;


    @FXML
    private TableColumn<PanierItem, String> descriptionColumn;



    @FXML
    private TableColumn<PanierItem, String> nomColumn;

    @FXML
    private TableColumn<PanierItem, Double> prixColumn;

    @FXML
    private TableColumn<PanierItem, Integer> qteColumn;


    @FXML
    private Label coutTotalLabel;

    @FXML
    private Label coutTotalLabelTTC;

    @FXML
    private Button btnValider;


    @FXML
    private TextField paiementTextfield;


    @FXML
    private Label remiseLabel;

    @FXML
    private ImageView imgAjouter;

    @FXML
    private Button btnAjouter;


    @FXML
    private TextField quantiteTextF;


    @FXML
    private TextField nomproduitTextF;

    @FXML
    private TextField nLotTextF;

    @FXML
    private TextArea descriptionTextF;

    @FXML
    private TextField dosageTextF;

    @FXML
    private TextField formeTextF;

    @FXML
    private TextField fournisseurTextF;

    @FXML
    private TextField dateExpirationTxtF;



    @FXML
    private TextField prixTextF;

    @FXML
    private TextField rechercherTxtF;

    private AutoCompletionBinding<String> binding;



    @FXML
    private Button clearButton;

    @FXML
    private ComboBox<String> modeReglementComboBox;

    private DashboardController dashboardController; // Ajoutez une référence au DashboardController


    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }


    public PanierController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    // Ajoutez le code de reçu aléatoire
    String codeRecu = genererCodeRecu();


    // Ajoutez ceci au début de la méthode initialize
    private boolean isUpdating = false;

    public PanierController() {
    }


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
                Thread.sleep(1500);

                // Mettez à jour la table dans la file d'interface utilisateur
                Platform.runLater(() -> {
                    isUpdating = true;

                    // Update the TableView with items from the cart
                    ObservableList<PanierItem> panierItems = FXCollections.observableArrayList(panier);
                    panierTable.setItems(panierItems);

                    // Update total_individuelColumn based on the new quantities
                    for (PanierItem panierItem : panierItems) {
                        double total = panierItem.getMedicament().getPrixUnitaire() * panierItem.getQte();
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
        coutTotalLabel.setStyle("-fx-text-fill: green;");

        //cout total TTC
        coutTotalLabelTTC.setText(String.format("%.2f FCFA", coutTotal));
        coutTotalLabelTTC.setStyle("-fx-text-fill: green;");
        return coutTotal;
    }






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


    // Méthode pour afficher une boîte de dialogue d'alerte
    private void afficherAlerte(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }



    private void handleValiderButton() {
        // Récupérer le montant entré par le client depuis le TextField
        String montantClientText = paiementTextfield.getText();

        if (montantClientText != null && !montantClientText.isEmpty()) {
            try {
                double montantClient = Double.parseDouble(montantClientText);

                // Calculer la remise en soustrayant le montant du coût total
                double coutTotal = calculerCoutTotalPanier();

                if (montantClient < coutTotal) {
                    // Émettre un bip sonore
                    Toolkit.getDefaultToolkit().beep();

                    // Afficher une alerte si le montant versé est inférieur au coût total
                    afficherAlerte("Montant insuffisant", "Le montant versé est inférieur au coût total.");
                } else {

                    // Générer le reçu avec les informations des produits achetés
                    genererRecu();




                }
            } catch (NumberFormatException e) {
                // Gérer le cas où l'entrée du client n'est pas un nombre valide
                remiseLabel.setText("Montant invalide");
            }
        } else {
            // Émettre un bip sonore
            Toolkit.getDefaultToolkit().beep();

            // Gérer le cas où le champ est vide
            afficherAlerte("Champ vide", "Veuillez entrer le montant versé par le client.");
        }
    }




    private void genererRecu() {
        // Vérifier si la quantité dans le panier est disponible dans le stock
        boolean stockSuffisant = true;

        List<PanierItem> elementsASupprimer = new ArrayList<>();  // Liste temporaire

        for (PanierItem panierItem : panier) {
            // Récupérer le produit du panier
            MedicamentStock produit = panierItem.getMedicament();

            // Récupérer la quantité achetée
            int quantiteAchetee = panierItem.getQte();

            // Calculer la quantité totale disponible en stock pour ce produit
            int quantiteTotaleEnStock = getTotalQuantityFromStock(produit.getCodeBarre());

            // Vérifier si la quantité achetée est disponible dans le stock
            if (quantiteAchetee > quantiteTotaleEnStock) {
                // Émettre un bip sonore
                Toolkit.getDefaultToolkit().beep();

                stockSuffisant = false;
                // Gérer le cas où la quantité achetée est supérieure à celle du stock
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur ");
                alert.setHeaderText(null);
                alert.setContentText("STOCK INSUFFISANT POUR: " + produit.getNomProduit() + ", QUANTITE EN STOCK: " + quantiteTotaleEnStock);
                alert.showAndWait();
                // Continuer avec le produit suivant sans effectuer les actions de mise à jour
                continue;
            }

            // Ajouter à la liste temporaire
            elementsASupprimer.add(panierItem);
        }

        // Si le stock n'est pas suffisant pour au moins un produit, ne pas continuer le reste de la logique
        if (!stockSuffisant) {
            return;
        }

        // Calculer la remise en soustrayant le montant du coût total
        double montantClient = Double.parseDouble(paiementTextfield.getText());
        double coutTotal = calculerCoutTotalPanier();
        double reste = montantClient - coutTotal;

        // Afficher le résultat dans l'étiquette remiseLabel si la remise est valide
        remiseLabel.setText(String.format(" %.2f FCFA", reste));

        // Actions de mise à jour en dehors de la boucle
        for (PanierItem panierItem : elementsASupprimer) {
            MedicamentStock produit = panierItem.getMedicament();
            int quantiteAchetee = panierItem.getQte();
            String codeBarre = produit.getCodeBarre();

            try {
                // Obtenir les entrées de stock pour le produit, triées par date de réception
                String query = "SELECT id, quantite FROM Stocks WHERE code_barres = ? ORDER BY date_reception";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, codeBarre);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next() && quantiteAchetee > 0) {
                    int id = resultSet.getInt("id");
                    int quantiteEnStock = resultSet.getInt("quantite");

                    // Déterminer la quantité à soustraire de cette entrée
                    int quantiteASoustraire = Math.min(quantiteAchetee, quantiteEnStock);

                    // Mettre à jour la quantité en stock pour cette entrée
                    int nouvelleQuantite = quantiteEnStock - quantiteASoustraire;
                    updateStockQuantity(id, nouvelleQuantite);

                    // Réduire la quantité achetée du total à soustraire
                    quantiteAchetee -= quantiteASoustraire;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Gérer l'exception SQL
            }
        }

        // Insert purchase information into the "achat" table
        insertAchatData(codeRecu);

        // Afficher l'icone de chargement après la suppression dans le panier
        showLoadingIcon();

        updateTableAfterDelay();

        // AFFICHER LE RECU ICI
        Node recuNode = createRecuNode();
        afficherRecu(recuNode);

        // Vider la table du panier
        panier.removeAll(elementsASupprimer);

        // Mettre à jour la TableView après avoir vidé le panier
        panierTable.getItems().clear();
        panierTable.refresh();

        btnValider.setDisable(true);
    }

    private int getTotalQuantityFromStock(String codeBarre) {
        int totalQuantity = 0;
        String query = "SELECT SUM(quantite) AS total FROM Stocks WHERE code_barres = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, codeBarre);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalQuantity = resultSet.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception SQL
        }
        return totalQuantity;
    }

    private void updateStockQuantity(int stockId, int newQuantity) {
        if (newQuantity > 0) {
            String updateQuery = "UPDATE Stocks SET quantite = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, newQuantity);
                preparedStatement.setInt(2, stockId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                // Gérer l'exception SQL
            }
        } else {
            // Supprimer l'entrée de stock si la quantité est 0
            String deleteQuery = "DELETE FROM Stocks WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setInt(1, stockId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                // Gérer l'exception SQL
            }
        }
    }


    public Image generateBarcode(String codeBarre) {
        Code128Bean barcodeBean = new Code128Bean();
        final int dpi = 150;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(baos, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

        barcodeBean.generateBarcode(canvasProvider, codeRecu);

        try {
            canvasProvider.finish();
            ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
            return new Image(bis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



/*
    private void genererRecu() {

        // Vérifier si la quantité dans le panier est disponible dans le stock
        boolean stockSuffisant = true;

        List<PanierItem> elementsASupprimer = new ArrayList<>();  // Liste temporaire

        for (PanierItem panierItem : panier) {
            // Récupérer le produit du panier
            MedicamentStock produit = panierItem.getMedicament();

            // Récupérer la quantité achetée
            int quantiteAchetee = panierItem.getQte();

            // Vérifier si la quantité achetée est disponible dans le stock
            if (quantiteAchetee > produit.getQuantite()) {

                // Émettre un bip sonore
                Toolkit.getDefaultToolkit().beep();

                stockSuffisant = false;
                // Gérer le cas où la quantité achetée est supérieure à celle du stock
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur ");
                alert.setHeaderText(null);
                alert.setContentText("STOCK INSUFFISANT POUR: " + produit.getNomProduit() + ", QUANTITE EN STOCK: " + produit.getQuantite());
                alert.showAndWait();
                // Continuer avec le produit suivant sans effectuer les actions de mise à jour
                continue;
            }

            // Ajouter à la liste temporaire
            elementsASupprimer.add(panierItem);
        }

        // Si le stock n'est pas suffisant pour au moins un produit, ne pas continuer le reste de la logique
        if (!stockSuffisant) {

            return;
        }

        // Calculer la remise en soustrayant le montant du coût total
        double montantClient = Double.parseDouble(paiementTextfield.getText());
        double coutTotal = calculerCoutTotalPanier();
        double reste = montantClient - coutTotal;

        // Afficher le résultat dans l'étiquette remiseLabel si la remise est valide
        remiseLabel.setText(String.format(" %.2f FCFA", reste));

        // Actions de mise à jour en dehors de la boucle
        for (PanierItem panierItem : elementsASupprimer) {
            MedicamentStock produit = panierItem.getMedicament();
            int quantiteAchetee = panierItem.getQte();

            // Soustraire la quantité du panier de la quantité du stock
            int nouvelleQuantiteStock = produit.getQuantite() - quantiteAchetee;
            produit.setQuantite(nouvelleQuantiteStock);


            // Mise à jour de la quantité dans la base de données
            updateQuantiteInDatabase(produit.getCodeBarre(), nouvelleQuantiteStock);
            // Autres actions à effectuer si la condition sur le produit est valide





        }

        // Insert purchase information into the "achat" table
        insertAchatData(codeRecu);

        // Afficher l'icone de chargement après la suppression dans le panier
        showLoadingIcon();

        updateTableAfterDelay();

        // AFFICHER LE RECU ICI
        Node recuNode = createRecuNode();
        afficherRecu(recuNode);

        // Vider la table du panier
        panier.removeAll(elementsASupprimer);

        // Mettre à jour la TableView après avoir vidé le panier
        panierTable.getItems().clear();
        panierTable.refresh();

        btnValider.setDisable(true);
    } */



    /*
    private double getTotalCoutPanier() {
        return panier.stream()
                .mapToDouble(item -> item.getTotIndividuel())
                .sum();
    }

     */

    private void addSeparator(GridPane gridPane, int rowIndex) {
        Separator separator = new Separator();
        separator.setMaxWidth(Double.MAX_VALUE);
        GridPane.setColumnSpan(separator, GridPane.REMAINING);
        gridPane.add(separator, 0, rowIndex);
    }



    private Node createRecuNode() {
        VBox recuLayout = new VBox();
        recuLayout.setSpacing(3);

        // Centrage
        recuLayout.setAlignment(Pos.CENTER);

        // Définir une taille de police globale pour le VBox
        recuLayout.setStyle("-fx-font-size: 8px;");

        // Définir la largeur de la VBox pour correspondre à la largeur du support d'impression (80 mm)
        double largeurSupportImpressionPixels = convertMmToPixels(58);
        recuLayout.setPrefWidth(largeurSupportImpressionPixels);
        recuLayout.setMaxWidth(largeurSupportImpressionPixels);
        recuLayout.setMinWidth(largeurSupportImpressionPixels);

        // Partie Entête
        recuLayout.getChildren().add(createEnteteLabel());

        // Partie Corps: GridPane
        GridPane gridPane = createGridPane(recuLayout);
        recuLayout.getChildren().add(gridPane);

        // Calcul du total général
        double totalGeneral = panier.stream().mapToDouble(PanierItem::getTotIndividuel).sum();
        recuLayout.getChildren().add(createTotalLabel(totalGeneral));

        // Paiement et remise
        double montantClient = parseMontantClient(paiementTextfield.getText());
        recuLayout.getChildren().add(new Label("Paiement client : " + String.format("%.2f FCFA", montantClient)));
        double remise = montantClient - calculerCoutTotalPanier();
        recuLayout.getChildren().add(new Label("Remise : " + String.format("%.2f FCFA", remise)));

        // Date et heure de l'achat
        recuLayout.getChildren().add(new Label("Date et Heure Achat: " + getDateHeureAchat()));

        // Code de reçu
        Label codeRecuLabel = new Label("Code de reçu: " + codeRecu);
        codeRecuLabel.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        recuLayout.getChildren().add(codeRecuLabel);

        // Ajouter l'image du code-barres
        Image barcodeImage = generateBarcode(codeRecu);
        if (barcodeImage != null) {
            ImageView barcodeImageView = new ImageView(barcodeImage);
            barcodeImageView.setFitWidth(100);  // Ajuster la largeur du code-barres si nécessaire
            barcodeImageView.setPreserveRatio(true);
            recuLayout.getChildren().add(barcodeImageView);
        }

        // Pied de Page
        recuLayout.getChildren().add(createPiedDePageLabel());



        // Ajustement de la largeur du TableView
        //gridPane.prefWidthProperty().bind(recuLayout.widthProperty());



        // Gestion ScrollPane si nécessaire
        Scene tempScene = new Scene(recuLayout, largeurSupportImpressionPixels, 600);
        if (recuLayout.getBoundsInParent().getHeight() > tempScene.getHeight()) {
            ScrollPane scrollPane = new ScrollPane(recuLayout);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            return scrollPane;
        }

        return recuLayout;
    }

    private GridPane createGridPane(VBox recuLayout) {
        GridPane gridPane = new GridPane();
       // gridPane.setHgap(5); // Espacement horizontal
       // gridPane.setVgap(5);  // Espacement vertical
      //  gridPane.setPadding(new Insets(10, 10, 10, 10));

        // Création des en-têtes de colonnes
        Label produitHeader = new Label("Produit");
        Label prixUnitaireHeader = new Label("Prix");
        Label quantiteHeader = new Label("Qte");
        Label totalIndividuelHeader = new Label("Total");

        // Permettre aux en-têtes de colonnes de revenir à la ligne et appliquer des styles

        produitHeader.setStyle("-fx-font-weight: bold;   -fx-font-family: 'Courier New';");

        prixUnitaireHeader.setStyle("-fx-font-weight: bold;   -fx-font-family: 'Courier New';");

        quantiteHeader.setStyle("-fx-font-weight: bold;   -fx-font-family: 'Courier New';");
        totalIndividuelHeader.setWrapText(true);
        totalIndividuelHeader.setStyle("-fx-font-weight: bold;   -fx-font-family: 'Courier New';");

        // Ajout des en-têtes de colonnes au GridPane
        // Ajout des en-têtes de colonnes au GridPane
        addCenteredLabelToGridPane(gridPane, produitHeader, 0, 0);
        addCenteredLabelToGridPane(gridPane, prixUnitaireHeader, 1, 0);
        addCenteredLabelToGridPane(gridPane, quantiteHeader, 2, 0);
        addCenteredLabelToGridPane(gridPane, totalIndividuelHeader, 3, 0);

        // Ajout du séparateur après les en-têtes de colonnes
        addSeparator(gridPane, 1);


        // Ajout des données du tableau au GridPane, en commençant à la ligne 2
        addTableData(gridPane, 2);


        // Ajout du séparateur après les données du tableau
        addSeparator(gridPane, gridPane.getRowCount());


        // Ajustement de la largeur des colonnes
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(51);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPercentWidth(18);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.ALWAYS);
        col3.setPercentWidth(8);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setHgrow(Priority.ALWAYS);
        col4.setPercentWidth(23);
        gridPane.getColumnConstraints().addAll(col1, col2, col3, col4);

        // Ajustement de la largeur du GridPane
        gridPane.prefWidthProperty().bind(recuLayout.widthProperty());

        return gridPane;
    }

    private void addTableData(GridPane gridPane, int startRow) {
        ObservableList<PanierItem> items = FXCollections.observableArrayList(panier);
        for (int i = 0; i < items.size(); i++) {
            PanierItem item = items.get(i);
            MedicamentStock medicament = item.getMedicament();

            Label produitLabel = new Label(medicament.getNomProduit());
            produitLabel.setWrapText(true);

            Label prixUnitaireLabel = new Label(String.valueOf(medicament.getPrixUnitaire()));

            Label quantiteLabel = new Label(String.valueOf(item.getQte()));

            Label totalIndividuelLabel = new Label(String.valueOf(item.getTotIndividuel()));
            totalIndividuelLabel.setWrapText(true);

            addCenteredLabelToGridPane(gridPane, produitLabel, 0, startRow + i);
            addCenteredLabelToGridPane(gridPane, prixUnitaireLabel, 1, startRow + i);
            addCenteredLabelToGridPane(gridPane, quantiteLabel, 2, startRow + i);
            addCenteredLabelToGridPane(gridPane, totalIndividuelLabel, 3, startRow + i);
        }
    }

    private void addCenteredLabelToGridPane(GridPane gridPane, Label label, int colIndex, int rowIndex) {
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(label);
        stackPane.setAlignment(Pos.CENTER);
        gridPane.add(stackPane, colIndex, rowIndex);
    }

    private Label createEnteteLabel() {
        Label enteteLabel = new Label(
                " Pharmacie Fidokpui\n" +
                        "Adresse: Agoè-Zongo \n" +
                        "Quartier: Fidokpui\n" +
                        "Numéro: +228 91264085\n"+
                        "---------------------------------------------\n"
        );
        enteteLabel.setStyle("-fx-font-weight: bold;");
        return enteteLabel;
    }

    private Label createTotalLabel(double totalGeneral) {
        return new Label(
                "---------------------------------------------\n" +
                        "PRIX TOTAL : " + String.format("%.2f FCFA", totalGeneral) + " FCFA\n" +
                        "---------------------------------------------\n"
        );
    }

    private double parseMontantClient(String montantClientText) {
        try {
            return Double.parseDouble(montantClientText);
        } catch (NumberFormatException e) {
            // Gérer le cas où l'entrée du client n'est pas un nombre valide
            return 0.0;
        }
    }

    private String getDateHeureAchat() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter);
    }

    private Label createPiedDePageLabel() {
        Label piedDePageLabel = new Label("---- MERCI DE VOTRE FIDELITE ----");
        piedDePageLabel.setStyle("-fx-font-weight: bold; -fx-font-family: 'Courier New';");
        return piedDePageLabel;
    }





    //METHODE POUR DEFINIR LA LARGEUR DU PAPIER DU RECU A 80mm
    private double convertMmToPixels(double mm) {
        final double MM_TO_INCH = 0.0393701;
        final double DPI = 72; // Nombre de points par pouce, peut varier selon l'imprimante
        return mm * MM_TO_INCH * DPI;
    }


  /*  private void afficherRecu(Node recuNode) {


        Stage recuStage = new Stage();
        recuStage.setTitle("RECU DE VENTE");
        recuStage.initModality(Modality.WINDOW_MODAL);

        // Créez une ScrollPane pour envelopper le contenu du reçu
        ScrollPane scrollPane = new ScrollPane(recuNode);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(new Group(scrollPane)); // Ajustez la taille en conséquence
        recuStage.setScene(scene);


        recuStage.setResizable(false);

        recuStage.show();

        // Demande à l'utilisateur s'il souhaite imprimer le reçu
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Imprimer le reçu");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous imprimer ce reçu?");

        ButtonType buttonImprimer = new ButtonType("Imprimer", ButtonBar.ButtonData.YES);
        ButtonType buttonAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonImprimer, buttonAnnuler);

        Optional<ButtonType> result = alert.showAndWait();



        if (result.isPresent() && result.get() == buttonImprimer) {
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null && printerJob.showPrintDialog(recuStage.getOwner())) {
                // Configure the printer job to have zero margins
                PageLayout pageLayout = printerJob.getPrinter().createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
                printerJob.getJobSettings().setPageLayout(pageLayout);

                boolean success = printerJob.printPage(recuNode);
                if (success) {
                    printerJob.endJob();
                }
            }
        }
    }*/


    private void afficherRecu(Node recuNode) {
        Stage recuStage = new Stage();
        recuStage.setTitle("RECU DE VENTE");
        recuStage.initModality(Modality.WINDOW_MODAL);

        // Affiche le reçu dans une nouvelle fenêtre
        Scene scene = new Scene(new Group(recuNode)); // Ajustez la taille en conséquence
        recuStage.setScene(scene);

        recuStage.setResizable(false);
        recuStage.show();

        // Demande à l'utilisateur s'il souhaite imprimer le reçu
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Imprimer le reçu");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous imprimer ce reçu?");

        ButtonType buttonImprimer = new ButtonType("Imprimer", ButtonBar.ButtonData.YES);
        ButtonType buttonAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonImprimer, buttonAnnuler);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == buttonImprimer) {
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null && printerJob.showPrintDialog(recuStage.getOwner())) {
                // Créer une mise en page pour l'imprimante TSP100III
                PageLayout pageLayout = printerJob.getPrinter().createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
                printerJob.getJobSettings().setPageLayout(pageLayout);



                boolean success = printerJob.printPage(pageLayout, recuNode);
                if (success) {
                    printerJob.endJob();
                }
            }
        }

    }








    public static void updateQuantiteInDatabase(String produitId, int nouvelleQuantite) {
        String query = "UPDATE stocks SET quantite = ? WHERE code_barres = ?";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, nouvelleQuantite);
            preparedStatement.setString(2, produitId);

            preparedStatement.executeUpdate();

            System.out.println("Quantité mise à jour avec succès dans la base de données.");
        } catch (SQLException e) {
            e.printStackTrace();

            // Affichez une alerte ou gérez l'erreur d'une autre manière en cas d'échec de la mise à jour
            // Ajoutez ici le code pour gérer l'erreur d'une manière appropriée dans votre application
        }
    }




    //--------------------------------------------//
   /* private void insertAchatData(String codeRecu) {
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

    */


    private void insertAchatData(String codeRecu) {
        String insertAchatQuery = "INSERT INTO vente (date_achat, code_recu, total) VALUES (?, ?, ?)";
        String insertDetailVenteQuery = "INSERT INTO detailvente (IDVente, code_barres, Quantite, PrixUnitaire) VALUES (?, ?, ?, ?)";

        try (PreparedStatement insertAchatStatement = connection.prepareStatement(insertAchatQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement insertDetailVenteStatement = connection.prepareStatement(insertDetailVenteQuery)) {

            // Set the purchase date
            LocalDateTime now = LocalDateTime.now();
            insertAchatStatement.setTimestamp(1, java.sql.Timestamp.valueOf(now));

            // Set the receipt code
            insertAchatStatement.setString(2, codeRecu);

            // Set the total cost of the purchase
            double totalCout = calculerCoutTotalPanier();
            insertAchatStatement.setDouble(3, totalCout);

            // Execute the insert query for achat and get generated keys
            insertAchatStatement.executeUpdate();
            ResultSet generatedKeys = insertAchatStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                int idAchat = generatedKeys.getInt(1);

                // Iterate through panier and insert details into detailvente
                for (PanierItem panierItem : panier) {
                    String idProduit = panierItem.getMedicament().getCodeBarre();
                    int quantite = panierItem.getQte();
                    double prixUnitaire = panierItem.getMedicament().getPrixUnitaire();

                    insertDetailVenteStatement.setInt(1, idAchat);
                    insertDetailVenteStatement.setString(2, idProduit);
                    insertDetailVenteStatement.setInt(3, quantite);
                    insertDetailVenteStatement.setDouble(4, prixUnitaire);

                    // Execute the insert query for detailvente
                    insertDetailVenteStatement.executeUpdate();
                }
            }

            System.out.println("Purchase information inserted into the 'achat' table successfully.");
            System.out.println("DetailVente information inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception appropriately for your application
        }
    }


    private List<MedicamentStock> searchProductsInDatabase2(String searchText) {
        List<MedicamentStock> matchingProducts = new ArrayList<>();
        String query = "SELECT p.code_barres, p.nom_produit, p.description, p.dosage, p.prix, p.categorie_produit, p.fournisseur, p.instructions_utilisation, " +
                "SUM(s.quantite) AS quantite_totale " +
                "FROM produits p " +
                "JOIN stocks s ON p.code_barres = s.code_barres " +
                "WHERE p.nom_produit LIKE ? " +
                "GROUP BY p.code_barres, p.nom_produit, p.description, p.dosage, p.prix, p.categorie_produit, p.fournisseur, p.instructions_utilisation";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + searchText + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String codeBarres = resultSet.getString("code_barres");
                String productName = resultSet.getString("nom_produit");
                String description = resultSet.getString("description");
                String dosage = resultSet.getString("dosage");
                double prix = resultSet.getDouble("prix");
                String categorie = resultSet.getString("categorie_produit");
                String fournisseur = resultSet.getString("fournisseur");
                String instructionsUtilisation = resultSet.getString("instructions_utilisation");
                int quantiteTotale = resultSet.getInt("quantite_totale");

                // Création de l'objet MedicamentStock avec toutes les informations nécessaires
                MedicamentStock medicament = new MedicamentStock(codeBarres, productName, dosage, description, prix, categorie, quantiteTotale, fournisseur, instructionsUtilisation);
                matchingProducts.add(medicament);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception SQL
        }
        return matchingProducts;
    }






    // Méthode pour mettre à jour la visibilité du bouton d'effacement
    private void updateClearButtonVisibility(String text) {
        clearButton.setVisible(!text.isEmpty());
    }


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearProductDetails() {
        // Effacer les détails du produit dans l'interface utilisateur
        nomproduitTextF.clear();
        descriptionTextF.clear();
        dosageTextF.clear();
        dateExpirationTxtF.clear();
        prixTextF.clear();
        fournisseurTextF.clear();
        formeTextF.clear();
        nLotTextF.clear();
    }






    private boolean isProductInPanierItems2(PanierItem newItem) {
        for (PanierItem item : panier) {
            if (item.getMedicament().getCodeBarre().equals(newItem.getMedicament().getCodeBarre())) {
                return true;
            }
        }
        return false;
    }

    private void setupAutoCompletion() {
        // Créer le TableView et le Popup une seule fois
        TableView<MedicamentStock> tableView = new TableView<>();
        Popup popup = new Popup();

        // Créer les colonnes du TableView
        TableColumn<MedicamentStock, Double> Id_column = new TableColumn<>("#");
        Id_column.setCellValueFactory(new PropertyValueFactory<>("codeBarre"));

        TableColumn<MedicamentStock, String> produitColumn = new TableColumn<>("Produit");
        produitColumn.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));

        TableColumn<MedicamentStock, String> dosageColumn = new TableColumn<>("Dosage");
        dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));

        TableColumn<MedicamentStock, String> formeColumn = new TableColumn<>("categorie");
        formeColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        TableColumn<MedicamentStock, Double> prixColumn = new TableColumn<>("Prix Unitaire");
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));

        TableColumn<MedicamentStock, Double> qte_column = new TableColumn<>("qte En Stock");
        qte_column.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        // Ajouter les colonnes au TableView
        tableView.getColumns().addAll(Id_column, produitColumn, dosageColumn, qte_column, formeColumn, prixColumn );

        // Ajouter le TableView au Popup
        popup.getContent().add(tableView);

        // Lier la largeur du TableView à celle du TextField
        tableView.prefWidthProperty().bind(rechercherTxtF.widthProperty());
        tableView.prefHeightProperty().bind(rechercherTxtF.heightProperty().multiply(4)); // Multiplier par 4 pour obtenir une hauteur appropriée

        // Ajouter un gestionnaire d'événements au TextField rechercherTxtF pour l'autocomplétion
        rechercherTxtF.textProperty().addListener((observable, oldValue, newValue) -> {
            // Obtenir une liste de produits correspondants depuis la base de données
            List<MedicamentStock> matchingProducts = searchProductsInDatabase2(newValue);

            // Ajouter les données au TableView
            tableView.setItems(FXCollections.observableArrayList(matchingProducts));

            // Afficher ou masquer le bouton d'effacement en fonction de la saisie
            updateClearButtonVisibility(newValue);

            // Afficher ou masquer le Popup
            if (!newValue.isEmpty() && !matchingProducts.isEmpty()) {
                // Lier l'affichage de la fenêtre contextuelle à la position du TextField
                Platform.runLater(() -> {
                    popup.show(rechercherTxtF,
                            rechercherTxtF.localToScreen(rechercherTxtF.getBoundsInLocal()).getMinX(),
                            rechercherTxtF.localToScreen(rechercherTxtF.getBoundsInLocal()).getMaxY());
                    // Lier la largeur du Popup à celle du TableView
                    popup.setWidth(tableView.getWidth());
                });
            } else {
                popup.hide();
            }
        });

        // Définir un gestionnaire pour la sélection d'un élément depuis le TableView
        tableView.setOnMouseClicked(event -> {
            MedicamentStock medicamentSelectionne = tableView.getSelectionModel().getSelectedItem();
            if (medicamentSelectionne != null) {
                // Afficher l'objet dans la console
                System.out.println("code_barres : " + medicamentSelectionne.getCodeBarre());
                System.out.println("Médicament sélectionné : " + medicamentSelectionne.getNomProduit());
                System.out.println("Description : " + medicamentSelectionne.getDescription());
                System.out.println("Dosage : " + medicamentSelectionne.getDosage());
                System.out.println("Prix : " + medicamentSelectionne.getPrixUnitaire());
                System.out.println("Date d'expiration : " + medicamentSelectionne.getDateExpiration());
                System.out.println("Quantité : " + medicamentSelectionne.getQuantite());
                System.out.println("Numéro de lot : " + medicamentSelectionne.getNumeroLot());
                System.out.println("Forme du produit : " + medicamentSelectionne.getCategorie());

                // Ajouter le produit au panier
                int quantiteChoisie = 1;
                Double totIndividuel = medicamentSelectionne.getPrixUnitaire() * quantiteChoisie;
                PanierItem panierItem = new PanierItem(medicamentSelectionne, quantiteChoisie, totIndividuel);

                // Vérifiez si le produit est déjà dans le panierItems
                if (isProductInPanierItems2(panierItem)) {
                    // Émettre un bip sonore
                    Toolkit.getDefaultToolkit().beep();
                    // Produit déjà dans le panierItems, affichez une alerte
                    showAlert(Alert.AlertType.WARNING, "ERREUR AJOUT AU PANIER", "Le produit est déjà dans le panier.");
                    System.out.println("Le produit avec l'ID " + medicamentSelectionne.getCodeBarre() + " est déjà dans le panier.");
                } else {
                    panier.add(panierItem);

                    // Mettez à jour la TableView du panier
                    panierTable.setItems(FXCollections.observableArrayList(panier));
                }
            }
            popup.hide();
        });
    }





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        setupAutoCompletion();

        // Set the font for the TableView , Application des styles CSS pout la couleur de la tableview
        //panierTable.setStyle("-fx-font-family: 'Courier New'; -fx-base: rgb(158, 152, 69);");
        panierTable.setStyle("-fx-font-family: 'Courier New'; -fx-base: #4E6C50;");



             // Ajouter un gestionnaire d'événements au bouton Valider
        //btnValider.setOnAction(this::handleValiderButtonAction);

        btnValider.setOnAction(event ->

            handleValiderButton()

        );

        // Rendre le bouton invisible à l'initialisation
        clearButton.setVisible(false);


        // Liste des formes de produits
        List<String> formesListData = Arrays.asList(
                "ESPÈCE", "T-Money", "Flooz", "Chèque"

        );



        // Convertir les éléments en majuscules
        List<String> formesList = formesListData.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        // Ajout des formes à la ComboBox
        modeReglementComboBox.setItems(FXCollections.observableArrayList(formesList));




        // Ajouter un gestionnaire d'événements sur le bouton clearButton
        clearButton.setOnAction(event -> {
            // Effacer le contenu du TextField rechercherTxtF
            rechercherTxtF.clear();
        });


        // Ajouter un gestionnaire d'événements au TextField rechercherTxtF pour l'autocomplétion
      /*  rechercherTxtF.textProperty().addListener((observable, oldValue, newValue) -> {
            // Obtenir une liste de produits correspondants depuis la base de données
            Set<String> uniqueMatchingProducts = new HashSet<>(searchProductsInDatabase(newValue));
            List<String> matchingProducts = new ArrayList<>(uniqueMatchingProducts);


            // Créer une FilteredList pour filtrer les suggestions en fonction de la saisie de l'utilisateur
            FilteredList<String> filteredList = new FilteredList<>(FXCollections.observableArrayList(matchingProducts));

            // Lier l'autocomplétion au TextField en utilisant les suggestions filtrées
            binding = TextFields.bindAutoCompletion(rechercherTxtF, filteredList);



            // Définir la largeur de l'objet d'autocomplétion pour correspondre à celle du TextField
            binding.setPrefWidth(rechercherTxtF.getWidth());


            // Définir un gestionnaire pour la sélection d'un élément depuis la fenêtre contextuelle d'autocomplétion
            binding.setOnAutoCompleted(event -> {
                // Gérer le cas où l'utilisateur sélectionne une suggestion
                String nomProduitSelectionne = event.getCompletion();

                // Rechercher le MedicamentSearch dans la base de données en fonction du nom du produit sélectionné
                MedicamentSearch medicamentSelectionne = rechercherMedicamentDansBD(nomProduitSelectionne);

                // Afficher l'objet dans la console
                if (medicamentSelectionne != null) {
                    System.out.println("ID : " + medicamentSelectionne.getID()); // Ajout de cette ligne
                    System.out.println("Médicament sélectionné : " + medicamentSelectionne.getNom_medicament());
                    System.out.println("Description : " + medicamentSelectionne.getDescription());
                    System.out.println("Dosage : " + medicamentSelectionne.getDosage());
                    System.out.println("Prix : " + medicamentSelectionne.getPrix());
                    System.out.println("Date d'expiration : " + medicamentSelectionne.getDate_expiration());
                    System.out.println("Quantité : " + medicamentSelectionne.getQuantite());
                    System.out.println("Numéro de lot : " + medicamentSelectionne.getNumeroLot());
                    System.out.println("Forme du produit : " + medicamentSelectionne.getFormeProduit());





                    // Ajouter le produit au panier
                    int quantiteChoisie = 1;
                    Double totIndividuel = medicamentSelectionne.getPrix() * quantiteChoisie;
                    PanierItem panierItem = new PanierItem(medicamentSelectionne, quantiteChoisie, totIndividuel);

                    // Vérifiez si le produit est déjà dans le panierItems
                    if (isProductInPanierItems(panierItem)) {
                        // Émettre un bip sonore
                        Toolkit.getDefaultToolkit().beep();
                        // Produit déjà dans le panierItems, affichez une alerte
                        showAlert(Alert.AlertType.WARNING, "ERREUR AJOUT AU PANIER", "Le produit est déjà dans le panier.");
                    } else {
                        panier.add(panierItem);

                        // Mettez à jour la TableView du panier
                        panierTable.setItems(FXCollections.observableArrayList(panier));


                    }




                }

                // Votre logique existante pour ajouter au panier ou effectuer d'autres actions
                if (medicamentSelectionne != null) {
                    // Votre logique existante ici, par exemple :
                    // Vérifiez si la quantité est nulle, si elle est déjà dans le panier, etc.
                    // Ensuite, effectuez les actions nécessaires avec l'objet medicamentSelectionne.
                }
            });

            // Afficher ou masquer le bouton d'effacement en fonction de la saisie
            updateClearButtonVisibility(newValue);

        });

        // Gestionnaire pour le bouton d'effacement
        clearButton.setOnAction(event -> {
            rechercherTxtF.clear();
            clearButton.setVisible(false);
        });
*/


        // Configurer le TextFormatter pour n'accepter que des chiffres
        UnaryOperator<Change> filter = change -> {
            String newText = change.getControlNewText();
            if (Pattern.matches("\\d*", newText)) {
                return change; // Accepter le changement
            }
            return null; // Rejeter le changement
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        paiementTextfield.setTextFormatter(textFormatter);






        // Appelez la méthode pour calculer le coût total du panier
        calculerCoutTotalPanier();






        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicament().getNomProduit()));
        //descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicament().getDescription()));
        prixColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getMedicament().getPrixUnitaire()).asObject());

        //total_individuelColumn.setCellValueFactory(new PropertyValueFactory<>("totIndividuel"));




        showLoadingIcon();
        updateTableAfterDelay();

        panierTable.setItems(panierItems);



        // Créez une cellFactory pour la colonne "Action"
        TableColumn<PanierItem, Void> actionColumn = action_Column;
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PanierItem, Void> call(final TableColumn<PanierItem, Void> param) {
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
                            // Vous pouvez accéder aux données de la ligne actuelle avec getItem()
                            PanierItem objet = getTableView().getItems().get(getIndex());
                            // methode pour supprimer un produit dans le panier
                            panier.remove(objet);
                            // Mettez à jour la TableView
                            panierTable.getItems().remove(objet);

                            // Afficher l'icone de chargement apres la suppression dans le panier
                            showLoadingIcon();

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
        prixColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getMedicament().getPrixUnitaire()).asObject());
        prixColumn.setCellFactory(col -> new TableCell<PanierItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    // Display the price with the symbol "CFA"
                    setText(String.format("%.2f CFA", item));
                    setStyle("-fx-alignment: CENTER; -fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 16;"); // Centrer le texte


                }
            }
        });


        // Définissez une cell factory pour la colonne nomColumn
        nomColumn.setCellFactory(new Callback<TableColumn<PanierItem, String>, TableCell<PanierItem, String>>() {
            @Override
            public TableCell<PanierItem, String> call(TableColumn<PanierItem, String> param) {
                return new TableCell<PanierItem, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        // Assurez-vous que la cellule n'est pas vide
                        if (item == null || empty) {
                            setText(null);
                            setStyle(""); // Réinitialisez le style de la cellule
                        } else {
                            // Définir la limite de caractères pour passer à la ligne
                            int limit = 15; // Vous pouvez ajuster cette valeur en fonction de vos besoins

                            if (item.length() > limit) {
                                // Si la chaîne est trop longue, insérer des retours à la ligne
                                setText(item.substring(0, limit) + "\n" + item.substring(limit));
                            } else {
                                setText(item);
                            }

                            // Définissez la couleur de fond, centrez le contenu et le rend en gras
                            setStyle(

                                    "-fx-font-weight: bold;"+
                                    "-fx-font-size: 15"
                            );



                        }
                    }
                };
            }
        });


      /*  descriptionColumn.setCellFactory(new Callback<TableColumn<PanierItem, String>, TableCell<PanierItem, String>>() {
            @Override
            public TableCell<PanierItem, String> call(TableColumn<PanierItem, String> param) {
                return new TableCell<PanierItem, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        // Assurez-vous que la cellule n'est pas vide
                        if (item == null || empty) {
                            setText(null);
                            setStyle(""); // Réinitialisez le style de la cellule
                        } else {
                            // Définir la limite de caractères pour passer à la ligne
                            int limit = 15; // Vous pouvez ajuster cette valeur en fonction de vos besoins

                            if (item.length() > limit) {
                                // Si la chaîne est trop longue, insérer des retours à la ligne
                                setText(item.substring(0, limit) + "\n" + item.substring(limit));
                            } else {
                                setText(item);
                            }

                            // Définissez la couleur de fond, centrez le contenu et le rend en gras
                            setStyle(

                                            "-fx-font-weight: bold;"+
                                            "-fx-font-size: 14"
                            );



                        }
                    }
                };
            }
        });*/





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
                            double total = item.getMedicament().getPrixUnitaire() * newQte;
                            item.setTotIndividuel(total);

                            // Afficher l'icone de chargement apres le changement dans le textfield
                            showLoadingIcon();
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

                    // Set the text color to white
                    textField.setStyle("-fx-text-fill: green; -fx-font-size: 17; -fx-background-color: white");
                }
            }
        });


        // Vérifiez si le panier est vide et désactivez le bouton en conséquence
        if (panier.isEmpty()) {
            btnValider.setDisable(true);
        }






    }
}
