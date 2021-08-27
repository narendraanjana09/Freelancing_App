package com.nsa.labelimages.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nsa.labelimages.Model.CreditModel;
import com.nsa.labelimages.Model.PaymentRequestModel;
import com.nsa.labelimages.R;

import java.util.List;


public class CreditHistoryAdapter extends RecyclerView.Adapter<CreditHistoryAdapter.ViewHolder> {
    Context context;
    List<CreditModel> creditModelList;

    public CreditHistoryAdapter(Context context, List<CreditModel> creditModelList) {
        this.context = context;
        this.creditModelList = creditModelList;
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
       CreditModel model=creditModelList.get(position);
       holder.amountTV.setText(context.getString(R.string.rupee)+" "+model.getAmount());
        holder.date1TV.setText(model.getDate());
        holder.referenceID.setText("refernece : "+model.getPaymetRef());
        holder.numberTV.setText("Number : "+model.getNumber());
        holder.typeTV.setText("Payment Mode : "+model.getPaymentBy());
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return creditModelList.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView amountTV,date1TV, referenceID,numberTV,typeTV;



        public ViewHolder(View view) {
            super(view);

            amountTV=view.findViewById(R.id.amountTV);
            date1TV =  view.findViewById(R.id.dateTV1);
            referenceID =view.findViewById(R.id.dateTV2);
            numberTV =  view.findViewById(R.id.numberTV);
            typeTV =  view.findViewById(R.id.paymentPath);
        }
    }
}