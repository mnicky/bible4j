package com.github.mnicky.bible4j.formats;

import java.io.OutputStream;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.storage.BibleStorage;

public interface BibleExporter {
    
    void export(BibleVersion bible, BibleStorage storage, OutputStream stream) throws BibleExporterException;

}
