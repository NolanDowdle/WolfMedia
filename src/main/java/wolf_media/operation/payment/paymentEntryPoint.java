package main.java.wolf_media.operation.payment;

import main.java.wolf_media.operation.common.OperationEntry;
import main.java.wolf_media.operation.common.OperationEntryPointBase;

/**
 * Selection Menu for Payment Processing
 * 
 * @author KR
 *
 */
public class paymentEntryPoint extends OperationEntryPointBase {

    // Helper var to easily increment operation number for each unique operation
    private static int opNum = 1;
    // Array of operations this entrypoint performs
    private static OperationEntry[] OPERATIONS = {
            new OperationEntry(opNum++, "Get User Payment Information", new paymentInfo()),
            new OperationEntry(opNum++, "Calculate royalties for a given song", new songRoyalty()),
            new OperationEntry(opNum++, "Post Royalties for Record Labels", new moLabelRoyalties()),
            new OperationEntry(opNum++, "Post Royalties for Artists", new moArtistRoyalties()),
            new OperationEntry(opNum++, "Post Pay for Podcast Host", new moPodcastPayment()),
            new OperationEntry(opNum++, "Post Subscription Fee to Ledger", new moSubBilling())
    };

    public paymentEntryPoint() {
        // Initialize base/super class (OperationEntryPointBase)
        super();
    }

    /**
     * Returns an array of operation entries so the super class can construct a selection many from them
     */
    @Override
    protected OperationEntry[] getOperations() {
        // Return operations array so the superclass can use them
        return OPERATIONS;
    }

}
