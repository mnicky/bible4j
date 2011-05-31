package com.github.mnicky.bible4j.parsers;

import java.io.InputStream;

import com.github.mnicky.bible4j.storage.BibleStorage;

/**
 * Interface for classes providing the import of Bible from various formats.
 */
public interface BibleImporter {
    
    void setStorage(BibleStorage storage);

    void importBible(InputStream input);

}
