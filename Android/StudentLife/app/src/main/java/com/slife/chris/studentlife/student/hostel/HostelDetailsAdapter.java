package com.slife.chris.studentlife.student.hostel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.slife.chris.studentlife.R;

import java.util.ArrayList;


/**
 * Created by Chris on 2/1/2017.
 */

public class HostelDetailsAdapter extends RecyclerView.Adapter<HostelDetailsAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private static ArrayList<HostelDetailsStructure> hostelArrayList;

    public HostelDetailsAdapter(Context context, ArrayList<HostelDetailsStructure> hostelStructures) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        hostelArrayList = hostelStructures;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
         TextView roomType;
         TextView roomPrice;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            roomPrice = (TextView) view.findViewById(R.id.room_name);
            roomType = (TextView) view.findViewById(R.id.room_price);

        }
    }

    @Override
    public HostelDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hostel_room_list_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HostelDetailsAdapter.ViewHolder holder, int position) {
        holder.roomType.setText(hostelArrayList.get(position).getRoomType());
        holder.roomPrice.setText("Ksh: "+hostelArrayList.get(position).getRoomPrice());

    }

    @Override
    public int getItemCount() {
        return hostelArrayList.size();
    }
}
