// All "Processing of Payments" will be merged in the following pkg name
package main.java.wolf_media.operation.payment;

//Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * 
 * Author:      JK
 * Purpose:     Retrieve payment info from the ledger
 * Composed On: April 1st, 2023
 * Modified On: April 1st, 2023
 * 
 */
public class paymentInfo extends OperationBase {

    private static final String QUERY = "SELECT u.userId, lastName, firstName, regDate AS registrationDate, subFee AS Fee, \n"
            + "bankAccountNumber, acctStatus AS AccountType, transDate AS transactionDate, \n"
            + "transType AS transactionType, amount FROM Users u JOIN Ledger l ON u.userId = l.userId\n"
            + "WHERE u.userId = ? AND (? <= transDate AND transDate <= ?)";
    /**
     * Payment Info for User
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return          result set of payment history for user
     */
    protected void executeImpl(Connection conn) throws SQLException {
        Integer userId   = InputUtil.getInt("Enter the userId");
        String startDate = InputUtil.getString("What is the starting date in format YYYY-MM-DD?");
        String endDate   = InputUtil.getString("What is the ending date in format YYYY-MM-DD?");
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
            stmt.setInt(1, userId);
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