package com.fido.pharmacie.controller;

import com.fido.pharmacie.model.CommandeFournisseur;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class CommandeFournisseurCell extends ListCell<CommandeFournisseur> {
    @Override
    protected void updateItem(CommandeFournisseur commande, boolean empty) {
        super.updateItem(commande, empty);

        if (empty || commande == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Créer la mise en page de votre cellule personnalisée
            // Exemple : utiliser un HBox pour afficher les détails de la commande
            HBox box = new HBox();
            Label numCommandeLabel = new Label("N°Commande: " + commande.getNum_commande());
            Label dateLabel = new Label("Date de Commande: " + commande.getDate_commande());
            Label totalLabel = new Label("Total de commande: " + commande.getMontant_total());
            // Ajoutez d'autres labels pour status, action, etc.

            // Ajoutez les labels à votre mise en page
            box.getChildren().addAll(numCommandeLabel, dateLabel, totalLabel);

            setGraphic(box);
        }
    }
}
