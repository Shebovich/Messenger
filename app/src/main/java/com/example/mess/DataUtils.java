package com.example.mess;


import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DataUtils implements Serializable {

    private SimpleDateFormat date = new SimpleDateFormat("dd.MM.yy");
    private SimpleDateFormat dateFull = new SimpleDateFormat("dd MMMM");
    private SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat month = new SimpleDateFormat("MMM");

    private Calendar calendar = Calendar.getInstance();
    public String getElapsedTime(Long timeString, String type) {

        long timeStr = timeString / 86400000;
        long minutStr = timeString / 60000;
        long currentTime = System.currentTimeMillis() / 86400000;


        date.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        month.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        time.setTimeZone(TimeZone.getTimeZone("GMT+3"));


        calendar.setTimeInMillis(timeString);
        int mMonth = calendar.get(Calendar.MONTH);

        if (timeString <= 1){
            return "никогда";
        }else {


            if (type.equals("channels")) {
                if (timeStr == currentTime) {

                    return time.format(calendar.getTime());
                } else {

                    return date.format(calendar.getTime());

                }
            } else if (type.equals("chats_header")) {
                if (timeStr == currentTime) {
                    return "Сегодня";
                } else

                    return dateFull.format(calendar.getTime());
            } else if (type.equals("chats_message")) {

                return time.format(calendar.getTime());
            }
            if (type.equals("members")) {
                if (timeStr == currentTime) {
                    return time.format(calendar.getTime());
                } else if (minutStr == currentTime) {
                    return "В сети";
                } else {
                    return dateFull.format(calendar.getTime());
                }

            }

        }
        return null;
    }

    // convert milliseconds into the month of the year string
    public  String monthStringFormat(long msecs) {
        calendar.setTimeInMillis(msecs);
        month.setTimeZone(TimeZone.getTimeZone("GMT+3"));



        return month.format(calendar.getTime());
    }
    public boolean getEqualsMonthMessage(Long dateString1, Long dateString2) {
        dateString1 = dateString1 / 2592000000L;

        dateString2 = dateString2 / 2592000000L;
        if (dateString1.equals(dateString2)) {
            return true;
        } else {
            return false;
        }

    }


    public boolean getEqualsHeaderMessage(Long dateString1, Long dateString2) {
        dateString1 = dateString1 / 86400000;

        dateString2 = dateString2 / 86400000;
        if (dateString1.equals(dateString2)) {
            return true;
        } else {
            return false;
        }

    }
    public String getFileSize(Long size){
        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if(size < sizeMb)
            return df.format(size / sizeKb)+ " Kb";
        else if(size < sizeGb)
            return df.format(size / sizeMb) + " Mb";
        else if(size < sizeTerra)
            return df.format(size / sizeGb) + " Gb";

        return "";
    }




}