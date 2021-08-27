package com.nsa.labelimages.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nsa.labelimages.Interfaces.ImageClick;
import com.nsa.labelimages.Interfaces.ImageDownloadListener;
import com.nsa.labelimages.Interfaces.UserClick;
import com.nsa.labelimages.Model.ApplicationModel;
import com.nsa.labelimages.Model.ImagesModel;
import com.nsa.labelimages.Model.RealImagesModel;
import com.nsa.labelimages.Model.UserModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.maineditor.DownloadImage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.nsa.labelimages.activities.TaskActivity.current_task_model;
import static com.nsa.labelimages.activities.TaskActivity.userModel;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    Context context;
    List<ImagesModel> imagesList;
    ImageClick imageClick;

    public ImagesAdapter(Context context, List<ImagesModel> imagesList, ImageClick imageClick) {
        this.context = context;
        this.imagesList = imagesList;
        this.imageClick = imageClick;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_layout, parent, false);
        return new ImagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ImagesModel model=imagesList.get(position);

            ImageDownloadListener imageDownloadListener=new ImageDownloadListener() {
                @Override
                public void onImageDownloaded(Bitmap bitmap) {
                    imagesList.get(position).setImgBitmap(bitmap);
                    holder.imageView.setImageBitmap(bitmap);
                    setImage(bitmap,holder.imageView);
                }

                @Override
                public void onImageDownloadError() {
                    Log.e("Image Download", "onImageDownloadError: " );
                }
            };
            DownloadImage downloadImage=new DownloadImage(imageDownloadListener);
            downloadImage.execute(Uri.parse(model.getImageLink()));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imagesList.get(position).getImgBitmap()==null){

                    }else{
                    imageClick.onClick(imagesList.get(position));
                }}
            });
        }
        public void setImage(Bitmap imageBitmap,ImageView imageView){
            int h=imageBitmap.getHeight();
            int w=imageBitmap.getWidth();

            float aspect_ratio = (float)w / (float)h ;

            CardView.LayoutParams layoutParams;
            if(aspect_ratio>1){
                layoutParams=new CardView.LayoutParams(350,250);
            }else{
                layoutParams=new CardView.LayoutParams(250,350);
            }
            layoutParams.setMargins(5,5,5,5);
            imageView.setLayoutParams(layoutParams);
        }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardBTN;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            cardBTN=itemView.findViewById(R.id.cardBTN);
        }
    }
}
