package com.fido.pharmacie.model;

import java.sql.Timestamp;

public class VenteData {
    private Timestamp dateAchat;
    private double total;

    public VenteData(Timestamp dateAchat, double total) {
        this.dateAchat = dateAchat;
        this.total = total;
    }

    public Timestamp getDateAchat() {
        return dateAchat;
    }

    public double getTotal() {
        return total;
    }
}

