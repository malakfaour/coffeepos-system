package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.LoginView;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        LoginView loginView = new LoginView(stage);
        Scene scene = new Scene(loginView.build(), 350, 250);
        
        stage.setTitle("CoffeePOS - Login");
        stage.setScene(scene);
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
