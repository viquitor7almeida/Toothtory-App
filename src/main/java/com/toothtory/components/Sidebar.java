package com.toothtory.components;

import com.toothtory.MainApp;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Sidebar extends VBox {
    private final MainApp mainApp;

    public Sidebar(MainApp mainApp) {
        this.mainApp = mainApp;
        setPrefWidth(200);
        setPadding(new Insets(20));
        setSpacing(10);
        setStyle("-fx-background-color: #2c3e50;");

        Text logo = new Text("🦷");
        logo.setStyle("-fx-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnHome = criarBotao("Home");
        Button btnPacientes = criarBotao("Pacientes");
        Button btnProcedimentos = criarBotao("Procedimentos");
        Button btnConsultas = criarBotao("Consultas");

        btnHome.setOnAction(e -> this.mainApp.carregarTela("dashboard"));
        btnPacientes.setOnAction(e -> this.mainApp.carregarTela("pacientes"));
        btnProcedimentos.setOnAction(e -> this.mainApp.carregarTela("procedimentos"));
        btnConsultas.setOnAction(e -> this.mainApp.carregarTela("consultas"));

        getChildren().addAll(logo, btnHome, btnPacientes, btnProcedimentos, btnConsultas);
    }

    private Button criarBotao(String texto) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 10; -fx-cursor: hand;");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }
}