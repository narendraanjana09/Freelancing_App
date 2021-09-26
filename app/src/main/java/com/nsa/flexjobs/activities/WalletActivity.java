package com.nsa.flexjobs.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nsa.flexjobs.Adapters.CreditHistoryAdapter;
import com.nsa.flexjobs.Adapters.QueueHistoryAdapter;
import com.nsa.flexjobs.Extra.FireBase;
import com.nsa.flexjobs.Extra.Progress;
import com.nsa.flexjobs.Interfaces.OnSyncUserData;
import com.nsa.flexjobs.Model.CreditModel;
import com.nsa.flexjobs.Model.PaymentRequestModel;
import com.nsa.flexjobs.Model.UserModel;
import com.nsa.flexjobs.R;
import com.nsa.flexjobs.paytm.PaytmActivity;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nsa.flexjobs.activities.TaskActivity.firebaseUser;
import static com.nsa.flexjobs.activities.TaskActivity.paymentRequestModelList;
import static com.nsa.flexjobs.activities.TaskActivity.userModel;

public class WalletActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    TextView balanceTV,creditTV,requestTV;
    ExtendedFloatingActionButton redeemBTN;
    CardView balanceCard,creditCard,requestCard;
    LinearLayout paymentLayout;
    RadioGroup paymentRG;
    EditText amountED,numberEd;
    int paymentMode=-1;
    final int PAYTM=1;
    final int PHONEPE=2;
    final int DEBIT=0;
    String amount="";
    String number="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        balanceTV=findViewById(R.id.balanceTV);
        creditTV=findViewById(R.id.creditTV);
        redeemBTN=findViewById(R.id.redeemBTN);
        balanceCard=findViewById(R.id.balanceCard);
        creditCard=findViewById(R.id.creditCard);
        paymentLayout=findViewById(R.id.payMethodLayout);
        paymentRG=findViewById(R.id.paymentRG);
        numberEd=findViewById(R.id.numberEd);
        amountED=findViewById(R.id.amountEditText);
        requestTV=findViewById(R.id.requestTV);
        requestCard=findViewById(R.id.requestCard);
        paymentRG.setOnCheckedChangeListener(this);

        amountED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                amount=s.toString();

                    checkRedeem();


            }
        });
        numberEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                number=s.toString();
                checkRedeem();

            }
        });
        OnSyncUserData onSyncUserData =new OnSyncUserData() {
            @Override
            public void newUserData(UserModel userModel) {
                setData();
            }

            @Override
            public void newRequestdata() {


            }
        };
        TaskActivity.onSyncUserDataList.add(onSyncUserData);

        setData();
        getCredHistory();
    }
    CreditHistoryAdapter creditHistoryAdapter;
    BottomSheetDialog creditDialog;
    List<CreditModel> creditModelList;
    public void getCredHistory() {
        creditModelList=new ArrayList<>();
        creditDialog = new BottomSheetDialog(this);
        creditDialog.setContentView(R.layout.history_layout);
        RecyclerView recyclerView=creditDialog.findViewById(R.id.historyRV);
        creditHistoryAdapter=new CreditHistoryAdapter(this,creditModelList);
        recyclerView.setAdapter(creditHistoryAdapter);
        TextView titleTV=creditDialog.findViewById(R.id.titleTV);
        titleTV.setText("Credit History");
        FloatingActionButton closeBTN=creditDialog.findViewById(R.id.closeFAB);
        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditDialog.dismiss();
            }
        });
        new FireBase().getReferencePaymentCreditHistory().child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    creditModelList.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        CreditModel model=dataSnapshot.getValue(CreditModel.class);
                        creditModelList.add(0,model);
                    }
                    creditHistoryAdapter.notifyDataSetChanged();

                }else{
                    creditModelList.clear();
                    creditHistoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    public void showCreditHistory(View view) {
        if(creditDialog!=null){
        creditDialog.show();
        }else{
            Toast.makeText(this, "error try later", Toast.LENGTH_SHORT).show();
        }
    }

    String TAG="Wallet";
    int totalBalance;
    int totalQueue;
    int totalCredited;

    private void setData() {
        if(userModel!=null){



            totalBalance= Integer.parseInt(userModel.getBalance().getBalance());
            totalCredited= Integer.parseInt(userModel.getBalance().getCredited());
            totalQueue=Integer.parseInt(userModel.getBalance().getInQueue());
            balanceTV.setText(getResources().getString(R.string.rupee)+" "+totalBalance);
            creditTV.setText(getResources().getString(R.string.rupee)+" "+totalCredited);
            requestTV.setText(getResources().getString(R.string.rupee)+" "+totalQueue);
            if(totalBalance<50){
                redeemBTN.setEnabled(false);
            }else{
                redeemBTN.setEnabled(true);
            }
            if(userModel.getBalance().getCredited().equals("0")){
                creditCard.setVisibility(View.GONE);
            }else{
                creditCard.setVisibility(View.VISIBLE);
            }

            if(userModel.getBalance().getInQueue().equals("0")){
                requestCard.setVisibility(View.GONE);
            }else{
                requestCard.setVisibility(View.VISIBLE);
            }
        }

    }

    public void backToTask(View view) {
        finish();
    }
    Progress progress;

    public void redeem(View view) {
        if(paymentLayout.getVisibility()==View.GONE){
            paymentLayout.setVisibility(View.VISIBLE);
            checkRedeem();
        }else{
            progress=new Progress(this);
            progress.setTitle("Payment");
            progress.setMessage("Processing...");
            String payment="";
            switch (paymentMode){
                case PAYTM: payment="Paytm";
                break;
                case PHONEPE: payment="PhonePe";
                    break;
                case DEBIT: payment="Debit";
                    break;
            }
            String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            int amountLeft=totalBalance-Integer.parseInt(amount);
            userModel.getBalance().setBalance(amountLeft+"");
            int a= Integer.parseInt(userModel.getBalance().getInQueue());
            a+=Integer.parseInt(amount);
            userModel.getBalance().setInQueue(a+"");
            progress.show();
            uploadData();


            PaymentRequestModel requestModel=new PaymentRequestModel(firebaseUser.getUid(),amount,date,payment,number);
            if(paymentRequestModelList!=null){
            paymentRequestModelList.add(0,requestModel);
        }
        }
    }

    private void uploadData() {
        new FireBase().getReferenceUsers().child(firebaseUser.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    uploadRequest();
                }else{
                    progress.dismiss();
                }
            }
        });
    }

    private void uploadRequest() {
        if(paymentRequestModelList==null){
            Toast.makeText(this, "somethng wrong", Toast.LENGTH_SHORT).show();
            return;
        }

                new FireBase().getReferencePaymentQueueHistory().child(firebaseUser.getUid()).setValue(paymentRequestModelList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progress.dismiss();
                            Toast.makeText(WalletActivity.this, "Your amount will be credited within 24hr!", Toast.LENGTH_LONG).show();
                        }else{
                            progress.dismiss();
                        }
                        paymentLayout.setVisibility(View.GONE);
                    }
                });

    }

    public void checkRedeem(){
        if(paymentMode!=-1 && !amount.isEmpty() && number.length()==10){
            if(Integer.parseInt(amount)<=Integer.parseInt(userModel.getBalance().getBalance()) &&
                    Integer.parseInt(amount)>0
            ){
                redeemBTN.setEnabled(true);
            }else{
                redeemBTN.setEnabled(false);
            }

        }else{
            redeemBTN.setEnabled(false);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.paytm:paymentMode=PAYTM;
                break;
            case R.id.phonepe:paymentMode=PHONEPE;
                break;

        }
        checkRedeem();

    }

    public void showQueueHistory(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.history_layout);
        RecyclerView recyclerView=bottomSheetDialog.findViewById(R.id.historyRV);
        QueueHistoryAdapter queueHistoryAdapter=new QueueHistoryAdapter(this,paymentRequestModelList);
        recyclerView.setAdapter(queueHistoryAdapter);
        TextView titleTV=bottomSheetDialog.findViewById(R.id.titleTV);
        titleTV.setText("Queue History");
        FloatingActionButton closeBTN=bottomSheetDialog.findViewById(R.id.closeFAB);
        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();

    }


    public void addMoney(View view) {
        Intent intent=new Intent(this, PaytmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type","wallet");
        intent.putExtras(bundle);
        startActivity(intent);
    }
}