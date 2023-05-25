package main.java.wolf_media.operation.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * Get song per artist report
 */
public class GetSongsPerArtist extends OperationBase {
	private static final String QUERY =
            "SELECT AudioEntities.*, Songs.royaltyRate\n"
            + "FROM PrimaryArtists\n"
            + "JOIN Songs ON\n"
            + "PrimaryArtists.songId = Songs.audioId\n"
            + "JOIN AudioEntities ON\n"
            + "Songs.audioId = AudioEntities.audioId\n"
            + "WHERE PrimaryArtists.artistId = ?";
    
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        int artistId = InputUtil.getInt("Input artist ID");
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
            stmt.setInt(1, artistId);
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