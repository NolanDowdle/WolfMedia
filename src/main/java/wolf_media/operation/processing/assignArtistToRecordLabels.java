// All "Information Processing" will be merged in the following pkg name
package main.java.wolf_media.operation.processing;

// Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import our custom runtime libraries
import main.java.wolf_media.operation.atom.insertSong;
import main.java.wolf_media.operation.atom.insertArtist;
import main.java.wolf_media.operation.atom.insertRecordLabel;
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.ExitException;

/**
 * Assign artist to record label
 * 
 * @author KR
 * 
 */
public class assignArtistToRecordLabels extends OperationBase {

    private static final OperationBase insertArtist       = new insertArtist();
    private static final OperationBase insertSong         = new insertSong();
    private static final OperationBase insertRecordLabel  = new insertRecordLabel();

    private static final String insertContractsArtist     = "INSERT INTO Contracts(artistId, recordLabelId, songId) VALUES(?,?,?)";
    
    private static final String retrieveArtistId          = "SELECT userId FROM Users WHERE firstName = ? AND lastName = ?";
    private static final String retrieveAudioId           = "SELECT audioId FROM AudioEntities WHERE title = ?";
    private static final String retrieveRecordLabelId     = "SELECT userId FROM Users WHERE firstName = ? AND lastName = ?";

    private static final String artistExistSQL            = "SELECT 1 FROM Users WHERE firstName = ? AND lastName = ?";
    private static final String songExistSQL              = "SELECT 1 FROM AudioEntities WHERE title = ?";
    private static final String recordLabelExistSQL       = "SELECT 1 FROM Users WHERE firstName = ? AND lastName = ?";

    /**
     * Get Record Label info first then assign artists to it.
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     * @throws          ExitException
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {
        
        // Global ID variables
        Integer recordLabelId = 0;
        Integer songAudioId   = 0;
        Integer artistId      = 0;
        
        /////////////////////////////////////////
        // RECORD LABEL LOGIC                   //
        /////////////////////////////////////////
        /*
         * We need to deduce if the record label exist or not because Contracts relation
         * needs the record label ID.
         *
         * If record label does not exist then we need to create a tuple before we can
         * invoke the Contracts relation.
         */
        String recordLabelFirstName = InputUtil.getString("What is the record label first name?");
        String recordLabelLastName  = InputUtil.getString("What is the record label last name?");
        try (PreparedStatement existsStmt = conn.prepareStatement(recordLabelExistSQL)) {
            existsStmt.setString(1, recordLabelFirstName);
            existsStmt.setString(2, recordLabelLastName);
            boolean recordLabelExists = false;
            try (ResultSet existsRS = existsStmt.executeQuery()) {
                // True if a row is returned from the query, false otherwise
                recordLabelExists = existsRS.next();
            }
            if (recordLabelExists) {
                System.out.println("Record Label already exist in the database");
            } else {
                System.out.println("Record Label not found in database hence we need to insertRecordLabel");
                insertRecordLabel.executePartial(conn);
                System.out.println("Record Label inserted into Db");
            }
        }
        
        // Now, we know the record label exist in the database hence retrieve the record label id
        try (PreparedStatement stmt = conn.prepareStatement(retrieveRecordLabelId)) {
            stmt.setString(1, recordLabelFirstName);
            stmt.setString(2, recordLabelLastName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    recordLabelId = rs.getInt("userId");
                }
            }
        }
        /*
         * Now deduce whether the artist exist in the Db or not 
         *   Afterwards, assign the artist to the album.
         */
        int numberOfArtists = InputUtil.getInt("How many artist you want to assign?");
        for (int i = 0; i < numberOfArtists; i++) {
            String artistFirstName = InputUtil.getString("What is the artist first name?");
            String artistLastName  = InputUtil.getString("What is the artist last name?");
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
            try (PreparedStatement stmt = conn.prepareStatement(retrieveArtistId)) {
                stmt.setString(1, artistFirstName);
                stmt.setString(2, artistLastName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        artistId = rs.getInt("userId");
                    }
                }
            }
            /*
             * In our implementation, the Contracts TABLE needs songId hence we have to ask
             *   about the Song name to deduce whether it exist or need creation. 
             */
            String songTitle = InputUtil.getString("What is the title of the song that you want to use?");
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
            try (PreparedStatement stmt = conn.prepareStatement(retrieveAudioId)) {
                stmt.setString(1, songTitle);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        songAudioId = rs.getInt("audioId");
                    }
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(insertContractsArtist)) {
                stmt.setInt(1, artistId);
                stmt.setInt(2, recordLabelId);
                stmt.setInt(3, songAudioId);
                stmt.executeUpdate();
            }
        }
    }
}
