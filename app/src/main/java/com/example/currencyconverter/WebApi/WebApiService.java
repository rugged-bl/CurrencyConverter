package com.example.currencyconverter.WebApi;



import android.content.Context;

import com.example.currencyconverter.BuildConfig;
import com.example.currencyconverter.entities.Value;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WebApiService {

    @GET("/api/v6/convert?compact=y")
    Single<HashMap<String, Value>> getRate(@Query("q") String currencyPair);

    /**
     * Factory class for convenient creation of the Api Service interface
     */
    class Factory {
        public static WebApiService create(String baseApiUrl, Context context) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().
                            setLevel((BuildConfig.DEBUG) ?
                                    HttpLoggingInterceptor.Level.BODY :
                                    HttpLoggingInterceptor.Level.NONE))
                    //.addInterceptor(new ConnectivityInterceptor(context))
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()) //automatically convert response to json
                    .client(okHttpClient)
                    .baseUrl(baseApiUrl)
                    .build();

            return retrofit.create(WebApiService.class);
        }
    }

}
