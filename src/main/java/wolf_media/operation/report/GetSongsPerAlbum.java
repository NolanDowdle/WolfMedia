package main.java.wolf_media.operation.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * Get songs per album report
 */
public class GetSongsPerAlbum extends OperationBase {
	private static final String QUERY =
            "SELECT AlbumTracks.trackNumber, Songs.royaltyRate, AudioEntities.*\n"
            + "FROM AlbumTracks\n"
            + "JOIN Songs ON\n"
            + "AlbumTracks.audioId = Songs.audioId\n"
            + "JOIN AudioEntities ON\n"
            + "Songs.audioId = AudioEntities.audioId\n"
            + "WHERE\n"
            + "AlbumTracks.albumId = ?";
    
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        int albumId = InputUtil.getInt("Input album ID");
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
            stmt.setInt(1, albumId);
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