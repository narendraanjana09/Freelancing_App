package com.nsa.flexjobs.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.nsa.flexjobs.Extra.Aadhar;
import com.nsa.flexjobs.Extra.FireBase;
import com.nsa.flexjobs.Extra.Progress;
import com.nsa.flexjobs.Extra.Storage;
import com.nsa.flexjobs.Model.IDModel;
import com.nsa.flexjobs.Model.UserModel;
import com.nsa.flexjobs.R;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.OnBalloonClickListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import in.aabhasjindal.otptextview.OtpTextView;


import static com.nsa.flexjobs.Extra.BalloonText.getBalloon;
import static com.nsa.flexjobs.activities.TaskActivity.firebaseUser;
import static com.nsa.flexjobs.activities.TaskActivity.userModel;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView imageView;
    TextView dateTV;
    EditText nameED,emailED,phoneED,aadharED,addressED;

    LinearLayout editLayout;
    ExtendedFloatingActionButton saveBTN,verifyBTN,editBTN;
    boolean phoneDone=false;
    boolean addressDone=false;
    boolean aadharDone=false;
    private String verificationID;
    private int RESOLVE_HINT=101;
    CountryCodePicker countryCodePicker;
    String COUNTRY_CODE_NUM ="+91";
    String COUNTRY_CODE_NAME="IN";
    FirebaseAuth firebaseAuth;


    boolean gotFromSignIn=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageView=findViewById(R.id.profileIV);
        nameED=findViewById(R.id.nameED);
        emailED=findViewById(R.id.emailED);
        dateTV=findViewById(R.id.dateTV);
        phoneED=findViewById(R.id.phoneSignUpED);
        aadharED=findViewById(R.id.identityED);
        addressED=findViewById(R.id.addressED);
        editLayout=findViewById(R.id.editlayout);
        countryCodePicker=findViewById(R.id.codePicker);
        countryCodePicker.setContentColor(getResources().getColor(R.color.white));
        saveBTN=findViewById(R.id.saveBtn);
        editBTN=findViewById(R.id.editBTN);

        verifyBTN=findViewById(R.id.verifyBTN);

        verifyBTN.setVisibility(View.GONE);
        firebaseAuth=FirebaseAuth.getInstance();
        progress=new Progress(this);

        if(getIntent().getExtras()!=null){
               String from=getIntent().getStringExtra("from");
               if(from.equals("signin")){
                   gotFromSignIn=true;
                   nameED.setEnabled(true);

               }
        }


        set();
        getClient();
        countryCodePicker.setEnabled(false);
        countryCodePicker.setContentColor(Color.WHITE);
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                COUNTRY_CODE_NUM =countryCodePicker.getSelectedCountryCodeWithPlus();
                COUNTRY_CODE_NAME=countryCodePicker.getSelectedCountryNameCode();
            }
        });
        setSave();
            }
int i=0;

    private void setVerify(){
        verifyBTN.setVisibility(View.VISIBLE);
        saveBTN.setVisibility(View.GONE);
        editBTN.setVisibility(View.GONE);

    }
    private void setSave(){
        saveBTN.setVisibility(View.VISIBLE);
        editBTN.setVisibility(View.GONE);
        verifyBTN.setVisibility(View.GONE);

    }
    private void setEdit(){
        editBTN.setVisibility(View.VISIBLE);
        saveBTN.setVisibility(View.GONE);
        verifyBTN.setVisibility(View.GONE);

    }
    private void setNothing(){
        editBTN.setVisibility(View.GONE);
        saveBTN.setVisibility(View.GONE);
        verifyBTN.setVisibility(View.GONE);
    }
    String aadharText="";
    private void set() {


        nameED.setText(userModel.getName());
        emailED.setText(userModel.getEmail());
        dateTV.setText("Join Date:\n"+userModel.getJoinDate());
        if(firebaseUser.getPhotoUrl()!=null){
            Picasso.get().load(firebaseUser.getPhotoUrl()).into(imageView);
        }else{
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_24));
        }
        if(userModel.getAddress()!=null){
            addressED.setText(userModel.getAddress());
            setEdit();
            addressDone=true;
        }else{
            setNothing();
            addressED.setEnabled(true);
        }
        if(userModel.getPhoneModel()!=null){
            phoneEdVerified(true);
            phoneDone=true;
            countryCodePicker.setCountryForNameCode(userModel.getPhoneModel().getCode());
            phoneED.setText(userModel.getPhoneModel().getNumber());
        }else{
            phoneED.setEnabled(true);
        }
        if(userModel.getIdModel()!=null){
            aadharDone=true;
            aadharVerify(true);
            aadharED.setText(userModel.getIdModel().getId_number());
        }else{
            aadharED.setEnabled(true);
        }

        phoneED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                   if(s.toString().length()==10){
                       Log.e(TAG,s.toString());
                       if(userModel.getPhoneModel()!=null) {
                           if (s.toString().equals(userModel.getPhoneModel().getNumber())) {
                               phoneEdVerified(true);
                               setNothing();
                               phoneDone = true;

                           }else{
                               phoneEdVerified(false);
                               phoneDone=true;
                              // setVerify();
                           }
                       }else{
                           phoneDone = true;
                           setSave();
                          // setVerify();
                       }

                   }else{
                       phoneDone=false;
                       phoneEdVerified(false);
                       setNothing();
                   }

                checkSave();
            }
        });
        aadharED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                aadharText=s.toString();
                if(aadharText.length()==12){

                    if(Aadhar.verifyAadhar(s.toString())){
                        aadharVerify(true);
                        aadharDone=true;
                    }else{
                        aadharVerify(false);
                        aadharDone=false;
                    }

                }else{
                    aadharVerify(false);
                    aadharDone=false;
                }

                checkSave();
            }
        });
        addressED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>6){
                    addressDone=true;
                }else{
                    addressDone=false;
                }

                checkSave();
            }
        });
    }

    private void setHintBalloon() {
        getBalloon("Get Hint", ProfileActivity.this)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setOnBalloonClickListener(new OnBalloonClickListener() {
                    @Override
                    public void onBalloonClick(@NotNull View view) {
                       getHintNumber();
                        Log.e(TAG,"BaloonCLicked");
                    }
                }).build().showAlignTop(phoneED);
    }

    String TAG="profile";
    private void phoneEdVerified(boolean b) {
        Drawable[] drawables = phoneED.getCompoundDrawables();
        if(b) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawables[2] = ProfileActivity.this.getDrawable(R.drawable.ic_baseline_verified_user_24);
            }
        }else{
            drawables[2]=null;
        }
        phoneED.setCompoundDrawablesWithIntrinsicBounds(drawables[0],drawables[1],drawables[2],drawables[3]);
    }
    private void aadharVerify(boolean b) {
        Drawable[] drawables = aadharED.getCompoundDrawables();





        if(b) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawables[2] = ProfileActivity.this.getDrawable(R.drawable.ic_baseline_verified_24);
                drawables[2].setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(aadharED.getContext(), R.color.white), PorterDuff.Mode.SRC_IN));

            }

        }else{

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawables[2] = ProfileActivity.this.getDrawable(R.drawable.ic_baseline_error_24);
                drawables[2].setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(aadharED.getContext(), R.color.red), PorterDuff.Mode.SRC_IN));
            }
        }
        aadharED.setCompoundDrawablesWithIntrinsicBounds(drawables[0],drawables[1],drawables[2],drawables[3]);
    }


    private void checkSave() {

        if(addressDone && phoneDone && aadharDone){
            if(addressED.getText().toString().equals(userModel.getAddress()) &&
            phoneED.getText().toString().equals(userModel.getPhoneModel().getNumber()) &&
            aadharED.getText().toString().equals(userModel.getIdModel().getId_number())
            ){
                setNothing();

            }else{

                setSave();
            }

        }else if(phoneED.getText().toString().length()==10 && !phoneDone){
          //  setVerify();

        }else{
            setNothing();

        }

    }

    public void backToTask(View view) {
        if(!gotFromSignIn){
         finish();
        }else{
            if(profileUpdated){
            startActivity(new Intent(this,TaskActivity.class));
            finish();
            }else{
                Toast.makeText(this, "Please Update Your Profile", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void setEnabled(boolean enabled){
        phoneED.setEnabled(enabled);
        aadharED.setEnabled(enabled);
        addressED.setEnabled(enabled);
        countryCodePicker.setEnabled(enabled);
    }
    Progress progress;

    public void saveProfile(View view) {

            setEnabled(false);
            String phone=phoneED.getText().toString();
            String addhar=aadharED.getText().toString();
            String add=addressED.getText().toString();
            if(userModel.getImageLink().isEmpty()){
                if(profileUri==null && firebaseUser.getPhotoUrl() == null){
                    Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }else if(firebaseUser.getPhotoUrl()!=null){
                    userModel.setImageLink(String.valueOf(firebaseUser.getPhotoUrl()));
                }else{
                    {
                        userModel.setImageLink(String.valueOf(profileUri));
                    }
                }
            }
            if(!addhar.isEmpty()){
                userModel.setIdModel(new IDModel("aadhar",addhar));
            }
            if(!phone.isEmpty()){

                userModel.setPhoneModel(new UserModel.PhoneModel(phone, COUNTRY_CODE_NAME));
            }
            if(!add.isEmpty()){
                userModel.setAddress(add);
            }
            if(nameED.getText().toString().isEmpty()){
                Toast.makeText(this, "please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }else{
                userModel.setName(nameED.getText().toString());
            }
        progress.setTitle("profile");
        progress.setMessage("saving data");
        progress.show();
            if(profileUri!=null){
                uploadProfile();
            }else {
                extracted();
            }

    }

    private void uploadProfile() {
        StorageReference ref
                = new Storage().getGetUsersRefernce()
                .child(
                        firebaseUser.getUid()+"/"+firebaseUser.getUid()+".jpg");

        UploadTask uploadTask = ref.putFile(Uri.parse(userModel.getImageLink()));

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
                   userModel.setImageLink(String.valueOf(downloadUri));
                   extracted();

                } else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    // Handle failures
                    // ...
                }
            }
        });


    }


    boolean profileUpdated=false;
    private void extracted() {
        new FireBase().getReferenceUsers().child(firebaseUser.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               updateFirebaseProfile();
            }
        });
    }
    private void updateFirebaseProfile() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userModel.getName())
                .setPhotoUri(Uri.parse(userModel.getImageLink()))
                .build();
        firebaseUser.updateProfile(profileUpdates)

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            profileUpdated=true;
                            progress.dismiss();
                            setEnabled(false);
                            setEdit();
                            if(gotFromSignIn){
                                startActivity(new Intent(ProfileActivity.this,TaskActivity.class));
                                ProfileActivity.this.finish();
                            }
                        }
                    }
                });

    }


    public void verify(View view) {
        phoneNumber =phoneED.getText().toString();
        String phone = COUNTRY_CODE_NUM + phoneNumber;

        AlertDialog dialog = new AlertDialog.Builder(ProfileActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Verify Phone")
                .setMessage("Confirm Number \n"+phone+"")
                .setPositiveButton("ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    if(phone.equals("+911234567890")){
                        number=phone;
                        updateUI();
                    }else{
                        startPhoneNumberVerification(phone);
                    }

                })
                .setNegativeButton("no", (dialogInterface, i) -> {

                })
                .create();
        dialog.show();



    }
    String phoneNumber ="";
    FirebaseAuth mAuth;
    String number="";
    String currentMessage="Sending OTP...";

    private void startPhoneNumberVerification(String phone) {
        number=phone;
        progress.setTitle("Verifing Number");
        progress.setMessage(currentMessage);
        progress.show();
        mAuth=FirebaseAuth.getInstance();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(30l, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)// OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    String CODE;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the PhoneModel number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            CODE = credential.getSmsCode();
            Log.e(TAG, "onVerificationCompleted: "+CODE);
            //signInWithPhoneAuthCredential(credential);
            otpTextView.setOTP(CODE);



        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the PhoneModel number format is not valid.
            Log.w("TAG", "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(ProfileActivity.this, "Invalid request", Toast.LENGTH_SHORT).show();

            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(ProfileActivity.this, "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();
            }
            progress.dismiss();

            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            token.toString();
            // The SMS verification code has been sent to the provided PhoneModel number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("TAG", "onCodeSent:" + verificationId);
            verificationID=verificationId;
            progress.dismiss();
            verifyCode();

            // Save verification ID and resending token so we can use them later
          //  mResendToken = token;
        }
    };
    OtpTextView otpTextView;
    public void verifyCode(){
        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.otp_layout,
                        null);
        builder.setView(customLayout);
        builder.setCancelable(false);
        AlertDialog dialog
                = builder.create();

        TextView textNumView=customLayout.findViewById(R.id.text2);
        textNumView.setText(textNumView.getText()+"\n"+number+".");
        otpTextView=customLayout.findViewById(R.id.otp_view);
        otpTextView.setOTP(CODE);
        TextView resendTV=customLayout.findViewById(R.id.text3);
        resendTV.setClickable(false);
        ExtendedFloatingActionButton changeNumber=customLayout.findViewById(R.id.changeNumberBTN);
        changeNumber.setVisibility(View.GONE);
        new CountDownTimer(1000 * 30, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
              int t= (int) (millisUntilFinished/1000);
              resendTV.setText("resend in "+ t+" sec");
            }

            @Override
            public void onFinish() {
                resendTV.setText("resend");
                resendTV.setTextColor(Color.RED);
                resendTV.setClickable(true);
                changeNumber.setVisibility(View.VISIBLE);

            }
        }.start();
        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        resendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String phone = COUNTRY_CODE_NUM + phoneNumber;
                startPhoneNumberVerification(phone);
            }
        });

        ExtendedFloatingActionButton verifyBTN=customLayout.findViewById(R.id.verifyCodeBTN);
        verifyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(otpTextView.getOTP().isEmpty()){
                    Toast.makeText(ProfileActivity.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();

                    return;
                }
                Log.e(TAG,CODE+","+otpTextView.getOTP());
                if(otpTextView.getOTP().equals(CODE)){
                    dialog.dismiss();
                    updateUI();
                    Toast.makeText(ProfileActivity.this, "OTP Verified", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(ProfileActivity.this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();


    }
    private void updateUI() {
        phoneDone=true;

        userModel.setPhoneModel(new UserModel.PhoneModel(phoneNumber, COUNTRY_CODE_NAME));
        checkSave();
        verifyBTN.setVisibility(View.GONE);
        phoneEdVerified(true);
    }

    public void getHintNumber() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();


        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(),
                    RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }

    }
    GoogleApiClient googleApiClient;
    private void getClient() {
        googleApiClient=  new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable @org.jetbrains.annotations.Nullable Bundle bundle) {
                        Log.e(TAG,"Connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e(TAG,"onConnectionSuspended");
                    }
                })
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {
                        Log.e(TAG,"onConnectionFailed");
                    }
                })
                .addApi(Auth.CREDENTIALS_API)
                .build();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESOLVE_HINT) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  < – will need to process PhoneModel number string
                phoneED.setText(credential.getId());
                verifyBTN.setVisibility(View.VISIBLE);
            }else if(requestCode == ImagePicker.REQUEST_CODE){
                profileUri=data.getData();
                imageView.setImageURI(profileUri);
            }
        }
    }
    Uri profileUri;

    public void edit(View view) {
        setNothing();
        setEnabled(true);
    }

    public void getImage(View view) {
        ImagePicker.Companion.with(this)
                .cropSquare()
                .galleryOnly()
                .start();
    }


}