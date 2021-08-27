package com.nsa.labelimages.service;

import static com.nsa.labelimages.Fragments.ExploreTaskFragment.taskAdaper;
import static com.nsa.labelimages.Fragments.MyTasksFragment.myTaskAdaper;
import static com.nsa.labelimages.activities.TaskActivity.TOTAL_BYTES;
import static com.nsa.labelimages.activities.TaskActivity.TOTAL_BYTES_TRANSFERRED;

import static com.nsa.labelimages.activities.TaskActivity.imagesUriList;
import static com.nsa.labelimages.activities.TaskActivity.myTasksModelList;
import static com.nsa.labelimages.activities.TaskActivity.size;
import static com.nsa.labelimages.activities.TaskActivity.taskModelList;
import static com.nsa.labelimages.activities.TaskActivity.testImagesUriList;
import static com.nsa.labelimages.activities.TaskActivity.uploadComplete;
import static com.nsa.labelimages.activities.TaskActivity.uploadCount;
import static com.nsa.labelimages.activities.TaskActivity.uploadTaskModel;
import static com.nsa.labelimages.service.App.CHANNEL_ID;
import static com.nsa.labelimages.service.App.manager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nsa.labelimages.Extra.FireBase;
import com.nsa.labelimages.Extra.Storage;
import com.nsa.labelimages.Model.TaskModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.activities.TaskActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class UploadService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }
    public  NotificationCompat.Builder notification;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, TaskActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        notification  = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Uploading Task")
                .setContentText(input)
                .setSilent(true)
                .setSmallIcon(R.drawable.google_icon)
                .setContentIntent(pendingIntent);



        startForeground(1, notification.build());

            uploadPrevImage(uploadTaskModel);


        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }
    private void uploadPrevImage(TaskModel taskModel) {


          totalImages=1+imagesUriList.size()+testImagesUriList.size();
          currentImage =1;

        new Thread(new Runnable() {
            @Override
            public void run() {
                StorageReference ref
                        = new Storage().getGetUsersRefernce()
                        .child(
                                "PreviewImages"+"/"+System.currentTimeMillis()+".jpg");

                UploadTask uploadTask = ref.putFile(Uri.parse(taskModel.getPrev_img_link()));

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        size=taskSnapshot.getTotalByteCount();
                        double progre=TOTAL_BYTES_TRANSFERRED;
                        TOTAL_BYTES_TRANSFERRED+=taskSnapshot.getBytesTransferred();
                        notification.setProgress((int)TOTAL_BYTES,(int)TOTAL_BYTES_TRANSFERRED,false);
                        notification.setContentText(currentImage +"/"+totalImages);
                        manager.notify(1,notification.build());

                        if(uploadComplete!=null){
                            uploadComplete.onProgress(TOTAL_BYTES_TRANSFERRED,TOTAL_BYTES);
                        }
                        TOTAL_BYTES_TRANSFERRED=progre;

                    }
                });
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {

                            Log.e(TAG,task.getException().getMessage());
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {



                        if (task.isSuccessful()) {


                            Uri downloadUri = task.getResult();
                            taskModel.setPrev_img_link(downloadUri.toString());
                            uploadCount=0;
                            TOTAL_BYTES_TRANSFERRED+= size;

                            uploadTestImages(taskModel);

                        } else {

                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                            // Handle failures
                            // ...
                        }
                    }
                });


            }
        }).start();


    }

    private void uploadTestImages(TaskModel model) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(model != null) {

                    // Code for showing progressDialog while uploading


                    currentImage++;
                    Uri imageUri=testImagesUriList.get(uploadCount);
                    File file=new File(String.valueOf(imageUri));


                    // Defining the child of storageReference
                    StorageReference ref
                            = new Storage().getTestImagesReference().child(model.getTask_id())
                            .child("/"+file.getName());

                    UploadTask uploadTask = ref.putFile(imageUri);
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                            size=taskSnapshot.getTotalByteCount();
                            double progre=TOTAL_BYTES_TRANSFERRED;
                            TOTAL_BYTES_TRANSFERRED+=taskSnapshot.getBytesTransferred();
                            notification.setProgress((int)TOTAL_BYTES,(int)TOTAL_BYTES_TRANSFERRED,false);
                            notification.setContentText(currentImage +"/"+totalImages);
                            manager.notify(1,notification.build());

                            if(uploadComplete!=null){
                                uploadComplete.onProgress(TOTAL_BYTES_TRANSFERRED,TOTAL_BYTES);
                            }
                            TOTAL_BYTES_TRANSFERRED=progre;

                        }
                    });
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {

                                Log.e(TAG,task.getException().getMessage());

                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {



                            if (task.isSuccessful()) {

                                TOTAL_BYTES_TRANSFERRED+=size;
                                Uri downloadUri = task.getResult();
                                if(testImagesUriList.size()==(uploadCount+1)){
                                    uploadCount=0;
                                    uploadImages(model);


                                }else{
                                    uploadCount++;

                                    uploadTestImages(model);
                                }

                            } else {


                                // Handle failures
                                // ...
                            }
                        }
                    });


                }
            }
        }).start();

    }
    int totalImages=0;
    int currentImage =0;



    private void uploadImages(TaskModel model)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (model != null) {

                    // Code for showing progressDialog while uploading

                    currentImage++;
                    Uri imageUri=imagesUriList.get(uploadCount);
                    File file=new File(String.valueOf(imageUri));


                    // Defining the child of storageReference
                    StorageReference ref
                            = new Storage().getTaskImagesReference().child(model.getTask_id())
                            .child("/"+file.getName());

                    UploadTask uploadTask = ref.putFile(imageUri);
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                            size=taskSnapshot.getTotalByteCount();
                            double progre=TOTAL_BYTES_TRANSFERRED;
                            TOTAL_BYTES_TRANSFERRED+=taskSnapshot.getBytesTransferred();
                            notification.setProgress((int)TOTAL_BYTES,(int)TOTAL_BYTES_TRANSFERRED,false);
                            notification.setContentText(currentImage +"/"+totalImages);
                            manager.notify(1,notification.build());
                            if(uploadComplete!=null){
                                uploadComplete.onProgress(TOTAL_BYTES_TRANSFERRED,TOTAL_BYTES);
                            }
                            TOTAL_BYTES_TRANSFERRED=progre;

                        }
                    });
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {

                                Log.e(TAG,task.getException().getMessage());

                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {



                            if (task.isSuccessful()) {

                                TOTAL_BYTES_TRANSFERRED+=size;

                                Uri downloadUri = task.getResult();
                                if(imagesUriList.size()==(uploadCount+1)){

                                    uploadTaskModel(model);
                                    uploadCount=0;

                                }else{
                                    uploadCount++;

                                    uploadImages(model);
                                }

                            } else {


                                // Handle failures
                                // ...
                            }
                        }
                    });


                }
            }
        }).start();

    }

    private void uploadTaskModel(TaskModel model) {
        DatabaseReference reference=new FireBase().getReferenceTask();
        taskModelList.add(model);
        taskAdaper.notifyItemInserted(taskModelList.size()-1);
        myTasksModelList.add(model);
        if(myTaskAdaper!=null){
            myTaskAdaper.notifyItemInserted(myTasksModelList.size()-1);
        }
        reference.child(model.getTask_id()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {

                if(task.isSuccessful()){
                    uploadComplete.onComplete();
                    stopSelf();
                }else{

                }

            }
        });

    }


    private String TAG="Background";
    boolean destroyed=false;
    @Override
    public void onDestroy() {
        Log.e(TAG,"onDestroy");
        destroyed=true;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
