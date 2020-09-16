package com.bapplications.maplemobile.views;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameActivityViewModel extends ViewModel {
    private MutableLiveData<Short> hp = new MutableLiveData<>();
    private MutableLiveData<Short> max_hp = new MutableLiveData<>();

    private MutableLiveData<Short> mp = new MutableLiveData<>();
    private MutableLiveData<Short> max_mp = new MutableLiveData<>();

    private MutableLiveData<Integer> exp = new MutableLiveData<>();
    private MutableLiveData<Integer> max_exp = new MutableLiveData<>();

    public LiveData<Short> getHp() {
        return hp;
    }

    public void setHp(short hp){
        this.hp.postValue(hp);
    }

    public LiveData<Short> getMax_hp() {
        return max_hp;
    }

    public void setMaxHp(short maxHp){
        this.max_hp.postValue(maxHp);
    }

    public LiveData<Short> getMp() {
        return mp;
    }

    public void setMp(short mp){
        this.mp.postValue(mp);
    }

    public LiveData<Short> getMax_mp() {
        return max_mp;
    }

    public void setMaxMp(short maxMp){
        this.max_mp.postValue(maxMp);
    }

    public LiveData<Integer> getExp() {
        return exp;
    }

    public void setExp(int exp){
        this.exp.postValue(exp);
    }

    public LiveData<Integer> getMax_exp() {
        return max_exp;
    }

    public void setMaxExp(int maxExp){
        this.max_exp.postValue(maxExp);
    }

}
