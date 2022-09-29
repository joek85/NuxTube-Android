package com.joek.nuxtube.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class SizeChangeAnimation extends Animation {
    private int startHeight;
    private int deltaHeight;

    private int startWidth;
    private int deltaWidth;

    private Boolean withScale = false;

    private float x;
    private View view;
    private float offset;
    private int end;

    public SizeChangeAnimation(View view) {
        this.view = view;
    }

    public void setX(float x) {
        this.x = x;
    }
    public void withWidthScale(Boolean widthScale){
        this.withScale = widthScale;
    }

    public void setHeights(int start, int end) {
        this.startHeight = start;
        this.deltaHeight = end - this.startHeight;
    }

    public void setWidths(int start, int end) {
        this.startWidth = start;
        this.deltaWidth = end - this.startWidth;
    }

    public void setWidths(int start, int end, float offset) {
        this.startWidth = start;
        this.deltaWidth = end - this.startWidth;
        this.offset = offset;
        this.end = end;
    }
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        //if (startHeight != 0) {
            if (deltaHeight > 0) {
                view.getLayoutParams().height = (int) (deltaHeight * interpolatedTime);
//            } else {
//                view.getLayoutParams().height = (int) (startHeight - Math.abs(deltaHeight) * interpolatedTime);
            }
       // }

        //if (startWidth != 0) {
            if (deltaWidth > 0) {
                if (withScale) {
                    if (deltaWidth > end * 0.8f){
                        view.getLayoutParams().width = (int) (end * interpolatedTime);

                    }else {
                        view.getLayoutParams().width = (int) (deltaWidth * interpolatedTime);

                    }
                }else {
                    view.getLayoutParams().width = (int) (deltaWidth * interpolatedTime);

                }
            //} else {
                //view.getLayoutParams().width = (int) (startWidth - Math.abs(deltaWidth) * interpolatedTime);
            }
        //}

        if (x != 0) {
            view.setX(x);
        }
        view.requestLayout();
    }
}
