package com.github.mnicky.bible4j.cli;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public final class ReadCommandParser extends CommandParser {

    private List<Position> positions;
    
    private List<BibleVersion> versions;
    
    public ReadCommandParser(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    public List<Verse> getVerses() throws BibleStorageException {
	List<Verse> verses = new ArrayList<Verse>();
	
	if (versions.size() <= 1 && positions.size() <= 1) {
	    if (wholeChaptersRequested)
		verses = bibleStorage.getChapter(positions.get(0), versions.get(0));
	    else
		verses.add(bibleStorage.getVerse(positions.get(0), versions.get(0)));
	}
	else if (versions.size() <= 1 && positions.size() > 1) {
	    if (wholeChaptersRequested)
		for (Position pos : positions)
		    verses.addAll(bibleStorage.getChapter(pos, versions.get(0)));
	    else
		verses = bibleStorage.getVerses(positions, versions.get(0));
	}
	else if (versions.size() > 1 && positions.size() <= 1) {
	    if (wholeChaptersRequested)
		for (BibleVersion ver : versions)
		    verses.addAll(bibleStorage.getChapter(positions.get(0), ver));
	    else
		verses = bibleStorage.compareVerses(positions.get(0), versions);
	}
	else if (versions.size() > 1 && positions.size() > 1) {
	    if (wholeChaptersRequested)
		for (BibleVersion ver : versions)
		    for (Position pos : positions)
			verses.addAll(bibleStorage.getChapter(pos, ver));
	    else
		verses = bibleStorage.compareVerses(positions, versions);
	}
	
	if (verses.get(0) == null)
	    verses = new ArrayList<Verse>();
	
	return verses;
    }


    public void parse(String[] args) throws BibleStorageException {
	versions = parseVersionsAndReturnFirstIfEmpty(args);
	positions = parsePositions(getFirstValue(args).toLowerCase(new Locale("en")));
    }

    @Override
    public void printHelp() {
        System.out.println("Usage:");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " POSITION [" + BIBLE_VERSION_ARGUMENT + " BIBLE_VERSION...]");
        
        System.out.println();
        System.out.println("\tPOSITION \t Bible coordinates (without spaces)");
        System.out.println("\tBIBLE_VERSION \t Bible version abbreviation");
        
        System.out.println();
        System.out.println("\tBible coordinates are specified in common used format, but without spaces.");
        System.out.println("\tUse ',' or ':' as delimiters between chapter and verse(s).");
        System.out.println("\tUse '-' to define an interval of verses (or chapters).");
        System.out.println("\tUse '.' to define a disjoint part of verses (or chapters).");
        System.out.println("\tOmit verse number(s) to view whole chapters.");
        System.out.println("\tTo read text from specific Bible versions, use argument '" + BIBLE_VERSION_ARGUMENT + "' and specify one or more Bible versions.");
        System.out.println("\tWhen no bible version is declared, the first Bible version found is used.");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  Reading one verse:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Mt23,12");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Jn16:15");
        
        System.out.println();
        System.out.println("  Reading more verses:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Lk3:12-14");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Genesis34,1-10");
        
        System.out.println();
        System.out.println("  Specifying also disjoint verses:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Acts20:12.15");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " 1Peter3,1-5.7-8.10");
    
        System.out.println();
        System.out.println("  Reading the whole chapters:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " 1Pt2");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " 1Jn2-3");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Exodus1-2.4-7.13-15");
    
        System.out.println();
        System.out.println("  Specifying the Bible versions:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Gal2,3-7.8 " + BIBLE_VERSION_ARGUMENT + " kjv");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Ps139:6-10 " + BIBLE_VERSION_ARGUMENT + " niv rsv kjv");
        
    }

    //for testing purposes
    public static void main(String[] args) throws BibleStorageException, SQLException {
	CommandParser p = new ReadCommandParser(null);
	String[] params = {"", " + BIBLE_VERSION_ARGUMENT + ", "kjv", "niv", "esv"};
	
	System.out.println(p.getAllValuesOfArgument(" + BIBLE_VERSION_ARGUMENT + ", params));
	assert p.getAllValuesOfArgument(" + BIBLE_VERSION_ARGUMENT + ", params).toString().equals("[kjv, niv, esv]");
	
	System.out.println(p.getPositionOfFirstNonLetter("mt5,4-8.12-17.21.23"));
	assert p.getPositionOfFirstNonLetter("mt5,4-8.12-17.21.23") == 2;
	
	System.out.println(p.extractFirstWord("mt5,4-8.12-17.21.23"));
	assert p.extractFirstWord("mt5,4-8.12-17.21.23").toString().equals("mt");
	
	System.out.println(p.extractFirstWord("1cor5,4-8.12-17.21.23"));
	assert p.extractFirstWord("1cor5,4-8.12-17.21.23").toString().equals("1cor");
	
	System.out.println(p.extractBibleBook("mt5,4-8.12-17.21.23"));
	assert p.extractBibleBook("mt5,4-8.12-17.21.23").toString().equals("MATTHEW");
	
	System.out.println(p.parseChapters("mt21,8"));
	assert p.parseChapters("mt21,8").toString().equals("[21]");
	
	System.out.println(p.parseChapters("mt10,4-8"));
	assert p.parseChapters("mt10,4-8").toString().equals("[10]");
	
	System.out.println(p.parseChapters("mt15,4-8"));
	assert p.parseChapters("mt15,4-8.12-17.21.23").toString().equals("[15]");
	
	System.out.println(p.parseChapters("jn4-6"));
	assert p.parseChapters("jn4-6").toString().equals("[4, 5, 6]");
	
	String[] ranges = {"1-5", "8-10", "15-16"};
	System.out.println(p.parseNumberRanges(ranges));
	assert p.parseNumberRanges(ranges).toString().equals("[1, 2, 3, 4, 5, 8, 9, 10, 15, 16]");
	
	System.out.println(p.parseChapters("mt4.12"));
	assert p.parseChapters("mt4.12").toString().equals("[4, 12]");
	
	System.out.println(p.parseChapters("mt4-8.12-17.21.23"));
	assert p.parseChapters("mt4-8.12-17.21.23").toString().equals("[4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17, 21, 23]");
	
	System.out.println(p.parseVerses("mt22,4-8.12-17.21.23"));
	assert p.parseVerses("mt22,4-8.12-17.21.23").toString().equals("[4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17, 21, 23]");
	

	CommandParser p2 = new ReadCommandParser(null);
	System.out.println(p2.parsePositions("mt22:4-8.12-17.21.23"));
	assert p2.parsePositions("mt22:4-8.12-17.21.23").toString().equals("[MATTHEW 22,4, MATTHEW 22,5, MATTHEW 22,6, MATTHEW 22,7, MATTHEW 22,8," +
	                                      	" MATTHEW 22,12, MATTHEW 22,13, MATTHEW 22,14, MATTHEW 22,15," +
						" MATTHEW 22,16, MATTHEW 22,17, MATTHEW 22,21, MATTHEW 22,23]");
	
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	ReadCommandParser p3 = new ReadCommandParser(storage);
	String[] params2 = {"1Jn1,6-7.9", BIBLE_VERSION_ARGUMENT, "czeb21", "kjv"};
	p3.parse(params2);
	System.out.println();
	List<Verse> verses = p3.getVerses(); 
	for (Verse v : verses)
	    System.out.println(v == null ? "no text found" : v.getText());
	
    }

}
