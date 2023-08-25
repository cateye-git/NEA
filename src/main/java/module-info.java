module com.example.nea {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.nea to javafx.fxml;
    exports com.example.nea;
}