package com.example.currencyconverter.converterActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.currencyconverter.R;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;


public class MainActivity extends AppCompatActivity implements MainView {
    // Tag used for debugging/logging
    public static final String TAG = "MainActivity";

    private MainPresenter presenter;
    private EditText editConvertFrom;
    private EditText editConvertTo;
    private LinearLayout convertBtn;
    private TextView textConvertBtn;
    private ProgressBar progressConvert;
    private RelativeLayout resultLayout;
    private TextView convertResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editConvertFrom = findViewById(R.id.editConvertFrom);
        editConvertTo = findViewById(R.id.editConvertTo);
        convertBtn = findViewById(R.id.buttonConvert);
        textConvertBtn = findViewById(R.id.textButtonConvert);
        progressConvert = findViewById(R.id.progressConvert);
        resultLayout = findViewById(R.id.resultLayout);
        convertResult = findViewById(R.id.textConvertResult);

        presenter = new MainPresenterImpl(getApplication(), this);

        Observable<Object> exchangePairObs = (RxView.clicks(convertBtn)
                .debounce(200, TimeUnit.MILLISECONDS)); //No matter to process clicks more often than 200ms

        presenter.convertHandler(exchangePairObs);

    }

    public Observable<Pair<String, String>> getConvertPair() {
        return Observable.zip(
                Observable.fromCallable(() -> editConvertFrom.getText().toString().toUpperCase()),
                Observable.fromCallable(() -> editConvertTo.getText().toString().toUpperCase()),
                Pair::new);
    }

    @Override
    public void noResult() {
        setResultLayoutGone();
        setProgressGone();
    }

    @Override
    public void noConnection() {
        convertResult.setText("Нет соединения");
        setResultMediumAppearance();
        setProgressGone();
        setResultLayoutVisible();
    }

    @Override
    public void resultReady(Double result) {
        convertResult.setText("" + result);
        setResultLargeAppearance();
        setProgressGone();
        setResultLayoutVisible();
    }

    @Override
    public void localResultReady(Double result) {
        convertResult.setText("устарело: " + result);
        setResultMediumAppearance();
        setProgressGone();
        setResultLayoutVisible();
    }

    @Override
    public void loadingPairs() {
        convertResult.setText("");
        setProgressVisible();
        setResultLayoutVisible();
    }

    @Override
    public void enteredInvalidPairs() {
        convertResult.setText("Нет таких пар");
        setProgressGone();
        setResultLayoutVisible();
    }

    private void setResultLayoutGone() {
        resultLayout.setVisibility(View.GONE);
    }

    private void setResultLayoutVisible() {
        resultLayout.setVisibility(View.VISIBLE);
    }

    private void setProgressGone() {
        textConvertBtn.setText(R.string.convertButtonText);
        progressConvert.setVisibility(View.GONE);
    }

    private void setProgressVisible() {
        textConvertBtn.setText("");
        progressConvert.setVisibility(View.VISIBLE);
    }

    private void setResultLargeAppearance() {
        convertResult.setTextAppearance(android.R.style.TextAppearance_Material_Large);
    }

    private void setResultMediumAppearance() {
        convertResult.setTextAppearance(android.R.style.TextAppearance_Material_Medium);
    }

    @Override
    protected void onStop() {
        super.onStop();

       presenter.dispose();
    }
}


