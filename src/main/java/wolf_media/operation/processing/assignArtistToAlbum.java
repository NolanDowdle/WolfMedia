// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.processing;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.atom.insertAlbum;
import main.java.wolf_media.operation.atom.insertArtist;
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.ExitException;

/**
 * Assign artist to albums
 * 
 * @author KR
 * 
 */
public class assignArtistToAlbum extends OperationBase {

    private static final OperationBase insertAlbum        = new insertAlbum();
    private static final OperationBase insertArtist       = new insertArtist();

    private static final String insertIntoAlbumCreatedBy  = "INSERT INTO AlbumCreatedBy(artistId, albumId) VALUES(?,?)";
    
    private static final String retrieveArtistId          = "SELECT userId FROM Users WHERE firstName = ? AND lastName = ?";
    private static final String retrieveAlbumId           = "SELECT albumId FROM Albums WHERE name = ?";

    private static final String albumExistSQL             = "SELECT 1 FROM Albums WHERE name = ?";
    private static final String artistExistSQL            = "SELECT 1 FROM Users WHERE firstName = ? AND lastName = ?";

    /**
     * Get ALBUM TABLE info first then assign artists to it.
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     * @throws          ExitException
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {

        /*
         * First perform album logic to deduce whether we need to create a new album
         */
        String albumTitle = InputUtil.getString("What is the title of the album?");
        try (PreparedStatement existsStmt = conn.prepareStatement(albumExistSQL)) {
            existsStmt.setString(1, albumTitle);
            boolean albumExist = false;
            try (ResultSet existsRS = existsStmt.executeQuery()) {
                // True if a row is returned from the query, false otherwise
                albumExist = existsRS.next();
            }
            if (albumExist) {
                System.out.println("Album already exist in the database");
            } else {
                System.out.println("Album not found in database hence we need to insertAlbum");
                insertAlbum.executePartial(conn);
                System.out.println("Album inserted into Db");
            }
        }

        /*
         * Regardless of creating a new album or not, we know by now that it exist in
         * the Db hence query for the albumId
         */
        Integer albumId = 0;
        try (PreparedStatement stmt = conn.prepareStatement(retrieveAlbumId)) {
            stmt.setString(1, albumTitle);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    albumId = rs.getInt("albumId");
                }
            }
        }

        /*
         * Now deduce whether the artist exist in the Db or not 
         *   Afterwards, assign the artist to the album.
         */
        int numberOfArtists = InputUtil.getInt("How many artist you want to assign?");
        for (int i = 0; i < numberOfArtists; i++) {
            String artistFirstName = InputUtil.getString("What is the artist first Name?");
            String artistLastName  = InputUtil.getString("What is the artist last Name?");
            try (PreparedStatement existsStmt = conn.prepareStatement(artistExistSQL)) {
                existsStmt.setString(1, artistFirstName);
                existsStmt.setString(2, artistLastName);
                boolean artistExist = false;
                try (ResultSet existsRS = existsStmt.executeQuery()) {
                    // True if a row is returned from the query, false otherwise
                    artistExist = existsRS.next();
                }
                if (artistExist) {
                    System.out.println("Artist already exist in the database");
                } else {
                    System.out.println("Artist not found in database hence we need to insertArtist");
                    insertArtist.executePartial(conn);
                    System.out.println("Artist inserted into Db");
                }
            }
            
            Integer artistId = 0;
            try (PreparedStatement stmt = conn.prepareStatement(retrieveArtistId)) {
                stmt.setString(1, artistFirstName);
                stmt.setString(2, artistLastName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        artistId = rs.getInt("userId");
                    }
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(insertIntoAlbumCreatedBy)) {
                stmt.setInt(1, artistId);
                stmt.setInt(2, albumId);
                stmt.executeUpdate(); 
            }
        }
    }
}
