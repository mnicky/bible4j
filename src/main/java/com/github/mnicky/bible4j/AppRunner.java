package com.github.mnicky.bible4j;

import java.io.IOException;

import com.github.mnicky.bible4j.cli.CommandParser;
import com.github.mnicky.bible4j.parsers.BibleExporterException;
import com.github.mnicky.bible4j.parsers.BibleImporterException;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorageFactory;

public final class AppRunner {
    
    public static void main(String[] args) throws BibleStorageException, BibleImporterException, BibleExporterException, IOException {
	CommandParser cp = new CommandParser(new H2DbBibleStorageFactory());
	cp.launch(args);
    }
    
    //used as Logger for SLF4J
    public static class Logger {}

}
