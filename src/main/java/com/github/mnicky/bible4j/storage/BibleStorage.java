package com.github.mnicky.bible4j.storage;

import java.util.List;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Bookmark;
import com.github.mnicky.bible4j.data.Note;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;

/**
 * Interface representing a storage for Bibles.
 */
public interface BibleStorage {

    /**
     * Close this BibleStorage.
     * 
     * @throws BibleStorageException when BibleStorage could not be closed
     */
    void close();

    public boolean isStorageInitialized();
    
    //TODO: change createStorage() to return void
    int[] initializeStorage();

    void insertVerse(Verse verse);

    void insertBibleVersion(BibleVersion version);

    void insertPosition(Position position);

    void insertBibleBook(BibleBook book);

    BibleVersion getBibleVersion(String abbr);
    
    List<BibleVersion> getAllBibleVersions();

    // merge these four functionalities into the last one (or the first one)?
    Verse getVerse(Position position, BibleVersion version);

    List<Verse> getVerses(List<Position> position, BibleVersion version);

    //TODO replace fake chapter (which is a Position in fact) with real Chapter object
    
    //chapter is represented by Position object with ignored verse number information
    List<Verse> getChapter(Position chapter, BibleVersion version);
    
    List<Position> getChapterList(BibleVersion version);

    List<Verse> compareVerses(Position position, List<BibleVersion> versions);

    List<Verse> compareVerses(List<Position> positions, List<BibleVersion> versions);

    void insertNote(Note note);

    int deleteNote(Position position);
    
    List<Note> getNotes(Position position);
    
    List<Note> getNotesForChapter(Position chapter);

    void insertBookmark(Bookmark bookmark);
    
    int deleteBookmark(String bookmarkName);

    List<Bookmark> getBookmarks();

    List<Bookmark> getBookmarks(BibleVersion version);
    
    List<Verse> searchVersesForText(String text);
    
    List<Verse> searchVersesForText(String text, BibleVersion version);
    
    List<Verse> searchVersesForText(String text, BibleBook book);
    
    List<Verse> searchVersesForText(String text, BibleBook book, BibleVersion version);

}
