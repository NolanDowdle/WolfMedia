// All "Processing of Payments" will be merged in the following pkg name
package main.java.wolf_media.operation.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

// Import our custom runtime libraries
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.OutputUtil;
import main.java.wolf_media.util.InputUtil;

/**
 * Author: JK
 * Purpose: Post monthly subscription fee billing to the ledger
 * Composed On: April 12th, 2023
 * Modified On: April 12th, 2023
 */
public class moSubBilling extends OperationBase {
    
    private static final String TQUERY = "SELECT userId, SYSDATE() AS transDate, 0 AS transComplete, "
            + " CAST(subFee AS DECIMAL(19,2)) AS amount, 'subscription' AS transType FROM Users "
            + " WHERE acctStatus = 'pay'";
    
    private static final String LQUERY = "SELECT * FROM Ledger WHERE transType = 'subscription' AND "
            + "(? <= transDate AND transDate <= ?)";
    
    private static final String INSERT = "INSERT INTO Ledger SELECT userId, SYSDATE() AS transDate, 0 AS transComplete, "
            + " CAST(subFee AS DECIMAL(19,2)) AS amount, 'subscription' AS transType FROM Users "
            + " WHERE acctStatus = 'pay' AND userId NOT IN (SELECT userId FROM Ledger "
            + " WHERE transType = 'subscription' AND (? <= transDate AND transDate <= ?))";
    
    private static final String QUERY = "SELECT userId, SYSDATE() AS transDate, 0 AS transComplete, "
            + " CAST(subFee AS DECIMAL(19,2)) AS amount, 'subscription' AS transType FROM Users "
            + " WHERE acctStatus = 'pay' AND userId NOT IN (SELECT userId FROM Ledger "
            + " WHERE transType = 'subscription' AND (? <= transDate AND transDate <= ?))";
    
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
            try (ResultSet rs = qstmt.executeQuery()) {
                try {
                    OutputUtil.printResultSet(rs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try (PreparedStatement istmt = conn.prepareStatement(INSERT)) {
            istmt.setString(1, startDate);
            istmt.setString(2, endDate);
            int    numTrans = istmt.executeUpdate();
            String prompt   = "Monthly subscription fees for " + numTrans + " users have been posted to the ledger.";
            System.out.println(prompt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
