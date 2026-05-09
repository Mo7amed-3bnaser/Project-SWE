package com.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Product class
 */
@DisplayName("Product Tests")
public class ProductTest {

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("إنشاء product بقيم صحيحة")
    void testProductCreation() {
        Product product = new Product("Laptop", 10, 999.99);

        assertEquals("Laptop", product.getName());
        assertEquals(10, product.getQuantity());
        assertEquals(999.99, product.getPrice(), 0.001);
    }

    @Test
    @DisplayName("إنشاء product بـ quantity = 0")
    void testProductWithZeroQuantity() {
        Product product = new Product("Mouse", 0, 25.00);

        assertEquals(0, product.getQuantity());
    }

    @Test
    @DisplayName("إنشاء product بـ price = 0")
    void testProductWithZeroPrice() {
        Product product = new Product("Free Item", 5, 0.0);

        assertEquals(0.0, product.getPrice(), 0.001);
    }

    // ==================== Getter Tests ====================

    @Test
    @DisplayName("getName يرجع الاسم الصح")
    void testGetName() {
        Product product = new Product("Keyboard", 15, 75.50);
        assertEquals("Keyboard", product.getName());
    }

    @Test
    @DisplayName("getQuantity يرجع الـ quantity الصح")
    void testGetQuantity() {
        Product product = new Product("Monitor", 3, 300.00);
        assertEquals(3, product.getQuantity());
    }

    @Test
    @DisplayName("getPrice يرجع الـ price الصح")
    void testGetPrice() {
        Product product = new Product("Headphones", 7, 149.99);
        assertEquals(149.99, product.getPrice(), 0.001);
    }

    // ==================== Setter Tests ====================

    @Test
    @DisplayName("setName بيغير الاسم صح")
    void testSetName() {
        Product product = new Product("OldName", 5, 10.0);
        product.setName("NewName");
        assertEquals("NewName", product.getName());
    }

    @Test
    @DisplayName("setQuantity بيغير الـ quantity صح")
    void testSetQuantity() {
        Product product = new Product("Chair", 2, 200.0);
        product.setQuantity(20);
        assertEquals(20, product.getQuantity());
    }

    @Test
    @DisplayName("setPrice بيغير الـ price صح")
    void testSetPrice() {
        Product product = new Product("Desk", 1, 500.0);
        product.setPrice(450.0);
        assertEquals(450.0, product.getPrice(), 0.001);
    }

    // ==================== toString Tests ====================

    @Test
    @DisplayName("toString بيرجع الـ format الصح")
    void testToString() {
        Product product = new Product("Phone", 5, 699.99);
        String result = product.toString();

        assertTrue(result.contains("Phone"));
        assertTrue(result.contains("5"));
        assertTrue(result.contains("699.99"));
    }

    @Test
    @DisplayName("toString بيحتوي على Quantity و Price")
    void testToStringContainsLabels() {
        Product product = new Product("Tablet", 3, 399.00);
        String result = product.toString();

        assertTrue(result.contains("Quantity"));
        assertTrue(result.contains("Price"));
    }

    // ==================== Edge Case Tests ====================

    @Test
    @DisplayName("product بـ اسم فيه spaces")
    void testProductWithSpacesInName() {
        Product product = new Product("Gaming Mouse", 10, 59.99);
        assertEquals("Gaming Mouse", product.getName());
    }

    @Test
    @DisplayName("product بـ price بكسور عشرية كتير")
    void testProductWithPrecisePrice() {
        Product product = new Product("Item", 1, 19.999);
        assertEquals(19.999, product.getPrice(), 0.0001);
    }

    @Test
    @DisplayName("تحديث كل الـ fields بعد الإنشاء")
    void testUpdateAllFields() {
        Product product = new Product("Old", 1, 1.0);
        product.setName("New");
        product.setQuantity(100);
        product.setPrice(999.0);

        assertEquals("New", product.getName());
        assertEquals(100, product.getQuantity());
        assertEquals(999.0, product.getPrice(), 0.001);
    }
}
