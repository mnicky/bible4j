package com.github.mnicky.bible4j;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.github.mnicky.bible4j.cli.CommandParser;
import com.github.mnicky.bible4j.parsers.BibleExporterException;
import com.github.mnicky.bible4j.parsers.BibleImporterException;
import com.github.mnicky.bible4j.storage.BibleStorage;
import com.github.mnicky.bible4j.storage.BibleStorageException;
import com.github.mnicky.bible4j.storage.H2DbBibleStorage;

public final class AppRunner {
    
    public static void main(String[] args) throws BibleStorageException, BibleImporterException, BibleExporterException, IOException, SQLException {
	CommandParser cp = new CommandParser(getStorage());
	cp.launch(args);
    }

    private static BibleStorage getStorage() throws SQLException {
	return new H2DbBibleStorage(DriverManager.getConnection("jdbc:h2:tcp://localhost/test", "test", ""));
    }

}
