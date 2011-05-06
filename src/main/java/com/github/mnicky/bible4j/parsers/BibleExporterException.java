package com.github.mnicky.bible4j.parsers;

/**
 * Exception thrown when error occurs in BibleExporter.
 */
public class BibleExporterException extends Exception {

    /**
     * UID used in serialization.
     */
    private static final long serialVersionUID = 4612683872748299515L;

    /**
     * Constructs new BibleExporterException.
     */
    public BibleExporterException() {
	super();
    }
    
    /**
     * Constructs new BibleExporterException with specified detail message.
     * @param message the detail message
     */
    public BibleExporterException(String message) {
	super(message);
    }
    
    /**
     * Constructs new BibleExporterException, wrapping another Exception.
     * @param e Exception to wrap
     */
    public BibleExporterException(Exception e) {
	super(e);
    }
    
    /**
     * Constructs new BibleExporterException with specified detail message, wrapping another Exception.
     * @param message the detail message
     * @param e Exception to wrap
     */
    public BibleExporterException(String message, Exception e) {
	super(message, e);
    }

}
