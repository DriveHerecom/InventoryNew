package com.creadigol.drivehere.Network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.creadigol.drivehere.Model.CarAdd;
import com.creadigol.drivehere.util.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;

/**
 * Created by root on 8/6/17.
 */

public class MultipartRequest extends Request<String> {

    private HttpEntity mHttpEntity;
    private Response.Listener mListener;
    private HashMap<String, String> stringParams;
    private ArrayList<String> imagesPath;
    private ArrayList<String> imagesPathTitles;
    private ArrayList<CarAdd.GPS> gpsList;

    public MultipartRequest(String url, HashMap<String, String> stringParams,
                            ArrayList<String> imagesPath,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        this.stringParams = stringParams;
        this.imagesPath = imagesPath;
        mHttpEntity = buildMultipartEntity();
    }

    public MultipartRequest(String url, HashMap<String, String> stringParams,
                            ArrayList<String> imagesPath,
                            ArrayList<String> imagesPathTitles,
                            ArrayList<CarAdd.GPS> gpsList,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        this.stringParams = stringParams;
        this.imagesPath = imagesPath;
        this.imagesPathTitles = imagesPathTitles;
        this.gpsList = gpsList;
        mHttpEntity = buildMultipartEntity();
    }

    private HttpEntity buildMultipartEntity() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        for (Map.Entry<String, String> entry : stringParams.entrySet()) {
            builder.addTextBody(entry.getKey(), entry.getValue());
            Log.e("buildMultipartEntity", entry.getKey() + ":" + entry.getValue());
        }

        if (imagesPath != null && imagesPath.size() > 0) {
            int imageIndex = 0;
            for (int i = 0; i < imagesPath.size(); i++) {
                String path = imagesPath.get(i).trim();
                if (path != null && path.length() > 0) {
                    if (!path.startsWith(Constant.PREFIX_HTTPS)) {
                        File file = getFile(path);
                        if (file != null) {
                            FileBody Fbody = new FileBody(file);
                            ++imageIndex;
                            builder.addPart(ParamsKey.IMAGE_ + imageIndex, Fbody);
                            Log.e("buildMultipartEntity", ParamsKey.IMAGE_ + imageIndex + ":" + path);
                        }
                    }
                }
            }
            builder.addTextBody(ParamsKey.COUNT_IMAGE, String.valueOf(imageIndex));
            Log.e("buildMultipartEntity", ParamsKey.COUNT_IMAGE + ":" + String.valueOf(imageIndex));
        }

        if (imagesPathTitles != null && imagesPathTitles.size() > 0) {
            int imageIndex = 0;
            for (int i = 0; i < imagesPathTitles.size(); i++) {
                String path = imagesPathTitles.get(i).trim();
                if (path != null && path.length() > 0) {
                    if (!path.startsWith(Constant.PREFIX_HTTPS)) {
                        File file = getFile(path);
                        if (file != null) {
                            FileBody Fbody = new FileBody(file);
                            ++imageIndex;
                            builder.addPart(ParamsKey.IMAGE_TITLE_ + imageIndex, Fbody);
                            Log.e("buildMultipartEntity", ParamsKey.IMAGE_TITLE_ + imageIndex + ":" + path);
                        }
                    }
                }
            }
            builder.addTextBody(ParamsKey.COUNT_IMAGE_TITLE, String.valueOf(imageIndex));
            Log.e("buildMultipartEntity", ParamsKey.COUNT_IMAGE_TITLE + ":" + String.valueOf(imageIndex));
        }

        if (gpsList != null && gpsList.size() > 0) {
            int imageIndex = 0;
            for (int i = 0; i < gpsList.size(); i++) {
                String path = gpsList.get(i).getImage().trim();
                if (path != null && path.length() > 0) {
                    if (!path.startsWith(Constant.PREFIX_HTTPS)) {
                        File file = getFile(path);
                        if (file != null) {
                            FileBody Fbody = new FileBody(file);
                            ++imageIndex;
                            builder.addPart(ParamsKey.GPS_IMAGE_ + imageIndex, Fbody);
                            builder.addTextBody(ParamsKey.GPS_VALUE_ + imageIndex, gpsList.get(i).getValue());
                            builder.addTextBody(ParamsKey.GPS_TECHNICIAN_ + imageIndex, gpsList.get(i).getTechnicianName());
                            Log.e("buildMultipartEntity", ParamsKey.GPS_IMAGE_ + imageIndex + ":" + path);
                            Log.e("buildMultipartEntity", ParamsKey.GPS_VALUE_ + imageIndex + ":" + gpsList.get(i).getValue());
                        }
                    }
                }
            }
            builder.addTextBody(ParamsKey.GPS_COUNT, String.valueOf(imageIndex));
            Log.e("buildMultipartEntity", ParamsKey.GPS_COUNT + ":" + String.valueOf(imageIndex));
        }

        return builder.build();
    }

    private File getFile(String path) {
        File file = new File(path);
        if (file.exists()) return file;
        else return null;
    }

    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, "UTF-8"),
                    getCacheEntry());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            return Response.success(new String(response.data), getCacheEntry());
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
