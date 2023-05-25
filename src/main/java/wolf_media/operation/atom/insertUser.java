package main.java.wolf_media.operation.atom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;

public class insertUser extends OperationBase {
    // Define my static SQL query statements
    private static final String INSERT_USER_STMT = "INSERT INTO Users(userId, email, firstName, lastName, "
            + "country, city, subfee, bankAccountNumber, acctStatus, regDate, phoneNum) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    
    private static final String[] USER_ACCT_STATUSES = new String[] {
        "free", "pay"
    };

    /**
     * Insert an user into Users TABLE then insert into Users table
     * 
     * @param conn The connection used to interact with MariaDb
     * @return Success if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {

        Integer userId = InputUtil.getIntOrNull("Insert an userId or an empty string to generate one");
        if (userId == null) {
            userId = InputUtil.incrementUserId();
        }

        String userEmailAddr = InputUtil.getString("What is the user e-mail address?");
        String userFirstname = InputUtil.getString("What is the user first name?");
        String userLastName = InputUtil.getString("What is the user last name?");
        String userCountry = InputUtil.getString("What is the user country?");
        String userCity = InputUtil.getString("What is the user city?");
        Double userSubFee = InputUtil.getDouble("What is the user subscription fee?");
        String userBankAccountNumber = InputUtil.getString("What is the user bank account number?");
        String userAcctStatus = InputUtil.getEnumValue("What is the user account status?", USER_ACCT_STATUSES);
        Timestamp userTimestamp = InputUtil.getTimestampOrNull("What is the user registration timestamp (empty string for current time)?");
        String userPhoneNumber = InputUtil.getString("What is the user phone number number?");

        try (PreparedStatement stmt = conn.prepareStatement(INSERT_USER_STMT)) {
            stmt.setInt(1, userId);
            stmt.setString(2, userEmailAddr);
            stmt.setString(3, userFirstname);
            stmt.setString(4, userLastName);
            stmt.setString(5, userCountry);
            stmt.setString(6, userCity);
            // Below is the subscription fee hard coded to zero
            stmt.setDouble(7, userSubFee);
            stmt.setString(8, userBankAccountNumber);
            // Below is the account status hard coded to 'Free'
            stmt.setString(9, userAcctStatus);
            if (userTimestamp == null) {
                stmt.setString(10, InputUtil.getTodayDateAndTime());
            } else {
                stmt.setTimestamp(10, userTimestamp);
            }
            stmt.setString(11, userPhoneNumber);
            stmt.executeUpdate();
        }
    }
}
