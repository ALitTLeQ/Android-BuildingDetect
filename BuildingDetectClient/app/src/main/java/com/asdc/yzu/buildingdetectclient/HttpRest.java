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
                //�o��Ū�������e(�y)
                InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                while (((inputLine = buffer.readLine()) != null))
                {
                    resultData += inputLine + "\n";

                }
                in.close();
                //����http�s��
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
                //�]�m�HPOST�覡
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                urlConn.setRequestMethod("POST");
                //POST�ШD����ϥνw�s
                urlConn.setUseCaches(false);
                urlConn.setInstanceFollowRedirects(true);

                //�t�m�����s����Content_type
                urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                urlConn.connect();
                //DataOutputStream�y�C
                DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());

                //�N�n�W�Ǫ����e�g�J�y��
                out.writeBytes(content);
                //��s�B����
                out.flush();
                out.close();
                //����ƾ�
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
