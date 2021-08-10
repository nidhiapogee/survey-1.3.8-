package com.apogee.surveydemo.multiview;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ItemDropword extends RecyclerView.ViewHolder {

    TextView txtdrop;
    Spinner spindrop;

    String title;

    public ItemDropword(@NonNull View itemView) {
        super(itemView);
        txtdrop = itemView.findViewById(R.id.txtdrop);
        spindrop = itemView.findViewById(R.id.spindrop);
    }


    public void setDropdown(List<String> listdropdown,final OnItemValueListener onItemValueListener) {

        spindrop.setAdapter(new ArrayAdapter<>(itemView.getContext(), R.layout.support_simple_spinner_dropdown_item, listdropdown));
        spindrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemname =  parent.getItemAtPosition(position).toString();
                title = txtdrop.getText().toString();
                onItemValueListener.returnValue(title,itemname);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setDropdown(final Map<String,String> listdropdown, final OnItemValueListener onItemValueListener) {
        Set<String> baudratekey = listdropdown.keySet();

        List<String> baudratevaluelist = new ArrayList<>(baudratekey);

        spindrop.setAdapter(new ArrayAdapter<>(itemView.getContext(), R.layout.support_simple_spinner_dropdown_item, baudratevaluelist));
        spindrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemname =  parent.getItemAtPosition(position).toString();
                title = txtdrop.getText().toString();
                String itemValue = listdropdown.get(itemname);
                onItemValueListener.returnValue(title,itemValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }





}
