package com.creadigol.drivehere.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creadigol.drivehere.Model.RepoReport;
import com.creadigol.drivehere.Network.ParamsKey;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.ui.CarListActivity;
import com.creadigol.drivehere.util.Constant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class RepoReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RepoReport> repoReports;
    private Context context;
    private View.OnClickListener clickListener;

    public RepoReportAdapter(Context context, List<RepoReport> repoReports, View.OnClickListener clickListener) {

        if (repoReports == null) {
            throw new IllegalArgumentException(
                    "repoReports must not be null");
        }
        this.context = context;
        this.repoReports = repoReports;
        this.clickListener = clickListener;
        Log.e("tag", this.repoReports.size() + "size");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_auction_list_main, viewGroup, false);
        return new RepoMainViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        final RepoReport repoReport = repoReports.get(i);
        final RepoMainViewHolder repoMainViewHolder = (RepoMainViewHolder) viewHolder;
        // set default_car data
        Log.e("repoReport total car", "repoReport.getTotalCars() " + repoReport.getTotalCars());
        repoMainViewHolder.tvTotalCar.setText(String.valueOf(repoReport.getTotalCars()));
        repoMainViewHolder.tvAuctionName.setText(repoReport.getCompanyName());

        if (repoReport.isDetailVisible())
            repoMainViewHolder.clAuctionSubContent.setVisibility(View.VISIBLE);
        else
            repoMainViewHolder.clAuctionSubContent.setVisibility(View.GONE);

        repoMainViewHolder.clAuctionMainContent.setTag(i);

        repoMainViewHolder.clAuctionMainContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashParams = new HashMap<>();
                hashParams.put(ParamsKey.REPO_NAME, repoReport.getCompanyName());

                Intent search = new Intent(context, CarListActivity.class);
                search.putExtra(CarListActivity.EXTRA_KEY_PARAMS, hashParams);
                search.putExtra(CarListActivity.EXTRA_KEY_LIST_TYPE, CarListActivity.LIST_TYPE.Repo);
                context.startActivity(search);
            }
        });

    }

    @Override
    public int getItemCount() {
        return repoReports.size();
    }


    class RepoMainViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTotalCar, tvAuctionName;
        public RecyclerView rvAuctionSub;
        public ConstraintLayout clAuctionMainContent, clAuctionSubContent;

        public RepoMainViewHolder(View itemView) {
            super(itemView);
            tvTotalCar = (TextView) itemView.findViewById(R.id.tv_total_car);
            tvAuctionName = (TextView) itemView.findViewById(R.id.tv_auction_name);
            rvAuctionSub = (RecyclerView) itemView.findViewById(R.id.rv_auction_list_sub);
            clAuctionMainContent = (ConstraintLayout) itemView.findViewById(R.id.cl_auction_main_content);
            clAuctionSubContent = (ConstraintLayout) itemView.findViewById(R.id.cl_auction_list_sub);
        }
    }

    public class RepoReportSubAdapter extends RecyclerView.Adapter<RepoReportSubAdapter.AuctionReportSubViewHolder> {

        List<RepoReport.LotCodeRepo> lotCodeRepo;
        String companyName;

        public RepoReportSubAdapter(List<RepoReport.LotCodeRepo> lotCodeRepo, String companyName) {

            if (lotCodeRepo == null) {
                throw new IllegalArgumentException(
                        "lotCodeRepo must not be null");
            }

            this.lotCodeRepo = lotCodeRepo;
            this.companyName = companyName;
        }

        @Override
        public AuctionReportSubViewHolder onCreateViewHolder(
                ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_auction_list_sub,
                            viewGroup,
                            false);
            return new AuctionReportSubViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(
                AuctionReportSubViewHolder viewHolder, int position) {
            RepoReport.LotCodeRepo lotCodeRepo = this.lotCodeRepo.get(position);

            viewHolder.tvLotCode.setText(lotCodeRepo.getLotCode() + ":");
            viewHolder.tvTotalCar.setText(String.valueOf(lotCodeRepo.getTotalCars()));
            viewHolder.clAuctionLotCode.setTag(companyName + Constant.SEPARATOR_AUCTION_VIEW_TAG + lotCodeRepo.getLotCode());
            viewHolder.clAuctionLotCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = (String) v.getTag();
                    String[] s = tag.split("%20");
                    //CommonFunctions.showToast(context, "A:" + s[0] + " L:" + s[1]);
                    HashMap<String, String> hashParams = new HashMap<>();
                    hashParams.put(ParamsKey.REPO_NAME, s[0]);
                    hashParams.put(ParamsKey.REPO_LOT_CODE, s[1]);

                    Intent search = new Intent(context, CarListActivity.class);
                    search.putExtra(CarListActivity.EXTRA_KEY_PARAMS, hashParams);
                    search.putExtra(CarListActivity.EXTRA_KEY_LIST_TYPE, CarListActivity.LIST_TYPE.Repo);
                    context.startActivity(search);
                    
                }
            });
        }

        @Override
        public int getItemCount() {
            return lotCodeRepo.size();
        }

        public final class AuctionReportSubViewHolder
                extends RecyclerView.ViewHolder {

            TextView tvLotCode;
            TextView tvTotalCar;
            ConstraintLayout clAuctionLotCode;

            public AuctionReportSubViewHolder(View itemView) {
                super(itemView);
                tvLotCode = (TextView) itemView.findViewById(R.id.tv_lot_code);
                tvTotalCar = (TextView) itemView.findViewById(R.id.tv_total_car);
                clAuctionLotCode = (ConstraintLayout) itemView.findViewById(R.id.cl_auction_lot_code);
            }
        }
    }
}
