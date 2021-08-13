package com.apogee.surveydemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.Generic.Record;
import com.apogee.surveydemo.Generic.ShowAllDataAdapter;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.apogee.surveydemo.Generic.taskGeneric.Name;

/*This class is all about importing csv and txt data into database*/
public class Import extends AppCompatActivity {
    ShowAllDataAdapter showAllDataAdapter;
    List<Record> recordlist = new ArrayList<>();
    RecyclerView recordsView;
    Toolbar toolbar;
    private static final int ACTIVITY_CHOOSE_FILE1 = 100;
    ArrayList<String> newlist;
    DatabaseOperation dbtask = new DatabaseOperation(this);
    int tskid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        toolbar=findViewById(R.id.tool);
        toolbar.setTitle(getString(R.string.import_new));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Import.this);
        String taskname = sharedPreferences.getString(Name, "default value");
        if(taskname.equalsIgnoreCase("default value")){
            Toast.makeText(this, getString(R.string.select_task_first_or_create_new_one), Toast.LENGTH_SHORT).show();
        }
        dbtask.open();
        tskid = dbtask.gettaskid(taskname);
        final FrameLayout frmlt = findViewById(R.id.frmlt);
        FloatingTextButton savebutton = findViewById(R.id.save_button);
        FloatingTextButton importFile = findViewById(R.id.importFile);

        savebutton.setOnClickListener(view -> {
            if(!recordlist.isEmpty()){
                alertdialog();
            }else{
                Snackbar.make(frmlt, getString(R.string.no_data_found), Snackbar.LENGTH_SHORT).show();
            }

        });

        importFile.setOnClickListener(v -> selectCSVFile());

    }

    private void selectCSVFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.open_csv)), ACTIVITY_CHOOSE_FILE1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_CHOOSE_FILE1) {
            if (resultCode == RESULT_OK) {
                try {
                    final List list = readCSV(data.getData());
                    newlist = new ArrayList<>(list);
                    Record record = new Record();
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < newlist.size(); i++) {
                        builder.append(newlist.get(i) + "\n");
                        String value = newlist.get(i);
                        if (i != 0) {
                            String[] colums = value.split(",");
                            if (colums.length != 12) {
                                Toast.makeText(this, getString(R.string.unsupport_csv_row), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    record = new Record();
                                    record.sNo = String.valueOf(i);
                                    record.Point_name = colums[0].trim();
                                    record.Easting = colums[1].trim();
                                    record.Northing = colums[2].trim();
                                    record.Elevation = colums[3].trim();
                                    record.Point_Code = colums[4].trim();
                                    record.Horizontal_Precision = colums[5].trim();
                                    record.Vertical_Precision = colums[6].trim();
                                    record.Date = colums[7].trim();
                                    record.Time = colums[8].trim();
                                    record.Antenna_Height = colums[9].trim();
                                    record.Record_Type = colums[10].trim();
                                    record.Precision_Type = colums[11].trim();
                                    recordlist.add(record);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    viewgeneration(recordlist);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadcsv(View view){
      selectCSVFile();
    }

    void viewgeneration(List<Record> recordlist){
      showAllDataAdapter = new ShowAllDataAdapter(this, new ArrayList<Record>());
      recordsView = findViewById(R.id.records_view);
      recordsView.setHasFixedSize(true);
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
      linearLayoutManager.scrollToPositionWithOffset(2, 20);
      recordsView.setLayoutManager(linearLayoutManager);
      recordsView.setAdapter(showAllDataAdapter);
      showAllDataAdapter.add(recordlist);
    }

    public void alertdialog() {
        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alertyesorno, null);
        Button button1 = dialogView.findViewById(R.id.positive);
        Button button2 = dialogView.findViewById(R.id.negativebutton);
        TextView textView1 = dialogView.findViewById(R.id.header);
        TextView textView2 = dialogView.findViewById(R.id.messaggg);

        textView1.setText(getString(R.string.save_data));
        textView2.setText(getString(R.string.do_you_want_to_save_these_data));
        button1.setText(getString(R.string.save));
        button2.setText(getString(R.string.no));

        dialogBuilder.setCancelable(true);

        button2.setOnClickListener(view -> dialogBuilder.dismiss());

        button1.setOnClickListener(view -> {
            dialogBuilder.dismiss();
            StringBuilder builder = new StringBuilder();
            boolean result = false;
            for(int i = 0; i < newlist.size(); i++){
                builder.append(newlist.get(i) + "\n");
                String value = newlist.get(i);
                if(i!=0){
                    String[] colums = value.split(",");
                    if(colums.length != 12){
                        Toast.makeText(Import.this, getString(R.string.unsupport_csv_row), Toast.LENGTH_SHORT).show();
                    }else{
                        try{
                            dbtask.open();
                            result = dbtask.insertTopo(colums[10].trim(), colums[0].trim(), colums[4].trim(),tskid,Double.parseDouble(colums[1].trim()),Double.parseDouble(colums[2].trim()),colums[3].trim(),Double.parseDouble(colums[5].trim()),Double.parseDouble(colums[6].trim()),colums[11].trim(),colums[9].trim(),"43Q");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            /*If data saved successfully*/
            if(result){
                Toast.makeText(Import.this, getString(R.string.data_saved_successfully), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Import.this, getString(R.string.oop_something_went_wrong), Toast.LENGTH_SHORT).show();
            }

        });

        dialogBuilder.setView(dialogView);
        Window window = dialogBuilder.getWindow();
        dialogBuilder.show();
        if(window != null){ // After the window is created, get the SoftInputMode
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    /*Conversion of csv to list*/
    @NotNull
    public final List readCSV(@NotNull Uri uri) throws IOException {
        Intrinsics.checkParameterIsNotNull(uri, "uri");
        InputStream csvFile = this.getContentResolver().openInputStream(uri);
        InputStreamReader isr = new InputStreamReader(csvFile);
        return TextStreamsKt.readLines(new BufferedReader(isr));
    }
}