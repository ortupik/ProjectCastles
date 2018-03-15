package com.slife.chris.studentlife.units;

/**
 * Created by CHRIS on 3/31/2016.
 */
public class UnitsStructure {

    private String unitCode ="";
    private String name = "";
    private String lecturerName ="";
    private String course ="";
    private String group ="";

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

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }


}
