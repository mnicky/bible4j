package com.github.mnicky.bible4j.cli;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.github.mnicky.bible4j.data.DictTerm;
import com.github.mnicky.bible4j.parsers.DictionaryDownloader;
import com.github.mnicky.bible4j.parsers.EastonsDictionaryDownloader;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

class DictionaryCommandRunner extends CommandRunner {
    
    private DictTerm dictTerm = null;
    boolean downloading = false;

    public DictionaryCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    void parseCommandLine(String[] args) throws BibleStorageException, IOException {
	if (isArgument(args[0]) && args[0].equalsIgnoreCase(DOWNLOAD_ARGUMENT)) {
	    downloading = true;
	    downloadDictionary();
	}
	else
	    dictTerm = parseDictTerm(getFirstValue(args));
    }
    
    @Override
    void doAction() {
	if (downloading)
	    return;
        displayDictTerm();
    }

    private void displayDictTerm() {
	if (dictTerm != null)
	    System.out.println(dictTerm);
	else if (!downloading)
	    System.out.println("Term not found.");
    }

    private void downloadDictionary() throws BibleStorageException, IOException {
	DictionaryDownloader dictdown = new EastonsDictionaryDownloader(bibleStorage);
	System.out.println("Downloading " + dictdown.getTitle() + "...");
	dictdown.downloadDictionary();
	System.out.println("Dictionary downloaded.");
    }

    private DictTerm parseDictTerm(String name) throws BibleStorageException {
	return bibleStorage.getDictTerm(name);
    }

    @Override
    public void printHelp() {
	System.out.println("Usage:");
        System.out.println("\t" + CommandParser.DICTIONARY_COMMAND + " TERM_NAME");
        System.out.println("\t" + CommandParser.DICTIONARY_COMMAND + " " + DOWNLOAD_ARGUMENT);
        
        System.out.println();
        System.out.println("\tTERM_NAME \t Name of the term to look up in the dictionary (case insensitive)");
        
        System.out.println();
        System.out.println("\tTo download the dictionary, use argument '" + DOWNLOAD_ARGUMENT +"'");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  Lookup a word in the Bible dictionary:");
        System.out.println();
        System.out.println("\t" + CommandParser.DICTIONARY_COMMAND + " Israel");
        
        System.out.println();
        System.out.println("  Download the Bible dictionary:");
        System.out.println();
        System.out.println("\t" + CommandParser.DICTIONARY_COMMAND + " " + DOWNLOAD_ARGUMENT);
	
    }
    
    
    public static void main(String[] args) throws SQLException, BibleStorageException, IOException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	DictionaryCommandRunner p = new DictionaryCommandRunner(storage);
	//storage.insertDictTerm(new DictTerm("Jehovah", "Hebrew 'name' for God, meaning 'I am'"));
	//String[] params = {"aaron"};
	String[] params = {"-down"};
	p.parseCommandLine(params);
	p.doAction();
    }

}
