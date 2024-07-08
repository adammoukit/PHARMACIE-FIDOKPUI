package com.fido.pharmacie.model;

import java.util.Date;

public class Stock {
    int id;
    String code_barres, numero_Lot;
    int qte;
    Date date_reception, date_expiration;

    public Stock() {
    }

    public Stock(int id, String code_barres, String numero_Lot, int qte, Date date_reception, Date date_expiration) {
        this.id = id;
        this.code_barres = code_barres;
        this.numero_Lot = numero_Lot;
        this.qte = qte;
        this.date_reception = date_reception;
        this.date_expiration = date_expiration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode_barres() {
        return code_barres;
    }

    public void setCode_barres(String code_barres) {
        this.code_barres = code_barres;
    }

    public String getNumero_Lot() {
        return numero_Lot;
    }

    public void setNumero_Lot(String numero_Lot) {
        this.numero_Lot = numero_Lot;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public Date getDate_reception() {
        return date_reception;
    }

    public void setDate_reception(Date date_reception) {
        this.date_reception = date_reception;
    }

    public Date getDate_expiration() {
        return date_expiration;
    }

    public void setDate_expiration(Date date_expiration) {
        this.date_expiration = date_expiration;
    }
}
