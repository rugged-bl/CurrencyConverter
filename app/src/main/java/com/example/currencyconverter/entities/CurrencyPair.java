package com.example.currencyconverter.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

//Main database entity.
//field 'pair' stores currency pair (e.g. USD_RUB)
//field 'value' stores exchange rate
@Entity(tableName = "exchange_table")
public class CurrencyPair {
    public CurrencyPair(@NonNull String pair, double value) {
        this.pair = pair;
        this.value = value;
    }

    @Ignore
    public CurrencyPair(@NonNull String pair) {
        new CurrencyPair(pair, 0);
    }

    @PrimaryKey
    @NonNull
    private String pair;

    private double value;

    public void setPair(@NonNull String pair) {
        this.pair = pair;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getPair() {
        return pair;
    }

    public double getValue() {
        return value;
    }
}