package com.github.nicosensei.lostdir.helpers;

/**
 * Helper methods related to time.
 *
 * @author ngiraud
 *
 */
public class TimeFormatter {

    private TimeFormatter() {
        // prevent instantiation
    }

    public final static String formatDurationSince(long startTimeInMs) {
        return formatDuration(System.currentTimeMillis() - startTimeInMs);
    }

    public final static String formatDuration(long lMs) {
        // Validate
        if (lMs > 0L) {
            // -- Declare variables
            String strDays = "";
            String strHours = "";
            String strMinutes = "";
            String strSeconds = "";
            String strMillisecs = "";
            String strReturn = "";
            long lRest;

            // -- Find values
            // -- -- Days
            strDays = String.valueOf(lMs / 86400000L);
            lRest = lMs % 86400000L;
            // -- -- Hours
            strHours = String.valueOf(lRest / 3600000L);
            lRest %= 3600000L;
            // -- -- Minutes
            strMinutes = String.valueOf(lRest / 60000L);
            lRest %= 60000L;
            // -- -- Seconds
            strSeconds = String.valueOf(lRest / 1000L);
            lRest %= 1000L;
            // -- -- Milliseconds
            strMillisecs = String.valueOf(lRest);

            // -- Format return
            // -- -- Days
            if (new Integer(strDays).intValue() != 0) {
                strReturn += strDays + "day ";
            }
            // -- -- Hours
            if (new Integer(strHours).intValue() != 0) {
                strReturn += strHours + "hr ";
            }
            // -- -- Minutes
            if (new Integer(strMinutes).intValue() != 0) {
                strReturn += strMinutes + "min ";
            }
            // -- -- Seconds
            if (new Integer(strSeconds).intValue() != 0) {
                strReturn += strSeconds + "sec ";
            }
            // -- -- Milliseconds
            if (new Integer(strMillisecs).intValue() != 0) {
                strReturn += strMillisecs + "ms";
            }

            return strReturn;
        } else if (lMs == 0L) {

            return "0ms";
        } else {
            return "-1";
        }
    }

}
