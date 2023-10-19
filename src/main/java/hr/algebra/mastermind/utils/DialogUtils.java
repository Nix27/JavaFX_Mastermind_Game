package hr.algebra.mastermind.utils;

import javafx.scene.control.Alert;

public final class DialogUtils {
    private DialogUtils(){}

    public static void showInvalidCodeWarning(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid code");
        alert.setHeaderText("Code is not valid");
        alert.setContentText("All circles of code should be filled with color!");

        alert.showAndWait();
    }

    public static void showGameResult(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static void showErrorDialog(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
