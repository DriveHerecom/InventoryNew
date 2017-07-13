package com.creadigol.drivehere.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.creadigol.drivehere.Model.ScanHistoryItem;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.CommonFunctions;

import java.util.List;

/**
 * Created by Creadigol on 12-09-2016.
 */
public class LocationScanAdapter extends RecyclerView.Adapter<LocationScanAdapter.MyViewHolder> {
    Context context;
    List<ScanHistoryItem> scanHistoryItems;

    public LocationScanAdapter(Context context, List<ScanHistoryItem> scanHistoryItems) {
        this.context = context;
        this.scanHistoryItems = scanHistoryItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custome_location_scan_history, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ScanHistoryItem scanHistoryItem = scanHistoryItems.get(position);
        holder.tv_address.setText(scanHistoryItem.getAddress());
        holder.tv_date.setText(CommonFunctions.getDate(Long.parseLong(scanHistoryItem.getDate()), "E, MMM dd, yyyy")); //, "dd,MMMM,yyyy"

        final String latitude, longitude;
        latitude = scanHistoryItem.getLatitude().trim();
        longitude = scanHistoryItem.getLongitude().trim();

        if(latitude.length() > 0 && !latitude.equalsIgnoreCase("0") && longitude.length() > 0 && !longitude.equalsIgnoreCase("0")){
            holder.ivLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri; //= "http://maps.google.com/maps?daddr=" + 12f + "," + 2f + " (" + "Where the party is at" + ")";
                    //uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + mTitle + ")";
                    uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Scanned" + ")";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    try
                    {
                        context.startActivity(intent);
                    }
                    catch(ActivityNotFoundException ex)
                    {
                        try
                        {
                            Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            context.startActivity(unrestrictedIntent);
                        }
                        catch(ActivityNotFoundException innerEx)
                        {
                            CommonFunctions.showToast(context, "Please install a maps application");
                        }
                    }

                   /* Uri gmmIntentUri = Uri.parse("geo:"+latitude+","+longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    }*/

                }
            });
        } else {
            holder.ivLocation.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return scanHistoryItems.size();
    }

    public void modifyDataSet(List<ScanHistoryItem> search) {
        this.scanHistoryItems = search;
        this.notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_address, tv_date;
        public ImageView ivLocation;

        public MyViewHolder(View v) {
            super(v);

            tv_address = (TextView) v.findViewById(R.id.tvAddress);
            tv_date = (TextView) v.findViewById(R.id.tv_assigndate);
            ivLocation = (ImageView) v.findViewById(R.id.iv_location);
        }
    }
}