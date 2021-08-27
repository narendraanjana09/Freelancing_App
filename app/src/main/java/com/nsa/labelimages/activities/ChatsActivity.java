package com.nsa.labelimages.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.nsa.labelimages.Adapters.ChatsAdapter;
import com.nsa.labelimages.R;

public class ChatsActivity extends AppCompatActivity {

    RecyclerView chatsRV;
   public static ChatsAdapter chatsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        chatsRV=findViewById(R.id.chatsRV);
        chatsAdapter=new ChatsAdapter(this);
        chatsRV.setAdapter(chatsAdapter);

    }

    public void back(View view) {
        finish();
    }
}