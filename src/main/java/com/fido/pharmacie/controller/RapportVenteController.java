package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.Achat;
import com.fido.pharmacie.model.DataPoint;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.properties.TextAlignment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import javafx.stage.FileChooser;

public class RapportVenteController implements Initializable {

    @FXML
    private TableColumn<Achat, Hyperlink> actionColumn;
    @FXML
    private ComboBox<String> comboBoxPeriode;
    @FXML
    private TableColumn<Achat, Integer> id_venteColumn;
    @FXML
    private TableColumn<Achat, Timestamp> dateAchatColumn;
    @FXML
    private TableColumn<Achat, String> codeRecuColumn;
    @FXML
    private TableColumn<Achat, Double> totalColumn;
    @FXML
    private TableView<Achat> tableVente;
    @FXML
    private AnchorPane vueLineChart;
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private Button btnIprimer;
    @FXML
    private DatePicker Date_Debut;
    @FXML
    private DatePicker Date_Fin;
    @FXML
    private Button btn_Valider;
    @FXML
    private ImageView imgImprimer;
    @FXML
    private Label totalTransactionsLabel;

    private int startIndex = 0;  // Indice de départ pour la fenêtre glissante
    private int windowSize = 20; // Taille de la fenêtre glissante

    ObservableList<Achat> AchatObservableList = FXCollections.observableArrayList();

    Connection connectDB = DatabaseConnection.getConnection();

    private void afficherPrixVente(int venteId, double prix) {
        // Créer une boîte de dialogue d'information
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la vente");
        alert.setHeaderText("ID de vente: " + venteId);
        alert.setContentText("Prix de la vente: " + prix + " FCFA");

        // Afficher la boîte de dialogue
        alert.showAndWait();
    }

    // Méthode pour calculer le nombre total de transactions
    private void updateTotalTransactions() {
        int totalTransactions = AchatObservableList.size();
        totalTransactionsLabel.setText("" + totalTransactions);
    }

    private void genererEtEnregistrerPDF() {
        // Utiliser JavaFX FileChooser pour obtenir le chemin du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            try (PdfWriter writer = new PdfWriter(selectedFile);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                // Définir les marges du document (30px à gauche et à droite, 50px en haut et en bas)
                document.setMargins(30, 30, 50, 50);

                // Ajouter le titre "RAPPORT DE VENTE" avec soulignement
                Paragraph title = new Paragraph("RAPPORT DE VENTE").setBold();
                title.setUnderline();
                // Exemple d'utilisation de la propriété TextAlignment
                title.setTextAlignment(TextAlignment.CENTER);
                document.add(title);

                // Ajouter une ligne de soulignement après le titre
                document.add(new Paragraph().setUnderline().setMarginBottom(10));

                // Calculer la largeur disponible pour la table en fonction des marges
                float availableWidth = document.getPdfDocument().getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();

                // Créer une table pour les en-têtes et les valeurs
                Table table = new Table(4); // 4 colonnes pour id, dateAchat, codeRecu, total

                // Ajouter les en-têtes à la table avec une couleur de fond bleu clair
                addHeaderCell(table, "ID");
                addHeaderCell(table, "Date Achat");
                addHeaderCell(table, "Code Recu");
                addHeaderCell(table, "Total");

                // Ajouter les valeurs de la TableView à la table
                for (Achat achat : tableVente.getItems()) {
                    addCell(table, String.valueOf(achat.getId()));
                    addCell(table, achat.getDateAchat().toString());
                    addCell(table, achat.getCodeRecu());
                    // Ajouter " FCFA" devant chaque valeur dans la colonne "Total"
                    addCell(table, String.valueOf(achat.getTotal()) + " FCFA");
                }

                // Ajouter une dernière ligne pour le total
                Cell totalCell = new Cell(1, 3).add(new Paragraph("TOTAL"));
                totalCell.setTextAlignment(TextAlignment.CENTER);  // Centrer le texte
                table.addCell(totalCell);

                // Calculer la somme des valeurs de la colonne "Total"
                double totalSum = tableVente.getItems().stream().mapToDouble(Achat::getTotal).sum();
                // Ajouter la somme à la colonne "Total" de la dernière ligne
                Cell totalSumCell = new Cell().add(new Paragraph(String.valueOf(totalSum) + " FCFA"));
                totalSumCell.setTextAlignment(TextAlignment.CENTER);  // Centrer le texte
                totalSumCell.setBackgroundColor(ColorConstants.YELLOW);  // Définir la couleur de fond en jaune
                table.addCell(totalSumCell);

                // Ajuster la largeur de la table pour occuper l'espace disponible
                table.setWidth(availableWidth);

                // Ajouter la table au document PDF
                document.add(table);

                // Afficher un message de réussite
                System.out.println("PDF généré avec succès.");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour ajouter une cellule centrée à la table
    private void addCell(Table table, String content) {
        Cell cell = new Cell();

        // Ajouter "FCFA" après le prix dans la colonne "Total"
        if ("Total".equals(content)) {
            content += " FCFA";
        }

        // Ajouter "FCFA" devant les valeurs de la colonne "Total"
        if ("Total".equals(content)) {
            cell.add(new Paragraph("FCFA " + content));
        } else {
            cell.add(new Paragraph(content));
        }
        cell.setTextAlignment(TextAlignment.CENTER); // Centrer le contenu
        table.addCell(cell);
    }

    // Méthode pour ajouter une cellule centrée à la table avec couleur de fond bleu clair pour les en-têtes
    private void addHeaderCell(Table table, String content) {
        com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell();

        // Créer un paragraphe avec le contenu
        Paragraph paragraph = new Paragraph(content);

        // Centrer le texte à l'intérieur du paragraphe
        paragraph.setTextAlignment(TextAlignment.CENTER);

        // Ajouter le paragraphe à la cellule
        cell.add(paragraph);

        // Définir la couleur de fond de la cellule
        cell.setBackgroundColor(new DeviceRgb(173, 216, 230)); // Couleur de fond bleu clair

        // Ajouter la cellule à la table
        table.addHeaderCell(cell);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the font for the TableView, Application des styles CSS pout la couleur de la tableview
        tableVente.setStyle("-fx-base: rgb(158, 152, 69);");

        // Load the image for AjouterBtn into the existing ImageView (imgAjouter)
        String absolutePath2 = Paths.get("src/main/java/com/fido/pharmacie/controller/Image/PDF.png").toUri().toString();
        Image imageAjouter = new Image(absolutePath2);

        // Ajustez la taille de l'ImageView ici
        imgImprimer.setImage(imageAjouter);
        imgImprimer.setFitWidth(30); // Réglez la largeur souhaitée
        imgImprimer.setPreserveRatio(true); // Garantit que l'aspect ratio de l'image est conservé (le rapport largeur/hauteur)

        btnIprimer.setGraphic(imgImprimer);

        // Initialize startIndex to 0
        startIndex = 0;

        // Set windowSize to the desired initial number of products to display
        windowSize = 20;

        // Initialisation du tableau sans données
        tableVente.setItems(FXCollections.observableArrayList());

        // Ajoutez un gestionnaire d'événements pour le bouton btnImprimer
        btnIprimer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                genererEtEnregistrerPDF();
            }
        });

        // Add an event handler to the btn_Valider
        btn_Valider.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LocalDate startDate = Date_Debut.getValue();
                LocalDate endDate = Date_Fin.getValue();
                if (startDate != null && endDate != null) {
                    filterDataByDateRange(startDate, endDate);
                } else {
                    // Display an error message if dates are not selected
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de sélection de date");
                    alert.setHeaderText(null);
                    alert.setContentText("Veuillez sélectionner une date de début et une date de fin.");
                    alert.showAndWait();
                }
            }
        });

        // Setup TableColumns
        id_venteColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateAchatColumn.setCellValueFactory(new PropertyValueFactory<>("dateAchat"));
        codeRecuColumn.setCellValueFactory(new PropertyValueFactory<>("codeRecu"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Ajouter une colonne "Action" avec des liens "Voir Plus"
        actionColumn.setCellFactory(new Callback<TableColumn<Achat, Hyperlink>, TableCell<Achat, Hyperlink>>() {
            @Override
            public TableCell<Achat, Hyperlink> call(TableColumn<Achat, Hyperlink> param) {
                return new TableCell<Achat, Hyperlink>() {
                    final Hyperlink voirPlusLink = new Hyperlink("Voir Details");

                    {
                        voirPlusLink.setOnAction(event -> {
                            int index = getIndex();
                            if (index >= 0 && index < getTableView().getItems().size()) {
                                Achat achat = getTableView().getItems().get(index);
                                System.out.println("Voir Plus pour l'achat ID : " + achat.getId());
                                // Ajoutez ici la logique pour afficher plus d'informations sur l'achat

                                // Charger la vue DetailsVente.fxml
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fido/pharmacie/DetailsVente.fxml"));
                                Parent root;
                                try {
                                    root = loader.load();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return;
                                }

                                // Récupérer le contrôleur associé à la vue DetailsVente.fxml
                                DetailsVenteController detailsController = loader.getController();

                                // Initialiser les données de la vente dans le contrôleur
                                detailsController.initializeData(achat.getId());

                                // Afficher la nouvelle vue dans une nouvelle fenêtre
                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.show();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Hyperlink item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(voirPlusLink);
                        }
                    }
                };
            }
        });

        // Ajouter la liste observable à la TableView
        tableVente.setItems(AchatObservableList);

        // Update the total transactions label
        updateTotalTransactions();

        totalColumn.setCellFactory(col -> new TableCell<Achat, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    // Display the price with the symbol "FCFA"
                    setText(String.format("%.2f", item) + " FCFA");
                    setStyle("-fx-alignment: CENTER; -fx-text-fill: green; -fx-font-size: 15; -fx-font-weight: bold;"); // Centrer le texte
                }
            }
        });

        // Nettoyer les enfants du AnchorPane avant d'ajouter le LineChart
        vueLineChart.getChildren().clear();

        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        lineChart.setLegendVisible(true);
        lineChart.setCreateSymbols(true);
        lineChart.setHorizontalGridLinesVisible(true);
        lineChart.setVerticalGridLinesVisible(true);
        lineChart.setAnimated(true);
        lineChart.setHorizontalZeroLineVisible(true);
        lineChart.setCreateSymbols(true);

        // Ajouter les propriétés de redimensionnement pour que le LineChart prenne les dimensions du parent
        lineChart.prefWidthProperty().bind(vueLineChart.widthProperty());
        lineChart.prefHeightProperty().bind(vueLineChart.heightProperty());

        // Ajouter le LineChart à l'AnchorPane
        vueLineChart.getChildren().add(lineChart);
    }

    // Méthode pour charger les données en fonction des dates de début et de fin sélectionnées
    private void filterDataByDateRange(LocalDate startDate, LocalDate endDate) {
        String query = "SELECT ID, DATE_ACHAT, CODE_RECU, TOTAL FROM vente WHERE DATE_ACHAT BETWEEN ? AND ? ORDER BY DATE_ACHAT";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(query);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate.atTime(23, 59, 59)));

            ResultSet queryOutput = preparedStatement.executeQuery();

            AchatObservableList.clear();

            // Clear existing data in the LineChart
            lineChart.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Flux des ventes");

            // Create SimpleDateFormat with your desired date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            while (queryOutput.next()) {
                Integer queryIdVente = queryOutput.getInt("ID");
                java.sql.Timestamp queryDateAchat = queryOutput.getTimestamp("DATE_ACHAT");
                String queryCodeRecu = queryOutput.getString("CODE_RECU");
                Double queryTotal = queryOutput.getDouble("TOTAL");

                // Ajouter les données d'achat à la liste observable
                AchatObservableList.add(new Achat(queryIdVente, queryDateAchat, queryCodeRecu, queryTotal));

                // Ajouter les données au LineChart
                String formattedDate = dateFormat.format(queryDateAchat);
                DataPoint dataPoint = new DataPoint(queryIdVente, queryTotal);
                series.getData().add(new XYChart.Data<>(formattedDate, queryTotal, dataPoint));
            }

            // Ajouter la liste observable à la TableView
            tableVente.setItems(AchatObservableList);

            // Update the total transactions label
            updateTotalTransactions();

            // Ajouter la série de données au LineChart
            lineChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
