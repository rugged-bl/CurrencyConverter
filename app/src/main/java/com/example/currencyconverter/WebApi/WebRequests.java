package com.example.currencyconverter.WebApi;

import android.content.Context;

import com.example.currencyconverter.entities.Value;

import java.util.HashMap;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class WebRequests {
    private static WebRequests INSTANCE;

    private WebApiService apiService;

    public static WebRequests getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (WebRequests.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WebRequests(context);
                }
            }
        }
        return INSTANCE;
    }

    private WebRequests(final Context context) {
        apiService = WebApiService.Factory.create("https://free.currencyconverterapi.com/", context);
    }

    public Single<HashMap<String, Value>> getRate(String currencyPair) {
        return apiService.getRate(currencyPair)
                .subscribeOn(Schedulers.io());
    }

}
