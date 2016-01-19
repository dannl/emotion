
package la.niub.util.utils;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TimeUtils {

    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final long MILLISECONDS_PER_MINUTE = 60 * 1000;
    private static final long MILLISECONDS_PER_HOUR = 60 * 60 * 1000;
    private static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

    static final SimpleDateFormat TIMEFORMATER = new SimpleDateFormat("hh:mm", Locale.CHINA);

    private static final String DURATION_FORMAT = "%s:%s";

    public static String formatHHMM(long time) {
        Date date = new Date(time);
        SimpleDateFormat f = new SimpleDateFormat("hh:mm", Locale.getDefault());
        return f.format(date);
    }

    public static String formatyyyyMMdd_Chinese(long time) {
        return getDateTime(time, "yyyy年MM月dd日");
    }

    public static String androidLogTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss.SSS",
                Locale.getDefault());
        return formatter.format(new Date());
    }

    public static String getDateTime(long time) {
        return getDateTime(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateTime(final long time, final String format) {
        Date date = new Date(time);
        SimpleDateFormat f = new SimpleDateFormat(format, Locale.getDefault());
        return f.format(date);
    }

    public static String formatGeneralTime(final long time) {
        final int day = (int) (time / MILLISECONDS_PER_DAY);
        if (day == 0) {
            return 1 + "天";
        } else {
            return day + "天";
        }
    }

    static final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy.MM.dd EEEE",
            Locale.CHINA);

    public static String changeLongDateToString(Date date) {
        String dateString = null;
        try {
            dateString = dateFormater.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String changeTimeToString(Date date) {
        return DateUtils.getRelativeTimeSpanString(date.getTime()).toString();
    }

    // 判断两个日期是否为同一天
    public static boolean areSameDay(long ldatea, long ldateb) {
        Date dateA = new Date(ldatea);
        Date dateB = new Date(ldateb);

        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    // 判断是否润年
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0) && year % 100 != 0) {
            return true;
        } else if (year % 400 == 0) {
            return true;
        }
        return false;
    }

    public static boolean isBeforeToady(Date date) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today.getTimeInMillis() > date.getTime();
    }

    public static String getCurrentDate() {
        return formatDateText(new Date());
    }

    public static long getCurrentTimeMills() {
        return System.currentTimeMillis();
    }

    public static String formatDateText(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }

    public static boolean isValidDate(int year, int month, int day) {
        return isValidDate(formatDateText(year, month, day));
    }

    public static boolean isValidDate(String text) {
        return parseDate(text) != null;
    }

    public static Date parseDate(int year, int month, int day) {
        return new GregorianCalendar(year, month, day).getTime();
    }

    public static Date parseDate(String text) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static long toDuration(String hours, String mins, String secs, String msecs) {
        int h = SafeParser.parseInt(hours);
        int m = SafeParser.parseInt(mins);
        int s = SafeParser.parseInt(secs);
        int ms = SafeParser.parseInt(msecs);
        return toDuration(h, m, s, ms);
    }

    public static long toDuration(int hours, int mins, int secs, int msecs) {
        long h = hours * MILLISECONDS_PER_HOUR;
        long m = mins * MILLISECONDS_PER_MINUTE;
        long s = secs * MILLISECONDS_PER_SECOND;
        return h + m + s + msecs;
    }

    public static int getHours(long duration) {
        return (int) (duration / MILLISECONDS_PER_HOUR);
    }

    public static int getMins(long duration) {
        long time = duration / MILLISECONDS_PER_SECOND;
        return (int) ((time % MILLISECONDS_PER_SECOND) / 60);
    }

    public static int getSecs(long duration) {
        long time = duration / MILLISECONDS_PER_SECOND;
        return (int) (time % 60);
    }

    public static int getMSecs(long duration) {
        return (int) (duration % MILLISECONDS_PER_SECOND);
    }

    public static String formatDuration(long duration) {
        String h = formatNumber(getHours(duration), 1);
        String m = formatNumber(getMins(duration), 2);
        String s = formatNumber(getSecs(duration), 2);
        String ms = formatNumber(getMSecs(duration), 1);
        final String format = "%s:%s:%s.%s";
        return String.format(format, h, m, s, ms);
    }

    public static String formatDurationWithOutMsec(long duration) {
        String h = formatNumber(getHours(duration), 1);
        String m = formatNumber(getMins(duration), 2);
        String s = formatNumber(getSecs(duration), 2);
        final String format = "%s:%s:%s";
        return String.format(format, h, m, s);
    }

    // 格式化为分秒 01:02 ;
    public static String formatDurationShort(long duration) {
        int h = getHours(duration);
        if (h == 0) {
            String m = formatNumber(getMins(duration), 2);
            String s = formatNumber(getSecs(duration), 2);
            return String.format(DURATION_FORMAT, m, s);
        } else {
            return formatDurationWithOutMsec(duration);
        }
    }

    // 格式化为分秒 01'02" ;
    public static String formatDurationShort2(long duration) {
        int m = getMins(duration);
        int s = getSecs(duration);
        if (m > 0) {
            return m + "'" + s + "\"";
        } else {
            return s + "\"";
        }
    }

    private static String formatNumber(int num, int width) {
        return String.format("%0" + width + "d", num);
    }

    // format time to yyyy-MM-dd
    private static String formatDateText(int year, int month, int day) {
        StringBuilder builder = new StringBuilder();
        builder.append(year);
        builder.append('-');
        if (month < 10) {
            builder.append(0);
        }
        builder.append(month);
        builder.append('-');
        if (day < 10) {
            builder.append(0);
        }
        builder.append(day);
        return builder.toString();
    }

}
