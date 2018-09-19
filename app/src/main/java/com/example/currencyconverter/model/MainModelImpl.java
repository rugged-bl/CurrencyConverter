package com.example.currencyconverter.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.util.Pair;

import com.example.currencyconverter.WebApi.WebRequests;
import com.example.currencyconverter.database.AppRoomDatabase;
import com.example.currencyconverter.database.CurrencyPairDao;
import com.example.currencyconverter.entities.CurrencyPair;
import com.example.currencyconverter.entities.ViewStateInfo;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static com.example.currencyconverter.entities.ViewStateInfo.States.INVALID_PAIRS;
import static com.example.currencyconverter.entities.ViewStateInfo.States.LOADING;
import static com.example.currencyconverter.entities.ViewStateInfo.States.LOCAL_RESULT_READY;
import static com.example.currencyconverter.entities.ViewStateInfo.States.NOTHING;
import static com.example.currencyconverter.entities.ViewStateInfo.States.NO_CONNECTION;
import static com.example.currencyconverter.entities.ViewStateInfo.States.RESULT_READY;

public class MainModelImpl implements MainModel {
    // Tag used for debugging/logging
    public static final String TAG = "MainModelImpl";

    private static MainModelImpl INSTANCE;

    private Application application;
    private CurrencyPairDao pairDao;

    //data flow between model and view (reemits last element on new subscription)
    private BehaviorSubject<ViewStateInfo> viewStateInfoSubject = BehaviorSubject.create();

    public static MainModelImpl getInstance(final Application application) {
        if (INSTANCE == null) {
            synchronized (MainModelImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MainModelImpl(application);
                }
            }
        }
        return INSTANCE;
    }

    private MainModelImpl(Application application) {
        this.application = application;

        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        pairDao = db.pairDao();

        viewStateInfoSubject.onNext(new ViewStateInfo(NOTHING)); //initial state (nothing requested)
    }

    @SuppressLint("CheckResult")
    //emits data from remote api if its possible; if not, from local
    public BehaviorSubject<ViewStateInfo> convertHandler(Observable<Object> bind, Observable<Pair<String, String>> convertPair) {
        getValidPairs(bind, convertPair)
                .subscribe(str -> {
                    emitNewViewState(LOADING);
                    WebRequests.getInstance(application.getApplicationContext())
                            .getRate(str)
                            .zipWith(Single.just(str), ((pair, pairName) -> {
                                if (pair.containsKey(str)) {
                                    double value = pair.get(str).getVal();
                                    CurrencyPair currencyPair = new CurrencyPair(str, value);

                                    insert(currencyPair); //cache successfully obtained info
                                    return new ViewStateInfo(RESULT_READY, currencyPair);
                                } else
                                    return new ViewStateInfo(INVALID_PAIRS, str);
                            }))
                            .subscribe(this::emitNewViewState,
                                    throwable -> {
                                        throwable.printStackTrace();

                                        loadFromLocalSource(str); //In case of exception try to load data from local source
                                    });
                });

        return viewStateInfoSubject;
    }

    //fet actual currency pair (e.g. USD_RUB) and validate it
    private Observable<String> getValidPairs(Observable<Object> obs, Observable<Pair<String, String>> convertPair) {
        return obs.switchMap(o -> convertPair)
                .observeOn(Schedulers.io())
                .doOnNext(pair -> {
                    if (!isValidPair(pair))
                        emitNewViewState(INVALID_PAIRS);
                })
                .filter(this::isValidPair)
                .map(pair -> "" + pair.first + "_" + pair.second);
    }

    private Disposable loadFromLocalSource(String string) {
        return getPair(string)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> emitNewViewState(LOCAL_RESULT_READY, pair),
                        throwable2 -> {
                            Log.d(TAG, throwable2.getMessage());
                            emitNewViewState(NO_CONNECTION);
                        });
    }

    private void emitNewViewState(ViewStateInfo.States state, CurrencyPair pair) {
        viewStateInfoSubject.onNext(new ViewStateInfo(state, pair));
    }

    private void emitNewViewState(ViewStateInfo.States state) {
        viewStateInfoSubject.onNext(new ViewStateInfo(state));

    }

    private void emitNewViewState(ViewStateInfo stateInfo) {
        viewStateInfoSubject.onNext(stateInfo);
    }

    private boolean isValidPair(Pair<String, String> pair) {
        return pair.first.length() == 3 && pair.second.length() == 3;
    }

    private Single<CurrencyPair> getPair(String pair) {
        return pairDao.getPair(pair);
    }

    private void insert(CurrencyPair pair) {
        Completable.fromCallable(() -> pairDao.insert(pair))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();
    }

}
