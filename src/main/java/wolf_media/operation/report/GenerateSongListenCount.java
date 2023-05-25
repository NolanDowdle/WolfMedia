package main.java.wolf_media.operation.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * Generate song listen count report
 *
 */
public class GenerateSongListenCount extends OperationBase {
	private static final String QUERY =
            "SELECT audioId, COUNT(*) AS playCount "
            + "FROM Listens "
            + "WHERE "
            + "    playTime < ? "
            + "    AND playTime >= ? "
            + "    AND audioId IN (SELECT audioId FROM Songs) "
            + "GROUP BY audioId";

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