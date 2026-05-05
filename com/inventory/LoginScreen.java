package com.inventory;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
import javafx.scene.effect.DropShadow;

public class LoginScreen extends Application {
    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel; // رسالة حالة تسجيل الدخول
    private Stage primaryStage;
    private Scene scene;
    private StackPane loadingPane; // شاشة التحميل
    private StackPane root; // الحاوية الرئيسية

    // Default credentials
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Inventory System Login");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.getStyleClass().add("login-grid");

        Label titleLabel = new Label("Login");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.getStyleClass().add("login-title");
        grid.add(titleLabel, 0, 0, 2, 1);

        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 1);

        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setText("admin"); // اليوزرنيم مكتوب دايركت
        grid.add(usernameField, 1, 1);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setText("admin123"); // الباسورد مكتوب دايركت
        grid.add(passwordField, 1, 2);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(loginButton);
        grid.add(hbBtn, 1, 4);

        messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label");
        grid.add(messageLabel, 1, 6);

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (validateLogin(username, password)) {
                messageLabel.setText("Login successful!");
                messageLabel.setTextFill(Color.GREEN);

                FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), messageLabel);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> {
                    // إخفاء شاشة تسجيل الدخول وعرض شاشة التحميل
                    GridPane loginGrid = (GridPane) ((StackPane) scene.getRoot()).getChildren().get(0);
                    loginGrid.setVisible(false);
                    showLoadingScreen();

                    // تأخير لمحاكاة التحميل
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), ev -> {
                        try {
                            InventoryApp inventoryApp = new InventoryApp();
                            inventoryApp.start(new Stage());
                            primaryStage.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            hideLoadingScreen();
                            loginGrid.setVisible(true);
                            messageLabel.setText("Error launching application");
                            messageLabel.setTextFill(Color.RED);
                        }
                    }));
                    timeline.play();
                });
                fadeOut.play();
            } else {
                messageLabel.setText("Invalid username or password");
                messageLabel.setTextFill(Color.RED);
                passwordField.clear();
            }
        });

        // إنشاء شاشة التحميل
        createLoadingScreen();

        // إنشاء StackPane لوضع شاشة التحميل فوق شاشة تسجيل الدخول
        root = new StackPane();
        root.getChildren().addAll(grid, loadingPane);
        loadingPane.setVisible(false); // إخفاء شاشة التحميل في البداية

        scene = new Scene(root, 800, 600);
        try {
            String cssPath = "styles.css";
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Could not load CSS file: " + e.getMessage());
            applyFallbackStyling(titleLabel);
        }

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // فتح الشاشة بالحجم الكامل
        primaryStage.show();

        animateEntrance(grid);
    }

    private boolean validateLogin(String username, String password) {
        return username.equals(DEFAULT_USERNAME) && password.equals(DEFAULT_PASSWORD);
    }

    private void applyFallbackStyling(Label titleLabel) {
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#4e92df"));

        usernameField.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: white; -fx-border-color: #5a5a5a;");
        passwordField.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: white; -fx-border-color: #5a5a5a;");
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
    }

    private void animateEntrance(GridPane grid) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1500), grid);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    // إنشاء شاشة التحميل
    private void createLoadingScreen() {
        loadingPane = new StackPane();
        loadingPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox loadingBox = new VBox(20);
        loadingBox.setAlignment(Pos.CENTER);

        // إنشاء دائرة متحركة للتحميل
        Circle circle = new Circle(30, Color.DODGERBLUE);
        circle.setEffect(new DropShadow(10, Color.BLUE));

        // إضافة تأثير دوران للدائرة
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), circle);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();

        Text loadingText = new Text("Loading Inventory System...");
        loadingText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        loadingText.setFill(Color.WHITE);

        loadingBox.getChildren().addAll(circle, loadingText);
        loadingPane.getChildren().add(loadingBox);
    }

    // عرض شاشة التحميل
    private void showLoadingScreen() {
        loadingPane.setVisible(true);
    }

    // إخفاء شاشة التحميل
    private void hideLoadingScreen() {
        loadingPane.setVisible(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
