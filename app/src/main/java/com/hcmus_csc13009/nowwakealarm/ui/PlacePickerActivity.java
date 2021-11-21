package com.hcmus_csc13009.nowwakealarm.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hcmus_csc13009.nowwakealarm.R;

public class PlacePickerActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY_POSITION = "POSITION";
    public static final String EXTRA_REPLY_ADDRESS = "ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        prepare();

    }

    void prepare() {
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment == null) {
            Toast.makeText(this, "Can't load Map", Toast.LENGTH_SHORT).show();
            return;
        }
        Button button = (Button) findViewById(R.id.confirmButton);
        TextView addressView = (TextView) findViewById(R.id.locationTextView);
        mapFragment.setTextViewListener(addressView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapFragment.getCurrentSelected() != null) {
                    Intent replyIntent = new Intent();
                    replyIntent.putExtra(EXTRA_REPLY_POSITION, mapFragment.getCurrentSelectedPosition());
                    replyIntent.putExtra(EXTRA_REPLY_ADDRESS, mapFragment.getCurrentSelected());
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
            }
        });

        String position = getIntent().getStringExtra(AddAlarmActivity.EXTRA_POSITION);
        Log.i("@@@ position in picker", position);
        if (position != null)
            mapFragment.setPosition(position);
    }
}