package com.example.caratexpense.utils;

import com.example.caratexpense.models.Stock;

import java.util.ArrayList;
import java.util.List;

public class MockStockGenerator {

    public static List<Stock> generateMockStocks() {
        List<Stock> stocks = new ArrayList<>();

        stocks.add(new Stock("AAPL", "NASDAQ Global Select", "NASDAQ", "$187.45", "Apple Inc."));
        stocks.add(new Stock("MSFT", "NASDAQ Global Select", "NASDAQ", "$334.27", "Microsoft Corporation"));
        stocks.add(new Stock("GOOGL", "NASDAQ Global Select", "NASDAQ", "$131.86", "Alphabet Inc."));
        stocks.add(new Stock("AMZN", "NASDAQ Global Select", "NASDAQ", "$178.75", "Amazon.com Inc."));
        stocks.add(new Stock("TSLA", "NASDAQ Global Select", "NASDAQ", "$177.67", "Tesla, Inc."));
        stocks.add(new Stock("META", "NASDAQ Global Select", "NASDAQ", "$474.99", "Meta Platforms, Inc."));
        stocks.add(new Stock("NVDA", "NASDAQ Global Select", "NASDAQ", "$950.02", "NVIDIA Corporation"));
        stocks.add(new Stock("BRK.A", "NYSE", "NYSE", "$613,500.00", "Berkshire Hathaway Inc."));
        stocks.add(new Stock("JPM", "NYSE", "NYSE", "$198.47", "JPMorgan Chase & Co."));
        stocks.add(new Stock("V", "NYSE", "NYSE", "$276.96", "Visa Inc."));

        return stocks;
    }
}
