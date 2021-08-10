package com.apogee.surveydemo.multiview;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.R;

public class InputProjectlist extends RecyclerView.ViewHolder {

    TextView txtpname;
    TextView txtoprtr;
    TextView txttime;
    RecycerlViewAdapter.VARIABLE variable;
    public void setBackgroundView(int position) {
    }

    public InputProjectlist(@NonNull final View itemView, final OnItemValueListener onItemValueListener) {
        super(itemView);
        txtpname = itemView.findViewById(R.id.pname);
        txtoprtr = itemView.findViewById(R.id.opname);
        txttime = itemView.findViewById(R.id.datentime);
        variable=new RecycerlViewAdapter.VARIABLE();

       /* itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                onItemValueListener.returnValue(txtpname.getText().toString(), txttime.getText().toString(), getAdapterPosition());
                return false;
            }
        });*/


       /* itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                variable.setSelectedposition(getAdapterPosition());
                onItemValueListener.returnValue(txtpname.getText().toString(), txttime.getText().toString());
            }
        });*/
    }



}
