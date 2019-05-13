package com.nihanth.maproutebetweenmarkers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journaldev.maproutebetweenmarkers.R;

import java.util.ArrayList;


public class SigninLogin extends AppCompatActivity{

    Button b;
    EditText uid_y,pass_y;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginsign);
        b= findViewById(R.id.submit);
        uid_y=findViewById(R.id.uid);
        pass_y=findViewById(R.id.pass);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();



        //final String ruid="nihanth";
        //final String rpass="n";


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String x = uid_y.getText().toString();
                final String y = pass_y.getText().toString();
                DatabaseReference myRef = database.getReference().child("users");

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean found = false;
                        String did="";
                        String zd="";
                        //ArrayList<String> password = new ArrayList<>();
                        //ArrayList<String> username = new ArrayList<>();
                        for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                            String xd = item_snapshot.child("password").getValue().toString();
                            String yd = item_snapshot.child("username").getValue().toString();
                            //zd = item_snapshot.child("customer_details").getValue().toString();
                            did = item_snapshot.child("driverid").getValue().toString();
                            Log.d("creds",did+" "+yd);

                            //password.add(xd);
                            //username.add(yd);
                            if (x.equals(yd) && y.equals(xd)) {
                                Log.d("credss",did);

                                found = true;
                                break;
                            }
                        }
                        if(found == false)
                        {
                            Log.d("notfound", "no routes to assign as of now");
                            Toast.makeText(SigninLogin.this,"No corresponding username and password",Toast.LENGTH_SHORT).show();
                        }

                        else if(found==true){

                            Intent maps=new Intent(SigninLogin.this,MainActivity.class);
                            Log.d("credsss",did);
                            maps.putExtra("driverid",did);
                            //maps.putExtra("cust_details",zd);
                            startActivity(maps);
                            finish();
                            Toast.makeText(SigninLogin.this,"Success",Toast.LENGTH_SHORT).show();
                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("gone","gone");
                    }



                });
            }
        });




    }



    }

