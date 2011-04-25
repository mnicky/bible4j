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

    //TODO: change createStorage() to return void
    int[] createStorage() throws BibleStorageException;

    void insertVerse(Verse verse) throws BibleStorageException;

    void insertBibleVersion(BibleVersion version) throws BibleStorageException;

    void insertPosition(Position position) throws BibleStorageException;

    void insertBibleBook(BibleBook book) throws BibleStorageException;

    // merge these four functionalities into the last one (or the first one)?
    Verse getVerse(Position position, BibleVersion version) throws BibleStorageException;

    List<Verse> getVerses(List<Position> position, BibleVersion version) throws BibleStorageException;

    List<Verse> compareVerses(Position position, List<BibleVersion> versions) throws BibleStorageException;

    List<Verse> compareVerses(List<Position> positions, List<BibleVersion> versions)
	    throws BibleStorageException;

    // List<Verse> compareVersesOptimalized(Position position, List<BibleVersion> versions) throws
    // BibleStorageException;

    // void insertNote(Note note);

    // List<Note> getNotes(Position position);

    // void insertBookmark(Bookmark bookmark);

    // List<Bookmark> getBookmarks();

    // List<Bookmark> getBookmarks(BibleVersion version); ???

}
