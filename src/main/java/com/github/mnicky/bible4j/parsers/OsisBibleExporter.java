package com.github.mnicky.bible4j.parsers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public final class OsisBibleExporter implements BibleExporter {
    
    private BibleStorage storage;
    
    public OsisBibleExporter(BibleStorage storage) {
	this.storage = storage;
    }

    public void setStorage(BibleStorage storage) {
	this.storage = storage;
    }

    @Override
    public void exportBible(BibleVersion bible, OutputStream stream) throws BibleExporterException, BibleStorageException {

	try {
	    XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(stream);

	    writer.writeStartDocument("utf-8", "1.0");
	    writer.writeCharacters("\n");

	    writeOsisStartElement(writer);
	    writeOsisTextStartElement(bible, writer);
	    writeHeaderElement(bible, writer);

	    List<Position> chapters = storage.getChapterList(bible);
	    BibleBook lastBook = null;

	    for (Position chapter : chapters) {
		List<Verse> verses = storage.getChapter(chapter, bible);

		if (lastBook == null) {
		    lastBook = chapter.getBook();
		    writeBookStartElement(writer, lastBook);
		}
		else if (!lastBook.equals(chapter.getBook())) {
		    lastBook = chapter.getBook();
		    // div#book EndElement
		    writeEndElement(writer);
		    writeBookStartElement(writer, lastBook);
		}
		writeChapterStartElement(writer, chapter);
		
		for (Verse verse : verses)
		    writeVerseElement(writer, verse);

		// div#chapter EndElement		
		writeEndElement(writer);
	    }

	    // div#book EndElement	    
	    writeEndElement(writer);
	    // osisText EndElement	    
	    writeEndElement(writer);
	    // osis EndElement	    
	    writeEndElement(writer);

	    writer.writeEndDocument();
	    writer.flush();
	    writer.close();

	} catch (XMLStreamException e) {
	    throw new BibleExporterException("Exporting error", e);
	}

    }

    private void writeEndElement(XMLStreamWriter writer) throws XMLStreamException {
	writer.writeEndElement();
	writer.writeCharacters("\n");
    }

    private void writeOsisStartElement(XMLStreamWriter writer) throws XMLStreamException {
	writer.writeStartElement("", "osis", "http://www.bibletechnologies.net/2003/OSIS/namespace");
	writer.writeAttribute("xmlns", "http://www.bibletechnologies.net/2003/OSIS/namespace");
	writer.writeAttribute("xmlns", "", "xsi", "http://www.w3.org/2001/XMLSchema-instance");
	writer.writeAttribute("xsi", "", "schemaLocation", "http://www.bibletechnologies.net/2003/OSIS/namespace http://www.bibletechnologies.net/osisCore.2.1.1.xsd");
	writer.writeCharacters("\n");
    }

    private void writeOsisTextStartElement(BibleVersion bible, XMLStreamWriter writer) throws XMLStreamException {
	writer.writeStartElement("osisText");
	writer.writeAttribute("osisIDWork", bible.getAbbr().toUpperCase(new Locale("en")));
	writer.writeAttribute("xml", "", "lang", bible.getLanguage());
	writer.writeCharacters("\n");
    }

    private void writeVerseElement(XMLStreamWriter writer, Verse verse) throws XMLStreamException {
	writer.writeStartElement("verse");
	writer.writeAttribute("osisID", getOsisAbbrFromBibleBook(verse.getPosition().getBook()) + "."
	                      + verse.getPosition().getChapterNum() + "." + verse.getPosition().getVerseNum());
	writer.writeCharacters(verse.getText());
	writeEndElement(writer);
    }

    private void writeChapterStartElement(XMLStreamWriter writer, Position chapter) throws XMLStreamException {
	writer.writeStartElement("chapter");
	writer.writeAttribute("osisID", getOsisAbbrFromBibleBook(chapter.getBook()) + "." + chapter.getChapterNum());
	writer.writeCharacters("\n");
    }

    private void writeBookStartElement(XMLStreamWriter writer, BibleBook lastBook) throws XMLStreamException {
	writer.writeStartElement("div");
	writer.writeAttribute("type", "book");
	writer.writeAttribute("osisID", getOsisAbbrFromBibleBook(lastBook));
	writer.writeCharacters("\n");
    }

    private void writeHeaderElement(BibleVersion bible, XMLStreamWriter writer) throws XMLStreamException {
	writer.writeStartElement("header");
	writer.writeCharacters("\n");

		writer.writeStartElement("work");
		writer.writeAttribute("osisWork", bible.getAbbr().toUpperCase(new Locale("en")));
		writer.writeCharacters("\n");

			writer.writeStartElement("title");
			writer.writeCharacters(bible.getName());
			writeEndElement(writer);
			
			writer.writeStartElement("refSystem");
			writer.writeCharacters("Bible." + bible.getAbbr().toUpperCase(new Locale("en")));
			writeEndElement(writer);
			
		writeEndElement(writer);

	writeEndElement(writer);
    }
    
    private String getOsisAbbrFromBibleBook(BibleBook book) {
	if (book == BibleBook.GENESIS)
	    return "Gen";
	if (book == BibleBook.EXODUS)
	    return "Exod";
	if (book == BibleBook.LEVITICUS)
	    return "Lev";
	if (book == BibleBook.NUMBERS)
	    return "Num";
	if (book == BibleBook.DEUTERONOMY)
	    return "Deut";
	if (book == BibleBook.JOSHUA)
	    return "Josh";
	if (book == BibleBook.JUDGES)
	    return "Judg";
	if (book == BibleBook.RUTH)
	    return "Ruth";
	if (book == BibleBook.SAMUEL_1)
	    return "1Sam";
	if (book == BibleBook.SAMUEL_2)
	    return "2Sam";
	if (book == BibleBook.KINGS_1)
	    return "1Kgs";
	if (book == BibleBook.KINGS_2)
	    return "2Kgs";
	if (book == BibleBook.CHRONICLES_1)
	    return "1Chr";
	if (book == BibleBook.CHRONICLES_2)
	    return "2Chr";
	if (book == BibleBook.EZRA)
	    return "Ezra";
	if (book == BibleBook.NEHEMIAH)
	    return "Neh";
	if (book == BibleBook.ESTHER)
	    return "Esth";
	if (book == BibleBook.JOB)
	    return "Job";
	if (book == BibleBook.PSALMS)
	    return "Ps";
	if (book == BibleBook.PROVERBS)
	    return "Prov";
	if (book == BibleBook.ECCLESIASTES)
	    return "Eccl";
	if (book == BibleBook.SONG_OF_SONGS)
	    return "Song";
	if (book == BibleBook.ISAIAH)
	    return "Isa";
	if (book == BibleBook.JEREMIAH)
	    return "Jer";
	if (book == BibleBook.LAMENTATIONS)
	    return "Lam";
	if (book == BibleBook.EZEKIEL)
	    return "Ezek";
	if (book == BibleBook.DANIEL)
	    return "Dan";
	if (book == BibleBook.HOSEA)
	    return "Hos";
	if (book == BibleBook.JOEL)
	    return "Joel";
	if (book == BibleBook.AMOS)
	    return "Amos";
	if (book == BibleBook.OBADIAH)
	    return "Obad";
	if (book == BibleBook.JONAH)
	    return "Jonah";
	if (book == BibleBook.MICAH)
	    return "Mic";
	if (book == BibleBook.NAHUM)
	    return "Nah";
	if (book == BibleBook.HABAKKUK)
	    return "Hab";
	if (book == BibleBook.ZEPHANIAH)
	    return "Zeph";
	if (book == BibleBook.HAGGAI)
	    return "Hag";
	if (book == BibleBook.ZECHARIAH)
	    return "Zech";
	if (book == BibleBook.MALACHI)
	    return "Mal";

	if (book == BibleBook.TOBIT)
	    return "Tob";
	if (book == BibleBook.JUDITH)
	    return "Jdt";
	if (book == BibleBook.WISDOM)
	    return "Wis";
	if (book == BibleBook.SIRACH)
	    return "Sir";
	if (book == BibleBook.BARUCH)
	    return "Bar";
	if (book == BibleBook.MACCABEES_1)
	    return "1Macc";
	if (book == BibleBook.MACCABEES_2)
	    return "2Macc";

	if (book == BibleBook.MATTHEW)
	    return "Matt";
	if (book == BibleBook.MARK)
	    return "Mark";
	if (book == BibleBook.LUKE)
	    return "Luke";
	if (book == BibleBook.JOHN)
	    return "John";
	if (book == BibleBook.ACTS)
	    return "Acts";
	if (book == BibleBook.ROMANS)
	    return "Rom";
	if (book == BibleBook.CORINTHIANS_1)
	    return "1Cor";
	if (book == BibleBook.CORINTHIANS_2)
	    return "2Cor";
	if (book == BibleBook.GALATIANS)
	    return "Gal";
	if (book == BibleBook.EPHESIANS)
	    return "Eph";
	if (book == BibleBook.PHILIPPIANS)
	    return "Phil";
	if (book == BibleBook.COLOSSIANS)
	    return "Col";
	if (book == BibleBook.THESSALONIANS_1)
	    return "1Thess";
	if (book == BibleBook.THESSALONIANS_2)
	    return "2Thess";
	if (book == BibleBook.TIMOTHY_1)
	    return "1Tim";
	if (book == BibleBook.TIMOTHY_2)
	    return "2Tim";
	if (book == BibleBook.TITUS)
	    return "Titus";
	if (book == BibleBook.PHILEMON)
	    return "Phlm";
	if (book == BibleBook.HEBREWS)
	    return "Heb";
	if (book == BibleBook.JAMES)
	    return "Jas";
	if (book == BibleBook.PETER_1)
	    return "1Pet";
	if (book == BibleBook.PETER_2)
	    return "2Pet";
	if (book == BibleBook.JOHN_1)
	    return "1John";
	if (book == BibleBook.JOHN_2)
	    return "2John";
	if (book == BibleBook.JOHN_3)
	    return "3John";
	if (book == BibleBook.JUDE)
	    return "Jude";
	if (book == BibleBook.REVELATION)
	    return "Rev";
	else
	    throw new IllegalArgumentException();

    }

    //for testing purpose
    public static void main(String[] args) throws FileNotFoundException, BibleImporterException, BibleStorageException, SQLException, BibleExporterException  {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	BibleExporter exporter = new OsisBibleExporter(storage);
	exporter.exportBible(new BibleVersion("King's James Version", "kjv2", "en"), new FileOutputStream("/home/marek/projects/2011-dbs-vppj/misc/osis-bibles/my_kjv.xml"));
	System.out.println("finished");
    }

}
