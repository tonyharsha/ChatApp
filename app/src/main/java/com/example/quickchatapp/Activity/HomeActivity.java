package com.example.quickchatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quickchatapp.R;
import com.example.quickchatapp.Adapter.UserAdapter;
import com.example.quickchatapp.ModelClass.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView mainUserRecylerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView imglogout,img_Setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        DatabaseReference reference=database.getReference().child("user");
        usersArrayList=new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Users users=dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imglogout=findViewById(R.id.img_logOut);
        img_Setting=findViewById(R.id.img_Setting);
        mainUserRecylerView=findViewById(R.id.mainUserRecylerView);
        mainUserRecylerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new UserAdapter(HomeActivity.this,usersArrayList);
        mainUserRecylerView.setAdapter(adapter);

        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(HomeActivity.this,R.style.Dialoge);
                dialog.setContentView(R.layout.dialog_layout);
                TextView nobtn,yesbtn;
                nobtn=dialog.findViewById(R.id.no_btn);
                yesbtn=dialog.findViewById(R.id.yes_btn);

                yesbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this,RegistrationActivity.class));
                    }
                });
                nobtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        img_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,SettingActivity.class));
            }
        });



        if(auth.getCurrentUser()==null){
            startActivity(new Intent(HomeActivity.this,RegistrationActivity.class));
        }

    }
}