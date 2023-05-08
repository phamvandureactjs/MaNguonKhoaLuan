package com.example.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<History> mList;

    public HistoryAdapter(List<History> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History mHistory = mList.get(position);
        if (mHistory == null) {
            return;
        }
        holder.moth.setText("Tháng " + mHistory.moth + ":");
        holder.mothVal.setText(mHistory.val + " lít");
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView moth;
        private final TextView mothVal;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            moth = itemView.findViewById(R.id.moth);
            mothVal = itemView.findViewById(R.id.mothVal);
        }
    }
}
