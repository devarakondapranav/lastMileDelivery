package com.nihanth.maproutebetweenmarkers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journaldev.maproutebetweenmarkers.R;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent signin = getIntent();
        //final String driverid[1];
        final String driverid = signin.getStringExtra("driverid");
        //final String custDet =





        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference().child("routes");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                int count=0;
                ArrayList<String> coordinates = new ArrayList<>();
                ArrayList<String> custDetails = new ArrayList<>();
                ArrayList<String> custIds = new ArrayList<>();
                for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                    Log.d("heyyy",item_snapshot.child("status").getValue().toString());
                    //Log.d("heyyy",item_snapshot.child("status").getValue().toString());
                    Log.d("booll",driverid);
                    Log.d("bool",""+item_snapshot.child("status").getValue().toString().equals(driverid));
                    if(item_snapshot.child("status").getValue().toString().equals("Ongoing") || item_snapshot.child("status").getValue().toString().equals(driverid))
                    {
                        String temp = item_snapshot.child("coordinates").getValue().toString();
                        String cust="";
                        String id="";
                        if (item_snapshot.child("customer_details").getValue()==null) {
                            cust="";
                        }
                        else{
                            cust = item_snapshot.child("customer_details").getValue().toString();
                        }
                        if (item_snapshot.child("orderids").getValue()==null){
                            id="";
                        }
                        else{
                            id=item_snapshot.child("orderids").getValue().toString();
                        }
                        for(String yo: temp.split("->"))
                        {
                            coordinates.add(yo);
                        }
                        for(String yol: cust.split("->"))
                        {
                            custDetails.add(yol);
                        }
                        for(String yoll: id.split("->"))
                        {
                            custIds.add(yoll);
                        }
                        Log.d("hey", coordinates.toString());
                        item_snapshot.child("status").getRef().setValue(driverid);


                        found = true;
                        count=1;
                        break;
                    }





                    //Log.d("item id ",item_snapshot.child("coordinates").getValue().toString());

                }
                if (found==true){
                    Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                    intent.putStringArrayListExtra("coord",coordinates);
                    intent.putStringArrayListExtra("cust",custDetails);
                    intent.putStringArrayListExtra("ids",custIds);
                    intent.putExtra("did",driverid);
                    startActivity(intent);
                    finish();

                }
                if(found == false)
                {
                    Log.d("notfound", "no routes to assign as of now");
                    Toast.makeText(MainActivity.this,"No empty vehicles",Toast.LENGTH_SHORT).show();
                }

                //Iterable<DataSnapshot> vals=dataSnapshot.getChildren();
                //Iterator iterator = vals.iterator();
                //while (iterator.hasNext()){
                //    Log.d("hello",iterator.next().getVa)//240724
                //}

                //Log.d("great",""+dataSnapshot);

                //Dingding dingding = dataSnapshot.getValue(Dingding.class);
                //Log.d("hello",dingding.getName());
                Log.d("jai balayya ", "hello");
                /*
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();

                myRef.child("routes").child("26Feb2019").child("0").child("status").setValue("Completed");
                String x = myRef.child("routes").child("26Feb2019").child("0").child("status").toString();
                Log.d("helll",x);
                //myRef.child(var).child("long").setValue(longitude);
                */


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("gone","gone");
            }
        });

    }
}
