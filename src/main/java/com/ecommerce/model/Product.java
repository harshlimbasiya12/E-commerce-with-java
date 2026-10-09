
package com.ecommerce.model;

import java.sql.Timestamp;

public class Product {

    private int productId;
    private int ownerId;

    private String title;
    private double price;
    private String description;
    private String imageUrl;

    private Timestamp createdAt;

    // Default Constructor
    public Product() {
    }

    // Parameterized Constructor
    public Product(int productId, int ownerId,
                   String title, double price,
                   String description, String imageUrl) {

        this.productId = productId;
        this.ownerId = ownerId;
        this.title = title;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}