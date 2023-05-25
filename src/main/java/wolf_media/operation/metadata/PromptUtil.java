package main.java.wolf_media.operation.metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.wolf_media.operation.atom.insertArtist;
import main.java.wolf_media.operation.atom.insertPodcast;
import main.java.wolf_media.operation.atom.insertPodcastEpisode;
import main.java.wolf_media.operation.atom.insertSong;
import main.java.wolf_media.operation.atom.insertUser;
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.ExitException;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.TriValue;

public class PromptUtil {

    //
    // User
    //
    
    // Operation to prompt user to create a new user
    private static final OperationBase INSERT_USER = new insertUser();
    
    // SQL statement to check if a user exists
    private static final String USER_EXISTS_STMT =
            "SELECT 1 FROM Users WHERE userId = ?;";
    
    /**
     * Checks if a user exists
     * @param conn Database connection
     * @param userId User ID to check for existence
     * @return True if the user exists, false otherwise
     * @throws SQLException Occurs when there is an SQL error or database connection error
     */
    public static boolean userExistsCheck(Connection conn, int userId) throws SQLException {
        try (PreparedStatement existsStmt = conn.prepareStatement(USER_EXISTS_STMT)) {
            existsStmt.setInt(1, userId);
            try (ResultSet rs = existsStmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Helper function that prompts user to create user if it does not exist
     * @param conn Database connection
     * @param userId UserID to check if they exist
     * @return Three possible values, true, false, or exit
     * @throws SQLException
     * @throws ExitException
     */
    public static TriValue.Value userExistsPrompt(Connection conn, int userId) throws SQLException, ExitException {
        boolean userExists = userExistsCheck(conn, userId);
        if (userExists) {
            return TriValue.Value.TRUE;
        } else {
            String prompt = "User with ID " + userId + " not found. Do you want to create one";
            boolean promptRes = InputUtil.yesNoPrompt(prompt);
            if (promptRes) {
                INSERT_USER.executePartial(conn);
            } else {
                // cannot continue with non-existant user, return exit
                return TriValue.Value.EXIT;
            }
        }
        userExists = userExistsCheck(conn, userId);
        if (userExists) {
            return TriValue.Value.TRUE;
        } else {
            return TriValue.Value.FALSE;
        }
    }
    
    /**
     * Wrapper function to easily check if a user exists and prompt creation
     * @param conn Database connection
     * @param userId UserID to check if they exist
     * @throws SQLException
     * @throws ExitException
     */
    public static void userExistsPromptHelper(Connection conn, int userId) throws SQLException, ExitException {
        TriValue.Value userExistsVal = PromptUtil.userExistsPrompt(conn, userId);
        if (userExistsVal == TriValue.Value.EXIT) {
            throw new ExitException("No user, exiting");
        } else if (userExistsVal == TriValue.Value.FALSE) {
            throw new ExitException("No user with ID " + userId + " found, exiting");
        }
    }
    
    //
    // Artist
    //
    
    // Operation to create an artist
    private static final OperationBase INSERT_ARTIST = new insertArtist();
    
    // SQL statement to check if an artist exists
    private static final String ARTIST_EXSITS_STMT =
            "SELECT 1 FROM Artists WHERE userId = ?;";
    
    /**
     * Checks if an artist exists
     * @param conn Database connection
     * @param userId artist's user ID
     * @return True if the artist exists, false otherwise
     * @throws SQLException SQL or database error
     */
    public static boolean artistExistsCheck(Connection conn, int userId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(ARTIST_EXSITS_STMT)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * checks for existence and prompts user to create an artist
     * @param conn Database connection
     * @param artistId Artist ID to check for existence
     * @return Three possible values, true, false, or exit
     * @throws SQLException
     * @throws ExitException
     */
    public static TriValue.Value artistExistsPrompt(Connection conn, int artistId) throws SQLException, ExitException {
        boolean artistExists = artistExistsCheck(conn, artistId);
        if (artistExists) {
            return TriValue.Value.TRUE;
        } else {
            String prompt = "Artist with ID " + artistId + " not found. Do you want to create one";
            boolean promptRes = InputUtil.yesNoPrompt(prompt);
            if (promptRes) {
                INSERT_USER.executePartial(conn);
            } else {
                // cannot continue with non-existant artist, return exit
                return TriValue.Value.EXIT;
            }
        }
        artistExists = userExistsCheck(conn, artistId);
        if (artistExists) {
            return TriValue.Value.TRUE;
        } else {
            return TriValue.Value.FALSE;
        }
    }
    
    /**
     * Wrapper function to easily check if an artist exists and prompt creation
     * @param conn Database connection
     * @param artistId Artist ID
     * @throws SQLException
     * @throws ExitException
     */
    public static void artistExistsPromptHelper(Connection conn, int artistId) throws SQLException, ExitException {
        TriValue.Value artistExistsVal = PromptUtil.userExistsPrompt(conn, artistId);
        if (artistExistsVal == TriValue.Value.EXIT) {
            throw new ExitException("No artist, exiting");
        } else if (artistExistsVal == TriValue.Value.FALSE) {
            throw new ExitException("No artist with ID " + artistId + " found, exiting");
        }
    }
    
    //
    // Song
    //
    
    /**
     * Insert song operation
     */
    private static final OperationBase INSERT_SONG = new insertSong();
    
    /**
     * SQL statement to check if a song exists
     */
    private static final String SONG_EXISTS_STMT = 
            "SELECT 1 FROM Songs WHERE audioId = ?;";
    
    /**
     * Checks if a song exists
     * @param conn Database connection
     * @param songId The song ID to lookup
     * @return True if the song exists, false otherwise
     * @throws SQLException
     */
    public static boolean songExistsCheck(Connection conn, int songId) throws SQLException {
        try (PreparedStatement existsStmt = conn.prepareStatement(SONG_EXISTS_STMT)) {
            existsStmt.setInt(1, songId);
            try (ResultSet rs = existsStmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Checks and prompts users for to create a song
     * @param conn Database connection
     * @param songId Song ID to check
     * @return Three possible values, true, false, or exit
     * @throws SQLException
     * @throws ExitException
     */
    public static TriValue.Value songExistsPrompt(Connection conn, int songId) throws SQLException, ExitException {
        boolean songExists = songExistsCheck(conn, songId);
        if (songExists) {
            return TriValue.Value.TRUE;
        } else {
            String prompt = "Song with ID " + songId + " not found. Do you want to create one";
            boolean promptRes = InputUtil.yesNoPrompt(prompt);
            if (promptRes) {
                INSERT_SONG.executePartial(conn);
            } else {
                // cannot continue with non-existant song, return exit
                return TriValue.Value.EXIT;
            }
        }
        songExists = songExistsCheck(conn, songId);
        if (songExists) {
            return TriValue.Value.TRUE;
        } else {
            return TriValue.Value.FALSE;
        }
    }
    
    /**
     * Wrapper function to easily check if an artist exists and prompt creation
     * @param conn Database connection
     * @param songId Song ID to check for existence
     * @throws SQLException
     * @throws ExitException
     */
    public static void songExistsPromptHelper(Connection conn, int songId) throws SQLException, ExitException {
        TriValue.Value songExistsVal = PromptUtil.songExistsPrompt(conn, songId);
        if (songExistsVal == TriValue.Value.EXIT) {
            throw new ExitException("No song, exiting");
        } else if (songExistsVal == TriValue.Value.FALSE) {
            throw new ExitException("No song with ID " + songId + " found, exiting");
        }
    }
    
    //
    // Podcast
    //

    /**
     * Operation to insert a podcast
     */
    private static final OperationBase INSERT_PODCAST = new insertPodcast();

    
    /**
     * SQL statement to check if a podcast exists
     */
    private static final String PODCAST_EXISTS_STMT =
            "SELECT 1 FROM Podcasts WHERE podcastId = ?;";
    
    /**
     * Checks if a podcast exists
     * @param conn Database connection
     * @param podcastId Podcast ID to check for existence
     * @return Returns true if the podcast exists, false otherwise
     * @throws SQLException
     */
    public static boolean podcastExistsCheck(Connection conn, int podcastId) throws SQLException {
        try (PreparedStatement existsStmt = conn.prepareStatement(PODCAST_EXISTS_STMT)) {
            existsStmt.setInt(1, podcastId);
            try (ResultSet rs = existsStmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Checks and prompts users for to create a podcast
     * @param conn Database connection
     * @param podcastId Podcast ID to check for existence
     * @return Three possible values, true, false, or exit
     * @throws SQLException
     * @throws ExitException
     */
    public static TriValue.Value podcastExistsPrompt(Connection conn, int podcastId) throws SQLException, ExitException {
        boolean podcastExists = podcastExistsCheck(conn, podcastId);
        if (podcastExists) {
            return TriValue.Value.TRUE;
        } else {
            String prompt = "Podcast with ID " + podcastId + " not found. Do you want to create one";
            boolean promptRes = InputUtil.yesNoPrompt(prompt);
            if (promptRes) {
                INSERT_PODCAST.executePartial(conn);
            } else {
                // cannot continue with non-existant podcast, return exit
                return TriValue.Value.EXIT;
            }
        }
        podcastExists = podcastExistsCheck(conn, podcastId);
        if (podcastExists) {
            return TriValue.Value.TRUE;
        } else {
            return TriValue.Value.FALSE;
        }
    }
    
    /**
     * Wrapper function to easily check if an podcast exists and prompt creation
     * @param conn Database connection
     * @param podcastId Podcast Id to create
     * @throws SQLException
     * @throws ExitException
     */
    public static void podcastExistsPromptHelper(Connection conn, int podcastId) throws SQLException, ExitException {
        TriValue.Value podcastExistsVal = PromptUtil.podcastExistsPrompt(conn, podcastId);
        if (podcastExistsVal == TriValue.Value.EXIT) {
            throw new ExitException("No podcast, exiting");
        } else if (podcastExistsVal == TriValue.Value.FALSE) {
            throw new ExitException("No podcast with ID " + podcastId + " found, exiting");
        }
    }
    
    //
    // Podcast Audio
    //
    
    /**
     * Operation to insert a podcast episode
     */
    private static final OperationBase INSERT_PODCAST_EPISODE = new insertPodcastEpisode();
    
    /**
     * SQL statement to check if a podcast episode exists
     */
    private static final String PODCAST_EP_EXISTS_STMT = 
            "SELECT 1 FROM PodcastAudios WHERE audioId = ?;";
    
    /**
     * Checks if a podcast episode exists
     * @param conn Database connection
     * @param podcastAudioId Podcast episode to check/create
     * @return Returns true if the podcast exists, false otherwise
     * @throws SQLException
     */
    public static boolean podcastEpisodeExistsCheck(Connection conn, int podcastAudioId) throws SQLException {
        try (PreparedStatement podEpExistsStmt = conn.prepareStatement(PODCAST_EP_EXISTS_STMT)) {
            podEpExistsStmt.setInt(1, podcastAudioId);
            try (ResultSet rs = podEpExistsStmt.executeQuery()){
                return rs.next();
            }
        }
    }
    
    /**
     * Checks and prompts users for to create a podcast episode
     * @param conn Database connection
     * @param podcastAudioId podcast episode to check for existence
     * @return Three possible values, true, false, or exit
     * @throws SQLException
     * @throws ExitException
     */
    public static TriValue.Value podcastEpisodeExistsPrompt(Connection conn, int podcastAudioId) throws SQLException, ExitException {
        boolean podcastAudioExists = podcastEpisodeExistsCheck(conn, podcastAudioId);
        if (podcastAudioExists) {
            return TriValue.Value.TRUE;
        } else {
            String prompt = "Podcast Episode with ID " + podcastAudioId + " not found. Do you want to create one";
            boolean promptRes = InputUtil.yesNoPrompt(prompt);
            if (promptRes) {
                INSERT_PODCAST_EPISODE.executePartial(conn);
            } else {
                // cannot add listen to non-existant podcast, return success
                return TriValue.Value.EXIT;
            }
        }
        podcastAudioExists = podcastEpisodeExistsCheck(conn, podcastAudioId);
        if (podcastAudioExists) {
            return TriValue.Value.TRUE;
        } else {
            return TriValue.Value.FALSE;
        }
    }
    
    /**
     * Wrapper function to easily check if an podcast episode exists and prompt creation
     * @param conn Database connection
     * @param podcastAudioId Podcast episode ID to check for existence and create
     * @throws SQLException
     * @throws ExitException
     */
    public static void podcastEpisodeExistsPromptHelper(Connection conn, int podcastAudioId) throws SQLException, ExitException {
        TriValue.Value podcastEpExistsVal = PromptUtil.podcastEpisodeExistsPrompt(conn, podcastAudioId);
        if (podcastEpExistsVal == TriValue.Value.EXIT) {
            throw new ExitException("No podcast episode, exiting");
        } else if (podcastEpExistsVal == TriValue.Value.FALSE) {
            throw new ExitException("No podcast episode with ID " + podcastAudioId + " found, exiting");
        }
    }

}
