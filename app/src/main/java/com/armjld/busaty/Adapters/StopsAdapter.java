package com.armjld.busaty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.busaty.R;
import com.armjld.busaty.Utill.ArabicNormalizer;

import java.util.ArrayList;

import Models.Stops;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.MyViewHolder> implements Filterable {
    
    Context mContext;
    ArrayList<Stops> listStops;
    ArrayList<Stops> mDisplayedValues;
    AdapterCallback callback;
    ArabicNormalizer arabicNormalizer = new ArabicNormalizer();

    public interface AdapterCallback{
        void onItemClicked(Stops position);
    }

    public StopsAdapter(Context mContext, ArrayList<Stops> listStops, AdapterCallback callback) {
        this.mContext = mContext;
        this.listStops = listStops;
        this.mDisplayedValues = listStops;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_address, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Stops stop = mDisplayedValues.get(position);

        // Get Post Date
        holder.setData(stop);

        holder.myview.setOnClickListener(v -> {
            if(callback != null) {
                callback.onItemClicked(stop);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDisplayedValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public View myview;

        public TextView txtTitle, txtDisc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myview = itemView;

            txtTitle = myview.findViewById(R.id.txtTitle);
            txtDisc = myview.findViewById(R.id.txtDisc);
        }

        @SuppressLint("SetTextI18n")
        public void setData(Stops stop) {
            txtTitle.setText(stop.getName());
            txtDisc.setText(stop.getCode());
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @SuppressLint("NotifyDataSetChanged")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
                mDisplayedValues = (ArrayList<Stops>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Stops> FilteredArrList = new ArrayList<>();

                if (listStops == null) {
                    listStops = new ArrayList<>(mDisplayedValues); // saves the original data in Ticket
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = listStops.size();
                    results.values = listStops;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < listStops.size(); i++) {
                        // -- Search
                        if (arabicNormalizer.normalize(listStops.get(i).getName()).contains(arabicNormalizer.normalize(constraint.toString())) ||
                        listStops.get(i).getCode().contains(constraint.toString())) {

                            FilteredArrList.add(listStops.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
    }
}
