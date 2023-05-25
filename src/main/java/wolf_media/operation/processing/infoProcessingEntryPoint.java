package main.java.wolf_media.operation.processing;

import main.java.wolf_media.operation.atom.*;
import main.java.wolf_media.operation.common.OperationEntry;
import main.java.wolf_media.operation.common.OperationEntryPointBase;

/**
 * Selection Menu for Information processing
 * 
 * @author KR
 *
 */
public class infoProcessingEntryPoint extends OperationEntryPointBase {

    // Helper var to easily increment operation number for each unique operation
    private static int opNum = 1;
    // Array of operations this entrypoint performs
    private static OperationEntry[] OPERATIONS = {
            new OperationEntry(opNum++, "Enter/Insert Song", new insertSong()),
            new OperationEntry(opNum++, "Update Song", new updateSong()),
            new OperationEntry(opNum++, "Delete Song", new deleteSong()),
            new OperationEntry(opNum++, "Enter/Insert Artist", new insertArtist()),
            new OperationEntry(opNum++, "Update Artist", new updateArtist()),
            new OperationEntry(opNum++, "Delete Artist", new deleteArtist()),
            new OperationEntry(opNum++, "Enter/Insert Podcast Host", new insertPodcastHost()),
            new OperationEntry(opNum++, "Update Podcast Host", new updatePodcastHost()),
            new OperationEntry(opNum++, "Delete Podcast Host", new deletePodcastHost()),
            new OperationEntry(opNum++, "Enter/Insert Podcast Episode", new insertPodcastEpisode()),
            new OperationEntry(opNum++, "Update Podcast Episode", new updatePodcastEpisode()),
            new OperationEntry(opNum++, "Delete Podcast Podcast Episode", new deletePodcastEpisode()),
            new OperationEntry(opNum++, "Assign songs to album", new assignSongToAlbum()),
            new OperationEntry(opNum++, "Assign artist to album", new assignArtistToAlbum()),
            new OperationEntry(opNum++, "Assign artist to record label", new assignArtistToRecordLabels()),
            new OperationEntry(opNum++, "Assign podcast epsiode to podcast", new assignPodcastEpisodeToPodcast()),
            new OperationEntry(opNum++, "Assign podcast hosts to podcast", new assignPodcastHostToPodcast())
    };

    public infoProcessingEntryPoint() {
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
