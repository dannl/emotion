package com.qqdd.lottery.calculate;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class CalculatorListAdapter extends RecyclerView.Adapter<CalculatorListAdapter.CalculatorItemViewHolder>{


    public CalculatorListAdapter(CalculatorController calculators) {

    }

    @Override
    public CalculatorItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CalculatorItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static final class CalculatorItemViewHolder extends RecyclerView.ViewHolder {

        public CalculatorItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
