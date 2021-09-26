package com.nsa.flexjobs.Adapters;

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


import com.nsa.flexjobs.Model.StorageFileModel;
import com.nsa.flexjobs.R;

import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    List<StorageFileModel> nameList;
    Context context;

    // Constructor for initialization
    public ListAdapter(Context context,List<StorageFileModel> nameList) {
        this.context = context;
        this.nameList = nameList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        // Passing view to ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("tag","item set");
        String name=nameList.get(position).getName();
        String link=nameList.get(position).getLink();
        holder.text.setText((position+1)+". "+name);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webpage = Uri.parse(link);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                context.startActivity(webIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return nameList.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        CardView mainLayout;


        public ViewHolder(View view) {
            super(view);

            mainLayout=view.findViewById(R.id.mainLayout);
            text =  view.findViewById(R.id.nameTV);
        }
    }
}