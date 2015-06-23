package com.asdc.yzu.buildingdetectclient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yzu on 2015/6/23.
 */
public class HttpRest {
    public static String GET(String httpUrl){
        String resultData = "";
        URL url = null;
        try
        {
            url = new URL(httpUrl);
        }
        catch (MalformedURLException e)
        {
            Log.e(MainActivity.TAG, "MalformedURLException");
        }
        if (url != null)
        {
            try
            {
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                //得到讀取的內容(流)
                InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                while (((inputLine = buffer.readLine()) != null))
                {
                    resultData += inputLine + "\n";

                }
                in.close();
                //關閉http連接
                urlConn.disconnect();
            }
            catch (IOException e)
            {
                Log.e(MainActivity.TAG, "IOException");
            }
        }
        else
        {
            Log.e(MainActivity.TAG, "Url NULL");
        }
        return resultData;
    }
    public static String POST(String httpUrl, String content){
        String resultData = "";
        URL url = null;
        try
        {
            url = new URL(httpUrl);
        }
        catch (MalformedURLException e)
        {
            Log.e(MainActivity.TAG, "MalformedURLException");
        }
        if (url != null)
        {
            try
            {
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                //設置以POST方式
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                urlConn.setRequestMethod("POST");
                //POST請求不能使用緩存
                urlConn.setUseCaches(false);
                urlConn.setInstanceFollowRedirects(true);

                //配置本次連接的Content_type
                urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                urlConn.connect();
                //DataOutputStream流。
                DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());

                //將要上傳的內容寫入流中
                out.writeBytes(content);
                //刷新、關閉
                out.flush();
                out.close();
                //獲取數據
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String inputLine = null;
                while (((inputLine = reader.readLine()) != null))
                {
                    resultData += inputLine + "\n";
                }
                reader.close();
                urlConn.disconnect();
            }
            catch (IOException e)
            {
                Log.e(MainActivity.TAG, "IOException");
            }
        }
        else
        {
            Log.e(MainActivity.TAG, "Url NULL");
        }
        return resultData;
    }
}
