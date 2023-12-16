package com.fido.pharmacie.model;

import java.time.LocalDate;
import java.util.Date;

public class Commande {

    private String nomProduit;
    private String description;
    private String dosage;
    private LocalDate dateExpiration;
    private String fournisseur;
    private double prixProduit;
    private int quantite;

    public Commande(String nomProduit, String description, String dosage, LocalDate dateExpiration, String fournisseur, double prixProduit, int quantite) {
        this.nomProduit = nomProduit;
        this.description = description;
        this.dosage = dosage;
        this.dateExpiration = dateExpiration;
        this.fournisseur = fournisseur;
        this.prixProduit = prixProduit;
        this.quantite = quantite;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public String getDescription() {
        return description;
    }

    public String getDosage() {
        return dosage;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public double getPrixProduit() {
        return prixProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    public void setPrixProduit(double prixProduit) {
        this.prixProduit = prixProduit;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
