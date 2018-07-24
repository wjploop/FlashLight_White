package com.dionly.flashlight_white;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private CameraManager manager;
    private Camera camera = null;

    RelativeLayout bgLightOn;

    TextView rateTv;

    int curPos;

    boolean lightOn = false;
    int rate = 0;

    //定时器有消息传递到looper队列中，不能及时取消
    //加入这个flag
    boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);


        bgLightOn = findViewById(R.id.light_on_bg);
        rateTv = findViewById(R.id.rate_tv);
        final LinearLayout linearLayout = findViewById(R.id.rate_controller_ll);

        for (int i = 0; i < 9 * 8; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.mipmap.rate_controller);
            linearLayout.addView(imageView);
        }
        linearLayout.postInvalidate();
        SlideHolderScrollView hs = findViewById(R.id.rate_controller_view);
        hs.requestDisallowInterceptTouchEvent(true);

        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            setUpScrollerForGreatM(hs);
        }else{
            setUpScrollerForLessM(hs);
        }


        final LightSwitch lightSwitch = findViewById(R.id.light_switch);
        lightSwitch.setmOnToggleChangeListener(new LightSwitch.OnToggleChangeListener() {
            @Override
            public void setToggleState(boolean open) {
                lightOn = open;
               process(open,rate);
            }
        });

    }

    @RequiresApi(api = 23)
    private void setUpScrollerForGreatM(SlideHolderScrollView hs) {
        hs.setScrollViewListener(new SlideHolderScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(SlideHolderScrollView scrollView, int x, int y, int oldx, int oldy) {
                int perWidth = (((ViewGroup) scrollView).getChildAt(0).getWidth() - scrollView.getWidth()) / 9;
                curPos = x / perWidth;
                rateTv.setText(String.valueOf(curPos));
                rate = curPos;
                process(lightOn, rate);
            }
        });
    }

    int oldRate = 0;
    private void setUpScrollerForLessM(SlideHolderScrollView hs) {
        hs.setScrollViewListener(new SlideHolderScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(SlideHolderScrollView scrollView, int x, int y, int oldx, int oldy) {
                int perWidth = (((ViewGroup) scrollView).getChildAt(0).getWidth() - scrollView.getWidth()) / 9;
                curPos = x / perWidth > 0 ? 1 : 0;
                rateTv.setText(String.valueOf(curPos));
                rate = curPos;
                if (oldRate != rate) {
                    process(lightOn, rate);
                }
                oldRate = rate;

            }
        });
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    turnLight(false);
                    break;
                case 1:
                    turnLight(true);
                    break;
            }

        }
    };

    int tempLightState = 0;
    Timer mTimer;
    TimerTask mTimerTask;

    private void sos(int rate) {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                tempLightState = tempLightState == 0 ? 1 : 0;
                msg.what = tempLightState;
                handler.sendEmptyMessage(msg.what);
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 1000/rate);
    }

    private void process(boolean lightOn, int rate) {
        //任何情况下，先判断定时器是否开启
        if (mTimer != null) {
            mTimer.cancel();
            mTimerTask.cancel();
        }
        if (!lightOn) {
            turnLight(false);
        } else if (rate == 0) {
            turnLight(true);
        } else {
            turnLight(false);
            sos(rate);
        }
    }

    private void turnLight(boolean on) {
        processUI(on);

        new LightTask().execute(on);
    }

    private void processUI(boolean on) {
        if (on) {
            bgLightOn.setVisibility(View.VISIBLE);
        }else{
            bgLightOn.setVisibility(View.INVISIBLE);
        }
    }


    private class LightTask extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            process(booleans[0]);
            return null;
        }

        private void process(boolean lightOn) {
            if (lightOn) {
                lightOn();
            } else {
                lightOff();
            }
        }

    }

    private void lightOn() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            try {
                manager.setTorchMode("0", true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            final PackageManager pm = getPackageManager();
            final FeatureInfo[] features = pm.getSystemAvailableFeatures();
            for (final FeatureInfo f : features) {
                if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) { // 判断设备是否支持闪光灯
                    if (null == camera) {
                        camera = Camera.open();
                    }
                    final Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    ;
                    camera.setParameters(parameters);
                    camera.startPreview();
                }
            }
        }
    }

    private void lightOff() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            try {
                manager.setTorchMode("0", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        turnLight(false);

        super.onDestroy();

    }
}
