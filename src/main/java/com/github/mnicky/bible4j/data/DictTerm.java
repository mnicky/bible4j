package com.github.mnicky.bible4j.data;

public final class DictTerm {
    
    private final String name;
    
    private final String definition;
    
    public DictTerm(String name, String definition) {
	this.name = name;
	this.definition = definition;
    }

    public String getName() {
	return name;
    }

    public String getDefinition() {
	return definition;
    }
    
    @Override
    public String toString() {
	return name + " - " + definition;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this)
	    return true;
	if (!(obj instanceof DictTerm))
	    return false;
	DictTerm term = (DictTerm) obj;
	return term.name.equals(this.name) && term.definition.equals(this.definition);
    }

    @Override
    public int hashCode() {
	int result = 17;
	result = 31 * result + (name == null ? 0 : name.hashCode());
	result = 31 * result + (definition == null ? 0 : definition.hashCode());
	return result;
    }

}
