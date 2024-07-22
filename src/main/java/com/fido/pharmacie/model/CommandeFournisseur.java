package com.fido.pharmacie.model;

import javafx.scene.control.Button;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CommandeFournisseur {

    private String num_commande;
    private LocalDate date_commande;
    private BigDecimal montant_total;
    private String status;

    private Button action;

    public CommandeFournisseur(String num_commande, LocalDate date_commande, BigDecimal montant_total) {
        this.num_commande = num_commande;
        this.date_commande = date_commande;
        this.montant_total = montant_total;
        this.status = "En Cours.."; // Par défaut, vous pouvez ajuster cela
        this.action = new Button("Action"); // Bouton d'action par défaut

    }


    public Button getAction() {
        return action;
    }

    public void setAction(Button action) {
        this.action = action;
    }

    public String getNum_commande() {
        return num_commande;
    }

    public void setNum_commande(String num_commande) {
        this.num_commande = num_commande;
    }

    public LocalDate getDate_commande() {
        return date_commande;
    }

    public void setDate_commande(LocalDate date_commande) {
        this.date_commande = date_commande;
    }

    public BigDecimal getMontant_total() {
        return montant_total;
    }

    public void setMontant_total(BigDecimal montant_total) {
        this.montant_total = montant_total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
