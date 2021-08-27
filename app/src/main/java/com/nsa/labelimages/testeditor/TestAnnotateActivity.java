package com.nsa.labelimages.testeditor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nsa.labelimages.Adapters.ViewPagerAdapter;
import com.nsa.labelimages.Extra.FireBase;
import com.nsa.labelimages.Extra.ProgressBar;
import com.nsa.labelimages.Extra.SavedText;
import com.nsa.labelimages.Extra.Storage;

import com.nsa.labelimages.Model.ApplicationModel;
import com.nsa.labelimages.Model.ImageEditedModel;
import com.nsa.labelimages.Model.RealImagesModel;
import com.nsa.labelimages.Model.TextModel;
import com.nsa.labelimages.R;

import com.nsa.labelimages.Model.FormatModel;
import com.nsa.labelimages.Extra.ImagesPager;
import com.nsa.labelimages.activities.TaskActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nsa.labelimages.activities.TaskActivity.current_task_model;
import static com.nsa.labelimages.activities.TaskActivity.firebaseUser;
import static com.nsa.labelimages.activities.ApplyActivity.realImagesList;

public class TestAnnotateActivity extends AppCompatActivity {


    public List<FormatModel> TEXT_LIST;
    public List<Bitmap> bitmap_list;
    public static FormatModel currentModel;
    public static Context context;


    public static int IMAGE_HEIGHT = 0;
    public static int IMAGE_WIDTH = 0;
    public static View added_view = null;


    ExtendedFloatingActionButton addLabelButton, refreshButton, btnUploadFiles;


    Toolbar toolbar;

    RelativeLayout buttonsLayout, main_layout, done_layout, images_layout;
    ZoomLayout edit_image_layout;
    TestLabelView labelview;
    ImageView imageView;
    public static TextView xyTv, hwTv, textInfo, countTV,topTV,bottomTV;
    ViewPager2 imagesPager;
    ViewPagerAdapter imagesAdapter;
    ImagesPager pager;
    private int current_position = -1;
    SavedText text_saved;
    StorageReference storageReference;


    public void setTransformer(ViewPager2 imagesPager) {
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(8));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float v = 1 - Math.abs(position);
                page.setScaleY(0.8f + v * 0.2f);
            }
        });
        imagesPager.setPageTransformer(transformer);
    }

    public String CURRENT_IMAGE_NAME = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        xyTv = findViewById(R.id.XYTV);
        hwTv = findViewById(R.id.HWTV);
        textInfo = findViewById(R.id.text1);
        countTV = findViewById(R.id.countTV);
        topTV=findViewById(R.id.topTV);
        bottomTV=findViewById(R.id.bottomTV);
        images_layout = findViewById(R.id.images_layout);
        imagesPager = findViewById(R.id.imagesViewPager);
        imagesPager.setClipToPadding(false);
        imagesPager.setClipChildren(false);
        imagesPager.setOffscreenPageLimit(3);
        setTransformer(imagesPager);
        text_saved = new SavedText(TestAnnotateActivity.this);

        pager = new ImagesPager() {
            @Override
            public void onEdit(RealImagesModel model, int position) {
                images_layout.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.VISIBLE);
                textInfo.setVisibility(View.GONE);
                edit_image_layout.setVisibility(View.VISIBLE);
                CURRENT_IMAGE_NAME = model.getName();
                current_position = position;
                if (model.getEditedModel() != null) {
                    setBitmap(model.getEditedModel().getBitmap());
                    return;
                }
                Picasso.get().load(model.getLink()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        setBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        };

        imagesAdapter = new ViewPagerAdapter(getApplicationContext(), pager);
        imagesPager.setAdapter(imagesAdapter);
        imagesPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                countTV.setText((position + 1) + "/"+realImagesList.size());

            }
        });


        String type = getIntent().getStringExtra("tag");



        progressBar = new ProgressBar(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);

        buttonsLayout = findViewById(R.id.buttonLayout);

        context = TestAnnotateActivity.this;


        imageView = findViewById(R.id.imageview);
        edit_image_layout = findViewById(R.id.zoomLayout);
        main_layout = findViewById(R.id.mainLayout);
        done_layout = findViewById(R.id.doneLayout);
        btnUploadFiles = findViewById(R.id.upload);


        labelview = new TestLabelView(TestAnnotateActivity.this, main_layout);

        TEXT_LIST = new ArrayList<>();
        bitmap_list = new ArrayList<>();

        refreshButton = findViewById(R.id.undo);
        addLabelButton = findViewById(R.id.addLabelButton);
        refreshButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearAllViews();
                return true;
            }
        });
        if (type.equals("test")) {
            task_name=getIntent().getStringExtra("task_name");
            toolbar.setTitle(task_name);
            storageReference=new Storage().getTestImagesReference().child(current_task_model.getTask_id()).child(firebaseUser.getUid());
            textInfo.setText("Annotate these "+realImagesList.size()+" images to get verified");
            buttonsLayout.setVisibility(View.GONE);
            btnUploadFiles.setVisibility(View.INVISIBLE);

        }
    }

    String task_name="";


    private void clearAllViews() {
        TEXT_LIST.clear();
        bitmap_list.clear();
        currentModel = null;
        imageView.setImageBitmap(IMAGE_BITMAP);

    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    public static int COLOR;

    @SuppressLint("NewApi")
    public void addLabel(View view) {

        if(!gotImageHW){
            return;
        }
        COLOR = Color.RED;
        if (IMAGE_BITMAP == null) {
            Toast.makeText(this, "add image first", Toast.LENGTH_SHORT).show();
            return;
        }
        final CharSequence[] items = {"Red", "Black", "White"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(TestAnnotateActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        dialog.setCancelable(false);
        dialog.setTitle("Select Color and Label Name");
        EditText editText = new EditText(TestAnnotateActivity.this);
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
                    labelview.addText(LABEL_NAME, COLOR);

                    done_layout.setVisibility(View.VISIBLE);
                    buttonsLayout.setVisibility(View.GONE);

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

    private String LABEL_NAME = "";
    String name;
    File textfile;

    private void saveFile(String text) {
        if (text.isEmpty()) {
            Toast.makeText(this, "text is null", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setMessage("Saving...");
        progressBar.show();
        File folder = new File(getFilesDir(), "Labeled Images");
        if (!folder.exists()) {
            folder.mkdir();
        }
        textfile = new File(folder, name + ".appliedTV");
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(textfile, false);
            stream.write(text.getBytes());
            stream.close();
            uploadTextFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap IMAGE_BITMAP = null;





    String TAG = "TestAnnotateActivity";
    boolean gotImageHW=false;
    private void setBitmap(Bitmap imageBitmap) {
        Log.e(TAG,"setBitmap");
        gotImageHW=false;
        IMAGE_BITMAP = imageBitmap;
        int h=imageBitmap.getHeight();
        int w=imageBitmap.getWidth();
        Log.e(TAG,"h = "+h+" w = "+w);
        float aspect_ratio = (float)w / (float)h ;
        Log.e(TAG,"aspect_ratio = "+aspect_ratio);
        RelativeLayout.LayoutParams layoutParams;
        if(aspect_ratio>1){
            layoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        }else{
            layoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        }

        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(imageBitmap);
        TEXT_LIST.clear();
        bitmap_list.clear();
        currentModel = null;
        imgDrag = null;


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


    public void upload(View view) {



        if (firebaseUser == null) {
            Toast.makeText(this, "Sign-In First", Toast.LENGTH_SHORT).show();
            return;
        }
        if (realImagesList.size() == 0) {
            Toast.makeText(this, "No Image Edited", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setTitle("Upload");
        progressBar.setMessage("Uploading");
        progressBar.show();
        uploadTextFile();

    }

    ProgressBar progressBar;
    int counter=0;

    private void uploadTextFile() {


        progressBar.setMessage("uploading "+(counter+1)+"/"+realImagesList.size());
        String file_name=realImagesList.get(counter).getName();

        Bitmap bmp =realImagesList.get(counter).getEditedModel().getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image_byte = stream.toByteArray();
        byte[] text_byte=text_saved.getText(file_name).getBytes();

        bmp.recycle();
        StorageReference image_ref
                = storageReference
                .child(file_name);
        StorageReference text_ref
                = storageReference
                .child(file_name.replace(".png","") + ".appliedTV");


        UploadTask uploadTask_text = text_ref.putBytes(text_byte);
        UploadTask uploadTask_image = image_ref.putBytes(image_byte);
        Task<Uri> urlTask = uploadTask_text.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    progressBar.hide();
                    Log.e(TAG, task.getException().getMessage());
                    Toast.makeText(TestAnnotateActivity.this, " upload Failed", Toast.LENGTH_SHORT).show();
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

    private void uploadImage(UploadTask uploadTask_image, StorageReference image_ref) {
        Task<Uri> urlTask = uploadTask_image.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    progressBar.hide();
                    Log.e(TAG, task.getException().getMessage());
                    Toast.makeText(TestAnnotateActivity.this, " upload Failed", Toast.LENGTH_SHORT).show();
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
                    text_saved.remove(realImagesList.get(counter).getName());
                    counter++;
                    if(counter==realImagesList.size()){
                        String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                        ApplicationModel applicationModel=new ApplicationModel(current_task_model.getTask_type(),current_task_model.getTask_id(),current_task_model.getTask_name()
                               , date,current_task_model.getPosted_date(),"0",current_task_model.getTask_value(),"0");

                        new FireBase().getReferenceUsers().child(firebaseUser.getUid()).child("applicationModelList").child(current_task_model.getTask_id()).setValue(applicationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull  Task<Void> task) {
                                text_saved.setText("test","done");
                                saveToTask();
                            }
                        });
                    }else {

                        uploadTextFile();
                    }
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }

    private void saveToTask() {
        new FireBase().getReferenceTask().child(current_task_model.getTask_id()).child("TestDone")
                .child(firebaseUser.getUid()).setValue(firebaseUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                progressBar.dismiss();
                if(task.isSuccessful()){

                    Toast.makeText(TestAnnotateActivity.this, "files uploaded!", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(TestAnnotateActivity.this, TaskActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(TestAnnotateActivity.this, "files not uploaded!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public static ImageView imgDrag=null;

    public Bitmap EDITED_BITMAP;
    public void saveFile(View view) {
        if(imgDrag!=null){
            imgDrag.setVisibility(View.GONE);
        }
        xyTv.setText("");
        hwTv.setText("");
        Bitmap bitmap=getScreenShot(main_layout);
        imageView.setImageBitmap(bitmap);
        labelview.removeView(added_view);
        TEXT_LIST.add(currentModel);
        EDITED_BITMAP=bitmap;
        bitmap_list.add(bitmap);
        done_layout.setVisibility(View.GONE);
        buttonsLayout.setVisibility(View.VISIBLE);
    }

    public  Bitmap getScreenShot(View view) {
       Bitmap bitmap= Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void cancel(View view) {
        if(added_view!=null) {
            labelview.removeView(added_view);
        } done_layout.setVisibility(View.GONE);
        xyTv.setText("");
        hwTv.setText("");
        added_view=null;
        currentModel=null;
        buttonsLayout.setVisibility(View.VISIBLE);
    }

    public void undo(View view) {

        if(TEXT_LIST.size()!=0){
            TEXT_LIST.remove(TEXT_LIST.size()-1);
        }
        if(bitmap_list.size()!=0){
            bitmap_list.remove(bitmap_list.size()-1);

            if(bitmap_list.size()==0){
                EDITED_BITMAP=null;
                imageView.setImageBitmap(IMAGE_BITMAP);
                TEXT_LIST.clear();
            }else{
                EDITED_BITMAP=bitmap_list.get(bitmap_list.size()-1);
                imageView.setImageBitmap(EDITED_BITMAP);
            }
        }

    }

    public void prevImage(View view) {
        imagesPager.setCurrentItem(imagesPager.getCurrentItem()-1);
        countTV.setText((imagesPager.getCurrentItem()+1)+"/"+realImagesList.size());
    }

    public void nextImage(View view) {
        imagesPager.setCurrentItem(imagesPager.getCurrentItem()+1);
        countTV.setText((imagesPager.getCurrentItem()+1)+"/"+realImagesList.size());
    }

    public void close(View view) {

        gotImageHW=false;
        images_layout.setVisibility(View.VISIBLE);
        buttonsLayout.setVisibility(View.GONE);
        textInfo.setVisibility(View.VISIBLE);
        edit_image_layout.setVisibility(View.GONE);
    }
    int count=0;

    public void confirm(View view) {

        if(EDITED_BITMAP==null){

            return;
        }
        gotImageHW=false;


        ImageEditedModel imageEditedModel=new ImageEditedModel(CURRENT_IMAGE_NAME,EDITED_BITMAP,TEXT_LIST);
        TextModel textModel=new TextModel(CURRENT_IMAGE_NAME,TEXT_LIST);
        String text=text_saved.getText(CURRENT_IMAGE_NAME);
        for(FormatModel model:TEXT_LIST){
            text+=model.getText()+"\n";
        }
        text_saved.setText(CURRENT_IMAGE_NAME,text);


            RealImagesModel realImagesModel=realImagesList.get(current_position);
            realImagesModel.setEditedModel(imageEditedModel);
            realImagesList.remove(current_position);
            realImagesList.add(current_position,realImagesModel);

              TEXT_LIST.clear();
        for(RealImagesModel model1:realImagesList){
            if(model1.getEditedModel()!=null){
                count++;
            }
        }
        if(realImagesList.size()-count>0) {
            textInfo.setText((realImagesList.size() - count) + " more left");
        }else{
            btnUploadFiles.setVisibility(View.VISIBLE);
            textInfo.setText("You can now upload this images\nto get verified.");


        }

        count=0;
        close(null);
        imagesAdapter.notifyItemChanged(current_position);



    }

    public void backToTask(View view) {
        finish();
    }
}