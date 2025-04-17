package com.example.ecommerce_app.service;


import com.example.ecommerce_app.dao.CategoryDAO;
import com.example.ecommerce_app.dao.ProductDAO;
import com.example.ecommerce_app.model.Category;
import com.example.ecommerce_app.model.Product;

import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    // Get categories from the database
    public List<Category> getCategories() {
        return categoryDAO.getAllCategories();
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> getProductsByCategory(String category) {
        return productDAO.getProductsByCategory(category);
    }

    public void saveProduct(Product product) {
        productDAO.saveProduct(product);
    }

    public void updateProduct(Product product) {
        productDAO.updateProduct(product);
    }

    public void deleteProduct(int productId) {
        productDAO.deleteProduct(productId);
    }


}
