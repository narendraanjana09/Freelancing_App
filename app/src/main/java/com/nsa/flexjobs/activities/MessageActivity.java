package com.nsa.flexjobs.activities;


import static com.nsa.flexjobs.activities.TaskActivity.currentChatAddress;
import static com.nsa.flexjobs.activities.TaskActivity.currentUserModel;
import static com.nsa.flexjobs.activities.TaskActivity.firebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nsa.flexjobs.Extra.FireBase;
import com.nsa.flexjobs.Message.Chat;
import com.nsa.flexjobs.Message.MessageAdapter;
import com.nsa.flexjobs.Message.SendNotification;
import com.nsa.flexjobs.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {



    Toolbar toolbar;
    RecyclerView messageRV;
    TextView infoTV,seenTV;
    EditText messageED;
    CircleImageView imageView;
    MessageAdapter messageAdapter;
    List<Chat> chatList=new ArrayList<>();
    Context context;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        if(currentUserModel==null)
        {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            back(null);
            return;
        }
        context=MessageActivity.this;
        toolbar=findViewById(R.id.toolbar);
        imageView=findViewById(R.id.profileView);
        infoTV=findViewById(R.id.infoTV);
        seenTV=findViewById(R.id.txt_seen);
        Picasso.get().load(currentUserModel.getImageLink()).into(imageView);
        toolbar.setTitle(currentUserModel.getName());
        messageRV=findViewById(R.id.messagesRV);
        messageED=findViewById(R.id.text_send);
        messageAdapter=new MessageAdapter(this,chatList,currentUserModel.getImageLink());
        messageRV.setHasFixedSize(true);
         layoutManager =
                new LinearLayoutManager(getApplicationContext()
                ,RecyclerView.VERTICAL,true);

        messageRV.setLayoutManager(layoutManager);
        messageRV.setAdapter(messageAdapter);
        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
               // checkSeen();
                if(chatList.size()>0){
                    infoTV.setVisibility(View.GONE);
                }else{
                    infoTV.setVisibility(View.VISIBLE);
                    seenTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if(chatList.size()>0){
                    infoTV.setVisibility(View.GONE);
                    seenTV.setVisibility(View.VISIBLE);
                }else{
                    infoTV.setVisibility(View.VISIBLE);
                    seenTV.setVisibility(View.GONE);
                }
            }
        });
        messageRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

            }
        });

        getMessages();
        checkStatus();
        setSeenMessage(currentChatAddress);
    }



    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    private void status(String status){

      new FireBase().getReferenceChats().child(currentChatAddress)
              .child(firebaseUser.getUid())
              .setValue(status);





    }
    private void setSeenMessage(String currentChatAddress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new FireBase().getReferenceChats().child(currentChatAddress).child("chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Chat chat = snapshot.getValue(Chat.class);
                                if (chat.getReceiver().equals(firebaseUser.getUid())){
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("isseen", true);
                                    snapshot.getRef().updateChildren(hashMap);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).start();

    }
    boolean isOnline=false;
    private void checkStatus() {
        new FireBase().getReferenceChats().child(currentChatAddress).child(currentUserModel.getId())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String status= (String) snapshot.getValue();
                    if(status.equals("online")){
                        isOnline=true;
                        if(chatList.size()==0){
                            return;
                        }
                  if(chatList.get(0).getSender().equals(firebaseUser.getUid())){
                    seenTV.setText("Seen");
                    seenTV.setVisibility(View.VISIBLE);
                }
                        imageView.setBorderColor(getResources().getColor(R.color.chocalte));
                    }else{
                        isOnline=false;
                        imageView.setBorderColor(getResources().getColor(R.color.grey));
                    }


                }else{
                    isOnline=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    

    private void getMessages() {
        new FireBase().getReferenceChats().child(currentChatAddress).child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if(snapshot.getValue(Chat.class)==null){
                    return;
                }
                Chat chat = snapshot.getValue(Chat.class);
                if(chat==null){
                    return;
                }
                chatList.add(0,chat);

                int first=layoutManager.findFirstCompletelyVisibleItemPosition();
                int last = layoutManager.findLastCompletelyVisibleItemPosition();
                if(first<2){
                    messageRV.scrollToPosition(0);
                }
                if(chat.getSender().equals(firebaseUser.getUid())){
                    if(isOnline){
                    seenTV.setText("Seen");
                    seenTV.setVisibility(View.VISIBLE);
                }else{
                        if(chat.isIsseen()){
                            seenTV.setText("Seen");
                            seenTV.setVisibility(View.VISIBLE);
                        }else{
                        seenTV.setText("Delivered");
                        seenTV.setVisibility(View.VISIBLE);
                    }}
                }else{
                    seenTV.setVisibility(View.GONE);
                }


                messageAdapter.notifyItemInserted(0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void back(View view) {
        finish();
    }

    public void sendMessage(View view) {
        String message=messageED.getText().toString();
        message=message.trim();
        if(message.isEmpty()){
            return;
        }
        String time = String.valueOf(System.currentTimeMillis());
        String date=new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        Chat chat=new Chat(firebaseUser.getUid(),currentUserModel.getId(),message,date,time,false);
        new FireBase().getReferenceChats().child(currentChatAddress)
                .child("chat")
                .child(time).setValue(chat);
        if(!isOnline){
            SendNotification.send(currentUserModel.getMessage_token(),
                    firebaseUser.getDisplayName()+" : "+chat.getMessage()
            ,this);
        }

        messageED.setText("");

    }

   }