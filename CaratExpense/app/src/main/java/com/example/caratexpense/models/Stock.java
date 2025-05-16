package com.example.caratexpense.models;

import com.google.gson.annotations.SerializedName;

public class Stock {
    @SerializedName("symbol")
    private String symbol;

    @SerializedName("exchange")
    private String exchange;

    @SerializedName("exchangeShortName")
    private String exchangeShortName;

    @SerializedName("price")
    private String price;

    @SerializedName("name")
    private String name;

    public Stock(String symbol, String exchange, String exchangeShortName, String price, String name) {
        this.symbol = symbol;
        this.exchange = exchange;
        this.exchangeShortName = exchangeShortName;
        this.price = price;
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getExchangeShortName() {
        return exchangeShortName;
    }

    public void setExchangeShortName(String exchangeShortName) {
        this.exchangeShortName = exchangeShortName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
