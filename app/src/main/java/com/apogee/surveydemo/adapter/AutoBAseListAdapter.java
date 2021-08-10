package com.apogee.surveydemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.R;
import com.apogee.surveydemo.multiview.ItemType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoBAseListAdapter extends RecyclerView.Adapter<AutoBAseListAdapter.RecordViewHolder> {
    Context recordContext;
    List<ItemType> list;
    ClickListerner clickListerner = null;
    DatabaseOperation dbTask;
    String operation = "";
    Map<String, String> map1 = new HashMap<>();

    public AutoBAseListAdapter(Context context, List<ItemType> imgList) {
        list = imgList;
        recordContext = context;
         dbTask = new DatabaseOperation(recordContext);
    }

    public interface ClickListerner {
        void onSuccess(String latitude, String longtitude, String altitude, String accuracy);
    }

    public void setListerner( ClickListerner clickListerner) {
        this.clickListerner = clickListerner;
    }


    @NotNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.projectlistitem, viewGroup, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, final int position) {
           final ItemType itemType = list.get(position);
        holder.pname.setText(itemType.title);
        holder.opname.setText(itemType.oprtr);
        holder.datentime.setText(itemType.time);
        String timeStamps = itemType.timeStamp;
        String [] spilitTimeStamp = timeStamps.split(" ");
        holder.timeStamp.setText(spilitTimeStamp[0]);
        holder.cv.setOnClickListener(v -> {
            dbTask.open();
            operation = itemType.oprtr;
            map1 = dbTask.getRoverparamdetails(itemType.title,itemType.time);
            String latitude = map1.get("Latitude");
            String longtitude = map1.get("Longtitude");
            String altitude  = map1.get("Altitude");
            String surveyAccuracy = map1.get("Survey Accuracy");
            if(clickListerner != null){
                clickListerner.onSuccess(latitude,longtitude,altitude,surveyAccuracy);
            }

        });


    }




    @Override
    public int getItemCount() {
        if(list != null){
            return   list.size();
        } else{
            return  0;
        }


    }



    public void add(ArrayList<ItemType> mlist) {

        list = mlist;

        notifyDataSetChanged();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        TextView pname,opname,datentime,timeStamp;
        CardView cv;

        public RecordViewHolder(View view) {
            super(view);

             pname = view.findViewById(R.id.pname);
             opname = view.findViewById(R.id.opname);
             datentime = view.findViewById(R.id.datentime);
             timeStamp = view.findViewById(R.id.timeStamp);
              cv = view.findViewById(R.id.cv);

        }

    }




}

