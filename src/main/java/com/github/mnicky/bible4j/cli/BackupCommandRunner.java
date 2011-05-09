package com.github.mnicky.bible4j.cli;

import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

/**
 * This class controls the backup functionality of the application.
 *
 */
public class BackupCommandRunner extends CommandRunner {
    
    String backupLocation;

    public BackupCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    void parseCommandLine(String[] args) throws BibleStorageException {
	backupLocation = getFirstValue(args);
    }

    @Override
    void doRequestedAction() throws BibleStorageException {
	System.out.println("Backing up the Bible storage to zip file '" + backupLocation + "'...");
	bibleStorage.createBackup(backupLocation);
	System.out.println("Backup file created.");
	System.out.println("To restore, replace storage files with the files from this backup zip archive.");
    }

    @Override
    public void printHelp() {
	System.out.println();
	System.out.println("Usage:");
        System.out.println("\t" + CommandParser.BACKUP_COMMAND + " PATH_TO_FILE");
        
        System.out.println();
        System.out.println("\tPATH_TO_FILE \t path to backup file");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("\t" + CommandParser.BACKUP_COMMAND + " backup.zip");
        System.out.println();
    }

}
