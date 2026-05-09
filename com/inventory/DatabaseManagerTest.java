package com.inventory;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;

/**
 * Unit tests for the DatabaseManager class
 * بيستخدم database مؤقتة للـ testing عشان ميأثرش على الـ data الحقيقية
 */
@DisplayName("DatabaseManager Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseManagerTest {

    private DatabaseManager dbManager;

    // ==================== Setup & Teardown ====================

    @BeforeEach
    void setUp() {
        // إنشاء DatabaseManager جديد قبل كل test
        dbManager = new DatabaseManager();
        // مسح كل الـ products قبل كل test عشان نبدأ بـ database نظيفة
        clearAllProducts();
    }

    @AfterEach
    void tearDown() {
        // إغلاق الـ connection بعد كل test
        if (dbManager != null) {
            dbManager.closeConnection();
        }
    }

    /**
     * مساعد لمسح كل الـ products من الـ database
     */
    private void clearAllProducts() {
        ArrayList<Product> all = dbManager.getAllProducts();
        for (Product p : all) {
            dbManager.deleteProductByName(p.getName());
        }
    }

    // ==================== addProduct Tests ====================

    @Test
    @Order(1)
    @DisplayName("إضافة product جديد بنجاح")
    void testAddProduct_Success() {
        Product product = new Product("Laptop", 10, 999.99);
        boolean result = dbManager.addProduct(product);

        assertTrue(result, "addProduct يجب أن يرجع true عند النجاح");
    }

    @Test
    @Order(2)
    @DisplayName("إضافة product والتحقق إنه اتحفظ في الـ database")
    void testAddProduct_VerifyInDatabase() {
        Product product = new Product("Keyboard", 5, 75.00);
        dbManager.addProduct(product);

        ArrayList<Product> products = dbManager.getAllProducts();
        assertEquals(1, products.size());
        assertEquals("Keyboard", products.get(0).getName());
        assertEquals(5, products.get(0).getQuantity());
        assertEquals(75.00, products.get(0).getPrice(), 0.001);
    }

    @Test
    @Order(3)
    @DisplayName("إضافة أكتر من product")
    void testAddMultipleProducts() {
        dbManager.addProduct(new Product("Mouse", 20, 25.00));
        dbManager.addProduct(new Product("Monitor", 3, 350.00));
        dbManager.addProduct(new Product("Headphones", 8, 120.00));

        ArrayList<Product> products = dbManager.getAllProducts();
        assertEquals(3, products.size());
    }

    // ==================== getAllProducts Tests ====================

    @Test
    @Order(4)
    @DisplayName("getAllProducts يرجع list فاضية لو مفيش products")
    void testGetAllProducts_Empty() {
        ArrayList<Product> products = dbManager.getAllProducts();
        assertNotNull(products, "يجب أن يرجع list مش null");
        assertTrue(products.isEmpty(), "يجب أن تكون الـ list فاضية");
    }

    @Test
    @Order(5)
    @DisplayName("getAllProducts يرجع كل الـ products الصح")
    void testGetAllProducts_WithData() {
        dbManager.addProduct(new Product("Chair", 4, 200.00));
        dbManager.addProduct(new Product("Desk", 2, 450.00));

        ArrayList<Product> products = dbManager.getAllProducts();
        assertEquals(2, products.size());
    }

    // ==================== deleteProductByName Tests ====================

    @Test
    @Order(6)
    @DisplayName("حذف product موجود بنجاح")
    void testDeleteProductByName_Success() {
        dbManager.addProduct(new Product("Printer", 2, 180.00));

        boolean result = dbManager.deleteProductByName("Printer");
        assertTrue(result, "deleteProductByName يجب أن يرجع true عند النجاح");
    }

    @Test
    @Order(7)
    @DisplayName("التحقق إن الـ product اتحذف فعلاً")
    void testDeleteProductByName_VerifyDeleted() {
        dbManager.addProduct(new Product("Scanner", 1, 250.00));
        dbManager.deleteProductByName("Scanner");

        ArrayList<Product> products = dbManager.getAllProducts();
        assertTrue(products.isEmpty(), "يجب أن تكون الـ list فاضية بعد الحذف");
    }

    @Test
    @Order(8)
    @DisplayName("حذف product مش موجود يرجع false")
    void testDeleteProductByName_NotFound() {
        boolean result = dbManager.deleteProductByName("NonExistentProduct");
        assertFalse(result, "يجب أن يرجع false لو الـ product مش موجود");
    }

    @Test
    @Order(9)
    @DisplayName("حذف product واحد من أكتر من product")
    void testDeleteProductByName_OneOfMany() {
        dbManager.addProduct(new Product("Item A", 5, 10.00));
        dbManager.addProduct(new Product("Item B", 3, 20.00));
        dbManager.addProduct(new Product("Item C", 7, 30.00));

        dbManager.deleteProductByName("Item B");

        ArrayList<Product> products = dbManager.getAllProducts();
        assertEquals(2, products.size());

        // التحقق إن Item B اتحذف
        boolean itemBExists = products.stream()
                .anyMatch(p -> p.getName().equals("Item B"));
        assertFalse(itemBExists, "Item B يجب أن يكون اتحذف");
    }

    // ==================== deleteProduct (by ID) Tests ====================

    @Test
    @Order(10)
    @DisplayName("حذف product بـ ID مش موجود يرجع false")
    void testDeleteProduct_InvalidId() {
        boolean result = dbManager.deleteProduct(99999);
        assertFalse(result, "يجب أن يرجع false لو الـ ID مش موجود");
    }

    // ==================== updateProduct Tests ====================

    @Test
    @Order(11)
    @DisplayName("تحديث product بـ ID مش موجود يرجع false")
    void testUpdateProduct_InvalidId() {
        Product updatedProduct = new Product("Updated", 10, 100.00);
        boolean result = dbManager.updateProduct(updatedProduct, 99999);
        assertFalse(result, "يجب أن يرجع false لو الـ ID مش موجود");
    }

    @Test
    @Order(12)
    @DisplayName("تحديث product موجود بنجاح")
    void testUpdateProduct_Success() {
        // إضافة product أولاً
        Product original = new Product("Original Name", 5, 50.00);
        dbManager.addProduct(original);

        // جلب الـ product من الـ database مع الـ ID
        ArrayList<Product> products = dbManager.getAllProducts();
        assertEquals(1, products.size());
        Product productFromDb = products.get(0);
        int id = productFromDb.getId();

        // تحديث الـ product
        Product updated = new Product("Updated Name", 10, 100.00);
        boolean result = dbManager.updateProduct(updated, id);
        assertTrue(result, "updateProduct يجب أن يرجع true عند النجاح");

        // التحقق إن التحديث تم
        products = dbManager.getAllProducts();
        assertEquals(1, products.size());
        assertEquals("Updated Name", products.get(0).getName());
        assertEquals(10, products.get(0).getQuantity());
        assertEquals(100.00, products.get(0).getPrice(), 0.001);
    }

    // ==================== searchProductsByName Tests ====================

    @Test
    @Order(13)
    @DisplayName("البحث بـ keyword موجود يرجع نتائج")
    void testSearchProductsByName_Found() {
        dbManager.addProduct(new Product("Gaming Mouse", 10, 59.99));
        dbManager.addProduct(new Product("Gaming Keyboard", 5, 89.99));
        dbManager.addProduct(new Product("Monitor", 3, 300.00));

        ArrayList<Product> results = dbManager.searchProductsByName("Gaming");
        assertEquals(2, results.size());
    }

    @Test
    @Order(14)
    @DisplayName("البحث بـ keyword مش موجود يرجع list فاضية")
    void testSearchProductsByName_NotFound() {
        dbManager.addProduct(new Product("Laptop", 5, 999.00));

        ArrayList<Product> results = dbManager.searchProductsByName("XYZ_NOT_EXIST");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @Order(15)
    @DisplayName("البحث بـ partial name يشتغل صح")
    void testSearchProductsByName_PartialMatch() {
        dbManager.addProduct(new Product("Wireless Mouse", 8, 45.00));
        dbManager.addProduct(new Product("Wired Mouse", 12, 20.00));
        dbManager.addProduct(new Product("Keyboard", 6, 60.00));

        ArrayList<Product> results = dbManager.searchProductsByName("Mouse");
        assertEquals(2, results.size());
    }

    @Test
    @Order(16)
    @DisplayName("البحث بـ string فاضية يرجع كل الـ products")
    void testSearchProductsByName_EmptyString() {
        dbManager.addProduct(new Product("Product A", 1, 10.00));
        dbManager.addProduct(new Product("Product B", 2, 20.00));

        ArrayList<Product> results = dbManager.searchProductsByName("");
        assertEquals(2, results.size());
    }

    // ==================== Integration Tests ====================

    @Test
    @Order(17)
    @DisplayName("Add ثم Delete ثم تحقق إن الـ database فاضية")
    void testAddThenDelete() {
        dbManager.addProduct(new Product("Temp Product", 1, 1.00));
        assertEquals(1, dbManager.getAllProducts().size());

        dbManager.deleteProductByName("Temp Product");
        assertEquals(0, dbManager.getAllProducts().size());
    }

    @Test
    @Order(18)
    @DisplayName("إضافة product بـ price كبير")
    void testAddProductWithHighPrice() {
        Product expensive = new Product("Server", 1, 15000.99);
        boolean result = dbManager.addProduct(expensive);

        assertTrue(result);
        ArrayList<Product> products = dbManager.getAllProducts();
        assertEquals(15000.99, products.get(0).getPrice(), 0.001);
    }

    @Test
    @Order(19)
    @DisplayName("إضافة product بـ quantity كبير")
    void testAddProductWithHighQuantity() {
        Product bulk = new Product("Screws", 10000, 0.01);
        boolean result = dbManager.addProduct(bulk);

        assertTrue(result);
        ArrayList<Product> products = dbManager.getAllProducts();
        assertEquals(10000, products.get(0).getQuantity());
    }
}
