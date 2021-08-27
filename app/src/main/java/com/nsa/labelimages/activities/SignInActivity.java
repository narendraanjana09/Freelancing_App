package com.nsa.labelimages.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nsa.labelimages.Extra.FireBase;
import com.nsa.labelimages.Extra.Progress;
import com.nsa.labelimages.Extra.SavedText;
import com.nsa.labelimages.Model.BalanceModel;
import com.nsa.labelimages.Model.UserModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.email.Config;
import com.nsa.labelimages.service.Notification;


import java.text.SimpleDateFormat;
import java.util.Date;

public class SignInActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG="mainActivty";
    private FirebaseAuth mfirebaseAuth;
    public static FirebaseUser firebaseUser;
    private final int  RC_SIGN_IN=1234;
    ExtendedFloatingActionButton googleSignInBTN,signInBTN,verifyBTN;

    TextView loginTV,signUpTV,pwdSHTV;
    EditText emailED,pwdED;
    LinearLayout signInLayout,signupLayout;


    EditText emailSignUpED, pwdSignUpED1,pwdSignUpED2,referralED;
     Context context;
     String referralCode="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        context=SignInActivity.this;
        googleSignInBTN =findViewById(R.id.google_signIn_BTN);
        signInLayout =findViewById(R.id.loginLayout);
        signInLayout.setVisibility(View.VISIBLE);
        pwdSHTV=findViewById(R.id.pwdHSTV);
        emailED=findViewById(R.id.emailED);
        pwdED=findViewById(R.id.pwdED);

        signupLayout=findViewById(R.id.signUpLayout);
        signupLayout.setVisibility(View.GONE);




        signInBTN=findViewById(R.id.signInBtn);
        verifyBTN=findViewById(R.id.verifyBTN);
        loginTV=findViewById(R.id.loginTV);
        signUpTV=findViewById(R.id.signUPTV);
        signInBTN.setVisibility(View.VISIBLE);
        verifyBTN.setVisibility(View.GONE);

        emailSignUpED =findViewById(R.id.emailSignUpED);
        setEmailListener();
        pwdSignUpED1 =findViewById(R.id.passwordSignUpED1);
        pwdSignUpED2 =findViewById(R.id.passwordSignUpED2);
        referralED=findViewById(R.id.refferalED);
        setPWDListener();
        signIn();
       // setText(google_signIn_BTN);
        progressBar=new Progress(SignInActivity.this);

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy) ;


    }
    private void checkForDynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                Log.i(TAG,"We have a dynamic link!");


                Uri deepLink=null;
                if(pendingDynamicLinkData!=null){
                    deepLink=pendingDynamicLinkData.getLink();
                }
                if(deepLink!=null){


                    Log.i(TAG,"Here's the deep link url "+deepLink.toString());
                    String type=deepLink.getQueryParameter("type");

                    if(type==null||type.equals("applink")){
                        return;
                    }

                    if(type.equals("referral")){
                       referralCode=deepLink.getQueryParameter("by");
                        referralED.setText(referralCode);

                    }
                }

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.e(TAG,"oop's cant get dynamic link data"+e.getMessage());
            }
        });
    }




    int i=0;
    private void setText(ExtendedFloatingActionButton btn) {
        String text=btn.getText().toString();
        int l=text.length();
        btn.setText("");
        i=0;


        new CountDownTimer(100 * text.length(), 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!destroyed){


                String a=text.substring(0,i);
                String b=text.substring(i+1,l);
                btn.setText(a+" "+b);
                i++;
            }}

            @Override
            public void onFinish() {
                if(!destroyed){
                btn.setText("Sign-In with Google");
                setText(btn);
            }
            }
        }.start();


    }


    boolean destroyed=false;
    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: " );
        destroyed=true;
        super.onDestroy();
    }

    GoogleSignInAccount account;
    private void signIn() {
        mfirebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=mfirebaseAuth.getCurrentUser();
        account= GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        if(firebaseUser!=null && !firebaseUser.getDisplayName().isEmpty()){
            updateUi(firebaseUser);
            return;
        }
        checkForDynamicLink();




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            switch (requestCode){

                case RC_SIGN_IN:

                    progressBar.setTitle("SignIn");
                    progressBar.setMessage("connecting...");
                    progressBar.show();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);


                    break;

            }

        }

    }



    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            account = task.getResult(ApiException.class);
            Toast.makeText(SignInActivity.this, "Google SignIn SuccessFull", Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            progressBar.dismiss();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Google sign in failed"+e.toString()+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mfirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){


                    firebaseUser=task.getResult().getUser();
                    checkUserData();


                }else{
                    progressBar.dismiss();
                    Toast.makeText(SignInActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void checkUserData() {
        new FireBase().getReferenceUsers().child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("name").getValue().toString().isEmpty()){
                        getReferralLink();
                    }else {
                        progressBar.dismiss();
                        updateUi(firebaseUser);
                    }}else{
                    if(account!=null){
                   updateFirebaseProfile();
                }else{
                        getReferralLink();
                    }
                
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }

    private void updateUserProfile(String user_token, String shortLink) {
        String referralCode=new SavedText(this).getText("referralcode");
        BalanceModel balanceModel;
        if(referralCode.isEmpty()){
            referralCode="";
            balanceModel=new BalanceModel("0","0","0");
        }else{
            balanceModel=new BalanceModel("20","0","0");
        }
        UserModel.ReferralModel referralModel=new UserModel.ReferralModel(firebaseUser.getUid(),shortLink,referralCode);

        String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        UserModel userModel=new UserModel(firebaseUser.getUid(),user_token,"","",date
                ,null,firebaseUser.getEmail(),null,balanceModel
                ,null,referralModel
                ,null,null,null);
        new FireBase().getReferenceUsers().child(firebaseUser.getUid())
                .setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(balanceModel.getBalance().equals("20")){
                   DatabaseReference reference= new FireBase().getReferenceUsers().child(userModel.getReferral().getByCode()).child("balance").child("balance");
                   reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String str=snapshot.getValue()+"";
                                int num= Integer.parseInt(str);
                                num+=20;
                                reference.setValue(num+"");
                                TaskActivity.userModel=userModel;
                                TaskActivity.firebaseUser=firebaseUser;
                                new SavedText(context).remove("referralcode");
                                progressBar.dismiss();
                                new SavedText(context).setText("user_id",firebaseUser.getUid());
                                Intent intent=new Intent(SignInActivity.this,ProfileActivity.class);
                                intent.putExtra("from","signin");
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Notification.showNotification("Reward","You Have Recived a "+getResources().getString(R.string.rupee)+" 20 Joining Bonus In Your Wallet",firebaseUser.getUid(),SignInActivity.this);

                }else{
                    TaskActivity.userModel=userModel;
                    TaskActivity.firebaseUser=firebaseUser;
                    new SavedText(context).remove("referralcode");
                    progressBar.dismiss();
                    new SavedText(context).setText("user_id",firebaseUser.getUid());
                    Intent intent=new Intent(SignInActivity.this,ProfileActivity.class);
                    intent.putExtra("from","signin");
                    startActivity(intent);
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.dismiss();
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateFirebaseProfile() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(account.getDisplayName())
                .setPhotoUri(account.getPhotoUrl())
                .build();
        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG,"updateFirebaseProfile");
                            getReferralLink();
                        }
                    }
                });

    }


    private void getToken(String link) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Token", "Fetching FCM registration token failed\nTry again", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String  user_token = task.getResult();
                        if(account!=null ) {
                          //  getReferralCode(user_token,link);
                            uploadDetails(account,firebaseUser,referralCode,user_token,link);
                        }else{
                            updateUserProfile(user_token,link);
                        }





                    }
                });
    }

    private void getReferralLink() {

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.labelImages.com/?type=referral&by="+firebaseUser.getUid()))
                .setDomainUriPrefix("https://labelimages.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(this.getPackageName())
                                .setMinimumVersion(4)
                                .build())
                .buildDynamicLink();
        Log.e(TAG,"createDynamicLink");

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLink.getUri())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Log.e(TAG,"shortLinkTask");
                            Uri shortLink = task.getResult().getShortLink();
                            getToken(shortLink.toString());
                        } else {
                            Toast.makeText(SignInActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(SignInActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.e("LinkException",e.getMessage());
                    }
                });

    }

    private void getReferralCode(String user_token, String link) {
        progressBar.dismiss();
        EditText editText = new EditText(this);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setTextColor(Color.WHITE);
        editText.setText(referralCode);
        AlertDialog dialog = new AlertDialog.Builder(SignInActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Referral Code!")
                .setMessage("Enter Referral Code and You can\nearn "+getResources().getString(R.string.rupee)+" 20 after completing first task.")
                .setView(editText)
                .setPositiveButton("yes", (dialogInterface, i) -> {


                    String referralCode = editText.getText().toString();
                    if(referralCode.isEmpty()){
                        getReferralCode( user_token, link);
                        dialogInterface.dismiss();


                    }else{
                        progressBar.show();
                        uploadDetails(account,firebaseUser,referralCode,user_token,link);
                    }
                })
                .setNegativeButton("no", (dialogInterface, i) -> {

                    uploadDetails(account,firebaseUser,"",user_token, link);
                })
                .create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void uploadDetails(GoogleSignInAccount account, FirebaseUser firebaseUser, String referralCode, String user_token, String link) {
        String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        UserModel.ReferralModel referralModel=new UserModel.ReferralModel(firebaseUser.getUid(),link,referralCode);
        BalanceModel balanceModel;
        if(referralCode.isEmpty()){
            referralCode="";
            balanceModel=new BalanceModel("0","0","0");
        }else{
            balanceModel=new BalanceModel("20","0","0");
        }

        UserModel model=new UserModel(firebaseUser.getUid(),user_token,account.getDisplayName(),account.getPhotoUrl()+"",date
        ,null,account.getEmail(),null,balanceModel
                ,null,referralModel
                ,null,null,null);//new IDModel("0","0"));
        new FireBase().getReferenceUsers().child(firebaseUser.getUid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                if(balanceModel.getBalance().equals("20")){
                    DatabaseReference reference= new FireBase().getReferenceUsers().child(model.getReferral().getByCode()).child("balance").child("balance");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String str=snapshot.getValue()+"";
                                int num= Integer.parseInt(str);
                                num+=20;
                                reference.setValue(num+"");
                                TaskActivity.userModel=model;
                                TaskActivity.firebaseUser=firebaseUser;
                                new SavedText(context).remove("referralcode");
                                progressBar.dismiss();
                                new SavedText(context).setText("user_id",firebaseUser.getUid());
                                Intent intent=new Intent(SignInActivity.this,ProfileActivity.class);
                                intent.putExtra("from","signin");
                                startActivity(intent);
                                finish();
                            }
                            progressBar.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.dismiss();
                            Toast.makeText(context, "error :- "+error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    Notification.showNotification("Reward","You Have Recived a "+getResources().getString(R.string.rupee)+" 20 Joining Bonus In Your Wallet",firebaseUser.getUid(),SignInActivity.this);

                }else {

                    new SavedText(context).remove("referralcode");
                    progressBar.dismiss();
                    new SavedText(context).setText("user_id",firebaseUser.getUid());
                    Intent intent=new Intent(SignInActivity.this,TaskActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }



    private void updateUi(FirebaseUser account) {
        if(account!=null){
            new SavedText(this).setText("user_id",firebaseUser.getUid());
            startActivity(new Intent(SignInActivity.this,TaskActivity.class));
            finish();
        }
    }

    Progress progressBar;
    public void login(View view) {


            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    public void showSignUpLayout(View view) {
        signUpTV.setTextColor(getResources().getColor(R.color.chocalte));
        loginTV.setTextColor(getResources().getColor(R.color.white1));
        signInLayout.setVisibility(View.GONE);
        signInBTN.setVisibility(View.GONE);
        signupLayout.setVisibility(View.VISIBLE);
        verifyBTN.setVisibility(View.VISIBLE);
        googleSignInBTN.setText("Sign-Up with Google");
    }

    public void showLoginLayout(View view) {
        googleSignInBTN.setText("Sign-In with Google");
        signUpTV.setTextColor(getResources().getColor(R.color.white1));
        loginTV.setTextColor(getResources().getColor(R.color.chocalte));
        signInLayout.setVisibility(View.VISIBLE);
        signInBTN.setVisibility(View.VISIBLE);
        signupLayout.setVisibility(View.GONE);
        verifyBTN.setVisibility(View.GONE);


    }
    private void setPWDListener() {
        pwdED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pwd=s.toString();
                if(pwd.isEmpty()){
                    pwdSHTV.setVisibility(View.GONE);
                }else{
                    if(pwd.length()<8){
                        pwdED.setTextColor(getResources().getColor(R.color.red1));
                    }else{
                        pwdED.setTextColor(getResources().getColor(R.color.white));
                    }
                    pwdSHTV.setVisibility(View.VISIBLE);
                }

            }
        });
    }
    String pwd;

    public void signIn(View view) {
        String email=emailED.getText().toString();
        String password=pwdED.getText().toString();
        if(!checkEmail(email)){
            Toast.makeText(context, "Please Enter Correct Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<8){
            Toast.makeText(context, "Password must contain atleast 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setTitle("Sign-In Process");
        progressBar.setMessage("Checking details...");
        progressBar.show();


        mfirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(context, "signInWithEmail:success", Toast.LENGTH_SHORT).show();
                            firebaseUser = mfirebaseAuth.getCurrentUser();
                            if(firebaseUser.isEmailVerified()) {
                                checkUserData();
                            }else{
                                progressBar.dismiss();
                               Snackbar snackbar=Snackbar.make(googleSignInBTN,"Please Verify Your Email First",Snackbar.LENGTH_INDEFINITE);
                               snackbar.setAction("resend link", new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void unused) {
                                               Toast.makeText(context, "Email Sent", Toast.LENGTH_SHORT).show();
                                               snackbar.dismiss();
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               Toast.makeText(context, "Email Sent Failure\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                               snackbar.dismiss();
                                           }
                                       });
                                   }
                               });
                               snackbar.show();
                            }
                        } else {
                            progressBar.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, ""+task.getException(),
                                    Toast.LENGTH_LONG).show();
                      
                        }
                    }
                });

    }

    boolean passwordHide=true;
    public void showHidePWD(View view) {
        Drawable[] drawables = pwdSHTV.getCompoundDrawables();
        if (passwordHide) {
            passwordHide = false;
            drawables[0] = SignInActivity.this.getDrawable(R.drawable.ic_baseline_visibility_off_24);
            pwdED.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            passwordHide = true;
            drawables[0] = SignInActivity.this.getDrawable(R.drawable.ic_baseline_visibility_24);
            pwdED.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        }
        drawables[0].setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(pwdSHTV.getContext(), R.color.chocalte), PorterDuff.Mode.SRC_IN));
        pwdED.setSelection(pwdED.length());
        pwdSHTV.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);


    }
    String sEmail=Config.EMAIL;
    String sPassword=Config.PASSWORD;



    public boolean checkEmail(String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email.isEmpty()) {
            return false;
        }else {
            if (email.trim().matches(emailPattern)) {
                return  true;
            } else {
               return false;
            }
        }
    }
    private void setEmailListener() {
        emailSignUpED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               email=s.toString();
               if(checkEmail(email)){
                   emailSignUpED.setTextColor(getResources().getColor(R.color.white));
               }else{
                   emailSignUpED.setTextColor(getResources().getColor(R.color.red));
               }
            }
        });
    }
    String email="";
    public void verifyEmail(View view) {
        String email= emailSignUpED.getText().toString();
        String password1= pwdSignUpED1.getText().toString();
        String password2= pwdSignUpED2.getText().toString();
        String referralCode=referralED.getText().toString();
        if(!checkEmail(email)){
            Toast.makeText(context, "Please Enter Correct Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password1.length()<8){
            Toast.makeText(context, "Password must contain atleast 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!(password1.equals(password2))){
            Toast.makeText(context, "Password confirmation failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!referralCode.isEmpty()){
            if(referralCode.length()!=28){
                Toast.makeText(context, "Referral Code is not valid!", Toast.LENGTH_SHORT).show();
            }else{
            checkReferralCode(referralCode,email,password1);
            }
            return;
        }
        beginSignUP(email, password1);

    }
    private void checkReferralCode(String referralCode, String email, String password1) {
        progressBar.setTitle("Checking Refferal Code");
        progressBar.setMessage("configuring...");
        progressBar.show();
        new FireBase().getReferenceUsers().child(referralCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    new SavedText(context).setText("referralcode",referralCode);
                    beginSignUP(email,password1);
                }else{
                    Toast.makeText(context, "Referral Code is not valid!!", Toast.LENGTH_LONG).show();
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "error\n"+error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void beginSignUP(String email, String password1) {
        progressBar.setTitle("Sign-Up Process");
        progressBar.setMessage("sending link on email");
        if (!progressBar.isShowing()){
            progressBar.show();
    }

        mfirebaseAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mfirebaseAuth.getCurrentUser();
                            if(user.isEmailVerified()){

                            }else{
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.dismiss();
                                        Snackbar snackbar = Snackbar
                                                .make(googleSignInBTN, "We've Sent a verification link to your email address\nplease verify it first and sign in", Snackbar.LENGTH_INDEFINITE);
                                        snackbar.setAction("ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                snackbar.dismiss();
                                            }
                                        });
                                        snackbar.show();

                                        showLoginLayout(null);
                                    }
                                });

                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed\n"+task.getException(),
                                    Toast.LENGTH_LONG).show();
                            progressBar.dismiss();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "createUserWithEmail:Exception"+e.getMessage());
                Toast.makeText(SignInActivity.this, "Authentication Exception\n"+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void startReset(View view) {

        AlertDialog.Builder builder=new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailet= new EditText(this);

        // write the email using which you registered
        emailet.setHint("enter email");
        emailet.setText(emailED.getText().toString().trim());
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailet.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(emailet);
        linearLayout.setPadding(30,10,30,10);
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String emaill=emailet.getText().toString().trim();

                if(checkEmail(emaill)){

                    progressBar=new Progress(context);
                    progressBar.setTitle("Checking");
                    progressBar.setMessage("checking email");
                    progressBar.show();
                    checkUser(emaill);
                }else{

                    Toast.makeText(context, "please enter correct email ", Toast.LENGTH_SHORT).show();
                    startReset(null);
                }


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void checkUser(String emaill) {
        mfirebaseAuth.fetchSignInMethodsForEmail(emaill).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
               if(task.isSuccessful()){
                 if(task.getResult().getSignInMethods().size()==1){
                        sendRecoveryEmail(emaill);
                 }else{
                     progressBar.dismiss();
                     Toast.makeText(context, "sorry this user not exist!", Toast.LENGTH_SHORT).show();
                 }
               }
              
            }
        });
    }

    private void sendRecoveryEmail(String emaill) {
        mfirebaseAuth.sendPasswordResetEmail(emaill).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.dismiss();
                if(task.isSuccessful())
                {
                    // if isSuccessful then done messgae will be shown
                    // and you can change the password
                    Snackbar snackbar=Snackbar.make(googleSignInBTN,"We've sent an recovery mail to\n"+emaill+" please reset your password there!",Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }
                else {
                    Toast.makeText(context,"Error Occured",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.dismiss();
                Toast.makeText(context,"Error Failed",Toast.LENGTH_LONG).show();
            }
        });
    }
}