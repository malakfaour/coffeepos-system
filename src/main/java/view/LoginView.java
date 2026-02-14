package view;

import dao.UserDAO;
import dao.impl.UserDAOImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import security.PasswordHasher;
import util.ErrorHandler;

public class LoginView {

    private final Stage stage;
    private final UserDAO userDAO = new UserDAOImpl();

    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public Parent build() {

        BorderPane root = new BorderPane();
      

        root.getStyleClass().add("background-latte");
        root.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());


        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.getStyleClass().add("header-coffee");

        Label title = new Label("☕ CoffeePOS Login");
        title.getStyleClass().add("header-title");
        header.getChildren().add(title);


        VBox loginBox = new VBox(15);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(30));
        loginBox.setMaxWidth(350);
        loginBox.getStyleClass().add("login-box");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("input-field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input-field");

        Button btnLogin = new Button("Login");
        btnLogin.getStyleClass().add("coffee-button");
        btnLogin.setOnAction(e -> handleLogin());

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        loginBox.getChildren().addAll(usernameField, passwordField, btnLogin, errorLabel);

        root.setTop(header);
        root.setCenter(loginBox);

        return root;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            User user = userDAO.findByUsername(username);

            if (user == null) {
                errorLabel.setText("Invalid username or password.");
                return;
            }

            String storedHash = user.getPasswordHash();

            boolean ok = storedHash.startsWith("$2")
                    ? PasswordHasher.verify(password, storedHash)
                    : password.equals(storedHash);

            if (!ok) {
                errorLabel.setText("Invalid username or password.");
                return;
            }

            DashboardView dashboardView = new DashboardView(stage, user);
            stage.setScene(new Scene(dashboardView.build(), 800, 500));

        } catch (Exception e) {
            ErrorHandler.showError("Login error: " + e.getMessage());
        }
    }
}
