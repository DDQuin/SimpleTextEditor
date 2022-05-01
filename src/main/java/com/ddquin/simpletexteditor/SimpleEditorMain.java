package com.ddquin.simpletexteditor;

import javafx.event.EventHandler;
import javafx.event.EventType;
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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class SimpleEditorMain {

    private File fileOpen;

    private double screenWidth;
    private double screenHeight;

    @FXML
    private TextArea textArea;

    @FXML
    private Text lineCount;

    @FXML
    private Text hintText;

    @FXML
    private Text fileText;

    @FXML
    private MenuBar menuBar;


    @FXML
    public void initialize() {
        setUpMenus();
        setUpTextArea();
    }

    private void setUpMenus() {

    }

    private void setUpTextArea() {
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            KeyCombination increaseCombo = new KeyCharacterCombination("=", KeyCombination.SHORTCUT_DOWN);
            KeyCombination decreaseCombo = new KeyCharacterCombination("-", KeyCombination.SHORTCUT_DOWN);
            if (increaseCombo.match(keyEvent)) {
                changeFontSize(textArea.getFont().getSize() + 2);
            } else if (decreaseCombo.match(keyEvent)) {
                changeFontSize(textArea.getFont().getSize() - 2);
            }
        });
        textArea.setOnKeyTyped( event -> updateLineCount());
        textArea.setOnMouseClicked( event -> updateLineCount());
        updateLineCount();
    }



    @FXML
    public void chooseFile()  {
        FileChooser fileChooser = new FileChooser();
        fileOpen = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        // if no file chosen (user quit dialog)
        if (fileOpen == null) return;
        String content;
        try {
            content = Files.readString(fileOpen.toPath(), StandardCharsets.US_ASCII);
            hintText.setText("Opened " + fileOpen.getName());
            changeTitle(fileOpen.getName());
            textArea.setText(content);
            updateLineCount();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void saveFile() {
        if (fileOpen == null) {
            hintText.setText("No file open!");
            return;
        }
        try (PrintWriter out = new PrintWriter(fileOpen)) {
            out.println(textArea.getText());
            hintText.setText("Saved " + fileOpen.getName());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void closeFile() {
        if (fileOpen == null) {
            hintText.setText("No file open!");
            return;
        }
        hintText.setText("Closed " + fileOpen.getName());
        fileOpen = null;
        changeTitle("");
        textArea.setText("");
        updateLineCount();
    }

    private void updateLineCount() {
        lineCount.setText(" Lines: " + textArea.getText().lines().count());
    }

    private void changeTitle(String newTitle) {
        fileText.setText(newTitle);
    }


    private void changeFontSize(double newFontSize) {
        if (newFontSize < 10 || newFontSize > 40) return;
        textArea.setStyle("-fx-font-size: " + newFontSize);
    }

    private void showHintText(String text) {
        hintText.setText(text);
    }


    public double getScreenWidth() {
        return screenWidth;
    }

    public double getScreenHeight() {
        return screenHeight;
    }
}
