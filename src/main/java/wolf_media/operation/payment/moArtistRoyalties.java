// All "Processing of Payments" will be merged in the following pkg name
package main.java.wolf_media.operation.payment;

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
 * Purpose:     Post monthly artist royalty payments to the ledger
 * Composed On: April 1st, 2023
 * Modified On: April 1st, 2023
 * 
 */
public class moArtistRoyalties extends OperationBase {

    private static final String TQUERY = "SELECT artistId as userId, SYSDATE() as transDate, 0 AS transComplete, SUM(royalty) AS amount, 'royalty' AS transType\n"
            + "	FROM (SELECT asp.artistId, Songs.audioId, \n"
            + "			(royaltyRate * lc.listenCount * 0.7) / ac.artistCount AS royalty \n"
            + "			FROM Songs JOIN (SELECT audioId, COUNT(*) AS listenCount FROM Listens\n"
            + "			WHERE ? <= playTime AND playTime <= ? \n" + "			GROUP BY audioId) lc \n"
            + "		ON lc.audioId = Songs.audioId \n"
            + "		JOIN (SELECT songId, COUNT(*) AS artistCount FROM Contracts GROUP BY songId) ac \n"
            + "			ON ac.songId = Songs.audioId \n"
            + "		JOIN (SELECT artistId, songId FROM Contracts) asp ON asp.songId = Songs.audioId) asr \n"
            + "		GROUP BY artistId";
	
    private static final String LQUERY = "SELECT * FROM Ledger WHERE transType = 'royalty' AND (userId >= 2000 AND userId < 3000) AND "
            + "(? <= transDate AND transDate <= ?)";
	
    private static final String INSERT = "INSERT INTO Ledger "
            + "SELECT artistId, SYSDATE(), 0, SUM(royalty) AS amount, 'royalty' "
            + "FROM (SELECT asp.artistId, Songs.audioId, "
            + "      (royaltyRate * lc.listenCount * 0.7) / ac.artistCount AS royalty "
            + "    FROM Songs JOIN (SELECT audioId, COUNT(*) AS listenCount FROM Listens "
            + "        WHERE ? <= playTime AND playTime <= ? "
            + "        GROUP BY audioId) lc ON lc.audioId = Songs.audioId "
            + "    JOIN (SELECT songId, COUNT(*) AS artistCount FROM Contracts GROUP BY songId) ac ON "
            + "        ac.songId = Songs.audioId " + "    JOIN (SELECT artistId, songId FROM Contracts) asp ON "
            + "        asp.songId = Songs.audioId) asr "
            + "    WHERE artistId NOT IN ( SELECT userId FROM Ledger WHERE transType = 'royalty' AND "
            + "		(userId >= 2000 AND userId < 3000) AND (? <= transDate AND transDate <= ?))	" + "GROUP BY artistId";

    private static final String QUERY = "SELECT artistId as userId, SYSDATE() as transDate, 0 AS transComplete, SUM(royalty) AS amount, 'royalty' AS transType "
            + "FROM (SELECT asp.artistId, Songs.audioId, "
            + "      (royaltyRate * lc.listenCount * 0.7) / ac.artistCount AS royalty "
            + "    FROM Songs JOIN (SELECT audioId, COUNT(*) AS listenCount FROM Listens "
            + "        WHERE ? <= playTime AND playTime <= ? "
            + "        GROUP BY audioId) lc ON lc.audioId = Songs.audioId "
            + "    JOIN (SELECT songId, COUNT(*) AS artistCount FROM Contracts GROUP BY songId) ac ON "
            + "        ac.songId = Songs.audioId " + "    JOIN (SELECT artistId, songId FROM Contracts) asp ON "
            + "        asp.songId = Songs.audioId) asr "
            + "    WHERE artistId NOT IN ( SELECT userId FROM Ledger WHERE transType = 'royalty' AND "
            + "		(userId >= 2000 AND userId < 3000) AND (? <= transDate AND transDate <= ?)) " + "GROUP BY artistId";

	/**
     * Monthly Artist Royalties
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return          Success  if committed; SQLException in case of err
     */
    protected void executeImpl(Connection conn) throws SQLException {
        String startDate = InputUtil.getString("What is the starting date in format YYYY-MM-DD?");
        String endDate   = InputUtil.getString("What is the ending date in format YYYY-MM-DD?");
        
        try (PreparedStatement tCalc = conn.prepareStatement(TQUERY)) {
            tCalc.setString(1, startDate);
            tCalc.setString(2, endDate);
            String prompt = "Here are the calculated royalty payments for the period.";
            System.out.println(prompt);
            try (ResultSet rs = tCalc.executeQuery()) {
                
                OutputUtil.printResultSet(rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try (PreparedStatement transLedger = conn.prepareStatement(LQUERY)) {
            transLedger.setString(1, startDate);
            transLedger.setString(2, endDate);
            try (ResultSet ledgerRS = transLedger.executeQuery()) {
                try {
                    boolean tinLedger = ledgerRS.next();
                    ledgerRS.beforeFirst();
                    if (tinLedger) {
                        String prompt = "These transactions were found in the Ledger, and were not re-posted.";
                        System.out.println(prompt);
                        
                        OutputUtil.printResultSet(ledgerRS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try (PreparedStatement qstmt = conn.prepareStatement(QUERY)) {
            qstmt.setString(1, startDate);
            qstmt.setString(2, endDate);
            qstmt.setString(3, startDate);
            qstmt.setString(4, endDate);
            try (ResultSet rs = qstmt.executeQuery()) {
                
                OutputUtil.printResultSet(rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try (PreparedStatement istmt = conn.prepareStatement(INSERT)) {
            istmt.setString(1, startDate);
            istmt.setString(2, endDate);
            istmt.setString(3, startDate);
            istmt.setString(4, endDate);
            int    numTrans = istmt.executeUpdate();
            String prompt   = "Royalty payments for " + numTrans + " recording artists have been posted to the ledger.";
            System.out.println(prompt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}