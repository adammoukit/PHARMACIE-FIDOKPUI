package com.fido.pharmacie.model;

import java.sql.Date;
import java.time.LocalDate;

public class MedicamentStock {
    private String codeBarre;
    private String nomProduit;

    private String dosage;
    private String description;
    private double prixUnitaire;
    private String categorie;
    private String numeroLot;
    private java.sql.Date dateExpiration;

    private java.sql.Date dateReception;

    private int quantite;

    private String fournisseur , instructions ;

    public MedicamentStock(String codeBarre, String nomProduit, String dosage, String description, double prixUnitaire, String categorie, int quantite, String fournisseur, String instructions) {
        this.codeBarre = codeBarre;
        this.nomProduit = nomProduit;
        this.dosage = dosage;
        this.description = description;
        this.prixUnitaire = prixUnitaire;
        this.categorie = categorie;
        this.quantite = quantite;
        this.fournisseur = fournisseur;
        this.instructions = instructions;
    }

    public MedicamentStock(String codeBarre, String nomProduit, String dosage, double prixUnitaire, String categorie,  int quantite) {
        this.codeBarre = codeBarre;
        this.nomProduit = nomProduit;
        this.dosage = dosage;

        this.prixUnitaire = prixUnitaire;
        this.categorie = categorie;

        this.quantite = quantite;

    }

    public String getCodeBarre() {
        return codeBarre;
    }

    public void setCodeBarre(String codeBarre) {
        this.codeBarre = codeBarre;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public java.sql.Date getDateReception() {
        return dateReception;
    }

    public void setDateReception(Date dateReception) {
        this.dateReception = dateReception;
    }

    public String getDosage() {
        return dosage;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public String getDescription() {
        return description;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getNumeroLot() {
        return numeroLot;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setNumeroLot(String numeroLot) {
        this.numeroLot = numeroLot;
    }

    public java.sql.Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
