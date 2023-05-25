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
 * Insert a podcast
 * 
 * @author KR
 * 
 */
public class insertPodcast extends OperationBase {
    // Reuse code instead of copying and pasting it
    private static final OperationBase insertGenre             = new insertGenre();
    private static final OperationBase insertSponsor           = new insertSponsors();
    private static final OperationBase insertPodcastHost       = new insertPodcastHost();
    private static final String insertIntoPodcastsTable        = "INSERT INTO Podcasts(podcastId, podcastName, podcastLanguage) VALUES(?,?,?)";
    private static final String insertIntoPodcastGenreTable    = "INSERT INTO PodcastGenres(podcastId, genreId) VALUES(?,?)";
    private static final String insertIntoSponsorOfTable       = "INSERT INTO SponsorOf(podcastId, sponsorId) VALUES(?,?)";
    private static final String insertIntoPodcastToPodcastHost = "INSERT INTO PodcastToPodcastHost(hostId, podcastId) VALUES(?,?)";
    
    private static final String retrieveGenreId                = "SELECT genreId From Genres WHERE genreName = ?";
    private static final String retrieveSponsorId              = "SELECT sponsorId From Sponsors WHERE sponsorName = ?";
    private static final String retrievePodcastToPodcastId     = "SELECT userId From Users WHERE firstName = ? AND lastName = ?";
    
    private static final String genreExistSQL                  = "SELECT 1 From Genres WHERE genreName = ?";
    private static final String sponsorExistSQL                = "SELECT 1 From Sponsors WHERE sponsorName = ?";
    private static final String podcastHostExistSQL            = "SELECT 1 From Users WHERE firstName = ? AND lastName = ?";

    /**
     * Insert into the following TABLES: 
     *   1] Podcasts 
     *   2] PodcastGenre 
     *   3] SponsorOf 
     *   4] Sponsors
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     * @throws          ExitException
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException, ExitException {

        Integer podcastId = InputUtil.getIntOrNull("Insert an podcastId or an empty string to generate one");
        if (podcastId == null) {
            podcastId = InputUtil.incrementPodcastId();
        }

        String podcastName              =  InputUtil.getString("What is the podcast name?");
        String podcastLanguage          =  InputUtil.getString("What is the podcast language?");
        String podcastFlatFee           =  InputUtil.getString("What is the podcast flat fee?");
        String podcastHostFirstName     =  InputUtil.getString("First name of podcast host?");
        String podcastHostLastName      =  InputUtil.getString("Last name of podcast host?");

        try (PreparedStatement stmt = conn.prepareStatement(insertIntoPodcastsTable)) {
            stmt.setInt(1, podcastId);
            stmt.setString(2, podcastName);
            stmt.setString(3, podcastLanguage);
            stmt.setString(4, podcastFlatFee);
            stmt.executeUpdate(); 
        }

        //////////////////
        // GENRE LOGIC  //
        //////////////////
        /*
         * Check if genre already exist 
         *   If yes then insert directly into Podcast Genre 
         *   If no then create a genre tuple then insert into Pocast Genre
         */
        int numberOfPodcastGenre = InputUtil.getInt("Podcast has how many genres?");
        for (int i = 0; i < numberOfPodcastGenre; i++) {
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
                    System.out.println("Genre name inserted in the Db");
                }
            }
            // IF YES
            try (PreparedStatement stmt = conn.prepareStatement(retrieveGenreId)) {
                stmt.setString(1, genreName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int genreId = rs.getInt("genreId");
                        // Insert into the Song Genre Table
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertIntoPodcastGenreTable)) {
                            insertStmt.setInt(1, podcastId);
                            insertStmt.setInt(2, genreId);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        }

        /////////////////////////////////////////
        // PODCAST SPONSOR LOGIC                //
        /////////////////////////////////////////
        /*
         * Handle podcast sponsors 
         *   Insert into SponsorOf Table 
         *   Insert into Sponsors Table
         */
        int numberOfPodcastSponsors = InputUtil.getInt("Podcast has how many sponsors?");
        for (int i = 0; i < numberOfPodcastSponsors; i++) {
            String sponsorName = InputUtil.getString("What is the sponsor name?");
            try (PreparedStatement existsStmt = conn.prepareStatement(sponsorExistSQL)) {
                existsStmt.setString(1, sponsorName);
                boolean sponsorExists = false;
                try (ResultSet existsRS = existsStmt.executeQuery()) {
                    // True if a row is returned from the query, false otherwise
                    sponsorExists = existsRS.next();
                }
                if (sponsorExists) {
                    System.out.println("Sponsor already exist in the database");
                } else {
                    System.out.println("Sponsor not found in database hence we need to insertSponsor");
                    insertSponsor.executePartial(conn);
                    System.out.println("Sponsor inserted into Db");
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(retrieveSponsorId)) {
                stmt.setString(1, sponsorName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int sponsorId = rs.getInt("sponsorId");
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertIntoSponsorOfTable)) {
                            insertStmt.setInt(1, podcastId);
                            insertStmt.setInt(2, sponsorId);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        }
        
        try (PreparedStatement existsStmt = conn.prepareStatement(podcastHostExistSQL)) {
            existsStmt.setString(1, podcastHostFirstName);
            existsStmt.setString(2, podcastHostLastName);
            boolean podcastHostExists = false;
            try (ResultSet existsRS = existsStmt.executeQuery()) {
                // True if a row is returned from the query, false otherwise
                podcastHostExists = existsRS.next();
            }
            if (podcastHostExists) {
                System.out.println("Podcast host already exist in the database");
              // IF NO
            } else {
                System.out.println("Podcast host not found in database hence we need to insertPodcastHost");
                insertPodcastHost.executePartial(conn);
                System.out.println("Podcast host inserted in the Db");
            }
        }
        
        
        try (PreparedStatement stmt = conn.prepareStatement(retrievePodcastToPodcastId)) {
            stmt.setString(1, podcastHostFirstName);
            stmt.setString(2, podcastHostLastName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int podcastHostId = rs.getInt("userId");
                    // Insert into the Song Genre Table
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertIntoPodcastToPodcastHost)) {
                        insertStmt.setInt(1, podcastHostId);
                        insertStmt.setInt(2, podcastId);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }
}
