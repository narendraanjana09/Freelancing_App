package com.nsa.labelimages.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nsa.labelimages.Adapters.HomeFragmentsAdapter;
import com.nsa.labelimages.Adapters.NotificationsAdaper;
import com.nsa.labelimages.Extra.FireBase;
import com.nsa.labelimages.Extra.Progress;
import com.nsa.labelimages.Extra.SavedText;
import com.nsa.labelimages.Extra.Storage;
import com.nsa.labelimages.Interfaces.OnSyncUserData;
import com.nsa.labelimages.Interfaces.PaymentDone;
import com.nsa.labelimages.Interfaces.UploadComplete;
import com.nsa.labelimages.Message.Chat;
import com.nsa.labelimages.Model.ApplicationModel;
import com.nsa.labelimages.Model.PaymentRequestModel;
import com.nsa.labelimages.Model.TaskModel;
import com.nsa.labelimages.Model.UserModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.Extra.LimitedEditText;
import com.nsa.labelimages.paytm.PaytmActivity;
import com.nsa.labelimages.service.Notification;
import com.nsa.labelimages.service.UploadService;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.squareup.picasso.Picasso;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import abhishekti7.unicorn.filepicker.UnicornFilePicker;
import abhishekti7.unicorn.filepicker.utils.Constants;
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.nsa.labelimages.Fragments.ExploreTaskFragment.progressView;
import static com.nsa.labelimages.Fragments.ExploreTaskFragment.taskAdaper;
import static com.nsa.labelimages.Fragments.MyTasksFragment.myTaskAdaper;
import static com.nsa.labelimages.activities.ChatsActivity.chatsAdapter;


public class TaskActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static Context context;
    public static TaskModel current_task_model;
    public static UserModel currentUserModel;
    public static String currentChatAddress;

    public static List<OnSyncUserData> onSyncUserDataList;
    private FirebaseAuth mfirebaseAuth;
    public static FirebaseUser firebaseUser;
    private GoogleSignInClient mGoogleSignInClient;

    CircleImageView profileImageView1,profileImageView2;
    public static List<PaymentRequestModel> paymentRequestModelList;
    public static ApplicationModel currentApplicationModel=null;

   public static SavedText savedText;
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;

    TextView notiCountTextView,noNotiTV,messagesCountTV;
    ConstraintLayout notificationsLayout;
    ImageView deleteIMG;
    RecyclerView notificationRV;
    FloatingActionButton postTaskBTN;


    TabLayout tabLayout;
    ViewPager2 viewPager;
    HomeFragmentsAdapter homeFragmentsAdapter;
    final int EXPLORE_ID=0;
    final int MY_TASK_ID=1;
    CircularProgressIndicator circularProgress;
    RelativeLayout uploadProgressLayout;
    private void mixViewPagerAndTab() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getId()==EXPLORE_ID){
                    postTaskBTN.setVisibility(View.GONE);
                    tab.setIcon(TaskActivity.this.getDrawable(R.drawable.ic_baseline_explore_24));
                }else{
                    postTaskBTN.setVisibility(View.VISIBLE);
                    tab.setIcon(TaskActivity.this.getDrawable(R.drawable.ic_baseline_assignment_24));
                }

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(null);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Explore").setIcon(TaskActivity.this.getDrawable(R.drawable.ic_baseline_explore_24)).setId(EXPLORE_ID));
        tabLayout.addTab(tabLayout.newTab().setText("My Tasks").setId(MY_TASK_ID));
        homeFragmentsAdapter=new HomeFragmentsAdapter(getSupportFragmentManager(),getLifecycle());
        viewPager.setAdapter(homeFragmentsAdapter);
        mixViewPagerAndTab();
        profileImageView1 =findViewById(R.id.profileView);
        postTaskBTN=findViewById(R.id.addTask);
        circularProgress = findViewById(R.id.circular_progress);
        uploadProgressLayout=findViewById(R.id.uploadProgressLayout);
        uploadProgressLayout.setVisibility(View.GONE);



        noNotiTV=findViewById(R.id.noNotiTextView);
        deleteIMG=findViewById(R.id.deleteIMG);
        notiCountTextView=findViewById(R.id.notificationCountTV);
        messagesCountTV=findViewById(R.id.messageCountTextView);
        notificationsLayout=findViewById(R.id.notificationsLayout);
        notificationRV=findViewById(R.id.notificationRV);




        savedText=new SavedText(TaskActivity.this);
        context=TaskActivity.this;
        signIn();


    }


    public void openNotifications(View view) {
        if(userModel.getNotificationsList()==null || userModel.getNotificationsList().size()==0){
            Toast.makeText(this, "No Notifications", Toast.LENGTH_SHORT).show();
            return;
        }

        if(notificationsLayout.getVisibility()==View.VISIBLE){
            notificationsLayout.setVisibility(View.GONE);

        }else{
            notificationsLayout.setVisibility(View.VISIBLE);
        }
       // setContentView(R.layout.payment_success_layout);

    }

    public void closeNotificationDialog(View view) {

        notificationsLayout.setVisibility(View.GONE);
    }

    private void getDrawer() {
        drawerLayout = findViewById(R.id.task_drawer);
        navigationView = findViewById(R.id.task_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        setImageAndName();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
        int id=item.getItemId();
        switch(id){
            case R.id.wallet:
                if(userModel!=null){
                startActivity(new Intent(TaskActivity.this,WalletActivity.class));
                    closeDrawer();
                }
                break;
            case R.id.appliedTask:
                if(userModel!=null){
                    startActivity(new Intent(TaskActivity.this,MyTasksActivity.class));
                    closeDrawer();
                }
                break;
            case R.id.refer:
                if(userModel!=null){
                    startActivity(new Intent(TaskActivity.this,ReferActivity.class));
                    closeDrawer();
                }
                break;
            case R.id.support:
                startActivity(new Intent(TaskActivity.this,HelpSupportActivity.class));
                closeDrawer();
              break;
            case R.id.shareApp:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Invite Freinds");
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "App Link :- https://labelimages.page.link/jdF1";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
                break;
        }

                return true;
    }

    private void setImageAndName() {
        View header = navigationView.getHeaderView(0);
        TextView nameView= (TextView) header.findViewById(R.id.nameTextView);
       TextView openProfile=header.findViewById(R.id.openProfile);
       CircleImageView imageView= (CircleImageView) header.findViewById(R.id.profileImageView);
        openProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userModel!=null){
                startActivity(new Intent(TaskActivity.this,ProfileActivity.class));
                closeDrawer();
                }
            }
        });
       Picasso.get().load(firebaseUser.getPhotoUrl()).into(imageView);
        nameView.setText(firebaseUser.getDisplayName());
    }
    private void signIn() {
        mfirebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=mfirebaseAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            Picasso.get().load(firebaseUser.getPhotoUrl()).into(profileImageView1);

        getUserInfo();
        getDrawer();
        getMessages();
    }

    List<Pair<String,String>> chatList=new ArrayList<>();
    private void getMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new FireBase().getReferenceUsers().child(firebaseUser.getUid())
                        .child("chat_with").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            chatList.clear();
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                String userID=dataSnapshot.getKey();
                                String channelID=dataSnapshot.getValue().toString();
                                Pair<String,String> pair=new Pair<>(userID,channelID);
                                chatList.add(pair);
                            }
                            getChats();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).start();
    }


    public static List<Pair<String,List<Chat>>> chatsList=new ArrayList<>();
    private void getChats(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(Pair pair:chatList){
                    new FireBase().getReferenceChats().child(pair.second.toString()).child("chat")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        List<Chat> messagesList=new ArrayList<>();
                                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                            Chat chat=dataSnapshot.getValue(Chat.class);
                                            messagesList.add(chat);
                                        }
                                        if(chatsAdapter!=null){
                                            chatsAdapter.notifyDataSetChanged();
                                        }
                                        Pair<String,List<Chat>> map=new Pair<>(pair.second.toString(),messagesList);
                                        boolean contains=false;
                                        for(int i=0;i<chatsList.size();i++){
                                            Pair<String,List<Chat>> lst=chatsList.get(i);
                                            if(lst.first.equals(pair.second.toString())){
                                                chatsList.remove(i);
                                                chatsList.add(0,map);
                                                contains=true;
                                                break;
                                            }
                                        }
                                        if(!contains){
                                            chatsList.add(0,map);
                                            if(map.second.get(0).getReceiver().equals(firebaseUser.getUid())){
                                                getUserData(map.second.get(0).getSender());
                                            }else{
                                                getUserData(map.second.get(0).getReceiver());
                                            }

                                        }
                                        checkChats();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
        }).start();
    }
    public static List<UserModel> chatUsersList=new ArrayList<>();
    private void getUserData(String sender) {
        new FireBase().getReferenceUsers().child(sender).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserModel model=snapshot.getValue(UserModel.class);
                    chatUsersList.add(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    int chatCount =0;
    private void checkChats() {
        chatCount=0;
        for(Pair<String,List<Chat>> map:chatsList){
            for(Chat chat:map.second){
                if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                    chatCount++;
                }
                messagesCountTV.post(new Runnable() {
                    @Override
                    public void run() {
                        if(chatCount!=0)
                        messagesCountTV.setText(chatCount+"");
                    }
                });
            }
        }
    }

    public static UserModel userModel;
    int i=0;
    public static List<ApplicationModel> appliedModelList=new ArrayList<>();
    public static List<ApplicationModel>  verifiedModelList=new ArrayList<>();
    private void getUserInfo() {
        onSyncUserDataList=new ArrayList<>();
        Log.e(TAG,firebaseUser.getUid());

        new FireBase().getReferenceUsers().child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()){

                        userModel = snapshot.getValue(UserModel.class);

                    if(snapshot.child("applicationModelList").getChildren()!=null){
                        List<ApplicationModel> applicationModelList=new ArrayList<>();
                        userModel.setApplicaModelList(applicationModelList);
                        for(DataSnapshot snapshot1:snapshot.child("applicationModelList").getChildren()){
                            ApplicationModel applicationModel=snapshot1.getValue(ApplicationModel.class);
                            userModel.getApplicaModelList().add(0,applicationModel);
                        }
                    }
                    if(snapshot.child("notifications").getChildren()!=null){
                        List<String> notificationList=new ArrayList<>();
                        userModel.setNotificationsList(notificationList);
                        for(DataSnapshot snapshot1:snapshot.child("notifications").getChildren()){
                            String noti=snapshot1.getValue()+"";
                            userModel.getNotificationsList().add(0,noti);
                        }

                    }
                    setNotifications();
                   for(OnSyncUserData onSyncUserData:onSyncUserDataList){
                        onSyncUserData.newUserData(userModel);
                    }
                    if(i==0){

                    getTasks();
                    getPaymentRequestInfo();

                    i=1;
                    }


                    Log.e(TAG,"got user info ");
                }else{
                    Log.e(TAG,"not info");
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.e(TAG,"onCancelled "+error.getMessage());
            }
        });
    }

    private void getPaymentRequestInfo() {
        paymentRequestModelList=new ArrayList<>();
        if(firebaseUser.getUid()==null){
            return;
        }
        new FireBase().getReferencePaymentQueueHistory().child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 if(snapshot.exists()){
                     paymentRequestModelList.clear();
                     for(DataSnapshot snapshot1:snapshot.getChildren()){
                         PaymentRequestModel model=snapshot1.getValue(PaymentRequestModel.class);
                         paymentRequestModelList.add(0,model);
                         for(OnSyncUserData onSyncUserData:onSyncUserDataList){
                             onSyncUserData.newRequestdata();
                         }
                     }

                 }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    NotificationsAdaper notificationsAdaper;
    private void setNotifications(){
        if(userModel.getNotificationsList()!=null){
         noNotiTV.setVisibility(View.GONE);
         deleteIMG.setVisibility(View.VISIBLE);
            notificationRV.setVisibility(View.VISIBLE);
            if(userModel.getNotificationsList().size()==0){
                notiCountTextView.setText("");
            }else{
         notiCountTextView.setText(userModel.getNotificationsList().size()+"");
            }
         notificationsAdaper=new NotificationsAdaper(this,userModel.getNotificationsList());
         notificationRV.setAdapter(notificationsAdaper);

        }else{
            notiCountTextView.setText("");
            deleteIMG.setVisibility(View.INVISIBLE);
            notificationRV.setVisibility(View.GONE);
            noNotiTV.setVisibility(View.VISIBLE);
        }
    }


   public static List<TaskModel> taskModelList=new ArrayList<>();
    public static List<TaskModel> myTasksModelList=new ArrayList<>();


    public static void getTasks() {

        taskModelList.clear();
        myTasksModelList.clear();
        DatabaseReference referenceTask =new FireBase().getReferenceTask();
       referenceTask.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               if(snapshot.exists()){

                   for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                       TaskModel model=dataSnapshot.getValue(TaskModel.class);
                       if(model.getUser_id().equals(firebaseUser.getUid())) {
                           myTasksModelList.add(0,model);
                           if(myTaskAdaper!=null){
                               myTaskAdaper.notifyItemInserted(myTasksModelList.size()-1);
                           }
                       }
                       if(userModel.getApplicaModelList()!=null) {
                           boolean isEqual=false;

                           for (ApplicationModel model1 : userModel.getApplicaModelList()){
                               if (model.getTask_id().equals(model1.getTask_id())){
                                   isEqual=true;
                                   break;
                               }
                           }
                           if(!isEqual){
                               taskModelList.add(0,model);

                           }

                       }else{
                           taskModelList.add(0,model);
                       }
                   }
                   if(taskAdaper!=null)
                   taskAdaper.notifyDataSetChanged();
                   progressView.setRefreshing(false);

               }else{
                   progressView.setRefreshing(false);

               }
           }

           @Override
           public void onCancelled(@NonNull @NotNull DatabaseError error) {

           }
       });



    }


    String TAG="taskActivity";


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    public void signout(View view) {
        mfirebaseAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                firebaseUser=null;
                Toast.makeText(TaskActivity.this, "Sign-Out Succesfull!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TaskActivity.this,SignInActivity.class));
                finish();
            }
        });
    }

    public void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public void closeDrawer(){
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    public void openprofilemenu(View view) {
         openDrawer();
    }


    public void setNotifications(View view) {
        List<String> notiList=new ArrayList<>();
        notiList.add("Notification A");
        notiList.add("Notification B");
        notiList.add("Notification C");
        notiList.add("Notification D");
        notiList.add("Notification E");
        if(userModel.getNotificationsList()==null){
            userModel.setNotificationsList(notiList);
        }else{
            userModel.getNotificationsList().addAll(notiList);
        }

        new FireBase().getReferenceUsers().child(firebaseUser.getUid()).setValue(userModel);
    }

    public void deleteNotifications(View view) {

        new FireBase().getReferenceUsers().child(firebaseUser.getUid()).child("notifications").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Toast.makeText(TaskActivity.this, "Notifications Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        closeNotificationDialog(null);
    }
    TextView imageCountTV,testImageCountTV;
    public static List<Uri> imagesUriList=new ArrayList<>();
   public static List<Uri> testImagesUriList=new ArrayList<>();

    Progress progress;
    BottomSheetDialog bottomSheetDialog;
    ImageView prevImageView;
    public static boolean paymentDone=false;
    public static PaymentDone paymentListener;
    boolean notNotifiled=false;
    public void openAddTask(View view) {

        notNotifiled=false;

        progress=new Progress(this);
        progress.setTitle("Uploading...");

          bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.post_a_task_layout);

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                view.setVisibility(View.VISIBLE);
            }
        });
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                view.setVisibility(View.GONE);
            }
        });

        TextView titleTV=bottomSheetDialog.findViewById(R.id.titleTV);

        // the preview items
        TextView addPrevImgBTN=bottomSheetDialog.findViewById(R.id.addPrevImgBTN);
        TextView prevIcon=bottomSheetDialog.findViewById(R.id.prevIcon);
        prevIcon.setVisibility(View.VISIBLE);
        addPrevImgBTN.setVisibility(View.VISIBLE);
        addPrevImgBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(TaskActivity.this)
                        .crop(4,2)
                        .galleryOnly()//Crop image(Optional), Check Customization for more option
                        .compress(1024)//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        TextView taskNameTv =bottomSheetDialog.findViewById(R.id.taskNameTV);
        TextView userNameTv=bottomSheetDialog.findViewById(R.id.userNameTV);
         prevImageView=bottomSheetDialog.findViewById(R.id.prevImageView);
        TextView taskValueTV=bottomSheetDialog.findViewById(R.id.taskValueTV);
        TextView dateTV=bottomSheetDialog.findViewById(R.id.postedDateTV);
        FloatLabeledEditText floatLabeledEditText=bottomSheetDialog.findViewById(R.id.extraTaskLayout);
        LimitedEditText extraTaskED=bottomSheetDialog.findViewById(R.id.extraTaskED);
        LimitedEditText limitedNameED=extraTaskED;
        limitedNameED.setMaxTextSize(100);
        String date=new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());
        dateTV.setText(date);
        userNameTv.setText(firebaseUser.getDisplayName());



        titleTV.setText("Post a Task");
         imageCountTV=bottomSheetDialog.findViewById(R.id.imagesCountTV);
        LimitedEditText taskNameED=bottomSheetDialog.findViewById(R.id.taskNameED);
        LimitedEditText extraTaskLM=taskNameED;
        extraTaskLM.setMaxTextSize(50);
        EditText taskValueED=bottomSheetDialog.findViewById(R.id.taskValueED);
        LimitedEditText taskNoteED=bottomSheetDialog.findViewById(R.id.taskNoteED);
        LimitedEditText limitedEditText=taskNoteED;
        limitedEditText.setMaxTextSize(500);

        //setting up ED with prev TV

        taskValueED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String s1="";
                if(s.toString().equals(".")){
                    s1=".0";
                }
                if(!s.toString().isEmpty()){
                if(!s1.isEmpty()){
                    taskValueTV.setText(getResources().getString(R.string.rupee)+" "+Float.parseFloat(s1)+"/image");
                }else{
                taskValueTV.setText(getResources().getString(R.string.rupee)+" "+Float.parseFloat(s.toString())+"/image");
            }
                }else{
                    taskValueTV.setText("Task Value");
                }
            }
        });
        taskNameED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String htmlString="<u>"+s.toString()+"</u>";
                taskNameTv.setText(Html.fromHtml(htmlString));
            }
        });

        // test part
         testImageCountTV=bottomSheetDialog.findViewById(R.id.testImagesCuntTV);



        RelativeLayout addImagesLayout=bottomSheetDialog.findViewById(R.id.addImagesLayout);

        PowerSpinnerView spinnerView=bottomSheetDialog.findViewById(R.id.spinner);

        spinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(int prev, @Nullable Object PreviousItem, int curr, Object currentItem) {
                      if(currentItem.equals("Annotation")){
                          item=ANNOATATION;
                          addImagesLayout.setVisibility(View.VISIBLE);
                          floatLabeledEditText.setVisibility(View.GONE);
                      }else{
                          item=EXTRA;
                          addImagesLayout.setVisibility(View.GONE);
                          floatLabeledEditText.setVisibility(View.VISIBLE);
                      }
            }
        });

        testImageCountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testImageCountTV.setText("No Images Selected");
                testImagesUriList.clear();
            }
        });
        ExtendedFloatingActionButton btnAddTestImage=bottomSheetDialog.findViewById(R.id.addTestImagesBTN);
        btnAddTestImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testImagesUriList.size()==5){
                    Toast.makeText(TaskActivity.this, "You can only have test images between 2 to 5", Toast.LENGTH_SHORT).show();
                    return;
                }
              getTestImagesFolder();
            }
        });


        ExtendedFloatingActionButton btnAddImage=bottomSheetDialog.findViewById(R.id.addImagesBTN);
        ExtendedFloatingActionButton btnPostTask=bottomSheetDialog.findViewById(R.id.postTaskBTN);
        btnPostTask.setText("Post Task");
        btnPostTask.setIcon(getResources().getDrawable(R.drawable.ic_baseline_cloud_upload_24));

          paymentListener = new PaymentDone() {
              @Override
              public void statusChanged() {
                  if(paymentDone){
                      btnPostTask.setText("Post Task");
                      btnPostTask.setIcon(getResources().getDrawable(R.drawable.ic_baseline_cloud_upload_24));
                  }else{
                      btnPostTask.setText("Add Money");
                      btnPostTask.setIcon(getResources().getDrawable(R.drawable.rupee));
                  }
              }
          };


        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPickImageIntent();
            }
        });
        TextView info=bottomSheetDialog.findViewById(R.id.info);
        btnPostTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName=taskNameED.getText().toString();
                String taskValue=taskValueED.getText().toString();
                String taskNote=taskNoteED.getText().toString();
                String rupee=getResources().getString(R.string.rupee);
                if(taskName.isEmpty()){
                    Toast.makeText(TaskActivity.this, "Please Give a task name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(taskNote.isEmpty()){
                    Toast.makeText(TaskActivity.this, "Please describe your task", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(taskValue.isEmpty()){
                    Toast.makeText(TaskActivity.this, "Please Give a task value", Toast.LENGTH_SHORT).show();
                    return;
                }
                Float taskVal=Float.parseFloat(taskValue);
                if(taskVal<0.3f){
                    Toast.makeText(TaskActivity.this, "Task Value cannot be less than "+rupee+" 0.3", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(item!=-1){
                    if(item==ANNOATATION){
                        if(imagesUriList.size()==0){
                            Toast.makeText(TaskActivity.this, "Please add a dataset of images", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(testImagesUriList.size()==0){
                            Toast.makeText(TaskActivity.this, "Please add a dataset of Test images", Toast.LENGTH_SHORT).show();
                            return;
                        }


                    }else{
                        if(extraTaskED.getText().toString().isEmpty()){
                        Toast.makeText(TaskActivity.this, "Please Describe Your Extra Task", Toast.LENGTH_SHORT).show();
                        return;                    }
                    }
                }else{
                    Toast.makeText(TaskActivity.this, "Please select a task type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(prevImageUri==null){
                    Toast.makeText(TaskActivity.this, "Please Select a Preview image", Toast.LENGTH_SHORT).show();
                    return;
                }
                float amout= Float.parseFloat(userModel.getBalance().getBalance());

                float val=0;
                if(imagesUriList.size()==0){
                    val= Float.parseFloat(taskValue);
                }else{
                    val= Float.parseFloat(taskValue)*imagesUriList.size();
                }
                val+=10;
                if(!notNotifiled){
                if(!(amout>val)){
                    info.setText("you Doesn't have enough balance in your wallet\nplease add "+rupee+" "+(int)val+" to post this task.");
                    btnPostTask.setText("Add Money");
                    btnPostTask.setIcon(getResources().getDrawable(R.drawable.rupee));
                    notNotifiled=true;
                    return;
                }else{
                    paymentDone=true;
                }
                }

                if(paymentDone){
                    String task_id=new FireBase().getReferenceTask().push().getKey();
                    if(item!=ANNOATATION){
                        uploadTaskModel=new TaskModel("Other",firebaseUser.getUid(),firebaseUser.getDisplayName(),
                                prevImageUri+"",task_id,
                                taskName,taskNote,taskValue,date,extraTaskED.getText().toString()+"",null);
                        progress=new Progress(TaskActivity.this);
                        progress.setTitle("Upload");
                        progress.setMessage("uploading...");
                        progress.show();

                        uploadPrevImage();

                    }else{
                        uploadTaskModel=new TaskModel("Annotation",firebaseUser.getUid(),firebaseUser.getDisplayName(),
                                prevImageUri+"",task_id,
                                taskName,taskNote,taskValue,date,testImagesUriList.size()+"",imagesUriList.size()+"");
                        circularProgress.setMaxProgress(TOTAL_BYTES);
                        uploadProgressLayout.setVisibility(View.VISIBLE);
                        snackbar = Snackbar
                                .make(tabLayout, "Upload is gone to background but don't turn off your data", Snackbar.LENGTH_INDEFINITE);

                        snackbar.show();
                        upload();


                        bottomSheetDialog.dismiss();

                    }


                }else{
                    Intent intent=new Intent(TaskActivity.this, PaytmActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("type","post");
                    if(imagesUriList.size()==0){
                        bundle.putInt("size",1);
                    }else{
                        bundle.putInt("size",imagesUriList.size());
                    }
                    bundle.putFloat("value", Float.parseFloat(taskValue));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }



            }
        });
        imageCountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagesUriList.clear();
                imageCountTV.setText("No Folder Added");
            }
        });


        FloatingActionButton closeBTN=bottomSheetDialog.findViewById(R.id.closeFAB);
        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    private void uploadPrevImage() {
        String date=new SimpleDateFormat("ddMMyyyyhhss").format(new Date());
        StorageReference ref
                = new Storage().getGetUsersRefernce()
                .child(
                        firebaseUser.getUid()+"/PrevImages/"+date+".jpg");

        UploadTask uploadTask = ref.putFile(Uri.parse(uploadTaskModel.getPrev_img_link()));

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
                    uploadTaskModel.setPrev_img_link(String.valueOf(downloadUri));
                    uploadAnotherModel(uploadTaskModel);

                } else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    // Handle failures
                    // ...
                }
            }
        });



    }

    private void uploadAnotherModel(TaskModel uploadTaskModel) {


       new Thread(new Runnable() {
           @Override
           public void run() {
               new FireBase().getReferenceTask().child(uploadTaskModel.getTask_id()).setValue(uploadTaskModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       progress.dismiss();
                       uploadProgressLayout.setVisibility(View.GONE);
                       myTasksModelList.add(0,uploadTaskModel);
                       taskModelList.add(0,uploadTaskModel);
                       if(myTaskAdaper!=null){
                           myTaskAdaper.notifyItemInserted(myTasksModelList.size()-1);
                       }
                       if(taskAdaper!=null){
                           taskAdaper.notifyItemInserted(taskModelList.size()-1);
                       }
                       Toast.makeText(TaskActivity.this, "Task Uploaded", Toast.LENGTH_SHORT).show();

                           paymentDone=false;
                           prevImageUri=null;
                           uploadCount=0;
                       if(bottomSheetDialog!=null){
                           bottomSheetDialog.dismiss();
                       }
                       Notification.showNotification("Upload Complete","Upload For "+uploadTaskModel.getTask_name()+" is Completed",firebaseUser.getUid(),TaskActivity.this);
                   }
               });
           }
       }).start();
    }

    int item=-1;
    private int ANNOATATION=0;
    private int EXTRA=12;


    public static int uploadCount=0;
    private void upload() {
        uploadComplete=new UploadComplete() {
            @Override
            public void onComplete() {
                Intent serviceInten = new Intent(TaskActivity.this, UploadService.class);
                stopService(serviceInten);
                 progressView.setRefreshing(true);
                uploadProgressLayout.setVisibility(View.GONE);
                snackbar.setText("Upload Is Complete");
                snackbar.setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                Toast.makeText(TaskActivity.this, "Files Uploaded", Toast.LENGTH_SHORT).show();
                if(bottomSheetDialog!=null){
                    paymentDone=false;
                    prevImageUri=null;
                    uploadCount=0;
                    bottomSheetDialog.dismiss();
                    imagesUriList.clear();

                }
                Notification.showNotification("Upload Complete","Upload For "+uploadTaskModel.getTask_name()+" is Completed",firebaseUser.getUid(),TaskActivity.this);
            }

            @Override
            public void onProgress(double progress, double maxProgress) {
                if(uploadProgressLayout!=null){
                uploadProgressLayout.setVisibility(View.VISIBLE);
                }
                if(circularProgress!=null){
                circularProgress.setProgress(progress,maxProgress);
            }}
        };
        String input = "Uploading Images";
        Intent serviceIntent = new Intent(this, UploadService.class);
        serviceIntent.putExtra("inputExtra", input);
        ContextCompat.startForegroundService(this, serviceIntent);

    }

    public static TaskModel uploadTaskModel;
    public static UploadComplete uploadComplete;

    private void getTestImagesFolder() {
        if(permissionGranted()) {
            progress=new Progress(this);
            progress.setTitle("Getting Files");
            progress.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UnicornFilePicker.from(TaskActivity.this)
                            .addConfigBuilder()
                            .selectMultipleFiles(false)
                            .showOnlyDirectory(true)
                            .setRootDirectory(Environment.getExternalStorageDirectory().getAbsolutePath())
                            .showHiddenFiles(false)
                            .setFilters(new String[]{"heif","bmp", "png", "jpg", "jpeg"})
                            .addItemDivider(true)
                            .theme(R.style.UnicornFilePicker_Dracula)
                            .build()
                            .forResult(GET_TEST_IMAGES);
                }
            }).start();

        }
        else{
            Toast.makeText(this, "Provide Permission To Get Files", Toast.LENGTH_SHORT).show();
            requestPermission();
        }
    }
   public static int GET_TEST_IMAGES=9909;
   public static double size=0;
    Snackbar snackbar;


    public void getPickImageIntent(){

        AlertDialog dialog = new AlertDialog.Builder(TaskActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Upload Dataset Folder")
                .setMessage("Please select a folder which only contains images.")
                .setPositiveButton("ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    progress=new Progress(this);
                    progress.setTitle("Getting Files");
                    progress.show();
                    pickFolder();
                })
                .setNegativeButton("cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .create();
        dialog.show();


    }

    private void pickFolder() {



        if(permissionGranted()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UnicornFilePicker.from(TaskActivity.this)
                            .addConfigBuilder()
                            .selectMultipleFiles(false)
                            .showOnlyDirectory(true)
                            .setRootDirectory(Environment.getExternalStorageDirectory().getAbsolutePath())
                            .showHiddenFiles(false)
                            .setFilters(new String[]{"heif","bmp", "png", "jpg", "jpeg"})
                            .addItemDivider(true)
                            .theme(R.style.UnicornFilePicker_Dracula)
                            .build()
                            .forResult(Constants.REQ_UNICORN_FILE);
                }
            }).start();

        }
        else{
            Toast.makeText(this, "Provide Permission To Get Files", Toast.LENGTH_SHORT).show();
            requestPermission();
        }
    }


    Uri prevImageUri;
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == ImagePicker.REQUEST_CODE){
                prevImageUri=data.getData();
                if(prevImageView!=null){
                    prevImageView.setImageURI(prevImageUri);
                }
            }
           else if(requestCode == Constants.REQ_UNICORN_FILE){
                ArrayList<String> files = data.getStringArrayListExtra("filePaths");
                String folderPath="";
                for(String file : files){
                    folderPath=file;
                    Log.e(TAG, folderPath);
                }
                if(progress!=null){
                progress.dismiss();
                }


                File file=new File(folderPath);

                if(file.listFiles()==null || file.listFiles().length==0){
                    Toast.makeText(this, "Folder Is Empty", Toast.LENGTH_SHORT).show();
                }else{
                    imageCountTV.setText(folderPath+"("+file.listFiles().length+")");
                    imagesUriList.clear();
                    for(File file1:file.listFiles()){

                        Uri uri = Uri.fromFile(file1);
                        TOTAL_BYTES+=file1.length();
                        imagesUriList.add(uri);
                    }
                }
            }
            else if (requestCode == GET_TEST_IMAGES ) {
                ArrayList<String> files = data.getStringArrayListExtra("filePaths");
                String folderPath="";
                for(String file : files){
                    folderPath=file;
                    Log.e(TAG, folderPath);
                }
                if(progress!=null){
                    progress.dismiss();
                }
                File file=new File(folderPath);

                if(file.listFiles()==null || file.listFiles().length==0){
                    Toast.makeText(this, "Folder Is Empty", Toast.LENGTH_SHORT).show();
                }else{
                    testImageCountTV.setText(folderPath+"("+file.listFiles().length+")");
                    testImagesUriList.clear();
                    for(File file1:file.listFiles()){
                        Uri uri = Uri.fromFile(file1);
                        TOTAL_BYTES+=file1.length();

                        testImagesUriList.add(uri);
                    }
                }

            }
        }
    }
   public static double TOTAL_BYTES=0,TOTAL_BYTES_TRANSFERRED=0;
    private boolean permissionGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            pickFolder();
        }
    }

    public void showUploadingTasks(View view) {
    }

    public void openMessages(View view) {
        if(chatsList.size()==0){
            Toast.makeText(context, "You Have No Chats", Toast.LENGTH_SHORT).show();
        }else{
            startActivity(new Intent(this,ChatsActivity.class));
        }
    }
}