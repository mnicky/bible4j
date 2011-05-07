package com.github.mnicky.bible4j.parsers;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import com.github.mnicky.bible4j.Utils;
import com.github.mnicky.bible4j.data.DictTerm;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public class EastonsDictionaryDownloader implements DictionaryDownloader {
    
    private static final String TITLE = "Easton's Bible Dictionary";
    
    private static final String START_URL = "http://www2.mf.no/bibelprog/easton?word=aaron&nomo&nomd";
    
    private BibleStorage storage;
    
    public EastonsDictionaryDownloader(BibleStorage storage) {
	this.storage = storage;
    }
    
    public String getTitle() {
	return TITLE;
    }

    public void setStorage(BibleStorage storage) {
	this.storage = storage;
    }

    @Override
    public void downloadDictionary() throws BibleStorageException, IOException {
	Source source = Utils.getSource(new URL(START_URL), 3, 1000);

	String nextTerm = null;
	
	do {
	    StringBuilder definitionBuilder = new StringBuilder();
	    nextTerm = null;
	    Segment prev = null;
	    boolean definitionStarted = false;
	    boolean isNextTerm = false;

	    for (Segment segment : source) {
		if (isEndOfDefinition(prev, segment))
		    break;

		if (segment instanceof StartTag) {
		    String href = getHrefAttr(segment);
		    if (href != null && href.contains("easton?word=")) {
			if (isNextTerm)
			    nextTerm = parseNextTerm(href);
			else
			    isNextTerm = true;
		    }
		}

		if (definitionStarted && segmentIsText(segment))
		    definitionBuilder.append(segment);
		if (isStartOfDefinition(segment))
		    definitionStarted = true;

		prev = segment;
	    }

	    String definition = definitionBuilder.substring(definitionBuilder.indexOf(" - ") + 3);
	    String name = parseTermNameFromPage(source);

	    System.out.println("downloading '" + name +"'");
	    storage.insertDictTerm(new DictTerm(name, definition));

	    source = Utils.getSource(new URL("http://www2.mf.no/bibelprog/easton?word=" + nextTerm + "&nomo&nomd"), 3, 1000);
	    
	    
	} while (nextTerm != null);
    }

    private String parseNextTerm(String href) {
	return href.substring(href.indexOf('=') + 1, href.indexOf('&'));
    }

    private String getHrefAttr(Segment segment) {
	return ((StartTag) segment).getAttributeValue("href");
    }

    private String parseTermNameFromPage(Source source) {
	return source.getNextElement(0, "h3").getTextExtractor().toString().split(":")[1].trim();
    }

    private boolean isStartOfDefinition(Segment segment) {
	return segment.toString().equalsIgnoreCase("</h3>");
    }

    private boolean segmentIsText(Segment segment) {
	return segment.getClass().getName().equals("net.htmlparser.jericho.Segment");
    }

    private boolean isEndOfDefinition(Segment prev, Segment segment) {
	return segment.toString().equalsIgnoreCase("<center>") && prev != null && prev.toString().equalsIgnoreCase("<p>");
    }
    
    
    //for testing purpose
    public static void main(String[] args) throws SQLException, BibleImporterException, BibleStorageException, IOException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test;MVCC=TRUE", "test", ""));
	
	DictionaryDownloader dImp = new EastonsDictionaryDownloader(storage);
	dImp.downloadDictionary();
    }

}
