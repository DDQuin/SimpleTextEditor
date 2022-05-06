package com.ddquin.simpletexteditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SimpleEditorBoot extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Scene mainScene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("simple-editor-main.fxml"))), 1024, 768);
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
