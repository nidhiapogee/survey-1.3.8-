package com.apogee.surveydemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class CRSListAdapter extends RecyclerView.Adapter<CRSListAdapter.RecordViewHolder> {
    Context recordContext;
    List<String> list;
    String gpgsv =  "GPGSV";
    String glgsv = "GLGSV";
    String gagsv = "GAGSV";
    String gbgsv = "GBGSV";


    public CRSListAdapter(Context context, List<String> imgList) {
        list = imgList;
        recordContext = context;
    }



    @NotNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_crs_list, viewGroup, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, final int position) {
        final String data = list.get(position);
        if (data.contains("$GPGSV")) {//GPS, SBAS, QZSS
            try {
                String noofsat = data.split(",")[4];
                int satno = Integer.parseInt(noofsat);
                String selvtn = data.split(",")[5];
                String sazmth = data.split(",")[6];
                holder.type.setText(gpgsv);
                holder.satNo.setText(noofsat);
                holder.elevation.setText(selvtn);
                holder.azamuth.setText(sazmth);
                if (satno >= 1 && satno < 33) {//GPS

                     holder.color.setBackgroundColor(ContextCompat.getColor(recordContext, R.color.colorgreen));
                } else if (satno >= 120 && satno < 159) {//SBAS
                       holder.color.setBackgroundColor(ContextCompat.getColor(recordContext, R.color.color_blued));
                } else if (satno >= 193 && satno < 197) {//QZSS
                    holder.color.setBackgroundColor(ContextCompat.getColor(recordContext, R.color.colorPrimary1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (data.contains("$GLGSV")) {//GLONASS
            try {
                String noofsat = data.split(",")[4];
                String selvtn = data.split(",")[5];
                String sazmth = data.split(",")[6];
                holder.type.setText(glgsv);
                holder.satNo.setText(noofsat);
                holder.elevation.setText(selvtn);
                holder.azamuth.setText(sazmth);
                holder.color.setBackgroundColor(ContextCompat.getColor(recordContext, R.color.coloryellow));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (data.contains("$GAGSV")) {//Galileo
            try {
                String noofsat = data.split(",")[4];
                String selvtn = data.split(",")[5];
                String sazmth = data.split(",")[6];
                holder.type.setText(gagsv);
                holder.satNo.setText(noofsat);
                holder.elevation.setText(selvtn);
                holder.azamuth.setText(sazmth);
                holder.color.setBackgroundColor(ContextCompat.getColor(recordContext, R.color.colorlightblue));

            } catch (Exception e) {

                e.printStackTrace();
            }
        } else if (data.contains("$GBGSV")) {//BeiDou
            try {
                String noofsat = data.split(",")[4];
                String selvtn = data.split(",")[5];
                String sazmth = data.split(",")[6];

                holder.type.setText(gbgsv);
                holder.satNo.setText(noofsat);
                holder.elevation.setText(selvtn);
                holder.azamuth.setText(sazmth);
                holder.color.setBackgroundColor(ContextCompat.getColor(recordContext, R.color.coloret));
            } catch (Exception e) {

                e.printStackTrace();
            }

        }


    }




    @Override
    public int getItemCount() {
        if(list != null){
            return   list.size();
        } else{
            return  0;
        }


    }




    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        TextView type, satNo,elevation,azamuth;
        ImageView color;


        public RecordViewHolder(View view) {
            super(view);

            satNo = view.findViewById(R.id.satNo);
            elevation = view.findViewById(R.id.elevation);
            azamuth = view.findViewById(R.id.azamuth);
            color = view.findViewById(R.id.color);
            type = view.findViewById(R.id.type);

        }

    }




}

