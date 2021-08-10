package com.apogee.surveydemo.utility;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.R;
import com.apogee.surveydemo.model.BleModel;

public class BLEService extends IntentService {
Context context;
DatabaseOperation dbTask;
    public BLEService() {
        super("CableService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
       dbTask=new DatabaseOperation(BLEService.this);
        dbTask.open();
        BleModel model = new BleModel(BLEService.this);
        dbTask.close();
        long result = model.requestBleDetail();
        if(result!=0){
            Toast.makeText(context, getString(R.string.data_recieved_successfully), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, getString(R.string.oops_something_went_wrong), Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        context = getApplicationContext();

    }

}
