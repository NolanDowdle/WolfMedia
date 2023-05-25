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
 * Update Podcast Episode Attributes
 * 
 * @author KR
 * 
 */
public class updatePodcastEpisode extends OperationBase {

    // Define my static SQL query statements
    private static final String updatePodcastEpisodeNameSQL             = "UPDATE AudioEntities SET title = ? WHERE title = ?";
    private static final String updatePodcastEpsiodeDurationSQL         = "UPDATE AudioEntities SET duration = ? WHERE title = ?";
    private static final String updatePodcastEpisodeReleaseDateSQL      = "UPDATE AudioEntities SET releaseDate = ? WHERE title = ?";
    private static final String updatePodcastEpisodeReleaseCountrySQL   = "UPDATE AudioEntities SET releaseCountry = ? WHERE title = ?";
    private static final String updatePodcastEpisodeReleaseLanguageSQL  = "UPDATE AudioEntities SET releaseLanguage = ? WHERE title = ?";
    private static final String updatePodcastEpisodeAdCountSQL          = "UPDATE PodcastAudios SET adCount = ? WHERE audioId = ?";

    private static final String retrieveAudioId                         = "SELECT audioId FROM AudioEntities WHERE title = ?";
    

    /**
     * Update Podcast Episode Attributes
     * 
     * @param  conn     The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        String podcastEpisodeToUpdate          = InputUtil.getString("What is the podcast episode title you want to update?");
        String podcastEpisodeAttributeToUpdate = InputUtil.getString("Which field you want to update:\n"
                + " 1] title\n 2] duration\n 3] releaseDate\n 4] releaseCountry\n 5] releaseLanguage\n 6] advertisement count");
        switch (podcastEpisodeAttributeToUpdate) {
        // PODCAST EPISODE NAME
        case "1":
            String updatedPodcastEpisodeName = InputUtil.getString("What is the UPDATED name?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastEpisodeNameSQL)) {
                stmt.setString(1, updatedPodcastEpisodeName);
                stmt.setString(2, podcastEpisodeToUpdate);
                stmt.executeUpdate();
            }
            break;
        // PODCAST EPISODE DURATION
        case "2":
            String updatedPodcastEpisodeDuration = InputUtil.getString("What is the UPDATED duration HH:MM:SS format?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastEpsiodeDurationSQL)) {
                stmt.setString(1, updatedPodcastEpisodeDuration);
                stmt.setString(2, podcastEpisodeToUpdate);
                stmt.executeUpdate();
            }
            break;
         // PODCAST EPISODE RELEASE DATE 
        case "3":
            String updatedPodcastEpisodeReleaseDate = InputUtil.getString("What is the UPDATED releaseDate in YYYY-MM-DD format?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastEpisodeReleaseDateSQL)) {
                stmt.setString(1, updatedPodcastEpisodeReleaseDate);
                stmt.setString(2, podcastEpisodeToUpdate);
                stmt.executeUpdate();
            }
            break;
        // PODCAST EPISODE RELEASE COUNTRY
        case "4":
            String updatedPodcastEpisodeReleaseCountry = InputUtil.getString("What is the UPDATED release country?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastEpisodeReleaseCountrySQL)) {
                stmt.setString(1, updatedPodcastEpisodeReleaseCountry);
                stmt.setString(2, podcastEpisodeToUpdate);
                stmt.executeUpdate();
            }
            break;
        // PODCAST EPISODE RELEASE LANGUAGE
        case "5":
            String updatedPodcastEpisodeReleaseLanguage = InputUtil.getString("What is the UPDATED release language?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastEpisodeReleaseLanguageSQL)) {
                stmt.setString(1, updatedPodcastEpisodeReleaseLanguage);
                stmt.setString(2, podcastEpisodeToUpdate);
                stmt.executeUpdate();
            }
            break;
        // PODCAST EPISODE AD COUNT
        case "6":
            String updatedPodcastEpisodeAdCount = InputUtil.getString("What is the UPDATED advertisement count?");
            try (PreparedStatement stmt = conn.prepareStatement(retrieveAudioId)) {
                stmt.setString(1, podcastEpisodeToUpdate);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int podcastEpisodeAudioId = rs.getInt("audioId");
                        try (PreparedStatement updateStmt = conn.prepareStatement(updatePodcastEpisodeAdCountSQL)) {
                            updateStmt.setString(1, updatedPodcastEpisodeAdCount);
                            updateStmt.setInt(2, podcastEpisodeAudioId);
                            updateStmt.executeUpdate(); 
                        }
                    }
                }
            }
            break;
        }
    }
}