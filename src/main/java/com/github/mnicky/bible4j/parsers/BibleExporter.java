package com.github.mnicky.bible4j.parsers;

import java.io.OutputStream;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.storage.BibleStorage;

/**
 * Interface for classes providing exporting Bible to various formats.
 */
public interface BibleExporter {
    
    void setStorage(BibleStorage storage);

    void exportBible(BibleVersion bible, OutputStream stream);

}
