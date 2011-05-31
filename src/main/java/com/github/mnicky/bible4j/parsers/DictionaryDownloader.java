package com.github.mnicky.bible4j.parsers;

import java.io.IOException;

import com.github.mnicky.bible4j.storage.BibleStorage;

/**
 * Interface for classes providing the download of various dictionaries. 
 */
public interface DictionaryDownloader {
    
    String getTitle();
    
    void setStorage(BibleStorage storage);
    
    void downloadDictionary() throws IOException;

}
