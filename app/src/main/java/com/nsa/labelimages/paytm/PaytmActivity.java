package com.nsa.labelimages.paytm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.nsa.labelimages.Adapters.TransactionAdapter;
import com.nsa.labelimages.Extra.FireBase;
import com.nsa.labelimages.Model.TransactionModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.service.Notification;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static com.nsa.labelimages.activities.TaskActivity.context;
import static com.nsa.labelimages.activities.TaskActivity.firebaseUser;

import static com.nsa.labelimages.activities.TaskActivity.paymentDone;
import static com.nsa.labelimages.activities.TaskActivity.paymentListener;
import static com.nsa.labelimages.activities.TaskActivity.userModel;


public class PaytmActivity extends AppCompatActivity implements PaymentResultListener {

    ProgressBar progressBar;
    String orderIdString;
    ExtendedFloatingActionButton btnPayNow;
    int ActivityRequestCode = 102;
    EditText amountED,promoED;
    TextView infoTV,historyTv;
    RecyclerView historyRV;
    RequestQueue requestQueue;
    float totalAmount=0.0f;
    float charge=10;
    FloatLabeledEditText promoLayout;
    boolean isPostTask=false;
    String rupee="";
    TransactionAdapter adapter;

    String razorLiveKey="rzp_live_J5k4srJWGwPIZs";
    String razortestKey="rzp_test_pB4h72bkAh7jHv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm);
        Checkout.preload(getApplicationContext());
        progressBar =  findViewById(R.id.progressBar);
        btnPayNow =  findViewById(R.id.txnProcessBtn);
        amountED = findViewById(R.id.amountEditText);
        promoED=findViewById(R.id.promoED);
        promoLayout=findViewById(R.id.promoLayout);
        historyTv=findViewById(R.id.historyTV);
        historyRV=findViewById(R.id.addMoneyHistory);
        infoTV=findViewById(R.id.infoTV);
        rupee=getResources().getString(R.string.rupee);
        requestQueue= Volley.newRequestQueue(this);
        Bundle bundle = getIntent().getExtras();

        if(userModel.getTransactionModelList()==null || userModel.getTransactionModelList().size()==0){
            historyTv.setText("No Previous Transactions");
        }else{
            historyTv.setText("Transaction History");
           adapter=new TransactionAdapter(this);
            historyRV.setAdapter(adapter);
        }

        if (bundle != null) {
            String type = bundle.getString("type");
            if(type.equals("wallet")){
                isPostTask=false;
            }else{
                isPostTask=true;
                promoLayout.setVisibility(View.VISIBLE);
                int size=bundle.getInt("size");
                Float value=bundle.getFloat("value");
                totalAmount=value*size;

                DecimalFormat df = new DecimalFormat("#.#");
                totalAmount= Float.parseFloat(df.format(totalAmount));

                infoTV.setText("Total Images: "+size);
                infoTV.append("\nValue/image: "+rupee+" "+value);
                infoTV.append("\nTotel:"+rupee+" "+totalAmount);
                infoTV.append("\nOur Charges: "+rupee+" 10/post");
                totalAmount+=charge;
                totalAmount= (float) Math.ceil(totalAmount);
                infoTV.append("\nAmount to Pay: "+rupee+" "+totalAmount);

                infoTV.setGravity(Gravity.END);
                String amt= String.valueOf(totalAmount);
                amountED.setText("");
                amountED.setText(amt);
                amountED.setEnabled(false);
            }


        }
        promoED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){

                }else if(s.toString().equals("PROMO")){
                    Toast.makeText(PaytmActivity.this, "Payment SuccessFull", Toast.LENGTH_SHORT).show();
                    paymentDone=true;
                    paymentListener.statusChanged();
                    String date=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                    if(isPostTask){
                        transactionDone("TXN_SUCCESS",totalAmount+"",date,"promotional",infoTV.getText().toString());

                    }else{
                        transactionDone("TXN_SUCCESS",totalAmount+"",date,"promotional","amount added to wallet");
                    }

                }
            }
        });
        //     Bundle[{STATUS=TXN_SUCCESS, CHECKSUMHASH=u3mLrFRr7OJrYTPArRZ44OHrwk0zDi1ucqSpO8FB+WhYRxKLWdYWQ/G0IbL4Kcr3+n63BYoyS7nlIq392R5Hm/8hfytAuvC6HAmbbFcUqOA=,
        //     BANKNAME=ALH, ORDERID=NSAID1627212617511, TXNAMOUNT=10.00, TXNDATE=2021-07-25 17:00:21.0,
        //     MID=YCUOPc54396720201158, TXNID=20210725111212800110168759602829725, RESPCODE=01,
        //     PAYMENTMODE=DC, BANKTXNID=777001913334977, CURRENCY=INR, GATEWAYNAME=HDFC, RESPMSG=Txn Success}]




    }
    String TAG="paytm";
    String callback_url=Constants.CALLBACK_URL_LIVE;


    public void paytmPay(View view) {
        if(amountED.getText().toString().isEmpty()){
            Toast.makeText(this, "Enter Amount First", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        getToken();

    }


    private void getToken() {

        orderIdString="NSAID"+System.currentTimeMillis();
        callback_url+=orderIdString;

        StringRequest
                stringRequest
                = new StringRequest(
                Request.Method.GET,
                getLink(amountED.getText().toString(),orderIdString), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){

                    try {
                        Log.e(TAG,"response = "+response);
                        progressBar.setVisibility(View.GONE);
                        processPaytmTransaction(new JSONObject(response));
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }


                }else{
                    progressBar.setVisibility(View.GONE);
                     Log.e(TAG,"token null");
                     Toast.makeText(PaytmActivity.this, "token null", Toast.LENGTH_SHORT).show();
                 }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"error "+error);
                Toast.makeText(PaytmActivity.this, "error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });

        requestQueue.add(stringRequest);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode && data != null) {
            String nsdk = data.getStringExtra("nativeSdkForMerchantMessage");
            String response = data.getStringExtra("response");
            Log.e(TAG,"response ="+response+"\nnsdk ="+nsdk);
            Toast.makeText(this, nsdk + response, Toast.LENGTH_SHORT).show();
        }
    }
    private void processPaytmTransaction(JSONObject data) {
        try {
            Log.i("CHECKSUM", data.getJSONObject("body").toString());
            Log.i("CHECKSUM", data.getJSONObject("head").getString("signature"));
            Log.e("TXN_TOKEN", data.getJSONObject("body").getString("txnToken"));

          int amount=(int)Float.parseFloat(amountED.getText().toString());
            PaytmOrder paytmOrder = new PaytmOrder(orderIdString, Constants.MERCHANT_ID_LIVE, data.getJSONObject("body").getString("txnToken"),
                   amount+"", callback_url);
            TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
                @Override
                public void onTransactionResponse(Bundle bundle) {
                    if(bundle==null){
                        return;
                    }
                    String status=bundle.getString("STATUS");
                    String amount=bundle.getString("TXNAMOUNT");
                    String date=bundle.getString("TXNDATE");
                    String txn_id=bundle.getString("TXNID");
                    String transactionMessage=bundle.getString("RESPMSG");
                    Log.e(TAG,"status = "+status+
                                 "\namount = "+amount+
                                   "\ndate = "+date+
                            "\ntxn_id = "+txn_id+
                            "\nmessage = "+transactionMessage);


                    transactionDone(status+" by Paytm", amountED.getText().toString(), date, txn_id, transactionMessage);

                }
                // Success
                //     Bundle[{STATUS=TXN_SUCCESS, CHECKSUMHASH=u3mLrFRr7OJrYTPArRZ44OHrwk0zDi1ucqSpO8FB+WhYRxKLWdYWQ/G0IbL4Kcr3+n63BYoyS7nlIq392R5Hm/8hfytAuvC6HAmbbFcUqOA=,
                //     BANKNAME=ALH, ORDERID=NSAID1627212617511, TXNAMOUNT=10.00, TXNDATE=2021-07-25 17:00:21.0,
                //     MID=YCUOPc54396720201158, TXNID=20210725111212800110168759602829725, RESPCODE=01,
                //     PAYMENTMODE=DC, BANKTXNID=777001913334977, CURRENCY=INR, GATEWAYNAME=HDFC, RESPMSG=Txn Success}]

                //Failure Bundle[{STATUS=TXN_FAILURE, CHECKSUMHASH=TgeXWqQnEis1aKPS/ugvq7YcpBASy9Uo1pVCMsp7WXpFclUtLdxLzvBXyKYHHDwZU3ymt7KMRH7xukTBG6Iw5e3UZq5L2i2B2ZQFBZ+08Vw=,
                // BANKNAME=ALH, ORDERID=NSAID1627213417102, TXNAMOUNT=10.00, TXNDATE=2021-07-25 17:13:41.0,
                // MID=YCUOPc54396720201158, TXNID=20210725111212800110168168202843299, errorCode=227, RESPCODE=227,
                // PAYMENTMODE=DC, retryAllowed=false, BANKTXNID=,
                // errorMessage=Your payment has failed due to a technical error. Please try again or use a different payment method to complete the payment,
                // CURRENCY=INR,
                // GATEWAYNAME=HDFC,
                // RESPMSG=Your payment has failed due to a technical error. Please try again or use a different payment method to complete the payment}]




                @Override
                public void networkNotAvailable() {
                    Log.e("RESPONSE", "network not available");
                }

                @Override
                public void onErrorProceed(String s) {
                    Log.e("RESPONSE", "error proceed: " + s);

                }

                @Override
                public void clientAuthenticationFailed(String s) {
                    Log.e("RESPONSE", "client auth failed: " + s);

                }

                @Override
                public void someUIErrorOccurred(String s) {
                    Log.e("RESPONSE", "UI error occured: " + s);

                }

                @Override
                public void onErrorLoadingWebPage(int i, String s, String s1) {
                    Log.e("RESPONSE", "error loading webpage: " + s + "--" + s1);

                }

                @Override
                public void onBackPressedCancelTransaction() {
                    Log.e("RESPONSE", "back pressed");

                }

                @Override
                public void onTransactionCancel(String s, Bundle bundle) {
                    Log.e("RESPONSE", "transaction cancel: " + s);

                }
            });

            transactionManager.setShowPaymentUrl("https://securegw.paytm.in/theia/api/v1/showPaymentPage");
            transactionManager.startTransaction(PaytmActivity.this, ActivityRequestCode);
        } catch (Exception e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void transactionDone(String status, String amount, String date, String txn_id, String transactionMessage) {
        if(status.contains("TXN_SUCCESS")) {

            int balance = Integer.parseInt(userModel.getBalance().getBalance());
            int paidAmount = (int) Float.parseFloat(amount);
            balance += paidAmount;
            userModel.getBalance().setBalance(balance + "");

        }
        TransactionModel transactionModel;
        if(isPostTask){
            transactionModel =new TransactionModel(status, amount, date, txn_id, transactionMessage,infoTV.getText().toString());
        }else{
            transactionModel =new TransactionModel(status, amount, date, txn_id, transactionMessage,"amount added to wallet");

        }
        infoTV.setText(status +"\n"+ amount +"\n"+ transactionMessage);
        if(userModel.getTransactionModelList()==null){
            List<TransactionModel> transactionModelList=new ArrayList<>();
            transactionModelList.add(0,transactionModel);
            userModel.setTransactionModelList(transactionModelList);
        }else{
            userModel.getTransactionModelList().add(0,transactionModel);
        }
        new FireBase().getReferenceUsers().child(firebaseUser.getUid()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Notification.showNotification(status,"Amount Paid :- "+amount,firebaseUser.getUid(),PaytmActivity.this);
            }
        });
        if(adapter==null){
            adapter=new TransactionAdapter(this);
            historyRV.setAdapter(adapter);
        }else{
            adapter.notifyItemInserted(userModel.getTransactionModelList().size()-1);
        }
        amountED.setText("");
        setDoneDailog(transactionModel);
    }

    private void setDoneDailog(TransactionModel model) {

        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
             // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.payment_success_layout,
                        null);
        builder.setView(customLayout);
        builder.setCancelable(false);
        AlertDialog dialog
                = builder.create();


        TextView amoutTV =customLayout.findViewById(R.id.amountPaidTV);
        TextView txnIDTV =customLayout.findViewById(R.id.txnIDTV);

        amoutTV.setText("Amount Paid :- "+rupee+" "+model.getAmount());
        txnIDTV.setText("Transaction ID :- "+model.getTransactionID());

        ExtendedFloatingActionButton shareBTN,okayBTN;
        shareBTN=customLayout.findViewById(R.id.shareBTN);

        shareBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text="Transaction Receipt from "+getResources().getString(R.string.app_name)+"\n"+
                        amoutTV.getText().toString()+"\n"+txnIDTV.getText().toString();
                text+="\nStatus :- "+model.getStatus()+"\nNote :- "+model.getTransactionNote()+"\nMessage :- "
                        +model.getTransactionmessage();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, "Share Transaction Data");
                startActivity(shareIntent);
            }
        });

        okayBTN=customLayout.findViewById(R.id.okayBTN);
        okayBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPostTask){
                    paymentDone=true;
                    paymentListener.statusChanged();
                    dialog.dismiss();
                    finish();
                }else{
                    dialog.dismiss();
                }
            }
        });
        dialog.show();

    }

    public String getLink(String amount, String orderId){
        return "https://lableimages.000webhostapp.com/?amount="+amount+"&customer_id="+firebaseUser.getUid()+"&order_id="+orderId;
    }


    public void back(View view) {
        finish();
    }

    public void razorPay(View view) {
        if(amountED.getText().toString().isEmpty()){
            Toast.makeText(this, "Enter Amount First", Toast.LENGTH_SHORT).show();
            return;
        }
        int amount=(int)Float.parseFloat(amountED.getText().toString());
        Toast.makeText(this, ""+amount, Toast.LENGTH_SHORT).show();
        amount*=100;
        Checkout checkout=new Checkout();
        checkout.setKeyID(razorLiveKey);
        checkout.setImage(R.drawable.razor);
        JSONObject object=new JSONObject();
        try{
            object.put("name","Annotate Images");
            object.put("description","Live Payment");
            object.put("theme.color","292929");
            object.put("currency","INR");
            object.put("amount",amount+"" );
            if(userModel.getPhoneModel()!=null) {
                object.put("prefill.contact", userModel.getPhoneModel().getNumber());
                object.put("prefill.email", userModel.getEmail());
            }
            checkout.open(PaytmActivity.this,object);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.e(TAG,s);
        String date=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        if(isPostTask){
            transactionDone("TXN_SUCCESS"+" by RazorPay",amountED.getText().toString(),date,s,infoTV.getText().toString());
        }else{
            transactionDone("TXN_SUCCESS"+" by RazorPay",amountED.getText().toString(),date,s,"Amount Added To Wallet By RazorPay");
        }
        Toast.makeText(this, "Payment Is Success", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPaymentError(int i, String s) {
         Log.e(TAG,s);
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }
}