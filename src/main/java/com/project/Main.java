package com.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        final int windowWidth = 800;
        final int windowHeight = 600;

        UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
        UtilsViews.addView(getClass(), "MainScreen", "/assets/ventana1.fxml");
        UtilsViews.addView(getClass(), "EncriptarArchivo", "/assets/ventana2.fxml");
        UtilsViews.addView(getClass(), "DesencriptarArchivo", "/assets/ventana3.fxml");

        Scene scene = new Scene(UtilsViews.parentContainer);

        UtilsViews.setView("MainScreen");

        stage.setScene(scene);
        // Cambio de titulo
        stage.setTitle("Encriptacion de archivos");
        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);
        stage.setResizable(false);
        stage.show();

        // Add icon only if not Mac
        if (!System.getProperty("os.name").contains("Mac")) {
            Image icon = new Image("file:assets/icons/icon.png");
            stage.getIcons().add(icon);
        }
    }
}