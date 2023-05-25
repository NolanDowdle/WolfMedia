// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.processing;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.atom.insertPodcastEpisode;
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.ExitException;

/*
 * Assign Podcast Episode to Podcast
 * 
 * @author KR
 */
public class assignPodcastEpisodeToPodcast extends OperationBase {

    private static final OperationBase insertPodcastEpisode   = new insertPodcastEpisode();
    private static final String doesPodcastEpisodeExistSQL    = "SELECT 1 FROM AudioEntities WHERE title = ?";

    /**
     * Perform PodcastEpisode := Podcast assignment
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     * @throws          ExitException
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {

        /*
         * This class is simple since insertPodcastEpisode() handles the assignment of podcast episode to podcast
         *   If the podcast already exist then it is a safe assumption how it was already assigned to a podcast
         */
        String podcastEpisodeName = InputUtil.getString("What is the podcast episode title?");
        try (PreparedStatement existsStmt = conn.prepareStatement(doesPodcastEpisodeExistSQL)) {
            existsStmt.setString(1, podcastEpisodeName);
            boolean podcastEpisodeExists = false;
            try (ResultSet existsRS = existsStmt.executeQuery()) {
                // True if a row is returned from the query, false otherwise
                podcastEpisodeExists = existsRS.next();
            }
            if (podcastEpisodeExists) {
                System.out.println("Podcast episode already exist in the database hence it was already assigned to Podcast");
                // IF NO
            } else {
                System.out.println("Podcast episode name not found in database hence we need to insertPodcastEpisode which will assign podcast episode to a podcast");
                insertPodcastEpisode.execute(conn);
                System.out.println("Podcast episode was assigned to podcast");
            }
        }
    }
}