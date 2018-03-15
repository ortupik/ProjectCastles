package com.slife.chris.studentlife.units;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.slife.chris.studentlife.R;

import java.util.ArrayList;

public class LecturerUnitsAdapter extends BaseAdapter {
	private static ArrayList<UnitsStructure> unitsList;
	
	private LayoutInflater mInflater;
	private Typeface typefaceRobotoLight;


	public LecturerUnitsAdapter(Context context, ArrayList<UnitsStructure> units) {
		unitsList = units;
		mInflater = LayoutInflater.from(context);
		//Roboto font
//		typefaceRobotoLight = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
	}

	public int getCount() {
		return unitsList.size();
	}

	public Object getItem(int position) {
		return unitsList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.custom_units_view, null);
			holder = new ViewHolder();
			holder.txtunitName = (TextView) convertView.findViewById(R.id.name);
			holder.txtunitCode = (TextView) convertView.findViewById(R.id.unitCode);
			holder.txtLecturer = (TextView) convertView.findViewById(R.id.lecturer);



			//convertView.setBackgroundColor(color);
		//	holder.txtunitName.setTextColor(color);


			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

	//	holder.txtunitCode.setTypeface(typefaceRobotoLight);
		//holder.txtunitName.setTypeface(typefaceRobotoLight);
		//holder.txtLecturer.setTypeface(typefaceRobotoLight);


		holder.txtunitName.setText(unitsList.get(position).getName());
		holder.txtunitCode.setText(unitsList.get(position).getUnitCode());
		holder.txtLecturer.setText(unitsList.get(position).getLecturerName());

		return convertView;
	}

	static class ViewHolder {
		TextView txtunitName;
		TextView txtunitCode;
		TextView txtLecturer;


	}
}
