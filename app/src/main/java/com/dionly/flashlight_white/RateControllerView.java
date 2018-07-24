package com.dionly.flashlight_white;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.Image;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class RateControllerView extends HorizontalScrollView{


    HorizontalScrollView horizontalScrollView;
    int width,height;

    int perWidth;

    Bitmap bitmap;
    Paint paint;

    static final int SPAN = 2;

    public RateControllerView(Context context) {
        this(context, null);
    }


    public RateControllerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.rate_controller);
        perWidth = bitmap.getWidth();

        width = perWidth * 9 * SPAN;
        height = bitmap.getHeight();

        paint = new Paint();


        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < 9 * SPAN; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(bitmap);
            linearLayout.addView(imageView);
        }
        this.addView(linearLayout);

    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width,height);
    }

}
