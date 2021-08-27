package com.nsa.labelimages.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nsa.labelimages.Model.PaymentRequestModel;
import com.nsa.labelimages.Model.StorageFileModel;
import com.nsa.labelimages.R;

import java.util.List;


public class QueueHistoryAdapter extends RecyclerView.Adapter<QueueHistoryAdapter.ViewHolder> {
    Context context;
   List<PaymentRequestModel> paymentRequestModelList;

    public QueueHistoryAdapter(Context context, List<PaymentRequestModel> paymentRequestModelList) {
        this.context = context;
        this.paymentRequestModelList = paymentRequestModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);

        // Passing view to ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("tag","item set");
       PaymentRequestModel model=paymentRequestModelList.get(position);
       holder.amountTV.setText(context.getString(R.string.rupee)+" "+model.getAmount());
        holder.date1TV.setText(model.getDate());
        holder.numberTV.setText("Number : "+model.getNumber());
        holder.typeTV.setText("Payment Mode : "+model.getPaymentBy());
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return paymentRequestModelList.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView amountTV,date1TV,date2TV,numberTV,typeTV;



        public ViewHolder(View view) {
            super(view);

            amountTV=view.findViewById(R.id.amountTV);
            date1TV =  view.findViewById(R.id.dateTV1);
            date2TV=view.findViewById(R.id.dateTV2);
            date2TV.setVisibility(View.GONE);
            numberTV =  view.findViewById(R.id.numberTV);
            typeTV =  view.findViewById(R.id.paymentPath);
        }
    }
}