package com.slife.chris.studentlife.lecturer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.slife.chris.studentlife.R;

import java.util.ArrayList;

public class LecturerUnitsAdapter extends BaseAdapter {

	private static ArrayList<LecturerUnitsStructure> unitsList;
	private LayoutInflater mInflater;
	private Context myContext;

	public LecturerUnitsAdapter(Context context, ArrayList<LecturerUnitsStructure> units) {
		unitsList = units;
		mInflater = LayoutInflater.from(context);
        myContext = context;
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

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.unit_lec_view, null);
			holder = new ViewHolder();
			holder.txtunitName = (TextView) convertView.findViewById(R.id.unit_name);
			holder.txtunitCode = (TextView) convertView.findViewById(R.id.unit_code);
			holder.txtCourseName = (TextView) convertView.findViewById(R.id.course_name);
			holder.txtHasSet = (TextView) convertView.findViewById(R.id.has_set);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtunitName.setText(unitsList.get(position).getName());
		holder.txtunitCode.setText(unitsList.get(position).getUnitCode());
		holder.txtCourseName.setText(unitsList.get(position).getCourse()+" "+unitsList.get(position).getGroup());

		if(unitsList.get(position).getStatus().equals("pending")){
			holder.txtHasSet.setText("NOT SET");
			holder.txtHasSet.setTextColor(myContext.getResources().getColor(R.color.qiscus_red));
		}else{
			holder.txtHasSet.setText(unitsList.get(position).getStatus());
			holder.txtHasSet.setTextColor(myContext.getResources().getColor(R.color.material_light_blue));
		}
		//

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(myContext,LecPreferenceActivity.class);
                i.putExtra("name",unitsList.get(position).getName());
                i.putExtra("unit_code",unitsList.get(position).getUnitCode());
                i.putExtra("class_group",unitsList.get(position).getGroup());
                i.putExtra("course",unitsList.get(position).getCourse());
                i.putExtra("hours",unitsList.get(position).getHours());
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				myContext.startActivity(i);
			}
		});

		return convertView;
	}

	static class ViewHolder {
		TextView txtunitName;
		TextView txtunitCode;
		TextView txtHasSet;
		TextView txtCourseName;

	}
}
