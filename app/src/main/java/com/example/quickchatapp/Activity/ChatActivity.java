package com.example.quickchatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickchatapp.Adapter.MessagesAdapter;
import com.example.quickchatapp.ModelClass.Messages;
import com.example.quickchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReceiverImage,ReceiverName,ReceiverUid,SenderUid;
    CircleImageView profile_image;
    TextView receiverName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static  String sImage;
    public static String rImage;
    EditText editMessage;
    CardView sendBtn;
    RecyclerView messageAdaptor;

    MessagesAdapter adapter;


    ArrayList<Messages> messagesArrayList;
    String SenderRoom,ReceiverRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        ReceiverName=getIntent().getStringExtra("name");
        ReceiverImage=getIntent().getStringExtra("ReceiverImage");
        ReceiverUid=getIntent().getStringExtra("uid");

        messagesArrayList=new ArrayList<>();
        profile_image=findViewById(R.id.profile_image);
        editMessage=findViewById(R.id.editMessage);
        sendBtn=findViewById(R.id.sendBtn);
        messageAdaptor=findViewById(R.id.messageAdaptor);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        // messages move from bottom to top latest stays bottem older on stays on top
        linearLayoutManager.setStackFromEnd(true);
        messageAdaptor.setLayoutManager(linearLayoutManager);
        adapter=new MessagesAdapter(ChatActivity.this,messagesArrayList);
        messageAdaptor.setAdapter(adapter);


        receiverName=findViewById(R.id.receiverName);

        Picasso.get().load(ReceiverImage).into(profile_image);
        receiverName.setText(" "+ReceiverName);

        SenderUid=firebaseAuth.getUid();


        SenderRoom=SenderUid+ReceiverUid;
        ReceiverRoom=ReceiverUid+SenderUid;

        DatabaseReference reference=database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference Chatreference=database.getReference().child("chats").child(SenderRoom).child("messages");

        Chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Messages messages=dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               sImage=snapshot.child("imageUri").getValue().toString();
               rImage=ReceiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=editMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Please enter valid Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                editMessage.setText("");
                Date date=new Date();

                Messages messages=new Messages(message,SenderUid,date.getTime());

                database=FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(SenderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats")
                                .child(ReceiverRoom)
                                .child("messages")
                                .push()
                                .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        });

    }
}