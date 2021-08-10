package com.apogee.surveydemo.multiview;


import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class RecycerlViewAdapter extends RecyclerView.Adapter {
    Context context;
    List<ItemType> itemTypeList;
    OnItemValueListener onItemValueListener;

    String lat = "";
    String lng = "";
    String alti = "";
    String accuracy = "";

    public RecycerlViewAdapter(List<ItemType> itemTypeList, OnItemValueListener onItemValueListener) {
        this.itemTypeList = itemTypeList;
        this.onItemValueListener = onItemValueListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == 0) {
            View itemdropview = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_dropdown, parent, false);
            return new ItemDropword(itemdropview);
        } else if (viewType == 1) {
            View iteminputview = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_input, parent, false);
            return new ItemInput(iteminputview, onItemValueListener);
        } else if (viewType == 2) {
            View plistview = LayoutInflater.from(parent.getContext()).inflate(R.layout.projectlistitem, parent, false);
            return new InputProjectlist(plistview, onItemValueListener);
        } else if (viewType ==3){
            View ontxt = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_text_text, parent, false);
            return new inputonlytext(ontxt,onItemValueListener);
        }
        return null;
    }

    int selectedposition = -2;

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ItemType itemType = itemTypeList.get(position);
        switch (itemType.type) {
            case ItemType.DROPDOWNTYPE:
                ItemDropword itemDropword = (ItemDropword) holder;
                itemDropword.txtdrop.setText(itemType.title);
                itemDropword.setDropdown(itemType.getStringStringMapdrop(),onItemValueListener);
                break;
            case ItemType.INPUTTYPE:
                ItemInput itemInput = (ItemInput) holder;
                DatabaseOperation db = new DatabaseOperation(context);
                db.open();
                String rmkvalue = db.retrnfromtfromrmrk(itemType.title);
                String inputhint = db.inputhint(itemType.title);
                if (rmkvalue != null) {
                    if (rmkvalue.equals("1")) {
                        itemInput.edinput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    }
                }
                if(itemType.title.equals("Latitude") && lat != null && !lat.equals("") ){
                    itemInput.edinput.setText(lat);
                    onItemValueListener.returnValue(itemType.title, hexString(lat));
                }else  if(itemType.title.equals("Longitude") && lng != null && !lng.equals("")){
                    itemInput.edinput.setText(lng);
                    onItemValueListener.returnValue(itemType.title, hexString(lng));
                }else  if(itemType.title.equals("Altitude") && alti != null && !alti.equals("") ){
                    itemInput.edinput.setText(alti);
                    onItemValueListener.returnValue(itemType.title, hexString(alti));
                }else  if(itemType.title.equals("Accuracy") && accuracy != null && !accuracy.equals("")){
                    itemInput.edinput.setText(String.valueOf(hexToInt(accuracy)/100));
                    onItemValueListener.returnValue(itemType.title, String.valueOf(hexToInt(accuracy)/100));
                }
                else {
                    itemInput.edinput.setText("");
                }
                if(inputhint!=null){
                    itemInput.edinput.setHint(inputhint);
                }
                itemInput.txtinput.setText(itemType.title);
                break;
            case ItemType.INPUTTYPEPROJECT:
                final InputProjectlist inputProjectlist = (InputProjectlist) holder;
                inputProjectlist.txtpname.setText(itemType.title);
                inputProjectlist.txtoprtr.setText(itemType.oprtr);
                inputProjectlist.txttime.setText(itemType.time);
                inputProjectlist.setBackgroundView(position);
               final String operation = itemType.oprtr;
                if (selectedposition != position) {
                    inputProjectlist.itemView.setBackgroundColor(inputProjectlist.itemView.getContext().getResources().getColor(R.color.white));
                } else {
                    inputProjectlist.itemView.setBackgroundColor(inputProjectlist.itemView.getContext().getResources().getColor(R.color.colorDarkDim));
                }

                inputProjectlist.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectedposition=position;
                        onItemValueListener.returnValue(inputProjectlist.txtpname.getText().toString()+"##longclick", inputProjectlist.txttime.getText().toString(),position,operation);
                        notifyDataSetChanged();
                        return false;
                    }
                });

                inputProjectlist.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectedposition=position;
                        onItemValueListener.returnValue(inputProjectlist.txtpname.getText().toString()+"##onclick", inputProjectlist.txttime.getText().toString(),position,operation);
                        notifyDataSetChanged();
                    }
                });

                break;

            case ItemType.INPUTONLYTEXT:
                final inputonlytext onlytxt = (inputonlytext) holder;
                onlytxt.txtheader.setText(itemType.title);
                onlytxt.txtval.setText(itemType.oprtr);
                onlytxt.setBackgroundView(position);
                final String operat = itemType.oprtr;
                if (selectedposition != position) {
                    onlytxt.itemView.setBackgroundColor(onlytxt.itemView.getContext().getResources().getColor(R.color.white));
                } else {
                    onlytxt.itemView.setBackgroundColor(onlytxt.itemView.getContext().getResources().getColor(R.color.colorDarkDim));
                }

                onlytxt.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectedposition=position;
                        onItemValueListener.returnValue(onlytxt.txtheader.getText().toString(), onlytxt.txtval.getText().toString(),position,operat);
                        notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemTypeList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (itemTypeList.get(position).getType() == 0) {
            return 0;
        } else if (itemTypeList.get(position).getType() == 1) {
            return 1;
        } else if(itemTypeList.get(position).getType() == 2){
            return 2;
        } else {
            return 3;
        }


    }

    public static class VARIABLE {

        int selectedposition = -2;

        public int getSelectedposition() {
            return selectedposition;
        }

        public void setSelectedposition(int selectedposition) {
            this.selectedposition = selectedposition;
        }
    }

    public void setListAdapter(String latitude, String longtitude, String altitude, String accuracy){
        lat = latitude;
        lng = longtitude;
        alti = altitude;
        this.accuracy = accuracy;
        notifyDataSetChanged();

    }

    private String hexString(String input) {

        char[] charinput = input.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < charinput.length; i++) {
            stringBuilder.append(Integer.toHexString(charinput[i]));
        }
        return stringBuilder.toString();
    }

    public int hexToInt(String hex){
        // Parse hex to int
        if(hex.contains(".")){
            hex = hex.replace(".","");
        }
        int value = Integer.parseInt(hex, 16);
        // Flip byte order using ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.asIntBuffer().put(value);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int flipped = buffer.asIntBuffer().get();

        System.out.println("hex: 0x" + hex);
        System.out.println("flipped: " + flipped);

        return flipped;
    }
}
