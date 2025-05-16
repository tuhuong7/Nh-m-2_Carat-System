package com.example.caratexpense.models;

import java.io.Serializable;

public class CategoryReport implements Serializable {
    private Category category;
    private double amount;
    private double percentage;

    public CategoryReport(Category category, double amount) {
        this.category = category;
        this.amount = amount;
        this.percentage = 0;
    }

    public Category getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public void addAmount(double amount) {
        this.amount += amount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
