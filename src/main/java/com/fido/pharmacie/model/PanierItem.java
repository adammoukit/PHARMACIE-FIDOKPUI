package com.fido.pharmacie.model;

import java.util.Date;

public class PanierItem {

     MedicamentSearch medicament;
     int qte;
     Double totIndividuel;


    public PanierItem(MedicamentSearch medicament, int qte, Double totIndividuel) {
        this.medicament = medicament;
        this.qte = qte;
        this.totIndividuel = totIndividuel;
    }

    public MedicamentSearch getMedicament() {
        return medicament;
    }

    public int getQte() {
        return qte;
    }

    public Double getTotIndividuel() {
        return totIndividuel;
    }

    public void setMedicament(MedicamentSearch medicament) {
        this.medicament = medicament;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public void setTotIndividuel(Double totIndividuel) {
        this.totIndividuel = totIndividuel;
    }
}
