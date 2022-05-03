package com.ddquin.simpletexteditor;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
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


    //Middle Split Pane
    @FXML
    private TreeView<File> fileStructure;

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
        isSaved = true;
        timeToCloseHint = new Timer();
        Platform.runLater(() -> menuBar.getScene().getWindow().setOnCloseRequest(e -> {
            askToSave();
            if (timeToCloseHint != null) timeToCloseHint.cancel();
            System.exit(0);
        }));
        setUpFileStructure(new File("/Users/mariolink"));
        setUpTextArea();
    }


    private void setUpTextArea() {
        textArea.textProperty().addListener((observable, oldVal, newVal) -> {
            isSaved = false;
            updateBottomBar();
        });
        updateBottomBar();
    }

    private void setUpFileStructure(File root) {
        TreeItem<File> rootFile = new TreeItem<>(root);
        for (File file: Objects.requireNonNull(root.listFiles())) {
            TreeItem<File> fileItem = new TreeItem<>(file);
            rootFile.getChildren().add(fileItem);
        }
        fileStructure.setRoot(rootFile);
        fileStructure.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                TreeItem<File> item = fileStructure.getSelectionModel().getSelectedItem();
                askToSave();
                openFile(item.getValue());

            }
        });
    }


    @FXML
    public void increaseFont() {
        changeFontSize(textArea.getFont().getSize() + 2);
    }

    @FXML
    public void decreaseFont() {
        changeFontSize(textArea.getFont().getSize() - 2);
    }



    @FXML
    public void chooseFile()  {
        FileChooser fileChooser = new FileChooser();
        askToSave();
        openFile(fileChooser.showOpenDialog(menuBar.getScene().getWindow()));
        // if no file chosen (user quit dialog)


    }

    private void openFile(File fileToOpen) {
        if (fileToOpen == null) return;
        fileOpen = fileToOpen;
        String content;
        try {
            content = Files.readString(fileOpen.toPath(), StandardCharsets.US_ASCII);
            showHintText("Opened " + fileOpen.getName());
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
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showSaveDialog(null);
            if (selectedFile != null) {
                fileOpen = selectedFile;
                doSave();
            }
            return;
        }
        doSave();
    }

    private void askToSave() {
        if (isSaved) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("You have unsaved changes.");
        alert.setContentText("Save?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(type -> {
            if (type == ButtonType.YES) {
                saveFile();
            } else if (type == ButtonType.NO) {

            }
        });
    }

    private void doSave() {
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
        askToSave();
        if (fileOpen != null) {
            showHintText("Closed " + fileOpen.getName());
        }
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
