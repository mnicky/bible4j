package com.github.mnicky.bible4j.cli;

import hirondelle.date4j.DateTime;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.github.mnicky.bible4j.data.DailyReading;
import com.github.mnicky.bible4j.parsers.MassGospelReadingsDownloader;
import com.github.mnicky.bible4j.parsers.ReadingsDownloader;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public class DailyReadingsCommandParser extends CommandParser {

    private DateTime date = new DateTime("0000-00-00");
    
    public DailyReadingsCommandParser(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    public void parse(String[] args) throws IOException, BibleStorageException {
	if (isArgument(args[0]) && args[0].equalsIgnoreCase(DOWNLOAD_ARGUMENT))
	    downloadReadings(parseDownloadMonthCount(args));
	date = parseDate(getFirstValue(args));
	System.out.println(date);
    }
    
    public List<DailyReading> getDailyReadings() throws BibleStorageException {
	return bibleStorage.getDailyReadings(date);
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
	readDown.downloadDictionary(nextMonths);

    }

    @Override
    public void printHelp() {
	// TODO Auto-generated method stub

    }

    // for testing purposes
    public static void main(String[] args) throws SQLException, IOException, BibleStorageException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	DailyReadingsCommandParser p = new DailyReadingsCommandParser(storage);
	String[] params = { "29.2.2000" };
	p.parse(params);
	p.getDailyReadings();
    }

}
