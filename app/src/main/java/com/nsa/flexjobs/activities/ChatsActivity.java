package com.nsa.flexjobs.activities;

import static com.nsa.flexjobs.Message.MessageAdapter.convertTime;
import static com.nsa.flexjobs.activities.TaskActivity.firebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nsa.flexjobs.Adapters.ChatsAdapter;
import com.nsa.flexjobs.Extra.FireBase;
import com.nsa.flexjobs.Extra.Progress;
import com.nsa.flexjobs.Message.Chat;
import com.nsa.flexjobs.Model.UserModel;
import com.nsa.flexjobs.R;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    RecyclerView chatsRV;
   public static ChatsAdapter chatsAdapter;
   Progress progress;

   public interface click{
       void userclick(int userModel);
   }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        chatsRV=findViewById(R.id.chatsRV);

        click click=new click() {
            @Override
            public void userclick(int position) {
                userClicked(userList.get(position));
            }
        };

        chatsAdapter=new ChatsAdapter(this,userList,click);

        chatsRV.setAdapter(chatsAdapter);
        progress=new Progress(this);
        progress.setMessage("cheking data...");
        progress.show();
        checkData();
    }

    private void userClicked(UserModel userModel) {
       TaskActivity.currentUserModel=userModel;
       TaskActivity.currentChatAddress=userModel.getAddress();
       startActivity(new Intent(this,MessageActivity.class));
    }

    private void checkData() {
        new FireBase().getReferenceUsers().child(firebaseUser.getUid())
                .child("chat_with").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    getChatWith();
                }else{
                    Toast.makeText(getApplicationContext(), "You Don't have any chats", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: "+error.getMessage() );
                progress.dismiss();
                finish();
            }
        });
    }



    private void getChatWith() {

        new FireBase().getReferenceUsers().child(firebaseUser.getUid())
                .child("chat_with")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        progress.dismiss();
                        addChatListener(snapshot.getKey(),snapshot.getValue().toString());
                        getUserData(snapshot.getKey(),snapshot.getValue().toString(),"");

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

                       progress.dismiss();
                       finish();
                        Log.e(TAG, "onCancelled: "+error.getMessage() );
                    }
                });
    }

    private void addChatListener(String key, String chatAddress){
       new FireBase().getReferenceChats().child(chatAddress)
       .child("chat").addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               Chat chat = snapshot.getValue(Chat.class);

               if(chat==null){
                   return;
               }



               for(int i=0;i<userList.size();i++){

                   UserModel model=userList.get(i);
                  if((chat.getSender().equals(model.getId()) && !chat.getReceiver().equals(model.getId()))
                  || (!chat.getSender().equals(model.getId()) && chat.getReceiver().equals(model.getId()))){

                      userList.remove(i);
                      chatsAdapter.notifyItemRemoved(i);
                      model.setEmail(chat.getMessage()+"   "+convertTime(chat.getTime()));
                      userList.add(0,model);
                      chatsAdapter.notifyItemInserted(0);

                      break;

                  }



               }


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

               Log.e(TAG, "onCancelled:4 "+error.getMessage() );
           }
       });
    }

    List<UserModel> userList=new ArrayList<>();
    private void getUserData(String key, String value, String message) {
        new FireBase().getReferenceUsers().child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            UserModel userModel=snapshot.getValue(UserModel.class);
                            userModel.setAddress(value);
                            userModel.setEmail(message);
                           getLastChat(userModel,value);

                        }else{

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled single: "+error);
                        finish();
                    }
                });
    }

    private void getLastChat(UserModel userModel, String value) {

        new FireBase().getReferenceChats().child(value)
                .child("chat")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Chat chat=new Chat();
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                chat=dataSnapshot.getValue(Chat.class);
                            }
                            if(chat!=null){
                            userModel.setEmail(chat.getMessage()+"   "+convertTime(chat.getTime()));
                            }
                            userList.add(0,userModel);
                            chatsAdapter.notifyItemInserted(0);


                        }else{
                            userList.add(0,userModel);
                            chatsAdapter.notifyItemInserted(0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userList.add(0,userModel);
                        chatsAdapter.notifyItemInserted(0);
                        Log.e(TAG, "onCancelled:5 " );
                    }
                });
    }

    String TAG="chatsActivity";

    public void back(View view) {
        finish();
    }
}