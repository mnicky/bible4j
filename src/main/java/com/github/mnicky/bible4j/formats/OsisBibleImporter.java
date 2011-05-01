package com.github.mnicky.bible4j.formats;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public final class OsisBibleImporter implements BibleImporter {

    private final BibleStorage storage;

    private String currentVerseText = "";

    private BibleVersion currentBibleVersion = new BibleVersion("unknown", "unknown");

    private Position currentPosition = null;

    public OsisBibleImporter(BibleStorage storage) {
	this.storage = storage;
    }

    @Override
    public void importBible(InputStream input) throws XMLStreamException, FactoryConfigurationError, BibleStorageException {

	XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(input);

	while (reader.hasNext()) {

	    if (reader.getEventType() == XMLEvent.START_ELEMENT) {

		if (reader.getLocalName().equals("osisText")) {
		    parseOsisText(reader);
		    storage.insertBibleVersion(currentBibleVersion);
		}
		else if (reader.getLocalName().equals("verse")) {
		    parseVerse(reader);

		    storage.insertBibleBook(currentPosition.getBook());
		    storage.insertPosition(currentPosition);
		    storage.insertVerse(new Verse(currentVerseText, currentPosition, currentBibleVersion));
		}

	    }

	    reader.next();
	}
	reader.close();

    }

    private void parseOsisText(XMLStreamReader reader) {
	String versionName = "unknown";
	String versionLang = "unknown";

	for (int i = 0; i < reader.getAttributeCount(); i++) {
	    if (reader.getAttributeLocalName(i).equals("osisIDWork")) {
		versionName = reader.getAttributeValue(i);
	    }
	    else if (reader.getAttributeLocalName(i).equals("lang")) {
		versionLang = reader.getAttributeValue(i);
	    }
	}

	currentBibleVersion = new BibleVersion(versionName, versionLang);
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
	return new Position(getBibleBookName(positionArray[0]), Integer.valueOf(positionArray[1]), Integer.valueOf(positionArray[2]));
    }

    private BibleBook getBibleBookName(String bookAbbr) {
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
	else
	    throw new IllegalArgumentException();

    }
				 
    public static void main(String[] args) throws XMLStreamException, FactoryConfigurationError, BibleStorageException, SQLException, FileNotFoundException {
	
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test;TRACE_LEVEL_FILE=2;MVCC=TRUE", "test", ""));
	//storage.createStorage();
	BibleImporter importer = new OsisBibleImporter(storage);
	importer.importBible(new FileInputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/cz-bkr_osis.xml"));
	System.out.println("finished");
    }

}
