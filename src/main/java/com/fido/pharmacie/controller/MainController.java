package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.image.Image;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Date;




public class MainController implements Initializable {
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
    private AnchorPane mainContainer;


    @FXML
    private Menu menuRapport;

    @FXML
    private MenuItem menuItemStock;


    @FXML
    private MenuItem menuItemVente;





    @FXML
    private void handleSaveAction(ActionEvent event) {
        DatabaseConnection.backupDatabase();
    }



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


    // LES METHODES QUI SONT APPELE LORSQU'ON CLIC SUR LES BOUTON POUR AFFICHER LES VUES DANS LE CONTAINER AnchorPane
    public void afficherVueMedicaments() {
        chargerVueDansContainer("/com/fido/pharmacie/Medicament.fxml");
    }



    public void afficherVuePanier() {
        chargerVueDansContainer("/com/fido/pharmacie/Panier.fxml");
    }


    public void afficherDashboard() {
        chargerVueDansContainer("/com/fido/pharmacie/Dashboard.fxml");
    }


    public void afficherVueRapportStock() {
        chargerVueDansContainer("/com/fido/pharmacie/RapportStocK.fxml");
    }


    public void afficherVueRapportVente() {
        chargerVueDansContainer("/com/fido/pharmacie/RapportVente.fxml");
    }


    public void afficherVueFournisseurs() {
        chargerVueDansContainer("/com/fido/pharmacie/Fournisseurs.fxml");
    }


    /*private void chargerVueDansContainer(String fichierFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fichierFXML));
            AnchorPane vue = loader.load();
            medicamentContainer.getChildren().setAll(vue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */

    //   ICI LA METHODE LORSQU'ON CLIC SUR UN BOUTON CA CHANGE DE VUE
    private void chargerVueDansContainer(String fichierFXML) {
        try {
            InputStream fxmlStream = getClass().getResourceAsStream(fichierFXML);
            if (fxmlStream == null) {
                throw new IOException("Fichier FXML non trouvé: " + fichierFXML);
            }

            FXMLLoader loader = new FXMLLoader();
            AnchorPane vue = loader.load(fxmlStream);
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(vue);

            // Optionnel : pour que le contenu s'agrandisse avec mainContainer
            // ICI C'EST POUR QUE LE CONTENU S'AGRANDISSE AVEC LE CONTENEUR mainContainer
            AnchorPane.setTopAnchor(vue, 0.0);
            AnchorPane.setBottomAnchor(vue, 0.0);
            AnchorPane.setLeftAnchor(vue, 0.0);
            AnchorPane.setRightAnchor(vue, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // LA METHODE POUR RECUPERER LES FOURNISSEURS ET LES METTRE DANS LE COMBOBOX DE LA BOITE DE DIALOG
    public static List<String> getFournisseurs() {
        List<String> fournisseurs = new ArrayList<>();

        String query = "SELECT Nom FROM fournisseurs"; // Assurez-vous que la table dans votre base de données s'appelle "fournisseur"
        try (PreparedStatement preparedStatement = DatabaseConnection.getConnection().prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String nomFournisseur = resultSet.getString("Nom");
                fournisseurs.add(nomFournisseur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérez les erreurs de requête ici
        }
        return fournisseurs;
    }

    //
    //    ICI LA METHODE POUR APPELER DE DIALOG DE SAISIE D'INFORMATION
    //
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
            stage.getIcons().add(new Image("C:/Users/DELL/IdeaProjects/Pharmacie/src/main/resources/Image/Plus.png")); // Remplacez le chemin par le chemin de votre icône


            // Afficher le dialogue et attendre que l'utilisateur agisse
            Optional<MedicamentSearch> result = dialog.showAndWait();

            if (result.isPresent()) {

                MedicamentSearch medicament = result.get();
                if (medicament != null) {
                    // L'utilisateur a cliqué sur "OK" et les données sont valides
                    // Faites quelque chose avec l'objet MedicamentSearch, par exemple, l'ajouter à une liste ou à une base de données

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Produit Ajouté");
                    alert.setHeaderText(null);
                    alert.setContentText("Le produit a été ajouté à la base de données avec succès.");
                    alert.showAndWait();
                } else {
                    // L'utilisateur a cliqué sur "OK" mais les données ne sont pas valides (des champs sont vides)
                    // Aucune action requise ici, l'alerte a déjà été affichée dans getAddProductData()
                }

            }else  {

            }
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement du dialogue ici
        }
    }





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connection connectDB = DatabaseConnection.getConnection();


            try {
                InputStream fxmlStream = getClass().getResourceAsStream("/com/fido/pharmacie/Dashboard.fxml");


                FXMLLoader loader = new FXMLLoader();
                AnchorPane vue = loader.load(fxmlStream);
                mainContainer.getChildren().clear();
                mainContainer.getChildren().add(vue);

                // Optionnel : pour que le contenu s'agrandisse avec mainContainer
                // ICI C'EST POUR QUE LE CONTENU S'AGRANDISSE AVEC LE CONTENEUR mainContainer
                AnchorPane.setTopAnchor(vue, 0.0);
                AnchorPane.setBottomAnchor(vue, 0.0);
                AnchorPane.setLeftAnchor(vue, 0.0);
                AnchorPane.setRightAnchor(vue, 0.0);



                // Ajoutez un gestionnaire d'événements au menu 'menuItemStock'
                menuItemStock.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        // Appeler la méthode pour afficher la vue de rapport de stock
                        afficherVueRapportStock();
                    }
                });

                // Ajoutez un gestionnaire d'événements au menu 'menuItemStock'
                menuItemVente.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        // Appeler la méthode pour afficher la vue de rapport de stock
                        afficherVueRapportVente();
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }






    }
}
