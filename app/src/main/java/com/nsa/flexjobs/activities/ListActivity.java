package com.nsa.flexjobs.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.nsa.flexjobs.Adapters.ListAdapter;
import com.nsa.flexjobs.Extra.Storage;
import com.nsa.flexjobs.Model.StorageFileModel;
import com.nsa.flexjobs.R;

import java.util.ArrayList;
import java.util.List;

import static com.nsa.flexjobs.activities.TaskActivity.firebaseUser;


public class ListActivity extends AppCompatActivity {

    List<StorageFileModel> fileModelList;
    RecyclerView recyclerView;
    ListAdapter listAdapter;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        textView=findViewById(R.id.textView);
        recyclerView=findViewById(R.id.recylerView);
        fileModelList=new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        listAdapter=new ListAdapter(ListActivity.this,fileModelList);
        recyclerView.setAdapter(listAdapter);
        getList();
    }

    private static String TAG="listactivity";
    private void getList() {

        new Storage().getStorageReference().child(firebaseUser.getUid()).listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        if(listResult.getItems().size()==0){
                            textView.setText("No Files Were Uploaded");
                            return;
                        }
                        for (StorageReference item : listResult.getItems()) {

                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.e(TAG,item.getName());
                                    StorageFileModel model=new StorageFileModel(item.getName(), String.valueOf(uri));
                                    fileModelList.add(model);
                                    listAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        textView.setText("Failed try later!");
                    }
                });
    }
}