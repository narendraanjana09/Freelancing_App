package com.nsa.labelimages.testeditor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nsa.labelimages.Model.FormatModel;
import com.nsa.labelimages.R;


public class TestLabelView {
    Context context;
    RelativeLayout relativeLayout;


    public TestLabelView(Context context, RelativeLayout frameLayout) {
        this.context = context;
        this.relativeLayout =frameLayout;

    }
    public  void addText(String text,int color){
      View view= LayoutInflater.from(context).inflate(R.layout.test_label_layout,null,true);
      setupView(view,text,color);
    }


    private void setupView(View view, String text, int color) {
        RelativeLayout.LayoutParams layoutParams =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        TextView textView=view.findViewById(R.id.tvPhotoEditorText);
        textView.setTextColor(color);
        textView.setText(text);


        ImageView imgDrag=view.findViewById(R.id.imgPhotoEditorDrag);
        TestAnnotateActivity.imgDrag=imgDrag;


        ImageView imgRect=view.findViewById(R.id.imgPhotoEditorRect);


        TestAnnotateActivity.currentModel=new FormatModel(text,
                0+"",0+"",imgRect.getLayoutParams().height+"",imgRect.getLayoutParams().width+"");










        switch(color){
            case Color.RED:

                imgDrag.setImageDrawable(context.getResources().getDrawable(R.drawable.drag_red));
                imgRect.setImageDrawable(context.getResources().getDrawable(R.drawable.rectangle_red));
                break;
            case Color.BLACK:
                imgDrag.setImageDrawable(context.getResources().getDrawable(R.drawable.drag_black));
                imgRect.setImageDrawable(context.getResources().getDrawable(R.drawable.rectangle_black));
                break;
            default:

                imgDrag.setImageDrawable(context.getResources().getDrawable(R.drawable.drag_white));
                imgRect.setImageDrawable(context.getResources().getDrawable(R.drawable.rectangle_white));

        }

        addView(view);

    }

    public void removeView(View view){
        TestAnnotateActivity.added_view=null;
                relativeLayout.removeView(view);
    }

    private void addView(View view) {
         TestAnnotateActivity.added_view=view;
        relativeLayout.addView(view);

    }

}
