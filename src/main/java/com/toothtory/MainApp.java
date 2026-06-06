package com.toothtory;

import com.toothtory.infra.database.DatabaseInitializer;
import com.toothtory.components.Sidebar;
import com.toothtory.components.TopBar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    private BorderPane root;

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseInitializer.initialize();

        root = new BorderPane();
        root.setLeft(new Sidebar(this));
        root.setTop(new TopBar());

        carregarTela("dashboard");

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Toothory");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void carregarTela(String nome) {
        try {
            String fxml = String.format("/views/%s-view.fxml", nome);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            root.setCenter(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}