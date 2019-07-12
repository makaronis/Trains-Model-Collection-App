package com.makaroni.chocho;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;


public class EditorImageView extends android.support.v7.widget.AppCompatImageView {

    public EditorImageView(Context context) {
        super(context);
    }

    public EditorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            Drawable drawable = getDrawable();

            if (drawable == null) {
                setMeasuredDimension(0, 0);
            } else {
                float imageSideRatio = (float)drawable.getIntrinsicWidth() / (float)drawable.getIntrinsicHeight();
                float viewSideRatio = (float)MeasureSpec.getSize(widthMeasureSpec) / (float)MeasureSpec.getSize(heightMeasureSpec);
                if (imageSideRatio >= viewSideRatio) {
                    // Image is wider than the display (ratio)
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = (int)(width / imageSideRatio);
                    setMeasuredDimension(width, height);
                } else {
                    // Image is taller than the display (ratio)
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    int width = (int)(height * imageSideRatio);
                    setMeasuredDimension(width, height);
                }
            }
        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}