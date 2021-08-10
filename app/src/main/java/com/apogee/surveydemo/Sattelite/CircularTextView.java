package com.apogee.surveydemo.Sattelite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.apogee.surveydemo.R;
/*Circular textview for satellite positions*/
@SuppressLint("AppCompatCustomView")
public class CircularTextView extends androidx.appcompat.widget.AppCompatTextView {
    private ShapeDrawable backgroundDrawable;
    private OvalShape ovalShape;

    private int backgroundColor;

    public CircularTextView(Context context) {
        super(context);
        backgroundColor = ContextCompat.getColor(context, R.color.colorAccent);
        allocateShapes();
    }

    public CircularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundColor = ContextCompat.getColor(context, R.color.colorPrimary);
        allocateShapes();
    }

    public CircularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        backgroundColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        allocateShapes();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();
        int r = Math.max(w, h);

        setMeasuredDimension(r, r);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        backgroundDrawable.setShape(ovalShape);
        backgroundDrawable.getPaint().setColor(backgroundColor);

        setBackground(backgroundDrawable);
    }

    private void allocateShapes(){
        backgroundDrawable = new ShapeDrawable();
        ovalShape = new OvalShape();
    }

    public void setBackgroundColor(int color){
        backgroundColor = color;
        invalidate();
    }
}

