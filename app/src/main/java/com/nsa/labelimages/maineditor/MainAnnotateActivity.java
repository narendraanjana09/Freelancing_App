package com.nsa.labelimages.maineditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nsa.labelimages.Extra.FireBase;
import com.nsa.labelimages.Extra.Progress;

import com.nsa.labelimages.Extra.Storage;
import com.nsa.labelimages.Interfaces.ImageDownloadListener;
import com.nsa.labelimages.Model.FormatModel;
import com.nsa.labelimages.Model.ImageNameModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.testeditor.ZoomLayout;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;
import static android.view.View.GONE;

import static com.nsa.labelimages.activities.TaskActivity.currentApplicationModel;

import static com.nsa.labelimages.activities.TaskActivity.firebaseUser;

public class MainAnnotateActivity extends AppCompatActivity {

    public  static int IMAGE_HEIGHT;
    public  static int IMAGE_WIDTH;
    public static View added_view=null;
    public static ImageView imgDrag=null;
    public static FormatModel currentModel;
    public List<FormatModel> TEXT_LIST=new ArrayList<>();
    public List<Bitmap> bitmap_list=new ArrayList<>();
    TextView countTV,infoTV,topTV,bottomTV;
    public static TextView hwTV,xyTV;
    ImageView imageView,editImageView;
    LinearLayout text1;
    ExtendedFloatingActionButton editBTN,uploadBTN,undoBTN;
    Toolbar toolbar;
    Progress progress;

    RelativeLayout buttonLayout,mainLayout,bottomLayout,doneLayout;
    ZoomLayout zoomLayout;
    private boolean gotImageHW=false;
    MainLabelView labelView;
    public Bitmap EDITED_BITMAP;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotate);
        progress=new Progress(this);
        progress.setTitle("Getting Images");
        progress.setMessage("loading...");

        //testeditor views
        editImageView=findViewById(R.id.editImageView);
        buttonLayout=findViewById(R.id.buttonLayout);
        mainLayout=findViewById(R.id.mainLayout);
        zoomLayout=findViewById(R.id.zoomLayout);
        bottomLayout=findViewById(R.id.bottomLayout);
        progressBar=findViewById(R.id.progress_horizontal);
        progressBar.setVisibility(GONE);
        doneLayout=findViewById(R.id.doneLayout);
        hwTV=findViewById(R.id.HWTV);
        xyTV=findViewById(R.id.XYTV);
        topTV=findViewById(R.id.topTV);
        bottomTV=findViewById(R.id.bottomTV);
        text1=findViewById(R.id.text1);
        undoBTN=findViewById(R.id.undo);
        undoBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearAllViews();
                return true;
            }
        });
        labelView=new MainLabelView(this,mainLayout);




        countTV=findViewById(R.id.countTV);
        infoTV=findViewById(R.id.textView);
        infoTV.setMovementMethod(new ScrollingMovementMethod());
        imageView=findViewById(R.id.prevImageView);
        editBTN=findViewById(R.id.editBTN);
        uploadBTN=findViewById(R.id.uploadBTN);
        uploadBTN.setVisibility(View.INVISIBLE);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle(currentApplicationModel.getTask_name());
        getList();
    }
    public Bitmap IMAGE_BITMAP = null;
    private void clearAllViews() {
        TEXT_LIST.clear();
        bitmap_list.clear();
        currentModel = null;
        imageView.setImageBitmap(IMAGE_BITMAP);
        editImageView.setImageBitmap(IMAGE_BITMAP);
    }

    String TAG="MainAnnotateActivity";
    int currentImageIndex=0;

    List<ImageNameModel> linkList=new ArrayList<>();
    List<String> uploadedList=new ArrayList<>();
    private void getList() {
        progress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                new Storage().getTaskImagesReference().child(currentApplicationModel.getTask_id()).child(firebaseUser.getUid()).listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                if(listResult.getItems().size()==0){

                                    getMainList();
                                }else{
                                    int size=listResult.getItems().size();
                                    for(StorageReference item:listResult.getItems()){
                                        uploadedList.add(item.getName());
                                        if(uploadedList.size()==size){
                                            getMainList();
                                        }
                                    }

                                }
                            }
                        });
            }
        }).start();

    }



   int totalSize=0;
    private void getMainList() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                new Storage().getTaskImagesReference().child(currentApplicationModel.getTask_id()).listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                if(listResult.getItems().size()==0){
                                    Toast.makeText(MainAnnotateActivity.this, "no images found", Toast.LENGTH_SHORT).show();

                                    return;
                                }
                                totalSize=listResult.getItems().size();
                                countTV.setText((uploadedList.size()/2)+"/"+totalSize);
                                linkList.clear();

                                checkList(listResult);
                                Log.e(TAG,"nsa1");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.dismiss();
                                Toast.makeText(MainAnnotateActivity.this, "error", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();

                            }
                        });
            }
        }).start();

    }
     int ind=0;
    private void checkList(ListResult listResult) {
        StorageReference item=listResult.getItems().get(ind);
        Log.e(TAG,"nsa2");
           if(uploadedList.size()!=0){
          Search  search=new Search() {
                @Override
                public void onComplete(boolean result) {
                    if(!result){
                        getLink(item);
                        Log.e(TAG,"nsa3");
                    }
                    ind++;
                    if(listResult.getItems().size()!=ind){
                        checkList(listResult);
                        Log.e(TAG,"nsa4");
                    }

                }
            };
            search(item.getName(),search);
           }else{
               for(StorageReference item1:listResult.getItems()){
                   getLink(item1);
                   Log.e(TAG,"nsa5");
               }

           }

    }
    public void search(String name, Search search){
        for(String str:uploadedList){
            if(str.equals(name)){
                search.onComplete(true);
                return;
            }
        }
        search.onComplete(false);
    }

    private void getLink(StorageReference item) {
       new Thread(new Runnable() {
           @Override
           public void run() {
               item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       linkList.add(new ImageNameModel(item.getName(),uri));
                       Log.e(TAG, "onSuccess: "+linkList.size()+"  "+linkList.get(linkList.size()-1).getName() );
                       if(linkList.size()==1){
                           progress.dismiss();
                           newImage=true;
                           currentImageIndex=0;
                           showImage(currentImageIndex);

                       }
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull @NotNull Exception e) {
                       progress.dismiss();
                       Toast.makeText(MainAnnotateActivity.this, "error", Toast.LENGTH_SHORT).show();
                       e.printStackTrace();

                   }
               });
           }
       }).start();
    }

    private void showImage(int index) {

        ImageDownloadListener downloadListener=new ImageDownloadListener() {
            @Override
            public void onImageDownloaded(Bitmap bitmap) {
                Log.e(TAG, "onImageDownloaded: index "+index+" link = "+linkList.get(index).getLink());
                setBitmap(bitmap);
            }

            @Override
            public void onImageDownloadError() {
                Toast.makeText(MainAnnotateActivity.this, "Error Downloading Image\nTry Again Later.", Toast.LENGTH_SHORT).show();
            }
        };
        DownloadImage downloadImage=new DownloadImage(downloadListener);
        downloadImage.execute(linkList.get(index).getLink());


    }

    private void setBitmap(Bitmap imageBitmap) {
        int h=imageBitmap.getHeight();
        int w=imageBitmap.getWidth();


        IMAGE_BITMAP=imageBitmap;
        Log.e(TAG,"h = "+h+" w = "+w);

        float aspect_ratio = (float)w / (float)h ;
        Log.e(TAG,"aspect_ratio = "+aspect_ratio);
        RelativeLayout.LayoutParams layoutParams;
        if(aspect_ratio>1){
            layoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        }else{
            layoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        }
        layoutParams.addRule(RelativeLayout.BELOW,text1.getId());

        imageView.setLayoutParams(layoutParams);
        editImageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(imageBitmap);
        editImageView.setImageBitmap(imageBitmap);




    }

    public void back(View view) {
        finish();
    }

    public void edit(View view) {
        Toast.makeText(this, ""+linkList.size(), Toast.LENGTH_SHORT).show();
       setEditVisible();
    }
    boolean newImage=false;
    public void setEditVisible(){
        zoomLayout.setVisibility(VISIBLE);
        bottomLayout.setVisibility(VISIBLE);

        imageView.setVisibility(GONE);
        infoTV.setVisibility(GONE);
        editBTN.setVisibility(GONE);
        uploadBTN.setVisibility(GONE);


        if(newImage){
            newImage=false;
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, (millisUntilFinished / 1000) + "");
            }

            @Override
            public void onFinish() {
                int topX= (int) topTV.getX();
                int topY= (int) topTV.getY();
                int bottomX= (int) bottomTV.getX();
                int bottomY= (int) bottomTV.getY();

                IMAGE_HEIGHT = bottomY-topY;
                IMAGE_WIDTH =bottomX-topX;
                gotImageHW=true;
                Log.e(TAG, IMAGE_HEIGHT + "," + IMAGE_WIDTH);


            }
        }.start();
        }
    }
    public void setMainVisisble(){
        zoomLayout.setVisibility(GONE);
        hwTV.setText("");
        xyTV.setText("");
        bottomLayout.setVisibility(GONE);

        imageView.setVisibility(VISIBLE);
        infoTV.setVisibility(VISIBLE);
        editBTN.setVisibility(VISIBLE);
        if(TEXT_LIST.size()!=0){
        uploadBTN.setVisibility(VISIBLE);
    }
        setText();

    }

    private void setText() {
        infoTV.setText("");
        for(FormatModel model:TEXT_LIST){
            infoTV.append(model.getLabelName()+" "+model.getX()+" "+model.getY()+
                    " "+model.getWidth()+" "+model.getHeight()+"\n");
        }
    }


    int COLOR;
    String LABEL_NAME;
    public void addLabel(View view) {
        if(!gotImageHW){
            return;
        }
        COLOR = Color.RED;
        final CharSequence[] items = {"Red", "Black", "White"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainAnnotateActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        dialog.setCancelable(false);
        dialog.setTitle("Select Color and Label Name");
        EditText editText = new EditText(MainAnnotateActivity.this);
        editText.setHint("Enter Label Name");
        dialog.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        COLOR = Color.RED;
                        break;
                    case 1:
                        COLOR = Color.BLACK;
                        break;
                    default:
                        COLOR = Color.WHITE;
                }

            }
        });


        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        dialog.setView(editText);
        dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                if (text.isEmpty()) {
                    dialog.dismiss();
                    addLabel(null);
                } else {

                    LABEL_NAME = text.replace(" ", "_");
                    labelView.addText(LABEL_NAME, COLOR);
                    doneLayout.setVisibility(VISIBLE);
                    buttonLayout.setVisibility(GONE);

                }
            }
        });
        dialog.setNegativeButton("cencel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        dialog.show();

    }



    public void upload(View view) {


        uploadTextFile(EDITED_BITMAP,infoTV.getText().toString(),currentImageIndex);
        currentImageIndex++;
        if(currentImageIndex==linkList.size()){
            progress.setTitle("uploading...");
            progress.show();
        }else {
            progressBar.setProgress(0);
            progressBar.setVisibility(VISIBLE);
            showImage(currentImageIndex);
        }

        currentModel=null;
        IMAGE_BITMAP=null;
        TEXT_LIST.clear();
        bitmap_list.clear();
        infoTV.setText("");
        uploadBTN.setVisibility(GONE);
        if(uploadedList.size()!=0){
            countTV.setText(((uploadedList.size()/2)+currentImageIndex)+"/"+totalSize);
        }else{
            countTV.setText((currentImageIndex)+"/"+totalSize);
        }


    }

     private void uploadTextFile(Bitmap EDITED_BITMAP, String s, int currentImageIndex) {

       new Thread(new Runnable() {
           @Override
           public void run() {
               Bitmap bmp = EDITED_BITMAP;
               ByteArrayOutputStream stream = new ByteArrayOutputStream();

               bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
               byte[] image_byte = stream.toByteArray();
               byte[] text_byte=s.getBytes();

               bmp.recycle();
               StorageReference image_ref
                       = new Storage().getTaskImagesReference().child(currentApplicationModel.getTask_id())
                       .child(firebaseUser.getUid())
                       .child(linkList.get(currentImageIndex).getName());
               StorageReference text_ref
                       = new Storage().getTaskImagesReference().child(currentApplicationModel.getTask_id())
                       .child(firebaseUser.getUid())
                       .child(linkList.get(currentImageIndex).getName()+".appliedTV");


               UploadTask uploadTask_text = text_ref.putBytes(text_byte);
               UploadTask uploadTask_image = image_ref.putBytes(image_byte);
               Task<Uri> urlTask = uploadTask_text.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                   @Override
                   public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                       if (!task.isSuccessful()) {
                           progress.dismiss();
                           Log.e(TAG, task.getException().getMessage());
                           Toast.makeText(MainAnnotateActivity.this, " upload Failed", Toast.LENGTH_SHORT).show();
                           throw task.getException();
                       }

                       // Continue with the task to get the download URL
                       return text_ref.getDownloadUrl();
                   }
               }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                   @Override
                   public void onComplete(@NonNull Task<Uri> task) {


                       if (task.isSuccessful()) {


                           Uri downloadUri = task.getResult();
                           uploadImage(uploadTask_image,image_ref);

                       } else {
                           // Handle failures
                           // ...
                       }
                   }
               });

           }
       }).start();

    }

    private void uploadImage(UploadTask uploadTask_image, StorageReference image_ref) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadTask_image.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred());
                        progressBar.setProgress((int) progress);
                    }
                });
                Task<Uri> urlTask = uploadTask_image.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {
                            progress.dismiss();
                            Log.e(TAG, task.getException().getMessage());
                            Toast.makeText(MainAnnotateActivity.this, " upload Failed", Toast.LENGTH_SHORT).show();
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return image_ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {


                        if (task.isSuccessful()) {


                            Uri downloadUri = task.getResult();

                            // progressBar.setVisibility(GONE);



                            if(progress.isShowing()){

                                new FireBase().getReferenceUsers().child(firebaseUser.getUid()).child("applicationModelList")
                                        .child(currentApplicationModel.getTask_id()).child("task_status").setValue("2").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progress.dismiss();
                                            Toast.makeText(MainAnnotateActivity.this, "Task Completed", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                            }else{
                                progressBar.setVisibility(GONE);

                            }

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });


            }
        }).start();

    }

    public void saveFile(View view) {
        if(imgDrag!=null){
            imgDrag.setVisibility(View.GONE);
        }
        xyTV.setText("");
        hwTV.setText("");
        Bitmap bitmap=getScreenShot(mainLayout);
        imageView.setImageBitmap(bitmap);
        editImageView.setImageBitmap(bitmap);
        labelView.removeView(added_view);
        TEXT_LIST.add(currentModel);
        EDITED_BITMAP=bitmap;
        bitmap_list.add(bitmap);
        doneLayout.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.VISIBLE);
    }

    public  Bitmap getScreenShot(View view) {
        Bitmap bitmap= Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void cancel(View view) {
        if(added_view!=null) {
            labelView.removeView(added_view);
        }
        doneLayout.setVisibility(View.GONE);
        xyTV.setText("");
        hwTV.setText("");
        added_view=null;
        currentModel=null;
        buttonLayout.setVisibility(View.VISIBLE);
    }

    public void undo(View view) {

        if(TEXT_LIST.size()!=0){
            TEXT_LIST.remove(TEXT_LIST.size()-1);
        }
        if(bitmap_list.size()!=0){
            bitmap_list.remove(bitmap_list.size()-1);

            if(bitmap_list.size()==0){
                EDITED_BITMAP=null;
                added_view=null;

                imageView.setImageBitmap(IMAGE_BITMAP);
                editImageView.setImageBitmap(IMAGE_BITMAP);
                TEXT_LIST.clear();
            }else{
                EDITED_BITMAP=bitmap_list.get(bitmap_list.size()-1);
                imageView.setImageBitmap(EDITED_BITMAP);
                editImageView.setImageBitmap(EDITED_BITMAP);
            }
        }
        setText();


    }

    public void close(View view) {



        setMainVisisble();
    }

    public void confirm(View view) {

        if(EDITED_BITMAP==null){

            return;
        }


        setMainVisisble();
        added_view=null;
        currentModel=null;

        close(null);






    }
}