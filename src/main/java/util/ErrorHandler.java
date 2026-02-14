package util;

import javafx.scene.control.Alert;

public class ErrorHandler {

    public static void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }

    public static void showWarning(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).show();
    }

    public static void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}
