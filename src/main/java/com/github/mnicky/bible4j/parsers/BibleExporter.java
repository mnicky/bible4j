package com.github.mnicky.bible4j.parsers;

import java.io.OutputStream;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public interface BibleExporter {
    
    void setStorage(BibleStorage storage);

    void exportBible(BibleVersion bible, OutputStream stream) throws BibleExporterException, BibleStorageException;

}
