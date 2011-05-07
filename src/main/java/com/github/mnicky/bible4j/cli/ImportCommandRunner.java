package com.github.mnicky.bible4j.cli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.github.mnicky.bible4j.parsers.BibleImporter;
import com.github.mnicky.bible4j.parsers.BibleImporterException;
import com.github.mnicky.bible4j.parsers.OsisBibleImporter;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public class ImportCommandRunner extends CommandRunner {

    InputStream input;
    
    public ImportCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    void doAction() throws BibleImporterException, BibleStorageException {
        importBible();        
    }

    @Override
    void parseCommandLine(String[] args) throws BibleImporterException {
	try {
	    input = parseInputStream(args);
	} catch (FileNotFoundException e) {
	    throw new BibleImporterException("Specified file not found", e);
	}

    }
    
    public void importBible() throws BibleImporterException, BibleStorageException {
	BibleImporter importer = new OsisBibleImporter(bibleStorage);
	System.out.println("Importing the Bible...");
	importer.importBible(input);
	System.out.println("Bible imported.");
    }

    private InputStream parseInputStream(String[] args) throws FileNotFoundException {
	FileInputStream in = new FileInputStream(getFirstValue(args));
	return in;
    }

    @Override
    public void printHelp() {
	System.out.println("Usage:");
        System.out.println("\t" + CommandParser.IMPORT_COMMAND + " PATH_TO_FILE");
        
        System.out.println();
        System.out.println("\tPATH_TO_FILE \t path to OSIS file containing the Bible");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("\t" + CommandParser.IMPORT_COMMAND + " kjv-osis-bible.xml");
	
    }

}
