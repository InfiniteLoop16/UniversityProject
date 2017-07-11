package com.example.android.universityproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LocationWarnningPopUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_warnning_pop_up);
    }

    // Method to close pop-up
    public void okClose(View view){this.finish();}
}
