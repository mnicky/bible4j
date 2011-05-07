package com.github.mnicky.bible4j.cli;

import static com.github.mnicky.bible4j.Utils.isWholeChapter;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.mnicky.bible4j.Utils;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public final class ReadCommandRunner extends CommandRunner {

    private List<Position> positions;
    
    private List<BibleVersion> versions;
    
    private List<Verse> verses = null;
    
    public ReadCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    void parseCommandLine(String[] args) throws BibleStorageException {
	versions = parseVersionsAndReturnFirstIfEmpty(args);
	positions = Utils.parsePositions(getFirstValue(args).toLowerCase(new Locale("en")));
    }

    @Override
    void doAction() throws BibleStorageException {
        verses = getVerses();
        displayVerses();
    }

    private void displayVerses() {
	if (verses == null)
	    return;
	
	BibleVersion lastBible = null;
	int lastChapter = 0;
	
	for (Verse verse : verses) {
	    BibleVersion bible = verse.getBibleVersion();
	    String book = verse.getPosition().getBook().name();//.replace("_", " ");
	    int chapter = verse.getPosition().getChapterNum();
	    
	    if (lastBible == null || !lastBible.equals(bible)) {
		lastBible = bible;
		lastChapter = 0;
	    }
	    if (lastChapter == 0 || lastChapter != chapter) {
		System.out.println("\n   " + book + ", chapter " + chapter + ", " + bible);
		System.out.println("   =============================================================");
		lastChapter = chapter;
	    }
	    System.out.println(verse.getPosition().getVerseNum() + "  " + verse.getText());
	}
    }

    private List<Verse> getVerses() throws BibleStorageException {
        List<Verse> verseList = new ArrayList<Verse>();
        
        if (versions.size() <= 1 && positions.size() <= 1) {
            if (isWholeChapter(positions.get(0)))
        	verseList = bibleStorage.getChapter(positions.get(0), versions.get(0));
            else
        	verseList.add(bibleStorage.getVerse(positions.get(0), versions.get(0)));
        }
        else if (versions.size() <= 1 && positions.size() > 1) {
            if (isWholeChapter(positions.get(0)))
        	for (Position pos : positions)
        	    verseList.addAll(bibleStorage.getChapter(pos, versions.get(0)));
            else
        	verseList = bibleStorage.getVerses(positions, versions.get(0));
        }
        else if (versions.size() > 1 && positions.size() <= 1) {
            if (isWholeChapter(positions.get(0)))
        	for (BibleVersion ver : versions)
        	    verseList.addAll(bibleStorage.getChapter(positions.get(0), ver));
            else
        	verseList = bibleStorage.compareVerses(positions.get(0), versions);
        }
        else if (versions.size() > 1 && positions.size() > 1) {
            if (isWholeChapter(positions.get(0)))
        	for (BibleVersion ver : versions)
        	    for (Position pos : positions)
        		verseList.addAll(bibleStorage.getChapter(pos, ver));
            else
        	verseList = bibleStorage.compareVerses(positions, versions);
        }
        
        if (verseList.get(0) == null)
            verseList = new ArrayList<Verse>();
        
        return verseList;
    }

    @Override
    public void printHelp() {
        System.out.println("Usage:");
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " POSITION [" + BIBLE_VERSION_ARGUMENT + " BIBLE_VERSION...]");
        
        System.out.println();
        System.out.println("\tPOSITION \t Bible coordinates (without spaces)");
        System.out.println("\tBIBLE_VERSION \t Bible version abbreviation");
        
        System.out.println();
        System.out.println("\tBible coordinates must be provided without spaces and can be specified in two common formats:");
        System.out.println();
        System.out.println("\t(1)  The format used by the New American Bible (a preferred one)");
        System.out.println("\t     This format is using ',' as delimiter between chapter and verse(s)");
        System.out.println("\t     and '.' to define a disjoint part of verses (e.g. Mk3,5-7.10)");
        System.out.println();
        System.out.println("\t(2)  The format used by Chicago manual of style");
        System.out.println("\t     This format is using ':' as delimiter between chapter and verse(s)");
        System.out.println("\t     and ',' to define a disjoint part of verses (e.g. Mk3:5-7,10)");
        System.out.println();
        System.out.println("\tIn both of above formats, '-' is used to define an interval of verses.");
        System.out.println("\tSee http://en.wikipedia.org/wiki/Bible_citation#Common_formats for more information.");
        System.out.println();
        System.out.println("\tTo specify whole chapters, verse numbers can be omitted, but ONLY USING THE FIRST FORMAT (e.g. Psalms20-23.120)");
        System.out.println();
        System.out.println("\tTo read text from specific Bible versions, use argument '" + BIBLE_VERSION_ARGUMENT + "' and specify one or more Bible versions.");
        System.out.println("\tWhen no bible version is declared, the first Bible version found is used.");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  Reading one verse:");
        System.out.println();
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " Mt23,12");
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " Jn16:15");
        
        System.out.println();
        System.out.println("  Reading more verses:");
        System.out.println();
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " Lk3,12-14");
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " Genesis34:1-10");
        
        System.out.println();
        System.out.println("  Specifying also disjoint verses:");
        System.out.println();
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " Acts20,12.15");
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " 1Peter3:1-5,7-8,10");
    
        System.out.println();
        System.out.println("  Specifying the Bible versions:");
        System.out.println();
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " Gal2,3-7.10 " + BIBLE_VERSION_ARGUMENT + " kjv");
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " Ps139:3-6,9 " + BIBLE_VERSION_ARGUMENT + " niv rsv kjv");
    
        System.out.println();
        System.out.println("  Specifying the whole chapters:");
        System.out.println();
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " 1Pt2");
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " 1Jn2-3");
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " Exodus1-2.4-7.9");
        System.out.println("\t" + CommandParser.BIBLE_READ_COMMAND + " Deut3-6.10-15.33 " + BIBLE_VERSION_ARGUMENT + " esv asv");
    }

    //for testing purposes
    public static void main(String[] args) throws BibleStorageException, SQLException {
	CommandRunner p = new ReadCommandRunner(null);
	String[] params = {"", " + BIBLE_VERSION_ARGUMENT + ", "kjv", "niv", "esv"};
	
	//System.out.println(p.getAllValuesOfArgument(" + BIBLE_VERSION_ARGUMENT + ", params));
	assert p.getAllValuesOfArgument(" + BIBLE_VERSION_ARGUMENT + ", params).toString().equals("[kjv, niv, esv]");
	
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	ReadCommandRunner p2 = new ReadCommandRunner(storage);
	String[] params2 = {"1Jn1,6-7.9", BIBLE_VERSION_ARGUMENT, "czeb21", "kjv"};
	p2.parseCommandLine(params2);
	p2.doAction();
	
    }

}
