package com.nsa.labelimages.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.nsa.labelimages.Extra.Progress;
import com.nsa.labelimages.Extra.Storage;
import com.nsa.labelimages.Model.RealImagesModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.testeditor.TestAnnotateActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.nsa.labelimages.activities.TaskActivity.current_task_model;
import static com.nsa.labelimages.activities.TaskActivity.savedText;

public class ApplyActivity extends AppCompatActivity {

    Toolbar toolbar;
    String text="";
    TextView testInfo;
    Progress progress;
    StorageReference testImagesRefernce;
    ImageView prevImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        toolbar=findViewById(R.id.toolbar);
        testInfo=findViewById(R.id.testInfo);
        prevImageView=findViewById(R.id.prevImageView);

        Picasso.get().load(current_task_model.getPrev_img_link()).into(prevImageView);

        progress=new Progress(ApplyActivity.this);
        testImagesRefernce=new Storage().getTestImagesReference().child(current_task_model.getTask_id());


        toolbar.setTitle(current_task_model.getTask_name());
        text+="Task Info:-"+current_task_model.getTask_note()+"\nPosted Date:-"+current_task_model.getPosted_date()+
        "\nUserName:-"+current_task_model.getUser_name();
        testInfo.setText(text);

        if(!current_task_model.getTask_type().equals("Annotation")){

        }
    }

    public void backToTask(View view) {
        finish();
    }
    public static List<RealImagesModel> realImagesList;
    private String TAG="Apply Activity";
    int count=0;
    private void getImages() {
       count=0;
        realImagesList=new ArrayList<>();
        testImagesRefernce.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        if(listResult.getItems().size()==0){
                            Toast.makeText(ApplyActivity.this, "no images found", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                            return;
                        }
                        for (StorageReference item : listResult.getItems()){
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.e(TAG,item.getName());
                                    savedText.remove(item.getName());
                                    RealImagesModel model=new RealImagesModel(item.getName(),String.valueOf(uri),null);
                                    realImagesList.add(0,model);
                                    count++;
                                    if(count==listResult.getItems().size()){
                                        progress.dismiss();
                                        start();
                                    }
                                }
                            });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();

                    }
                });
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"resume");
        if(realImagesList!=null){
            for(RealImagesModel model:realImagesList){
                savedText.remove(model.getName());
            }
        }
    }
    public void start() {
        Intent intent=new Intent(this, TestAnnotateActivity.class);
        intent.putExtra("tag","test");
        intent.putExtra("task_name",current_task_model.getTask_name());
        startActivity(intent);
    }
    public void startTest(View view) {
        progress.setTitle("Images");
        progress.setMessage("downloading test images.");
        progress.show();
        getImages();
    }
}