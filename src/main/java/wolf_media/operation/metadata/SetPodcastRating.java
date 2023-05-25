package main.java.wolf_media.operation.metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.DataGenUtil;
import main.java.wolf_media.util.ExitException;
import main.java.wolf_media.util.InputUtil;

public class SetPodcastRating extends OperationBase {

    // SQL statement to delete all ratings for a podcast
    private static final String DELETE_PODCAST_RATINGS_STMT =
            "DELETE FROM PodcastRatings WHERE podcastId = ?;";
    
    // SQL statement to get any user ID
    private static final String GET_ANY_USER_STMT =
            "SELECT userId FROM Users LIMIT 1;";
    
    // SQL statement to enter a new podcast Rating
    private static final String ENTER_RATING_QUERY =
            "INSERT INTO PodcastRatings(userId, podcastId, rating) VALUES (?, ?, ?);";
    
    /**
     * Deletes all ratings for a podcast
     * @param conn Database connection
     * @param podcastId A podcast ID
     * @throws SQLException
     */
    private static void deletePodcastRatings(Connection conn, int podcastId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_PODCAST_RATINGS_STMT)) {
            stmt.setInt(1, podcastId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Gets any user ID from the database. If no users exists one is generated
     * @param conn Database connection
     * @return A user ID
     * @throws SQLException
     */
    private static int getAnyUserId(Connection conn) throws SQLException {
        // Attempt to get any user from the database
        try (PreparedStatement stmt = conn.prepareStatement(GET_ANY_USER_STMT)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        // No users generate one and return it
        return DataGenUtil.generateNewUsers(conn, 1).get(0);
    }
    
    /**
     * Adds a rating for a podcast
     * @param conn Database connection
     * @param podcastId A podcast ID
     * @param userId User ID who made the rating
     * @param rating new podcast rating
     * @throws SQLException
     */
    private static void addPodcastRating(Connection conn, int podcastId, int userId, double rating) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(ENTER_RATING_QUERY)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, podcastId);
            stmt.setDouble(3, rating);
            stmt.executeUpdate();
        }
    }

    /**
     * Implementation of "Set podcast ratings" operation
     * @param conn Database connection
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {
        int podcastId = InputUtil.getInt("Input podcast ID");
        PromptUtil.podcastExistsPromptHelper(conn, podcastId);
        double newRating = InputUtil.getDouble("Input new podcast rating");
        // Delete all ratings for a given podcast, so it can be updated to the new value
        deletePodcastRatings(conn, podcastId);
        // Get user to create new rating
        int userId = getAnyUserId(conn);
        // Set podcast rating
        addPodcastRating(conn, podcastId, userId, newRating);
    }

}
