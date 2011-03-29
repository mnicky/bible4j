package com.github.mnicky.bible4j.data;

/**
 * Represents the books of the Bible.
 */
public enum BibleBook {

    // The Old Testament
    GENESIS		(Testament.OLD, false),
    EXODUS		(Testament.OLD, false),
    LEVITICUS		(Testament.OLD, false),    
    NUMBERS		(Testament.OLD, false),    
    DEUTERONOMY		(Testament.OLD, false),    
    JOSHUA		(Testament.OLD, false), 
    JUDGES		(Testament.OLD, false),
    RUTH		(Testament.OLD, false),
    SAMUEL_1		(Testament.OLD, false),
    SAMUEL_2		(Testament.OLD, false),
    KINGS_1		(Testament.OLD, false),
    KINGS_2		(Testament.OLD, false),
    CHRONICLES_1	(Testament.OLD, false),
    CHRONICLES_2	(Testament.OLD, false),
    EZRA		(Testament.OLD, false),
    NEHEMIAH		(Testament.OLD, false),
    TOBIT		(Testament.OLD, true),  // deuterocanonical
    JUDITH		(Testament.OLD, true),  // deuterocanonical
    ESTHER		(Testament.OLD, false),
    JOB			(Testament.OLD, false),
    PSALMS		(Testament.OLD, false),
    PROVERBS		(Testament.OLD, false),
    ECCLESIASTES	(Testament.OLD, false),
    SONG_OF_SONGS	(Testament.OLD, false),
    WISDOM		(Testament.OLD, true),	// deuterocanonical
    SIRACH		(Testament.OLD, true),  // deuterocanonical
    ISAIAH		(Testament.OLD, false),
    JEREMIAH		(Testament.OLD, false),	
    LAMENTATIONS	(Testament.OLD, false),
    BARUCH		(Testament.OLD, true),  // deuterocanonical
    EZEKIEL		(Testament.OLD, false),
    DANIEL		(Testament.OLD, false),
    HOSEA		(Testament.OLD, false),
    JOEL		(Testament.OLD, false),
    AMOS		(Testament.OLD, false),
    OBADIAH		(Testament.OLD, false),
    JONAH		(Testament.OLD, false),
    MICAH		(Testament.OLD, false),
    NAHUM		(Testament.OLD, false),
    HABAKKUK		(Testament.OLD, false),
    ZEPHANIAH		(Testament.OLD, false),
    HAGGAI		(Testament.OLD, false),
    ZECHARIAH		(Testament.OLD, false),
    MALACHI		(Testament.OLD, false),
    MACCABEES_1		(Testament.OLD, true),  // deuterocanonical
    MACCABEES_2		(Testament.OLD, true),  // deuterocanonical
   
    // The New Testament    
    MATTHEW		(Testament.NEW, false),
    MARK		(Testament.NEW, false),
    LUKE		(Testament.NEW, false),
    JOHN		(Testament.NEW, false),
    ACTS		(Testament.NEW, false),
    ROMANS		(Testament.NEW, false),
    CORINTHIANS_1	(Testament.NEW, false),
    CORINTHIANS_2	(Testament.NEW, false),
    GALATIANS		(Testament.NEW, false),
    EPHESIANS		(Testament.NEW, false),
    PHILIPPIANS		(Testament.NEW, false),
    COLOSSIANS		(Testament.NEW, false),
    THESSALONIANS_1	(Testament.NEW, false),
    THESSALONIANS_2	(Testament.NEW, false),
    TIMOTHY_1		(Testament.NEW, false),
    TIMOTHY_2		(Testament.NEW, false),
    TITUS		(Testament.NEW, false),
    PHILEMON		(Testament.NEW, false),
    HEBREWS		(Testament.NEW, false),
    JAMES		(Testament.NEW, false),
    PETER_1		(Testament.NEW, false),
    PETER_2		(Testament.NEW, false),
    JOHN_1		(Testament.NEW, false),
    JOHN_2		(Testament.NEW, false),
    JOHN_3		(Testament.NEW, false),
    JUDE		(Testament.NEW, false),
    REVELATION		(Testament.NEW, false)
    ;
    
    /**
     * Testament this Bible book is in.
     */
    private final Testament testament;
    
    /**
     * Whether this Bible book is deuterocanonical.
     */
    private final boolean isDeuterocanonical;
    
    //TODO add bible book abbreviation?
    
    /**
     * Private constructor for instances of this enum.
     * @param testament Bible testament this book is in
     * @param isDeuterocanonical whether this book is deuterocanonical
     */
    private BibleBook(Testament testament, boolean isDeuterocanonical) {
	this.testament = testament;
	this.isDeuterocanonical = isDeuterocanonical;
    }
    
    /**
     * Returns true if this Bible book is deuterocanonical.
     * @return true if this Bible book is deuterocanonical
     */
    public boolean isDeutero() {
	return this.isDeuterocanonical;
    }
    
    /**
     * Returns testament this Bible book is in. 
     * @return testament this Bible book is in
     */
    public Testament getTestament() {
	return this.testament;
    }
    
    public String getName() {
	return this.toString().toLowerCase();
    }
    
    /**
     * This inner enum represents Bible Testaments.
     */
    public static enum Testament {
	OLD,
	NEW,
    }
    
}
