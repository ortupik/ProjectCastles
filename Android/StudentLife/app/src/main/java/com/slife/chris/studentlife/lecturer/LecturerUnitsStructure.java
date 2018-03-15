package com.slife.chris.studentlife.lecturer;

/**
 * Created by CHRIS on 3/31/2016.
 */
public class LecturerUnitsStructure {

    private String unitCode ="";
    private String name = "";
    private String hasSet ="";
    private String course ="";
    private String group ="";
    private String hours ="";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status ="";


    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String year) {
        this.group = year;
    }


    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHasSet() {
        return hasSet;
    }

    public void setHasSet(String hasSet) {
        this.hasSet = hasSet;
    }


}
