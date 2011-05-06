package com.github.mnicky.bible4j.formats;

import java.io.InputStream;

import com.github.mnicky.bible4j.storage.BibleStorageException;

public interface DictionaryImporter {
    
    void importDictionary(InputStream input) throws BibleImporterException, BibleStorageException;

}
