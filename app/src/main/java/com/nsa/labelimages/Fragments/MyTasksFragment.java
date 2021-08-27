package com.nsa.labelimages.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.nsa.labelimages.Adapters.ApplyOtherModel;
import com.nsa.labelimages.Adapters.FilesAdapter;
import com.nsa.labelimages.Adapters.ImagesAdapter;
import com.nsa.labelimages.Adapters.MyTaskAdaper;
import com.nsa.labelimages.Adapters.TestVeriUsersAdapter;
import com.nsa.labelimages.Extra.DownloadSnapshot;
import com.nsa.labelimages.Extra.FireBase;
import com.nsa.labelimages.Extra.Progress;
import com.nsa.labelimages.Extra.Storage;
import com.nsa.labelimages.Interfaces.ImageClick;
import com.nsa.labelimages.Interfaces.ImageDownloadListener;
import com.nsa.labelimages.Interfaces.SnapShotListener;
import com.nsa.labelimages.Interfaces.TaskClick;
import com.nsa.labelimages.Interfaces.UserClick;
import com.nsa.labelimages.Model.ApplicationModel;
import com.nsa.labelimages.Model.ImagesModel;
import com.nsa.labelimages.Model.TaskModel;
import com.nsa.labelimages.Model.UserModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.activities.MessageActivity;
import com.nsa.labelimages.activities.TaskActivity;
import com.nsa.labelimages.maineditor.DownloadImage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.nsa.labelimages.activities.TaskActivity.currentApplicationModel;
import static com.nsa.labelimages.activities.TaskActivity.currentUserModel;
import static com.nsa.labelimages.activities.TaskActivity.current_task_model;
import static com.nsa.labelimages.activities.TaskActivity.firebaseUser;
import static com.nsa.labelimages.activities.TaskActivity.myTasksModelList;
import static com.nsa.labelimages.activities.TaskActivity.userModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyTasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTasksFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyTasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerifiedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTasksFragment newInstance(String param1, String param2) {
        MyTasksFragment fragment = new MyTasksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    // task recycler views
    public static MyTaskAdaper myTaskAdaper;
    RecyclerView myTaskRV;
    TextView infoView;

    // task info views
    CardView infoCard,testCard,veriCard;
    FloatingActionButton backBTN,testUserBTN,veriUsersBTN;
    ImageView imageView;
    TextView nameTV,dateTV,amountTV
            ,taskInfoTV,imagesCountTV
            ,testUserCountTV,veriUsersCountTV;
    TaskClick taskClick;
    List<String> testUsersList=new ArrayList<>();
    List<String> veriUsersList=new ArrayList<>();
    ProgressBar progressTest,progressVeri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_my_task, container, false);
        myTaskRV=view.findViewById(R.id.myTaskRV);
        infoView=view.findViewById(R.id.infoView);
        
        taskClick=new TaskClick() {
            @Override
            public void onClick(TaskModel model) {
                setTaskInfoVisible();
                setTaskInfo(model);

                current_task_model=model;
                progressTest.setVisibility(View.VISIBLE);
                progressVeri.setVisibility(View.VISIBLE);
                if(model.getTask_type().equals("Annotation")){
                    veriCard.setVisibility(View.VISIBLE);
                getTestList(model);
                getVeriList(model);
                }else{
                    veriCard.setVisibility(View.GONE);
                    getAppliedList(model);
                    getVerifiedList(model);
                }
            }
        };
        
        myTaskAdaper=new MyTaskAdaper(getContext(),myTasksModelList,taskClick);
        myTaskRV.setAdapter(myTaskAdaper);
        if(myTasksModelList.size()!=0){
            infoView.setVisibility(View.GONE);
        }
        myTaskAdaper.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {

                infoView.setVisibility(View.GONE);
                super.onItemRangeInserted(positionStart, itemCount);

            }
        });

        // task info views
        imageView=view.findViewById(R.id.prevImageView);
        backBTN= view.findViewById(R.id.backBTN);
        veriCard=view.findViewById(R.id.veriCard);
        testCard=view.findViewById(R.id.notVeriCard);
        infoCard=view.findViewById(R.id.infoCard);
        nameTV=view.findViewById(R.id.taskNameTV);
        amountTV=view.findViewById(R.id.taskValueTV);
        dateTV=view.findViewById(R.id.dateTV);
        taskInfoTV=view.findViewById(R.id.taskInfoTV);
        imagesCountTV=view.findViewById(R.id.imagesCountTV);
        testUserCountTV=view.findViewById(R.id.testUserCount);
        veriUsersCountTV=view.findViewById(R.id.verifiedUserCount);
        testUserBTN=view.findViewById(R.id.testUsersBTN);
        veriUsersBTN=view.findViewById(R.id.veriUsersBTN);
        progressTest=view.findViewById(R.id.progressTest);
        progressVeri=view.findViewById(R.id.progressVeri);

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerVisible();
            }
        });
        testUserBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTestDialog();

            }
        });
        veriUsersBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVeriDialog();
            }
        });

        return view;
    }

    private void getVeriDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.test_veri_users_layout);

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                testUserBTN.setVisibility(View.VISIBLE);
            }
        });
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                testUserBTN.setVisibility(View.INVISIBLE);
            }
        });

        TextView titleTV=bottomSheetDialog.findViewById(R.id.titleTV);
        ProgressBar progressBar=bottomSheetDialog.findViewById(R.id.progressBar);
        FloatingActionButton closeFab=bottomSheetDialog.findViewById(R.id.closeFAB);
        FloatingActionButton backFAB=bottomSheetDialog.findViewById(R.id.backFAB);

        closeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        titleTV.setText("Verified Users");
        RecyclerView usersRV=bottomSheetDialog.findViewById(R.id.usersRV);
        if(testUserModelList.size()!=0){
            progressBar.setVisibility(View.INVISIBLE);
        }

        // USER info VIEWS
        RelativeLayout mainDataLayout=bottomSheetDialog.findViewById(R.id.userDataLayout);
        mainDataLayout.setVisibility(View.GONE);
        CircleImageView profileIV=bottomSheetDialog.findViewById(R.id.profileIV);
        TextView userNameTV=bottomSheetDialog.findViewById(R.id.userNameTV);
        TextView appliedDateTV=bottomSheetDialog.findViewById(R.id.appliedDateTV);
        FloatingActionButton expandTestImages=bottomSheetDialog.findViewById(R.id.expandTestImagesList);
        TextView appliedTVNote =bottomSheetDialog.findViewById(R.id.appliedTV);
        RecyclerView testImagesRV=bottomSheetDialog.findViewById(R.id.testImagesRV);

        ExtendedFloatingActionButton verifyBTN=bottomSheetDialog.findViewById(R.id.verifyBTN);
        verifyBTN.setVisibility(View.GONE);
        ExtendedFloatingActionButton messageBTN=bottomSheetDialog.findViewById(R.id.messageBTN);
        messageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MessageActivity.class));
            }
        });


        UserClick userClick=new UserClick() {
            @Override
            public void onUserClick(UserModel model,String date) {
                TaskActivity.currentUserModel=model;
                for(ApplyOtherModel applyOtherModel:applyOtherModelsList){
                    if(applyOtherModel.getUser_id().equals(model.getId())){
                        currentApplyOtherModel=applyOtherModel;
                        break;
                    }
                }


                usersRV.setVisibility(View.GONE);
                backBTN.setVisibility(View.INVISIBLE);
                mainDataLayout.setVisibility(View.VISIBLE);
                backFAB.setVisibility(View.VISIBLE);

                userNameTV.setText(model.getName()+"\n"+model.getEmail());
                Picasso.get().load(model.getImageLink()).into(profileIV);
                if(current_task_model.getTask_type().equals("Annotation")) {
                    expandTestImages.setVisibility(View.GONE);
                    appliedTVNote.setText("Test Images");
                    appliedDateTV.setText("Applied Date :- "+date);
                    ImageClick imageClick=new ImageClick() {
                        @Override
                        public void onClick(ImagesModel imagesModel) {
                            setImageDialog(imagesModel);
                        }
                    };

                    ImagesAdapter imagesAdapter=new ImagesAdapter(getContext(),editedImagesList,imageClick);
                    testImagesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    testImagesRV.setItemAnimator(null);
                    testImagesRV.setAdapter(imagesAdapter);
                    getTestImages(userModel.getId(),current_task_model.getTask_id(),testImagesRV,imagesAdapter);

                }else {
                    expandTestImages.setVisibility(View.VISIBLE);
                    appliedTVNote.setText("View Document");
                    appliedDateTV.setText("Verified Date :- "+date+"\n\nDescription :- "+currentApplyOtherModel.getDescription());
                }



            }
        };
        expandTestImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentApplyOtherModel!=null){
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                    intent.setData(Uri.parse(currentApplyOtherModel.getPdfLink()));
//                    startActivity(intent);

                }
            }
        });
        backFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRV.setVisibility(View.VISIBLE);
                mainDataLayout.setVisibility(View.GONE);
                backFAB.setVisibility(View.GONE);
                backBTN.setVisibility(View.VISIBLE);
            }
        });

        testVeriUsersAdapter=new TestVeriUsersAdapter(getContext(),veriUserModelList,userClick);
        usersRV.setAdapter(testVeriUsersAdapter);

        testVeriUsersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        bottomSheetDialog.show();

    }


    TestVeriUsersAdapter testVeriUsersAdapter;
    private void getTestDialog() {

       BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.test_veri_users_layout);

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                testUserBTN.setVisibility(View.VISIBLE);
            }
        });
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                testUserBTN.setVisibility(View.INVISIBLE);
            }
        });

        TextView titleTV=bottomSheetDialog.findViewById(R.id.titleTV);
        ProgressBar progressBar=bottomSheetDialog.findViewById(R.id.progressBar);
        FloatingActionButton closeFab=bottomSheetDialog.findViewById(R.id.closeFAB);
        FloatingActionButton backFAB=bottomSheetDialog.findViewById(R.id.backFAB);

        closeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        titleTV.setText("Applied Users");
        RecyclerView usersRV=bottomSheetDialog.findViewById(R.id.usersRV);
        if(testUserModelList.size()!=0){
            progressBar.setVisibility(View.INVISIBLE);
        }

        // USER info VIEWS
        RelativeLayout mainDataLayout=bottomSheetDialog.findViewById(R.id.userDataLayout);
        mainDataLayout.setVisibility(View.GONE);
        ChipGroup rateCG=bottomSheetDialog.findViewById(R.id.rateCG);
        Chip goodCP=bottomSheetDialog.findViewById(R.id.goodFit);
        Chip noFitCP=bottomSheetDialog.findViewById(R.id.notFit);
        CircleImageView profileIV=bottomSheetDialog.findViewById(R.id.profileIV);
        TextView userNameTV=bottomSheetDialog.findViewById(R.id.userNameTV);
        TextView appliedDateTV=bottomSheetDialog.findViewById(R.id.appliedDateTV);
        FloatingActionButton expandTestImages=bottomSheetDialog.findViewById(R.id.expandTestImagesList);
        TextView appliedTVNote =bottomSheetDialog.findViewById(R.id.appliedTV);
        RecyclerView testImagesRV=bottomSheetDialog.findViewById(R.id.testImagesRV);
        ExtendedFloatingActionButton verifyBTN=bottomSheetDialog.findViewById(R.id.verifyBTN);

        ExtendedFloatingActionButton messageBTN=bottomSheetDialog.findViewById(R.id.messageBTN);
        messageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Progress progress=new Progress(getContext());
                progress.setTitle("loading...");
                progress.show();
                DatabaseReference reference= new FireBase().getReferenceUsers().child(firebaseUser.getUid()).child("chat_with").child(currentUserModel.getId());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            progress.dismiss();
                            TaskActivity.currentChatAddress=snapshot.getValue().toString();
                            Log.e(TAG, "onDataChange: " +TaskActivity.currentChatAddress);
                            startActivity(new Intent(getContext(), MessageActivity.class));
                        }else{
                            String date=new SimpleDateFormat("dd_MM_yyyy").format(new Date())+"_"+System.currentTimeMillis();
                            reference.setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    new FireBase().getReferenceUsers().child(currentUserModel.getId()).child("chat_with").child(firebaseUser.getUid())
                                            .setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            progress.dismiss();
                                            TaskActivity.currentChatAddress=date;
                                            startActivity(new Intent(getContext(), MessageActivity.class));
                                        }
                                    });

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progress.dismiss();
                        Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        UserClick userClick=new UserClick() {
            @Override
            public void onUserClick(UserModel model,String date) {
                TaskActivity.currentUserModel=model;
                for(ApplyOtherModel applyOtherModel:applyOtherModelsList){
                    if(applyOtherModel.getUser_id().equals(model.getId())){
                        currentApplyOtherModel=applyOtherModel;
                        break;
                    }
                }
                rateCG.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(ChipGroup group, int checkedId) {
                        if(checkedId == R.id.notFit){
                            new FireBase().getReferenceUsers().child(model.getId()).child("applicationModelList")
                                    .child(current_task_model.getTask_id()).child("task_status").setValue("3");
                        }else{
                            new FireBase().getReferenceUsers().child(model.getId()).child("applicationModelList")
                                    .child(current_task_model.getTask_id()).child("task_status").setValue("1");
                        }
                    }
                });
                new FireBase().getReferenceUsers().child(model.getId()).child("applicationModelList")
                        .child(current_task_model.getTask_id()).child("task_status").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status=snapshot.getValue().toString();
                            Log.e(TAG,"status "+status);
                            if(status.equals("1")){
                                goodCP.setChecked(true);
                            }else if(status.equals("3")){
                                noFitCP.setChecked(true);
                            }else if(status.equals("0")){
                                Toast.makeText(getContext(), "status chages "+status, Toast.LENGTH_SHORT).show();
                                new FireBase().getReferenceUsers().child(model.getId()).child("applicationModelList")
                                        .child(current_task_model.getTask_id()).child("task_status").setValue("2");
                                noFitCP.setChecked(false);
                                goodCP.setChecked(false);
                            }else{
                                noFitCP.setChecked(false);
                                goodCP.setChecked(false);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                verifyBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new FireBase().getReferenceUsers().child(model.getId()).child("applicationModelList")
                                .child(current_task_model.getTask_id()).child("task_status").setValue("1");
                        if(current_task_model.getTask_type().equals("Annotation")) {

                            new FireBase().getReferenceTask().child(current_task_model.getTask_id())
                                    .child("TestDone").child(model.getId()).removeValue();
                            new FireBase().getReferenceTask().child(current_task_model.getTask_id())
                                    .child("VeriDone").child(userModel.getId()).setValue(userModel.getId());
                            veriUsersList.add(0,userModel.getId()+"");
                            getUserData(userModel.getId()+"",false);
                            veriUsersCountTV.setText(veriUsersList.size()+" Verified User");
                        }else{
                            new FireBase().getReferenceTask().child(current_task_model.getTask_id())
                                    .child("AppliedUsers").child(model.getId()).removeValue();
                            new FireBase().getReferenceTask().child(current_task_model.getTask_id())
                                    .child("VerifiedUsers").child(model.getId()).setValue(currentApplyOtherModel);
                            verifiedOtherModelsList.add(0,currentApplyOtherModel);
                            veriUsersCountTV.setText(verifiedOtherModelsList.size()+" Verified User");
                            testVeriUsersAdapter.notifyDataSetChanged();
                        }
                        testUserModelList.remove(userModel);
                        testUserCountTV.setText(testUserModelList.size()+"Applied Users");
                        veriUsersBTN.setVisibility(View.VISIBLE);
                        bottomSheetDialog.dismiss();
                        backBTN.setVisibility(View.VISIBLE);

                    }
                });

                usersRV.setVisibility(View.GONE);
                backBTN.setVisibility(View.INVISIBLE);
                mainDataLayout.setVisibility(View.VISIBLE);


                backFAB.setVisibility(View.VISIBLE);

                if(model.getPhoneModel()==null || model.getPhoneModel().getNumber()==null){
                    userNameTV.setText(model.getName()+"\n"+model.getEmail());
                }else{
                userNameTV.setText(model.getName()+"\n"+model.getPhoneModel().getNumber()+"\n"+model.getEmail());
                }
                Picasso.get().load(model.getImageLink()).into(profileIV);
                if(current_task_model.getTask_type().equals("Annotation")) {
                    expandTestImages.setVisibility(View.GONE);
                            appliedTVNote.setText("Test Images");
                    appliedDateTV.setText("Applied Date :- "+date);
                    ImageClick imageClick=new ImageClick() {
                        @Override
                        public void onClick(ImagesModel imagesModel) {
                            setImageDialog(imagesModel);
                        }
                    };

                    ImagesAdapter imagesAdapter=new ImagesAdapter(getContext(),editedImagesList,imageClick);
                    testImagesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    testImagesRV.setItemAnimator(null);
                    testImagesRV.setAdapter(imagesAdapter);
                    getTestImages(userModel.getId(),current_task_model.getTask_id(),testImagesRV,imagesAdapter);

                }else {
                    if(currentApplyOtherModel.getFiles()==null){
                        appliedTVNote.setText("User Haven't Uploaded Any Document");
                        expandTestImages.setVisibility(View.GONE);
                    }else{
                        appliedTVNote.setText("Uploaded Document's");
                        FilesAdapter adapter=new FilesAdapter(currentApplyOtherModel.getFiles(),getContext());
                        testImagesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        testImagesRV.setAdapter(adapter);
                    }
                    appliedDateTV.setText("Applied Date :- "+date+"\n\nDescription :- "+currentApplyOtherModel.getDescription());
                }
            }
        };

        backFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRV.setVisibility(View.VISIBLE);
                mainDataLayout.setVisibility(View.GONE);
                backFAB.setVisibility(View.GONE);
                backBTN.setVisibility(View.VISIBLE);
                

            }
        });

        testVeriUsersAdapter=new TestVeriUsersAdapter(getContext(),testUserModelList,userClick);
        usersRV.setAdapter(testVeriUsersAdapter);

        testVeriUsersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        bottomSheetDialog.show();

    }
    public static ApplyOtherModel currentApplyOtherModel;

    private void setImageDialog(ImagesModel imagesModel) {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());
        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.images_text_layout,
                        null);
        builder.setView(customLayout);
        builder.setCancelable(true);
        AlertDialog dialog
                = builder.create();

        PhotoView photoView=customLayout.findViewById(R.id.annotateImageView);
        photoView.setImageBitmap(imagesModel.getImgBitmap());
        TextView textTV =customLayout.findViewById(R.id.annoatateTextView);
        textTV.setText(imagesModel.getTextLink());
              dialog.show();
    }

    List<ImagesModel> editedImagesList=new ArrayList<>();
    private void getTestImages(String id, String task_id, RecyclerView testImagesRV, ImagesAdapter imagesAdapter) {
        new Storage().getTestImagesReference().child(task_id).child(id).listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        if(listResult.getItems().size()==0){
                            Toast.makeText(getContext(), "no images found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.e(TAG, "onSuccess: got image list" );
                        editedImagesList.clear();
                        List<StorageReference> imgList=new ArrayList<>();
                        List<StorageReference> textList=new ArrayList<>();
                       for(StorageReference item:listResult.getItems()){
                           if(item.getName().contains(".appliedTV")){
                               textList.add(item);
                           }else{
                               imgList.add(item);
                           }
                       }

                        counter=0;
                         countermax=imgList.size();
                       editedImagesList.clear();
                         getImagesLink(imgList, textList,testImagesRV,imagesAdapter);




                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();

                    }
                });
    }

    int counter=0,countermax=0;
    private void getImagesLink(List<StorageReference> imgList, List<StorageReference> textList, RecyclerView testImagesRV, ImagesAdapter imagesAdapter) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(counter<countermax){
                    ImagesModel model=new ImagesModel(imgList.get(counter).getName()+"","","",null);

                    imgList.get(counter).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri url) {
                            model.setImageLink(String.valueOf(url));
                            textList.get(counter).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    StorageReference gsReference = new Storage().getStorage().getReferenceFromUrl(String.valueOf(uri));

                                    final long ONE_MEGABYTE = 1024 * 1024;
                                    gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            model.setTextLink(new String(bytes));
                                            Log.e(TAG,model.getTextLink());

                                            editedImagesList.add(model);
                                            if(imagesAdapter!=null){
                                                imagesAdapter.notifyItemInserted(editedImagesList.size()-1);
                                            }
                                            counter++;
                                            getImagesLink(imgList,textList, testImagesRV, imagesAdapter);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Log.e(TAG,e.getMessage());
                                        }
                                    });


                                }
                            });
                        }
                    });

                }
                }
        }).start();



    }

    List<UserModel> testUserModelList=new ArrayList<>();
    List<UserModel> veriUserModelList=new ArrayList<>();
    public void getUserData(String userID,boolean isTest){
          SnapShotListener snapShotListener=new SnapShotListener() {
              @Override
              public void onSnapshot(DataSnapshot snapshot) {
                  if(snapshot == null){

                  }else{
                      UserModel model=snapshot.getValue(UserModel.class);
                      if(snapshot.child("applicationModelList").getChildren()!=null){
                          List<ApplicationModel> applicationModelList=new ArrayList<>();
                          model.setApplicaModelList(applicationModelList);
                          for(DataSnapshot snapshot1:snapshot.child("applicationModelList").getChildren()){
                              ApplicationModel applicationModel=snapshot1.getValue(ApplicationModel.class);
                              model.getApplicaModelList().add(0,applicationModel);
                          }
                      }
                      if(isTest){
                          testUserModelList.add(0,model);
                      }else{
                          veriUserModelList.add(0,model);
                      }
                      Log.e(TAG, "onSnapshot: "+model.getName());
                      if(testVeriUsersAdapter!=null){
                          testVeriUsersAdapter.notifyDataSetChanged();
                      }
                  }
              }
          };
        DownloadSnapshot downloadSnapshot=new DownloadSnapshot(snapShotListener);
        downloadSnapshot.execute(new FireBase().getReferenceUsers().child(userID));
    }
    private void getVerifiedList(TaskModel model) {

       new FireBase().getReferenceTask().child(model.getTask_id()).child("VerifiedUsers").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               progressVeri.setVisibility(View.GONE);
               if(snapshot.exists()){
                   verifiedOtherModelsList.clear();
                   for(DataSnapshot snapshot1:snapshot.getChildren()){
                       ApplyOtherModel model1=snapshot1.getValue(ApplyOtherModel.class);
                       verifiedOtherModelsList.add(0,model1);
                       if(!veriUsersList.contains(model1.getUser_id())){
                           veriUsersList.add(0,model1.getUser_id());
                           getUserData(model1.getUser_id(),true);
                       }
                   }
                   int size=verifiedOtherModelsList.size();
                   if(size==1){
                       veriUsersCountTV.setText(size+" Verified User");
                       veriUsersBTN.setVisibility(View.VISIBLE);
                   }else if(size == 0){
                       veriUsersCountTV.setText("No Verified User");
                       veriUsersBTN.setVisibility(View.INVISIBLE);
                   }
                   else{
                       veriUsersBTN.setVisibility(View.VISIBLE);
                       veriUsersCountTV.setText(size+" Test Users");
                   }
               }else{
                   veriUsersCountTV.setText("No Verified User");
                   veriUsersBTN.setVisibility(View.INVISIBLE);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }
    List<ApplyOtherModel> verifiedOtherModelsList=new ArrayList<>();
    List<ApplyOtherModel> applyOtherModelsList=new ArrayList<>();
    private void getAppliedList(TaskModel model) {

    new FireBase().getReferenceTask().child(model.getTask_id()).child("AppliedUsers").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            progressTest.setVisibility(View.GONE);
                if(snapshot.exists()){
                    applyOtherModelsList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        ApplyOtherModel model1=snapshot1.getValue(ApplyOtherModel.class);
                        applyOtherModelsList.add(0,model1);
                        if(!testUsersList.contains(model1.getUser_id())){
                            testUsersList.add(0,model1.getUser_id());
                            getUserData(model1.getUser_id(),true);
                        }
                    }
                    int size=applyOtherModelsList.size();
                    if(size==1){
                        testUserCountTV.setText(size+" User Applied");
                        testUserBTN.setVisibility(View.VISIBLE);
                    }else if(size == 0){
                        testUserCountTV.setText("No Users Applied");
                        testUserBTN.setVisibility(View.INVISIBLE);
                    }
                    else{
                        testUserBTN.setVisibility(View.VISIBLE);
                        testUserCountTV.setText(size+" Users Applied");
                    }
                }else{
                    testUserCountTV.setText("No Users Applied");
                    testUserBTN.setVisibility(View.INVISIBLE);
                }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    }

    private void getTestList(TaskModel model) {
        SnapShotListener snapShotListener=new SnapShotListener() {
            @Override
            public void onSnapshot(DataSnapshot snapshot) {
                progressTest.setVisibility(View.GONE);
                if(snapshot==null){
                    testUserCountTV.setText("No Test User");
                    testUserBTN.setVisibility(View.INVISIBLE);
                }else{
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        if(!testUsersList.contains(dataSnapshot.getValue())){
                            testUsersList.add(0,dataSnapshot.getValue()+"");
                            getUserData(dataSnapshot.getValue()+"",true);
                        }

                        Log.e(TAG,"userID = "+testUsersList.get(testUsersList.size()-1));
                    }

                    int size=testUsersList.size();
                    if(size==1){
                        testUserCountTV.setText(size+" Test User");
                        testUserBTN.setVisibility(View.VISIBLE);
                    }else if(size == 0){
                        testUserCountTV.setText("No Test User");
                        testUserBTN.setVisibility(View.INVISIBLE);
                    }
                    else{
                        testUserBTN.setVisibility(View.VISIBLE);
                        testUserCountTV.setText(size+" Test Users");
                    }


                }
            }
        };
        DownloadSnapshot downloadSnapshot=new DownloadSnapshot(snapShotListener);
        downloadSnapshot.execute(new FireBase().getReferenceTask().child(model.getTask_id()).child("TestDone"));
    }

    private void getVeriList(TaskModel model) {
        SnapShotListener snapShotListener=new SnapShotListener() {
            @Override
            public void onSnapshot(DataSnapshot snapshot) {
                progressVeri.setVisibility(View.GONE);
                if(snapshot==null){
                    veriUsersCountTV.setText("No Verified User");
                    veriUsersBTN.setVisibility(View.INVISIBLE);
                }else{
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        if(!veriUsersList.contains(dataSnapshot.getValue())){
                            veriUsersList.add(0,dataSnapshot.getValue()+"");
                            getUserData(dataSnapshot.getValue()+"",false);
                        }
                        Log.e(TAG,"userID = "+veriUsersList.get(veriUsersList.size()-1));
                    }

                    int size=veriUsersList.size();
                    if(size==1){
                        veriUsersCountTV.setText(size+" Verified User");
                        veriUsersBTN.setVisibility(View.VISIBLE);
                    }else if(size == 0){
                        veriUsersCountTV.setText("No Verified User");
                        veriUsersBTN.setVisibility(View.INVISIBLE);
                    }
                    else{
                        veriUsersBTN.setVisibility(View.VISIBLE);
                        veriUsersCountTV.setText(size+" Verified Users");
                    }


                }
            }
        };
        DownloadSnapshot downloadSnapshot=new DownloadSnapshot(snapShotListener);
        downloadSnapshot.execute(new FireBase().getReferenceTask().child(model.getTask_id()).child("VeriDone"));
    }

    private void setTaskInfo(TaskModel model) {
        ImageDownloadListener imageDownloadListener=new ImageDownloadListener() {
            @Override
            public void onImageDownloaded(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onImageDownloadError() {
                Toast.makeText(getContext(), "can't load image\nerror", Toast.LENGTH_SHORT).show();
            }
        };
        DownloadImage downloadImage=new DownloadImage(imageDownloadListener);
        downloadImage.execute(Uri.parse(model.getPrev_img_link()));
        nameTV.setText(model.getTask_name());
        amountTV.setText(getContext().getString(R.string.rupee)+" "+model.getTask_value()+"/image");
        dateTV.setText(model.getPosted_date());
        taskInfoTV.setText(model.getTask_note());
        imagesCountTV.setText(model.getTotalTestImages()+" Test Images\n"+model.getTotalMainImages()+" Main Images");

    }

    public void setRecyclerVisible(){
        myTaskRV.setVisibility(View.VISIBLE);

        infoCard.setVisibility(View.GONE);
        testCard.setVisibility(View.GONE);
        veriCard.setVisibility(View.GONE);
        backBTN.setVisibility(View.GONE);
    }
    public void setTaskInfoVisible(){
        myTaskRV.setVisibility(View.GONE);

        infoCard.setVisibility(View.VISIBLE);
        testCard.setVisibility(View.VISIBLE);
        veriCard.setVisibility(View.VISIBLE);
        backBTN.setVisibility(View.VISIBLE);
    }




    String TAG="verified";
}