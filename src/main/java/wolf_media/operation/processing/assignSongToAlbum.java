// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.processing;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.atom.insertAlbum;
import main.java.wolf_media.operation.atom.insertSong;
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.ExitException;

/**
 * Assign songs to albums
 * 
 * @author KR
 * 
 */
public class assignSongToAlbum extends OperationBase {

    private static final OperationBase insertAlbum       = new insertAlbum();
    private static final OperationBase insertSong        = new insertSong();

    private static final String insertIntoAlbumTracksSQL = "INSERT INTO AlbumTracks(albumId, audioId, trackNumber) VALUES(?,?,?)";
    
    private static final String retrieveAudioId          = "SELECT audioId FROM AudioEntities WHERE title = ?";
    private static final String retrieveAlbumId          = "SELECT albumId FROM Albums WHERE name = ?";
    
    private static final String albumExistSQL            = "SELECT 1 FROM Albums WHERE name = ?";
    private static final String songExistSQL             = "SELECT 1 FROM AudioEntities WHERE title = ?";

    /**
     * Get ALBUM TABLE info first then assign songs to it.
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
         * Now deduce whether the song exist in the Db or not Afterwards, assign the
         * song to the album
         */
        int numberOfSongs = InputUtil.getInt("How many songs you want to assign?");
        for (int i = 0; i < numberOfSongs; i++) {
            String songTitle = InputUtil.getString("What is the title of the song?");
            try (PreparedStatement existsStmt = conn.prepareStatement(songExistSQL)) {
                existsStmt.setString(1, songTitle);
                boolean songExist = false;
                try (ResultSet existsRS = existsStmt.executeQuery()) {
                    // True if a row is returned from the query, false otherwise
                    songExist = existsRS.next();
                }
                if (songExist) {
                    System.out.println("Song already exist in the database");
                } else {
                    System.out.println("Song not found in database hence we need to insertSong");
                    insertSong.executePartial(conn);
                    System.out.println("Song inserted into Db");
                }
            }
            String trackNumber = InputUtil.getString("What is the track number of this song?");
            try (PreparedStatement stmt = conn.prepareStatement(retrieveAudioId)) {
                stmt.setString(1, songTitle);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int songAudioId = rs.getInt("audioId");
                        try (PreparedStatement updateStmt = conn.prepareStatement(insertIntoAlbumTracksSQL)) {
                            updateStmt.setInt(1, albumId);
                            updateStmt.setInt(2, songAudioId);
                            updateStmt.setString(3, trackNumber);
                            updateStmt.executeUpdate();
                        }
                    }
                }
            }
        }
    }
}