module bookmanagementsystem.bookstoremanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.j;
    requires de.jensd.fx.glyphs.fontawesome;
//    requires eu.hansolo.tilesfx;

    opens bookmanagementsystem.bookstoremanagementsystem to javafx.fxml;
    exports bookmanagementsystem.bookstoremanagementsystem;
}