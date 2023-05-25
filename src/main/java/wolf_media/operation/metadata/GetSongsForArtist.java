package main.java.wolf_media.operation.metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * Operation to get all songs for a given artist
 * 
 * @author John Fagan
 */
public class GetSongsForArtist extends OperationBase {

    /**
     * SQL statement to get all songs for a given artist
     */
    private static final String QUERY =
            "SELECT artistId, AudioEntities.audioId, royaltyRate, title, duration, releaseDate, releaseCountry, releaseLanguage "
            + "FROM PrimaryArtists "
            + "JOIN Songs ON "
            + "PrimaryArtists.songId = Songs.audioId "
            + "JOIN AudioEntities ON "
            + "Songs.audioId = AudioEntities.audioId "
            + "WHERE PrimaryArtists.artistId = ?;";
    
    /**
     * Prompts user for an artist ID and outputs all songs by that artist
     * Transaction commits/rollbacks are handled by the OperationBase base class.
     * 
     * @param conn Database connection
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        // Prompt for input
        int artistId = InputUtil.getInt("Input artist ID");
        // Execute SQL statement
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
            stmt.setInt(1, artistId);
            try (ResultSet rs = stmt.executeQuery()) {
                try {
                    OutputUtil.printResultSet(rs);
                } catch (Exception e) {
                    // Should not happen unless DB connection closes
                    e.printStackTrace();
                }
            }
        }
    }
}
