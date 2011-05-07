package com.github.mnicky.bible4j.cli;

import java.io.IOException;
import java.util.Arrays;

import com.github.mnicky.bible4j.parsers.BibleExporterException;
import com.github.mnicky.bible4j.parsers.BibleImporterException;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;

public class CommandParser {

    private final BibleStorage storage;

    private boolean helpRequested = false;

    static final String BIBLE_READ_COMMAND = "read";
    static final String BIBLE_SEARCH_COMMAND = "search";
    static final String IMPORT_COMMAND = "import";
    static final String EXPORT_COMMAND = "export";
    static final String NOTES_COMMAND = "note";
    static final String DICTIONARY_COMMAND = "dict";
    static final String DAILY_READINGS_COMMAND = "daily";
    static final String BOOKMARKS_COMMAND = "bkmark";
    static final String INFO_COMMAND = "info";
    static final String HELP_COMMAND = "help";

    public CommandParser(BibleStorage bibleStorage) {
	this.storage = bibleStorage;
    }

    public void launch(String[] args) throws BibleStorageException, BibleImporterException, BibleExporterException, IOException {

	CommandRunner runner = getCommandRunner(args);

	if (runner != null) {
	    if (helpRequested)
		// print specific help
		runner.printHelp();
	    else
		//run the application
		runCommandRunner(runner, args);	    	
	}
	else {
	    // print main help
	    printProgramInfo();
	    printHelp();
	}
    }

    private void runCommandRunner(CommandRunner runner, String[] args) throws BibleStorageException, BibleImporterException, BibleExporterException, IOException {
	runner.parseCommandLine(Arrays.copyOfRange(args, 1, args.length));
	runner.doAction();
    }

    private CommandRunner getCommandRunner(String[] args) {
	if (args.length < 1)
	    return null;

	if (args[0].equalsIgnoreCase(BIBLE_READ_COMMAND))
	    return new ReadCommandRunner(storage);

	else if (args[0].equalsIgnoreCase(BIBLE_SEARCH_COMMAND))
	    return new SearchCommandRunner(storage);

	else if (args[0].equalsIgnoreCase(IMPORT_COMMAND))
	    return new ImportCommandRunner(storage);

	else if (args[0].equalsIgnoreCase(EXPORT_COMMAND))
	    return new ExportCommandRunner(storage);

	else if (args[0].equalsIgnoreCase(NOTES_COMMAND))
	    return new NotesCommandRunner(storage);

	else if (args[0].equalsIgnoreCase(DICTIONARY_COMMAND))
	    return new DictionaryCommandRunner(storage);

	else if (args[0].equalsIgnoreCase(DAILY_READINGS_COMMAND))
	    return new DailyReadingsCommandRunner(storage);

	else if (args[0].equalsIgnoreCase(BOOKMARKS_COMMAND))
	    return new BookmarksCommandRunner(storage);

	else if (args[0].equalsIgnoreCase(INFO_COMMAND))
	    return new InfoCommandRunner(storage);

	else if (args[0].equalsIgnoreCase(HELP_COMMAND) && !helpRequested) {
	    helpRequested = true;
	    if (args.length > 1)
		return getCommandRunner(Arrays.copyOfRange(args, 1, args.length));
	}

	return null;
    }

    private void printHelp() {
	System.out.println();
	System.out.println("Use '" + HELP_COMMAND + " COMMAND' for help.");
	System.out.println();
	System.out.println("Possible commands:");
	System.out.println(BIBLE_READ_COMMAND + "\t read the Bible");
	System.out.println(BIBLE_SEARCH_COMMAND + "\t search the Bible");
	System.out.println(NOTES_COMMAND + "\t add notes to the Bible text");
	System.out.println(BOOKMARKS_COMMAND + "\t bookmark Bible passage");
	System.out.println(DAILY_READINGS_COMMAND + "\t view daily readings");
	System.out.println(DICTIONARY_COMMAND + "\t look up a word in a Biblical dictionary");
	System.out.println(IMPORT_COMMAND + "\t import the Bible");
	System.out.println(EXPORT_COMMAND + "\t export the Bible");
	System.out.println(INFO_COMMAND + "\t view informations about program and available Bible versions");
    }

    static void printProgramInfo() {
        System.out.println("bible4j - Simple Bible viewer for Java");
        System.out.println("        - by Marek Srank (xmnicky@gmail.com, http://mnicky.github.com)");
        System.out.println();
        System.out.println("License - The MIT License (http://www.opensource.org/licenses/mit-license.php)");
        System.out.println("Bugs    - Probably. Please, send bug reports to the email above.");
        System.out.println();
    }

    // for testing purposes
    public static void main(String[] args) throws BibleStorageException, BibleImporterException, BibleExporterException, IOException {
	CommandParser cp = new CommandParser(null);
	String[] params = { "help", "fg" };
	cp.launch(params);
    }

}
