package com.fido.pharmacie.model;

public class Produit {
    private String produitID;
    private String nomProduit;
    private int quantite;
    private double prixUnitaire;
    private double montantTotal;

    public Produit(String produitID, String nomProduit, int quantite, double prixUnitaire, double montantTotal) {
        this.produitID = produitID;
        this.nomProduit = nomProduit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.montantTotal = montantTotal;
    }

    // Ajoutez les getters et les setters selon vos besoins

    public String getProduitID() {
        return produitID;
    }

    public void setProduitID(String produitID) {
        this.produitID = produitID;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }
}

