package com.ddquin.simpletexteditor;

import javafx.fxml.FXML;
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

    @FXML
    private MenuBar menuBar;

    private double screenWidth;
    private double screenHeight;
    private boolean isSaved;



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
        menuBar.getScene().getWindow().setOnCloseRequest(e -> {
            if (timeToCloseHint != null) timeToCloseHint.cancel();
        });
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
