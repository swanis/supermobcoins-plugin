package me.swanis.mobcoins.utils;

public class TimeUtil {

    private static final int MILLIS_TO_SECONDS = 1000;
    private static final int MILLIS_TO_MINUTES = MILLIS_TO_SECONDS * 60;
    private static final int MILLIS_TO_HOURS = MILLIS_TO_MINUTES * 60;
    private static final int MILLIS_TO_DAYS = MILLIS_TO_HOURS * 24;

    public static int[] getRemainingTime(long remaining) {
        int days = (int) (remaining / MILLIS_TO_DAYS);
        int hours = (int) (remaining % MILLIS_TO_DAYS) / MILLIS_TO_HOURS;
        int minutes = (int) ((remaining % MILLIS_TO_HOURS) / MILLIS_TO_MINUTES);
        int seconds = (int) ((remaining % MILLIS_TO_MINUTES) / MILLIS_TO_SECONDS);

        return new int[] {
                days,
                hours,
                minutes,
                seconds
        };
    }

    public static String getFormattedString(long remaining) {
        int[] remainingTime = getRemainingTime(remaining);
        String string = "";

        for(int i = 0; i < remainingTime.length; i++) {
            if(remainingTime[i] == 0) continue;

            String s = null;

            if(i == 0) s = "d";
            if(i == 1) s = "h";
            if(i == 2) s = "m";
            if(i == 3) s = "s";

            if(string.length() == 0) {
                string = remainingTime[i] + s;
            } else {
                string = string + " " + remainingTime[i] + s;
            }
        }

        return string;
    }
}
