package com.apogee.surveydemo.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.Connect;
import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.ProjectList;
import com.apogee.surveydemo.R;
import com.apogee.surveydemo.model.Operation;
import com.apogee.surveydemo.multiview.ItemType;
import com.apogee.surveydemo.multiview.OnItemValueListener;
import com.apogee.surveydemo.multiview.RecycerlViewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class User extends Fragment implements OnItemValueListener {
    View view;
    /*Model Detail*/
    Spinner modeltype,modelname;
    ArrayList<String> modeltypeList,mnList;
    DatabaseOperation dbTask;
    TextView mnText,tvmt;
    CardView Card1;
    int model_type_id=0;
    String dgps_id = "0",device_id;
    Button connectBtn2;
    Map<String, String> selectionValue1 = new HashMap<>();

    String ble_device_name;
    String dgps_device_name;
    String dg_device_name;

    RecycerlViewAdapter recycerlViewAdapter;
    RecyclerView recylcerview;
    List<ItemType> itemTypeList = new ArrayList<>();
    String selectedmodeule = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_user, container, false);
        final Context context = inflater.getContext();
        /*Model Detail*/
        dbTask=new DatabaseOperation(context);
        mnText= view.findViewById(R.id.mntxt);
        modelname= view.findViewById(R.id.mn);
        tvmt = view.findViewById(R.id.tvmt);
        connectBtn2 = view. findViewById(R.id.connect1);
        Card1 = view. findViewById(R.id.card_view);

        recylcerview = view.findViewById(R.id.recylcerview);
        recycerlViewAdapter = new RecycerlViewAdapter(itemTypeList, this);

        recylcerview.setAdapter(recycerlViewAdapter);



        mnList=new ArrayList<>();
        modeltypeList=new ArrayList<>();
        dbTask.open();

        tvmt.setText(getString(R.string.finished));

        modelname.setVisibility(View.VISIBLE);
        connectBtn2.setVisibility(View.VISIBLE);
        model_type_id=dbTask.getModelType_id(getString(R.string.finished));
        mnList = dbTask.getmnypes(model_type_id);
        ArrayAdapter<String> mnAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, mnList);
        mnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelname.setAdapter(mnAdapter);

        modelname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                itemTypeList.clear();
                if (!item.equals("--select--")) {
                    dbTask.open();
                    selectedmodeule = item;
                    model_type_id = dbTask.getModelType_id2(item);
                    int device_id1 = dbTask.getModelType_id1(model_type_id);
                    Map<String, Map<String, String>> mapdetail=dbTask.finished(device_id1);
                    Set<String> devicetype = mapdetail.keySet();
                    for (String param: devicetype) {
                        selectionValue1 = mapdetail.get(param);
                        Set<String> moduledeviceid = selectionValue1.keySet();
                        Collection<String> devicename = selectionValue1.values();
                        String strNew = devicename.toString().replace("[", "");
                        String strNew1 = strNew.replace("]", "");
                        dgps_device_name=strNew1;
                        System.out.println(strNew1);
                        String strNew11 = moduledeviceid.toString().replace("[", "");
                        String strNew111 = strNew11.replace("]", "");
                        if(param.equalsIgnoreCase("BLE")){
                            device_id= strNew111;
                            ble_device_name = strNew1;
                        }else{
                            dgps_id=strNew111;
                            dg_device_name=strNew1;
                        }



                        System.out.println("Initial values : " + strNew1);
                        System.out.println("Initial values : " + strNew111);
                        itemTypeList.add(new ItemType(ItemType.INPUTONLYTEXT,param , null,dgps_device_name,null,""));
                        recycerlViewAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        connectBtn2.setOnClickListener(view -> {
            dbTask.open();
            String device_name =dbTask.modeldetail(device_id);
            String device_address = dbTask.deviceAdress(device_id);
            Intent intent = new Intent(context, ProjectList.class);
            intent.putExtra("device_name", device_name);
            intent.putExtra("device_address", device_address);
            intent.putExtra("device_id", device_id);
            intent.putExtra("dgps_device_id", dgps_id);
            intent.putExtra(" selectedmodeule" ,selectedmodeule);
            startActivity(intent);
            Operation operation = new Operation();

            operation.setDeviceid(model_type_id);
            operation.setDevicename(device_name);
            operation.setBleid(Integer.parseInt(device_id));
            operation.setBlename(ble_device_name);
            operation.setDgpsid(Integer.parseInt(dgps_id));
            operation.setDgpsname(dg_device_name);
            operation.setDevice_address(device_address);
            List<Operation> savedevicelist = new ArrayList<>();
            Gson gson = new Gson();
            if (((Connect) getActivity()).sharedPreferences.getString("SAVEDLIST", null) != null) {

                String json = ((Connect) getActivity()).sharedPreferences.getString("SAVEDLIST", null);



                Type type = new TypeToken<List<Operation>>(){}.getType();
                List<Operation> operationList = gson.fromJson(json, type);
                savedevicelist.addAll(operationList);
            }

            savedevicelist.add(operation);
            Set<Operation> stringSet = new HashSet<>();
            stringSet.addAll(savedevicelist);

            String json = gson.toJson(savedevicelist);
            ((Connect) getActivity()).editor.putString("SAVEDLIST", json);
            ((Connect) getActivity()).editor.apply();

            startActivity(intent);
        });
        return view;
    }

    @Override
    public void returnValue(String title, String finalvalue) {

    }

    @Override
    public void returnValue(String title, String finalvalue, int position, String operation) {

    }


}
