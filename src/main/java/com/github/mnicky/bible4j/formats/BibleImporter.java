package com.github.mnicky.bible4j.formats;

import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public interface BibleImporter {

    void importBible(InputStream input) throws BibleImporterException, BibleStorageException;

}
