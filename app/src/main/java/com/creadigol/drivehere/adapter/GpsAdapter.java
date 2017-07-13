package com.creadigol.drivehere.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.creadigol.drivehere.Model.CarAdd;
import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class GpsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CarAdd.GPS> gpsList;
    private Context context;
    private View.OnClickListener deleteListener;

    public GpsAdapter(Context context, ArrayList<CarAdd.GPS> gpsList, View.OnClickListener deleteListener) {
        this.context = context;
        this.gpsList = gpsList;
        this.deleteListener = deleteListener;
        Log.e("tag", this.gpsList.size() + "size");
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_add_gps, viewGroup, false);
        return new GpsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        CarAdd.GPS gps = gpsList.get(i);
        GpsViewHolder gpsViewHolder = (GpsViewHolder) viewHolder;

        if (gps != null) {
            gpsViewHolder.edtSerialNumber.setText(gps.getValue());
            gpsViewHolder.edtSerialNumber.setEnabled(false);

            gpsViewHolder.edtTechnicianName.setText(gps.getTechnicianName());
            gpsViewHolder.edtTechnicianName.setEnabled(false);

            String imagePath = gps.getImage();

            if (imagePath != null && imagePath.trim().length() > 0) {
                if (imagePath.startsWith(Constant.PREFIX_HTTPS)) {
                    MyApplication.getInstance().getImageLoader().displayImage(imagePath, gpsViewHolder.ivGPSImage, getDisplayImageOptions());
                } else
                    MyApplication.getInstance().getImageLoader().displayImage("file://" + imagePath, gpsViewHolder.ivGPSImage, getDisplayImageOptions());
            } else
                MyApplication.getInstance().getImageLoader().displayImage("", gpsViewHolder.ivGPSImage, getDisplayImageOptions());

            // set default_car data
            if (deleteListener != null) {
                gpsViewHolder.ivDelete.setVisibility(View.VISIBLE);
                gpsViewHolder.ivDelete.setTag(i);
                gpsViewHolder.ivDelete.setOnClickListener(deleteListener);
            }
        }
    }

    @Override
    public int getItemCount() {
        return gpsList.size();
    }

    public void notifyDataSetChanged(ArrayList<CarAdd.GPS> gpsList) {
        this.gpsList = gpsList;
        this.notifyDataSetChanged();
    }

    public void notifyInsertItem(ArrayList<CarAdd.GPS> gpsList) {
        this.gpsList = gpsList;
        this.notifyItemInserted(gpsList.size()-1);
    }

    public DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_car)
                .showImageOnFail(R.drawable.default_car)
                .showImageOnLoading(R.drawable.default_car).build();
        return options;
    }

    class GpsViewHolder extends RecyclerView.ViewHolder {
        public EditText edtSerialNumber, edtTechnicianName;
        public ImageView ivGPSImage, ivDelete;

        public GpsViewHolder(View itemView) {
            super(itemView);
            edtSerialNumber = (EditText) itemView.findViewById(R.id.edt_serial_number);
            edtTechnicianName = (EditText) itemView.findViewById(R.id.edt_technician_name);

            ivGPSImage = (ImageView) itemView.findViewById(R.id.iv_gps_image);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }
}
