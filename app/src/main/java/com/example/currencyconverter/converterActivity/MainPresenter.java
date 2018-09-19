package com.example.currencyconverter.converterActivity;

import io.reactivex.Observable;

public interface MainPresenter {
    void convertHandler(Observable<Object> bind);
    void dispose();
}