package com.asdc.yzu.buildingdetectclient;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    protected static final String TAG = "HelloOpenCV";
    private static final int MSG_UPLOAD_OK = 0x01;

    static {
        if (!OpenCVLoader.initDebug())
            Log.e(TAG, "Failed to load OpenCV!");
    }
    private MyCameraView mOpenCvCameraView;
    private TextView txtInfo;
    private String BuildingMatch;
    private String BuildingMatchTime;

    private boolean mIsJavaCamera = true;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        if (mIsJavaCamera)
            mOpenCvCameraView = (MyCameraView) findViewById(R.id.camera_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        txtInfo = (TextView)findViewById(R.id.text_info);
        BuildingMatch = new String("");
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
        long startTime, endTime;

        txtInfo.setText("Waiting...");

        new Thread(new ComputeThread(this.handler)).start();


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
                    //更新畫面資訊
                    txtInfo.setText("Match: "+BuildingMatch + ", Time: " + BuildingMatchTime);
                    break;
            }
        }
    };

    //thread上傳圖片
    public class ComputeThread implements Runnable {
        private Handler handler;

        public ComputeThread(Handler handler) {
            super();
            this.handler = handler;
        }

        @Override
        public void run() {
            long startTime, endTime;
            OpenCvUtils.SaveImage(mRgba.clone(), "Image.jpg");
            String url = "http://140.109.143.60:5000/";
            String url2 = "http://140.109.143.60:5000/file";

            String path = Environment.getExternalStorageDirectory() + "/Images/Image.jpg";

            startTime = System.nanoTime();
            //上傳圖片並接收結果
            String BuildingAround = new String("1,2,20");
            BuildingMatch = uploadFile(url2, path, BuildingAround);

            endTime = System.nanoTime();

            BuildingMatchTime = new String(String.valueOf((endTime - startTime) / 1000000.0));
            BuildingMatchTime = BuildingMatchTime.substring(0, BuildingMatchTime.indexOf(".") + 3);
            Log.i(TAG, "Time: " + BuildingMatchTime);

            //傳送thread結束 message
            this.handler.sendEmptyMessage(MSG_UPLOAD_OK);
        }
    }

    private int serverResponseCode = 0;
    public String uploadFile(String upLoadServerUri, String sourceFileUri, String BuildingAround) {
        String fileName = sourceFileUri;
        String result = new String("");

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :");
            return "File not exist";

        }
        else
        {
            try {
                Log.i(TAG, "上傳中");
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + BuildingAround + "\"" + lineEnd);

                dos.writeBytes(lineEnd);


                StringBuffer sb = new StringBuffer();
                bufferSize = 1024;
                buffer = new byte[bufferSize];
                int length = -1;
                // 從文件讀取數據至緩衝區
                while ((length = fileInputStream.read(buffer)) != -1) {
                    // 將資料寫入DataOutputStream中
                    dos.write(buffer, 0, length);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // 關閉流
                fileInputStream.close();
                dos.flush();
                // 獲取響應流
                InputStream is = conn.getInputStream();
                int ch;
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }
                // 關閉DataOutputStream
                dos.close();
                return sb.toString();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.i(TAG, "上傳失敗"+ex);
                result = "Upload Fail";
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "上傳失敗" + e);
                result = "Upload Fail";
            }
            return result;

        } // End else block
    }

}
