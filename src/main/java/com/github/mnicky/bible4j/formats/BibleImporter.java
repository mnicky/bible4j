package com.github.mnicky.bible4j.formats;

import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import com.github.mnicky.bible4j.storage.BibleStorageException;

public interface BibleImporter {
    
    //TODO change exceptions to context level ones
    void importBible(InputStream input) throws XMLStreamException, FactoryConfigurationError, BibleStorageException;

}
