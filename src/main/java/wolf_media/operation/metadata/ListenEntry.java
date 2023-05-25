package main.java.wolf_media.operation.metadata;

import java.sql.Timestamp;

/**
 * Struct-like class for storing a Listen table tuple
 * 
 * @author John Fagan
 */
public class ListenEntry {
    
    // Listen entry play time
    public Timestamp playTime;
    // Song/Podcast episode ID
    public int audioId;
    // User who listened
    public int userId;
    
    public ListenEntry(Timestamp playTime, int audioId, int userId) {
        this.playTime = playTime;
        this.audioId = audioId;
        this.userId = userId;
    }
}
