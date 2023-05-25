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
 * Update BASIC SONG attributes
 * 
 * The basic information is defined as:
 *   1] title
 *   2] duration
 *   3] release date
 *   4] release country 
 *   5] release language
 *   6] royalty rate
 * @author KR
 * 
 */
public class updateSong extends OperationBase {
	// Define my static SQL query statements
	private static final String updateSongNameSQL            = "UPDATE AudioEntities SET title = ? WHERE title = ?";
	private static final String updateSongDurationSQL        = "UPDATE AudioEntities SET duration = ? WHERE title = ?";
	private static final String updateSongReleaseDateSQL     = "UPDATE AudioEntities SET releaseDate = ? WHERE title = ?";
	private static final String updateSongReleaseCountrySQL  = "UPDATE AudioEntities SET releaseCountry = ? WHERE title = ?";
	private static final String updateSongReleaseLanguageSQL = "UPDATE AudioEntities SET releaseLanguage = ? WHERE title = ?";
	private static final String updateRoyaltyRateSql         = "UPDATE Songs SET royaltyRate = ? WHERE audioId = ?";
	
	private static final String retrieveAudioId              = "SELECT audioId FROM AudioEntities WHERE title = ?";

	/**
	 * Update BASIC song attributes
	 * 
	 * @param conn     The connection used to interact with MariaDb
	 * @return Success if committed; SQLException in case of err
	 */
	@Override
	protected void executeImpl(Connection conn) throws SQLException {
		String songToUpdate          = InputUtil.getString("What is the song title you want to update?");
		String songAttributeToUpdate = InputUtil.getString("Which field you want to update:\n"
				+ "1] songName\n 2] duration\n 3] releaseDate\n 4] releaseCountry\n 5] releaseLanguage\n 6] royaltyRate?");
		switch (songAttributeToUpdate) {
		// SONG NAME
		case "1":
			String updatedSongName      = InputUtil.getString("What is the UPDATED title?");
			try (PreparedStatement stmt = conn.prepareStatement(updateSongNameSQL)) {
				stmt.setString(1, updatedSongName);
				stmt.setString(2, songToUpdate);
				stmt.executeUpdate();
			}
			break;
		// SONG DURATION
		case "2":
			String updatedSongDuration  = InputUtil.getString("What is the UPDATED duration in HH:MM:SS format?");
			try (PreparedStatement stmt = conn.prepareStatement(updateSongDurationSQL)) {
				stmt.setString(1, updatedSongDuration);
				stmt.setString(2, songToUpdate);
				stmt.executeUpdate();
			}
			break;
		// SONG RELEASE DATE
		case "3":
			String updatedSongReleaseDate = InputUtil.getString("What is the UPDATED releaseDate in YYYY-MM-DD format?");
			try (PreparedStatement stmt   = conn.prepareStatement(updateSongReleaseDateSQL)) {
				stmt.setString(1, updatedSongReleaseDate);
				stmt.setString(2, songToUpdate);
				stmt.executeUpdate();
			}
			break;
		// SONG RELEASE COUNTRY
		case "4":
			String updatedSongReleaseCountry = InputUtil.getString("What is the UPDATED release country?");
			try (PreparedStatement stmt      = conn.prepareStatement(updateSongReleaseCountrySQL)) {
				stmt.setString(1, updatedSongReleaseCountry);
				stmt.setString(2, songToUpdate);
				stmt.executeUpdate();
			}
			break;
		// SONG RELEASE LANGUAGE
		case "5":
			String updatedSongReleaseLanguage = InputUtil.getString("What is the UPDATED release language?");
			try (PreparedStatement stmt       = conn.prepareStatement(updateSongReleaseLanguageSQL)) {
				stmt.setString(1, updatedSongReleaseLanguage);
				stmt.setString(2, songToUpdate);
				stmt.executeUpdate();
			}
			break;
		// ROYALTY RATE
		case "6":
			String updatedRoyaltyRate   = InputUtil.getString("What is the UPDATED royalty rate?");
			try (PreparedStatement stmt = conn.prepareStatement(retrieveAudioId)) {
				stmt.setString(1, songToUpdate);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						int songAudioId = rs.getInt("audioId");
						try (PreparedStatement updateStmt = conn.prepareStatement(updateRoyaltyRateSql)) {
							updateStmt.setString(1, updatedRoyaltyRate);
							updateStmt.setInt(2, songAudioId);
							updateStmt.executeUpdate(); 
						}
					}
				}
			}
			break;
		}
	}
}
