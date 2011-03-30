package com.github.mnicky.bible4j.storage;

import java.util.List;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;

/**
 * Interface representing storage for Bibles.
 */
public interface BibleStorage {

    /**
     * Close this BibleStorage.
     * 
     * @throws BibleStorageException
     *             when BibleStorage could not be closed
     */
    void close() throws BibleStorageException;

    int[] createStorage() throws BibleStorageException;

    void insertVerse(Verse verse) throws BibleStorageException;

    void insertBibleVersion(BibleVersion version) throws BibleStorageException;

    void insertPosition(Position position) throws BibleStorageException;

    void insertBibleBook(BibleBook book) throws BibleStorageException;

    Verse getVerse(Position position, BibleVersion version) throws BibleStorageException;

    List<Verse> getVerses(List<Position> position, BibleVersion version) throws BibleStorageException;

    // List<Verse> compareVerses(Position position, List<BibleVersion> version);

    // List<Verse> compareVerses(List<Position> , List<BibleVersion> version);

}
