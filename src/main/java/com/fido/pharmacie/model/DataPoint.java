package com.fido.pharmacie.model;

public class DataPoint {
    private int venteId;
    private double prix;

    public DataPoint(int venteId, double prix) {
        this.venteId = venteId;
        this.prix = prix;
    }

    public int getVenteId() {
        return venteId;
    }

    public double getPrix() {
        return prix;
    }
}
