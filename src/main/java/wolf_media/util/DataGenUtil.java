package main.java.wolf_media.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Random;

public class DataGenUtil {

    // Random number generator
    private static final Random RND = new Random();
    
    // SQL statement to get user IDs
    private static final String GET_USER_IDS_STMT =
            "SELECT userId FROM Users;";
    
    // SQL statement to insert a new user
    private static final String INSERT_USERS_STMT =
            "INSERT INTO Users(userId, email, firstName, lastName, country, city, subFee, bankAccountNumber, acctStatus, regDate, phoneNum) VALUES "
            + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    
    // Get a new random user ID
    public static int getRandomNewUserId(Connection conn) throws SQLException {
        ArrayList<Integer> userIds = new ArrayList<Integer>();
        try (PreparedStatement stmt = conn.prepareStatement(GET_USER_IDS_STMT)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt(1);
                    userIds.add(userId);
                }
            }
        }
        int outputUserId = RND.nextInt(1000000) + 999001;
        while (userIds.contains(outputUserId)) {
            outputUserId = RND.nextInt(1000000) + 999001;
        }
        return outputUserId;
    }
    
    // Get many new user IDs
    public static ArrayList<Integer> getManyRandomNewUserId(Connection conn, int count) throws SQLException {
        ArrayList<Integer> userIds = new ArrayList<Integer>();
        try (PreparedStatement stmt = conn.prepareStatement(GET_USER_IDS_STMT)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt(1);
                    userIds.add(userId);
                }
            }
        }
        ArrayList<Integer> output = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            int userId = RND.nextInt(1000000) + 999001;
            while (userIds.contains(userId)) {
                userId = RND.nextInt(1000000) + 999001;
            }
            output.add(userId);
        }
        return output;
    }

    // Generate a random bank account value
    private static String getRandomBankAccount() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(RND.nextInt(10));
        }
        return sb.toString();
    }
    
    // Gets the last day of the month
    private static int getRandomDayOfMonth(int month) {
        switch (month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            return 31;
        case 4:
        case 6:
        case 9:
        case 11:
            return 30;
        case 2:
        default:
            return 28;
        }
    }
    
    // Generates a random timestamp with given year and month
    public static Timestamp getRandomTimestamp(int year, int month) {
        int day = RND.nextInt(getRandomDayOfMonth(month)) + 1;
        int hour = RND.nextInt(24);
        int minute = RND.nextInt(60);
        int second = RND.nextInt(60);
        LocalDateTime datetime = LocalDateTime.of(year, month, day, hour, minute, second);
        return Timestamp.valueOf(datetime);
    }
    
    // Generates a random timestamp with given year
    public static Timestamp getRandomTimestamp(int year) {
        int month = RND.nextInt(12) + 1;
        return getRandomTimestamp(year, month);
    }
    
    // Generate random phone number
    private static String getRandomPhoneNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        sb.append(RND.nextInt(9) + 1);
        for (int i = 0; i < 10; i++) {
            sb.append(RND.nextInt(10));
        }
        return sb.toString();
    }
    
    // Generate a collection of random users
    public static ArrayList<Integer> generateNewUsers(Connection conn, int count) throws SQLException {
        ArrayList<Integer> userIds = getManyRandomNewUserId(conn, count);
        for (int i = 0; i < userIds.size(); i++) {
            int userId = userIds.get(i);
            boolean done = false;
            while (!done) {
                try {
                    String email = String.format("dummy%d@mail.com", userId);
                    String firstName = String.format("dummy-%d", userId);
                    String lastName = "";
                    String country = "US";
                    String city = "New York";
                    double subFee = 0;
                    String bankAccount = getRandomBankAccount();
                    String acctStatus = "free";
                    Timestamp regDate = getRandomTimestamp(2020);
                    String phoneNum = getRandomPhoneNumber();
                    try (PreparedStatement stmt = conn.prepareStatement(INSERT_USERS_STMT)) {
                        stmt.setInt(1, userId);
                        stmt.setString(2, email);
                        stmt.setString(3, firstName);
                        stmt.setString(4, lastName);
                        stmt.setString(5, country);
                        stmt.setString(6, city);
                        stmt.setDouble(7, subFee);
                        stmt.setString(8, bankAccount);
                        stmt.setString(9, acctStatus);
                        stmt.setTimestamp(10, regDate);
                        stmt.setString(11, phoneNum);
                        stmt.executeUpdate();
                    }
                    done = true;
                    break;
                } catch (Exception ignore) {}
            }
        }
        return userIds;
    }
    
    // Get the next month based on the year and month
    public static YearMonth getNextMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.plusMonths(1);
    }
}
