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
 * Insert a sponsor
 * 
 * @author: KR
 */
public class insertSponsors extends OperationBase {

    private static final String insertSponsorsQuery = "INSERT INTO Sponsors(sponsorId, sponsorName) VALUES (?, ?)";

    /**
     * Insert a sponsor into the Sponsors TABLE
     * 
     * @param conn     The connection used to interact with MariaDb
     * @return Success if committed; SQLException in case of err
     */
    @Override
    protected void executeImpl(Connection conn) throws SQLException {

        Integer sponsorId = InputUtil.getIntOrNull("Insert a sponsorId or an empty string to generate one");
        if (sponsorId == null) {
            sponsorId = InputUtil.incrementUserId();
        }
        String sponsorName = InputUtil.getString("What is the sponsor name?");
        try (PreparedStatement stmt = conn.prepareStatement(insertSponsorsQuery)) {
            stmt.setInt(1, sponsorId);
            stmt.setString(2, sponsorName);
            stmt.executeUpdate(); 
        }
    }
}
