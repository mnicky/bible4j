package com.github.mnicky.bible4j.parsers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mnicky.bible4j.AppRunner;
import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

//TODO add unit tests for this class

public final class OsisBibleImporter implements BibleImporter {
    
    private final static Logger logger = LoggerFactory.getLogger(AppRunner.Logger.class);
    
    private BibleStorage storage;

    private String currentVerseText = "";

    private BibleVersion currentBibleVersion = new BibleVersion("unknown", "unknown", "unknown");

    private Position currentPosition = null;
    
    boolean bibleVersionInserted = false;
    
    public OsisBibleImporter(BibleStorage storage) {
	this.storage = storage;
    }

    public void setStorage(BibleStorage storage) {
	this.storage = storage;
    }

    @Override
    public void importBible(InputStream input) throws BibleImporterException, BibleStorageException {
	XMLStreamReader reader = null;
	try {
	    reader = XMLInputFactory.newInstance().createXMLStreamReader(input);

	    while (reader.hasNext()) {

		if (reader.getEventType() == XMLEvent.START_ELEMENT) {

		    if (reader.getLocalName().equals("osisText")) {
			parseOsisText(reader);
		    }
		    else if (reader.getLocalName().equals("work")) {
			parseWork(storage, reader);
		    }
		    else if (reader.getLocalName().equals("verse")) {
			
			if (!bibleVersionInserted) {
			    storage.insertBibleVersion(currentBibleVersion);
			    bibleVersionInserted = true;
			}
			
			parseVerse(reader);
			storage.insertBibleBook(currentPosition.getBook());
			storage.insertPosition(currentPosition);
			storage.insertVerse(new Verse(currentVerseText, currentPosition, currentBibleVersion));
		    }

		}

		reader.next();
	    }
	    
	    resetValues();
	    
	} catch (XMLStreamException e) {
	    throw new BibleImporterException("Importing error", e);
	} finally {
	    if (reader != null)
		try {
		    reader.close();
		} catch (XMLStreamException e) {
		    throw new BibleImporterException("Importing error", e);
		}
	}

    }

    private void resetValues() {
	    currentVerseText = "";
	    currentBibleVersion = new BibleVersion("unknown", "unknown", "unknown");
	    currentPosition = null;	    
	    bibleVersionInserted = false;	
    }

    private void parseWork(BibleStorage storage, XMLStreamReader reader) throws XMLStreamException, BibleStorageException {
	
	while (reader.hasNext() && (
		!(reader.getEventType() == XMLEvent.START_ELEMENT && reader.getLocalName().equals("title"))
		&& !(reader.getEventType() == XMLEvent.END_ELEMENT && reader.getLocalName().equals("work"))
		))
	    reader.next();
	
	if (reader.getEventType() == XMLEvent.START_ELEMENT && reader.getLocalName().equals("title"))
	    currentBibleVersion = new BibleVersion(reader.getElementText(), currentBibleVersion.getAbbr(), currentBibleVersion.getLanguage());
    }

    private void parseOsisText(XMLStreamReader reader) {
	String versionAbbr = "unknown";
	String versionLang = "unknown";
	String versionName = "unknown";

	for (int i = 0; i < reader.getAttributeCount(); i++) {
	    if (reader.getAttributeLocalName(i).equals("osisIDWork")) {
		versionAbbr = reader.getAttributeValue(i).toLowerCase(new Locale("en"));
	    }
	    else if (reader.getAttributeLocalName(i).equals("lang")) {
		versionLang = reader.getAttributeValue(i);
	    }
	}

	currentBibleVersion = new BibleVersion(versionName, versionAbbr, versionLang);
    }

    private void parseVerse(XMLStreamReader reader) throws XMLStreamException {
	for (int i = 0; i < reader.getAttributeCount(); i++) {
	    if (reader.getAttributeLocalName(i).equals("osisID")) {
		currentPosition = parsePosition(reader.getAttributeValue(i));
		break;
	    }
	}
	currentVerseText = reader.getElementText();
    }

    private Position parsePosition(String position) {
	String[] positionArray = position.split("\\.");
	return new Position(getBibleBookByOsisAbbr(positionArray[0]), Integer.valueOf(positionArray[1]), Integer.valueOf(positionArray[2]));
    }

    //TODO change method of obtaining Book from OSIS abbreviations to be shorter
    private BibleBook getBibleBookByOsisAbbr(String bookAbbr) {
	if (bookAbbr.equals("Gen"))
	    return BibleBook.GENESIS;
	if (bookAbbr.equals("Exod"))
	    return BibleBook.EXODUS;
	if (bookAbbr.equals("Lev"))
	    return BibleBook.LEVITICUS;
	if (bookAbbr.equals("Num"))
	    return BibleBook.NUMBERS;
	if (bookAbbr.equals("Deut"))
	    return BibleBook.DEUTERONOMY;
	if (bookAbbr.equals("Josh"))
	    return BibleBook.JOSHUA;
	if (bookAbbr.equals("Judg"))
	    return BibleBook.JUDGES;
	if (bookAbbr.equals("Ruth"))
	    return BibleBook.RUTH;
	if (bookAbbr.equals("1Sam"))
	    return BibleBook.SAMUEL_1;
	if (bookAbbr.equals("2Sam"))
	    return BibleBook.SAMUEL_2;
	if (bookAbbr.equals("1Kgs"))
	    return BibleBook.KINGS_1;
	if (bookAbbr.equals("2Kgs"))
	    return BibleBook.KINGS_2;
	if (bookAbbr.equals("1Chr"))
	    return BibleBook.CHRONICLES_1;
	if (bookAbbr.equals("2Chr"))
	    return BibleBook.CHRONICLES_2;
	if (bookAbbr.equals("Ezra"))
	    return BibleBook.EZRA;
	if (bookAbbr.equals("Neh"))
	    return BibleBook.NEHEMIAH;
	if (bookAbbr.equals("Esth"))
	    return BibleBook.ESTHER;
	if (bookAbbr.equals("Job"))
	    return BibleBook.JOB;
	if (bookAbbr.equals("Ps"))
	    return BibleBook.PSALMS;
	if (bookAbbr.equals("Prov"))
	    return BibleBook.PROVERBS;
	if (bookAbbr.equals("Eccl"))
	    return BibleBook.ECCLESIASTES;
	if (bookAbbr.equals("Song"))
	    return BibleBook.SONG_OF_SONGS;
	if (bookAbbr.equals("Isa"))
	    return BibleBook.ISAIAH;
	if (bookAbbr.equals("Jer"))
	    return BibleBook.JEREMIAH;
	if (bookAbbr.equals("Lam"))
	    return BibleBook.LAMENTATIONS;
	if (bookAbbr.equals("Ezek"))
	    return BibleBook.EZEKIEL;
	if (bookAbbr.equals("Dan"))
	    return BibleBook.DANIEL;
	if (bookAbbr.equals("Hos"))
	    return BibleBook.HOSEA;
	if (bookAbbr.equals("Joel"))
	    return BibleBook.JOEL;
	if (bookAbbr.equals("Amos"))
	    return BibleBook.AMOS;
	if (bookAbbr.equals("Obad"))
	    return BibleBook.OBADIAH;
	if (bookAbbr.equals("Jonah"))
	    return BibleBook.JONAH;
	if (bookAbbr.equals("Mic"))
	    return BibleBook.MICAH;
	if (bookAbbr.equals("Nah"))
	    return BibleBook.NAHUM;
	if (bookAbbr.equals("Hab"))
	    return BibleBook.HABAKKUK;
	if (bookAbbr.equals("Zeph"))
	    return BibleBook.ZEPHANIAH;
	if (bookAbbr.equals("Hag"))
	    return BibleBook.HAGGAI;
	if (bookAbbr.equals("Zech"))
	    return BibleBook.ZECHARIAH;
	if (bookAbbr.equals("Mal"))
	    return BibleBook.MALACHI;

	if (bookAbbr.equals("Tob"))
	    return BibleBook.TOBIT;
	if (bookAbbr.equals("Jdt"))
	    return BibleBook.JUDITH;
	if (bookAbbr.equals("Wis"))
	    return BibleBook.WISDOM;
	if (bookAbbr.equals("Sir"))
	    return BibleBook.SIRACH;
	if (bookAbbr.equals("Bar"))
	    return BibleBook.BARUCH;
	if (bookAbbr.equals("1Macc"))
	    return BibleBook.MACCABEES_1;
	if (bookAbbr.equals("2Macc"))
	    return BibleBook.MACCABEES_2;

	if (bookAbbr.equals("Matt"))
	    return BibleBook.MATTHEW;
	if (bookAbbr.equals("Mark"))
	    return BibleBook.MARK;
	if (bookAbbr.equals("Luke"))
	    return BibleBook.LUKE;
	if (bookAbbr.equals("John"))
	    return BibleBook.JOHN;
	if (bookAbbr.equals("Acts"))
	    return BibleBook.ACTS;
	if (bookAbbr.equals("Rom"))
	    return BibleBook.ROMANS;
	if (bookAbbr.equals("1Cor"))
	    return BibleBook.CORINTHIANS_1;
	if (bookAbbr.equals("2Cor"))
	    return BibleBook.CORINTHIANS_2;
	if (bookAbbr.equals("Gal"))
	    return BibleBook.GALATIANS;
	if (bookAbbr.equals("Eph"))
	    return BibleBook.EPHESIANS;
	if (bookAbbr.equals("Phil"))
	    return BibleBook.PHILIPPIANS;
	if (bookAbbr.equals("Col"))
	    return BibleBook.COLOSSIANS;
	if (bookAbbr.equals("1Thess"))
	    return BibleBook.THESSALONIANS_1;
	if (bookAbbr.equals("2Thess"))
	    return BibleBook.THESSALONIANS_2;
	if (bookAbbr.equals("1Tim"))
	    return BibleBook.TIMOTHY_1;
	if (bookAbbr.equals("2Tim"))
	    return BibleBook.TIMOTHY_2;
	if (bookAbbr.equals("Titus"))
	    return BibleBook.TITUS;
	if (bookAbbr.equals("Phlm"))
	    return BibleBook.PHILEMON;
	if (bookAbbr.equals("Heb"))
	    return BibleBook.HEBREWS;
	if (bookAbbr.equals("Jas"))
	    return BibleBook.JAMES;
	if (bookAbbr.equals("1Pet"))
	    return BibleBook.PETER_1;
	if (bookAbbr.equals("2Pet"))
	    return BibleBook.PETER_2;
	if (bookAbbr.equals("1John"))
	    return BibleBook.JOHN_1;
	if (bookAbbr.equals("2John"))
	    return BibleBook.JOHN_2;
	if (bookAbbr.equals("3John"))
	    return BibleBook.JOHN_3;
	if (bookAbbr.equals("Jude"))
	    return BibleBook.JUDE;
	if (bookAbbr.equals("Rev"))
	    return BibleBook.REVELATION;
	else {
	    logger.error("Unknown BibleBook type specified: {}", bookAbbr);
	    throw new IllegalArgumentException("Unknown BibleBook type specified.");
	}

    }
	
    //for testing purpose
    public static void main(String[] args) throws FileNotFoundException, BibleImporterException, BibleStorageException, SQLException  {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test;MVCC=TRUE", "test", ""));
	storage.initializeStorage();
	
	OsisBibleImporter importer = new OsisBibleImporter(storage);
	
	//importer.importBible(new FileInputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/real/cs-b21_osis.xml"));	
	//importer.importBible(new FileInputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/real/cs-bkr_osis.xml"));	
	//importer.importBible(new FileInputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/real/cs-cep_osis.xml"));	
	//importer.importBible(new FileInputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/real/en-asv_osis.xml"));	
	//importer.importBible(new FileInputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/real/en-kjv_osis.xml"));	
	//importer.importBible(new FileInputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/real/en-rsv_osis.xml"));	
	//importer.importBible(new FileInputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/real/en-web_osis.xml"));
	//importer.importBible(new FileInputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/real/en-bbe_osis.xml"));
	importer.importBible(new FileInputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/real/en-ylt_osis.xml"));
	
	System.out.println("finished");
    }

}
