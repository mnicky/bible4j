package com.github.mnicky.bible4j.formats;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import com.github.mnicky.bible4j.data.DictTerm;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public class EastonsDictionaryImporter implements DictionaryImporter {
    
    private static final String TITLE = "Easton's Bible Dictionary";
    
    private static final String DOWNLOAD_URL = "http://www2.mf.no/bibelprog/easton?word=aaron&nomo&nomd";
    
    private BibleStorage storage;
    
    public EastonsDictionaryImporter(BibleStorage storage) {
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
	Source source = new Source(new URL(DOWNLOAD_URL));

	String nextTerm = null;
	
	do {
	    StringBuilder sb = new StringBuilder();
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
		    sb.append(segment).toString();
		if (isStartOfDefinition(segment))
		    definitionStarted = true;

		prev = segment;
	    }

	    String definition = sb.substring(sb.indexOf("-") + 2);
	    String name = parseTermNameFromPage(source);

	    System.out.println("downloading '" + name +"'");
	    storage.insertDictTerm(new DictTerm(name, definition));

	    source = new Source(new URL("http://www2.mf.no/bibelprog/easton?word=" + nextTerm + "&nomo&nomd").openStream());
	    
	    
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
    public static void main(String[] args) throws SQLException, BibleImporterException, BibleStorageException, MalformedURLException, IOException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test;MVCC=TRUE", "test", ""));
	
	DictionaryImporter dImp = new EastonsDictionaryImporter(storage);
	dImp.downloadDictionary();
    }

}
