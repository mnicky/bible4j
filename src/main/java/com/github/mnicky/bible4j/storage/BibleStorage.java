package com.github.mnicky.bible4j.storage;

import hirondelle.date4j.DateTime;

import java.util.List;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Bookmark;
import com.github.mnicky.bible4j.data.DailyReading;
import com.github.mnicky.bible4j.data.DictTerm;
import com.github.mnicky.bible4j.data.Note;
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

    BibleVersion getBibleVersion(String abbr) throws BibleStorageException;

    // merge these four functionalities into the last one (or the first one)?
    Verse getVerse(Position position, BibleVersion version) throws BibleStorageException;

    List<Verse> getVerses(List<Position> position, BibleVersion version) throws BibleStorageException;

    //TODO replace fake chapter (which is a Position in fact) with real Chapter object
    
    //chapter is represented by Position object with ignored verse number information
    List<Verse> getChapter(Position chapter, BibleVersion version) throws BibleStorageException;
    
    List<Position> getChapterList(BibleVersion version) throws BibleStorageException;

    List<Verse> compareVerses(Position position, List<BibleVersion> versions) throws BibleStorageException;

    List<Verse> compareVerses(List<Position> positions, List<BibleVersion> versions)
	    throws BibleStorageException;

    void insertNote(Note note) throws BibleStorageException;

    List<Note> getNotes(Position position) throws BibleStorageException;

    void insertBookmark(Bookmark bookmark) throws BibleStorageException;

    List<Bookmark> getBookmarks() throws BibleStorageException;

    List<Bookmark> getBookmarks(BibleVersion version) throws BibleStorageException;
    
    void insertDictTerm(DictTerm term) throws BibleStorageException;
    
    DictTerm getDictTerm(String name) throws BibleStorageException;
    
    void insertReadingList(String name) throws BibleStorageException;
    
    void insertDailyReading(DailyReading reading) throws BibleStorageException;
    
    List<DailyReading> getDailyReadings(DateTime date) throws BibleStorageException;
    
    List<Verse> searchVersesForText(String text) throws BibleStorageException;

}
