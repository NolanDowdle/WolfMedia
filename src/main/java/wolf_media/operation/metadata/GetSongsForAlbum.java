package main.java.wolf_media.operation.metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * Gets songs for a given album
 * 
 * @author John Fagan
 */
public class GetSongsForAlbum extends OperationBase {

    /**
     * SQL statement to retrieve songs for a given album
     */
    private static final String QUERY =
            "SELECT AudioEntities.audioId, royaltyRate, title, duration, releaseDate, releaseCountry, releaseLanguage "
            + "FROM Albums "
            + "JOIN AlbumTracks "
            + "ON Albums.albumId = AlbumTracks.albumId "
            + "JOIN Songs "
            + "ON AlbumTracks.audioId = Songs.audioId "
            + "JOIN AudioEntities "
            + "ON Songs.audioId = AudioEntities.audioId "
            + "WHERE Albums.albumId = ?;";
    
    /**
     * Prompts user for an album ID and returns all songs associated with the album
     * Transaction commits/rollbacks are handled by the OperationBase base class.
     * 
     * @param conn Database connection
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        // Prompt for input
        int albumId = InputUtil.getInt("Input album ID");
        // Execute SQL statement
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
            stmt.setInt(1, albumId);
            try (ResultSet rs = stmt.executeQuery()) {
                try {
                    OutputUtil.printResultSet(rs);
                } catch (Exception e) {
                    // should not happen unless DB connection closes
                    e.printStackTrace();
                }
            }
        }
    }
}
