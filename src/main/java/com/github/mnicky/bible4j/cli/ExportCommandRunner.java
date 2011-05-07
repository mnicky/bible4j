package com.github.mnicky.bible4j.cli;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.parsers.BibleExporter;
import com.github.mnicky.bible4j.parsers.BibleExporterException;
import com.github.mnicky.bible4j.parsers.OsisBibleExporter;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public class ExportCommandRunner extends CommandRunner {
    
    OutputStream output;
    private BibleVersion version;

    public ExportCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    void parseCommandLine(String[] args) throws BibleExporterException, BibleStorageException {
	    try {
		output = parseOutputStream(args);
	    } catch (FileNotFoundException e) {
		throw new BibleExporterException("File not found.", e);
	    }
	    version = parseVersionsAndReturnFirstIfEmpty(args).get(0);
    }
    
    @Override
    void doAction() throws BibleStorageException, BibleExporterException {
        exportBible();
        //display
    }

    private void exportBible() throws BibleStorageException, BibleExporterException {
	BibleExporter exporter = new OsisBibleExporter(bibleStorage);
	exporter.exportBible(version, output);
    }

    private OutputStream parseOutputStream(String[] args) throws FileNotFoundException {
	FileOutputStream out = new FileOutputStream(getFirstValue(args));
	return out;
    }

    @Override
    public void printHelp() {
	System.out.println("Usage:");
        System.out.println("\t" + CommandParser.EXPORT_COMMAND + " PATH_TO_FILE [" + BIBLE_VERSION_ARGUMENT + " BIBLE_VERSION]");
        
        System.out.println();
        System.out.println("\tPATH_TO_FILE \t path to OSIS file containing the Bible");
        System.out.println("\tBIBLE_VERSION \t Bible version abbreviation (if no bible version is specified, the first Bible found is used)");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("\t" + CommandParser.EXPORT_COMMAND + " kjv-osis-bible.xml " + BIBLE_VERSION_ARGUMENT + " kjv");
	
    }

}