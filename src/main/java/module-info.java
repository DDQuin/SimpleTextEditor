module com.ddquin.simpletexteditor {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ddquin.simpletexteditor to javafx.fxml;
    exports com.ddquin.simpletexteditor;
}
