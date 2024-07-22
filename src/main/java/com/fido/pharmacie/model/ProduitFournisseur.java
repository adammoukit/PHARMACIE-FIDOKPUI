package com.fido.pharmacie.model;

public class ProduitFournisseur {
    private Long codeBarres;
    private String libelle;
    private double prixCession;
    private double prixPublic;



    private Integer qte;

    public ProduitFournisseur(Long codeBarres, String libelle, double prixCession, double prixPublic, Integer qte) {
        this.codeBarres = codeBarres;
        this.libelle = libelle;
        this.prixCession = prixCession;
        this.prixPublic = prixPublic;
        this.qte = qte;

    }

    public Double getTotal() {
        return prixPublic * qte;
    }

    public ProduitFournisseur(Long codeBarres, String libelle, double prixCession, double prixPublic) {
        this.codeBarres = codeBarres;
        this.libelle = libelle;
        this.prixCession = prixCession;
        this.prixPublic = prixPublic;
    }

    public Integer getQte() {
        return qte;
    }

    public void setQte(Integer qte) {
        this.qte = qte;
    }

    public Long getCodeBarres() {
        return codeBarres;
    }

    public void setCodeBarres(Long codeBarres) {
        this.codeBarres = codeBarres;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public double getPrixCession() {
        return prixCession;
    }

    public void setPrixCession(double prixCession) {
        this.prixCession = prixCession;
    }

    public double getPrixPublic() {
        return prixPublic;
    }

    public void setPrixPublic(double prixPublic) {
        this.prixPublic = prixPublic;
    }
}
