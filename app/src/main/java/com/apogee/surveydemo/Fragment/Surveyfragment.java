package com.apogee.surveydemo.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.apogee.surveydemo.AutoMap;
import com.apogee.surveydemo.GridAdapter;
import com.apogee.surveydemo.MeterActivity;
import com.apogee.surveydemo.R;
import com.apogee.surveydemo.Stakeselection;
import com.apogee.surveydemo.TopoMap;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Surveyfragment extends Fragment {
    SharedPreferences sharedPreferences;
    public static final String antennapref = "antenapref";
    GridView grid;
    String[] web = {
            "Topo Survey", "Auto Survey", "Stake Point", "View Meter   Survey",


    } ;
    int[] imageId = {R.drawable.placeholder, R.drawable.auto, R.drawable.stakepoint,R.drawable.meter_surveyy,


    };

    public Surveyfragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);


        GridAdapter adapter = new GridAdapter(getActivity().getApplicationContext(), web, imageId);
        grid = view.findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener((parent, view1, position, id) -> {
            if(position==0) {
                alertdialog(getString(R.string.title_activity_topo_map));
            }
            else if(position==1)
            {
                alertdialog(getString(R.string.title_activity_auto_map));

            }else if(position==2){
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.you_clicked_at)+" " +web[+ position], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getApplicationContext(), Stakeselection.class);
                startActivity(intent);
            }else if(position == 3){
                Intent intent = new Intent(getActivity().getApplicationContext(), MeterActivity.class);
                startActivity(intent);
            }
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.you_clicked_at)+" " + web[+position], Toast.LENGTH_SHORT).show();

        });
        return view;
    }

    /*Check dialog for Antenna setup is completed or not/*/
    public void dialogantenna(final String msg){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogantenna, null);
        final String[] antennatype = new String[1];
        final EditText editText = dialogView.findViewById(R.id.nm);
        final EditText editText1 = dialogView.findViewById(R.id.rds);
        final ImageView imgvw = dialogView.findViewById(R.id.imgv);
        final Spinner spinner = dialogView.findViewById(R.id.antennatype);
        Button button1 = dialogView.findViewById(R.id.antnabtn);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String antenna = sharedPreferences.getString(antennapref, "default value");
        if(!antenna.equalsIgnoreCase("default value")) {
            String pname = antenna.split("_")[0];
            String height = antenna.split("_")[1];
            editText.setText(pname);
            editText1.setText(height);
        }
        final ArrayList<String> typelist = new ArrayList<>();
        typelist.add(getString(R.string.vertical));
        typelist.add(getString(R.string.slope));
        final ArrayAdapter<String> model_typeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, typelist);
        model_typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(model_typeAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                antennatype[0] = parent.getItemAtPosition(position).toString();
                if(antennatype[0].equalsIgnoreCase(getString(R.string.vertical))){
                    imgvw.setImageResource(R.drawable.vertical);
                }else if(antennatype[0].equalsIgnoreCase(getString(R.string.slope))){
                    imgvw.setImageResource(R.drawable.slope);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button1.setOnClickListener(view -> {
            // DO SOMETHINGS
            String pointname = editText.getText().toString().trim();
            String height = editText1.getText().toString().trim();
            if(!pointname.equalsIgnoreCase("") && !height.equalsIgnoreCase("")){
                if(antennatype[0].equalsIgnoreCase(getString(R.string.vertical))){
                    editor.putString(antennapref, pointname+"_"+height+"_"+getString(R.string.vertical));
                    editor.apply();
                }else if(antennatype[0].equalsIgnoreCase(getString(R.string.slope))){
                    editor.putString(antennapref, pointname+"_"+height+"_"+getString(R.string.slope));
                }

                if(msg.equalsIgnoreCase(getString(R.string.title_activity_topo_map))){
                    Intent intent = new Intent(getActivity().getApplicationContext(), TopoMap.class);
                    startActivity(intent);
                }else if(msg.equalsIgnoreCase(getString(R.string.title_activity_auto_map))){
                    Intent intent = new Intent(getActivity().getApplicationContext(), AutoMap.class);
                    startActivity(intent);
                }

            }else{
                Toast.makeText(getContext(), getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
            dialogBuilder.dismiss();
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        Window window = dialogBuilder.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    public void alertdialog(final String msg) {
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(getContext());
        builder1.setTitle(getString(R.string.antenna_setup));
        builder1.setMessage(getString(R.string.did_you_configure_antenna));
        builder1.setCancelable(true);


        builder1.setPositiveButton(
                getText(R.string.configure),
                (dialog, id) -> {
                    dialogantenna(msg);
                    dialog.cancel();
                });


        builder1.setNegativeButton(getText(R.string.continue1), (dialog, which) -> {
            if(msg.equalsIgnoreCase(getString(R.string.title_activity_topo_map))){
                Intent intent = new Intent(getActivity().getApplicationContext(), TopoMap.class);
                startActivity(intent);
            }else if(msg.equalsIgnoreCase(getString(R.string.title_activity_auto_map))){
                Intent intent = new Intent(getActivity().getApplicationContext(), AutoMap.class);
                startActivity(intent);
            }

        });

        android.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
