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
 * Delete a Podcast Episode
 * 
 * @author KR
 * 
 */
public class deletePodcastEpisode extends OperationBase {
    private static final String deletePodcastEpisodeFromAudioETableQuery            = "DELETE FROM AudioEntities WHERE audioId = ?";
    private static final String deletePodcastEpisodeFromPodcastAudioTableQuery      = "DELETE FROM PodcastAudios WHERE audioId = ?";
    private static final String deletePodcastEpisodeFromPodcastGuestsTableQuery     = "DELETE FROM PodcastGuests WHERE audioId = ?";
    private static final String deletePodcastEpisodeFromPodcastEpisodesTableQuery   = "DELETE FROM PodcastEpisodes WHERE episodeId = ?";
    private static final String retrieveAudioId                                     = "SELECT audioId FROM AudioEntities WHERE title = ?";
    
    /**
     * Delete a Podcast Episode from the following TABLES: 
     *   AudioEntities
     *   PodcastAudios
     *   PodcastGuests
     *   PodcastEpisodes
     * 
     * @param  conn     The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        String podcastEpisodeToDelete = InputUtil.getString("What is the podcast episode title you want to delete?");
        try (PreparedStatement stmt = conn.prepareStatement(retrieveAudioId)) {
            stmt.setString(1, podcastEpisodeToDelete);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int podcastEpisodeAudioId = rs.getInt("audioId");
                    /*
                     * You MUST DELETE FROM AudioEntities LAST since PodcastAudios has a foreign key from AudioEntities
                     */
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deletePodcastEpisodeFromPodcastEpisodesTableQuery)) {
                        deleteStmt.setInt(1, podcastEpisodeAudioId);
                        deleteStmt.executeUpdate();
                    }
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deletePodcastEpisodeFromPodcastGuestsTableQuery)) {
                        deleteStmt.setInt(1, podcastEpisodeAudioId);
                        deleteStmt.executeUpdate();
                    }
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deletePodcastEpisodeFromPodcastAudioTableQuery)) {
                        deleteStmt.setInt(1, podcastEpisodeAudioId);
                        deleteStmt.executeUpdate();
                    }
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deletePodcastEpisodeFromAudioETableQuery)) {
                        deleteStmt.setInt(1, podcastEpisodeAudioId);
                        deleteStmt.executeUpdate();
                    }
                }
            }
        }
    }
}