package com.dionly.flashlight_white;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SlideToggleViewVertical_orgin extends View {

    private Bitmap mCloseBg,mOpenBg;
    private Bitmap curBg;
    private Bitmap mSlideBitmap;
    private Paint mPaint;

    private int mBgWidth, mBgHeight;
    private int mSlideWidth, mSlideHeight;

    private boolean isViewIniting = true;

    private int state = -1;

    private float downX, downY;
    private boolean mToggleOpen;

    private float offsetX;

    public SlideToggleViewVertical_orgin(Context context) {
        this(context, null);
    }

    public SlideToggleViewVertical_orgin(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        loadBitmap();
    }

    private void loadBitmap() {
        mCloseBg = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_off_bg);
        mOpenBg = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_on_bg);
        mSlideBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_btn);

        curBg = mCloseBg;

        mBgWidth = mCloseBg.getWidth();
        mBgHeight = mCloseBg.getHeight();

        mSlideWidth = mSlideBitmap.getWidth();
        mSlideHeight = mSlideBitmap.getHeight();

        offsetX = (mBgWidth - mSlideWidth) / 2;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mBgWidth, mBgHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(curBg, 0, 0, mPaint);
        if (isViewIniting) {
            canvas.drawBitmap(mSlideBitmap, offsetX, 0, mPaint);
            isViewIniting = false;
        } else if (state == MotionEvent.ACTION_DOWN) {
            canvas.drawBitmap(mSlideBitmap, offsetX, downY-mSlideHeight/2, mPaint);
        } else if (state == MotionEvent.ACTION_MOVE) {
            canvas.drawBitmap(mSlideBitmap, offsetX, downY-mSlideHeight/2, mPaint);
        } else {
            canvas.drawBitmap(mSlideBitmap, offsetX, downY-mSlideHeight/2, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                if (downY > mBgHeight - mSlideHeight / 2) {
                    downY = mBgHeight - mSlideHeight / 2;
                }
                if (downY < mSlideHeight / 2) {
                    downY = mSlideHeight / 2;
                }
                state = MotionEvent.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                state = MotionEvent.ACTION_MOVE;
                float moveX = event.getX();
                float moveY = event.getY();

                downX = moveX;
                downY = moveY;

                if (downY > mBgHeight - mSlideHeight / 2) {
                    downY = mBgHeight - mSlideHeight;
                }
                if (downY < mSlideHeight / 2) {
                    downY = mSlideHeight / 2;
                }
                //改变按钮背景色
                if (downY <= mBgHeight / 2) {
                    curBg = mOpenBg;
                }else{
                    curBg = mCloseBg;
                }
                break;
            case MotionEvent.ACTION_UP:
                state = MotionEvent.ACTION_UP;
                float upX = event.getX();
                float upY = event.getY();

                downX = upX;
                downY = upY;

                if (downY > mBgHeight - mSlideHeight / 2) {
                    downY = mBgHeight - mSlideHeight / 2;
                }
                if (downY < mSlideHeight / 2) {
                    downY = mSlideHeight / 2;
                }
                if (downY <= mBgHeight / 2) {
                    downY = mSlideHeight / 2;

                    if (mToggleOpen) {
                        mToggleOpen = false;
                        if (mOnToggleChangeListener != null) {
                            mOnToggleChangeListener.setToggleState(mToggleOpen);
                        }
                    }

                }
                if (downY > mBgHeight / 2) {
                    downY = mBgHeight - mSlideHeight / 2;
                    if (!mToggleOpen) {
                        mToggleOpen = true;
                        if (mOnToggleChangeListener != null) {
                            mOnToggleChangeListener.setToggleState(mToggleOpen);
                        }
                    }
                }
                break;
                default:
                    break;

        }

        //触发重绘制
        invalidate();

        return true;
    }


    public interface OnToggleChangeListener{
        void setToggleState(boolean open);
    }

    public OnToggleChangeListener mOnToggleChangeListener;

    public void setmOnToggleChangeListener(OnToggleChangeListener onToggleChangeListener) {
        this.mOnToggleChangeListener = onToggleChangeListener;
    }

    public void setToggleState(boolean isOpen) {
        //此方法很可能先于onmeasure()和ondraw()方法执行（生命周期方法先于普通方法执行）
        this.mToggleOpen = isOpen;
        if (isOpen) {
            downY = mBgHeight - mSlideHeight / 2;
        }else{
            downY = mSlideHeight / 2;
        }
        isViewIniting = false;
        state=MotionEvent.ACTION_DOWN;//作假
    }
}
