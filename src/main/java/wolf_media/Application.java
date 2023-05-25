package main.java.wolf_media;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import main.java.wolf_media.manager.DBManager;
import main.java.wolf_media.operation.common.IOperation;
import main.java.wolf_media.operation.report.*;
import main.java.wolf_media.util.InputUtil;
import main.java.wolf_media.operation.processing.*;
import main.java.wolf_media.operation.payment.*;

import main.java.wolf_media.operation.metadata.MetadataEntryPoint;

public class Application {
    
	// methods for info processing
	private static final IOperation infoProcessingSelection    = new infoProcessingEntryPoint(); 
	// methods for metadata
    private static final IOperation metadataSelection          = new MetadataEntryPoint();
    // methods for maintaining payments
    private static final IOperation paymentSelection           = new paymentEntryPoint();
    // methods for reports
    private static final IOperation reportSelection            = new reportEntryPoint();

	
    /**
     * Entry point for the application.
     * 
     * @param arg Application parameters.
     * @throws ClassNotFoundException 
     * @throws SQLException 
     */
    public static void main(final String[] arg) throws ClassNotFoundException, SQLException {
        Scanner    in        = new Scanner(System.in);
        DBManager  dbManager = new DBManager();
        Connection conn      = null;
        try {
            // Based on the tester, the database connection is set-up differently (e.g., tunnel vs VPN)
            //   It does not impact the functionality of our project at all
            String testName = InputUtil.getString("Who is testing?");
            //if (testName.equals()) {
                //
            //} else {
                //
            //}
            String askForMoreInput = "N";
            do {
                // Ask which operation the user would like to perform
                System.out.println("Choose an operation type:\n" + "1 for Information Processing\n"
                        + "2 for Maintaining Metadata and Records\n" + "3 for Maintaining Payments\n"
                        + "4 for Reports\n");
                String operationType = in.nextLine();
                if (Integer.parseInt(operationType) == 1) {
                    infoProcessingSelection.execute(conn);
                } else if (Integer.parseInt(operationType) == 2) {
                    metadataSelection.execute(conn);
                } else if (Integer.parseInt(operationType) == 3) {
                    paymentSelection.execute(conn);
                } else if (Integer.parseInt(operationType) == 4) {
                    reportSelection.execute(conn);
                } else {
                    throw new IllegalArgumentException("Value must be an integer between 1 and 4");
                }
                System.out.println("Would you like to perform another operation? (Y/N):\n");
                askForMoreInput = in.nextLine();
            } while (askForMoreInput.equals("Y") || askForMoreInput.equals("y"));
        } finally {
            if (conn != null) {
                // Always close connection
                conn.close();
            }
        }
        System.out.println("Exiting");
    }
}
