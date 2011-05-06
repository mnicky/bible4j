package com.github.mnicky.bible4j.formats;

import java.io.IOException;

import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public interface DictionaryImporter {
    
    String getTitle();
    
    void setStorage(BibleStorage storage);
    
    void downloadDictionary() throws BibleStorageException, IOException;

}
