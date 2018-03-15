package com.slife.chris.studentlife.student;

import com.slife.chris.studentlife.student.RoomEventModel;

import java.util.Comparator;


/**
 * A collection of {@link Comparator}s for {@link RoomEventModel} objects.
 *
 * @author ISchwarz
 */
public final class RoomEventComparator {

    private RoomEventComparator() {
        //no instance
    }

    public static Comparator<RoomEventModel> getDayComparator() {
        return new DayComparator();
    }

    public static Comparator<RoomEventModel> getTimeComparator() {
        return new TimeComparator();
    }

    public static Comparator<RoomEventModel> getGroupComparator() {
        return new GroupComparator();
    }

    public static Comparator<RoomEventModel> getUnitCodeComparator() {
        return new UnitCodeComparator();
    }


    private static class DayComparator implements Comparator<RoomEventModel> {

        @Override
        public int compare(final RoomEventModel car1, final RoomEventModel car2) {
            return car1.getDay().compareTo(car2.getDay());
        }
    }

    private static class TimeComparator implements Comparator<RoomEventModel> {

        @Override
        public int compare(final RoomEventModel car1, final RoomEventModel car2) {
            return car1.getS_time().compareTo(car2.getS_time());
        }
    }

    private static class GroupComparator implements Comparator<RoomEventModel> {

        @Override
        public int compare(final RoomEventModel car1, final RoomEventModel car2) {
            return car1.getClass_group().compareTo(car2.getClass_group());
        }
    }

    private static class UnitCodeComparator implements Comparator<RoomEventModel> {

        @Override
        public int compare(final RoomEventModel car1, final RoomEventModel car2) {
            return car1.getUnit_code().compareTo(car2.getUnit_code());
        }
    }

}
