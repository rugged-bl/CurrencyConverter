package com.example.currencyconverter.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.currencyconverter.entities.CurrencyPair;

@Database(entities = {CurrencyPair.class}, version = 1)
public abstract class AppRoomDatabase extends RoomDatabase {

    public abstract CurrencyPairDao pairDao();

    private static AppRoomDatabase INSTANCE;

    public static AppRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, "currency_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}