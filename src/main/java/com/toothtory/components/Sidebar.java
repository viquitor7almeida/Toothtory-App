package com.toothtory.components;

import com.toothtory.MainApp;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;

public class Sidebar extends VBox {
    private final MainApp mainApp;
    private final Map<String, Button> itens = new LinkedHashMap<>();

    public Sidebar(MainApp mainApp) {
        this.mainApp = mainApp;
        setPrefWidth(210);
        getStyleClass().add("sidebar");

        VBox cabecalho = new VBox();
        cabecalho.getStyleClass().add("sidebar-cabecalho");
        Label logo = new Label("Toothtory");
        logo.getStyleClass().add("sidebar-logo");
        Label subtitulo = new Label("Gestão Odontológica");
        subtitulo.getStyleClass().add("sidebar-subtitulo");
        cabecalho.getChildren().addAll(logo, subtitulo);

        Region divisor = new Region();
        divisor.getStyleClass().add("sidebar-divisor");
        VBox.setMargin(divisor, new Insets(18, 8, 14, 8));

        getChildren().addAll(cabecalho, divisor);
        adicionarItem("dashboard", "Home");
        adicionarItem("pacientes", "Pacientes");
        adicionarItem("procedimentos", "Procedimentos");
        adicionarItem("consultas", "Consultas");
        adicionarItem("financeiro", "Financeiro");
    }

    private void adicionarItem(String tela, String rotulo) {
        Button item = new Button(rotulo);
        item.getStyleClass().add("sidebar-item");
        item.setOnAction(e -> mainApp.carregarTela(tela));
        itens.put(tela, item);
        getChildren().add(item);
    }

    public void selecionar(String tela) {
        itens.values().forEach(item -> item.getStyleClass().remove("ativo"));
        Button item = itens.get(tela);
        if (item != null) {
            item.getStyleClass().add("ativo");
        }
    }
}
