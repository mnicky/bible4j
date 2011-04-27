package com.github.mnicky.bible4j.data;

import hirondelle.date4j.DateTime;

import java.util.Collections;
import java.util.List;

public final class DailyReading {
    
    private final String readingListName;
    
    private final DateTime date;
    
    private final List<Position> positions;
    
    public DailyReading(String readingListName, DateTime date, List<Position> positions) {
	this.readingListName = readingListName;
	this.date = date;
	this.positions = Collections.unmodifiableList(positions);
    }

    public String getReadingListName() {
	return readingListName;
    }

    public DateTime getDate() {
	return date;
    }

    public List<Position> getPositions() {
	return positions;
    }
    
    @Override
    public String toString() {
	return readingListName + " - " + date.format("DD-MM-YYYY");
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this)
	    return true;
	if (!(obj instanceof DailyReading))
	    return false;
	DailyReading dr = (DailyReading) obj;
	return dr.readingListName.equals(this.readingListName) && dr.date.equals(this.date) &&
	dr.positions.equals(this.positions);
    }

    @Override
    public int hashCode() {
	int result = 17;
	result = 31 * result + (readingListName == null ? 0 : readingListName.hashCode());
	result = 31 * result + (date == null ? 0 : date.hashCode());
	result = 31 * result + (positions == null ? 0 : positions.hashCode());
	return result;
    }

}
