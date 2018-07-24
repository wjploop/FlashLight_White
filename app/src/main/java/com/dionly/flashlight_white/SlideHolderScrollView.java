package com.dionly.flashlight_white;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class SlideHolderScrollView extends HorizontalScrollView {

    private ScrollViewListener scrollViewListener = null;

    public SlideHolderScrollView(Context context) {
        super(context);
    }

    public SlideHolderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideHolderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SlideHolderScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public void setScrollViewListener(ScrollViewListener listener) {
        this.scrollViewListener = listener;
    }

    public interface ScrollViewListener {
        void onScrollChanged(SlideHolderScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}
