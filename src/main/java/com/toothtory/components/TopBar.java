package com.toothtory.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class TopBar extends HBox {
    public TopBar() {
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("topbar");

        Label wordmark = new Label("Toothtory");
        wordmark.getStyleClass().add("wordmark");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox chip = new HBox();
        chip.getStyleClass().add("chip-usuario");
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("avatar");
        Label inicial = new Label("C");
        inicial.getStyleClass().add("avatar-inicial");
        avatar.getChildren().add(inicial);
        Label nome = new Label("Dra. Cátia");
        nome.getStyleClass().add("chip-nome");
        chip.getChildren().addAll(avatar, nome);

        getChildren().addAll(wordmark, espaco, chip);
    }
}
