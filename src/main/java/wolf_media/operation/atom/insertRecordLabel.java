// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.atom;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;

/**
 * Insert a record label
 * 
 * @author KR
 * 
 */
public class insertRecordLabel extends OperationBase {

    // Define my static SQL query statements
    private static final String insertUserQuery        = "INSERT INTO Users(userId, email, firstName, lastName, "
            + "country, city, subfee, bankAccountNumber, acctStatus, regDate, phoneNum) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String insertRecordLabelQuery = "INSERT INTO RecordLabels(userId) VALUES (?)";

    /**
     * Insert a record label
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {

        Integer userId = InputUtil.getIntOrNull("Insert an userId or an empty string to generate one");
        if (userId == null) {
            userId = InputUtil.incrementUserId();
        }

        String recordLabelEmailAddr         =  InputUtil.getString("What is the record label e-mail address?");
        String recordLabelFirstname         =  InputUtil.getString("What is the record label first name?");
        String recordLabelLastName          =  InputUtil.getString("What is the record label last name?");
        String recordLabelCountry           =  InputUtil.getString("What is the record label country?");
        String recordLabelCity              =  InputUtil.getString("What is the record label city?");
        String recordLabelBankAccountNumber =  InputUtil.getString("What is the record label bank account number?");
        String recordLabelPhoneNumber       =  InputUtil.getString("What is the record label phone number number?");

        try (PreparedStatement stmt = conn.prepareStatement(insertUserQuery)) {
            stmt.setInt(1, userId);
            stmt.setString(2, recordLabelEmailAddr);
            stmt.setString(3, recordLabelFirstname);
            stmt.setString(4, recordLabelLastName);
            stmt.setString(5, recordLabelCountry);
            stmt.setString(6, recordLabelCity);
            // Below is the subscription fee hard coded to zero
            stmt.setString(7, "0");
            stmt.setString(8, recordLabelBankAccountNumber);
            // Below is the account status hard coded to 'Free'
            stmt.setString(9, "Free");
            stmt.setString(10, InputUtil.getTodayDateAndTime());
            stmt.setString(11, recordLabelPhoneNumber);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(insertRecordLabelQuery)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}
