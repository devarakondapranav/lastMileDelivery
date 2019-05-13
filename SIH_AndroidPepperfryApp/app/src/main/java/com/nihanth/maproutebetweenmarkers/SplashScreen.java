package com.nihanth.maproutebetweenmarkers;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.journaldev.maproutebetweenmarkers.R;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_mod);
        final ImageView imageView = findViewById(R.id.imageView);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this,SigninLogin.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashScreen.this,
                                imageView,
                                ViewCompat.getTransitionName(imageView));
                if (Build.VERSION.SDK_INT >= 21){
                    SplashScreen.this.startActivity(mainIntent, options.toBundle());
                    SplashScreen.this.finish();
                }
                else {
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
            }
        }, 1000);
    }
}
