package main.java.wolf_media.operation.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * Generate podcast episode per podcast report
 *
 */
public class GetPodcastEpisodesPerPodcast extends OperationBase {
	private static final String QUERY =
            "SELECT episodeId, title, episodeNumber, duration, adCount, releaseDate, releaseCountry, releaseLanguage\n"
            + "FROM PodcastEpisodes\n"
            + "JOIN PodcastAudios\n"
            + "ON PodcastEpisodes.episodeId = PodcastAudios.audioId\n"
            + "JOIN AudioEntities\n"
            + "ON PodcastAudios.audioId = AudioEntities.audioId\n"
            + "WHERE PodcastEpisodes.podcastId = ?";
    
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        int podcastId = InputUtil.getInt("Input podcast ID");
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
            stmt.setInt(1, podcastId);
            try (ResultSet rs = stmt.executeQuery()) {
                try {
                    OutputUtil.printResultSet(rs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}