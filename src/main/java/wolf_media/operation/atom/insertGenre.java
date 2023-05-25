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
 * Insert a genre 
 * 
 * @author KR
 */
public class insertGenre extends OperationBase {
    
    private static final String insertIntoGenreTable    = "INSERT INTO Genres(genreId, genreName) VALUES(?,?)";
    
    /**
     * Helper class for inserting into Genre table
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return Success  if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {
        
        Integer genreId = InputUtil.getIntOrNull("Insert an genreId or an empty string to generate one");
        if (genreId == null) {
            genreId = InputUtil.incrementGenreId();
        }
        
        String genreName = InputUtil.getString("What is the genre name?");
        
        try (PreparedStatement stmt = conn.prepareStatement(insertIntoGenreTable)) {
            stmt.setInt(1, genreId);
            stmt.setString(2, genreName);
            stmt.executeUpdate(); 
        }
    }
}
