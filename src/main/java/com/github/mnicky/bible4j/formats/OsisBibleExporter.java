package com.github.mnicky.bible4j.formats;

import java.io.OutputStream;

import javax.xml.crypto.NoSuchMechanismException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.github.mnicky.bible4j.data.BibleVersion;
import com.github.mnicky.bible4j.storage.BibleStorage;

public final class OsisBibleExporter implements BibleExporter {

    @Override
    public void export(BibleVersion bible, BibleStorage storage, OutputStream stream) throws BibleExporterException {
	
	try {
	    
	    XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(stream);
	    
	    
	    
	} catch (XMLStreamException e) {
	    throw new BibleExporterException("Exporting error", e);
	}
	
    }


}
