package com.inventory;

import javafx.application.Application;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.sql.SQLException;

public class InventoryApp extends Application {

    // الـ database manager بتاع التطبيق
    private DatabaseManager dbManager;

    // الـ lists اللي بنخزن فيها الـ products
    private ArrayList<Product> products = new ArrayList<>(); // قائمة عادية للـ products
    private ObservableList<Product> observableProducts; // قائمة خاصة بالـ JavaFX للـ UI

    // مكونات الـ UI بتاعة التطبيق
    private TextField nameField; // حقل إدخال اسم الـ product
    private TextField quantityField; // حقل إدخال الـ quantity
    private TextField priceField; // حقل إدخال الـ price
    private TableView<Product> productTable; // جدول عرض الـ products
    private TextField searchField; // حقل البحث عن الـ products
    private Label titleLabel; // عنوان التطبيق
    private FilteredList<Product> filteredProducts; // قائمة مفلترة للبحث

    @Override
    public void start(Stage primaryStage) {
        // Initialize database connection
        dbManager = new DatabaseManager();

        // Load products from database
        products = dbManager.getAllProducts();

        primaryStage.setTitle("Inventory Management System");

        // Apply CSS stylesheet
        Scene scene = createScene(primaryStage);
        try {
            // Try to load CSS from file path
            String cssPath = "styles.css";
            scene.getStylesheets().add(cssPath);
            System.out.println("CSS loaded successfully");
        } catch (Exception e) {
            System.err.println("Could not load CSS file: " + e.getMessage());
            // Fallback styling if CSS can't be loaded
            applyFallbackStyling();
        }

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // فتح الشاشة بالحجم الكامل
        primaryStage.show();

        // Apply entrance animations
        animateEntrance();
    }

    // Apply fallback styling if CSS file can't be loaded
    private void applyFallbackStyling() {
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#4e92df"));

        productTable.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: white;");
    }

    private Scene createScene(Stage primaryStage) {
        // Create title label
        titleLabel = new Label("Inventory Management System");
        titleLabel.getStyleClass().add("title-label");

        // Create input fields
        Label nameLabel = new Label("Product Name:");
        nameField = new TextField();
        nameField.setPromptText("Enter product name");

        Label quantityLabel = new Label("Quantity:");
        quantityField = new TextField();
        quantityField.setPromptText("Enter quantity");

        Label priceLabel = new Label("Price:");
        priceField = new TextField();
        priceField.setPromptText("Enter price");

        // Create buttons with styling
        Button addButton = new Button("Add Product");
        addButton.getStyleClass().addAll("button", "add-button");
        addButtonHoverEffect(addButton);

        Button updateButton = new Button("Update Product");
        updateButton.getStyleClass().addAll("button", "update-button");
        addButtonHoverEffect(updateButton);

        Button deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().addAll("button", "delete-button");
        addButtonHoverEffect(deleteButton);

        // Create search field
        Label searchLabel = new Label("Search Products:");
        searchField = new TextField();
        searchField.setPromptText("Enter product name to search");
        searchField.getStyleClass().add("search-field");

        // Create table view with styling
        productTable = new TableView<>();
        productTable.getStyleClass().add("product-table");
        productTable.setPlaceholder(new Label("No products available"));

        // Define table columns
        TableColumn<Product, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(150);

        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setPrefWidth(100);

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setPrefWidth(100);

        productTable.getColumns().addAll(nameColumn, quantityColumn, priceColumn);

        // Setup data
        observableProducts = FXCollections.observableArrayList(products);
        filteredProducts = new FilteredList<>(observableProducts, p -> true);
        productTable.setItems(filteredProducts);

        // إضافة خاصية لعرض بيانات المنتج المحدد في حقول الإدخال
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // عرض بيانات المنتج المحدد في حقول الإدخال
                nameField.setText(newSelection.getName());
                quantityField.setText(String.valueOf(newSelection.getQuantity()));
                priceField.setText(String.valueOf(newSelection.getPrice()));
            }
        });

        // Configure search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProducts.setPredicate(product -> {
                // If search field is empty, show all products
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return product.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Add functionality to buttons
        addButton.setOnAction(e -> addProduct());
        updateButton.setOnAction(e -> updateProduct());
        deleteButton.setOnAction(e -> deleteProduct());

        // Organize UI elements
        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(15));
        inputGrid.setHgap(15);
        inputGrid.setVgap(15);
        inputGrid.getStyleClass().add("grid-pane");

        inputGrid.add(nameLabel, 0, 0);
        inputGrid.add(nameField, 1, 0);
        inputGrid.add(quantityLabel, 0, 1);
        inputGrid.add(quantityField, 1, 1);
        inputGrid.add(priceLabel, 0, 2);
        inputGrid.add(priceField, 1, 2);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getStyleClass().add("button-box");
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton);

        // Search box
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(searchLabel, searchField);

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));
        mainLayout.getStyleClass().add("main-layout");
        mainLayout.getChildren().addAll(titleLabel, inputGrid, buttonBox, searchBox, productTable);

        return new Scene(mainLayout, 600, 700);
    }

    // Add new product function
    private void addProduct() {
        try {
            String name = nameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());

            if (name.isEmpty()) {
                showAlert("Error", "Product name is required");
                return;
            }

            Product product = new Product(name, quantity, price);

            // Add to database
            boolean success = dbManager.addProduct(product);

            if (success) {
                products.add(product);
                updateListView();

                // Clear fields after adding
                clearFields();
                showAlert("Success", "Product added successfully");
            } else {
                showAlert("Error", "Failed to add product to database");
            }

            // Add animation for table
            FadeTransition fade = new FadeTransition(Duration.millis(700), productTable);
            fade.setFromValue(0.8);
            fade.setToValue(1);
            fade.play();

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for quantity and price");
        }
    }

    // Delete selected product function
    private void deleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            // Create a fade out animation for the table
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), productTable);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.8);
            fadeOut.setOnFinished(e -> {
                // Get the index of the selected product in the table
                int selectedIndex = productTable.getSelectionModel().getSelectedIndex();

                // لازم نجيب الـ ID من قاعدة البيانات مش من الـ index
                // هنعدل الكود عشان نحذف المنتج بناءً على الاسم بدلاً من الـ ID
                String productName = selectedProduct.getName();

                // حذف المنتج من قاعدة البيانات باستخدام اسم المنتج
                boolean success = deleteProductByName(productName);

                if (success) {
                    // Remove the product after animation completes
                    products.remove(selectedProduct);
                    updateListView();
                    showAlert("Success", "Product deleted successfully");
                } else {
                    showAlert("Error", "Failed to delete product from database");
                }
            });
            fadeOut.play();
        } else {
            showAlert("Warning", "Please select a product to delete");
        }
    }

    // Update table view
    private void updateListView() {
        observableProducts.clear();
        observableProducts.addAll(products);
        // Reapply current filter
        String currentFilter = searchField.getText();
        searchField.setText(""); // Clear
        searchField.setText(currentFilter); // Reapply
    }

    // Clear input fields
    private void clearFields() {
        nameField.clear();
        quantityField.clear();
        priceField.clear();
        nameField.requestFocus();
    }

    // Show alert message
    private void showAlert(String title, String message) {
        Alert alert;

        if (title.equals("Error")) {
            alert = new Alert(Alert.AlertType.ERROR);
        } else if (title.equals("Warning")) {
            alert = new Alert(Alert.AlertType.WARNING);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Add button hover effect
    private void addButtonHoverEffect(Button button) {
        // Button hover effects are handled in CSS, but we'll add click animation here
        button.setOnMousePressed(e -> {
            TranslateTransition press = new TranslateTransition(Duration.millis(100), button);
            press.setByY(3);
            press.play();
        });

        button.setOnMouseReleased(e -> {
            TranslateTransition release = new TranslateTransition(Duration.millis(100), button);
            release.setByY(-3);
            release.play();
        });
    }

    // Animate UI elements on startup
    private void animateEntrance() {
        // Fade in title
        FadeTransition fadeTitle = new FadeTransition(Duration.millis(1500), titleLabel);
        fadeTitle.setFromValue(0);
        fadeTitle.setToValue(1);
        fadeTitle.play();

        // Slide in table view from bottom
        TranslateTransition slideTable = new TranslateTransition(Duration.millis(1000), productTable);
        slideTable.setFromY(50);
        slideTable.setToY(0);
        slideTable.setDelay(Duration.millis(300));
        slideTable.play();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        // Close database connection when application closes
        if (dbManager != null) {
            dbManager.closeConnection();
        }
    }

    // دالة جديدة لحذف المنتج بالاسم
    private boolean deleteProductByName(String productName) {
        // استدعاء دالة حذف المنتج بالاسم من الـ DatabaseManager
        boolean success = dbManager.deleteProductByName(productName);
        return success;
    }

    // دالة لتحديث المنتج المحدد
    private void updateProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                // الحصول على البيانات الجديدة من حقول الإدخال
                String name = nameField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());

                if (name.isEmpty()) {
                    showAlert("Error", "Product name is required");
                    return;
                }

                // الحصول على اسم المنتج الأصلي قبل التحديث
                String originalName = selectedProduct.getName();

                // حذف المنتج القديم
                boolean deleteSuccess = deleteProductByName(originalName);

                if (deleteSuccess) {
                    // إنشاء منتج جديد بالبيانات المحدثة
                    Product updatedProduct = new Product(name, quantity, price);

                    // إضافة المنتج المحدث إلى قاعدة البيانات
                    boolean addSuccess = dbManager.addProduct(updatedProduct);

                    if (addSuccess) {
                        // تحديث القائمة المحلية
                        int index = products.indexOf(selectedProduct);
                        products.set(index, updatedProduct);
                        updateListView();

                        // مسح الحقول بعد التحديث
                        clearFields();
                        showAlert("Success", "Product updated successfully");

                        // إضافة تأثير حركي للجدول
                        FadeTransition fade = new FadeTransition(Duration.millis(700), productTable);
                        fade.setFromValue(0.8);
                        fade.setToValue(1);
                        fade.play();
                    } else {
                        showAlert("Error", "Failed to update product in database");
                    }
                } else {
                    showAlert("Error", "Failed to update product");
                }

            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter valid numbers for quantity and price");
            }
        } else {
            showAlert("Warning", "Please select a product to update");
        }
    }
}