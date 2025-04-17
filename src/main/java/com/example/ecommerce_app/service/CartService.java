package com.example.ecommerce_app.service;

import com.example.ecommerce_app.model.Product;
import javafx.scene.control.Button;
import java.util.*;

public class CartService {
    private static final List<Product> cart = new ArrayList<>();
    private static Button cartButton;

    public static void setCartButton(Button btn) {
        cartButton = btn;
        updateCartButton();
    }

    public static void addToCart(Product p) {
        cart.add(p);
        updateCartButton();
    }

    public static void updateCartButton() {
        if (cartButton != null) {
            cartButton.setText("🛒 Cart (" + cart.size() + ")");
        }
    }

    public static List<Product> getCartItems() {
        return new ArrayList<>(cart);
    }

    public static void removeFromCart(Product product) {
        cart.remove(product); // This removes the first occurrence of the product (based on equals method)
    }

    public static void clearCart() {
        cart.clear();
        updateCartButton();
    }

    public static void updateProductQuantity(Product product) {
        for (Product cartProduct : cart) {
            // If product IDs match, update quantity
            if (cartProduct.getId() == product.getId()) {
                cartProduct.setQuantity(product.getQuantity());  // Update the quantity to the new value
                break;  // Exit the loop once the product is found and updated
            }
        }
    }
}
