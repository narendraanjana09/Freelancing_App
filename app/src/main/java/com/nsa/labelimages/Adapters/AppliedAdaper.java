package com.nsa.labelimages.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nsa.labelimages.Model.ApplicationModel;
import com.nsa.labelimages.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AppliedAdaper extends RecyclerView.Adapter<AppliedAdaper.ViewHolder> {

    Context context;
    List<ApplicationModel> applicationModelList;
    public AppliedAdaper(List<ApplicationModel> applicationModelList, Context context) {
        this.context=context;
        this.applicationModelList=applicationModelList;

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.applied_layout, parent, false);
        return new AppliedAdaper.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ApplicationModel model=applicationModelList.get(position);
        String htmlString="<u>"+model.getTask_name()+"</u>";
        holder.nameTV.setText(Html.fromHtml(htmlString));
        if(model.getTask_status().equals("2")){
            holder.statusTV.setText("Task Viewed");
        }else if(model.getTask_status().equals("1")){
            holder.statusTV.setText("You're a good fit");
        }else if(model.getTask_status().equals("3")){
            holder.statusTV.setText("You're not a good fit");
        }


        holder.postedDateTV.setText("Posted Date: "+model.getTask_post_date());
        holder.appliedDateTV.setText("Applied Date: "+model.getAppli_date());
        holder.valueTV.setText("reward: "+context.getString(R.string.rupee)+" "+model.getTask_value()+"/image");



    }

    @Override
    public int getItemCount() {
        return applicationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV,valueTV,postedDateTV,appliedDateTV,statusTV;
        CardView cardBTN;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV=itemView.findViewById(R.id.taskNameTV);
            valueTV=itemView.findViewById(R.id.taskValueTV);
            postedDateTV=itemView.findViewById(R.id.postedDateTV);
            cardBTN=itemView.findViewById(R.id.infoCard);
            statusTV=itemView.findViewById(R.id.taskStatusTV);
            appliedDateTV=itemView.findViewById(R.id.appliedDateTV);

        }
    }
}
