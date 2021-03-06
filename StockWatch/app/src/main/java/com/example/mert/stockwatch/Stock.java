package com.example.mert.stockwatch;

public class Stock {
    private String symbol;
    private String name;
    private double price;
    private double priceChange;
    private double changePercent;

    public Stock(){}


    public Stock(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public double getChangePercent() {
        return changePercent;
    }

    @Override
    public boolean equals(Object obj) {

        boolean result = false;
        if (obj == null || obj.getClass() != getClass()) {
            result = false;
        } else {
            Stock stock = (Stock) obj;
            if (this.symbol.equals(stock.symbol)) {
                result = true;
            }
        }
        return result;
    }

}