package com.nsa.labelimages.Adapters;

import static com.nsa.labelimages.Message.MessageAdapter.convertTime;
import static com.nsa.labelimages.activities.TaskActivity.chatUsersList;
import static com.nsa.labelimages.activities.TaskActivity.chatsList;
import static com.nsa.labelimages.activities.TaskActivity.firebaseUser;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nsa.labelimages.Message.Chat;
import com.nsa.labelimages.Model.UserModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.activities.ChatsActivity;
import com.nsa.labelimages.activities.MessageActivity;
import com.nsa.labelimages.activities.TaskActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    Context context;

    public ChatsAdapter(Context context) {
        this.context=context;

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
        Pair<String, List<Chat>> pair=chatsList.get(position);
        for(UserModel model:chatUsersList){
            if(model.getId().equals(pair.second.get(0).getSender()) ||
                    model.getId().equals(pair.second.get(0).getReceiver())){
                holder.nameTV.setText(model.getName());
                holder.messageTV.setText(pair.second.get(pair.second.size()-1).getMessage()+"  "+
                        convertTime(pair.second.get(pair.second.size()-1).getTime()));
                Picasso.get().load(model.getImageLink()).into(holder.prevImageView);
                int count=0;
                for(Chat chat:pair.second){
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        count++;
                    }
                }
                if(count!=0) {
                    holder.countTV.setText("" + count);
                }
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TaskActivity.currentUserModel=model;
                        TaskActivity.currentChatAddress= pair.first;
                        context.startActivity(new Intent(context, MessageActivity.class));
                    }
                });

                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
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
            prevImageView =itemView.findViewById(R.id.imageView);
            cardView=itemView.findViewById(R.id.userCardTV);


        }
    }
}
