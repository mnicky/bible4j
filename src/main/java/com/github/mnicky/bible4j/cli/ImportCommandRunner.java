package com.github.mnicky.bible4j.cli;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mnicky.bible4j.AppRunner;
import com.github.mnicky.bible4j.parsers.BibleImporter;
import com.github.mnicky.bible4j.parsers.BibleImporterException;
import com.github.mnicky.bible4j.parsers.OsisBibleImporter;
import com.github.mnicky.bible4j.storage.BibleStorage;

/**
 *  This class invokes and controls the application functionality of importing the Bible.
 */
class ImportCommandRunner extends CommandRunner {
    
    private final static Logger logger = LoggerFactory.getLogger(AppRunner.AppLogger.class);

    InputStream input;
    
    public ImportCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    void doRequestedAction() {
        importBible();        
    }

    @Override
    void parseCommandLine(String[] args) {
	try {
	    input = parseInputStream(args);
	} catch (FileNotFoundException e) {
	    logger.error("Exception caught when parsing the filename - probably the file not found. Provided arguments: {}", args);
	    throw new BibleImporterException("Specified file not found", e);
	}
    }
    
    public void importBible() {
	BibleImporter importer = new OsisBibleImporter(bibleStorage);
	System.out.println("Importing the Bible...");
	importer.importBible(input);
	System.out.println("Bible imported.");
    }

    private InputStream parseInputStream(String[] args) throws FileNotFoundException {
	BufferedInputStream in = new BufferedInputStream(new FileInputStream(getFirstValue(args)));
	return in;
    }

    @Override
    public void printHelp() {
	System.out.println();
	System.out.println("Usage:");
        System.out.println("\t" + CommandParser.IMPORT_COMMAND + " PATH_TO_FILE");
        
        System.out.println();
        System.out.println("\tPATH_TO_FILE \t path to OSIS file containing the Bible");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("\t" + CommandParser.IMPORT_COMMAND + " kjv-osis-bible.xml");
        System.out.println();
    }

}
