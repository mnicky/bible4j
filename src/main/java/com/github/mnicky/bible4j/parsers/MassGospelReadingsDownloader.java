package com.github.mnicky.bible4j.parsers;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public final class MassGospelReadingsDownloader implements ReadingsDownloader {
    
    private static final String TITLE = "Catholic Mass Gospel Readings";
    
    private static final String START_URL = "http://cathcal.org/index.php";

    BibleStorage storage;
    
    public MassGospelReadingsDownloader(BibleStorage storage) {
	this.storage = storage;
    }
    
    @Override
    public void setStorage(BibleStorage storage) {
	this.storage = storage;
    }
    
    public String getTitle() {
	return TITLE;
    }

    @Override
    public void downloadDictionary(int nextMonths) throws IOException, BibleStorageException {
		
	Source source = new Source(new URL(START_URL));
	
	int monthCount = 0;
	int actualMonth = 0;

	String nextReadingUrl = null;
	String nextReadingDate = null;
	String bibleCoordinate = null;

	while (nextMonths != 0 && monthCount < nextMonths) {

	    if (nextReadingUrl != null && nextReadingDate != null) {
		System.out.println(nextReadingDate);
		System.out.println(bibleCoordinate);
		//System.out.println(nextReadingUrl);
		System.out.println();
	    }
	    
	    bibleCoordinate = null;
	    nextReadingUrl = null;
	    nextReadingDate = null;

	    boolean gospelHrefFollows = false;
	    boolean gospelCoordinateFollows = false;
	    boolean isAfterNextReadingInfo = false;

	    for (Segment segment : source) {

		if (gospelHrefFollows && segment instanceof StartTag) {
		    gospelHrefFollows = false;
		    gospelCoordinateFollows = true;
		}

		if (gospelCoordinateFollows && segmentIsText(segment)) {
		    bibleCoordinate = segment.toString().trim().replace(" ", "");
		    break;
		}

		if (!isAfterNextReadingInfo && segmentIsText(segment) && segment.toString().trim().equalsIgnoreCase("day")) {
		    nextReadingUrl = parseNextDate(source, segment);
		    nextReadingDate = parseNextReadingDate(nextReadingUrl);
		    isAfterNextReadingInfo = true;
		}

		if (segmentIsText(segment) && segment.toString().trim().equalsIgnoreCase("gospel:"))
		    gospelHrefFollows = true;

	    }

	    if (actualMonth == 0)
		actualMonth = parseActualMonth(nextReadingDate);
	    else if (actualMonth != parseActualMonth(nextReadingDate)) {
		monthCount++;
		actualMonth = parseActualMonth(nextReadingDate);
	    }
	    
	    source = new Source(new URL(nextReadingUrl));
	}

	// storage.insertDailyReading(new DailyReading(TITLE, null, null));

    }

    private int parseActualMonth(String nextReadingDate) {
	return Integer.valueOf(nextReadingDate.split("-")[1]);
    }

    private String parseNextReadingDate(String nextReadingUrl) {
	String nextReadingDate;
	StringBuilder nextDateBuilder = new StringBuilder(nextReadingUrl.split("\\?")[1]);
	nextDateBuilder.insert(4, "-");
	nextDateBuilder.insert(7, "-");
	nextReadingDate = nextDateBuilder.toString();
	return nextReadingDate;
    }

    private String parseNextDate(Source source, Segment segment) {
	return source.getNextElement(segment.getEnd()).getAttributeValue("href");
    }
    


    private boolean segmentIsText(Segment segment) {
	return segment.getClass().getName().equals("net.htmlparser.jericho.Segment");
    }
    
    
    //for testing purposes
    public static void main(String[] args) throws SQLException, IOException, BibleStorageException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test;MVCC=TRUE", "test", ""));
	
	ReadingsDownloader readD = new MassGospelReadingsDownloader(storage);
	readD.downloadDictionary(9);
    }

}
