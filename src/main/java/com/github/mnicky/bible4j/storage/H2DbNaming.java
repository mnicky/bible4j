package com.github.mnicky.bible4j.storage;

final class H2DbNaming {

    // database tables

    static final String VERSIONS = "`bible_versions`";
    static final String VERSION_ID = "`id`";
    static final String VERSION_ID_F = VERSIONS + "." + VERSION_ID;
    static final String VERSION_NAME = "`name`";
    static final String VERSION_NAME_F = VERSIONS + "." + VERSION_NAME;
    static final String VERSION_LANG = "`lang`";
    static final String VERSION_LANG_F = VERSIONS + "." + VERSION_LANG;

    static final String BOOKS = "`bible_books`";
    static final String BOOK_ID = "`id`";
    static final String BOOK_ID_F = BOOKS + "." + BOOK_ID;
    static final String BOOK_NAME = "`name`";
    static final String BOOK_NAME_F = BOOKS + "." + BOOK_NAME;
    static final String BOOK_DEUT = "`is_deutero`";
    static final String BOOK_DEUT_F = BOOKS + "." + BOOK_DEUT;

    static final String COORDS = "`coords`";
    static final String COORD_ID = "`id`";
    static final String COORD_ID_F = COORDS + "." + COORD_ID;
    static final String COORD_BOOK = "`bible_book_id`";
    static final String COORD_BOOK_F = COORDS + "." + COORD_BOOK;
    static final String COORD_CHAPT = "`chapter_num`";
    static final String COORD_CHAPT_F = COORDS + "." + COORD_CHAPT;
    static final String COORD_VERSE = "`verse_num`";
    static final String COORD_VERSE_F = COORDS + "." + COORD_VERSE;

    static final String VERSES = "`verses`";
    static final String VERSE_ID = "`id`";
    static final String VERSE_ID_F = VERSES + "." + VERSE_ID;
    static final String VERSE_TEXT = "`text`";
    static final String VERSE_TEXT_F = VERSES + "." + VERSE_TEXT;
    static final String VERSE_VERSION = "`bible_version_id`";
    static final String VERSE_VERSION_F = VERSES + "." + VERSE_VERSION;
    static final String VERSE_COORD = "`coord_id`";
    static final String VERSE_COORD_F = VERSES + "." + VERSE_COORD;

    static final String NOTES = "`notes`";
    static final String NOTE_ID = "`id`";
    static final String NOTE_ID_F = NOTES + "." + NOTE_ID;
    static final String NOTE_TYPE = "`type`";
    static final String NOTE_TYPE_F = NOTES + "." + NOTE_TYPE;
    static final String NOTE_TEXT = "`text`";
    static final String NOTE_TEXT_F = NOTES + "." + NOTE_TEXT;
    static final String NOTE_COORD = "`coord_id`";
    static final String NOTE_COORD_F = NOTES + "." + NOTE_COORD;

    static final String BKMARKS = "`bookmarks`";
    static final String BKMARK_ID = "`id`";
    static final String BKMARK_ID_F = BKMARKS + "." + BKMARK_ID;
    static final String BKMARK_NAME = "`name`";
    static final String BKMARK_NAME_F = BKMARKS + "." + BKMARK_NAME;
    static final String BKMARK_VERSE = "`verse_id`";
    static final String BKMARK_VERSE_F = BKMARKS + "." + BKMARK_VERSE;

    static final String RLISTS = "`daily_readings_lists`";
    static final String RLIST_ID = "`id`";
    static final String RLIST_ID_F = RLISTS + "." + RLIST_ID;
    static final String RLIST_NAME = "`name`";
    static final String RLIST_NAME_F = RLISTS + "." + RLIST_NAME;

    static final String READS = "`daily_readings`";
    static final String READ_ID = "`id`";
    static final String READ_ID_F = READS + "." + READ_ID;
    static final String READ_DATE = "`date`";
    static final String READ_DATE_F = READS + "." + READ_DATE;
    static final String READ_LIST = "`daily_readings_list_id`";
    static final String READ_LIST_F = READS + "." + READ_LIST;

    static final String READxCOORDS = "`readings_coords`";
    static final String READxCOORD_ID = "`id`";
    static final String READxCOORD_ID_F = READxCOORDS + "." + READxCOORD_ID;
    static final String READxCOORD_COORD = "`coord_id`";
    static final String READxCOORD_COORD_F = READxCOORDS + "." + READxCOORD_COORD;
    static final String READxCOORD_READ = "`reading_id`";
    static final String READxCOORD_READ_F = READxCOORDS + "." + READxCOORD_READ;

    static final String TERMS = "`dict_terms`";
    static final String TERM_ID = "`id`";
    static final String TERM_ID_F = TERMS + "." + TERM_ID;
    static final String TERM_NAME = "`name`";
    static final String TERM_NAME_F = TERMS + "." + TERM_NAME;
    static final String TERM_DEF = "`def`";
    static final String TERM_DEF_F = TERMS + "." + TERM_DEF;

    /**
     * This is a static library class, so cannot be instantiated.
     */
    private H2DbNaming() {

    }

}