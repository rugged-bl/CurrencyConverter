package com.example.currencyconverter.model;

import android.util.Pair;

import com.example.currencyconverter.entities.ViewStateInfo;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public interface MainModel {
    BehaviorSubject<ViewStateInfo> convertHandler(Observable<Object> bind, Observable<Pair<String, String>> convertPair);
}
