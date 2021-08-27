package com.nsa.labelimages.maineditor;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.nsa.labelimages.R;

import static com.nsa.labelimages.maineditor.MainAnnotateActivity.IMAGE_HEIGHT;
import static com.nsa.labelimages.maineditor.MainAnnotateActivity.IMAGE_WIDTH;
import static com.nsa.labelimages.maineditor.MainAnnotateActivity.added_view;
import static com.nsa.labelimages.maineditor.MainAnnotateActivity.currentModel;
import static com.nsa.labelimages.maineditor.MainAnnotateActivity.hwTV;
import static com.nsa.labelimages.maineditor.MainAnnotateActivity.xyTV;


public class MovableImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener {

    @Override
    public void setOnContextClickListener(@Nullable OnContextClickListener l) {
        Log.e("click","clicked");
        super.setOnContextClickListener(l);
    }

    public MovableImageView(Context context) {
        super(context);
        init();
        }

public MovableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        }

public MovableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        }

private void init() {
        setOnTouchListener(this);
        }


    @Override
public boolean onTouch(View view, MotionEvent motionEvent){

        FrameLayout viewParent = (FrameLayout)view.getRootView();


        ImageView imgRect=viewParent.findViewById(R.id.imgPhotoEditorRect);
       String Tag=imgRect.getTag()+"";

        float x=motionEvent.getX();
        float y=motionEvent.getY();
        int h=imgRect.getLayoutParams().height;
        int w=imgRect.getLayoutParams().width;
        h+=y;
        w+=x;
        int X=(int) added_view.getX();
        int Y=(int) added_view.getY();
       if(X >=0 && Y>=0) {
           xyTV.setText("x:"+X+",y:"+Y);
           if (h <IMAGE_HEIGHT - 100 && h>30) {
               imgRect.getLayoutParams().height = h;
               hwTV.setText("w:" + w + ",h:" + h);
           }
           if (w <IMAGE_WIDTH - 100 && w>30) {

               imgRect.getLayoutParams().width = w;
               hwTV.setText("w:" + w + ",h:" + h);

           }
           imgRect.requestLayout();
       }




        currentModel.setHeight(imgRect.getLayoutParams().height+"");
        currentModel.setWidth(imgRect.getLayoutParams().width+"");

        return true;


        }




}

