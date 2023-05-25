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

public class SetPodcastSubscribers extends OperationBase {

    // SQL statement to get current subscribers count
    private static final String GET_CUR_SUBSCRIBERS_STMT =
            "SELECT COUNT(*) FROM PodcastSubscribers WHERE podcastId = ?";
    
    // SQL statement to insert a podcast subscription
    private static final String INSERT_SUBSCRIBER_STMT =
            "INSERT INTO PodcastSubscribers(userId, podcastId) "
            + "VALUES (?, ?);";
    
    // SQL statement to get subscriber user IDs
    private static final String GET_SUBSCRIBER_USER_IDS =
            "SELECT userId FROM PodcastSubscribers WHERE podcastId = ? ORDER BY userId DESC LIMIT ?";
    
    // SQL statement to remove podcast subscribers
    private static final String DELETE_SUBSCRIBER =
            "DELETE FROM PodcastSubscribers WHERE "
            + "userId = ? "
            + "AND podcastId = ?;";
    
    /**
     * Gets podcast subscriber count
     * @param conn Database connection
     * @param podcastId A podcast ID
     * @return Returns the podcast subscriber count
     * @throws SQLException
     */
    private static int getPodcastSubscribers(Connection conn, int podcastId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(GET_CUR_SUBSCRIBERS_STMT)) {
            stmt.setInt(1, podcastId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get podcast subscriber user IDs
     * @param conn Database connection
     * @param podcastId A podcast ID
     * @param count number of subscribers to get
     * @return Returns list of subscriber user IDs
     * @throws SQLException
     */
    private static ArrayList<Integer> getSubscriberIds(Connection conn, int podcastId, int count) throws SQLException {
        ArrayList<Integer> output = new ArrayList<Integer>(count);
        try (PreparedStatement stmt = conn.prepareStatement(GET_SUBSCRIBER_USER_IDS)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt(1);
                    output.add(userId);
                }
            }
        }
        return output;
    }
    
    /**
     * Add a subscriber to database
     * @param conn Database connection
     * @param podcastId A podcast ID
     * @param addAmount the number of subscribers to add
     * @throws SQLException
     */
    private static void addSubscribers(Connection conn, int podcastId, int addAmount) throws SQLException {
        if (addAmount <= 0) {
            // cannot add zero or negative subscribers
            return;
        }
        ArrayList<Integer> userIds = DataGenUtil.generateNewUsers(conn, addAmount);
        for (int i = 0; i < addAmount; i++) {
            Integer userId = userIds.get(i);
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SUBSCRIBER_STMT)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, podcastId);
                stmt.executeUpdate();
            }
        }
    }
    
    /**
     * remove an amount of podcast subscribers from the database
     * @param conn Database connection
     * @param podcastId A podcast ID
     * @param removeAmount the amount of subscribers to remove
     * @throws SQLException
     * @throws ExitException
     */
    private static void removeSubscribers(Connection conn, int podcastId, int removeAmount) throws SQLException, ExitException {
        if (removeAmount <= 0) {
            // cannot remove zero or negative subscribers
            return;
        }
        ArrayList<Integer> subIds = getSubscriberIds(conn, podcastId, removeAmount);
        if (subIds.size() < removeAmount) {
            boolean doPartial = InputUtil.yesNoPrompt("Only able to remove " + subIds.size() + " subscribers. Continue anyway?");
            if (!doPartial) {
                throw new ExitException("Exiting operation");
            }
            removeAmount = subIds.size();
        }
        for (int i = 0; i < removeAmount; i++) {
            Integer userId = subIds.get(i);
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_SUBSCRIBER)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, podcastId);
                stmt.executeUpdate();
            }
        }
    }
    
    /**
     * Implementation of the "Set podcast subscriber count" operation
     * @param conn Database connection
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {
        int podcastId = InputUtil.getInt("Input podcast ID");
        PromptUtil.podcastExistsPromptHelper(conn, podcastId);
        int newSubscribers = InputUtil.getInt("Input new subscriber count");
        // Get current subscriber count
        int curSubscribers = getPodcastSubscribers(conn, podcastId);
        if (newSubscribers > curSubscribers) {
            // Add subscribers
            int addAmount = newSubscribers - curSubscribers;
            System.out.println("Adding " + addAmount + " subscribers");
            addSubscribers(conn, podcastId, addAmount);
        } else if (newSubscribers < curSubscribers) {
            // Remove subscribers
            int removeAmount = curSubscribers - newSubscribers;
            System.out.println("Removing " + removeAmount + " subscribers");
        } else {
            // Nothing to do
            System.out.println("Podcast " + podcastId + " already has " + newSubscribers + " subscribers");
        }
    }

}
