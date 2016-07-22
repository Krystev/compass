package com.inveitix.android.compass.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inveitix.android.compass.R;
import com.inveitix.android.compass.database.models.LocationModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationHolder> {

    private List<LocationModel> data;
    private Context context;
    private SimpleDateFormat dateFormat;
    private OnItemChangedListener listener;

    public LocationsAdapter(Context context) {
        this.data = new ArrayList<>();
        this.context = context;
        dateFormat = new SimpleDateFormat("MM/dd/yyyy\nhh:mm:ss");
    }

    public void setData(List<LocationModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_location, parent, false);
        LocationHolder vh = new LocationHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(LocationHolder holder, int position) {
        LocationModel item = data.get(position);
        holder.position = position;
        holder.txtLocation.setText(context.getString(R.string.north_offset, item.getNorthOffset()));
        holder.txtTimestamp.setText(dateFormat.format(new Date(item.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class LocationHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_location)
        TextView txtLocation;
        @BindView(R.id.txt_timestamp)
        TextView txtTimestamp;

        int position;

        public LocationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.btn_delete)
        public void onDeleteClicked() {
            LocationModel item = data.remove(position);
            notifyItemRemoved(position);
            if(listener != null) {
                listener.onItemRemoved(item);
            }
        }
    }

    public void setListener(OnItemChangedListener listener) {
        this.listener = listener;
    }

    interface OnItemChangedListener {
        void onItemRemoved(LocationModel item);
    }
}
