package com.apogee.surveydemo.Sattelite;

import android.view.View;

public class Skymodel {
    private float mAzim;
    private int mElev;
    private View mView;


    public float getmAzim() {
        return mAzim;
    }

    public void setmAzim(float mAzim) {
        this.mAzim = mAzim;
    }

    public int getmElev() {
        return mElev;
    }

    public void setmElev(int mElev) {
        this.mElev = mElev;
    }

    public View getmView() {
        return mView;
    }

    public void setmView(View mView) {
        this.mView = mView;
    }

    public Skymodel(int mElev, float mAzim, View mView) {
        this.mElev = mElev;
        this.mAzim = mAzim;
        this.mView = mView;
    }




}
