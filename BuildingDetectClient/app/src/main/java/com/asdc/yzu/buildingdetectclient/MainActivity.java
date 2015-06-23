package com.asdc.yzu.buildingdetectclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


public class MainActivity extends ActionBarActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    protected static final String TAG = "HelloOpenCV";
    private static final int MSG_UPLOAD_OK = 0x01;
    static {
        if (!OpenCVLoader.initDebug())
            Log.e(TAG, "Failed to load OpenCV!");
    }

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private Mat mRgba;
    private Mat mGray;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    mOpenCvCameraView.enableView();

                } break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        if (mIsJavaCamera)
            mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void Detect(View view) {
        new Thread(new ComputeThread(this.handler)).start();
        Log.i(TAG, mGray.toString());
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba =inputFrame.rgba();
        mGray =inputFrame.gray();
        return mRgba;
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPLOAD_OK:

                    break;
            }
        }
    };
    public class ComputeThread implements Runnable {
        private Handler handler;

        public ComputeThread(Handler handler) {
            super();
            this.handler = handler;
        }

        @Override
        public void run() {
            long startTime, endTime;

            String url = "http://140.109.143.55:5000/";
            String url2 = "http://140.109.143.55:5000/api/test";
            String content = "input=" + OpenCvUtils.matToJson(mRgba.clone());
            String content2 = "input=" + OpenCvUtils.matToString(mGray.clone());
            String resultDate = "";

            startTime = System.nanoTime();
            resultDate = HttpRest.GET(url);
            Log.i(TAG, resultDate);
            endTime = System.nanoTime();
            Log.i(TAG, "Time: " +  new String(String.valueOf((endTime - startTime) / 1000000.0) + "ms"));



            startTime = System.nanoTime();
            resultDate = HttpRest.POST(url2, content2);
            Log.i(TAG, resultDate);
            endTime = System.nanoTime();
            Log.i(TAG, "Time: " +  new String(String.valueOf((endTime - startTime) / 1000000.0) + "ms"));

            //¶Ç°ethreadµ²§ô message
            this.handler.sendEmptyMessage(MSG_UPLOAD_OK);
        }
    }
}
