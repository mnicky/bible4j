package com.github.mnicky.bible4j.cli;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.mnicky.bible4j.data.Note;
import com.github.mnicky.bible4j.data.Note.NoteType;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public class NotesCommandParser extends CommandParser {
    
    private List<Position> positions;
    
    private String textOfNote = null;
    
    private List<Note> notes;

    public NotesCommandParser(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    public void parse(String[] args) {
	positions = parsePositions(getFirstValue(args).toLowerCase(new Locale("en")));
	if (isArgumentPresent(ADD_ARGUMENT, args)) 
	    textOfNote = parseText(args);
    }
    
    public void retrieveOrAddNote() throws BibleStorageException {
	
	if (positions.isEmpty())
	    throw new IllegalArgumentException("Coordinate of note not specified");
	
	if (textOfNote != null) {
	    if (wholeChaptersRequested)
		throw new IllegalArgumentException("Notes cannot be added to whole chapters.");
	    bibleStorage.insertNote(new Note(textOfNote, positions.get(0), NoteType.USER_NOTE));
	}
	else {
	    notes = new ArrayList<Note>();
	    
	    if (wholeChaptersRequested)
    	    	for (Position position : positions) 
    	    	    notes.addAll(bibleStorage.getNotesForChapter(position));
	    else
		for (Position position : positions) 
    	    	    notes.addAll(bibleStorage.getNotes(position));
	}
	    
    }
    
    public List<Note> getNotes() {
	return notes;
    }

    @Override
    public void printHelp() {
	System.out.println("Usage:");
        System.out.println("\t" + CommandParserLauncher.NOTES_COMMAND + " POSITION [" + ADD_ARGUMENT + " NOTE_TEXT]");
        
        System.out.println();
        System.out.println("\tPOSITION \t Bible coordinates without spaces");
        System.out.println("\tNOTE_TEXT \t Text of note to add");
        
        System.out.println();
        System.out.println("\tTo view notes, enter just Bible coordinates.");
        System.out.println("\tTo add note, use argument '" + ADD_ARGUMENT + "' and specify the text of note.");
        System.out.println("\tNotes can only be added to one verse. If more verses are specified, the first one is used.");
        System.out.println("\tSee '" + CommandParserLauncher.HELP_COMMAND + " " + CommandParserLauncher.BIBLE_READ_COMMAND + "' for description of how to define Bible coordinates.");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  View notes for specified verses:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.NOTES_COMMAND + " Mt23,12");
        System.out.println("\t" + CommandParserLauncher.NOTES_COMMAND + " Jn16:1-10.20-30");
        
        System.out.println();
        System.out.println("  View notes for whole specified chapters:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.NOTES_COMMAND + " Exodus1");
        System.out.println("\t" + CommandParserLauncher.NOTES_COMMAND + " Acts1-5");
        
        System.out.println();
        System.out.println("  Add note to verse:");
        System.out.println();
        System.out.println("\t" + CommandParserLauncher.NOTES_COMMAND + " Lk3:12 " + ADD_ARGUMENT + " \"This is note text\"");    
        
    }
    
    
    
    public static void main(String[] args) throws SQLException, BibleStorageException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	NotesCommandParser p = new NotesCommandParser(storage);
	
//	String[] params = {"Jn1,2", ADD_ARGUMENT, "This is my second note :-)"};
//	p.parse(params);
//	p.retrieveOrAddNote();
	
	String[] params2 = {"Jn1,5"};
	p.parse(params2);
	p.retrieveOrAddNote();
	System.out.println();
	List<Note> notes = p.getNotes(); 
	for (Note v : notes)
	    System.out.println(v == null ? "no text found" : v.getText());
    }

}
