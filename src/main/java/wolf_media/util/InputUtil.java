package main.java.wolf_media.util;

import java.time.LocalDate; 
import java.time.format.DateTimeFormatter;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;



/**
 * Utility class for getting input from the console
 * 
 * @author John Fagan
 *
 */
public class InputUtil {

    private static final String DATE_FORMAT = "yyyy-MM-DD";
    private static final String TIME_FORMAT = "hh:mm:ss";
    private static final String TIMESTAMP_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
    private static final String TIMESTAMP_FORMAT_UPPER = TIMESTAMP_FORMAT.toUpperCase();
    private static int audioId     = 10000;
    private static int userId      = 20000;
    private static int albumId     = 30000;
    private static int podcastId   = 40000;
    private static int genreId     = 50000;
    private static int sponsorId   = 60000;
    
    private static final SimpleDateFormat TIMESTAMP_DATE_FORMAT = new SimpleDateFormat(TIMESTAMP_FORMAT);
    
    public static String getString(String prompt) {
        if (prompt.length() > 0) {
            System.out.println(String.format("%s:", prompt));
        }
        @SuppressWarnings("resource") // Suppress because we do not want to close standard input
        Scanner inputScanner = new Scanner(System.in);
        return inputScanner.nextLine();
    }

    public static int getInt(String prompt) {
        if (prompt.length() > 0) {
            System.out.println(String.format("%s:", prompt));
        }
        @SuppressWarnings("resource") // Suppress because we do not want to close standard input
        Scanner inputScanner = new Scanner(System.in);
        do {
            String line = inputScanner.nextLine();
            try {
                int output = Integer.parseInt(line.strip());
                return output;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please input a valid integer");
            }
        } while (true);
    }
    
    public static Integer getIntOrNull(String prompt) {
        if (prompt.length() > 0) {
            System.out.println(String.format("%s:", prompt));
        }
        @SuppressWarnings("resource") // Suppress because we do not want to close standard input
        Scanner inputScanner = new Scanner(System.in);
        do {
            String line = inputScanner.nextLine().strip();
            if (line.length() <= 0) {
                return null;
            }
            try {
                int output = Integer.parseInt(line);
                return output;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please input a valid integer or an empty string");
            }
        } while (true);
    }
    
    public static double getDouble(String prompt) {
        if (prompt.length() > 0) {
            System.out.println(String.format("%s:", prompt));
        }
        @SuppressWarnings("resource") // Suppress because we do not want to close standard input
        Scanner inputScanner = new Scanner(System.in);
        do {
            String line = inputScanner.nextLine();
            try {
                double output = Double.parseDouble(line);
                return output;
            } catch (Exception e) {
                System.out.println("Invalid input. Please input a valid real number");
            }
        } while (true);
    }

    public static Timestamp getTimestamp(String prompt, boolean appendFormat) {
        if (prompt.length() > 0) {
            if (appendFormat) {
                String prompt2 = String.format("%s (%s):", prompt, TIMESTAMP_FORMAT_UPPER);
                System.out.println(prompt2);
            } else {
                System.out.println(String.format("%s:", prompt));
            }
        }
        @SuppressWarnings("resource") // Suppress because we do not want to close standard input
        Scanner inputScanner = new Scanner(System.in);
        do {
            String line = inputScanner.nextLine();
            try {
                Date date = TIMESTAMP_DATE_FORMAT.parse(line);
                Timestamp timestamp = new Timestamp(date.getTime());
                return timestamp;
            } catch (Exception e) {
                System.out.println("Invalid input. Please input a valid timestamp");
            }
        } while (true);
    }
    
    public static Timestamp getTimestamp(String prompt) {
        return getTimestamp(prompt, true);
    }
    
    public static Timestamp getTimestampOrNull(String prompt, boolean appendFormat) {
        if (prompt.length() > 0) {
            if (appendFormat) {
                String prompt2 = String.format("%s (%s):", prompt, TIMESTAMP_FORMAT_UPPER);
                System.out.println(prompt2);
            } else {
                System.out.println(String.format("%s:", prompt));
            }
        }
        @SuppressWarnings("resource") // Suppress because we do not want to close standard input
        Scanner inputScanner = new Scanner(System.in);
        do {
            String line = inputScanner.nextLine().strip();
            if (line.length() <= 0) {
                return null;
            }
            try {
                Date date = TIMESTAMP_DATE_FORMAT.parse(line);
                Timestamp timestamp = new Timestamp(date.getTime());
                return timestamp;
            } catch (Exception e) {
                System.out.println("Invalid input. Please input a valid timestamp");
            }
        } while (true);
    }
    
    public static Timestamp getTimestampOrNull(String prompt) {
        return getTimestampOrNull(prompt, true);
    }
    
    public static String getTodayDateAndTime() {
        String dateAndTimePattern   = "MM/DD/YYYY HH:mm:ss";
        DateFormat dateFmt          = new SimpleDateFormat(dateAndTimePattern);
        Date       today            = Calendar.getInstance().getTime(); 
        String     registrationDate = dateFmt.format(today);
        return registrationDate; 
    }


    public static int incrementAudioId() {
        return audioId += 1;
    }

    public static int incrementUserId() {
        return userId += 1;
    }

    public static int incrementAlbumId() {
        return albumId += 1;
    }
    
    public static int incrementPodcastId() {
    	return podcastId += 1;
    }

    public static int incrementGenreId() {
        return genreId += 1;
    }

    public static int incrementSponsorId() {
        return sponsorId += 1;
    }

    public static boolean yesNoPrompt(String prompt) {
        if (prompt.length() > 0) {
            prompt = "" + prompt + " (y/n):";
        } else {
            prompt = "(y/n):";
        }
        System.out.println(prompt);
        @SuppressWarnings("resource") // Suppress because we do not want to close standard input
        Scanner inputScanner = new Scanner(System.in);
        do {
            String line = inputScanner.nextLine().strip().toLowerCase();
            try {
                if (line.equals("y")) {
                    return true;
                } else if (line.equals("n")) {
                    return false;
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please input \"y\" or \"n\"");
            }
        } while (true);
    }
    
    @SuppressWarnings("resource") // Suppress because we do not want to close standard input
    public static String getEnumValue(String prompt, String[] values) {
        if (values.length <= 0) {
            // no enum values, treat as getString
            return getString(prompt);
        }
        // Prune nulls from values
        ArrayList<String> realValues = new ArrayList<String>(values.length);
        for (int i = 0; i < values.length; i++) {
            String val = values[i];
            // If null ignore value
            if (val != null) {
                realValues.add(val);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(realValues.get(0));
        for (int i = 1; i < realValues.size(); i++) {
            sb.append(", ");
            sb.append(realValues.get(i));
        }
        sb.append("):");
        String completePrompt = "" + prompt + "\n" + sb.toString();
        while (true) {
            System.out.println(completePrompt);
            Scanner inputScanner = new Scanner(System.in);
            String select = inputScanner.nextLine().strip();
            // Check if input value is valid
            if (realValues.contains(select)) {
                return select;
            }
            System.out.println("Invalid selection. Please select a valid value");
        }
    }

}
