package com.fido.pharmacie.controller;


import com.fido.pharmacie.model.CommandeFournisseur;
import com.fido.pharmacie.model.MedicamentStock;
import com.fido.pharmacie.model.ProduitFournisseur;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;

import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.fido.pharmacie.controller.DatabaseConnection.connection;

public class FournisseursController implements Initializable {

    public FournisseursController() {
        // Créer le ProgressIndicator dans le constructeur
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setPrefSize(20, 20); // Définir la taille à 40x40 pixels



    }

    @FXML
    private TableColumn<ProduitFournisseur, String> libelleColumn;

    @FXML
    private TableColumn<ProduitFournisseur, Double> prix_cession_Column;

    @FXML
    private TableColumn<ProduitFournisseur, Double> prix_public_column;

    @FXML
    private TableColumn<ProduitFournisseur, Long> produitColumn;


    @FXML
    private TableView<ProduitFournisseur> tableProduitUbipharm;

    @FXML
    private TextField rechercheTextfield;

    @FXML
    private Label nbr_produit_label;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnConfigurer_commande;

    @FXML
    private Label labelFournisseur;

    @FXML
    private ProgressIndicator ProdChargement;

    @FXML
    private Label nbrCommandeLabel;

    @FXML
    private VBox VboxProduit;

    @FXML
    private Tab MesCommandes_Tab;


    @FXML
    private ProgressIndicator progressIndicator;


    @FXML
    private ListView<CommandeFournisseur> commandesListview;



    private ConfigurerCommandeController configurerCommandeController;

    // Créer une instance de ConfigurerCommandeController



    private ObservableList<ProduitFournisseur> produits = FXCollections.observableArrayList();


    private void updateClearButtonVisibility(String text) {
        btnSupprimer.setVisible(!text.isEmpty());
    }

    private void rechercherProduit(){
        // Créez une liste filtrée liée à la liste observable des médicaments
        FilteredList<ProduitFournisseur> filteredList = new FilteredList<>(produits, p -> true);

        // Liez le predicat du FilteredList à la propriété text du TextField
        rechercheTextfield.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(medicament -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true; // Affichez tous les éléments si le champ de texte est vide
                    }

                    // Convertissez la recherche en minuscules et vérifiez si elle correspond à certains champs du médicament
                    String lowerCaseFilter = newValue.toLowerCase();
                    // Afficher ou masquer le bouton d'effacement en fonction de la saisie
                    updateClearButtonVisibility(newValue);
                    return medicament.getLibelle().toLowerCase().contains(lowerCaseFilter);
                            //|| medicament.getDescription().toLowerCase().contains(lowerCaseFilter)
                           // || medicament.getDosage().toLowerCase().contains(lowerCaseFilter);

                }));



        // Créez une liste triée liée à la liste filtrée
        SortedList<ProduitFournisseur> sortedList = new SortedList<>(filteredList);

        // Ajouter un écouteur à la liste triée pour mettre à jour le nombre de produits
        sortedList.addListener((ListChangeListener<ProduitFournisseur>) change -> {
            updateNombreProduitsLabel();
        });
        // Liez la liste triée à la TableView
        tableProduitUbipharm.setItems(sortedList);

        // Définissez le comparateur pour trier la liste
        sortedList.comparatorProperty().bind(tableProduitUbipharm.comparatorProperty());

    }


    @FXML
    private void handleConfigurerCommande(ActionEvent event) {
        // Cacher le bouton et afficher l'indicateur de progression
        //btnConfigurer_commande.setText(""); // Vider le texte du bouton
        btnConfigurer_commande.setGraphic(progressIndicator);
        //btnConfigurer_commande.setVisible(false);
        btnConfigurer_commande.setText("");
        progressIndicator.setVisible(true); // Afficher l'indicateur de progression

        // Simuler une tâche longue (vous pouvez remplacer cela par votre propre logique)
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simulez une tâche longue ici
                Thread.sleep(700); // Par exemple, attendez 1 seconde
                return null;
            }
        };

        // Une fois la tâche terminée, masquez l'indicateur de progression et rétablissez le texte du bouton
        task.setOnSucceeded(e -> {
            progressIndicator.setVisible(false);
           // btnConfigurer_commande.setVisible(true);
            btnConfigurer_commande.setText("Creer");
            btnConfigurer_commande.setGraphic(null);

            // Afficher la vue de configuration de commande dans une nouvelle fenêtre
            afficherVueConfigurerCommande();

        });

        // Exécutez la tâche dans un nouveau thread
        new Thread(task).start();
    }


    private void configureTableView() {
        produitColumn.setCellValueFactory(new PropertyValueFactory<>("codeBarres"));
        libelleColumn.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        prix_cession_Column.setCellValueFactory(new PropertyValueFactory<>("prixCession"));
        prix_public_column.setCellValueFactory(new PropertyValueFactory<>("prixPublic"));

        tableProduitUbipharm.setItems(produits);
    }


    public void loadProductsAsync(String filePath) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Charger les produits depuis le fichier Excel
                //updateProgress(0, 1);
                ProdChargement.setVisible(true);
                loadProductsFromExcel(filePath);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            // Mettre à jour l'interface après le chargement des données
            configureTableView();
            rechercherProduit();
            updateNombreProduitsLabel();
            ProdChargement.setVisible(false);
        });

        // Démarrer la tâche dans un nouveau thread
        Thread thread = new Thread(task);
        thread.start();
    }



    String filePath = "src/main/resources/liste_produits2.xlsx"; // Chemin vers votre fichier Excel




    // Méthode pour charger les produits depuis un fichier Excel

    private void loadProductsFromExcel(String filePath) {
        int idFournisseur = getIdFournisseurByName(labelFournisseur.getText());
        try (InputStream file = new FileInputStream(filePath)) {
            if (file == null) {
                throw new IllegalArgumentException("File not found: " + filePath);
            }
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Long codeBarre = (long) row.getCell(0).getNumericCellValue();
                String libelle = row.getCell(1).getStringCellValue();
                double prixCession = row.getCell(3).getNumericCellValue();
                double prixPublic = row.getCell(4).getNumericCellValue();

                ProduitFournisseur product = new ProduitFournisseur(codeBarre, libelle, prixCession, prixPublic);
                produits.add(product);

                // Vérification et mise à jour de la table ProduitFournisseur
                //updateOrInsertProduitFournisseur(codeBarre, libelle, prixCession, prixPublic, idFournisseur);
            }
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error loading Excel file", e);
        }
    }

    // Méthode pour mettre à jour le label du nombre total de produits
    private void updateNombreProduitsLabel() {


        Platform.runLater(() -> {
            nbr_produit_label.setText(Integer.toString(tableProduitUbipharm.getItems().size()));
        });

    }

    // Méthode pour récupérer l'id_fournisseur en fonction du nom du fournisseur
    private int getIdFournisseurByName(String fournisseurName) {
        String query = "SELECT id_fournisseur FROM Fournisseurs WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, fournisseurName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_fournisseur");
                } else {
                    throw new IllegalArgumentException("Fournisseur not found: " + fournisseurName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gestion des exceptions SQL
            throw new RuntimeException("Error retrieving id_fournisseur", e);
        }
    }

    //******************************************************************************

    // Méthode pour calculer le nombre de commandes
    public void calculerNombreCommandes() {
        String query = "SELECT COUNT(*) AS total FROM commandefournisseur";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                int total = resultSet.getInt("total");

                // Mettre à jour le label depuis le thread JavaFX principal
                Platform.runLater(() -> nbrCommandeLabel.setText(String.valueOf(total)));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Gérez les exceptions de manière appropriée
        }
    }

    //******************************************************************************
    public void loadCommandesFromDatabase() {
        List<CommandeFournisseur> commandes = new ArrayList<>();
        String query = "SELECT num_commande, date_commande, montant_total FROM commandefournisseur";

        try (
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String numCommande = resultSet.getString("num_commande");
                LocalDate dateCommande = resultSet.getDate("date_commande").toLocalDate();
                BigDecimal montantTotal = resultSet.getBigDecimal("montant_total");

                CommandeFournisseur commande = new CommandeFournisseur(numCommande, dateCommande, montantTotal);
                commandes.add(commande);
            }

            // Mettre à jour l'interface graphique depuis le thread JavaFX principal
            Platform.runLater(() -> commandesListview.setItems(FXCollections.observableArrayList(commandes)));

            // Calculer le nombre de commandes
            calculerNombreCommandes();

        } catch (SQLException e) {
            e.printStackTrace(); // Gérez les exceptions de manière appropriée
        }
    }

    //***********************************************************************************

    // Méthode pour mettre à jour ou insérer un produit dans la table ProduitFournisseur
    private void updateOrInsertProduitFournisseur(long codeBarre, String libelle, double prixCession, double prixPublic, int idFournisseur) {
        try {
            // Requête pour vérifier si le produit existe déjà
            String selectQuery = "SELECT * FROM ProduitFournisseur WHERE produit = ? AND id_fournisseur = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setLong(1, codeBarre);
                selectStmt.setInt(2, idFournisseur);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        // Le produit existe, exécuter la mise à jour
                        String updateQuery = "UPDATE ProduitFournisseur SET libelle = ?, prixCession = ?, prixPublic = ? WHERE produit = ? AND id_fournisseur = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, libelle);
                            updateStmt.setDouble(2, prixCession);
                            updateStmt.setDouble(3, prixPublic);
                            updateStmt.setLong(4, codeBarre);
                            updateStmt.setInt(5, idFournisseur);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        // Le produit n'existe pas, exécuter l'insertion
                        String insertQuery = "INSERT INTO ProduitFournisseur (produit, libelle, prixCession, prixPublic, id_fournisseur) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                            insertStmt.setLong(1, codeBarre);
                            insertStmt.setString(2, libelle);
                            insertStmt.setDouble(3, prixCession);
                            insertStmt.setDouble(4, prixPublic);
                            insertStmt.setInt(5, idFournisseur);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gestion des exceptions SQL
        }
    }



    private void setBouton(){
        String absolutePath1 = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Close.png").toUri().toString();

        final ImageView CloseIcon = new ImageView(new Image(absolutePath1));

        CloseIcon.setFitWidth(20);
        CloseIcon.setPreserveRatio(true);
        btnSupprimer.setGraphic(CloseIcon);

        // Ajouter un gestionnaire d'événements sur le bouton clearButton
        btnSupprimer.setOnAction(event -> {
            // Effacer le contenu du TextField rechercherTxtF
            rechercheTextfield.clear();
        });


    }

    private void afficherVueConfigurerCommande() {
        try {
            // Charger la vue de configuration de commande
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/ConfigurerCommande.fxml"));
            AnchorPane vueConfigurerCommande = loader.load();



            // Obtenir le contrôleur associé à la vue
            ConfigurerCommandeController configurerCommandeController = loader.getController();


            // Définir le nom du fournisseur dans le contrôleur de configuration de commande
            String nomFournisseur = labelFournisseur.getText();
            configurerCommandeController.setNomFournisseur(nomFournisseur);

            // Passer la référence de FournisseurController
            configurerCommandeController.setFournisseurController(this);

            // Créer une nouvelle scène avec la vue de configuration de commande
            Scene scene = new Scene(vueConfigurerCommande);

            // Créer une nouvelle fenêtre (Stage)
            Stage newStage = new Stage();
            newStage.setTitle("Configurer une commande");
            newStage.setScene(scene);

            String absolutePath = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/Plus.png").toUri().toString();
            newStage.getIcons().add(new Image(absolutePath));


            // Désactiver la redimension et la maximisation de la fenêtre
            newStage.setResizable(false);

            // Afficher la nouvelle fenêtre
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<CommandeFournisseur> commandesList;





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //***************************************

        // Initialisation de la ListView
        commandesListview.setItems(FXCollections.observableArrayList());

        // Exécution de la récupération des données dans un thread séparé
        Thread loadDataThread = new Thread(this::loadCommandesFromDatabase);
        loadDataThread.setDaemon(true); // Pour que le thread se termine lorsque l'application se ferme
        loadDataThread.start();

        //*****************************************

        configureTableView();
      //  loadProductsFromExcel(filePath);
        rechercherProduit();
        updateNombreProduitsLabel();
        // Ajouter un écouteur à la liste observable de produits
        produits.addListener((ListChangeListener<ProduitFournisseur>) change -> {
            while (change.next()) {
                updateNombreProduitsLabel();
            }
        });

        loadProductsAsync(filePath);


        // Rendre le bouton invisible à l'initialisation
        btnSupprimer.setVisible(false);
        setBouton();



        progressIndicator.setVisible(false);

        //*********************************************************************************
        commandesList = FXCollections.observableArrayList();
        commandesListview.setItems(commandesList);



        //**************************************************************************



        commandesListview.setCellFactory(new Callback<ListView<CommandeFournisseur>, ListCell<CommandeFournisseur>>() {
            @Override
            public ListCell<CommandeFournisseur> call(ListView<CommandeFournisseur> param) {
                return new ListCell<CommandeFournisseur>() {
                    @Override
                    protected void updateItem(CommandeFournisseur item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(7); // Espacement de 7
                            hBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5px;"); // Fond gris
                            hBox.setAlignment(Pos.CENTER_LEFT); // Alignement à gauche

                            Label numCommandeLabel = createLabel(String.valueOf(item.getNum_commande()));
                            Label dateCommandeLabel = createLabel(item.getDate_commande().toString());
                            Label montantTotalLabel = createLabel(item.getMontant_total().toString());
                            Label statusLabel = createLabel(item.getStatus());
                            Button actionButton = item.getAction();

                            // Configurer les labels pour utiliser toute la largeur disponible
                            HBox.setHgrow(numCommandeLabel, Priority.ALWAYS);
                            HBox.setHgrow(dateCommandeLabel, Priority.ALWAYS);
                            HBox.setHgrow(montantTotalLabel, Priority.ALWAYS);
                            HBox.setHgrow(statusLabel, Priority.ALWAYS);

                            // Configurer le bouton pour utiliser l'espace restant
                            HBox.setHgrow(actionButton, Priority.NEVER); // Le bouton n'occupe pas l'espace supplémentaire

                            hBox.getChildren().addAll(numCommandeLabel, dateCommandeLabel, montantTotalLabel, statusLabel, actionButton);

                            AnchorPane anchorPane = new AnchorPane();
                            AnchorPane.setTopAnchor(hBox, 0.0);
                            AnchorPane.setBottomAnchor(hBox, 0.0);
                            AnchorPane.setLeftAnchor(hBox, 0.0);
                            AnchorPane.setRightAnchor(hBox, 0.0);

                            anchorPane.getChildren().add(hBox);

                            // Lier la largeur du HBox à celle de l'AnchorPane
                            hBox.prefWidthProperty().bind(anchorPane.widthProperty());

                            setGraphic(anchorPane);
                            setText(null); // Pour ne pas afficher de texte en plus du graphique
                        }
                    }

                    private Label createLabel(String text) {
                        Label label = new Label(text);
                        label.setMaxWidth(Double.MAX_VALUE);
                        label.setAlignment(Pos.CENTER_LEFT);
                        return label;
                    }
                };
            }
        });

        // Charger les données depuis la base de données
        loadCommandesFromDatabase();
        //**********************************************************************************


    }
}
