// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.atom;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;

/**
 * Delete a Podcast Host
 * 
 * @author KR
 * 
 */
public class deletePodcastHost extends OperationBase {
    // Define my static SQL query statements
    private static final String retrieveuserId                                  = "SELECT userId FROM Users WHERE firstName = ? and lastName = ?";
    private static final String deleteUserFromPodcastHosttTable                 = "DELETE FROM PodcastHosts WHERE userId = ?";
    private static final String deletePodcastHostFromUsersTable                 = "DELETE FROM Users WHERE userId = ?";
    private static final String deletePodcastHostFromPodcastToPodcastHostTable  = "DELETE FROM PodcastToPodcastHost WHERE hostId = ?";

    private static final String doesPodcastHostExistInPToPodcastHTable          = "SELECT 1 FROM PodcastToPodcastHost WHERE hostId = ?";
    /**
     * Delete Podcast Host
     * 
     * @param conn     The connection used to interact with MariaDb
     * @return Success if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        String podcastHostToDeleteFirstName = InputUtil.getString("What is the podcast host first name that you want to DELETE?");
        String podcastHostToDeleteLastName  = InputUtil.getString("What is the podcast host last name that you want to DELETE?");
        try (PreparedStatement stmt = conn.prepareStatement(retrieveuserId)) {
            stmt.setString(1, podcastHostToDeleteFirstName);
            stmt.setString(2, podcastHostToDeleteLastName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    try (PreparedStatement existsStmt = conn.prepareStatement(doesPodcastHostExistInPToPodcastHTable)) {
                        existsStmt.setInt(1, userId);
                        boolean doesPodcastHostExist = false;
                        try (ResultSet existsRS = existsStmt.executeQuery()) {
                            doesPodcastHostExist = existsRS.next();
                        }
                        if (doesPodcastHostExist) {
                            try (PreparedStatement deleteStmt = conn
                                    .prepareStatement(deletePodcastHostFromPodcastToPodcastHostTable)) {
                                deleteStmt.setInt(1, userId);
                                deleteStmt.executeUpdate();
                            }
                        }
                    }
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteUserFromPodcastHosttTable)) {
                        deleteStmt.setInt(1, userId);
                        deleteStmt.executeUpdate();   
                    }
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deletePodcastHostFromUsersTable)) {
                        deleteStmt.setInt(1, userId);
                        deleteStmt.executeUpdate();
                    }
                }
            }
        }
    }
}