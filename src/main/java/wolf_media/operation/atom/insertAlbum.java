// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.atom;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;

/*
 * Create a new album
 * 
 * @author KR
 * 
 */
public class insertAlbum extends OperationBase {

    private static final String insertIntoAlbumTableSQL = "INSERT INTO Albums(albumId, name, releaseYear, edition) VALUES(?,?,?,?)";
    /**
     * Helper class to create a new ALBUM
     * 
     * @param conn     The connection used to interact with MariaDb
     * @return Success if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        Integer albumId = InputUtil.getIntOrNull("Insert an albumId or an empty string to generate one");
        if (albumId == null) {
            albumId = InputUtil.incrementAlbumId();
        }

        String albumTitle       = InputUtil.getString("What is the title of the new album?");
        String albumReleaseYear = InputUtil.getString("What is the release date in format YYYY?");
        String albumEdition     = InputUtil.getString("What is the release edition (special, limited, collector's edition, remastered, regular)?");
        
        try (PreparedStatement stmt = conn.prepareStatement(insertIntoAlbumTableSQL)) {
            stmt.setInt(1, albumId);
            stmt.setString(2, albumTitle);
            stmt.setString(3, albumReleaseYear);
            stmt.setString(4, albumEdition);
            stmt.executeUpdate();
        }
    }
}
