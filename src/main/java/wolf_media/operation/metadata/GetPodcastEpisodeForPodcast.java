package main.java.wolf_media.operation.metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * Returns the podcast episodes for a given podcast.
 * 
 * @author John Fagan
 */
public class GetPodcastEpisodeForPodcast extends OperationBase {

    /**
     * SQL statement to get the podcast episodes for a given podcast.
     */
    private static final String QUERY =
            "SELECT PodcastEpisodes.episodeId, PodcastAudios.adCount, PodcastEpisodes.episodeNumber, AudioEntities.title, "
            + "AudioEntities.duration, AudioEntities.releaseDate, AudioEntities.releaseCountry, AudioEntities.releaseLanguage "
            + "FROM PodcastEpisodes "
            + "JOIN PodcastAudios "
            + "ON PodcastEpisodes.episodeId = PodcastAudios.audioId "
            + "JOIN AudioEntities "
            + "ON PodcastAudios.audioId = AudioEntities.audioId "
            + "WHERE PodcastEpisodes.podcastId = ?;";
    
    /**
     * Prompts user for a podcast ID and retrieves its episodes.
     * Transaction commits/rollbacks are handled by the OperationBase base class.
     * 
     * @param conn Database connection
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        // Prompt for input
        int podcastId = InputUtil.getInt("Input podcast ID");
        // Execute SQL statement
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
            stmt.setInt(1, podcastId);
            try (ResultSet rs = stmt.executeQuery()) {
                try {
                    OutputUtil.printResultSet(rs);
                } catch (Exception e) {
                    // This shouldn't happen unless the database looses connection
                    e.printStackTrace();
                }
            }
        }
    }

}
