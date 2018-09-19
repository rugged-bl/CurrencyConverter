package com.example.currencyconverter.converterActivity;


import android.util.Pair;

import io.reactivex.Observable;

public interface MainView {
    void noResult();
    void noConnection();
    void resultReady(Double result);
    void localResultReady(Double result);
    void loadingPairs();
    void enteredInvalidPairs();

    Observable<Pair<String, String>> getConvertPair();
}
