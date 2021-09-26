package com.nsa.flexjobs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nsa.flexjobs.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotificationsAdaper extends RecyclerView.Adapter<NotificationsAdaper.ViewHolder> {

    Context context;
    List<String> notificationList;

    public NotificationsAdaper(Context context, List<String> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationsAdaper.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
       holder.notiTV.setText(notificationList.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView notiTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notiTV=itemView.findViewById(R.id.notificationTextView);


        }
    }
}
