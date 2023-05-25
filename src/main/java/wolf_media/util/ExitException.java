package main.java.wolf_media.util;

public final class ExitException extends Exception {

    // used for serialization
    private static final long serialVersionUID = -7241957197293398639L;

    // Constructor
    public ExitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    // Constructor
    public ExitException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor
    public ExitException(String message) {
        super(message);
    }

    // Constructor
    public ExitException(Throwable cause) {
        super(cause);
    }


}
