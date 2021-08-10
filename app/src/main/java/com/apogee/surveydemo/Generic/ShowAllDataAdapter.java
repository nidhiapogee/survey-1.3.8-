package com.apogee.surveydemo.Generic;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.R;

import java.util.List;

public class ShowAllDataAdapter extends RecyclerView.Adapter<ShowAllDataAdapter.RecordViewHolder> {
    Context recordContext;
    List<Record> recordList;

    public ShowAllDataAdapter(Context context, List<Record> records) {
        recordList = records;
        recordContext = context;
    }


    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.import_record, viewGroup, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        final Record record = recordList.get(position);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize =7;
            holder.sNo.setText(record.sNo);
            holder.Point_name.setText(record.Point_name);
            holder.Easting.setText(record.Easting);
            holder.Northing.setText(record.Northing);
            holder.Elevation.setText(record.Elevation);
            holder.Point_code.setText(record.Point_Code);
            holder.Horizontal_Precision.setText(record.Horizontal_Precision);
            holder.Vertical_Precision.setText(record.Vertical_Precision);
            holder.Date.setText(record.Date);
            holder.Time.setText(record.Time);
            holder.Antenna_Height.setText(record.Antenna_Height);
            holder.Record_Type.setText(record.Record_Type);
            holder.Precision_Type.setText(record.Precision_Type);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public void add(Record record) {
        recordList.add(record);
        notifyDataSetChanged();
    }

    public void add(
            List<Record> record) {

        recordList = record;

        notifyDataSetChanged();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        public TextView sNo;
        public TextView Point_name;
        public TextView Easting;
        public TextView Northing;
        public TextView Elevation;
        public TextView Point_code;
        public TextView Horizontal_Precision;
        public TextView Vertical_Precision;
        public TextView Date;
        public TextView Time;
        public TextView Antenna_Height;
        public TextView Record_Type;
        public TextView Precision_Type;


        public RecordViewHolder(View view) {
            super(view);
            sNo = view.findViewById(R.id.snno);
            Point_name = view.findViewById(R.id.Point_name);
            Easting = view.findViewById(R.id.Easting);
            Northing = view.findViewById(R.id.Northing);
            Elevation = view.findViewById(R.id.Elevation);
            Point_code = view.findViewById(R.id.Point_code);
            Horizontal_Precision = view.findViewById(R.id.Horizontal_Precision);
            Vertical_Precision = view.findViewById(R.id.Vertical_Precision);
            Date = view.findViewById(R.id.Date);
            Time = view.findViewById(R.id.Time);
            Antenna_Height = view.findViewById(R.id.Antenna_Height);
            Record_Type = view.findViewById(R.id.Record_Type);
            Precision_Type = view.findViewById(R.id.Precision_Type);
        }

    }


}
