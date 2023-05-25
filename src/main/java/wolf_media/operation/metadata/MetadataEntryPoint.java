package main.java.wolf_media.operation.metadata;

import main.java.wolf_media.operation.common.OperationEntry;
import main.java.wolf_media.operation.common.OperationEntryPointBase;

/**
 * Selection Menu for metadata operations
 * 
 * @author John Fagan
 *
 */
public class MetadataEntryPoint extends OperationEntryPointBase {

    // Helper var to easily increment operation number for each unique operation
    private static int opNum = 1;
    // Array of operations this entrypoint performs
    private static OperationEntry[] OPERATIONS = {
            new OperationEntry(opNum++, "Set Song Play Counts", new SetSongPlayCount()),
            new OperationEntry(opNum++, "Set Artist's Monthly Listeners", new SetArtistMonthlyListeners2()),
            new OperationEntry(opNum++, "Set Podcast Subscribers", new SetPodcastSubscribers()),
            new OperationEntry(opNum++, "Set Podcast Rating", new SetPodcastRating()),
            new OperationEntry(opNum++, "Set Podcast Episode Listening Count", new SetPodcastListeningCount()),
            new OperationEntry(opNum++, "Get Podcast Episodes for Podcast", new GetPodcastEpisodeForPodcast()),
            new OperationEntry(opNum++, "Get Songs for Album", new GetSongsForAlbum()),
            new OperationEntry(opNum++, "Get Songs for Artist", new GetSongsForArtist())
    };

    public MetadataEntryPoint() {
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
