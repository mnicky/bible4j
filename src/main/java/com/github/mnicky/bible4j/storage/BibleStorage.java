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

    void createStorage() throws BibleStorageException;

    //void insertVerse(Verse verse);
    //void insertBibleVersion(BibleVersion version);
    //void insertPosition(Position position);
    //void insertBibleBook(BibleBook book);

    //Verse getVerse(Position position);
    //Verse getVerse(Position position, BibleVersion version);

    //List<Verse> getVerses(List<Position> position, BibleVersion version);

    //List<Verse> compareVerses(Position position);
    //List<Verse> compareVerses(Position position, List<BibleVersion> version);

}
