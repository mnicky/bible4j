package com.github.mnicky.bible4j.storage;

/**
 * Exception thrown when error occurs in BibleStorage.
 */
public class BibleStorageException extends RuntimeException {

    /**
     * UID used in serialization.
     */
    private static final long serialVersionUID = 2615867084744509978L;

    /**
     * Constructs new BibleStorageException.
     */
    public BibleStorageException() {
	super();
    }
    
    /**
     * Constructs new BibleStorageException with specified detail message.
     * @param message the detail message
     */
    public BibleStorageException(String message) {
	super(message);
    }
    
    /**
     * Constructs new BibleStorageException, wrapping another Exception.
     * @param e Exception to wrap
     */
    public BibleStorageException(Exception e) {
	super(e);
    }
    
    /**
     * Constructs new BibleStorageException with specified detail message, wrapping another Exception.
     * @param message the detail message
     * @param e Exception to wrap
     */
    public BibleStorageException(String message, Exception e) {
	super(message, e);
    }

}
