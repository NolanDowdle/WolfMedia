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
 * Delete a song
 * 
 * @author KR
 * 
 */
public class deleteSong extends OperationBase {

	private static final String deleteSongFromAudioETableQuery       = "DELETE FROM AudioEntities WHERE audioId = ?";
	private static final String deleteSongFromSongsTableQuery        = "DELETE FROM Songs WHERE audioId = ?";
	private static final String deleteSongFromPrimaryArtistTable     = "DELETE FROM PrimaryArtists WHERE songId = ?";
	private static final String deleteSongFromContractsTable         = "DELETE FROM Contracts WHERE songId = ?";
	private static final String deleteSongFromSongGenreTable         = "DELETE FROM SongGenre WHERE audioId = ?";
	private static final String deleteSongFromAlbumTracksTable       = "DELETE FROM AlbumTracks WHERE audioId = ?";
	
	private static final String retrieveAudioId 			         = "SELECT audioId FROM AudioEntities WHERE title = ?";
	
	private static final String doesSongExistInSongGenreTbl          = "SELECT 1 FROM SongGenre WHERE audioId = ?";
	private static final String doesSongExistInContractsTbl          = "SELECT 1 FROM Contracts WHERE songId = ?";
	private static final String doesSongExistInAlbumTracksTbl        = "SELECT 1 FROM AlbumTracks WHERE audioId = ?";

	/**
	 * Delete a song from the following TABLEs:
	 *   1] SongGenre
	 *   2] Contracts
	 *   3] PrimaryArtist
	 *   4] Songs
	 *   5] AudioEntities
	 * 
	 * @param  conn     The connection used to interact with MariaDb
	 * @return Success  if committed; SQLException in case of err
	 */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        String songToDelete = InputUtil.getString("What is the song title you want to delete?");
        try (PreparedStatement stmt = conn.prepareStatement(retrieveAudioId)) {
            stmt.setString(1, songToDelete);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int songAudioId = rs.getInt("audioId");
                    try (PreparedStatement existsStmt = conn.prepareStatement(doesSongExistInSongGenreTbl)) {
                        existsStmt.setInt(1, songAudioId);
                        boolean doesSongExist = false;
                        try (ResultSet existsRS = existsStmt.executeQuery()) {
                            doesSongExist = existsRS.next();
                        }
                        if (doesSongExist) {
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSongFromSongGenreTable)) {
                                deleteStmt.setInt(1, songAudioId);
                                deleteStmt.executeUpdate();
                            }
                        }
                    }
                    try (PreparedStatement existsStmt = conn.prepareStatement(doesSongExistInAlbumTracksTbl)) {
                        existsStmt.setInt(1, songAudioId);
                        boolean doesSongExist = false;
                        try (ResultSet existsRS = existsStmt.executeQuery()) {
                            doesSongExist = existsRS.next();
                        }
                        if (doesSongExist) {
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSongFromAlbumTracksTable)) {
                                deleteStmt.setInt(1, songAudioId);
                                deleteStmt.executeUpdate();
                            }
                        }
                    }
                    try (PreparedStatement existsStmt = conn.prepareStatement(doesSongExistInContractsTbl)) {
                        existsStmt.setInt(1, songAudioId);
                        boolean doesSongExist = false;
                        try (ResultSet existsRS = existsStmt.executeQuery()) {
                            doesSongExist = existsRS.next();
                        }
                        if (doesSongExist) {
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSongFromContractsTable)) {
                                deleteStmt.setInt(1, songAudioId);
                                deleteStmt.executeUpdate();
                            }
                        }
                    }
                    // The below tables should be filed based on narrative
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSongFromPrimaryArtistTable)) {
                        deleteStmt.setInt(1, songAudioId);
                        deleteStmt.executeUpdate();
                    }
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSongFromSongsTableQuery)) {
                        deleteStmt.setInt(1, songAudioId);
                        deleteStmt.executeUpdate();
                    }
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSongFromAudioETableQuery)) {
                        deleteStmt.setInt(1, songAudioId);
                        deleteStmt.executeUpdate();
                    }
                }
            }
        }
    }
}
