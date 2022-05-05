package com.ddquin.simpletexteditor;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Random;
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
    private TreeView<TreeFile> fileStructure;

    @FXML
    private TextArea textArea;

    // Bottom Bar

    @FXML
    private Label lineCount;

    @FXML
    private Label hintText;

    @FXML
    private Label fileText;

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
        //setUpFileStructure(new File("/Users/mariolink/Documents/JavaScript stuff/other-site"));
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
        TreeItem<TreeFile> rootFile = new TreeItem<>(new TreeFile(root));
        Image dirImage = new Image(getClass().getResourceAsStream("filedir.png"));
        rootFile.setGraphic(new ImageView(dirImage));
        addFiles(rootFile);
        fileStructure.setRoot(rootFile);
        fileStructure.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                TreeItem<TreeFile> item = fileStructure.getSelectionModel().getSelectedItem();
                if (item.getValue().getFile().isDirectory()) return;
                askToSave();
                openFile(item.getValue().getFile());

            }
        });
    }

    private void addFiles(TreeItem<TreeFile> root) {
        if (root.getValue().getFile().isFile()) return;
        for (File file: root.getValue().getFile().listFiles()) {
            TreeItem<TreeFile> fileItem = new TreeItem<>(new TreeFile(file));

            if (file.isDirectory()) {
                Image dirImage = new Image(getClass().getResourceAsStream("filedir.png"));
                fileItem.setGraphic(new ImageView(dirImage));
                addFiles(fileItem);
            } else {
                Image fileImage = new Image(getClass().getResourceAsStream("filetext.png"));
               fileItem.setGraphic(new ImageView(fileImage));
            }
            root.getChildren().add(fileItem);
        }
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
        String content;
        try {
            content = Files.readString(fileToOpen.toPath(), StandardCharsets.US_ASCII);
            fileOpen = fileToOpen;
            showHintText("Opened " + fileOpen.getName());
            setFileOpenText(fileOpen.getName());
            textArea.setText(content);
            isSaved = true;
            updateBottomBar();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Problem loading file");
            alert.setContentText("There was a problem loading the file, most likely not in txt format");
            alert.showAndWait();
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

    @FXML
    public void chooseWorkspace() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File workspace = directoryChooser.showDialog(menuBar.getScene().getWindow());
        if (workspace != null) setUpFileStructure(workspace);
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
            menuBar.getScene().getStylesheets().add(getClass().getResource("dark-mode.css").toExternalForm());
        } else {
            menuBar.getScene().getStylesheets().remove(getClass().getResource("dark-mode.css").toExternalForm());
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
