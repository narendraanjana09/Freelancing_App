package com.nsa.flexjobs.Adapters;

import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nsa.flexjobs.Model.ApplicationModel;
import com.nsa.flexjobs.R;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ApplicationModel model=applicationModelList.get(position);
        String htmlString="<u>"+model.getTask_name()+"</u>";
        holder.nameTV.setText(Html.fromHtml(htmlString));
        int color=context.getColor(R.color.red1);
        if(model.getTask_status().equals("2")){
             color=context.getColor(R.color.green);
            holder.statusTV.setText("Viewed");
        }else if(model.getTask_status().equals("1")){
            color=context.getColor(R.color.teal_200);
            holder.statusTV.setText("You're a good fit\nUser will soon message you!");
        }else if(model.getTask_status().equals("3")){
            color=context.getColor(R.color.red);
            holder.statusTV.setText("You're not a good fit");
        }else if(model.getTask_status().equals("0")){
            color=context.getColor(R.color.white);
            holder.statusTV.setText("Applied");
        }
        holder.statusTV.setTextColor(color);
        holder.statusTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        holder.postedDateTV.setText("Posted Date: "+model.getTask_post_date());
        holder.appliedDateTV.setText("Applied Date: "+model.getAppli_date());
        holder.valueTV.setText("reward: "+context.getString(R.string.rupee)+" "+model.getTask_value()+"/image");

        holder.taskDesTV.setText("Task Description :-\n"+model.getTask_des());

        holder.cardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                        if(holder.taskDesTV.getVisibility()==View.GONE){
                            holder.taskDesTV.setVisibility(View.VISIBLE);
                        }else{
                            holder.taskDesTV.setVisibility(View.GONE);
                        }
            }
        });



    }

    @Override
    public int getItemCount() {
        return applicationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV,valueTV,postedDateTV,appliedDateTV,statusTV,taskDesTV;
        CardView cardBTN;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV=itemView.findViewById(R.id.taskNameTV);
            valueTV=itemView.findViewById(R.id.taskValueTV);
            postedDateTV=itemView.findViewById(R.id.postedDateTV);
            cardBTN=itemView.findViewById(R.id.infoCard);
            statusTV=itemView.findViewById(R.id.taskStatusTV);
            appliedDateTV=itemView.findViewById(R.id.appliedDateTV);
            taskDesTV=itemView.findViewById(R.id.taskDesTV);

        }
    }
}
