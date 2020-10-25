package com.bapplications.maplemobile.ui.view_models;


import com.bapplications.maplemobile.ui.GameActivityUIManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameActivityViewModel extends ViewModel {

    private MutableLiveData<Short> hp = new MutableLiveData<>();
    public LiveData<Short> getHp() {
        return hp;
    }
    public void setHp(short hp){
        this.hp.postValue(hp);
    }

    private MutableLiveData<Short> max_hp = new MutableLiveData<>();
    public LiveData<Short> getMax_hp() {
        return max_hp;
    }
    public void setMaxHp(short maxHp){
        this.max_hp.postValue(maxHp);
    }

    private MutableLiveData<Short> mp = new MutableLiveData<>();
    public LiveData<Short> getMp() {
        return mp;
    }
    public void setMp(short mp){
        this.mp.postValue(mp);
    }

    private MutableLiveData<Short> max_mp = new MutableLiveData<>();
    public LiveData<Short> getMax_mp() {
        return max_mp;
    }
    public void setMaxMp(short maxMp){
        this.max_mp.postValue(maxMp);
    }

    private MutableLiveData<Integer> exp = new MutableLiveData<>();
    public LiveData<Integer> getExp() { return exp; }
    public void setExp(int exp) { this.exp.postValue(exp);}

    private MutableLiveData<Integer> max_exp = new MutableLiveData<>();
    public LiveData<Integer> getMax_exp() {
        return max_exp;
    }
    public void setMaxExp(int maxExp){ this.max_exp.postValue(maxExp); }

    private MutableLiveData<GameActivityUIManager.WindowState> windowState = new MutableLiveData<>(GameActivityUIManager.WindowState.GONE);
    public MutableLiveData<GameActivityUIManager.WindowState> getWindowState() {
        return windowState;
    }

    public void setWindowState(GameActivityUIManager.WindowState state){
        if(windowState.getValue() != state)
        this.windowState.postValue(state); }
}
