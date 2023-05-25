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
import main.java.wolf_media.util.ExitException;

/**
 * Insert a song Handles the situations: 
 *   1] Assigning primary artist 
 *     1.1] Creates primary artist in case it does not exist 
 *     2] Assigning collaborating artist 
 *       2.2] Creates collaborating artist if they do not exist
 * 
 * @author KR
 * 
 */
public class insertSong extends OperationBase {

    // Reuse code instead of copying and pasting it
    private static final OperationBase insertArtist      = new insertArtist();
    private static final OperationBase insertRecordLabel = new insertRecordLabel();
    private static final OperationBase insertGenre       = new insertGenre();

    // Define my static SQL query statements
    private static final String insertSongQuery          = "INSERT INTO AudioEntities(audioId, title, duration, releaseDate, "
            + "releaseCountry, releaseLanguage) " + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String insertRoyaltyRateForSong = "INSERT INTO Songs(audioId, royaltyRate) VALUES (?, ?)";
    private static final String insertPrimaryArtist      = "INSERT INTO PrimaryArtists(artistId, songId) VALUES(?,?)";
    private static final String insertContractsArtist    = "INSERT INTO Contracts(artistId, recordLabelId, songId) VALUES(?,?,?)";
    private static final String insertIntoSongGenreTable = "INSERT INTO SongGenre(genreId, audioId) VALUES(?,?)";

    private static final String artistExistSQL           = "SELECT 1 From Users WHERE firstName = ? AND lastName = ?";
    private static final String recordLabelExistSQL      = "SELECT 1 From Users WHERE firstName = ? AND lastName = ?";
    private static final String collabAritstExistSQL     = "SELECT 1 From Users WHERE firstName = ? AND lastName = ?";
    private static final String genreExistSQL            = "SELECT 1 From Genres WHERE genreName = ?";

    private static final String retrieveArtistId         = "SELECT userId FROM Users WHERE firstName = ? AND lastName = ?";
    private static final String retrieveRecordLabelId    = "SELECT userId FROM Users WHERE firstName = ? AND lastName = ?";
    private static final String retrieveGenreId          = "SELECT genreId From Genres WHERE genreName = ?";

    /**
     * Inserts into the following TABLES: 
     *   1] AudioEntities 
     *   2] Songs 
     *   3] PrimaryArtist
     *   4] Contracts
     * 
     * @param conn     The connection used to interact with MariaDb
     * @return Success if committed; SQLException in case of err
     * @throws         ExitException
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {

        Integer audioId = InputUtil.getIntOrNull("Insert an audioId or an empty string to generate one");
        if (audioId == null) {
            audioId = InputUtil.incrementAudioId();
        }

        String songName        = InputUtil.getString("What is the title of the song?");
        String duration        = InputUtil.getString("What is the duration of the song in format HH:MM:SS?");
        String releaseDate     = InputUtil.getString("What is the release date in format YYYY-MM-DD?");
        String releaseCountry  = InputUtil.getString("What is the release country?");
        String releaseLanguage = InputUtil.getString("What is the release language?");
        String royaltyRate     = InputUtil.getString("What is the royalty rate?");

        try (PreparedStatement stmt = conn.prepareStatement(insertSongQuery)) {
            stmt.setInt(1, audioId);
            stmt.setString(2, songName);
            stmt.setString(3, duration);
            stmt.setString(4, releaseDate);
            stmt.setString(5, releaseCountry);
            stmt.setString(6, releaseLanguage);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(insertRoyaltyRateForSong)) {
            stmt.setInt(1, audioId);
            stmt.setString(2, royaltyRate);
            stmt.executeUpdate();
        }
        
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
        System.out.println("Before handling collaborator artist logic, we need to deduce if record label exist or not");
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
        
        // Get the record label Id since it already exist or we inserted it if it did
        // not exist
        int recordLabelId = 0;
        try (PreparedStatement stmt = conn.prepareStatement(retrieveRecordLabelId)) {
            stmt.setString(1, recordLabelFirstName);
            stmt.setString(2, recordLabelLastName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    recordLabelId = rs.getInt("userId");
                }
            }
        }

        //////////////////////////////////
        // PRIMARY ARTISTS LOGIC        //
        /////////////////////////////////
        /*
         * Deduce whether the artist exist or not 
         * If do not exist then insert the artist before assigning as primary artist 
         * If exist then go straight to assigning as primary artist
         */
        int numberOfSongArtist = InputUtil.getInt("Song has how many artists?");
        for (int i = 0; i < numberOfSongArtist; i++) {
            String artistFirstName = InputUtil.getString("What is the " + (i + 1) + " artist first name?");
            String artistLastName  = InputUtil.getString("What is the " + (i + 1) + " artist last name?");
            try (PreparedStatement existsStmt = conn.prepareStatement(artistExistSQL)) {
                existsStmt.setString(1, artistFirstName);
                existsStmt.setString(2, artistLastName);
                boolean artistExists = false;
                try (ResultSet existsRS = existsStmt.executeQuery()) {
                    // True if a row is returned from the query, false otherwise
                    artistExists = existsRS.next();
                }
                if (artistExists) {
                    System.out.println("Artist already exist in the database");
                } else {
                    System.out.println("Artist not found in database hence we need to insertArtist");
                    insertArtist.executePartial(conn);
                    System.out.println("Artist inserted into Db");
                }
                try (PreparedStatement stmt = conn.prepareStatement(retrieveArtistId)) {
                    stmt.setString(1, artistFirstName);
                    stmt.setString(2, artistLastName);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int artistUserId = rs.getInt("userId");
                            try (PreparedStatement primaryArtistInsertstmt = conn.prepareStatement(insertPrimaryArtist)) {
                                primaryArtistInsertstmt.setInt(1, artistUserId);
                                primaryArtistInsertstmt.setInt(2, audioId);
                                primaryArtistInsertstmt.executeUpdate();
                            }
                            try (PreparedStatement contractArtistInsertstmt = conn.prepareStatement(insertContractsArtist)) {
                                contractArtistInsertstmt.setInt(1, artistUserId);
                                contractArtistInsertstmt.setInt(2, recordLabelId);
                                contractArtistInsertstmt.setInt(3, audioId);
                                contractArtistInsertstmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        }


        /////////////////////////////////////////
        // COLLABORATORS ARTISTS LOGIC         //
        /////////////////////////////////////////
        /*
         * Same logic as primary artists
         */
        int numberOfSongCollabArtist = InputUtil.getInt("Song has how many collaborator artists?");
        for (int i = 0; i < numberOfSongCollabArtist; i++) {
            String collabArtistFirstName = InputUtil.getString("What is the " + (i + 1) + " collaborator artist first name?");
            String collabArtistLastName  = InputUtil.getString("What is the " + (i + 1) + " collaborator artist last name?");
            try (PreparedStatement existsStmt = conn.prepareStatement(collabAritstExistSQL)) {
                existsStmt.setString(1, collabArtistFirstName);
                existsStmt.setString(2, collabArtistLastName);
                boolean collabArtistExists = false;
                try (ResultSet existsRS = existsStmt.executeQuery()) {
                    // True if a row is returned from the query, false otherwise
                    collabArtistExists = existsRS.next();
                }
                if (collabArtistExists) {
                    System.out.println("Collaborator artist already exist in the database");
                } else {
                    System.out.println("Collaborator artist not found in database hence we need to insertArtist");
                    insertArtist.executePartial(conn);
                    System.out.println("Collaborator artist inserted into Db");
                }
                try (PreparedStatement stmt = conn.prepareStatement(retrieveArtistId)) {
                    stmt.setString(1, collabArtistFirstName);
                    stmt.setString(2, collabArtistLastName);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int artistUserId = rs.getInt("userId");
                            try (PreparedStatement contractArtistInsertstmt = conn.prepareStatement(insertContractsArtist)) {
                                contractArtistInsertstmt.setInt(1, artistUserId);
                                contractArtistInsertstmt.setInt(2, recordLabelId);
                                contractArtistInsertstmt.setInt(3, audioId);
                                contractArtistInsertstmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        }

        //////////////////
        // GENRE LOGIC  //
        //////////////////
        /*
         * Check if genre already exist 
         *   If yes then insert directly into Song Genre 
         *   If no then create a genre tuple then insert into Song Genre
         */
        int numberOfSongGenre = InputUtil.getInt("Song has how many genres?");
        for (int i = 0; i < numberOfSongGenre; i++) {
            String genreName = InputUtil.getString("What is the " + (i + 1) + " genre name?");
            try (PreparedStatement existsStmt = conn.prepareStatement(genreExistSQL)) {
                existsStmt.setString(1, genreName);
                boolean genreExists = false;
                try (ResultSet existsRS = existsStmt.executeQuery()) {
                    // True if a row is returned from the query, false otherwise
                    genreExists = existsRS.next();
                }
                if (genreExists) {
                    System.out.println("Genre name already exist in the database");
                  // IF NO
                } else {
                    System.out.println("Genre name not found in database hence we need to insertGenre");
                    insertGenre.executePartial(conn);
                }
            }
            // IF YES
            try (PreparedStatement stmt = conn.prepareStatement(retrieveGenreId)) {
                stmt.setString(1, genreName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int genreId = rs.getInt("genreId");
                        // Insert into the Song Genre Table
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertIntoSongGenreTable)) {
                            insertStmt.setInt(1, genreId);
                            insertStmt.setInt(2, audioId);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        }
    }
}