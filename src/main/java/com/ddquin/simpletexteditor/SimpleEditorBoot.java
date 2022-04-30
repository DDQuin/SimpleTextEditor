package com.ddquin.simpletexteditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class SimpleEditorBoot extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene mainScene = new Scene(FXMLLoader.load(getClass().getResource("simple-editor-main.fxml")), 1024, 768);
        stage.setScene(mainScene);
        stage.setTitle("Simple Text Editor");
        stage.setWidth(1024);
        stage.setHeight(768);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
