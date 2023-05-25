package main.java.wolf_media.operation.common;

import java.sql.Connection;

public interface IOperation {
    /**
     * Interface function for executing an operation on a database connection.
     * 
     * @param conn An active connection to a database.
     * @return True if the operation executed successfully. False otherwise.
     */
    public boolean execute(Connection conn);
}
