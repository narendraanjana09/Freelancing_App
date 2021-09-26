package com.nsa.flexjobs.Adapters;

import static com.nsa.flexjobs.Message.MessageAdapter.convertTime;
import static com.nsa.flexjobs.activities.TaskActivity.firebaseUser;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nsa.flexjobs.Interfaces.UserClick;
import com.nsa.flexjobs.Message.Chat;
import com.nsa.flexjobs.Model.UserModel;
import com.nsa.flexjobs.R;
import com.nsa.flexjobs.activities.ChatsActivity;
import com.nsa.flexjobs.activities.MessageActivity;
import com.nsa.flexjobs.activities.TaskActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    Context context;
    List<UserModel> userModelList;
    ChatsActivity.click click;
    public ChatsAdapter(Context context, List<UserModel> userModelList, ChatsActivity.click click) {
        this.context = context;
        this.userModelList = userModelList;
        this.click=click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_user_item, parent, false);
        return new ChatsAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
       holder.setData(userModelList.get(position));

    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV,messageTV,countTV;
        CircleImageView prevImageView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countTV=itemView.findViewById(R.id.messageCntTV);
            nameTV =itemView.findViewById(R.id.nameTV);
            messageTV =itemView.findViewById(R.id.messageTV);
            messageTV.setText("");
            prevImageView =itemView.findViewById(R.id.imageView);
            cardView=itemView.findViewById(R.id.userCardTV);


        }

        public void setData(UserModel model) {
            nameTV.setText(model.getName());
            messageTV.setText(model.getEmail());
            Picasso.get().load(model.getImageLink()).into(prevImageView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.userclick(ViewHolder.this.getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
