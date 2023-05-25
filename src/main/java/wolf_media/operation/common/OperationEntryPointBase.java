package main.java.wolf_media.operation.common;

import java.sql.Connection;
import java.util.HashMap;

import main.java.wolf_media.util.InputUtil;

/**
 * Base class to implement common operation selection
 * 
 * @author John Fagan
 */
public abstract class OperationEntryPointBase implements IOperation {

    /**
     * @return Returns an array of operation entries for the user to select from
     */
    protected abstract OperationEntry[] getOperations();

    /**
     * Maps operation selection number to operation entry
     */
    private HashMap<Integer, OperationEntry> entryMap;
    
    /**
     * Constructor
     * Creates a map from operation number and operation entry
     */
    public OperationEntryPointBase() {
        // Get operations
        OperationEntry[] operations = this.getOperations();
        // Build map from integer operation number to operation entry
        this.entryMap = new HashMap<Integer, OperationEntry>(operations.length);
        for (int i = 0; i < operations.length; i++) {
            OperationEntry entry = operations[i];
            // Check for duplicate operation numbers
            if (entryMap.containsKey(entry.getOpNum())) {
                System.out.println("WARN: duplicate operation number: " + entry.getOpNum());
            }
            entryMap.put(entry.getOpNum(), entry);
        }
    }

    /**
     * Shows a menu to select a metadata operation from
     * @param conn Database connection
     * @return Returns true if selected operation completes successfully or "exit" is selected,
     * false otherwise.
     */
    @Override
    public boolean execute(Connection conn) {
        // Get operations
        OperationEntry[] operations = this.getOperations();
        // Loop forever to handle invalid inputs
        while (true) {
            // Print selection menu
            for (int i = 0; i < operations.length; i++) {
                OperationEntry entry = operations[i];
                String formatted = String.format("%02d: %s", entry.getOpNum(), entry.getOpString());
                System.out.println(formatted);
            }
            System.out.println("Input empty string to exit this selection");
            // Prompt user to select an operation or exit
            Integer select = InputUtil.getIntOrNull("Select an operation");
            // Check for exit selection
            if (select == null) {
                // Exit selected, execute is vacuously true
                return true;
            }
            // Check if valid selection
            if (entryMap.containsKey(select)) {
                // Execute selected operation
                IOperation op = entryMap.get(select).getOperation();
                return op.execute(conn);
            } else {
                // Invalid selection, loop re-prompts user for input
                System.out.println("Invalid operation selection. Please select a valid operation.");
            }
        }
    }

}
