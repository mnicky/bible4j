package com.github.mnicky.bible4j.storage;

final class H2DbNaming {
    
    //database tables
    
    
    static final String VERSIONS = "`bible_versions`";
    static final String VERSION_ID = "`id`";
    static final String VERSION_NAME = "`name`";
    static final String VERSION_LANG = "`lang`";
    
    static final String BOOKS = "`bible_books`";
    static final String BOOK_ID = "`id`";
    static final String BOOK_NAME = "`name`";
    static final String BOOK_DEUT = "`is_deutero`";
    
    static final String COORDS = "`coords`";
    static final String COORD_ID = "`id`";
    static final String COORD_BOOK = "`bible_book_id`";
    static final String COORD_CHAPT = "`chapter_num`";
    static final String COORD_VERSE = "`verse_num`";
    
    static final String VERSES = "`verses`";
    static final String VERSE_ID = "`id`";
    static final String VERSE_TEXT = "`text`";
    static final String VERSE_VERSION = "`bible_version_id`";
    static final String VERSE_COORD = "`coord_id`";
    
    static final String NOTES = "`notes`";
    static final String NOTE_ID = "`id`";
    static final String NOTE_TYPE = "`type`";
    static final String NOTE_TEXT = "`text`";
    static final String NOTE_COORD = "`coord_id`";
    
    static final String BKMARKS = "`bookmarks`";
    static final String BKMARK_ID = "`id`";
    static final String BKMARK_NAME = "`name`";
    static final String BKMARK_VERSE = "`verse_id`";
    
    static final String RLISTS = "`daily_readings_lists`";
    static final String RLIST_ID = "`id`";
    static final String RLIST_NAME = "`name`";
    
    static final String READS = "`daily_readings`";
    static final String READ_ID = "`id`";
    static final String READ_DATE = "`date`";
    static final String READ_LIST = "`daily_readings_list_id`";
    
    static final String READxCOORDS = "`readings_coords`";
    static final String READxCOORD_ID = "`id`";
    static final String READxCOORD_READ = "`coord_id`";
    static final String READxCOORD_COORD = "`reading_id`";
    
    static final String TERMS = "`dict_terms`";
    static final String TERM_ID = "`id`";
    static final String TERM_NAME = "`name`";
    static final String TERM_DEF = "`def`";
    
    
    
    /**
     * This is a static library class, so cannot be instantiated. 
     */
    private H2DbNaming() {
	
    }

}
