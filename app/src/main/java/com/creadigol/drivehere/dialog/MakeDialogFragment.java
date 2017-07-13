package com.creadigol.drivehere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creadigol.drivehere.R;
import com.creadigol.drivehere.util.CircleView;

public class MakeDialogFragment extends DialogFragment implements View.OnClickListener {

    ListDialogListener mListener;
    RecyclerView recyclerView;
    String tag;

    public MakeDialogFragment(ListDialogListener listener, String tag) {
        mListener = listener;
        this.tag = tag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose "+ tag);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_lotcode_choice, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        CustomAdapter adapter = new CustomAdapter(recyclerView);
        recyclerView.setAdapter(adapter);
        builder.setView(view);

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onDialogNegativeClick(MakeDialogFragment.this);
                }
            }
        });

        return builder.create();
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        String[] options;
        LayoutInflater inflater;
        RecyclerView recyclerView;

        public CustomAdapter(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            options = getResources().getStringArray(R.array.Make);
            inflater = LayoutInflater.from(getActivity());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvOption;

            public ViewHolder(View v) {
                super(v);
                tvOption = (TextView) v.findViewById(R.id.tv_option);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view.
            View v = inflater.inflate(R.layout.row_dialog_simple, viewGroup, false);
            v.setOnClickListener(MakeDialogFragment.this);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            viewHolder.tvOption.setText(options[position]);
        }

        @Override
        public int getItemCount() {
            return options.length;
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int position = recyclerView.getChildAdapterPosition(v);
        mListener.onItemClick(position, tag);
        dismiss();
    }
}
