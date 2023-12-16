package com.fido.pharmacie.model;

import java.sql.Timestamp;

public class Achat {
    private int id;
    private Timestamp dateAchat;
    private String codeRecu;
    private double total;


    public Achat(int id, Timestamp dateAchat, String codeRecu, double total) {
        this.id = id;
        this.dateAchat = dateAchat;
        this.codeRecu = codeRecu;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public Timestamp getDateAchat() {
        return dateAchat;
    }

    public String getCodeRecu() {
        return codeRecu;
    }

    public double getTotal() {
        return total;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateAchat(Timestamp dateAchat) {
        this.dateAchat = dateAchat;
    }

    public void setCodeRecu(String codeRecu) {
        this.codeRecu = codeRecu;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
