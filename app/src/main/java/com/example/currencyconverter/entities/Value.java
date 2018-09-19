package com.example.currencyconverter.entities;

//Used for converting api response from json
//field 'val' stores exchange rate
public class Value {
    private double val;

    public Value(double val) {
        this.val = val;
    }

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }
}
