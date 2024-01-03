module hr.algebra.mastermind {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.naming;
    requires java.xml;

    exports hr.algebra.mastermind.chat to java.rmi;
    opens hr.algebra.mastermind to javafx.fxml;
    exports hr.algebra.mastermind;
    opens hr.algebra.mastermind.controller to javafx.fxml;
    exports hr.algebra.mastermind.controller;
    opens hr.algebra.mastermind.enums to javafx.fxml;
    exports hr.algebra.mastermind.enums;
    opens hr.algebra.mastermind.model to javafx.fxml;
    exports hr.algebra.mastermind.model;
}