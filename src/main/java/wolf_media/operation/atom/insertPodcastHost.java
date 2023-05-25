// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.atom;

//Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//Import our custom runtime libraries
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;

/**
 * Insert podcast host
 * 
 * @author KR
 */
public class insertPodcastHost extends OperationBase {
    
    // Define my static SQL query statements
    
    private static final String insertUserQuery        = "INSERT INTO Users(userId, email, firstName, lastName, "
            + "country, city, subfee, bankAccountNumber, acctStatus, regDate, phoneNum) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String insertPodcastHostQuery = "INSERT INTO PodcastHosts(userId) VALUES (?)";
    /**
     * Insert a Podcast Host into Users TABLE then insert into the PodcastHost TABLE
     * 
     * @param conn       The connection used to interact with MariaDb
     * @return Success   if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        Integer userId = InputUtil.getIntOrNull("Insert an userId or an empty string to generate one");
        if (userId == null) {
            userId = InputUtil.incrementUserId();
        }

        String podcastHostEmailAddr             =  InputUtil.getString("What is the podcast host e-mail address?");
        String podcastHostFirstname             =  InputUtil.getString("What is the podcast host first name?");
        String podcastHostLastName              =  InputUtil.getString("What is the podcast host last name?");
        String podcastHostCountry               =  InputUtil.getString("What is the podcast host country?");
        String podcastHostCity                  =  InputUtil.getString("What is the podcast host city?");
        String podcastHostBankAccountNumber     =  InputUtil.getString("What is the podcast host bank account number?");
        String podcastHostPhoneNumber           =  InputUtil.getString("What is the podcast host phone number number?");
        
        try (PreparedStatement stmt = conn.prepareStatement(insertUserQuery)) {
            stmt.setInt(1, userId);
            stmt.setString(2, podcastHostEmailAddr);
            stmt.setString(3, podcastHostFirstname);
            stmt.setString(4, podcastHostLastName);
            stmt.setString(5, podcastHostCountry);
            stmt.setString(6, podcastHostCity);
            // Below is the subscription fee hard coded to zero 
            stmt.setString(7, "0");
            stmt.setString(8, podcastHostBankAccountNumber);
            // Below is the account status hard coded to 'Free'
            stmt.setString(9, "Free");
            stmt.setString(10, InputUtil.getTodayDateAndTime());
            stmt.setString(11, podcastHostPhoneNumber);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(insertPodcastHostQuery)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
}
