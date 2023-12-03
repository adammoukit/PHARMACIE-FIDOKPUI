package com.fido.pharmacie.model;

import java.util.Date;

public class MedicamentSearch {
    Integer ID;
    String Nom_medicament, description, dosage;
    Double prix;
    Date date_expiration;
    Integer quantite;
   // Integer id_produitF;

    public MedicamentSearch(Integer ID, String nom_medicament, String description, String dosage, Double prix, Date date_expiration, Integer quantite) {
        this.ID = ID;
        Nom_medicament = nom_medicament;
        this.description = description;
        this.dosage = dosage;
        this.prix = prix;
        this.date_expiration = date_expiration;
        this.quantite = quantite;
       // this.id_produitF = id_produitF;
    }

    public Integer getID() {
        return ID;
    }

    public String getNom_medicament() {
        return Nom_medicament;
    }

    public String getDescription() {
        return description;
    }

    public String getDosage() {
        return dosage;
    }

    public Double getPrix() {
        return prix;
    }

    public java.sql.Date getDate_expiration() {
        return (java.sql.Date) date_expiration;
    }

    public Integer getQuantite() {
        return quantite;
    }




    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setNom_medicament(String nom_medicament) {
        Nom_medicament = nom_medicament;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public void setDate_expiration(Date date_expiration) {
        this.date_expiration = date_expiration;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }



}
