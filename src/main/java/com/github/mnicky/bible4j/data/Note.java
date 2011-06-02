package com.github.mnicky.bible4j.data;

/**
 * Represents note.
 */
public final class Note implements Comparable<Note> {

    public enum NoteType {
	COMMENTARY,
	USER_NOTE;

	/**
	 * Returns char specifying the NoteType. This method is intented for use with databases that doesn't
	 * have built-in enum type.
	 * 
	 * @return char specifying this NoteType
	 */
	public char getSpecifyingChar() {
	    return this.name().toLowerCase().charAt(0);
	}

    }

    private final String text;

    private final Position position;

    private final NoteType type;

    public Note(String text, Position position, NoteType type) {
	this.text = text;
	this.position = position;
	this.type = type;
    }

    /**
     * Returns NoteType conforming the specified character. This method is intented for use with databases
     * that doesn't have built-in enum type.
     * 
     * @param ch character specifying the NoteType
     * @return NoteType conforming the specified character
     */
    public static NoteType getNoteTypeByChar(char ch) {
	switch (Character.toLowerCase(ch)) {
	    case 'c':
		return NoteType.COMMENTARY;
	    case 'u':
	    default:
		return NoteType.USER_NOTE;
	}

    }

    public String getText() {
	return text;
    }

    public Position getPosition() {
	return position;
    }

    public NoteType getType() {
	return type;
    }

    @Override
    public String toString() {
	return text + " - " + position.toString() + " (" + type + ")";
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof Note))
	    return false;
	Note other = (Note) obj;
	if (this.position == null) {
	    if (other.position != null)
		return false;
	}
	else if (!this.position.equals(other.position))
	    return false;
	if (this.text == null) {
	    if (other.text != null)
		return false;
	}
	else if (!this.text.equals(other.text))
	    return false;
	if (this.type != other.type)
	    return false;
	return true;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((this.position == null) ? 0 : this.position.hashCode());
	result = prime * result + ((this.text == null) ? 0 : this.text.hashCode());
	result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
	return result;
    }

    @Override
    public int compareTo(Note n) {
	return this.position.compareTo(n.getPosition());
    }

}
