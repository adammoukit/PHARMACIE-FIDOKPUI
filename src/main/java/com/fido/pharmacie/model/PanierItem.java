package com.fido.pharmacie.model;

import java.util.Date;

public class PanierItem {

     MedicamentStock medicament;
     int qte;
     Double totIndividuel;


    public PanierItem(MedicamentStock medicament, int qte, Double totIndividuel) {
        this.medicament = medicament;
        this.qte = qte;
        this.totIndividuel = totIndividuel;
    }

    public MedicamentStock getMedicament() {
        return medicament;
    }

    public int getQte() {
        return qte;
    }

    public Double getTotIndividuel() {
        return totIndividuel;
    }

    public void setMedicament(MedicamentStock medicament) {
        this.medicament = medicament;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public void setTotIndividuel(Double totIndividuel) {
        this.totIndividuel = totIndividuel;
    }
}
