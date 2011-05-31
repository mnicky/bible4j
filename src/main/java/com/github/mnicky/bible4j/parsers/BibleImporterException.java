package com.github.mnicky.bible4j.parsers;

/**
 * Unchecked exception, thrown when error occurs in BibleImporter.
 */
public class BibleImporterException extends RuntimeException {

    /**
     * UID used in serialization.
     */
    private static final long serialVersionUID = 5662564677656350010L;

    /**
     * Constructs new BibleImporterException.
     */
    public BibleImporterException() {
	super();
    }
    
    /**
     * Constructs new BibleImporterException with specified detail message.
     * @param message the detail message
     */
    public BibleImporterException(String message) {
	super(message);
    }
    
    /**
     * Constructs new BibleImporterException, wrapping another Exception.
     * @param e Exception to wrap
     */
    public BibleImporterException(Exception e) {
	super(e);
    }
    
    /**
     * Constructs new BibleImporterException with specified detail message, wrapping another Exception.
     * @param message the detail message
     * @param e Exception to wrap
     */
    public BibleImporterException(String message, Exception e) {
	super(message, e);
    }

}
