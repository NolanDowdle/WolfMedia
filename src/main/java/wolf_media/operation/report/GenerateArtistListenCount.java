package main.java.wolf_media.operation.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * Generate artist listen count from a given time range
 * 
 */
public class GenerateArtistListenCount extends OperationBase {
	
    private static final String QUERY =
            "SELECT PrimaryArtists.artistId, COUNT(*) as playCount\n"
            + "FROM Listens\n"
            + "JOIN Songs ON\n"
            + "    Listens.audioId = Songs.audioId\n"
            + "JOIN PrimaryArtists ON\n"
            + "    Songs.audioId = PrimaryArtists.songId\n"
            + "WHERE\n"
            + "    playTime < ? \n"
            + "    AND playTime >= ? \n"
            + "GROUP BY PrimaryArtists.artistId";

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