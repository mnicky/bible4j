package com.github.mnicky.bible4j.cli;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public final class InfoCommandRunner extends CommandRunner {
    
    private List<BibleVersion> versions;

    public InfoCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    void parseCommandLine(String[] args) throws BibleStorageException {
	versions = getVersions();
    }

    @Override
    void doAction() {
	//info about program
        printVersionsInfo();
        
    }

    private List<BibleVersion> getVersions() throws BibleStorageException {
	return bibleStorage.getAllBibleVersions();
    }

    private void printVersionsInfo() {
	System.out.println("Available Bible versions:");
	System.out.println();
	System.out.println("Abbreviation \t Language \t Name");
	System.out.println("-------------------------------------------------------------------");
	for (BibleVersion version : versions)
	    System.out.println(version.getAbbr() + " \t\t " + version.getLanguage() + " \t\t " + version.getName());
    }

    @Override
    public void printHelp() {
	System.out.println("Usage:");
        System.out.println("\t" + CommandParser.INFO_COMMAND);
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  View informations about program and available Bible versions:");
        System.out.println();
        System.out.println("\t" + CommandParser.HELP_COMMAND);

    }
    
    
    //for testing purposes
    public static void main(String[] args) throws BibleStorageException, SQLException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	InfoCommandRunner p = new InfoCommandRunner(storage);
	//storage.insertDictTerm(new DictTerm("Jehovah", "Hebrew 'name' for God, meaning 'I am'"));
	String[] params = {""};
	p.parseCommandLine(params);
	p.printVersionsInfo();
    }

}