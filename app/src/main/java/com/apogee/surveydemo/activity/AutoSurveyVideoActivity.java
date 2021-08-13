package com.apogee.surveydemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.apogee.surveydemo.R;

import java.io.File;


public class AutoSurveyVideoActivity extends Activity {

    String videoPath;
    ImageButton close;
    ImageView imageView;
    VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_survey_video);

        Intent intent=getIntent();
        videoPath = intent.getStringExtra("videoPath");
         videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageview);
        close = findViewById(R.id.close);

        close.setOnClickListener(v -> {
            finish();

        });

        if(videoPath.contains(".jpg")){
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.INVISIBLE);
            File imgFile = new  File(videoPath);

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);

            }
        }else if(videoPath.contains(".mp4")){
            imageView.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);

            //Creating MediaController
            MediaController mediaController= new MediaController(this);
            mediaController.setAnchorView(videoView);


            //Setting MediaController and URI, then starting the videoView
            videoView.setMediaController(mediaController);
            videoView.setVideoPath(videoPath);
            videoView.requestFocus();
            videoView.start();
        }



    }



}
