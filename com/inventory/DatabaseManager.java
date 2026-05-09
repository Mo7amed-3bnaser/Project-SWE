package com.inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseManager {

    // ده الـ connection بتاع الـ database
    private Connection connection;
    // ده الـ URL بتاع الـ database بتاعنا
    private static final String DB_URL = "jdbc:sqlite:inventory.db";

    public DatabaseManager() {
        try {
            // تحميل الـ SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // عمل الـ connection مع الـ database
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection established successfully");

            // إنشاء الـ tables لو مش موجودين
            createTables();

        } catch (ClassNotFoundException e) {
            // لو مش لاقي الـ JDBC driver هيطلع error
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            // لو فيه مشكلة في الـ connection هيطلع error
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        // الـ SQL query بتاع إنشاء جدول الـ products
        String createProductsTable = "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + // الـ ID بتاع المنتج
                "name TEXT NOT NULL, " + // اسم الـ product
                "quantity INTEGER NOT NULL, " + // الـ quantity المتوفرة
                "price REAL NOT NULL)"; // الـ price بتاع المنتج

        try (Statement stmt = connection.createStatement()) {
            // تنفيذ الـ query بتاع إنشاء الـ table
            stmt.execute(createProductsTable);
            System.out.println("Products table created or already exists");
        }
    }

    public boolean addProduct(Product product) {
        // الـ SQL query بتاع إضافة منتج جديد
        String sql = "INSERT INTO products (name, quantity, price) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // تعيين قيم الـ parameters في الـ query
            pstmt.setString(1, product.getName()); // اسم الـ product
            pstmt.setInt(2, product.getQuantity()); // الـ quantity
            pstmt.setDouble(3, product.getPrice()); // الـ price

            // تنفيذ الـ query والتحقق من نجاح العملية
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // لو حصل error في إضافة الـ product
            System.err.println("Error adding product: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProduct(Product product, int id) {
        // الـ SQL query بتاع تحديث منتج
        String sql = "UPDATE products SET name = ?, quantity = ?, price = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // تعيين قيم الـ parameters في الـ query
            pstmt.setString(1, product.getName()); // اسم الـ product
            pstmt.setInt(2, product.getQuantity()); // الـ quantity
            pstmt.setDouble(3, product.getPrice()); // الـ price
            pstmt.setInt(4, id); // الـ ID

            // تنفيذ الـ query والتحقق من نجاح العملية
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // لو حصل error في تحديث الـ product
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        // الـ SQL query بتاع حذف منتج
        String sql = "DELETE FROM products WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // تعيين الـ ID في الـ query
            pstmt.setInt(1, id);

            // تنفيذ الـ query والتحقق من نجاح العملية
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // لو حصل error في حذف الـ product
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Product> getAllProducts() {
        // عمل list فاضية للـ products
        ArrayList<Product> products = new ArrayList<>();
        // الـ SQL query بتاع جلب كل المنتجات
        String sql = "SELECT * FROM products";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            // لوب على كل الـ rows في الـ result
            while (rs.next()) {
                // استخراج الـ data من كل row
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");

                // عمل object جديد للـ product وإضافته للـ list
                Product product = new Product(id, name, quantity, price);
                products.add(product);
            }

        } catch (SQLException e) {
            // لو حصل error في جلب الـ products
            System.err.println("Error retrieving products: " + e.getMessage());
        }

        // إرجاع الـ list بتاعة الـ products
        return products;
    }

    /**
     * بتدور على products بالـ name
     * Searches for products by name
     * 
     * @param searchTerm الـ keyword اللي بندور عليها في أسماء الـ products
     * @return ArrayList بتاعة الـ Product objects اللي بتطابق الـ search
     */
    public ArrayList<Product> searchProductsByName(String searchTerm) {
        // عمل list فاضية للـ products
        ArrayList<Product> products = new ArrayList<>();
        // الـ SQL query بتاع البحث عن منتجات بالاسم
        String sql = "SELECT * FROM products WHERE name LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // تعيين الـ search term في الـ query مع إضافة % قبل وبعد للبحث الجزئي
            pstmt.setString(1, "%" + searchTerm + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                // لوب على كل الـ rows في الـ result
                while (rs.next()) {
                    // استخراج الـ data من كل row
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");

                    // عمل object جديد للـ product وإضافته للـ list
                    Product product = new Product(id, name, quantity, price);
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            // لو حصل error في البحث عن الـ products
            System.err.println("Error searching products: " + e.getMessage());
        }

        // إرجاع الـ list بتاعة الـ products
        return products;
    }

    public void closeConnection() {
        try {
            // التحقق من إن الـ connection موجود ومفتوح
            if (connection != null && !connection.isClosed()) {
                // قفل الـ connection
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            // لو حصل error في قفل الـ connection
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * بتحذف منتج بالاسم
     * Deletes a product by name
     * 
     * @param productName اسم المنتج المراد حذفه
     * @return boolean بيوضح نجاح أو فشل عملية الحذف
     */
    public boolean deleteProductByName(String productName) {
        // الـ SQL query بتاع حذف منتج بالاسم
        String sql = "DELETE FROM products WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // تعيين اسم المنتج في الـ query
            pstmt.setString(1, productName);

            // تنفيذ الـ query والتحقق من نجاح العملية
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // لو حصل error في حذف الـ product
            System.err.println("Error deleting product by name: " + e.getMessage());
            return false;
        }
    }
}