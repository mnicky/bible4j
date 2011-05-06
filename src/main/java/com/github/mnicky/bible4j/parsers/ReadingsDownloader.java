package com.github.mnicky.bible4j.parsers;

import java.io.IOException;
import java.net.MalformedURLException;

import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public interface ReadingsDownloader {
    
    String getTitle();
    
    void setStorage(BibleStorage storage);
    
    void downloadDictionary(int nextMonths) throws MalformedURLException, IOException, BibleStorageException;

}
