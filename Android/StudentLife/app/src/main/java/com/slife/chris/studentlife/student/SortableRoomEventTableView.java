package com.slife.chris.studentlife.student;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.slife.chris.studentlife.R;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;


/**
 * An extension of the {@link SortableTableView} that handles {@link RoomEventModel}s.
 *
 * @author ISchwarz
 */
public class SortableRoomEventTableView extends SortableTableView<RoomEventModel> {

    public SortableRoomEventTableView(final Context context) {
        this(context, null);
    }

    public SortableRoomEventTableView(final Context context, final AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }

    public SortableRoomEventTableView(final Context context, final AttributeSet attributes, final int styleAttributes) {
        super(context, attributes, styleAttributes);

        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, R.string.day_s, R.string.time, R.string.group, R.string.unit_code);
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(context, R.color.table_header_text));
        setHeaderAdapter(simpleTableHeaderAdapter);

        final int rowColorEven = ContextCompat.getColor(context, R.color.table_data_row_even);
        final int rowColorOdd = ContextCompat.getColor(context, R.color.table_data_row_odd);
        setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(rowColorEven, rowColorOdd));
        setHeaderSortStateViewProvider(SortStateViewProviders.brightArrows());

        setColumnWeight(0, 3);
        setColumnWeight(1, 3);
        setColumnWeight(2, 3);
        setColumnWeight(3, 3);

        setColumnComparator(0, RoomEventComparator.getDayComparator());
        setColumnComparator(1, RoomEventComparator.getTimeComparator());
        setColumnComparator(2, RoomEventComparator.getGroupComparator());
        setColumnComparator(3, RoomEventComparator.getUnitCodeComparator());

    }

}
