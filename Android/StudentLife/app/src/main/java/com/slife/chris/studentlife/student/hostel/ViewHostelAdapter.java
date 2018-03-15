package com.slife.chris.studentlife.student.hostel;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.slife.chris.studentlife.R;
import com.tigerpenguin.widget.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;



/**
 * Created by Chris on 2/1/2017.
 */

public class ViewHostelAdapter extends RecyclerView.Adapter<ViewHostelAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private static ArrayList<HostelStructure> hostelArrayList;

    public ViewHostelAdapter(Context context, ArrayList<HostelStructure> hostelStructures) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        hostelArrayList = hostelStructures;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
         TextView hostelNameView;
         RoundedImageView hostelImage;
         TextView hostelDistance;
        SimpleRatingBar simpleRatingBar;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            hostelImage = (RoundedImageView) view.findViewById(R.id.hostelImage);
            hostelDistance = (TextView) view.findViewById(R.id.hostelDistanceView);
            hostelNameView = (TextView) view.findViewById(R.id.hostelNameView);
            simpleRatingBar = (SimpleRatingBar) view.findViewById(R.id.myRatingBar);

        }
    }

    @Override
    public ViewHostelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hostel_list_item, parent, false);
        //view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHostelAdapter.ViewHolder holder, int position) {
       holder.hostelNameView.setText(hostelArrayList.get(position).getHostelName());
        holder.hostelDistance.setText(hostelArrayList.get(position).getHostelDistance());
        holder.simpleRatingBar.setRating(Integer.parseInt(hostelArrayList.get(position).getRatings()));
        Glide.with(holder.hostelImage.getContext())
                .load(R.drawable.sunrise)
                .placeholder(R.drawable.placeholder_user)
                .into(holder.hostelImage);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context,HostelDetailsActivity.class);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return hostelArrayList.size();
    }
}
