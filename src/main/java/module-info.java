module music.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media; // Add this line to require javafx.media module

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens music.project to javafx.fxml;
    exports music.project;
}