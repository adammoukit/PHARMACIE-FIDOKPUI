package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.MedicamentSearch;
import com.fido.pharmacie.HelloApplication;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.image.Image;
import javafx.util.Duration;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



public class MainController implements Initializable {




    private Stage primaryStage;

    @FXML
    private AnchorPane mainContainer;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private MenuItem menuItemStock;


    @FXML
    private MenuItem menuItemVente;

    @FXML
    private Label Heure;

    @FXML
    private Label dateDuJour;

    @FXML
    private Label nomUtilsateur;

    @FXML
    private Button btnLog_out;

    private HelloApplication mainApp;

    @FXML
    private Menu notificationMenu;

    private Label badgeLabel;

    private DashboardController dashboardControllerInstance;

    DashboardController dashboardCont = new DashboardController();



    public void setMainApp(HelloApplication mainApp) {
        this.mainApp = mainApp;
    }





    @FXML
    private void handleSaveAction(ActionEvent event) {
        //DatabaseConnection.backupDatabase();
    }


    @FXML
    private void handleLogOut() throws IOException {
        Stage stage = (Stage) btnLog_out.getScene().getWindow();
        stage.close();


        // Recharger l'écran de connexion
        if (mainApp != null) {
            mainApp.showLoginView();
        }


    }

    public void updateUserInfo(String username) {
        // Mettre à jour le label du nom d'utilisateur
        nomUtilsateur.setText(username);

        // Mettre à jour le label de la date du jour
        dateDuJour.setText("" + LocalDate.now());

        // Mettre à jour le label de l'heure au format HH:mm:ss
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            Heure.setText(currentTime.format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
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
        chargerVueDansContainer("/com/fido/pharmacie/RapportStock.fxml");
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
    /*private void chargerVueDansContainer(String fichierFXML) {
        try {
            InputStream fxmlStream = getClass().getResourceAsStream(fichierFXML);
            if (fxmlStream == null) {
                throw new IOException("Fichier FXML non trouvé: " + fichierFXML);
            }

            FXMLLoader loader = new FXMLLoader();
            AnchorPane vue = loader.load(fxmlStream);
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(vue);

            // Passer la référence du Stage principal au contrôleur de la vue chargée
            Object controller = loader.getController();
            if (controller instanceof MedicamentController) {
                ((MedicamentController) controller).setPrimaryStage(primaryStage);
            }

            // Optionnel : pour que le contenu s'agrandisse avec mainContainer
            AnchorPane.setTopAnchor(vue, 0.0);
            AnchorPane.setBottomAnchor(vue, 0.0);
            AnchorPane.setLeftAnchor(vue, 0.0);
            AnchorPane.setRightAnchor(vue, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */

    private void chargerVueDansContainer(String fichierFXML) {
        try {
            InputStream fxmlStream = getClass().getResourceAsStream(fichierFXML);
            if (fxmlStream == null) {
                throw new IOException("Fichier FXML non trouvé: " + fichierFXML);
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(c -> {
                if (c == MedicamentController.class) {
                    return new MedicamentController(LoginPageController.getCurrentUser());
                } else if (c == DashboardController.class) {
                    //return new DashboardController(); // ou autre instance de contrôleur

                    dashboardControllerInstance = new DashboardController();
                    return dashboardControllerInstance;
                } else if (c == PanierController.class) {

                   // return new PanierController(); // ou autre instance de contrôleur

                    /*PanierController panierController = new PanierController();
                    panierController.setDashboardController(dashboardControllerInstance); // Inject the dashboard controller
                    return panierController;*/

                    return new PanierController(dashboardCont);

                } else if (c == RapportVenteController.class) {
                    return new RapportVenteController(); // ou autre instance de contrôleur
                } else if (c == LoginPageController.class) {
                    return new LoginPageController(); // ou autre instance de contrôleur
                } else if (c == FournisseursController.class) {
                    return new FournisseursController();
                } else {
                    throw new IllegalArgumentException("Contrôleur inconnu : " + c.getName());
                }
            });
            AnchorPane vue = loader.load(fxmlStream);
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(vue);

           /* // Passer la référence du Stage principal au contrôleur de la vue chargée
            Object controller = loader.getController();
            if (controller instanceof MedicamentController) {
                ((MedicamentController) controller).setPrimaryStage(primaryStage);
            }*/

           /* Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                DashboardController dashboardControllerInstance = (DashboardController) controller;
                // Si vous chargez PanierController, passez-lui une référence de DashboardController
                if (controller instanceof PanierController) {
                    ((PanierController) controller).setDashboardController(dashboardControllerInstance);
                }
            }*/


            // Optionnel : pour que le contenu s'agrandisse avec mainContainer
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




    public void updateBadge(int count) {
        if (count > 0) {
            badgeLabel.setText(String.valueOf(count));
            badgeLabel.setVisible(true);
        } else {
            badgeLabel.setVisible(false);
        }
    }





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {





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



        badgeLabel = new Label();
        badgeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        badgeLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 2px 6px; -fx-background-radius: 10;");
        updateBadge(5); // initial count

        StackPane stack = new StackPane();
        stack.getChildren().addAll(badgeLabel);
        notificationMenu.setGraphic(stack);


    }
}
