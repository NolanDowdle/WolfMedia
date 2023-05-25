// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.atom;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.ExitException;
import main.java.wolf_media.util.InputUtil;

/**
 * Insert an artist
 * 
 * @author KR
 * 
 */
public class insertArtist extends OperationBase {

    // Used for PrimaryGenre TABLE insertion 
    private static final OperationBase insertGenre      = new insertGenre();

    // Define my static SQL query statements
    private static final String insertUserQuery         = "INSERT INTO Users(userId, email, firstName, lastName, "
            + "country, city, subfee, bankAccountNumber, acctStatus, regDate, phoneNum) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String insertArtistQuery       = "INSERT INTO Artists(userId, artistStatus, type) VALUES (?, ?, ?)";
    private static final String insertPrimaryGenreQuery = "INSERT INTO PrimaryGenre(artistId, genreId) VALUES (?,?)";

    private static final String genreExistSQL           = "SELECT 1 From Genres WHERE genreName = ?";
    private static final String retrieveGenreId         = "SELECT genreId From Genres WHERE genreName = ?";

    /**
     * Insert into the following TABLES: 
     *   1] Users 
     *   2] Artists 
     *   3] PrimaryGenre 
     *   4] Genre (if applicable)
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     * @throws          ExitException 
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {

        Integer userId = InputUtil.getIntOrNull("Insert an userId or an empty string to generate one");
        if (userId == null) {
            userId = InputUtil.incrementUserId();
        }

        String artistEmailAddr         = InputUtil.getString("What is the artist e-mail address?");
        String artistFirstname         = InputUtil.getString("What is the artist first name?");
        String artistLastName          = InputUtil.getString("What is the artist last name?");
        String artistCountry           = InputUtil.getString("What is the artist country?");
        String artistCity              = InputUtil.getString("What is the artist city?");
        String artistBankAccountNumber = InputUtil.getString("What is the artist bank account number?");
        String artistPhoneNumber       = InputUtil.getString("What is the artist phone number number?");

        String artistStatus            = InputUtil.getString("What is the artist status (active or retired)?");
        String artistType              = InputUtil.getString("What is the artist type (band, musician, or composer)?");

        String artistPrimaryGenre      = InputUtil.getString("What is the artist primary genre?");

        // Insert into the Users table
        try (PreparedStatement stmt = conn.prepareStatement(insertUserQuery)) {
            stmt.setInt(1, userId);
            stmt.setString(2, artistEmailAddr);
            stmt.setString(3, artistFirstname);
            stmt.setString(4, artistLastName);
            stmt.setString(5, artistCountry);
            stmt.setString(6, artistCity);
            // Below is the subscription fee hard coded to zero
            stmt.setString(7, "0");
            stmt.setString(8, artistBankAccountNumber);
            // Below is the account status hard coded to 'Free'
            stmt.setString(9, "Free");
            stmt.setString(10, InputUtil.getTodayDateAndTime());
            stmt.setString(11, artistPhoneNumber);
            stmt.executeUpdate();
        }

        // Insert into the Artists table
        try (PreparedStatement stmt = conn.prepareStatement(insertArtistQuery)) {
            stmt.setInt(1, userId);
            stmt.setString(2, artistStatus);
            stmt.setString(3, artistType);
            stmt.executeUpdate();
        }

        // Insert into the Genre table if genre does not exist inside of it already
        try (PreparedStatement existsStmt = conn.prepareStatement(genreExistSQL)) {
            existsStmt.setString(1, artistPrimaryGenre);
            boolean genreExists = false;
            try (ResultSet existsRS = existsStmt.executeQuery()) {
                // True if a row is returned from the query, false otherwise
                genreExists = existsRS.next();
            }
            if (genreExists) {
                System.out.println("Genre name already exist in the database");
            } else {
                System.out.println("Genre name not found in database hence we need to insertGenre");
                insertGenre.executePartial(conn);
                System.out.println("Genre name inserted into Db");
            }
        }

        // First get the genreId before inserting into primary genre table
        try (PreparedStatement stmt = conn.prepareStatement(retrieveGenreId)) {
            stmt.setString(1, artistPrimaryGenre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int genreId = rs.getInt("genreId");
                    // Insert into the Primary Genre table
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertPrimaryGenreQuery)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, genreId);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }
}