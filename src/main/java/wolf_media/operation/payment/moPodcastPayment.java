// All "Processing of Payments" will be merged in the following pkg name
package main.java.wolf_media.operation.payment;

//Import necessary runtime libraries from the Java framework 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

// Import our custom runtime libraries
import main.java.wolf_media.operation.common.OperationBase;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.util.OutputUtil;

/**
 * 
 * Author:      JK
 * Purpose:     Post monthly podcast host payments to the ledger
 * Composed On: April 1st, 2023
 * Modified On: April 1st, 2023
 * 
 */
public class moPodcastPayment extends OperationBase {

    private static final String TQUERY = "SELECT hostId, SYSDATE() AS transDate, 0 AS transComplete, "
            + "SUM(adCount * 5 + flatFee) AS amount, 'podcast' AS transType FROM PodcastToPodcastHost ph "
            + "JOIN PodcastEpisodes pe ON ph.podcastId = pe.podcastId "
            + "JOIN Podcasts p ON ph.podcastId = p.podcastId " + "JOIN PodcastAudios pa ON pa.audioId = pe.episodeId "
            + "JOIN AudioEntities ae ON pa.audioId = ae.audioId "
            + "WHERE ? < releaseDate AND releaseDate <= ? GROUP BY hostId";
    
    private static final String LQUERY = "SELECT * FROM Ledger WHERE transType = 'podcast' and (? <= transDate AND transDate <= ?)";
    
    private static final String INSERT = "INSERT INTO Ledger "
            + "SELECT hostId as userId, SYSDATE() AS transDate, 0 AS transComplete, "
            + "SUM(adCount * 5 + flatFee) AS amount, 'podcast' AS transType FROM PodcastToPodcastHost ph "
            + "JOIN PodcastEpisodes pe ON ph.podcastId = pe.podcastId "
            + "JOIN Podcasts p ON ph.podcastId = p.podcastId " + "JOIN PodcastAudios pa ON pa.audioId = pe.episodeId "
            + "JOIN AudioEntities ae ON pa.audioId = ae.audioId "
            + "WHERE ? < releaseDate AND releaseDate <= ? AND hostId NOT IN "
            + "(SELECT userId FROM Ledger WHERE transType = 'podcast' AND (? <= transDate AND transDate <= ?)) "
            + "GROUP BY hostId";
    
    private static final String QUERY = "SELECT hostId, SYSDATE() AS transDate, 0 AS transComplete, "
            + "SUM(adCount * 5 + flatFee) AS amount, 'podcast' AS transType FROM PodcastToPodcastHost ph "
            + "JOIN PodcastEpisodes pe ON ph.podcastId = pe.podcastId "
            + "JOIN Podcasts p ON ph.podcastId = p.podcastId " + "JOIN PodcastAudios pa ON pa.audioId = pe.episodeId "
            + "JOIN AudioEntities ae ON pa.audioId = ae.audioId "
            + "WHERE ? < releaseDate AND releaseDate <= ? AND hostId NOT IN "
            + "(SELECT userId FROM Ledger WHERE transType = 'podcast' AND (? <= transDate AND transDate <= ?)) "
            + "GROUP BY hostId";
	
	 /**
     * Monthly Podcast Host Payment
     * 
     * @param conn      The connection used to interact with MariaDb
     * @return          Success  if committed; SQLException in case of err
     */
     protected void executeImpl(Connection conn) throws SQLException {
         
         String startDate = InputUtil.getString("What is the starting date in format YYYY-MM-DD?");
         String endDate  = InputUtil.getString("What is the ending date in format YYYY-MM-DD?");
         
         try (PreparedStatement tCalc = conn.prepareStatement(TQUERY)) {
             tCalc.setString(1, startDate);
             tCalc.setString(2, endDate);
             String prompt = "Here are the calculated royalty payments for the period.";
             System.out.println(prompt);
             try (ResultSet rs = tCalc.executeQuery()) {
                 
                 OutputUtil.printResultSet(rs);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
         try (PreparedStatement transLedger = conn.prepareStatement(LQUERY)) {
             transLedger.setString(1, startDate);
             transLedger.setString(2, endDate);
             try (ResultSet ledgerRS = transLedger.executeQuery()) {
                 try {
                     boolean tinLedger = ledgerRS.next();
                     ledgerRS.beforeFirst();
                     if (tinLedger) {
                         String prompt = "These transactions were found in the Ledger, and were not re-posted.";
                         System.out.println(prompt);
                         
                         OutputUtil.printResultSet(ledgerRS);
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }
         try (PreparedStatement qstmt = conn.prepareStatement(QUERY)) {
             qstmt.setString(1, startDate);
             qstmt.setString(2, endDate);
             qstmt.setString(3, startDate);
             qstmt.setString(4, endDate);
             try (ResultSet rs = qstmt.executeQuery()) {
                 
                 OutputUtil.printResultSet(rs);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
         try (PreparedStatement istmt = conn.prepareStatement(INSERT)) {
             istmt.setString(1, startDate);
             istmt.setString(2, endDate);
             istmt.setString(3, startDate);
             istmt.setString(4, endDate);
             int   numTrans = istmt.executeUpdate();
             String prompt  = "Podcast episode payments for " + numTrans + " hosts have been posted to the ledger.";
             System.out.println(prompt);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
}