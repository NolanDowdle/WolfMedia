package main.java.wolf_media.operation.common;

/**
 * Class for operation selection data
 * @author John Fagan
 *
 */
public class OperationEntry {
    
    // The option number to display
    private int opNum;
    // The operation description to display
    private String opString;
    // The operation implementation
    private IOperation operation;
    
    // operation getter
    public IOperation getOperation() {
        return operation;
    }

    // Constructor
    public OperationEntry(int opNum, String opString, IOperation operation) {
        this.opNum = opNum;
        this.opString = opString;
        this.operation = operation;
    }

    // opNum getter
    public int getOpNum() {
        return opNum;
    }

    // opString getter
    public String getOpString() {
        return opString;
    }

}
