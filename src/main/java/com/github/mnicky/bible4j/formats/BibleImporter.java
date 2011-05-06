package com.github.mnicky.bible4j.formats;

import java.io.InputStream;

import com.github.mnicky.bible4j.storage.BibleStorageException;

public interface BibleImporter {

    void importBible(InputStream input) throws BibleImporterException, BibleStorageException;

}
