package com.fido.pharmacie.model;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

public class DetailVente {

    private final IntegerProperty produitID;
    private final IntegerProperty quantite;
    private final DoubleProperty prixUnitaire;
    private final DoubleProperty montantTotal;

    public DetailVente(int produitID, int quantite, double prixUnitaire) {
        this.produitID = new SimpleIntegerProperty(produitID);
        this.quantite = new SimpleIntegerProperty(quantite);
        this.prixUnitaire = new SimpleDoubleProperty(prixUnitaire);
        this.montantTotal = new SimpleDoubleProperty(quantite * prixUnitaire);
    }

    public int getProduitID() {
        return produitID.get();
    }

    public IntegerProperty produitIDProperty() {
        return produitID;
    }

    public int getQuantite() {
        return quantite.get();
    }

    public IntegerProperty quantiteProperty() {
        return quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire.get();
    }

    public DoubleProperty prixUnitaireProperty() {
        return prixUnitaire;
    }

    public double getMontantTotal() {
        return montantTotal.get();
    }

    public DoubleProperty montantTotalProperty() {
        return montantTotal;
    }


}
