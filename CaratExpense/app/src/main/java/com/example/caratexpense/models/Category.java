package com.example.caratexpense.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "categories")
public class Category implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private boolean isExpense;
    private String iconName;
    private String color; // ✅ Màu sắc dưới dạng chuỗi hex (ví dụ: "#FF5722")

    @Ignore  // Không lưu vào database
    private String icon;

    // ✅ Constructor Room sẽ dùng
    public Category(long id, String name, boolean isExpense, String iconName, String color) {
        this.id = id;
        this.name = name;
        this.isExpense = isExpense;
        this.iconName = iconName;
        this.color = color;
    }

    // ✅ Constructor phụ --> Room sẽ bỏ qua
    @Ignore
    public Category(int id, String name, boolean isExpense, String iconName) {
        this.id = id;
        this.name = name;
        this.isExpense = isExpense;
        this.iconName = iconName;
    }

    // Constructor này sẽ bị Room bỏ qua vì bạn chỉ cần sử dụng constructor có đầy đủ tham số
    @Ignore
    public Category(String name, boolean isExpense, String iconName) {
        this.name = name;
        this.isExpense = isExpense;
        this.iconName = iconName;
    }

    // Getter và Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExpense() {
        return isExpense;
    }

    public void setExpense(boolean expense) {
        isExpense = expense;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
