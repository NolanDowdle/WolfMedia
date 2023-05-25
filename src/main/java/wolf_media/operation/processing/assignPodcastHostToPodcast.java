// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.processing;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.atom.insertPodcastHost;
import main.java.wolf_media.operation.atom.insertPodcast;
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.ExitException;

/*
 * Assign Podcast Host Episode to Podcast
 * 
 * @author KR
 */
public class assignPodcastHostToPodcast extends OperationBase {

    private static final OperationBase insertPodcastHost   = new insertPodcastHost();
    private static final OperationBase insertPodcast       = new insertPodcast();
    private static final String doesPodcastHostExistSQL    = "SELECT 1 FROM Users WHERE firstName = ? AND lastName = ?";
    private static final String doesPodcastExistSQL        = "SELECT 1 FROM AudioEntities WHERE title = ?";

    /**
     * Perform PodcastHost := Podcast assignment
     * 
     * @param conn     The connection used to interact with MariaDb
     * @return Success if committed; SQLException in case of err
     * @throws         ExitException
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {

        /*
         * First, I need to ensure the Podcast Host exist 
         *   If not then I need to create an entry
         * 
         * Then I need to understand whether the Podcast exist 
         *   If exist then our assumption is one podcast host per podcast hence exit 
         *   If not exist then create the podcast & assign a host to it
         */
        String podcastHostFirstName = InputUtil.getString("What is the podcast host first name?");
        String podcastHostLastName  = InputUtil.getString("What is the podcast host last name?");
        try (PreparedStatement existsStmt = conn.prepareStatement(doesPodcastHostExistSQL)) {
            existsStmt.setString(1, podcastHostFirstName);
            existsStmt.setString(2, podcastHostLastName);
            boolean podcastHostExists = false;
            try (ResultSet existsRS = existsStmt.executeQuery()) {
                // True if a row is returned from the query, false otherwise
                podcastHostExists = existsRS.next();
            }
            if (podcastHostExists) {
                System.out.println("Podcast host already exist in the database.");
                // IF NO
            } else {
                System.out.println("Podcast host not found in database hence we need to insertPodcastHost");
                insertPodcastHost.executePartial(conn);
                System.out.println("Podcast host inserted into Db");
            }
        }

        String podcastName = InputUtil.getString("What is the podcast name?");
        try (PreparedStatement existsStmt = conn.prepareStatement(doesPodcastExistSQL)) {
            existsStmt.setString(1, podcastName);
            boolean podcastExists = false;
            try (ResultSet existsRS = existsStmt.executeQuery()) {
                // True if a row is returned from the query, false otherwise
                podcastExists = existsRS.next();
            }
            if (podcastExists) {
                System.out.println("Podcast already exist in the database hence it was already assigned to Podcast Host");
              // IF NO
            } else {
                System.out.println("Podcast name not found in Db hence we need to insertPodcast which will do the assignment");
                insertPodcast.executePartial(conn);
                System.out.println("Podcast host was assigned to podcast");
            }
        }
    }
}