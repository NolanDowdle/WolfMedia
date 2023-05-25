package main.java.wolf_media.operation.metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.DataGenUtil;
import main.java.wolf_media.util.ExitException;
import main.java.wolf_media.util.InputUtil;

public class SetPodcastListeningCount extends OperationBase {
    
    // SQL statement to get monthly listeners
    private static final String GET_CUR_MONTHLY_LISTENERS_STMT =
            "SELECT COUNT(*) "
            + "FROM ( "
            + "SELECT DISTINCT userId "
            + "FROM Listens "
            + "WHERE audioId = ? AND playTime >= ? AND playTime < ?) l;";
    
    // SQL statement to add listen entries
    private static final String INSERT_LISTEN_ENTRY =
            "INSERT INTO Listens(playTime, audioId, userId) "
            + "VALUES (?, ?, ?);";
    
    // SQL statement to get listen statements from users that have multiple listen statements
    private static final String REMOVABLE_LISTENS_STMT =
            "SELECT l1.playTime, l1.audioId, l1.userId "
            + "FROM Listens l1 "
            + "JOIN ( "
            + "SELECT userId, COUNT(*) as count "
            + "FROM Listens "
            + "WHERE "
            + "audioId = ? AND playTime >= ? AND playTime < ? "
            + "GROUP BY userId "
            + "LIMIT ?) l2 "
            + "ON l1.userId = l2.userId "
            + "WHERE "
            + "audioId = ? AND playTime >= ? AND playTime < ? "
            + "ORDER BY l2.count ASC;";
    
    // SQL statement to delete a listen entry
    private static final String REMOVE_LISTEN_STMT = 
            "DELETE FROM Listens WHERE "
            + "playTime = ? "
            + "AND audioId = ? "
            + "AND userId = ?;";

    /**
     * Get a podcast episode's monthly listeners
     * @param conn Database connection
     * @param podEpId A podcast episode ID
     * @param year A year
     * @param month A month
     * @return Returns the monthly listeners for a given podcast episode
     * @throws SQLException
     */
    private static int getMonthlyListeners(Connection conn, int podEpId, int year, int month) throws SQLException {
        YearMonth nextMonth = DataGenUtil.getNextMonth(year, month);
        String startTimestamp = String.format("%04d-%02d-01 00:00:00", year, month);
        String endTimestamp = String.format("%04d-%02d-01 00:00:00", nextMonth.getYear(), nextMonth.getMonthValue());
        try (PreparedStatement stmt = conn.prepareStatement(GET_CUR_MONTHLY_LISTENERS_STMT)) {
            stmt.setInt(1, podEpId);
            stmt.setString(2, startTimestamp);
            stmt.setString(3, endTimestamp);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    return rs.getInt(1);
                }
            }   
        }
        // no response from database
        return 0;
    }
    
    
    /**
     * Get a list of removable listen entries.
     * This is used to maintain the same play count with different monthly listeners
     * @param conn Database connection
     * @param podEpId A podcast episode ID
     * @param year A year
     * @param month A month
     * @param removeCount Amount of listen entries to get
     * @return Get a list of removable listen entries
     * @throws SQLException
     */
    private static ArrayList<ListenEntry> getRemovableListens(
            Connection conn, int podEpId, int year, int month, int removeCount) throws SQLException
    {
        // Calculate time range
        String startTimestamp = String.format("%04d-%02d-01 00:00:00", year, month);
        int endMonth = month + 1;
        int endYear = year;
        if (month == 12) {
            endMonth = 1;
            endYear += 1;
        }
        String endTimestamp = String.format("%04d-%02d-01 00:00:00", endYear, endMonth);
        // Run statement
        ArrayList<ListenEntry> output = new ArrayList<ListenEntry>();
        try (PreparedStatement stmt = conn.prepareStatement(REMOVABLE_LISTENS_STMT)) {
            stmt.setInt(1, podEpId);
            stmt.setString(2, startTimestamp);
            stmt.setString(3, endTimestamp);
            stmt.setInt(4, removeCount);
            stmt.setInt(5, podEpId);
            stmt.setString(6, startTimestamp);
            stmt.setString(7, endTimestamp);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp(1);
                    int audioId = rs.getInt(2);
                    int userId = rs.getInt(3);
                    output.add(new ListenEntry(timestamp, audioId, userId));
                }
            }
        }
        return output;
    }
    
    /**
     * Gets the number of unique users in a listens collection
     * @param listensList list of listen entries
     * @return Gets the number of unique users in a listens collection
     */
    private static int getUserCount(ArrayList<ListenEntry> listensList) {
        HashSet<Integer> userIds = new HashSet<Integer>();
        for (int i = 0; i < listensList.size(); i++) {
            ListenEntry entry = listensList.get(i);
            userIds.add(entry.userId);
        }
        return userIds.size();
    }
    
    /**
     * Adds listen entries to the database
     * @param conn Database connection
     * @param podEpId podcast episode ID
     * @param year A year
     * @param month A month
     * @param addAmount amount of listen entries to add
     * @throws SQLException
     */
    private static void addMonthlyListeners(Connection conn, int podEpId, int year, int month, int addAmount) throws SQLException {
        if (addAmount <= 0) {
            // Cannot add 0 or negative listeners
            return;
        }
        ArrayList<Integer> newUserIds = DataGenUtil.generateNewUsers(conn, addAmount);
        for (int i = 0; i < addAmount; i++) {
            Integer newUserId = newUserIds.get(i);
            Timestamp playTime = DataGenUtil.getRandomTimestamp(year, month);
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_LISTEN_ENTRY)) {
                stmt.setTimestamp(1, playTime);
                stmt.setInt(2, podEpId);
                stmt.setInt(3, newUserId);
            }
        }
    }

    /**
     * Removes listen entries from database
     * @param conn Database connection
     * @param podEpId A podcast ID
     * @param year A year
     * @param month A month
     * @param removeAmount amount of listen entries to remove
     * @throws SQLException
     * @throws ExitException
     */
    private static void removeMonthlyListeners(Connection conn, int podEpId, int year, int month, int removeAmount) throws SQLException, ExitException {
        if (removeAmount <= 0) {
            // Cannot remove 0 or negative listeners
            return;
        }
        ArrayList<ListenEntry> removables = getRemovableListens(conn, podEpId, year, month, removeAmount);
        int userCount = getUserCount(removables);
        if (userCount < removeAmount) {
            boolean doPartial = InputUtil.yesNoPrompt("Can only remove " + userCount + " monthly listeners. Remove that amount anyway?");
            if (!doPartial) {
                throw new ExitException("Exiting operation");
            }
        }
        // Perform operation
        for (int i = 0; i < removables.size(); i++) {
            ListenEntry entry = removables.get(i);
            try (PreparedStatement stmt = conn.prepareStatement(REMOVE_LISTEN_STMT)) {
                stmt.setTimestamp(1, entry.playTime);
                stmt.setInt(2, entry.audioId);
                stmt.setInt(3, entry.userId);
                stmt.executeUpdate();
            }
        }
    }
    
    /**
     * Implementation of the "set podcast listening count" operation
     * @param conn Database connection
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {
        // Get input
        int podEpId = InputUtil.getInt("Input podcast episode ID");
        // Check if podcastEpisode exists. If not prompt to make one
        PromptUtil.podcastEpisodeExistsPromptHelper(conn, podEpId);
        int year = InputUtil.getInt("Input year");
        int month = InputUtil.getInt("Input month");
        if (month < 1 || month > 12) {
            throw new ExitException("Invalid month: " + month + ", exiting");
        }
        int newValue = InputUtil.getInt("Input new monthly listener count");
        // Get current listen count
        int curMonthlyListeners = getMonthlyListeners(conn, podEpId, year, month);
        if (newValue > curMonthlyListeners) {
            // add listeners
            int addAmount = newValue - curMonthlyListeners;
            System.out.println("Adding " + addAmount + " listeners");
            addMonthlyListeners(conn, podEpId, year, month, addAmount);
        } else if (newValue < curMonthlyListeners) {
            // remove listeners
            int removeAmount = curMonthlyListeners - newValue;
            System.out.println("Removing " + removeAmount + " listeners");
            removeMonthlyListeners(conn, podEpId, year, month, removeAmount);
        } else {
            // no work to do
            System.out.println("Podcast Episode " + podEpId + " already has " + newValue + " monthly listeners");
        }
    }

    
}
