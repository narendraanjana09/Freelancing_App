package com.nsa.labelimages.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nsa.labelimages.Model.File;
import com.nsa.labelimages.Model.StorageFileModel;
import com.nsa.labelimages.R;

import java.util.List;


public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {
    List<File> filesList;
    Context context;

    public FilesAdapter(List<File> filesList, Context context) {
        this.filesList = filesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(context).inflate(R.layout.files_item, parent, false);

        // Passing view to ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name="<u>"+filesList.get(position).getFile_name()+"</u>";
        String link=filesList.get(position).getLink();

        holder.text.setText((position+1)+". "+ Html.fromHtml(name));
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
        return filesList.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        RelativeLayout mainLayout;


        public ViewHolder(View view) {
            super(view);

            mainLayout=view.findViewById(R.id.fileLayout);
            text =  view.findViewById(R.id.fileNameTV);
        }
    }
}