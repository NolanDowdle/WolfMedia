package main.java.wolf_media.operation.common;

import java.sql.Connection;
import java.sql.SQLException;

import main.java.wolf_media.util.ExitException;

public abstract class OperationBase implements IOperation {

    /**
     * Base function that handles committing or rolling back a transaction.
     * 
     * @param conn Database connection.
     * @return     Returns true if the transaction was successful, false otherwise.
     */
    public boolean execute(Connection conn) {
        if (conn == null) {
            // Connection is null, nothing to do and execute fails
            return false;
        }
        try {
            // Execute the query 
            this.executeImpl(conn);
            // If no err then commit the transaction 
            conn.commit();
            // Transaction successful hence let the end user know
            System.out.println("Transaction successful!");
            return true;
        } catch (SQLException e) {
            try {
                // SQLException throw hence rollback to a stable state
                conn.rollback();
            } catch (SQLException ignore) {}
            // Transaction unsuccessful hence let the end user know
            System.out.println("SQLException Occured!");
            // Catch all printStackTrace
            e.printStackTrace();
        } catch (ExitException e) {
            try {
                conn.rollback();
            } catch (SQLException ignore) {}
            System.out.println("" + e.getMessage() + " - Rollback transaction");
        }
        return false;
    }
    
    /**
     * Executes an operation that is a part of a large operation.
     * This method does not handle committing or reverting a transaction
     * @param conn Database connection
     * @throws SQLException Raised when an SQL or database connection error occurs
     * @throws ExitException Raised when the user wants the exit the operation without completing it
     */
    public void executePartial(Connection conn) throws SQLException, ExitException {
        if (conn == null) {
            // Connection is null, nothing to do and execute fails
            return;
        }
        // Execute the query 
        this.executeImpl(conn);
    }

    /**
     * Operation implementation
     * @param conn Database connection
     * @throws SQLException Raised when an SQL or database connection error occurs
     * @throws ExitException Raised when the user wants the exit the operation without completing it
     */
    protected abstract void executeImpl(Connection conn) throws SQLException, ExitException;
}
