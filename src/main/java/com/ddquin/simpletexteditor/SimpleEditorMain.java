package com.ddquin.simpletexteditor;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class SimpleEditorMain {

    private File fileOpen;

    private double screenWidth;
    private double screenHeight;

    @FXML
    private TextArea textArea;

    @FXML
    private MenuBar menuBar;


    @FXML
    public void initialize() {
        setMenus();
        setTextArea();
    }

    private void setMenus() {
        Menu file = new Menu("File");
        MenuItem openMenuItem = new MenuItem("Open");
        openMenuItem.setOnAction(e -> {
            try {
                chooseFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        file.getItems().add(openMenuItem);
        menuBar.getMenus().add(file);
    }

    private void setTextArea() {
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            KeyCombination increaseCombo = new KeyCharacterCombination("+", KeyCombination.CONTROL_DOWN);
            KeyCombination decreaseCombo = new KeyCharacterCombination("-", KeyCombination.CONTROL_DOWN);
            if (increaseCombo.match(keyEvent)) {
                changeFontSize(textArea.getFont().getSize() + 2);
            } else if (decreaseCombo.match(keyEvent)) {
                changeFontSize(textArea.getFont().getSize() - 2);
            }
        });
    }



    public void chooseFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileOpen = fileChooser.showOpenDialog((Stage) menuBar.getScene().getWindow());
        String content = Files.readString(fileOpen.toPath(), StandardCharsets.US_ASCII);
        textArea.setText(content);
    }

    private void changeFontSize(double newFontSize) {
        if (newFontSize < 10 || newFontSize > 40) return;
        textArea.setStyle("-fx-font-size: " + newFontSize);
    }


    public double getScreenWidth() {
        return screenWidth;
    }

    public double getScreenHeight() {
        return screenHeight;
    }
}
