package com.example.tomcat.remindmeapp.places;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.tomcat.remindmeapp.R;
import com.example.tomcat.remindmeapp.models.Places;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for Places List
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private final PlacesAdapter.PlacesAdapterOnClickHandler mClickHandler;
    private List<Places> mPlacesData;

    PlacesAdapter(PlacesAdapter.PlacesAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }


    public interface PlacesAdapterOnClickHandler {
        void onClick(int position, boolean longClick);
    }


    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_row, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        @BindView(R.id.place_name_tv)
        com.example.tomcat.remindmeapp.utilitis.TextViewRobotoLight placeNameTxt;

        ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            ButterKnife.bind(this, view);
           // placeNameTxt = view.findViewById(R.id.place_name_tv);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition, false);
        }


        @Override
        public boolean onLongClick(View view) {
            //int adapterPosition = getAdapterPosition();
            int tag = (int) view.getTag();
            mClickHandler.onClick(tag, true);
            return false;
        }
    }


    @Override
    public void onBindViewHolder(PlacesAdapter.ViewHolder holder, int position) {
        String name = mPlacesData.get(position).getPlaceName();
        holder.placeNameTxt.setText(name);

        int id = mPlacesData.get(position).getPlaceIDinDB();
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        if (null == mPlacesData) return 0;
        return mPlacesData.size();
    }

    void setRemindersData(List<Places> placesData) {
        mPlacesData = placesData;
        notifyDataSetChanged();
    }

    void refresh(){
        this.notifyDataSetChanged();
    }
}
