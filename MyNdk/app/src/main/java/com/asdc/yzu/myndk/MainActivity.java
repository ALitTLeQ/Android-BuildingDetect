package com.asdc.yzu.myndk;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;


public class MainActivity extends ActionBarActivity  implements CameraBridgeViewBase.CvCameraViewListener2{
    protected static final String TAG = "HelloOpenCV";

    static {
        if (!OpenCVLoader.initDebug())
            Log.e(TAG, "Failed to load OpenCV!");
        else {
            System.loadLibrary("MyLib");
        }
    }

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;
    private int                   mViewMode;
    private Mat mRgba;
    private Mat                  mGray;
    private Mat                  mIntermediateMat;
    private Mat                  mTemp;
    private int                   cnt = 0;
    private ImageView imgView;

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

        TextView tv = (TextView)findViewById(R.id.mytext);
        tv.setText(getStringFromNative());

        imgView = (ImageView) findViewById(R.id.imageView);
        mTemp = new Mat();
        try {
            mTemp = Utils.loadResource(MainActivity.this, R.mipmap.temp, Highgui.CV_LOAD_IMAGE_COLOR);
            Imgproc.cvtColor(mTemp, mTemp, Imgproc.COLOR_RGB2BGRA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap img = Bitmap.createBitmap(mTemp.cols(), mTemp.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mTemp, img);
        imgView.setImageBitmap(img);
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
    public native String getStringFromNative();
    public native int FindFeatures(long matAddrGr, long matAddrRgba, long matAddrTemp);

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        /*
                org.opencv.core.Size s = new Size(3,3);
                Imgproc.GaussianBlur(mGray, mGray, s, 2);
                Imgproc.Canny(mGray, mIntermediateMat, 50, threshold);
                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA);
                */
            int kps = FindFeatures(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr(), mTemp.getNativeObjAddr());
            Log.i(TAG, String.valueOf(kps));


        return mRgba;
    }
}
