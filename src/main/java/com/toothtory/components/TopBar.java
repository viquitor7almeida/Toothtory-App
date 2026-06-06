package com.toothtory.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class TopBar extends HBox {
    public TopBar() {
        setPadding(new Insets(15, 20, 15, 20));
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        Label title = new Label("Dentista na Vila");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label user = new Label("Dra. Cátia");
        getChildren().addAll(title, spacer, user);
    }
}