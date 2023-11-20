package com.fido.pharmacie.controller;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CustomBar extends StackPane {
    private Rectangle bar;

    public CustomBar(double value, double maxValue, double barWidth, double barHeight) {
        this.bar = new Rectangle(barWidth, (value / maxValue) * barHeight);
        this.bar.setFill(Color.BLUE); // Couleur de la barre
        getChildren().add(bar);
    }
}
