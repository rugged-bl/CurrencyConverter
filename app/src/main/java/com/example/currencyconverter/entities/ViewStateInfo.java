package com.example.currencyconverter.entities;

//Encapsulates all information to represent correct information on main view
public class ViewStateInfo {
    public enum States{
        NOTHING, NO_CONNECTION, INVALID_PAIRS, LOCAL_RESULT_READY, RESULT_READY, LOADING
    }

    private States state;
    private CurrencyPair pair;

    public ViewStateInfo(States state, CurrencyPair pair) {
        this.state = state;
        this.pair = pair;
    }

    public ViewStateInfo(States state, String str) {
        this(state, new CurrencyPair(str));
    }

    public ViewStateInfo(States state) {
        this(state, new CurrencyPair(""));
    }

    public States getState() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

    public CurrencyPair getPair() {
        return pair;
    }

    public void setPair(CurrencyPair pair) {
        this.pair = pair;
    }
}
