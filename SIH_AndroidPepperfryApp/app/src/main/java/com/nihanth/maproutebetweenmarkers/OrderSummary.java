package com.nihanth.maproutebetweenmarkers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journaldev.maproutebetweenmarkers.R;

import java.util.ArrayList;

public class OrderSummary extends AppCompatActivity {

    LinearLayout linearLayout;
    ScrollView scrollView;
    Button button;
    String did;
    ArrayList<String> incomplete_orders = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<Integer> integers = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_summary);
        button=findViewById(R.id.complete);
        //incomplete=findViewById(R.id.incomplete);


        Intent intent = getIntent();
        final int size = intent.getExtras().getInt("size",0);
        final String did = intent.getStringExtra("did");
        final ArrayList<String> strings = intent.getStringArrayListExtra("cust");
        final ArrayList<String> string_names = intent.getStringArrayListExtra("cust_name");
        ids = intent.getStringArrayListExtra("ids");

        button.setVisibility(View.VISIBLE);

        final int x[] = new int[1];
        x[0]=size;

        Log.d("uio",size+"");

        linearLayout= findViewById(R.id.linLayout);
        scrollView=findViewById(R.id.scroll);

        for(int i=1;i<=size;i++){
            String id = "ide"+i;
            int resId = 1000+i;
            Log.d("deb","deb");

            LinearLayout linearLayout1= new LinearLayout(this);
            linearLayout1.setOrientation(LinearLayout.VERTICAL);
            //linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //linearLayout1.setBackgroundColor(getResources().getColor(R.color.orange));


            final CheckBox checkBox =new CheckBox(this);
            checkBox.setId(resId);
            checkBox.setText(string_names.get(i)+"\n"+strings.get(i)); //initally i
            checkBox.setTextSize(25);
            checkBox.setPadding(10,10,10,10);

            View v = new View(this);
            v.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    5
            ));
            v.setPadding(10,10,10,10);
            v.setBackgroundColor(Color.parseColor("#B3B3B3"));
            /*checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (checkBox.isChecked()){
                        x[0]=x[0]--;
                        if (x[0]==0){
                            button.setVisibility(View.VISIBLE);
                            button.invalidate();
                        }
                    }
                    else {
                        x[0]=x[0]++;
                        button.setVisibility(View.GONE);
                        button.invalidate();
                    }
                }
            });*/
            /*checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()){
                        x[0]=x[0]--;
                        if (x[0]==0){
                            button.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });*/


            //LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
            //        100, 100);
            //checkParams.setMargins(10, 10, 10, 10);
            //checkParams.gravity = Gravity.CENTER;

            //linearLayout.setLayoutParams(checkParams);
            linearLayout1.addView(checkBox);
            linearLayout1.addView(v);
            linearLayout.addView(linearLayout1);
        }

        //Button button = new Button(this);
        //button.setText("Completed All orders");
        //linearLayout.addView(button,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.BOTTOM));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flag=0;
                Log.d("entered","entered");
                for (int j=0;j<size;j++){
                    CheckBox checkBox = findViewById(1000+j+1);
                    if (!checkBox.isChecked()){
                        flag=1;
                        integers.add(j);
                        Log.d("enteredd",""+j);
                        //break;
                    }
                }
                if (flag==0) {

                    Toast.makeText(OrderSummary.this, "COMPLETE", Toast.LENGTH_SHORT).show();


                    final FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference myRef = database.getReference().child("routes");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean found = false;
                            int count = 0;
                            ArrayList<String> coordinates = new ArrayList<>();
                            ArrayList<String> custDetails = new ArrayList<>();
                            for (DataSnapshot item_snapshot : dataSnapshot.getChildren()) {
                                Log.d("heyyyy", item_snapshot.child("status").getValue().toString());
                                //Log.d("heyyy",item_snapshot.child("status").getValue().toString());
                                //Log.d("booll", driverid);
                                //Log.d("booll", "" + item_snapshot.child("status").getValue().toString().equals(driverid));

                                if (item_snapshot.child("status").getValue().toString().equals(did)) {
                                    item_snapshot.child("status").getRef().setValue("Completed");
                                }
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("gone", "gone");
                        }
                    });
                }
                else if (flag == 1){
                    Log.d("fail","fail");
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final FirebaseDatabase database1= FirebaseDatabase.getInstance();

                    final DatabaseReference myRef = database.getReference().child("incomplete");
                    final DatabaseReference myRef1 = database1.getReference().child("routes");

                    Log.d("intsize",integers.toArray().toString());
                    Log.d("intsizee",""+integers.size());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (int i=0;i<integers.size();i++){
                                Log.d("intsizeee","Success");
                                String se = ids.get(integers.get(i));
                                String name = strings.get(integers.get(i)+1);
                                String loc = string_names.get(integers.get(i)+1);//i+1
                                Log.d("helloer",name+loc);
                                myRef.child("orderid").push().setValue(name+" "+loc);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                                if (item_snapshot.child("status").getValue().toString().equals(did)){
                                    item_snapshot.child("status").getRef().setValue("Completed");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


    }
}
