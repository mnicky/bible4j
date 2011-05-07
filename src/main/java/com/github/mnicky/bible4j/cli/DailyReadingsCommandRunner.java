package com.github.mnicky.bible4j.cli;

import hirondelle.date4j.DateTime;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.DailyReading;
import com.github.mnicky.bible4j.data.Verse;
import com.github.mnicky.bible4j.parsers.MassGospelReadingsDownloader;
import com.github.mnicky.bible4j.parsers.ReadingsDownloader;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public class DailyReadingsCommandRunner extends CommandRunner {

    private DateTime date = new DateTime("0000-00-00");
    
    
    private List<Verse> verses = null;
    private BibleVersion version;
    
    public DailyReadingsCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    void parseCommandLine(String[] args) throws IOException, BibleStorageException {
	if (isArgument(args[0]) && args[0].equalsIgnoreCase(DOWNLOAD_ARGUMENT))
	    downloadReadings(parseDownloadMonthCount(args));
	else {
	    date = parseDate(getFirstValue(args));
	    version = parseVersionsAndReturnFirstIfEmpty(args).get(0);
	}
    }

    @Override
    void doAction() throws BibleStorageException {
	verses = getReading();
	printReading();
    }

    private List<Verse> getReading() throws BibleStorageException {
	List<DailyReading> readings = bibleStorage.getDailyReadings(date);
	if (readings != null && readings.size() > 0)
	    return bibleStorage.getVerses(readings.get(0).getPositions(), version);
	else
	    return null;
    }

    private void printReading() {
	if (verses == null)
	    return;	

	int lastChapter = 0;
	    
	for (Verse verse : verses) {
	    BibleVersion bible = verse.getBibleVersion();
	    String book = verse.getPosition().getBook().name();//.replace("_", " ");
	    int chapter = verse.getPosition().getChapterNum();

	    if (lastChapter == 0 || lastChapter != chapter) {
		System.out.println("\n   " + book + ", chapter " + chapter + ", " + bible);
		System.out.println("   =============================================================");
		lastChapter = chapter;
	    }
	    System.out.println(verse.getPosition().getVerseNum() + "  " + verse.getText());
	}
	
    }

    private DateTime parseDate(String date) {
	DateTime dateTime = null;
	try {
	    date = date.replace(".", "-").replace("/", "-");
	    String[] dateArray = date.split("-");
	    dateTime = new DateTime(Integer.valueOf(dateArray[2]), Integer.valueOf(dateArray[1]), Integer.valueOf(dateArray[0]), 0, 0, 0, 0);
	} catch (RuntimeException e) {
	    throw new IllegalArgumentException("Probably bad date format specified.", e);
	}
	return dateTime;
    }

    private Integer parseDownloadMonthCount(String[] args) {
	return Integer.valueOf(getFirstValueOfArgument(DOWNLOAD_ARGUMENT, args));
    }

    private void downloadReadings(int nextMonths) throws IOException, BibleStorageException {
	ReadingsDownloader readDown = new MassGospelReadingsDownloader(bibleStorage);
	System.out.println("Downloading " + readDown.getTitle() + "...");
	readDown.downloadReadings(nextMonths);
	System.out.println("Readings downloaded.");
    }

    @Override
    public void printHelp() {
	
	System.out.println("Usage:");
        System.out.println("\t" + CommandParser.DAILY_READINGS_COMMAND + " DD-MM-YYYY");
        System.out.println("\t" + CommandParser.DAILY_READINGS_COMMAND + " " + DOWNLOAD_ARGUMENT + " NUMBER_OF_MONTHS");
        
        System.out.println();
        System.out.println("\tDD-MM-YYYY \t\t Date of daily bible reading to show (must be in specified format)");
        System.out.println("\tNUMBER_OF_MONTHS \t Number of next months to download readings for (including this month)");
        
        System.out.println();
	System.out.println("\tDate must be in format DD-MM-YYYY, but you can also use '.' or '/' as delimiters");
	System.out.println("\tTo download daily readings, use argument '" + DOWNLOAD_ARGUMENT
		+ "' and provide the number of months (including this) to download the readings for.");

        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  View daily Bible readings for specific day:");
        System.out.println();
        System.out.println("\t" + CommandParser.DAILY_READINGS_COMMAND + " 12-03-2010");
        
        System.out.println();
        System.out.println("  Download daily Bible readings for two months ahead:");
        System.out.println();
        System.out.println("\t" + CommandParser.DAILY_READINGS_COMMAND + " " + DOWNLOAD_ARGUMENT + " 2");

    }

    // for testing purposes
    public static void main(String[] args) throws SQLException, IOException, BibleStorageException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	DailyReadingsCommandRunner p = new DailyReadingsCommandRunner(storage);
	//String[] params = { "-down", "2" };
	String[] params = { "09-05-2011"};
	p.parseCommandLine(params);
	p.doAction();
    }

}
