package com.apogee.surveydemo.multiview;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apogee.surveydemo.R;
/*Created by Abhijeet*/

/*For only text input*/
public class inputonlytext extends RecyclerView.ViewHolder {
    TextView txtheader;
    TextView txtval;
    Button view;

    public void setBackgroundView(int position) {


    }

    public inputonlytext(@NonNull View itemView, final OnItemValueListener onItemValueListener) {
        super(itemView);

        txtheader = itemView.findViewById(R.id.txtinput);
        txtval = itemView.findViewById(R.id.txtinput2);
        view = itemView.findViewById(R.id.btView);

        itemView.setOnLongClickListener(v -> {

            onItemValueListener.returnValue(txtheader.getText().toString(), txtval.getText().toString(),getAdapterPosition(),"");
            return false;
        });
    }
}
