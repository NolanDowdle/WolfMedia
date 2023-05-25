//All "Information Processing" will be merged in the following pkg name
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
 * Delete an artist
 * 
 * @author KR
 * 
 */
public class deleteArtist extends OperationBase {
    // Define my static SQL query statements
    private static final String retrieveuserId                       = "SELECT userId FROM Users WHERE firstName = ? and lastName = ?";
    private static final String deleteFromPrimaryArtistsTable        = "DELETE FROM PrimaryArtists WHERE artistId = ?";
    private static final String deleteFromContractsTable             = "DELETE FROM Contracts WHERE artistId = ?";
    private static final String deleteFromPrimaryGenreTable          = "DELETE FROM PrimaryGenre WHERE artistId = ?";
    private static final String deleteFromAlbumCreatedBy             = "DELETE FROM AlbumCreatedBy WHERE artistId = ?";
    private static final String deleteUserFromArtistTable            = "DELETE FROM Artists WHERE userId = ?";
    private static final String deleteUserFromUsersTable             = "DELETE FROM Users WHERE userId = ?";
    
    private static final String doesArtistExistInPrimaryArtistsTbl   = "SELECT 1 From PrimaryArtists WHERE artistId = ?";
    private static final String doesSongExistInAlbumCreatedByTbl     = "SELECT 1 FROM AlbumCreatedBy WHERE artistId = ?";
    private static final String doesSongExistInContractsTbl          = "SELECT 1 FROM Contracts WHERE artistId = ?";
    private static final String doesSongExistInPrimaryGenreTbl       = "SELECT 1 FROM PrimaryGenre WHERE artistId = ?";

    /**
     * Delete Artist from the following TABLE IN THE GIVEN ORDER:
     *   1] PrimaryGenre
     *   2] Artists
     *   3] Users
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        String artistToDeleteFirstName = InputUtil.getString("What is the artist first name that you want to DELETE?");
        String artistToDeleteLastName  = InputUtil.getString("What is the artist last name that you want to DELETE?");
        try (PreparedStatement stmt = conn.prepareStatement(retrieveuserId)) {
            stmt.setString(1, artistToDeleteFirstName);
            stmt.setString(2, artistToDeleteLastName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    try (PreparedStatement existsStmt = conn.prepareStatement(doesArtistExistInPrimaryArtistsTbl)) {
                        existsStmt.setInt(1, userId);
                        boolean doesArtistExist = false;
                        try (ResultSet existsRS = existsStmt.executeQuery()) {
                            doesArtistExist = existsRS.next();
                        }
                        if (doesArtistExist) {
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteFromPrimaryArtistsTable)) {
                                deleteStmt.setInt(1, userId);
                                deleteStmt.executeUpdate();
                            }
                        }
                    }
                    try (PreparedStatement existsStmt = conn.prepareStatement(doesSongExistInAlbumCreatedByTbl)) {
                        existsStmt.setInt(1, userId);
                        boolean doesArtistExist = false;
                        try (ResultSet existsRS = existsStmt.executeQuery()) {
                            doesArtistExist = existsRS.next();
                        }
                        if (doesArtistExist) {
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteFromAlbumCreatedBy)) {
                                deleteStmt.setInt(1, userId);
                                deleteStmt.executeUpdate();
                            }
                        }
                    }
                    try (PreparedStatement existsStmt = conn.prepareStatement(doesSongExistInContractsTbl)) {
                        existsStmt.setInt(1, userId);
                        boolean doesArtistExist = false;
                        try (ResultSet existsRS = existsStmt.executeQuery()) {
                            doesArtistExist = existsRS.next();
                        }
                        if (doesArtistExist) {
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteFromContractsTable)) {
                                deleteStmt.setInt(1, userId);
                                deleteStmt.executeUpdate();
                            }
                        }
                    }
                    try (PreparedStatement existsStmt = conn.prepareStatement(doesSongExistInPrimaryGenreTbl)) {
                        existsStmt.setInt(1, userId);
                        boolean doesArtistExist = false;
                        try (ResultSet existsRS = existsStmt.executeQuery()) {
                            doesArtistExist = existsRS.next();
                        }
                        if (doesArtistExist) {
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteFromPrimaryGenreTable)) {
                                deleteStmt.setInt(1, userId);
                                deleteStmt.executeUpdate();
                            }
                        }
                    }
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteUserFromArtistTable)) {
                        deleteStmt.setInt(1, userId);
                        deleteStmt.executeUpdate();
                    }
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteUserFromUsersTable)) {
                        deleteStmt.setInt(1, userId);
                        deleteStmt.executeUpdate();
                    }
                }
            }
        }
    }
}
