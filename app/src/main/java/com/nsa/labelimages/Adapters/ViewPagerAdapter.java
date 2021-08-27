package com.nsa.labelimages.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;


import com.nsa.labelimages.Model.RealImagesModel;
import com.nsa.labelimages.R;
import com.nsa.labelimages.Extra.ImagesPager;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.nsa.labelimages.activities.ApplyActivity.realImagesList;


public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {


    static Context context;

    private LayoutInflater mInflater;
    List<RealImagesModel> list;

    ImagesPager pager;


      public ViewPagerAdapter(Context context, ImagesPager pager) {
          this.mInflater = LayoutInflater.from(context);
          this.context = context;
          list= realImagesList;

          this.pager=pager;


    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.images_pager_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Uri uri= Uri.parse(list.get(position).getLink());



            if(list.get(position).getEditedModel()!=null){

                holder.imageView.setImageBitmap(list.get(position).getEditedModel().getBitmap());

            }else{
                loadImage(holder.imageView,uri);
            }

            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pager.onEdit(list.get(position),position);
                }
            });


    }

    public static void loadImage(ImageView imageView, Uri uri){

        Picasso
                .get()
                .load(uri)
                .into(imageView);
    }
    @Override
    public int getItemCount() {

        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        ImageView editBtn;


        ViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageview);

            editBtn=itemView.findViewById(R.id.editBtn);



        }
    }

}
