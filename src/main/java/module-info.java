module gr.uoi.dit.master2025.gkouvas.dppclient {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires org.kordamp.ikonli.javafx;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.kordamp.ikonli.fontawesome;
    requires java.desktop;
    requires javafx.swing;
    requires com.google.zxing.javase;
    requires com.google.zxing;
    requires webcam.capture;

    // Για FXML
    opens gr.uoi.dit.master2025.gkouvas.dppclient to javafx.fxml;
    opens gr.uoi.dit.master2025.gkouvas.dppclient.controller to javafx.fxml;

    // Για Jackson (reflection πάνω στα models)
    opens gr.uoi.dit.master2025.gkouvas.dppclient.model
            to com.fasterxml.jackson.databind;

    // αν θες, μπορείς να exportάρεις μόνο το βασικό package
    exports gr.uoi.dit.master2025.gkouvas.dppclient;
}
