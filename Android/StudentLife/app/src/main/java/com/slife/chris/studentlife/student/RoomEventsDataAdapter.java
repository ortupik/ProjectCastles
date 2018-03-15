package com.slife.chris.studentlife.student;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.slife.chris.studentlife.student.RoomEventModel;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;



public class RoomEventsDataAdapter extends TableDataAdapter<RoomEventModel> {

    private static final int TEXT_SIZE = 14;

    public RoomEventsDataAdapter(final Context context, final List<RoomEventModel> data) {
        super(context, data);
    }

    @Override
    public View getCellView(final int rowIndex, final int columnIndex, final ViewGroup parentView) {
        final RoomEventModel roomEventModel = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderDay(roomEventModel);
                break;
            case 1:
                renderedView = renderSTime(roomEventModel);
                break;
            case 2:
                renderedView = renderGroup(roomEventModel);
                break;
            case 3:
                renderedView = renderUnitCode(roomEventModel);
                break;
        }

        return renderedView;
    }

    private View renderUnitCode(final RoomEventModel roomEventModel) {

        final TextView textView = new TextView(getContext());
        textView.setText(roomEventModel.getUnit_code());
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);

        textView.setTextColor(0xFF2E7D32);
      //  textView.setTextColor(0xFFC62828);

        return textView;
    }

    private View renderGroup(final RoomEventModel roomEventModel) {
        final TextView textView = new TextView(getContext());
        textView.setText(roomEventModel.getClass_group());
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        textView.setTextColor(0xFF2E7D32);

        return textView;
    }

    private View renderSTime(final RoomEventModel roomEventModel) {
        final TextView textView = new TextView(getContext());
        textView.setText(roomEventModel.getS_time());
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        textView.setTextColor(0xFF2E7D32);

        return textView;
    }


    private View renderDay(final RoomEventModel roomEventModel) {
        final TextView textView = new TextView(getContext());
        textView.setText(roomEventModel.getDay());
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        return textView;
    }

}
