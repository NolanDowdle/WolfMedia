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
 * Update podcast host
 * 
 * @author KR
 */
public class updatePodcastHost extends OperationBase {
    // Define my static SQL query statements
    private static final String updatedPodcastHostEmailAddrSQL = "UPDATE Users SET email = ? WHERE firstName = ? and lastName = ?";
    private static final String updatePodcastHostFirstNameSQL  = "UPDATE Users SET firstName = ? WHERE firstName = ? and lastName = ?";
    private static final String updatePodcastHostLastNameSQL   = "UPDATE Users SET lastName = ? WHERE firstName = ? and lastName = ?";
    private static final String updatePodcastHostCountrySQL    = "UPDATE Users SET country = ? WHERE firstName = ? and lastName = ?";
    private static final String updatePodcastHostCitySQL       = "UPDATE Users SET city = ? WHERE firstName = ? and lastName = ?";
    private static final String updatePodcastHostBankAccNumSQL = "UPDATE Users SET bankAccountNumber = ? WHERE firstName = ? and lastName = ?";
    private static final String updatePodcastHostPhoneNumSQL   = "UPDATE Users SET phoneNum = ? WHERE firstName = ? and lastName = ?";

    /**
     * Update BASIC Podcast Host attributes
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        String podcastHostToUpdateFirstName = InputUtil.getString("What is the podcast host first name that you want to UPDATE?");
        String postcastHostToUpdateLastName = InputUtil.getString("What is the podcast host last name that you want to UPDATE?");
        String poscastHosttAttributeToUpdate = InputUtil.getString("Which field you want to update:\n"
                + "1] EMail Address\n 2] firstName\n 3] lastName\n 4] Country\n 5] City\n 6] BankAccountNumber\n 7] phoneNumber");
        switch (poscastHosttAttributeToUpdate) {
        // PODCAST HOST EMAIL ADDRESS
        case "1":
            String updatedPodcastHostEMailAddress = InputUtil.getString("What is the UPDATED email address?");
            try (PreparedStatement stmt = conn.prepareStatement(updatedPodcastHostEmailAddrSQL)) {
                stmt.setString(1, updatedPodcastHostEMailAddress);
                stmt.setString(2, podcastHostToUpdateFirstName);
                stmt.setString(3, postcastHostToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // PODCAST HOST FIRST NAME
        case "2":
            String updatedPodcastHostFirstName = InputUtil.getString("What is the UPDATED first name?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastHostFirstNameSQL)) {
                stmt.setString(1, updatedPodcastHostFirstName);
                stmt.setString(2, podcastHostToUpdateFirstName);
                stmt.setString(3, postcastHostToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // PODCAST HOST LAST NAME
        case "3":
            String updatedPodcastHostLastName = InputUtil.getString("What is the UPDATED last name?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastHostLastNameSQL)) {
                stmt.setString(1, updatedPodcastHostLastName);
                stmt.setString(2, podcastHostToUpdateFirstName);
                stmt.setString(3, postcastHostToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // PODCAST HOST COUNTRY
        case "4":
            String updatedPodcastHostCountry = InputUtil.getString("What is the UPDATED country?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastHostCountrySQL)) {
                stmt.setString(1, updatedPodcastHostCountry);
                stmt.setString(2, podcastHostToUpdateFirstName);
                stmt.setString(3, postcastHostToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // PODCAST HOST CITY
        case "5":
            String updatedPodcastHostCity = InputUtil.getString("What is the UPDATED city?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastHostCitySQL)) {
                stmt.setString(1, updatedPodcastHostCity);
                stmt.setString(2, podcastHostToUpdateFirstName);
                stmt.setString(3, postcastHostToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // PODCAST HOST BANK ACC #
        case "6":
            String updatedPodcastHostBankAccountNumber = InputUtil.getString("What is the UPDATED bank account #?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastHostBankAccNumSQL)) {
                stmt.setString(1, updatedPodcastHostBankAccountNumber);
                stmt.setString(2, podcastHostToUpdateFirstName);
                stmt.setString(3, postcastHostToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // PHONE #
        case "7":
            String updatedPodcastHostPhoneNumber = InputUtil.getString("What is the UPDATED phone #?");
            try (PreparedStatement stmt = conn.prepareStatement(updatePodcastHostPhoneNumSQL)) {
                stmt.setString(1, updatedPodcastHostPhoneNumber);
                stmt.setString(2, podcastHostToUpdateFirstName);
                stmt.setString(3, postcastHostToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        }
    }
}
