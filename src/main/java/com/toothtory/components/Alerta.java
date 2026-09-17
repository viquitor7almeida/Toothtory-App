package com.toothtory.components;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

public class Alerta {
    private static Window janela;

    public static void definirJanela(Window janela) {
        Alerta.janela = janela;
    }

    public static boolean confirmar(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.YES, ButtonType.NO);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        if (janela != null) {
            alert.initOwner(janela);
        }
        alert.getDialogPane().getStylesheets()
                .add(Alerta.class.getResource("/styles/toothtory.css").toExternalForm());
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}
