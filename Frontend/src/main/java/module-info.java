module eformer.front.eformer_frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.burningwave.core;
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.jfoenix;
    requires org.json;

    opens eformer.front.eformer_frontend to javafx.fxml;
    opens eformer.front.eformer_frontend.controller to javafx.fxml;

    exports eformer.front.eformer_frontend;
    exports eformer.front.eformer_frontend.controller;
    exports eformer.front.eformer_frontend.model;
    exports eformer.front.eformer_frontend.connector;
}