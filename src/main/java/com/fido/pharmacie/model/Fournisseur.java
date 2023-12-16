package com.fido.pharmacie.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Fournisseur {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty nom = new SimpleStringProperty();
    private final SimpleStringProperty adresse = new SimpleStringProperty();
    private final SimpleStringProperty telephone = new SimpleStringProperty();
    private final SimpleStringProperty email = new SimpleStringProperty();


    public Fournisseur(int id, String nom, String adresse, String telephone, String email) {
        this.id.set(id);
        this.nom.set(nom);
        this.adresse.set(adresse);
        this.telephone.set(telephone);
        this.email.set(email);
    }

    // Ajoutez les méthodes getter pour chaque propriété
    public int getId() {
        return id.get();
    }

    public String getNom() {
        return nom.get();
    }

    public String getAdresse() {
        return adresse.get();
    }

    public String getTelephone() {
        return telephone.get();
    }

    public String getEmail() {
        return email.get();
    }

    // Ajoutez les propriétés pour chaque méthode getter
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nomProperty() {
        return nom;
    }

    public SimpleStringProperty adresseProperty() {
        return adresse;
    }

    public SimpleStringProperty telephoneProperty() {
        return telephone;
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

}
