package main.java.wolf_media.operation.metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.DataGenUtil;
import main.java.wolf_media.util.ExitException;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.TriValue;

public class SetSongPlayCount extends OperationBase {

    // Rnadom number generator
    private static final Random RND = new Random();
    
    // SQL statement to get current song play count
    private static final String GET_CUR_SONG_PLAY_COUNT_STMT =
            "SELECT COUNT(*) FROM Listens "
            + "WHERE audioId = ? AND playTime >= ? AND playTime < ?;";
    
    // SQL statement to get audio play times
    private static final String GET_PLAY_TIMES_STMT = 
            "SELECT playTime FROM Listens "
            + "WHERE audioId = ? AND playTime >= ? AND playTime < ?;";
    
    // SQL statement to get listener user IDs
    private static final String GET_SONG_LISTENERS_STMT =
            "SELECT DISTINCT userId FROM Listens "
            + "WHERE audioId = ? AND playTime >= ? AND playTime < ?;";
    
    // SQL statement to get a user ID
    private static final String GET_REAL_USER_ID_STMT =
            "SELECT DISTINCT userId FROM Listens WHERE audioId = ? ORDER BY userId DESC LIMIT 1;";
    
    // SQL statement to insert a play count
    private static final String INSERT_PLAYCOUNT_ENTRY =
            "INSERT INTO Listens(playTime, audioId, userId) "
            + "VALUES (?, ?, ?);";
    
    // SQL statement to remove song listen entries
    private static final String REMOVABLE_LISTENS_STMT =
            "SELECT playTime, audioId, userId "
            + "FROM Listens "
            + "WHERE "
            + "audioId = ? AND playTime >= ? AND playTime < ? "
            + "AND userId IN ("
            + "SELECT userId FROM Listens "
            + "WHERE audioId = ? AND playTime >= ? AND playTime < ? GROUP BY userId HAVING COUNT(*) > 1);";
    
    // SQL statement to remove a listen entry
    private static final String REMOVE_LISTEN =
            "DELETE FROM Listens WHERE "
            + "playTime = ? "
            + "AND audioId = ? "
            + "AND userId = ?;";
    
    private static int getCurPlayCount(Connection conn, int songId, int year, int month) throws SQLException {
        YearMonth nextMonth = DataGenUtil.getNextMonth(year, month);
        String startTimestamp = String.format("%04d-%02d-01 00:00:00", year, month);
        String endTimestamp = String.format("%04d-%02d-01 00:00:00", nextMonth.getYear(), nextMonth.getMonthValue());
        try (PreparedStatement stmt = conn.prepareStatement(GET_CUR_SONG_PLAY_COUNT_STMT)) {
            stmt.setInt(1, songId);
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
    
    // Get listeners for a song on a given month
    private static ArrayList<Integer> getSongListeners(Connection conn, int songId, int year, int month) throws SQLException {
        String startTimestamp = String.format("%04d-%02d-01 00:00:00", year, month);
        int endMonth = month + 1;
        int endYear = year;
        if (month == 12) {
            endMonth = 1;
            endYear += 1;
        }
        String endTimestamp = String.format("%04d-%02d-01 00:00:00", endYear, endMonth);
        try (PreparedStatement stmt = conn.prepareStatement(GET_SONG_LISTENERS_STMT)) {
            stmt.setInt(1, songId);
            stmt.setString(2, startTimestamp);
            stmt.setString(3, endTimestamp);
            ArrayList<Integer> listeners = new ArrayList<Integer>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt(1);
                    listeners.add(userId);
                }
            }
            return listeners;
        }
    }
    
    /**
     * Gets an existing user ID
     * @param conn Database connection
     * @param songId Song ID
     * @return Returns a song ID
     * @throws SQLException
     */
    private static int getAnyExistingUserId(Connection conn, int songId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(GET_REAL_USER_ID_STMT)) {
            stmt.setInt(1, songId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    // No users create one
                    ArrayList<Integer> newUsers = DataGenUtil.generateNewUsers(conn, 1);
                    return newUsers.get(0);
                }
            }
        }
    }
    
    /**
     * Get timestamps for listen entries. This is used to avoid listen entries colliding
     * @param conn Database connection
     * @param songId A song ID
     * @param year A year
     * @param month A month
     * @return Returns listen entry timestamps
     * @throws SQLException
     */
    private static ArrayList<Timestamp> getSongListenTimestamps(Connection conn, int songId, int year, int month) throws SQLException {
        String startTimestamp = String.format("%04d-%02d-01 00:00:00", year, month);
        int endMonth = month + 1;
        int endYear = year;
        if (month == 12) {
            endMonth = 1;
            endYear += 1;
        }
        String endTimestamp = String.format("%04d-%02d-01 00:00:00", endYear, endMonth);
        try (PreparedStatement stmt = conn.prepareStatement(GET_PLAY_TIMES_STMT)) {
            stmt.setInt(1, songId);
            stmt.setString(2, startTimestamp);
            stmt.setString(3, endTimestamp);
            ArrayList<Timestamp> timestamps = new ArrayList<Timestamp>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp(1);
                    timestamps.add(timestamp);
                }
            }
            return timestamps;
        }
    }
    
    /**
     * Get listen entries that can be removed to match desired play count
     * @param conn Database connection
     * @param songId A song ID
     * @param year A year
     * @param month A month
     * @return Returns a list of listen entries that can be removed without modifying 
     * @throws SQLException
     */
    private static ArrayList<ListenEntry> getRemovableListens(Connection conn, int songId, int year, int month) throws SQLException {
        String startTimestamp = String.format("%04d-%02d-01 00:00:00", year, month);
        int endMonth = month + 1;
        int endYear = year;
        if (month == 12) {
            endMonth = 1;
            endYear += 1;
        }
        String endTimestamp = String.format("%04d-%02d-01 00:00:00", endYear, endMonth);
        ArrayList<Integer> seenUserIds = new ArrayList<Integer>(16);
        ArrayList<ListenEntry> output = new ArrayList<ListenEntry>();
        try (PreparedStatement stmt = conn.prepareStatement(REMOVABLE_LISTENS_STMT)) {
            stmt.setInt(1, songId);
            stmt.setString(2, startTimestamp);
            stmt.setString(3, endTimestamp);
            stmt.setInt(4, songId);
            stmt.setString(5, startTimestamp);
            stmt.setString(6, endTimestamp);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp(1);
                    int audioId = rs.getInt(2);
                    int userId = rs.getInt(3);
                    if (!seenUserIds.contains(userId)) {
                        // Skip first instance of a user to avoid changing listener count
                        // This ensures at least one listen entry per unique user remains in the database
                        seenUserIds.add(userId);
                        continue;
                    }
                    ListenEntry entry = new ListenEntry(timestamp, audioId, userId);
                    output.add(entry);
                }
            }
        }
        return output;
    }
    
    /**
     * Add an amount of play counts to for a given song
     * @param conn Database connection
     * @param songId song ID
     * @param year A year
     * @param month A month
     * @param addAmount Amount of play counts to add
     * @throws SQLException
     */
    private static void addPlayCount(Connection conn, int songId, int year, int month, int addAmount) throws SQLException {
        if (addAmount <= 0) {
            // cannot add 0 or negative play counts
            return;
        }
        ArrayList<Integer> listeners = getSongListeners(conn, songId, year, month);
        if (listeners.size() <= 0) {
            listeners.clear();
            listeners.add(getAnyExistingUserId(conn, songId));
        }
        ArrayList<Timestamp> timestamps = getSongListenTimestamps(conn, songId, year, month);
        int songCountAdded = 0;
        while (songCountAdded < addAmount) {
            int userId = listeners.get(RND.nextInt(listeners.size()));
            Timestamp timestamp = DataGenUtil.getRandomTimestamp(year, month);
            while (timestamps.contains(timestamp)) {
                timestamp = DataGenUtil.getRandomTimestamp(year, month);
            }
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_PLAYCOUNT_ENTRY)) {
                stmt.setTimestamp(1, timestamp);
                stmt.setInt(2, songId);
                stmt.setInt(3, userId);
                stmt.executeUpdate();
                songCountAdded++;
            } catch (SQLException e) {}
        }
    }
    
    /**
     * Removes play counts for a given song
     * @param conn Database connection
     * @param songId A song ID
     * @param year A year
     * @param month A month
     * @param removeAmount Amount of play counts to remove
     * @throws SQLException
     * @throws ExitException
     */
    private static void removePlayCount(Connection conn, int songId, int year, int month, int removeAmount) throws SQLException, ExitException {
        if (removeAmount <= 0) {
            // Cannot remove 0 or negative play counts
            return;
        }
        ArrayList<ListenEntry> removables = getRemovableListens(conn, songId, year, month);
        Collections.shuffle(removables);
        if (removables.size() < removeAmount) {
            boolean doPartial = InputUtil.yesNoPrompt("Can only remove " + removables.size() + " without affecting the listener count. Remove that amount anyway?");
            if (!doPartial) {
                throw new ExitException("Exiting operation");
            }
            removeAmount = removables.size();
        }
        // Perform operation
        for (int i = 0; i < removeAmount; i++) {
            ListenEntry entry = removables.get(i);
            try (PreparedStatement stmt = conn.prepareStatement(REMOVE_LISTEN)) {
                stmt.setTimestamp(1, entry.playTime);
                stmt.setInt(2, entry.audioId);
                stmt.setInt(3, entry.userId);
                stmt.executeUpdate();
            }
        }
    }
    
    /**
     * Implementation of the "Set song play count" operation
     * @param conn Database connection
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {
        int songId = InputUtil.getInt("Input song ID");
        TriValue.Value songExistsVal = PromptUtil.songExistsPrompt(conn, songId);
        if (songExistsVal == TriValue.Value.EXIT) {
            throw new ExitException("No song, exiting");
        } else if (songExistsVal == TriValue.Value.FALSE) {
            throw new ExitException("No song with ID " + songId + " found, exiting");
        }
        int year = InputUtil.getInt("Input year");
        int month = InputUtil.getInt("Input month");
        if (month < 1 || month > 12) {
            throw new ExitException("Invalid month: " + month + ", exiting");
        }
        int newValue = InputUtil.getInt("Input new play count");
        // Get current play count
        int curPlayCount = getCurPlayCount(conn, songId, year, month);
        System.out.println("Currently have " + curPlayCount + " play count");
        if (newValue > curPlayCount) {
            // add play count
            int addAmount = newValue - curPlayCount;
            System.out.println("Adding " + addAmount + " play counts");
            addPlayCount(conn, songId, year, month, addAmount);
        } else if (newValue < curPlayCount) {
            // remove play count
            int removeAmount = curPlayCount - newValue;
            System.out.println("Removing " + removeAmount + " play counts");
            removePlayCount(conn, songId, year, month, removeAmount);
        } else {
         // no work to do
            System.out.println("Song " + songId + " already has " + newValue + " play count");
        }
    }

}
