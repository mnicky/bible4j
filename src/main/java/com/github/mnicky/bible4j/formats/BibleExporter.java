package com.github.mnicky.bible4j.formats;

import java.io.OutputStream;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public interface BibleExporter {

    void exportBible(BibleVersion bible, OutputStream stream) throws BibleExporterException, BibleStorageException;

}
