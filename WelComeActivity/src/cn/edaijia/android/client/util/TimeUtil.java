package cn.edaijia.android.client.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    static String yearstr = "", monthstr = "", daystr = "", hourstr = "",
            minutestr = "", secondstr = "", millistr = "";

    public static String getMyCallPhone(String time) {
        long l = Long.parseLong(time);
        getTime(l);
        String s = yearstr + "-" + monthstr + "-" + daystr + "  " + hourstr
                + ":" + minutestr + ":" + secondstr;
        return s;
    }

    public static String getHourTime(long milliseconds) {
        // 传入时间为毫秒级，返回10位格式串
        {
            String s;
            getTime(milliseconds);
            if (hourstr != null && hourstr.length() != 0) {
                s = hourstr + " 小时 " + minutestr + " 分钟";
            } else {
                s = minutestr + " 分钟";
            }
            return s;
        }
    }

    public String getReminderTime(long milliseconds) {
        // 传入时间为毫秒级，返回10位格式串
        {

            getTime(milliseconds);
            String s = yearstr + "-" + monthstr + "-" + daystr + "  " + hourstr
                    + ":" + minutestr + ":" + secondstr;
            return s;
        }
    }

    public String getEmpox10TimeStr(long milliseconds)
    // 传入时间为毫秒级，返回10位格式串
    {

        getTime(milliseconds);
        String s = monthstr + "-" + daystr + " " + hourstr + ":" + minutestr;
        return s;
    }

    public String getEmpox10TimeStrMessage(long milliseconds)
    // 传入时间为毫秒级，返回10位格式串
    {

        getTime(milliseconds);
        String s = monthstr + "月" + daystr + "日  " + hourstr + ":" + minutestr;
        return s;
    }

    public String getInformationDateFormat(long milliseconds) {
        getTime(milliseconds);
        String s = yearstr + "-" + monthstr + "-" + daystr;
        return s;
    }

    /* *
     * 提醒使用事件
     * 
     * @param milliseconds
     * 
     * @return
     */
    public static String getRemindDateFormat(long milliseconds) {
        getTime(milliseconds);
        String s = yearstr + "-" + monthstr + "-" + daystr + " " + hourstr
                + ":" + minutestr;
        return s;
    }

    public static long getMillisFrom12Str(String time12Str)
    // timeStr is like '200203012359'
    {
        Calendar mc = Calendar.getInstance();
        int year = Integer.parseInt(time12Str.substring(0, 4));
        int month = Integer.parseInt(time12Str.substring(4, 6)) - 1;
        int day = Integer.parseInt(time12Str.substring(6, 8));
        int hour = Integer.parseInt(time12Str.substring(8, 10));
        int min = Integer.parseInt(time12Str.substring(10, 12));
        mc.set(year, month, day, hour, min);

        return mc.getTimeInMillis();
    }

    public static void getTime(long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new java.util.Date(date));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        yearstr = String.valueOf(year);
        if (month < 10) {
            monthstr = "0" + month;
        } else {
            monthstr = String.valueOf(month);
        }
        if (day < 10) {
            daystr = "0" + day;
        } else {
            daystr = String.valueOf(day);
        }
        if (hour < 10) {
            hourstr = "0" + hour;
        } else {
            hourstr = String.valueOf(hour);
        }
        if (minute < 10) {
            minutestr = "0" + minute;
        } else {
            minutestr = String.valueOf(minute);
        }
        if (second < 10) {
            secondstr = "0" + second;
        } else {
            secondstr = String.valueOf(second);
        }
        millistr = "000";
    }

    /** 获取系统当前时间(时间戳形式) **/
    public static String getcurrentTimebylong() {
        // SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");
        // String date = sDateFormat.format(new java.util.Date());
        long curr_time = System.currentTimeMillis();
        String temp = (curr_time + "").substring(0, 10);
        return temp;
    }

    /** 获取系统当前时间(时间形式) **/
    public static String getcurrentTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /** 获取系统当前时间(时间形式) **/
    public static String getcurrentTimes() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /** 当前时间(时间形式) **/
    public static String getTimeHm(long dat) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(" MM-dd  HH:mm");
        String date = sDateFormat.format(dat);
        return date;
    }

    /** 时间（long） **/
    public static long getTime(String dat) {
        if (dat == null) {
            return 0l;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dat);
            long time = date.getTime();
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }
}
