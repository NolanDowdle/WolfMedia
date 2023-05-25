package main.java.wolf_media.operation.metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.DataGenUtil;
import main.java.wolf_media.util.ExitException;
import main.java.wolf_media.util.InputUtil;

public class SetArtistMonthlyListeners2 extends OperationBase {

    /**
     * SQL statement to get an artist's songs
     */
    private static final String GET_ARTIST_SONGS_STMT =
            "SELECT songId FROM PrimaryArtists WHERE artistId = ?;";
    
    /**
     * SQL statement to get a song's play count
     */
    private static final String GET_CUR_SONG_PLAY_COUNT_STMT =
            "SELECT COUNT(*) FROM Listens "
            + "WHERE audioId = ? AND playTime >= ? AND playTime < ?;";
    
    /**
     * SQL statement to delete an artist's listen entries
     */
    private static final String DELETE_ARTIST_LISTENS_STMT =
            "DELETE FROM Listens "
            + "WHERE playTime >= ? AND playTime < ? "
            + "    AND audioId IN (SELECT songId FROM PrimaryArtists WHERE artistId = ?);";
    
    /**
     * SQL statement to insert listen entry
     */
    private static final String INSERT_LISTEN_ENTRY_STMT =
            "INSERT INTO Listens(playTime, audioId, userId) "
            + "VALUES (?, ?, ?);";
    
    /**
     * Get an artist's monthly listeners
     */
    private static final String GET_ARTIST_MONTLY_LISTENERS_STMT =
            "SELECT COUNT(DISTINCT userId) FROM Listens "
            + "WHERE playTime >= ? AND playTime < ? "
            + "    AND audioId IN (SELECT songId FROM PrimaryArtists WHERE artistId = ?);";
    
    /**
     * Get an artist's song IDs
     * @param conn Database connection
     * @param artistId An artist ID
     * @return Returns a list of song IDs for songs created by the given artist
     * @throws SQLException
     */
    private static ArrayList<Integer> getArtistSongs(Connection conn, int artistId) throws SQLException {
        ArrayList<Integer> output = new ArrayList<Integer>();
        try (PreparedStatement stmt = conn.prepareStatement(GET_ARTIST_SONGS_STMT)) {
            stmt.setInt(1, artistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    output.add(rs.getInt(1));
                }
            }
        }
        return output;
    }
    
    /**
     * Get song play count
     * @param conn Database connection
     * @param songId A song ID
     * @param year A year
     * @param month A month
     * @return Returns song play count
     * @throws SQLException
     */
    private static int getCurPlayCount(Connection conn, int songId, int year, int month) throws SQLException {
        YearMonth nextMonth = DataGenUtil.getNextMonth(year, month);
        String startTimestamp = String.format("%04d-%02d-01 00:00:00", year, month);
        String endTimestamp = String.format("%04d-%02d-01 00:00:00", nextMonth.getYear(), nextMonth.getMonthValue());
        try (PreparedStatement stmt = conn.prepareStatement(GET_CUR_SONG_PLAY_COUNT_STMT)) {
            stmt.setInt(1, songId);
            stmt.setString(2, startTimestamp);
            stmt.setString(3, endTimestamp);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }   
        }
        // no response from database
        return 0;
    }
    
    /**
     * Deletes all listen entries for a given artist's song
     * @param conn Database connection
     * @param artistId An artist ID
     * @param year A year
     * @param month A month
     * @throws SQLException
     */
    private static void deleteArtistsListens(Connection conn, int artistId, int year, int month) throws SQLException {
        YearMonth nextMonth = DataGenUtil.getNextMonth(year, month);
        String startTimestamp = String.format("%04d-%02d-01 00:00:00", year, month);
        String endTimestamp = String.format("%04d-%02d-01 00:00:00", nextMonth.getYear(), nextMonth.getMonthValue());
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_ARTIST_LISTENS_STMT)) {
            stmt.setString(1, startTimestamp);
            stmt.setString(2, endTimestamp);
            stmt.setInt(3, artistId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Create many listen entries for a collection of user IDs
     * @param conn Database connection
     * @param userIds Collection of user IDs to create listen from
     * @param songId song ID for listen entries
     * @param year Year of listens
     * @param month Month of listens
     * @param amount Amount of listen entries to add
     * @throws SQLException
     */
    private static void batchAddNewListenEntries(Connection conn, ArrayList<Integer> userIds, int songId, int year, int month, int amount) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_LISTEN_ENTRY_STMT)) {
            for (int i = 0; i < amount; i++) {
                int userIndex = i % userIds.size();
                int userId = userIds.get(userIndex);
                Timestamp playTime = DataGenUtil.getRandomTimestamp(year, month);
                stmt.setTimestamp(1, playTime);
                stmt.setInt(2, songId);
                stmt.setInt(3, userId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    /**
     * Get an artist's monthly listeners
     * @param conn Database connection
     * @param artistId An artist ID
     * @param year A year
     * @param month A month
     * @return Returns an artist's monthly listeners
     * @throws SQLException
     */
    private static int getArtistMonthlyListeners(Connection conn, int artistId, int year, int month) throws SQLException {
        YearMonth nextMonth = DataGenUtil.getNextMonth(year, month);
        String startTimestamp = String.format("%04d-%02d-01 00:00:00", year, month);
        String endTimestamp = String.format("%04d-%02d-01 00:00:00", nextMonth.getYear(), nextMonth.getMonthValue());
        try(PreparedStatement stmt = conn.prepareStatement(GET_ARTIST_MONTLY_LISTENERS_STMT)) {
            stmt.setString(1, startTimestamp);
            stmt.setString(2, endTimestamp);
            stmt.setInt(3, artistId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }   
        }
        return 0;
    }
    
    /**
     * Implementation of the set artist's monthly listener operation
     * @param conn Database connection
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {
        // Get input
        int artistId = InputUtil.getInt("Input artist ID");
        // Check if song exists. If not prompt to make one
        PromptUtil.artistExistsPromptHelper(conn, artistId);
        int year = InputUtil.getInt("Input year");
        int month = InputUtil.getInt("Input month");
        if (month < 1 || month > 12) {
            throw new ExitException("Invalid month: " + month + ", exiting");
        }
        int curListenerCount = getArtistMonthlyListeners(conn, artistId, year, month);
        System.out.println("Pre-transaction Artist Monthly Listeners: " + curListenerCount);
        int newValue = InputUtil.getInt("Input new monthly listener count");
        // Get artist's songs
        ArrayList<Integer> songIds = getArtistSongs(conn, artistId);
        // Maps a songId to a play count value
        HashMap<Integer, Integer> songPlayCountMap = new HashMap<Integer, Integer>(songIds.size());
        for (int i = 0; i < songIds.size(); i++) {
            int songId = songIds.get(i);
            int songPlayCount = getCurPlayCount(conn, songId, year, month);
            songPlayCountMap.put(songId, songPlayCount);
        }
        // Clear artists listens
        deleteArtistsListens(conn, artistId, year, month);
        // Get <newValue> amount of new users
        ArrayList<Integer> newUserIds = DataGenUtil.generateNewUsers(conn, newValue);
        for (int i = 0; i < songIds.size(); i++) {
            int songId = songIds.get(i);
            int songPlayCount = songPlayCountMap.get(songId);
            batchAddNewListenEntries(conn, newUserIds, songId, year, month, songPlayCount);
        }
    }

}
