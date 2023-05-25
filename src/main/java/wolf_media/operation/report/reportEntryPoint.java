package main.java.wolf_media.operation.report;

import main.java.wolf_media.operation.common.OperationEntry;
import main.java.wolf_media.operation.common.OperationEntryPointBase;

/**
 * Selection Menu for Reports
 * 
 * @author KR
 *
 */
public class reportEntryPoint extends OperationEntryPointBase {

    // Helper var to easily increment operation number for each unique operation
    private static int opNum = 1;
    // Array of operations this entrypoint performs
    private static OperationEntry[] OPERATIONS = {
            new OperationEntry(opNum++, "Generate Revenue per given monthly time period report", new GenerateRevenueReportPerMonth()),
            new OperationEntry(opNum++, "Generate Revenue per given yearly time period report", new GenerateRevenueReportPerYear()),
            new OperationEntry(opNum++, "Generate Podcast Host payouts in a given time period report", new GeneratePodcastHostPayouts()),
            new OperationEntry(opNum++, "Generate Artist payouts in a given time period report", new GenerateArtistPayouts()),
            new OperationEntry(opNum++, "Generate Record Label payout in a given time period report", new GenerateRecordLabelPayouts()),
            new OperationEntry(opNum++, "Generate Artist play count report", new GenerateArtistListenCount()),
            new OperationEntry(opNum++, "Generate Song play count report", new GenerateSongListenCount()),
            new OperationEntry(opNum++, "Generate Album play Count report", new GenerateAlbumListenCount()),
            new OperationEntry(opNum++, "Get Songs per Artist report", new GetSongsPerArtist()),
            new OperationEntry(opNum++, "Get Podcast Episodes per Podacst report", new GetPodcastEpisodesPerPodcast()),
            new OperationEntry(opNum++, "Get Songs per Album", new GetSongsPerAlbum())
    };

    public reportEntryPoint() {
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
