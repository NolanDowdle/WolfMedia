/*
 * 
 * Author:      JK
 * Purpose:     Post monthly podcast host payments to the ledger
 * Composed On: April 1st, 2023
 * Modified On: April 1st, 2023
 * 
 */

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

public class moLabelRoyalties extends OperationBase { 

    private static final String TQUERY = "SELECT recordLabelId AS userId, SYSDATE() AS transDate, 0 AS transComplete, SUM(royalty) AS amount, 'royalty' AS transType "
            + "    FROM (SELECT recordLabelId, Songs.audioId, (royaltyRate * lc.listenCount * 0.3) AS royalty "
            + "    FROM Songs JOIN (SELECT audioId, COUNT(*) AS listenCount "
            + "        FROM Listens WHERE ? <= playTime AND playTime <= ? "
            + "        GROUP BY audioId) lc ON lc.audioId = Songs.audioId "
            + "    JOIN (SELECT recordLabelId, songId FROM Contracts) lsp ON lsp.songId = Songs.audioId) asr "
            + "GROUP BY recordLabelId";
    
    private static final String LQUERY = "SELECT * FROM Ledger WHERE transType = 'royalty' AND (userId >= 3000 AND userId < 4000) AND "
            + "(? <= transDate AND transDate <= ?)";
    
    private static final String INSERT = "INSERT INTO Ledger "
            + "SELECT recordLabelId, SYSDATE() AS transDate, 0 AS transComplete, SUM(royalty) AS amount, 'royalty' AS transType "
            + "FROM (SELECT recordLabelId, Songs.audioId, (royaltyRate * lc.listenCount * 0.3) AS royalty "
            + "FROM Songs JOIN (SELECT audioId, COUNT(*) AS listenCount "
            + "FROM Listens WHERE ? <= playTime AND playTime <= ? "
            + "GROUP BY audioId) lc ON lc.audioId = Songs.audioId "
            + "JOIN (SELECT recordLabelId, songId FROM Contracts) lsp ON " + "lsp.songId = Songs.audioId) asr "
            + "WHERE recordLabelId NOT IN ( SELECT userId FROM Ledger WHERE transType = 'royalty' AND "
            + "		(userId >= 3000 AND userId < 4000) AND (? <= transDate AND transDate <= ?))	"
            + "GROUP BY recordLabelId";
    
    private static final String QUERY = "SELECT recordLabelId AS userId, SYSDATE() AS transDate, 0 AS transComplete, SUM(royalty) AS amount, 'royalty' AS transType "
            + "    FROM (SELECT recordLabelId, Songs.audioId, (royaltyRate * lc.listenCount * 0.3) AS royalty "
            + "    FROM Songs JOIN (SELECT audioId, COUNT(*) AS listenCount "
            + "        FROM Listens WHERE ? <= playTime AND playTime <= ? "
            + "        GROUP BY audioId) lc ON lc.audioId = Songs.audioId "
            + "    JOIN (SELECT recordLabelId, songId FROM Contracts) lsp ON lsp.songId = Songs.audioId) asr "
            + "		WHERE recordLabelId NOT IN ( SELECT userId FROM Ledger WHERE transType = 'royalty' AND "
            + "		 (userId >= 3000 AND userId < 4000) AND (? <= transDate AND transDate <= ?))	"
            + "GROUP BY recordLabelId";
    
	/**
     * Monthly Label Royalties
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
            String prompt   = "Royalty payments for " + numTrans + " record labels have been posted to the ledger.";
            System.out.println(prompt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}