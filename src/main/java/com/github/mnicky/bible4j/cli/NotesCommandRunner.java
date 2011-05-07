package com.github.mnicky.bible4j.cli;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.mnicky.bible4j.Utils;
import com.github.mnicky.bible4j.data.Note;
import com.github.mnicky.bible4j.data.Note.NoteType;
import com.github.mnicky.bible4j.data.Position;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public class NotesCommandRunner extends CommandRunner {
    
    private List<Position> positions;
    
    private String textOfNote = null;
    
    private List<Note> notes;

    public NotesCommandRunner(BibleStorage bibleStorage) {
	super(bibleStorage);
    }

    @Override
    public void parseCommandLine(String[] args) {
	positions = Utils.parsePositions(getFirstValue(args).toLowerCase(new Locale("en")));
	if (isArgumentPresent(ADD_ARGUMENT, args)) 
	    textOfNote = parseAddText(args);
    }
    
    @Override
    void doAction() throws BibleStorageException {
	retrieveOrAddNote();
	displayNotes();
        
    }
    
    private void displayNotes() {
	if (notes == null)
	    return;
	System.out.println("Notes:");
	System.out.println();
	System.out.println("Verse \t\t Note");
	System.out.println("------------------------------------------------------------");
	for (Note note : notes)
	    System.out.println(note.getPosition() + " \t " + note.getText());
    }

    private void retrieveOrAddNote() throws BibleStorageException {
	
	if (positions.isEmpty())
	    throw new IllegalArgumentException("Coordinate of note not specified");
	
	if (textOfNote != null) {
	    if (Utils.isWholeChapter(positions.get(0)))
		throw new IllegalArgumentException("Notes cannot be added to whole chapters.");
	    bibleStorage.insertNote(new Note(textOfNote, positions.get(0), NoteType.USER_NOTE));
	    System.out.println("Note inserted.");
	}
	else {
	    notes = new ArrayList<Note>();
	    
	    if (Utils.isWholeChapter(positions.get(0)))
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
        System.out.println("\t" + CommandParser.NOTES_COMMAND + " POSITION [" + ADD_ARGUMENT + " NOTE_TEXT]");
        
        System.out.println();
        System.out.println("\tPOSITION \t Bible coordinates without spaces");
        System.out.println("\tNOTE_TEXT \t Text of note to add");
        
        System.out.println();
        System.out.println("\tTo view notes, enter just Bible coordinates.");
        System.out.println("\tTo add note, use argument '" + ADD_ARGUMENT + "' and specify the text of note.");
        System.out.println("\tNotes can only be added to one verse. If more verses are specified, the first one is used.");
        System.out.println("\tSee '" + CommandParser.HELP_COMMAND + " " + CommandParser.BIBLE_READ_COMMAND + "' for description of how to define Bible coordinates.");
        
        System.out.println();
        System.out.println("Examples:");
        
        System.out.println();
        System.out.println("  View notes for specified verses:");
        System.out.println();
        System.out.println("\t" + CommandParser.NOTES_COMMAND + " Mt23,12");
        System.out.println("\t" + CommandParser.NOTES_COMMAND + " Jn16:1-10.20-30");
        
        System.out.println();
        System.out.println("  View notes for whole specified chapters:");
        System.out.println();
        System.out.println("\t" + CommandParser.NOTES_COMMAND + " Exodus1");
        System.out.println("\t" + CommandParser.NOTES_COMMAND + " Acts1-5");
        
        System.out.println();
        System.out.println("  Add note to verse:");
        System.out.println();
        System.out.println("\t" + CommandParser.NOTES_COMMAND + " Lk3:12 " + ADD_ARGUMENT + " \"This is note text\"");    
        
    }
    
    
    
    public static void main(String[] args) throws SQLException, BibleStorageException {
	BibleStorage storage = new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
	NotesCommandRunner p = new NotesCommandRunner(storage);
	
//	String[] params = {"Jn1,2", ADD_ARGUMENT, "This is my second note :-)"};
//	p.parse(params);
//	p.retrieveOrAddNote();
	
	String[] params2 = {"Jn1"};
	p.parseCommandLine(params2);
	p.doAction();
	
    }

}
