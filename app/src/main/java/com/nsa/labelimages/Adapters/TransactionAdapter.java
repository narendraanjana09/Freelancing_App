package com.nsa.labelimages.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nsa.labelimages.Model.TransactionModel;
import com.nsa.labelimages.R;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import pl.droidsonroids.gif.GifImageView;

import static com.nsa.labelimages.activities.TaskActivity.userModel;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{

    Context context;
    public TransactionAdapter( Context context) {
        this.context=context;

    }
    @NonNull
    @NotNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_layout, parent, false);
        return new TransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TransactionAdapter.ViewHolder holder, int position) {
        if(userModel.getTransactionModelList()!=null){
        TransactionModel model=userModel.getTransactionModelList().get(position);
        String rupee=context.getString(R.string.rupee);
        holder.amountTV.setText(rupee+" "+model.getAmount());
        holder.dateTV.setText(model.getDate());
        if(model.getStatus().contains("TXN_SUCCESS")){

            holder.gifFailedView.setVisibility(View.GONE);
        }else{
            holder.gifSuccessView.setVisibility(View.GONE);

        }
        holder.cardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(context);
                // set the custom layout
                final View customLayout
                        = LayoutInflater.from(context)
                        .inflate(
                                R.layout.reciept_dialog,
                                null);
                TextView tv=customLayout.findViewById(R.id.recieptTV);
                String TransactionStatus="<u>Transaction Status:- </u>";
                String amount="<u>Amount:- </u>";
                String date="<u>Date:- </u>";
                String transactionMessage="<u>Transaction Message:- </u>";
                String transactionNote="<u>Transaction Note:- </u>";
                String transactionID="<u>Transaction ID:- </u>";
               // holder.taskNameTv.setText(Html.fromHtml(htmlString));
                tv.setText(Html.fromHtml(TransactionStatus)+model.getStatus()
                        +"\n\n"+Html.fromHtml(amount)+rupee+" "+model.getAmount()
                        +"\n\n"+Html.fromHtml(date)+model.getDate()
                        +"\n\n"+Html.fromHtml(transactionMessage)+model.getTransactionmessage()
                        +"\n\n" +Html.fromHtml(transactionNote)+model.getTransactionNote()
                        +"\n\n" +Html.fromHtml(transactionID)+model.getTransactionID());
                builder.setView(customLayout);
                builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text="Transaction Receipt from "+context.getString(R.string.app_name)+"\n\n"+
                                tv.getText().toString();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, "Share Transaction Data");
                        context.startActivity(shareIntent);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                AlertDialog dialog
                        = builder.create();
                dialog.show();
            }
        });
    }
    }

    @Override
    public int getItemCount() {
        if(userModel.getTransactionModelList()==null){
            return 0;
        }else{
        return userModel.getTransactionModelList().size();
    }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountTV,dateTV;
        CardView cardBTN;
        FloatingActionButton gifSuccessView,gifFailedView;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            amountTV=itemView.findViewById(R.id.amountTV);
            dateTV=itemView.findViewById(R.id.dateTV);
            cardBTN=itemView.findViewById(R.id.cardBTN);
            gifSuccessView =itemView.findViewById(R.id.gifSuccessView);
            gifFailedView =itemView.findViewById(R.id.gifFailedView);
        }
    }
}
