package com.example.currencyconverter.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.currencyconverter.entities.CurrencyPair;

import io.reactivex.Single;

@Dao
public interface CurrencyPairDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
        //on start point cache database is empty and its ok
        //but then updated information should be stored replacing old
    long insert(CurrencyPair pair);

        //we are not interested in data flow on future database changes - Single
    @Query("SELECT * from exchange_table WHERE pair = :pair")
    Single<CurrencyPair> getPair(String pair);
}
