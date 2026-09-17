package com.toothtory;

import com.toothtory.infra.database.DatabaseInitializer;
import com.toothtory.components.Alerta;
import com.toothtory.components.Notificacao;
import com.toothtory.components.Sidebar;
import com.toothtory.components.TopBar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;

public class MainApp extends Application {
    private BorderPane root;
    private Sidebar sidebar;

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseInitializer.initialize();
        carregarFontes();
        Alerta.definirJanela(primaryStage);

        root = new BorderPane();
        root.getStyleClass().add("app-root");
        sidebar = new Sidebar(this);
        root.setLeft(sidebar);
        root.setTop(new TopBar());
        root.getChildren().add(Notificacao.criarContainer(root));

        carregarTela("dashboard");

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/toothtory.css").toExternalForm());
        primaryStage.setTitle("Toothory");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(640);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void carregarTela(String nome) {
        try {
            String fxml = String.format("/views/%s-view.fxml", nome);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            root.setCenter(loader.load());
            if (sidebar != null) {
                sidebar.selecionar(nome);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carregarFontes() {
        carregarFonte("/fonts/Inter-Regular.ttf");
        carregarFonte("/fonts/Inter-Medium.ttf");
        carregarFonte("/fonts/Inter-SemiBold.ttf");
        carregarFonte("/fonts/Poppins-SemiBold.ttf");
    }

    private void carregarFonte(String caminho) {
        try (InputStream is = getClass().getResourceAsStream(caminho)) {
            if (is != null) {
                Font.loadFont(is, 13);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
