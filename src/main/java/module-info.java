module pl.aliaksandrou.interviewee {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    requires kotlin.stdlib;

    opens pl.aliaksandrou.interviewee.view to javafx.fxml;
    exports pl.aliaksandrou.interviewee;
    exports pl.aliaksandrou.interviewee.view to javafx.fxml;
}
