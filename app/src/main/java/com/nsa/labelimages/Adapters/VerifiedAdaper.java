package com.nsa.labelimages.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nsa.labelimages.Model.ApplicationModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.maineditor.MainAnnotateActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.nsa.labelimages.activities.TaskActivity.currentApplicationModel;

public class VerifiedAdaper extends RecyclerView.Adapter<VerifiedAdaper.ViewHolder> {

    Context context;
    List<ApplicationModel> taskModelList;
    public VerifiedAdaper(List<ApplicationModel> taskModelList, Context context) {
        this.context=context;
        this.taskModelList=taskModelList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.verified_layout, parent, false);
        return new VerifiedAdaper.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ApplicationModel model=taskModelList.get(position);
        String htmlString="<u>"+model.getTask_name()+"</u>";
        holder.nameTV.setText(Html.fromHtml(htmlString));
        holder.valueTV.setText("reward: "+context.getString(R.string.rupee)+" "+model.getTask_value()+"/image");
        holder.fabBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentApplicationModel=model;
               context.startActivity(new Intent(context, MainAnnotateActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return taskModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV,valueTV;
        CardView cardBTN;
        FloatingActionButton fabBTN;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV=itemView.findViewById(R.id.taskNameTV);
            valueTV=itemView.findViewById(R.id.taskValueTV);
            cardBTN=itemView.findViewById(R.id.infoCard);
            fabBTN=itemView.findViewById(R.id.openBTN);
        }
    }
}
