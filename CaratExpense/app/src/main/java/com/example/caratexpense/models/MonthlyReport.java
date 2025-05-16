package com.example.caratexpense.models;

import java.io.Serializable;

public class MonthlyReport implements Serializable {
    private int month;
    private String monthName;
    private double amount;
    
    public MonthlyReport(int month, String monthName, double amount) {
        this.month = month;
        this.monthName = monthName;
        this.amount = amount;
    }
    
    public int getMonth() {
        return month;
    }
    
    public void setMonth(int month) {
        this.month = month;
    }
    
    public String getMonthName() {
        return monthName;
    }
    
    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
