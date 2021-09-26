package com.nsa.flexjobs.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nsa.flexjobs.Interfaces.TaskClick;
import com.nsa.flexjobs.Model.TaskModel;
import com.nsa.flexjobs.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyTaskAdaper extends RecyclerView.Adapter<MyTaskAdaper.ViewHolder> {

    Context context;
    List<TaskModel> taskModelList;
    TaskClick taskClick;

    public MyTaskAdaper(Context context, List<TaskModel> taskModelList, TaskClick taskClick) {
        this.context = context;
        this.taskModelList = taskModelList;
        this.taskClick = taskClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new MyTaskAdaper.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        TaskModel model=taskModelList.get(position);
        String htmlString="<u>"+model.getTask_name()+"</u>";
        holder.taskNameTv.setText(Html.fromHtml(htmlString));
        holder.userNameTv.setText(model.getUser_name());

        Glide.with(context)
                .load(model.getPrev_img_link())
                .thumbnail()
                .into(holder.prevImageView);


        holder.dateTV.setText(model.getPosted_date());
        if(model.getTask_type().equals("Annotation")) {
            holder.valueTV.setText(context.getString(R.string.rupee) + " " + Float.parseFloat(model.getTask_value()) + "/image");
        }else{
            holder.valueTV.setText(context.getString(R.string.rupee) + " " + Float.parseFloat(model.getTask_value()));
        }
        holder.cardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                taskClick.onClick(model);

            }
        });

    }

    @Override
    public int getItemCount() {
        return taskModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTv,userNameTv,valueTV,dateTV;
        CardView cardBTN;
        ImageView prevImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskNameTv =itemView.findViewById(R.id.taskNameTV);
            userNameTv=itemView.findViewById(R.id.userNameTV);
            prevImageView=itemView.findViewById(R.id.prevImageView);
            valueTV=itemView.findViewById(R.id.taskValueTV);
            dateTV=itemView.findViewById(R.id.postedDateTV);
            cardBTN=itemView.findViewById(R.id.infoCard);

        }
    }
}
