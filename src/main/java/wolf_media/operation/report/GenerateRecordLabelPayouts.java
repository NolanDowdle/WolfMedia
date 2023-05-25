package main.java.wolf_media.operation.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.Constants;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/*
 * Generate record label payout report
 */
public class GenerateRecordLabelPayouts extends OperationBase {
	private static final String QUERY =
            "SELECT userId as RecordLabelId, SUM(amount) AS totalPayments\n"
            + "FROM Ledger\n"
            + "WHERE\n"
            + "    transComplete = 1\n"
            + "    AND transType = \"royalty\"\n"
            + "    AND ? <= transDate\n"
            + "    AND ? > transDate\n"
            + "    AND userId IN (SELECT userId FROM RecordLabels)\n"
            + "GROUP BY userId";

    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        String firstMonth  = InputUtil.getString("Input the begining month In YYYY-MM-DD");
        String lastMonth   = InputUtil.getString("Input ending month In YYYY-MM-DD");
        
        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
            stmt.setString(1, firstMonth);
            stmt.setString(2, lastMonth);
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
