package com.nsa.flexjobs.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import com.nsa.flexjobs.Adapters.ApplyOtherModel;
import com.nsa.flexjobs.Extra.FireBase;
import com.nsa.flexjobs.Extra.Progress;
import com.nsa.flexjobs.Extra.Storage;
import com.nsa.flexjobs.Model.ApplicationModel;
import com.nsa.flexjobs.Model.File;
import com.nsa.flexjobs.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nsa.flexjobs.activities.TaskActivity.current_task_model;
import static com.nsa.flexjobs.activities.TaskActivity.firebaseUser;

import org.jetbrains.annotations.NotNull;


public class TaskInfoActivity extends AppCompatActivity {

    TextView taskNameTV,taskValueTV,userNameTV,dateTV,taskInfoTV,fileNameTV;
    ImageView prevImageView;
    EditText detailED,documentED;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        taskNameTV=findViewById(R.id.taskNameTV);
        taskValueTV=findViewById(R.id.taskValueTV);
        userNameTV=findViewById(R.id.userNameTV);
        dateTV=findViewById(R.id.postedDateTV);
        taskInfoTV=findViewById(R.id.taskInfoTV);
        prevImageView=findViewById(R.id.prevImageView);
        detailED=findViewById(R.id.detailED);
        fileNameTV=findViewById(R.id.filesnameTV);
        documentED=findViewById(R.id.documentED);
        Picasso.get().load(current_task_model.getPrev_img_link()).into(prevImageView);
        taskNameTV.setText(current_task_model.getTask_name());
        taskValueTV.setText(getResources().getString(R.string.rupee)+" "+Float.parseFloat(current_task_model.getTask_value()));
        userNameTV.setText(current_task_model.getUser_name());
        dateTV.setText(current_task_model.getPosted_date());
        taskInfoTV.setText(current_task_model.getTask_note()+"\n"+current_task_model.getTotalTestImages());
    }

    public void back(View view) {
        if(STARTED){

            AlertDialog.Builder dialog = new AlertDialog.Builder(TaskInfoActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            dialog.setCancelable(false);
            dialog.setTitle("Upload Project");
            dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    count=filesReference.size()-1;
                    progress=new Progress(TaskInfoActivity.this);
                    progress.setTitle("Connecting...");
                    progress.show();
                   deleteFiles();

                }
            });
            dialog.show();
        }else{
        finish();
        }
    }

    int count=0;
    private void deleteFiles() {
        filesReference.get(count).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                   count--;
                   if(count<0){
                       progress.dismiss();
                       finish();

                   }else{
                   deleteFiles();

               }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
                Toast.makeText(TaskInfoActivity.this, "error", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                finish();
            }
        });
    }
    String TAG="taskInfor";

    public void saveStart(View view) {
        String detail=detailED.getText().toString();

        if(detail.isEmpty()){
            Toast.makeText(this, "Please Describe Yourself", Toast.LENGTH_SHORT).show();
            return;
        }
        String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        ApplicationModel applicationModel=new ApplicationModel(current_task_model.getTask_type(),current_task_model.getTask_id(),current_task_model.getTask_name()
                , date,current_task_model.getPosted_date(),"0",current_task_model.getTask_value(),"0",current_task_model.getTask_note());

        ApplyOtherModel otherModel=new ApplyOtherModel(firebaseUser.getDisplayName(),date,detail,firebaseUser.getUid(),filesList);


        Progress progress=new Progress(this);
        progress.setTitle("Uploading...");
        progress.setMessage("Uploading...");
        progress.show();

        uploadOtherModel(otherModel, applicationModel, progress);
    }


    private void uploadOtherModel(ApplyOtherModel otherModel, ApplicationModel applicationModel, Progress progress) {
        new FireBase().getReferenceTask().child(current_task_model.getTask_id()).child("AppliedUsers")
                .child(firebaseUser.getUid()).setValue(otherModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {

                if(task.isSuccessful()){
                    new FireBase().getReferenceUsers().child(firebaseUser.getUid()).child("applicationModelList").child(current_task_model.getTask_id()).setValue(applicationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(TaskInfoActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                            Intent i=new Intent(TaskInfoActivity.this, TaskActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }
                    });


                }else{
                    progress.dismiss();
                    Toast.makeText(TaskInfoActivity.this, "files not uploaded!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addAttachment(View view) {
        openFile();
    }
    private static final int PICK_FILE = 2;
    Progress progress;
    private void openFile() {
        if(permissionGranted()) {
             progress=new Progress(this);
            progress.setTitle("Getting Files");
            progress.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.setType("application/pdf");
//                    intent.setType("image/*");
                    startActivityForResult(getFileChooserIntent(), PICK_FILE);

                }
            }).start();

        }
        else{
            Toast.makeText(this, "Provide Permission To Get Files", Toast.LENGTH_SHORT).show();
            requestPermission();
        }
    }
    private Intent getFileChooserIntent() {
        String[] mimeTypes = {"image/*", "application/pdf"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        return intent;
    }
    private boolean permissionGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }
    Uri pdfUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progress.dismiss();
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_FILE){
                if(data.getData()!=null){

                pdfUri=data.getData();
                   // documentED.setText(file.getFile_name());
                    File file=new File(getFileName(pdfUri),data.getData()+"");
                    if(file.getFile_name().isEmpty()){
                        Toast.makeText(this, "Error With This FIle", Toast.LENGTH_SHORT).show();
                    }else{
                       showAlert(file);
                    }


            }
            }
        }
    }


    private void showAlert(File file) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(TaskInfoActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        dialog.setCancelable(false);
        dialog.setTitle("Should We Upload This File");
        dialog.setPositiveButton("okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadFile(file);
            }
        });
        dialog.setNegativeButton("cencel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        dialog.show();

    }
    boolean STARTED=false;
    List<File> filesList=new ArrayList<>();
    List<StorageReference> filesReference=new ArrayList<>();

    private void uploadFile(File file) {
        StorageReference storageReference = new Storage().getGetUsersRefernce()
                .child(firebaseUser.getUid())
                .child(current_task_model.getTask_id())
                .child(file.getFile_name());

        Progress progress=new Progress(this);
        progress.setTitle("Uploading...");
        progress.setMessage("Uploading...");
        progress.show();
        storageReference.putFile(Uri.parse(file.getLink())).continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // After uploading is done it progress
                    // dialog box will be dismissed
                    Uri uri=task.getResult();
                    file.setLink(uri.toString());
                    filesReference.add(storageReference);
                    fileNameTV.append(file.getFile_name()+"\n");
                    filesList.add(file);
                    STARTED=true;
                    Toast.makeText(TaskInfoActivity.this, "Uploaded Success", Toast.LENGTH_SHORT).show();


                } else {

                    Toast.makeText(TaskInfoActivity.this, "Uploaded Failed", Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
            }
        });
    }

    private String getFileName(Uri uri) {
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.e("FILE", "Display Name: " + displayName);
                return displayName;

//                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
//                // If the size is unknown, the value stored is null. But because an
//                // int can't be null, the behavior is implementation-specific,
//                // and unpredictable. So as
//                // a rule, check if it's null before assigning to an int. This will
//                // happen often: The storage API allows for remote files, whose
//                // size might not be locally known.
//                String size = null;
//                if (!cursor.isNull(sizeIndex)) {
//                    // Technically the column stores an int, but cursor.getString()
//                    // will do the conversion automatically.
//                    size = cursor.getString(sizeIndex);
//                } else {
//                    size = "Unknown";
//                }
//                Log.e("FILE", "Size: " + size);
            }
        } finally {
            cursor.close();
        }
        return "";
    }

    public void clearAllFiles(View view) {
    }
}