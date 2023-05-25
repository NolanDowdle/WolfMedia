// All "Processing of Payments" will be merged in the following pkg name
package main.java.wolf_media.operation.payment;

//Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

// Import our custom runtime libraries
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * 
 * Author:      JK
 * Purpose:     Retrieve royalty payments for song in specified data range
 * Composed On: April 9th, 2023
 * Modified On: April 9th, 2023
 * 
 */
public class songRoyalty extends OperationBase{

    private static final String QUERY = "SELECT audioId, COUNT(*) AS playCount, royaltyRate,\n"
            + "(COUNT(audioId) * royaltyRate) AS songRoyalty FROM Listens NATURAL JOIN Songs\n"
            + "WHERE audioId=? AND (? <= playTime AND playTime <= ?)";
	
    /**
     * Payment Info for User
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return          result set of tuple with total royalty amount for song in specified period
     */
    protected void executeImpl(Connection conn) throws SQLException {
        Integer songId    = InputUtil.getInt("Enter the songId");
        String  startDate = InputUtil.getString("What is the starting date in format YYYY-MM-DD?");
        String  endDate   = InputUtil.getString("What is the ending date in format YYYY-MM-DD?");
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
            stmt.setInt(1, songId);
            stmt.setString(2, startDate);
            stmt.setString(3, endDate);
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