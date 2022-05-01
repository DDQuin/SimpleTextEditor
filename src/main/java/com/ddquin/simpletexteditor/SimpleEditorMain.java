package com.ddquin.simpletexteditor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

public class SimpleEditorMain {

    private File fileOpen;
    private double screenWidth;
    private double screenHeight;
    private boolean isSaved;

    //Menu
    @FXML
    private MenuBar menuBar;

    @FXML
    private CheckMenuItem darkMenu;


    @FXML
    private TextArea textArea;

    // Bottom Bar

    @FXML
    private Text lineCount;

    @FXML
    private Text hintText;

    @FXML
    private Text fileText;

    private Timer timeToCloseHint;


    @FXML
    public void initialize() {
        timeToCloseHint = new Timer();
        Platform.runLater(() -> menuBar.getScene().getWindow().setOnCloseRequest(e -> {
            if (timeToCloseHint != null) timeToCloseHint.cancel();
        }));

        setUpMenus();
        setUpTextArea();
    }

    private void setUpMenus() {
    }

    private void setUpTextArea() {
        textArea.textProperty().addListener((observable, oldVal, newVal) -> {
            isSaved = false;
            updateBottomBar();
        });
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            KeyCombination increaseCombo = new KeyCharacterCombination("=", KeyCombination.SHORTCUT_DOWN);
            KeyCombination decreaseCombo = new KeyCharacterCombination("-", KeyCombination.SHORTCUT_DOWN);
            if (increaseCombo.match(keyEvent)) {
                changeFontSize(textArea.getFont().getSize() + 2);
            } else if (decreaseCombo.match(keyEvent)) {
                changeFontSize(textArea.getFont().getSize() - 2);
            }
        });
        updateBottomBar();
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
            showHintText("Opened" + fileOpen.getName());
            setFileOpenText(fileOpen.getName());
            textArea.setText(content);
            isSaved = true;
            updateBottomBar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void saveFile() {
        if (fileOpen == null) {
            showHintText("No file open!");
            return;
        }
        try (PrintWriter out = new PrintWriter(fileOpen)) {
            out.println(textArea.getText());
            showHintText("Saved " + fileOpen.getName());
            isSaved = true;
            updateBottomBar();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void closeFile() {
        if (fileOpen == null) {
            showHintText("No file open!");
            return;
        }
        showHintText("Closed " + fileOpen.getName());
        fileOpen = null;
        setFileOpenText("");
        textArea.setText("");
    }

    @FXML
    public void setDarkMode() {
        if (darkMenu.isSelected()) {
            menuBar.getScene().getStylesheets().add("dark-mode.css");
        } else {
            menuBar.getScene().getStylesheets().remove("dark-mode.css");
        }
    }

    private void updateBottomBar() {
        lineCount.setText(" Lines: " + textArea.getText().lines().count());
        if (fileOpen != null && !isSaved) {
            setFileOpenText(fileOpen.getName() + " *");
        }
        if (fileOpen != null && isSaved) {
            setFileOpenText(fileOpen.getName());
        }
    }

    private void setFileOpenText(String newTitle) {
        fileText.setText(newTitle);
    }


    private void changeFontSize(double newFontSize) {
        if (newFontSize < 10 || newFontSize > 40) return;
        textArea.setStyle("-fx-font-size: " + newFontSize);
    }

    private void showHintText(String text) {
        hintText.setText(text);
        TimerTask closeHint = new TimerTask() {
            @Override
            public void run() {
                hintText.setText("");
            }
        };
        timeToCloseHint.schedule(closeHint, 3000);
    }

}
