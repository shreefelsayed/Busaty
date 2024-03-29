package com.armjld.busaty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.busaty.R;
import com.armjld.busaty.Utill.BitMapMaker;

import java.util.ArrayList;

import Models.Routes;
import Models.Stops;
import de.hdodenhof.circleimageview.CircleImageView;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.MyViewHolder> implements Filterable {
    
    Context mContext;
    ArrayList<Routes> listRoutes;
    ArrayList<Routes> mDisplayedValues;
    AdapterCallback callback;

    public interface AdapterCallback{
        void onItemClicked(Routes routes);
    }

    public ArrayList<Routes> getListRoutes() {
        return listRoutes;
    }

    public void setListRoutes(ArrayList<Routes> listRoutes) {
        this.listRoutes = listRoutes;
        Log.i("RoutesAdapter", "List Updated");
    }

    public RouteAdapter(Context mContext, ArrayList<Routes> listRoutes, AdapterCallback callback) {
        this.mContext = mContext;
        this.listRoutes = listRoutes;
        this.mDisplayedValues = listRoutes;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_routes, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Routes routes = mDisplayedValues.get(position);
        holder.setData(routes);

        holder.myview.setOnClickListener(v -> {
            if(callback != null) {
                callback.onItemClicked(routes);
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


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public View myview;
        TextView txtFrom, txtTo;
        CircleImageView imgCode;
        TextView txtStops,txtMoney, txtCode;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;

            txtFrom = myview.findViewById(R.id.txtFrom);
            txtTo = myview.findViewById(R.id.txtTo);
            imgCode = myview.findViewById(R.id.imgCode);
            txtStops = myview.findViewById(R.id.txtStops);
            txtMoney = myview.findViewById(R.id.txtMoney);
            txtCode = myview.findViewById(R.id.txtCode);
        }

        @SuppressLint("SetTextI18n")
        public void setData(Routes routes) {
            BitMapMaker bitMapMaker = new BitMapMaker();
            imgCode.setImageBitmap(bitMapMaker.createImageRounded(mContext,150, 150, routes.getCode()));
            txtMoney.setText("سعر التذكره : " + routes.getFees() + " جنية");
            txtCode.setText(routes.getCode());
            if(routes.getListStops().size() == 0) return;
            txtFrom.setText(routes.getListStops().get(0).getName());
            txtTo.setText(routes.getListStops().get(routes.getListStops().size() - 1).getName());
            txtStops.setText("عدد محطات الخط : " + routes.getListStops().size());
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @SuppressLint("NotifyDataSetChanged")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
                mDisplayedValues = (ArrayList<Routes>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Routes> FilteredArrList = new ArrayList<>();

                if (listRoutes == null) {
                    listRoutes = new ArrayList<>(mDisplayedValues);
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = listRoutes.size();
                    results.values = listRoutes;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < listRoutes.size(); i++) {
                        // -- Search
                        if (listRoutes.get(i).getCode().contains(constraint.toString())) {
                            FilteredArrList.add(listRoutes.get(i));
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
