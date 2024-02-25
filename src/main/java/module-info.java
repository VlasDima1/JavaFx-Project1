module ro.ubbcluj.cs.map.labgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.xml;
    requires java.sql;
    requires org.apache.commons.codec;
    //requires org.mindrot.jbcrypt;

    opens ro.ubbcluj.cs.map.labgui to javafx.fxml;
    opens ro.ubbcluj.cs.map.controller to javafx.fxml;
    exports ro.ubbcluj.cs.map.labgui;

    opens ro.ubbcluj.cs.map.domain to javafx.base;
    exports ro.ubbcluj.cs.map.domain;

}