package com.nsa.flexjobs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nsa.flexjobs.Interfaces.UserClick;
import com.nsa.flexjobs.Model.ApplicationModel;
import com.nsa.flexjobs.Model.UserModel;
import com.nsa.flexjobs.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.nsa.flexjobs.activities.TaskActivity.current_task_model;
import static com.nsa.flexjobs.activities.TaskActivity.userModel;

public class TestVeriUsersAdapter extends RecyclerView.Adapter<TestVeriUsersAdapter.ViewHolder> {
    Context context;
    List<UserModel> usersList;
    UserClick userClick;

    public TestVeriUsersAdapter(Context context, List<UserModel> usersList, UserClick userClick) {
        this.context = context;
        this.usersList = usersList;
        this.userClick = userClick;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_veri_users_item, parent, false);
        return new TestVeriUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        UserModel model=usersList.get(position);
        holder.nameTV.setText(model.getName());


        holder.fabOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             userClick.onUserClick(model,holder.dateTV.getText().toString());
            }
        });
        if(userModel.getApplicaModelList()==null){
            return;
        }

        for(ApplicationModel model1:model.getApplicaModelList()){
            if(model1.getTask_id()==null){
                continue;
            }


            if(model1.getTask_id().equals(current_task_model.getTask_id())){
                holder.dateTV.setText(model1.getAppli_date());
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV,dateTV;
        FloatingActionButton fabOpen;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nameTV=itemView.findViewById(R.id.nameTV);
            dateTV=itemView.findViewById(R.id.dateTV);
            fabOpen=itemView.findViewById(R.id.openBTN);
        }
    }
}
