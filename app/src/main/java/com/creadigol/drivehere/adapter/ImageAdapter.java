package com.creadigol.drivehere.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

/**
 * Created by root on 14/6/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> mImages;
    private View.OnClickListener deleteListener;

    public ImageAdapter(Context context, ArrayList<String> mImages) {
        this.context = context;
        this.mImages = mImages;
    }

    public ImageAdapter(Context context, ArrayList<String> mImages, View.OnClickListener deleteListener) {
        this.deleteListener = deleteListener;
        this.context = context;
        this.mImages = mImages;
    }

    public void notifyDataSetChanged(ArrayList<String> mImages) {
        this.mImages = mImages;
        this.notifyDataSetChanged();
    }

    public void removeItem(int position) {
        this.notifyItemRemoved(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, viewGroup, false);
        return new ImageAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;
        // set default_car data
        String imagePath = mImages.get(i);

        if (imagePath != null && imagePath.trim().length() > 0) {
            if (imagePath.startsWith(Constant.PREFIX_HTTPS)) {
                MyApplication.getInstance().getImageLoader().displayImage(imagePath, imageViewHolder.ivImage, getDisplayImageOptions());
            } else
                MyApplication.getInstance().getImageLoader().displayImage("file://" + imagePath, imageViewHolder.ivImage, getDisplayImageOptions());
        } else
            MyApplication.getInstance().getImageLoader().displayImage("", imageViewHolder.ivImage, getDisplayImageOptions());

        if (deleteListener != null) {
            imageViewHolder.ivDelete.setVisibility(View.VISIBLE);
            imageViewHolder.ivDelete.setTag(i);
            imageViewHolder.ivDelete.setOnClickListener(deleteListener);
        }
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_car)
                .showImageOnFail(R.drawable.default_car)
                .showImageOnLoading(R.drawable.default_car).build();
        return options;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImage, ivDelete;
        public ConstraintLayout clImage;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            clImage = (ConstraintLayout) itemView.findViewById(R.id.cl_image);
        }
    }

}