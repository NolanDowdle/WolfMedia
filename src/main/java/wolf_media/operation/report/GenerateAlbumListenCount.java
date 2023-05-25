package main.java.wolf_media.operation.report;

// Import necessary packages
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.Constants;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * Generate album listen counter from a given time range
 */
public class GenerateAlbumListenCount extends OperationBase {
    
	private static final String QUERY =
            "SELECT Albums.albumId, COUNT(*) as playCount\n"
            + "FROM AlbumListens\n"
            + "JOIN Albums ON\n"
            + "    AlbumListens.albumId = Albums.albumId\n"
            + "WHERE\n"
            + "    playTime < ? \n"
            + "    AND playTime >= ? \n"
            + "GROUP BY Albums.albumId";

    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        String beginingTimeStamp = InputUtil.getString("Enter the begining playtime in YYYY-MM-DD HH:MM:SS");
        String endingTimeStamp   = InputUtil.getString("Enter the ending playtime in YYYY-MM-DD HH:MM:SS");
        
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {            
            stmt.setString(1, endingTimeStamp);
            stmt.setString(2, beginingTimeStamp);
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
