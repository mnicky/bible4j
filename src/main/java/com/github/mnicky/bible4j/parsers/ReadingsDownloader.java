package com.github.mnicky.bible4j.parsers;

import java.io.IOException;

import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public interface ReadingsDownloader {
    
    String getTitle();
    
    void setStorage(BibleStorage storage);
    
    void downloadReadings(int nextMonths) throws IOException, BibleStorageException;

}
