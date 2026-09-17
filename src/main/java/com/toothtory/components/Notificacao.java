package com.toothtory.components;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Notificacao {
    private static final VBox container = new VBox(8);
    private static final double LARGURA = 320;
    private static final double MARGEM_DIREITA = 24;
    private static final double TOPO = 76;

    private Notificacao() {
    }

    public static VBox criarContainer(Pane raiz) {
        container.getStyleClass().add("notificacao-container");
        container.setPrefWidth(LARGURA);
        container.setMinWidth(LARGURA);
        container.setMaxWidth(LARGURA);
        container.setLayoutY(TOPO);
        container.layoutXProperty().bind(raiz.widthProperty().subtract(LARGURA + MARGEM_DIREITA));
        container.setMouseTransparent(true);
        return container;
    }

    public static void sucesso(String mensagem) {
        mostrar(mensagem, "notificacao-sucesso", Duration.seconds(3));
    }

    public static void aviso(String mensagem) {
        mostrar(mensagem, "notificacao-aviso", Duration.seconds(4));
    }

    public static void erro(String mensagem) {
        mostrar(mensagem == null ? "Erro inesperado" : mensagem, "notificacao-erro", Duration.seconds(5));
    }

    private static void mostrar(String mensagem, String estilo, Duration duracao) {
        if (container.getParent() == null) {
            return;
        }

        Region indicador = new Region();
        indicador.getStyleClass().add("notificacao-indicador");

        Label texto = new Label(mensagem);
        texto.getStyleClass().add("notificacao-texto");
        texto.setWrapText(true);

        HBox cardo = new HBox(10);
        cardo.getStyleClass().addAll("notificacao", estilo);
        cardo.getChildren().addAll(indicador, texto);
        cardo.setOpacity(0);
        container.getChildren().add(cardo);

        FadeTransition entrada = new FadeTransition(Duration.millis(180), cardo);
        entrada.setFromValue(0);
        entrada.setToValue(1);
        entrada.play();

        PauseTransition pausa = new PauseTransition(duracao);
        pausa.setOnFinished(evento -> {
            FadeTransition saida = new FadeTransition(Duration.millis(280), cardo);
            saida.setFromValue(cardo.getOpacity());
            saida.setToValue(0);
            saida.setOnFinished(fim -> container.getChildren().remove(cardo));
            saida.play();
        });
        pausa.play();
    }
}
