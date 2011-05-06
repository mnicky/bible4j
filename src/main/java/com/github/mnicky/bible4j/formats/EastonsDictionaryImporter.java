package com.github.mnicky.bible4j.formats;

import java.io.InputStream;

import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public class EastonsDictionaryImporter implements DictionaryImporter {
    
    private BibleStorage storage;
    
    public EastonsDictionaryImporter(BibleStorage storage) {
	this.storage = storage;
    }

    public void setStorage(BibleStorage storage) {
	this.storage = storage;
    }

    @Override
    public void importDictionary(InputStream input) throws BibleImporterException, BibleStorageException {
	

    }

}
