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

import org.w3c.dom.Text;

public class SingleChoiceDialogFragment extends DialogFragment implements View.OnClickListener {

    private String[] options;
    private ListDialogListener mListener;
    private String tag;
    private RecyclerView recyclerView;
    private String title;
    private String description;

    public SingleChoiceDialogFragment(ListDialogListener listener, String[] options, String tag, String title, String description) {
        mListener = listener;
        this.options = options;
        this.tag = tag;
        this.title = title;
        this.description = description;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_lotcode_choice, null);

        if(description != null && description.trim().length()>0){
            TextView tvDescription = (TextView) view.findViewById(R.id.tv_description);
            tvDescription.setText(description);
            tvDescription.setVisibility(View.VISIBLE);
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        CustomAdapter adapter = new CustomAdapter(recyclerView, options);
        recyclerView.setAdapter(adapter);
        builder.setView(view);

        builder.setCancelable(false);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onDialogNegativeClick(SingleChoiceDialogFragment.this);
                }
            }
        });

        return builder.create();
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        String[] options;
        LayoutInflater inflater;
        RecyclerView recyclerView;

        public CustomAdapter(RecyclerView recyclerView, String[] options) {
            this.recyclerView = recyclerView;
            this.options = options;
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
            v.setOnClickListener(SingleChoiceDialogFragment.this);
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
