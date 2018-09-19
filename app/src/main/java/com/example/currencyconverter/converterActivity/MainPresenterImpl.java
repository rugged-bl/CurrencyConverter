package com.example.currencyconverter.converterActivity;

import android.app.Application;

import com.example.currencyconverter.entities.ViewStateInfo;
import com.example.currencyconverter.model.MainModel;
import com.example.currencyconverter.model.MainModelImpl;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class MainPresenterImpl implements MainPresenter {
    // Tag used for debugging/logging
    public static final String TAG = "MainPresenterImpl";

    private CompositeDisposable compositeDisposable;

    private MainView mainView;
    private MainModel mainModel;

    public MainPresenterImpl(Application application, MainView mainView) {

        compositeDisposable = new CompositeDisposable();

        this.mainView = mainView;
        this.mainModel = MainModelImpl.getInstance(application);
    }

    @Override
    public void convertHandler(Observable<Object> bind) {
        BehaviorSubject<ViewStateInfo> viewStateInfoSubject = mainModel.convertHandler(bind, mainView.getConvertPair());
        //this subject will store last view state and emit new when needed

        Disposable disposable = viewStateInfoSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewStateInfo -> {
                    switch (viewStateInfo.getState())
                    {
                        case NOTHING:
                            mainView.noResult();
                            break;
                        case LOADING:
                            mainView.loadingPairs();
                            break;
                        case RESULT_READY:
                            mainView.resultReady(viewStateInfo.getPair().getValue());
                            break;
                        case LOCAL_RESULT_READY:
                            mainView.localResultReady(viewStateInfo.getPair().getValue());
                            break;
                        case INVALID_PAIRS:
                            mainView.enteredInvalidPairs();
                            break;
                        case NO_CONNECTION:
                            mainView.noConnection();
                            break;
                    }
                });

        compositeDisposable.add(disposable);
    }

    @Override
    public void dispose() {
        if (compositeDisposable != null && compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
