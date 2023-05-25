// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.atom;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;

/**
 * Update BAISC Artist attributes
 * 
 * @author KR
 * 
 */
public class updateArtist extends OperationBase {
    // Define my static SQL query statements
    private static final String updatedArtistEmailAddrSQL = "UPDATE Users SET email = ? WHERE firstName = ? and lastName = ?";
    private static final String updateArtistFirstNameSQL  = "UPDATE Users SET firstName = ? WHERE firstName = ? and lastName = ?";
    private static final String updateArtistLastNameSQL   = "UPDATE Users SET lastName = ? WHERE firstName = ? and lastName = ?";
    private static final String updateArtistCountrySQL    = "UPDATE Users SET country = ? WHERE firstName = ? and lastName = ?";
    private static final String updateArtistCitySQL       = "UPDATE Users SET city = ? WHERE firstName = ? and lastName = ?";
    private static final String updateArtistBankAccNumSQL = "UPDATE Users SET bankAccountNumber = ? WHERE firstName = ? and lastName = ?";
    private static final String updateArtistPhoneNumSQL   = "UPDATE Users SET phoneNum = ? WHERE firstName = ? and lastName = ?";
    private static final String updateArtistStatusSQL     = "UPDATE Artists SET artistStatus = ? WHERE userId = ?";
    private static final String updateArtistTypeSQL       = "UPDATE Artists SET type = ? WHERE userId = ?";
    private static final String retrieveuserId            = "SELECT userId FROM Users WHERE firstName = ? and lastName = ?";

    /**
     * Update Artist attributes
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        String artistToUpdateFirstName = InputUtil.getString("What is the artist first name that you want to UPDATE?");
        String artistToUpdateLastName  = InputUtil.getString("What is the artist last name that you want to UPDATE?");
        String artistAttributeToUpdate = InputUtil.getString("Which field you want to update:\n"
                + "1] EMail Address\n 2] firstName\n 3] lastName\n 4] Country\n 5] City\n 6] BankAccountNumber\n 7] phoneNumber\n"
                + " 8] Artist Status\n 9] Artist Type");
        switch (artistAttributeToUpdate) {
        // ARTIST EMAIL ADDRESS
        case "1":
            String updatedArtistEMailAddress = InputUtil.getString("What is the UPDATED email address?");
            try (PreparedStatement stmt = conn.prepareStatement(updatedArtistEmailAddrSQL)) {
                stmt.setString(1, updatedArtistEMailAddress);
                stmt.setString(2, artistToUpdateFirstName);
                stmt.setString(3, artistToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // ARTIST FIRST NAME
        case "2":
            String updatedAristFirstName = InputUtil.getString("What is the UPDATED first name?");
            try (PreparedStatement stmt = conn.prepareStatement(updateArtistFirstNameSQL)) {
                stmt.setString(1, updatedAristFirstName);
                stmt.setString(2, artistToUpdateFirstName);
                stmt.setString(3, artistToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // ARTIST LAST NAME
        case "3":
            String updatedAristLastName = InputUtil.getString("What is the UPDATED last name?");
            try (PreparedStatement stmt = conn.prepareStatement(updateArtistLastNameSQL)) {
                stmt.setString(1, updatedAristLastName);
                stmt.setString(2, artistToUpdateFirstName);
                stmt.setString(3, artistToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // ARTIST COUNTRY
        case "4":
            String updatedArtistCountry = InputUtil.getString("What is the UPDATED country?");
            try (PreparedStatement stmt = conn.prepareStatement(updateArtistCountrySQL)) {
                stmt.setString(1, updatedArtistCountry);
                stmt.setString(2, artistToUpdateFirstName);
                stmt.setString(3, artistToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // ARTIST CITY
        case "5":
            String updatedArtistCity = InputUtil.getString("What is the UPDATED city?");
            try (PreparedStatement stmt = conn.prepareStatement(updateArtistCitySQL)) {
                stmt.setString(1, updatedArtistCity);
                stmt.setString(2, artistToUpdateFirstName);
                stmt.setString(3, artistToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // ARTIST BANK ACC #
        case "6":
            String updatedArtistBankAccountNumber = InputUtil.getString("What is the UPDATED bank account #?");
            try (PreparedStatement stmt = conn.prepareStatement(updateArtistBankAccNumSQL)) {
                stmt.setString(1, updatedArtistBankAccountNumber);
                stmt.setString(2, artistToUpdateFirstName);
                stmt.setString(3, artistToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // PHONE #
        case "7":
            String updatedArtistPhoneNumber = InputUtil.getString("What is the UPDATED phone #?");
            try (PreparedStatement stmt = conn.prepareStatement(updateArtistPhoneNumSQL)) {
                stmt.setString(1, updatedArtistPhoneNumber);
                stmt.setString(2, artistToUpdateFirstName);
                stmt.setString(3, artistToUpdateLastName);
                stmt.executeUpdate();
            }
            break;
        // STATUS
        case "8":
            String updatedArtistStatus = InputUtil.getString("What is the UPDATED status?");
            try (PreparedStatement stmt = conn.prepareStatement(retrieveuserId)) {
                stmt.setString(1, artistToUpdateFirstName);
                stmt.setString(2, artistToUpdateLastName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("userId");
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateArtistStatusSQL)) {
                            updateStmt.setString(1, updatedArtistStatus);
                            updateStmt.setInt(2, userId);
                            updateStmt.executeUpdate();
                        }
                    }
                }
            }
            break;
        // TYPE
        case "9":
            String updatedArtistType = InputUtil.getString("What is the UPDATED type?");
            try (PreparedStatement stmt = conn.prepareStatement(retrieveuserId)) {
                stmt.setString(1, artistToUpdateFirstName);
                stmt.setString(2, artistToUpdateLastName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("userId");
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateArtistTypeSQL)) {
                            updateStmt.setString(1, updatedArtistType);
                            updateStmt.setInt(2, userId);
                            updateStmt.executeUpdate();
                        }
                    }
                }
            }
            break;
        }
    }
}