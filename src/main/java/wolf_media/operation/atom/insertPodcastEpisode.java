// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.atom;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.ExitException;
import main.java.wolf_media.util.InputUtil;

/**
 * Insert a podcast episode
 * 
 * @author: KR
 */
public class insertPodcastEpisode extends OperationBase {
    private static final OperationBase insertPodcast                     = new insertPodcast();
    
    private static final String insertPodcastEpisodeAudioEntitiesQuery   = "INSERT INTO AudioEntities(audioId, title, duration, releaseDate, "
            + "releaseCountry, releaseLanguage) " + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String insertPodcastAudioQuery                  = "INSERT INTO PodcastAudios(audioId, adCount) VALUES (?, ?)";
    private static final String insertPodcastGuestQuery                  = "INSERT INTO PodcastGuests(audioId, guestName) VALUES (?, ?)";
    private static final String insertPodcastEpisodesTable               = "INSERT INTO PodcastEpisodes(podcastId, episodeId, episodeNumber) VALUES (?,?,?)";

    private static final String checkIfPodcastExist                      = "SELECT 1 FROM Podcasts WHERE podcastName = ?";

    private static final String retrievePodcastId                        = "SELECT podcastId FROM Podcasts WHERE podcastName = ?";

    /**
     * Insert a Podcast Episode
     * 
     * @param conn     The connection used to interact with MariaDb
     * @return Success if committed; SQLException in case of err
     * @throws         ExitException
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {

        Integer audioId = InputUtil.getIntOrNull("Insert an audioId or an empty string to generate one");
        if (audioId == null) {
            audioId = InputUtil.incrementAudioId();
        }

        String podcastEpisodeTitle           = InputUtil.getString("What is the title of the podcast episode?");
        String podcastEpisodeDuration        = InputUtil.getString("What is the duration of the podcast episode in format HH:MM:SS?");
        String podcastEpisodeReleaseDate     = InputUtil.getString("What is the podcast episode release date in format YYYY-MM-DD?");
        String podcastEpisodeReleaseCountry  = InputUtil.getString("What is the podcast epsiode release country?");
        String podcastEpisodeReleaseLanguage = InputUtil.getString("What is the podcast episode release language?");
        String podcastEpisodeAdCount         = InputUtil.getString("What is the podcast episode advertisement count?");
        
        try (PreparedStatement stmt = conn.prepareStatement(insertPodcastEpisodeAudioEntitiesQuery)) {
            stmt.setInt(1, audioId);
            stmt.setString(2, podcastEpisodeTitle);
            stmt.setString(3, podcastEpisodeDuration);
            stmt.setString(4, podcastEpisodeReleaseDate);
            stmt.setString(5, podcastEpisodeReleaseCountry);
            stmt.setString(6, podcastEpisodeReleaseLanguage);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(insertPodcastAudioQuery)) {
            stmt.setInt(1, audioId);
            stmt.setString(2, podcastEpisodeAdCount);
            stmt.executeUpdate();
        }

        ///////////////////////////////////
        // SPECIAL GUEST PARSING LOGIC   //
        //////////////////////////////////
        int numberOfGuests = InputUtil.getInt("How many special guest on this podcast episode?");
        for (int i = 0; i < numberOfGuests; i++) {
            String guestName = InputUtil.getString("What is the " + (i + 1) + " guest name?");
            try (PreparedStatement stmt = conn.prepareStatement(insertPodcastGuestQuery)) {
                stmt.setInt(1, audioId);
                stmt.setString(2, guestName);
                stmt.executeUpdate();
            }
        }

        String podcastName            = InputUtil.getString("What is the podcast name that is associated with this podcast episode?");
        String podcastEpisodeEpNumber = InputUtil.getString("What is the podcast episode's episode number?");
        try (PreparedStatement existsStmt = conn.prepareStatement(checkIfPodcastExist)) {
            existsStmt.setString(1, podcastName);
            boolean podcastExists = false;
            try (ResultSet existsRS = existsStmt.executeQuery()) {
                // True if a row is returned from the query, false otherwise
                podcastExists = existsRS.next();
            }
            if (podcastExists) {
                System.out.println("Podcast name already exist in the database");
            // IF NO
            } else {
                System.out.println("Podcast name not found in database hence we need to insertPodcast");
                insertPodcast.executePartial(conn);
                System.out.println("Podcast name inserted in the Db");
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(retrievePodcastId)) {
            stmt.setString(1, podcastName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int podcastId = rs.getInt("podcastId");
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertPodcastEpisodesTable)) {
                        insertStmt.setInt(1, podcastId);
                        insertStmt.setInt(2, audioId);
                        insertStmt.setString(3, podcastEpisodeEpNumber);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }
}