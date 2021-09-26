package com.nsa.flexjobs.testeditor;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;


import com.nsa.flexjobs.R;

import static com.nsa.flexjobs.testeditor.TestAnnotateActivity.currentModel;
import static com.nsa.flexjobs.testeditor.TestAnnotateActivity.hwTv;
import static com.nsa.flexjobs.testeditor.TestAnnotateActivity.xyTv;


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
        int X=(int) TestAnnotateActivity.added_view.getX();
        int Y=(int) TestAnnotateActivity.added_view.getY();
       if(X >=0 && Y>=0) {
           xyTv.setText("x:"+X+",y:"+Y);
           if (h < TestAnnotateActivity.IMAGE_HEIGHT - 120 && h>30) {
               imgRect.getLayoutParams().height = h;
               hwTv.setText("w:" + w + ",h:" + h);
           }
           if (w < TestAnnotateActivity.IMAGE_WIDTH - 120 && w>30) {

               imgRect.getLayoutParams().width = w;
               hwTv.setText("w:" + w + ",h:" + h);

           }
           imgRect.requestLayout();
       }




        currentModel.setHeight(imgRect.getLayoutParams().height+"");
        currentModel.setWidth(imgRect.getLayoutParams().width+"");

        return true;


        }




}

