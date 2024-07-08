package com.fido.pharmacie.model;


public class MedicamentSearch {

    String code_barre, nomProduit, description, fournisseur, dosage,
            instructions_utilisation, categorie_produit;
    Double prix;


    public MedicamentSearch(String code_barre, String nomProduit, String description, String fournisseur, String dosage, String instructions_utilisation, String categorie_produit, Double prix) {
        this.code_barre = code_barre;
        this.nomProduit = nomProduit;
        this.description = description;
        this.fournisseur = fournisseur;
        this.dosage = dosage;
        this.instructions_utilisation = instructions_utilisation;
        this.categorie_produit = categorie_produit;
        this.prix = prix;
    }

    public MedicamentSearch(String code_barre, String nomProduit, String description, String dosage, String categorie_produit, Double prix) {
        this.code_barre = code_barre;
        this.nomProduit = nomProduit;
        this.description = description;

        this.dosage = dosage;
        this.instructions_utilisation = instructions_utilisation;
        this.categorie_produit = categorie_produit;
        this.prix = prix;
    }

    public MedicamentSearch() {
    }


    public String getCode_barre() {
        return code_barre;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public String getDescription() {
        return description;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public String getInstructions_utilisation() {
        return instructions_utilisation;
    }

    public String getCategorie_produit() {
        return categorie_produit;
    }



    public Double getPrix() {
        return prix;
    }

    public String getDosage() {
        return dosage;
    }



    public void setCode_barre(String code_barre) {
        this.code_barre = code_barre;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFournisseur(String fabriquant) {
        this.fournisseur = fabriquant;
    }

    public void setInstructions_utilisation(String instructions_utilisation) {
        this.instructions_utilisation = instructions_utilisation;
    }

    public void setCategorie_produit(String categorie_produit) {
        this.categorie_produit = categorie_produit;
    }


    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
}
