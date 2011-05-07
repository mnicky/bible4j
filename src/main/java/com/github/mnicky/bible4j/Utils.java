package com.github.mnicky.bible4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.htmlparser.jericho.Source;

import com.github.mnicky.bible4j.data.BibleBook;
import com.github.mnicky.bible4j.data.Position;

public final class Utils {
    
    private static final String BIBLE_BOOK_ABBRS_FILE = "/bibleBookAbbrs.conf";
    private static final String COMMENT_CHAR = "#";
    private static final String SPLIT_CHAR = ":";
    
    /**
     * This is static library class, therefore it is not possible to instantiate it.
     */
    private Utils() {}
    
    /**
     * Tries to get Source from URL for 'tryTimes' times, sleeping for 'sleepMilis' and then throws an exception.
     */
    //Ugly but useful :-)
    public static Source getSource(URL url, int tryTimes, int sleepMilis) {
	Source source = null;
	int tryCountDown = tryTimes;
	boolean success = false;

	while (!success && tryCountDown > 0) {
	    try {
		source = new Source(url);
		success = true;
	    } catch (IOException e) {
		
		tryCountDown--;
		if (tryCountDown <= 0)
		    throw new RuntimeException(e);
		
		else {
		    e.printStackTrace();
		    
		    try {
			Thread.sleep(sleepMilis);
		    } catch (InterruptedException e1) {
			e1.printStackTrace();
		    }
		}
		
	    }
	}
	return source;
    }
    
    public static BibleBook getBibleBookNameByAbbr(String abbr) {
	
	Map<String, BibleBook> bookNames;
	try {
	    bookNames = getAbbrBookMap();
	} catch (IOException e) {
	    throw new RuntimeException("BibleBook name could not be retrieved.", e);
	}
	
	BibleBook book = bookNames.get(abbr.toLowerCase(new Locale("en")));
	
	if (book == null)
	    throw new IllegalArgumentException("Bible book abbreviation '" + abbr + "' is unknown.");
	
	return book;
	
    }

    public static Map<String, BibleBook> getAbbrBookMap() throws IOException {
	Map<String, BibleBook> bookNames = new HashMap<String, BibleBook>();
	BufferedReader r = null;
	String line;
	
	try {	    
	    r = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream(BIBLE_BOOK_ABBRS_FILE), "utf-8"));
	    
	    while ((line = r.readLine()) != null) {
		if (line.length() > 0 && !line.startsWith(COMMENT_CHAR)) {
		    String[] abbrAndName = line.split(SPLIT_CHAR);
		    if (abbrAndName.length > 2)
			throw new RuntimeException("Bad format in configuration file " + BIBLE_BOOK_ABBRS_FILE);
		    //System.out.println(abbrAndName[0] + " " + abbrAndName[1]);
		    bookNames.put(abbrAndName[0], BibleBook.getBibleBookByName(abbrAndName[1]));
		}
	    }
	} finally {
	    r.close();
	}

	return bookNames;
    }
    
    public static Map<BibleBook, String> getBookAbbrMap() throws IOException {
	Map<BibleBook, String> bookNames = new HashMap<BibleBook, String>();
	BufferedReader r = null;
	String line;

	try {
	    r = new BufferedReader(new InputStreamReader(new FileInputStream(BIBLE_BOOK_ABBRS_FILE), "utf-8"));

	    while ((line = r.readLine()) != null) {
		if (line.length() > 0 && !line.startsWith(COMMENT_CHAR)) {
		    String[] abbrAndName = line.split(SPLIT_CHAR);
		    if (abbrAndName.length > 2)
			throw new RuntimeException("Bad format in configuration file " + BIBLE_BOOK_ABBRS_FILE);
		    bookNames.put(BibleBook.getBibleBookByName(abbrAndName[1]), abbrAndName[0]);
		}
	    }
	} finally {
	    r.close();
	}

	return bookNames;
    }

    public static boolean isWholeChapter(Position position) {
        return position.getVerseNum() == 0;
    }

    public static List<Position> parsePositions(String posDef) {
        
        if (posDef.contains(":")) {
            posDef = posDef.replace(",", ".");
            posDef = posDef.replace(":", ",");
        }
        
        BibleBook book = extractBibleBook(posDef);
        List<Integer> chapters = parseChapters(posDef);
        List<Integer> verses = null;
        
        if (posDef.contains(","))
            verses = parseVerses(posDef);
        
        return getPositions(book, chapters, verses);	
    }

    private static List<Position> getPositions(BibleBook book, List<Integer> chapters, List<Integer> verses) {
        List<Position> positionList = new ArrayList<Position>();
        
        //contains also verse numbers (i.e.: Jn3,5-7)
        if (verses != null) {
            assert chapters.size() == 1 : "bible coordinate definition contains verse numbers and more than one chapter number";
            for (int verse : verses)
        	positionList.add(new Position(book, chapters.get(0), verse));
        }
        //contains only chapter numbers (i.e.: Mt3-4)
        else {
            for (int chapter : chapters)
        	positionList.add(new Position(book, chapter, 0));
        }
        
        
        return positionList;
    }

    private static List<Integer> parseVerses(String posDef) {
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

    private static List<Integer> parseChapters(String posDef) {
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

    private static List<Integer> parseNumberRanges(String[] numberRanges) {
        List<Integer> numbers = new ArrayList<Integer>();
        
        for (String numberRange : numberRanges) {
            
            //contains more numbers (i.e.: Mt13-15)
            if (numberRange.contains("-")) {
        	String[] numberRangeEnds = numberRange.split("-");
        	
        	if (numberRangeEnds.length > 2)
        	    throw new IllegalArgumentException("Bad format of number range: '" + numberRange + "'.");
        	
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

    private static BibleBook extractBibleBook(String posDef) {
        String bookNameDef = extractFirstWord(posDef);
        return Utils.getBibleBookNameByAbbr(bookNameDef);
    }

    private static int getPositionAfterBookName(String posDef) {
        int positionAfterBookName;
        
        if (Character.isDigit(posDef.charAt(0)))
            positionAfterBookName = getPositionOfFirstNonLetter(posDef.substring(1)) + 1;
        else
            positionAfterBookName = getPositionOfFirstNonLetter(posDef);
        
        return positionAfterBookName;
    }

    private static int getPositionOfFirstNonLetter(String posDef) {
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

    private static String extractFirstWord(String posDef) {
        return posDef.substring(0, getPositionAfterBookName(posDef));
    }
    
    
    
    
    //for testing purposes
    public static void main(String[] args) {
	System.out.println(Utils.getBibleBookNameByAbbr("mt"));

	System.out.println(getPositionOfFirstNonLetter("mt5,4-8.12-17.21.23"));
	assert getPositionOfFirstNonLetter("mt5,4-8.12-17.21.23") == 2;

	System.out.println(extractFirstWord("mt5,4-8.12-17.21.23"));
	assert extractFirstWord("mt5,4-8.12-17.21.23").toString().equals("mt");

	System.out.println(extractFirstWord("1cor5,4-8.12-17.21.23"));
	assert extractFirstWord("1cor5,4-8.12-17.21.23").toString().equals("1cor");
	
	System.out.println(extractBibleBook("mt5:4-8,12-17,21,23"));
	assert extractBibleBook("mt5:4-8,12-17,21,23").toString().equals("MATTHEW");
	
	System.out.println(parseChapters("mt21,8"));
	assert parseChapters("mt21,8").toString().equals("[21]");
	
	System.out.println(parseChapters("mt10,4-8"));
	assert parseChapters("mt10,4-8").toString().equals("[10]");
	
	System.out.println(parseChapters("mt15,4-8"));
	assert parseChapters("mt15,4-8.12-17.21.23").toString().equals("[15]");
	
	System.out.println(parseChapters("jn4-6"));
	assert parseChapters("jn4-6").toString().equals("[4, 5, 6]");
	
	String[] ranges = {"1-5", "8-10", "15-16"};
	System.out.println(parseNumberRanges(ranges));
	assert parseNumberRanges(ranges).toString().equals("[1, 2, 3, 4, 5, 8, 9, 10, 15, 16]");
	
	System.out.println(parseChapters("mt4.12"));
	assert parseChapters("mt4.12").toString().equals("[4, 12]");
	
	System.out.println(parseChapters("mt4-8.12-17.21.23"));
	assert parseChapters("mt4-8.12-17.21.23").toString().equals("[4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17, 21, 23]");
	
	System.out.println(parseVerses("mt22,4-8.12-17.21.23"));
	assert parseVerses("mt22,4-8.12-17.21.23").toString().equals("[4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17, 21, 23]");

	System.out.println(parsePositions("mt22:4-8,12-17,21,23"));
	assert parsePositions("mt22:4-8,12-17,21,23").toString().equals("[MATTHEW 22,4, MATTHEW 22,5, MATTHEW 22,6, MATTHEW 22,7, MATTHEW 22,8," +
	                                      	" MATTHEW 22,12, MATTHEW 22,13, MATTHEW 22,14, MATTHEW 22,15," +
						" MATTHEW 22,16, MATTHEW 22,17, MATTHEW 22,21, MATTHEW 22,23]");
    }

}
