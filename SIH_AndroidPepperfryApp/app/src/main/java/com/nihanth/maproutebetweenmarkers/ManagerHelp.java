package com.nihanth.maproutebetweenmarkers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toolbar;

import com.journaldev.maproutebetweenmarkers.R;

public class ManagerHelp extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("HELP");
        Intent intent= getIntent();

        //TextView textView = findViewById(R.id.te)
        //TextView textView = findViewById(R.id.te)
        TextView textView = findViewById(R.id.textView2);
        textView.setText("1)Sign in\n" +
                "2)The current order route is been shown on the screen\n" +
                "3)The route incrementally guides using numbers  to the destination with various stops \n" +
                "4)On clicking the first stop, Two  icons will appear  on screen \n" +
                "5)On selecting the first symbol,You will be shown the  directions and will be guided through voice assistance  to the first stop\n" +
                "6)On selecting the second symbol you can see the location,near landmarks in order to identify location.");
    }
}
