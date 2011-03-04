package com.github.mnicky.bible4j.storage;

/**
 * Interface representing storage for Bibles.
 */
public interface BibleStorage {

    /**
     * Close this BibleStorage.
     * @throws BibleStorageException when BibleStorage could not be closed
     */
    void close() throws BibleStorageException;
    
}
