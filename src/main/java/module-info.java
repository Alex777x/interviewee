module pl.aliaksandrou.interviewee {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    requires kotlin.stdlib;
    requires static lombok;
    requires java.desktop;
    requires org.apache.logging.log4j;
    requires kafka.clients;
    requires annotations;

    opens pl.aliaksandrou.interviewee.view to javafx.fxml;
    exports pl.aliaksandrou.interviewee;
    exports pl.aliaksandrou.interviewee.view to javafx.fxml;
    exports pl.aliaksandrou.interviewee.service;
    exports pl.aliaksandrou.interviewee.model;
    exports pl.aliaksandrou.interviewee.audiointerceptor;
}
