package com.asdc.yzu.buildingdetectclient;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.opencv.core.Mat;

/**
 * Created by yzu on 2015/6/23.
 */
public class OpenCvUtils {
    public static String matToJson(Mat mat) {
        JsonObject obj = new JsonObject();

        if (mat.isContinuous()) {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < mat.rows(); i++) {
                double[] d = mat.get(i, 0);
                builder.append(d[0]);
                if (i != mat.rows() - 1) {
                    builder.append(",");
                }
            }

            obj.addProperty("rows", mat.rows());
            obj.addProperty("cols", mat.cols());
            obj.addProperty("type", mat.type());
            obj.addProperty("data", builder.toString());

            Gson gson = new Gson();
            String json = gson.toJson(obj);

            Log.d("opencv_detector", "json: " + json);
            return json;
        } else {
            Log.e("opencv_detector", "Mat not continuous.");
        }
        return "{}";
    }

    public static Mat matFromJson(String json) {
        JsonParser parser = new JsonParser();
        JsonObject JsonObject = parser.parse(json).getAsJsonObject();

        int rows = JsonObject.get("rows").getAsInt();
        int cols = JsonObject.get("cols").getAsInt();
        int type = JsonObject.get("type").getAsInt();

        String dataString = JsonObject.get("data").getAsString();

        Mat mat = new Mat(rows, cols, type);

        int rowIndex = 0;

        for (String s : dataString.split(",")) {
            mat.put(rowIndex++, 0, Double.parseDouble(s));
        }

        return mat;

    }

}