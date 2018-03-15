package com.slife.chris.studentlife.utilities;

/**
 * Created by Chris on 9/27/2016.
 */

public class Time {

    public String getTimeText(String time) {
        String formattedDate = time.substring(0,10);
        String formattedTime = time.substring(11,16);
        int hour = Integer.parseInt(time.substring(11,13))+3;
        String ext = null;
        if(hour > 12){
            hour = hour -12;
            formattedTime = String.valueOf(hour)+time.substring(13,16);
            ext = "PM";
        }else{
            ext = "AM";
        }
        return "@"+formattedTime+" "+ext;
        //return TimeUtils.millisToLongDHMS(message.getTime() * 1000);
    }
}
