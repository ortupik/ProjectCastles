package com.slife.chris.studentlife.student.hostel;

/**
 * Created by Chris on 2/1/2017.
 */

public class HostelStructure {
    private String hostelName;

    public String getHostelName() {
        return hostelName;
    }

    public void setHostelName(String hostelName) {
        this.hostelName = hostelName;
    }

    public String getHostelDistance() {
        return hostelDistance;
    }

    public void setHostelDistance(String hostelDistance) {
        this.hostelDistance = hostelDistance;
    }

    public String getHostelImagePath() {
        return hostelImagePath;
    }

    public void setHostelImagePath(String hostelImagePath) {
        this.hostelImagePath = hostelImagePath;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    private String hostelDistance;
    private String hostelImagePath;
    private String ratings;
}
