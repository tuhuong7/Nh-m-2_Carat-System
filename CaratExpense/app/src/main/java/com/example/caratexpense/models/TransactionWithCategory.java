package com.example.caratexpense.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;

public class TransactionWithCategory implements Serializable {

    @Embedded
    public Transaction transaction;

    @Relation(
            parentColumn = "categoryId",
            entityColumn = "id"
    )
    private Category category;

    public Transaction getTransaction() {
        return transaction;
    }

    public Category getCategory() {
        return category;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
