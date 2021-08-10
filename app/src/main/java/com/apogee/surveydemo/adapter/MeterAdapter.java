package com.apogee.surveydemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.R;

import java.util.ArrayList;


public class MeterAdapter extends RecyclerView.Adapter<MeterAdapter.RecordViewHolder> {
    Context recordContext;
    ArrayList<Bitmap> imgList = new ArrayList<>();
      ClickListerner clickListerner = null;

    public MeterAdapter(Context context, ArrayList<Bitmap> imgList) {
        imgList = imgList;
        recordContext = context;
    }

   public interface ClickListerner {
        void onSuccess(int pos);
    }

   public void setListerner( ClickListerner clickListerner) {
        this.clickListerner = clickListerner;
    }


    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_meter_adapter, viewGroup, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, final int position) {
        Bitmap image = imgList.get(position);
        holder.imageView.setImageBitmap(image);


        holder.ib_Cancel.setOnClickListener(v -> {
            imgList.remove(position);
            notifyDataSetChanged();
            if(clickListerner != null){
                clickListerner.onSuccess(position);
            }
        });

    }




    @Override
    public int getItemCount() {
        if(imgList != null){
          return   imgList.size();
        } else{
           return  0;
        }


    }



    public void add(Bitmap record) {
        imgList.add(record);
        notifyDataSetChanged();
    }

    public void add(ArrayList<Bitmap> image) {

        imgList = image;

        notifyDataSetChanged();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton ib_Cancel;

        public RecordViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            ib_Cancel = view.findViewById(R.id.ib_Cancel);

        }

    }


}

