package com.github.mnicky.bible4j.parsers;

import hirondelle.date4j.DateTime;
import hirondelle.date4j.DateTime.DayOverflow;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import com.github.mnicky.bible4j.Utils;
import com.github.mnicky.bible4j.data.DailyReading;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public final class MassGospelReadingsDownloader implements ReadingsDownloader {
    
    private static final String TITLE = "Catholic Mass Gospel Readings";
    
    //private static final String START_URL = "http://cathcal.org/index.php";
    private static final String START_URL = "http://www.easterbrooks.com/cgi-bin/Cathcal.cgi?20121101";

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
    public void downloadReadings(int nextMonths) throws IOException, BibleStorageException {
		
	Source source = Utils.getSource(new URL(START_URL), 3, 1000);
	
	int monthCount = 0;
	int actualMonth = 0;

	String nextReadingUrl = null;
	String nextReadingDate = null;
	String bibleCoords = null;
	
	storage.insertReadingList(TITLE);

	while (nextMonths != 0 && monthCount < nextMonths) {

	    if (nextReadingUrl != null && nextReadingDate != null) {
		System.out.println(nextReadingDate);
		System.out.println(bibleCoords);
		//System.out.println(nextReadingUrl);
		System.out.println();

		try {
		    storage.insertDailyReading(new DailyReading(TITLE, new DateTime(nextReadingDate), Utils.parsePositions(bibleCoords)));
		} catch (IllegalArgumentException e) {
		    //we don't want to stop downloading the readings because of one malformed
		    e.printStackTrace();
		}
		
		
	    }
	    
	    bibleCoords = null;
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
		    bibleCoords = segment.toString().trim().replace(" ", "");
		    break;
		}

		if (!isAfterNextReadingInfo && segmentIsText(segment) && segmentEquals(segment, "day")) {
		    nextReadingUrl = parseNextReadingUrl(source, segment);
		    nextReadingDate = parseReadingDateFromUrl(nextReadingUrl);
		    isAfterNextReadingInfo = true;
		}

		if (segmentIsText(segment) && segmentEquals(segment, "gospel:") || segmentEquals(segment, "gospel: (optional)"))
		    gospelHrefFollows = true;

	    }

	    if (actualMonth == 0)
		actualMonth = parseActualMonth(nextReadingDate);
	    else if (actualMonth != parseActualMonth(nextReadingDate)) {
		monthCount++;
		actualMonth = parseActualMonth(nextReadingDate);
	    }
	    
	    source = Utils.getSource(new URL(nextReadingUrl), 3, 1000);
	    
	    //TODO add server error recovery - skip to next (manually computed) URL
//	    try {
//		source = Utils.getSource(new URL(nextReadingUrl), 2, 1000);
//	    } catch (IOException e) {
//		//we don't want to stop downloading the readings because of one bad page
//		e.printStackTrace();
//		System.out.println(computeNextUrl(nextReadingUrl));
//		//try to compute the next reading url
//		source = Utils.getSource(new URL(computeNextUrl(nextReadingUrl)), 5, 1000);
//	    }
	}

    }

    private String computeNextUrl(String nextReadingUrl) {
	DateTime date = new DateTime(parseReadingDateFromUrl(nextReadingUrl));
	date.plus(0, 0, 2, 0, 0, 0, DayOverflow.FirstDay);
	return date.toString();
    }

    private boolean segmentEquals(Segment segment, String string) {
	return segment.toString().trim().equalsIgnoreCase(string);
    }

    private int parseActualMonth(String nextReadingDate) {
	return Integer.valueOf(nextReadingDate.split("-")[1]);
    }

    private String parseReadingDateFromUrl(String nextReadingUrl) {
	String nextReadingDate;
	StringBuilder nextDateBuilder = new StringBuilder(nextReadingUrl.split("\\?")[1]);
	nextDateBuilder.insert(4, "-");
	nextDateBuilder.insert(7, "-");
	nextReadingDate = nextDateBuilder.toString();
	return nextReadingDate;
    }

    private String parseNextReadingUrl(Source source, Segment segment) {
	return source.getNextElement(segment.getEnd()).getAttributeValue("href");
    }
    


    private boolean segmentIsText(Segment segment) {
	return segment.getClass().getName().equals("net.htmlparser.jericho.Segment");
    }
    
    
    //for testing purposes
    public static void main(String[] args) throws SQLException, IOException, BibleStorageException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test;MVCC=TRUE", "test", ""));
	
	ReadingsDownloader readD = new MassGospelReadingsDownloader(storage);
	readD.downloadReadings(13);
    }

}
