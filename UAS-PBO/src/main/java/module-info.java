module com.example.uas_pbo {
    requires javafx.controls;
    requires javafx.fxml;
//    requires mysql.connector.java;
    requires java.sql;
//    requires mysql.connector.java;


    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires mysql.connector.j;

    opens com.example.uas_pbo to javafx.fxml;
    exports com.example.uas_pbo;
}