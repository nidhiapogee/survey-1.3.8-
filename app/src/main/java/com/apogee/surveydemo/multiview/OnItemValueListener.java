package com.apogee.surveydemo.multiview;

import android.view.View;

public interface OnItemValueListener {

    void returnValue(String title,String finalvalue);
    void returnValue(String title,String finalvalue,int position, String operation);


    public interface OnClickRecyclerListner
    {

        public void onClick(View view);

    }
}
