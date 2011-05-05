package com.github.mnicky.bible4j.cli;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.data.Verse;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;
import com.github.mnicky.bible4j.Utils;

public final class ReadCommandParser extends CommandParser {

    private List<Position> positions;
    
    private List<BibleVersion> versions;
    
    private boolean wholeChaptersRequested = false;
    
    public ReadCommandParser(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    public void printHelp() {
        System.out.println("Usage:");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " POSITION [" + BIBLE_VERSION_PARAMETER + " BIBLE_VERSION...]");
        
        System.out.println();
        System.out.println("\tPOSITION \t Bible coordinates without spaces");
        System.out.println("\tBIBLE_VERSION \t Bible version abbreviation");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  Use ',' or ':' as delimiters between chapter and verse(s):");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Mt23,12");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Jn16:15");
        
        System.out.println();
        System.out.println("  Use '-' to define an interval:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Lk3:12-14");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Genesis34,1-10");
        
        System.out.println();
        System.out.println("  Use '.' to define a disjoint part:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Acts20:12.15");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " 1Peter3,1-5.7-8.10");
    
        System.out.println();
        System.out.println("  Omit verse number(s) to view whole chapters:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " 1Pt2");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " 1Jn2-3");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Exodus1-2.4-7.13-15");
    
        System.out.println();
        System.out.println("  When no bible version is declared, the first bible found is used.");
        System.out.println("  You can declare one or more bible versions:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Gal2,3-7.8 " + BIBLE_VERSION_PARAMETER + " kjv");
        System.out.println("\t" + CommandParserLauncher.BIBLE_READ_COMMAND + " Ps139:6-10 " + BIBLE_VERSION_PARAMETER + " niv rsv kjv");
        
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
	versions = parseVersions(args);
	parsePositions(getFirstValue(args).toLowerCase(new Locale("en")));
    }

    private void parsePositions(String posDef) {
	posDef = posDef.replace(":", ",");
	
	BibleBook book = extractBibleBook(posDef);
	List<Integer> chapters = parseChapters(posDef);
	List<Integer> verses = null;
	
	if (posDef.contains(","))
	    verses = parseVerses(posDef);
	
	positions = getPositions(book, chapters, verses);	
    }

    private List<Position> getPositions(BibleBook book, List<Integer> chapters, List<Integer> verses) {
	List<Position> positions = new ArrayList<Position>();
	
	//contains also verse numbers (i.e.: Jn3,5-7)
	if (verses != null) {
	    assert chapters.size() == 1 : "bible coordinate definition contains verse numbers and more than one chapter number";
	    for (int verse : verses)
		positions.add(new Position(book, chapters.get(0), verse));
	}
	//contains only chapter numbers (i.e.: Mt3-4)
	else {
	    wholeChaptersRequested = true;
	    for (int chapter : chapters)
		positions.add(new Position(book, chapter, 0));
	}
	
	
	return positions;
    }


    private List<Integer> parseVerses(String posDef) {
	List<Integer> verses;
	String verseDef = posDef.substring(posDef.indexOf(",") + 1);
	
	String[] verseRanges = null;
	    //contains also disjoint verse nums (i.e.: Lk1-6.8)
	    if (verseDef.contains("."))
		 verseRanges = verseDef.split("\\.");
	    else {
		verseRanges = new String[1];
		verseRanges[0] = verseDef;
	    }
	    verses = parseNumberRanges(verseRanges);
	
	return verses;
    }


    private List<Integer> parseChapters(String posDef) {
	List<Integer> chapters;
	String chaptDef = posDef.substring(getPositionAfterBookName(posDef));
	
	//contains also verse numbers (i.e.: Jn3,4-6)
	if (chaptDef.contains(",")) {
	    chapters = new ArrayList<Integer>(1);
	    chapters.add(Integer.valueOf(chaptDef.substring(0, chaptDef.indexOf(","))));
	}
	
	//contains only chapter numbers  (i.e.: Mk4-6)
	else {
	    String[] chaptRanges = null;
	    //contains also disjoint chapter nums (i.e.: Mk3-5.8-9.15)
	    if (chaptDef.contains("."))
		 chaptRanges = chaptDef.split("\\.");
	    else {
		chaptRanges = new String[1];
		chaptRanges[0] = chaptDef;
	    }
	    chapters = parseNumberRanges(chaptRanges);
	}
	
	if (chapters.size() < 1)
	    throw new IllegalArgumentException("Bible coordinate doesn't contain chapter number(s).");
	
	return chapters;
    }


    private List<Integer> parseNumberRanges(String[] numberRanges) {
	List<Integer> numbers = new ArrayList<Integer>();
	
	for (String numberRange : numberRanges) {
	    
	    //contains more numbers (i.e.: Mt13-15)
	    if (numberRange.contains("-")) {
		String[] numberRangeEnds = numberRange.split("-");
		
		if (numberRangeEnds.length > 2)
		    throw new IllegalArgumentException("Bad format of number range.");
		
		int beginning = Integer.valueOf(numberRangeEnds[0]);
		int end = Integer.valueOf(numberRangeEnds[1]);
		
		if (beginning > end)
		    throw new IllegalArgumentException("Beginning of interval is greater than end: " + numberRange);
		
		for (int i = beginning; i <= end; i++)
		    numbers.add(i);
		
	    }
	    
	    //contains only one number (i.e.: Jn15)
	    else
		numbers.add(Integer.valueOf(numberRange));
	}
	
	return numbers;
    }


    private BibleBook extractBibleBook(String posDef) {
	String bookNameDef = extractFirstWord(posDef);
	return Utils.getBibleBookNameByAbbr(bookNameDef);
    }
    
    private int getPositionAfterBookName(String posDef) {
	int positionAfterBookName;
	
	if (Character.isDigit(posDef.charAt(0)))
	    positionAfterBookName = getFirstNonLetterPosition(posDef.substring(1)) + 1;
	else
	    positionAfterBookName = getFirstNonLetterPosition(posDef);
	
	return positionAfterBookName;
    }
    
    private int getFirstNonLetterPosition(String posDef) {
	int firstNonLetterPosition = -1;
	for (int i = 0; i < posDef.length(); i++)
	    if (!Character.isLetter(posDef.charAt(i))) {
		firstNonLetterPosition = i;
		break;
	    }
	if (firstNonLetterPosition == -1)
	    throw new IllegalArgumentException("Bible coordinate doesn't contain a book name.");	
	return firstNonLetterPosition;
    }


    private String extractFirstWord(String posDef) {
	return posDef.substring(0, getPositionAfterBookName(posDef));
    }

    //for testing purposes
    public static void main(String[] args) throws BibleStorageException, SQLException {
	ReadCommandParser p = new ReadCommandParser(null);
	String[] params = {"", " + BIBLE_VERSION_PARAMETER + ", "kjv", "niv", "esv"};
	
	System.out.println(p.getAllValuesOfArgument(" + BIBLE_VERSION_PARAMETER + ", params));
	assert p.getAllValuesOfArgument(" + BIBLE_VERSION_PARAMETER + ", params).toString().equals("[kjv, niv, esv]");
	
	System.out.println(p.getFirstNonLetterPosition("mt5,4-8.12-17.21.23"));
	assert p.getFirstNonLetterPosition("mt5,4-8.12-17.21.23") == 2;
	
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
	

	ReadCommandParser p2 = new ReadCommandParser(null);
	p2.parsePositions("mt22:4-8.12-17.21.23");
	System.out.println(p2.positions);
	assert p2.positions.toString().equals("[MATTHEW 22,4, MATTHEW 22,5, MATTHEW 22,6, MATTHEW 22,7, MATTHEW 22,8," +
	                                      	" MATTHEW 22,12, MATTHEW 22,13, MATTHEW 22,14, MATTHEW 22,15," +
						" MATTHEW 22,16, MATTHEW 22,17, MATTHEW 22,21, MATTHEW 22,23]");
	
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	ReadCommandParser p3 = new ReadCommandParser(storage);
	String[] params2 = {"1Jn1,6-7.9", " + BIBLE_VERSION_PARAMETER + ", "czeb21"};
	p3.parse(params2);
	System.out.println();
	List<Verse> verses = p3.getVerses(); 
	for (Verse v : verses)
	    System.out.println(v == null ? "no text found" : v.getText());
	
    }

}
