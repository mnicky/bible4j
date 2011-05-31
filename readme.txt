About
=======
Bible4j is a simple Bible viewer for command line.


Information
=============

- Author: mnicky
- E-mail: xmnicky [at] gmail.com
- Web:    http://mnicky.github.com

- License: The MIT License (http://www.opensource.org/licenses/mit-license.php)



Requirements
==============

In order to build Bible4j from sources you need:

- Apache Maven (2.0.11 or higher) - http://maven.apache.org/download
                                  - http://maven.apache.org/download#Installation

- Java SE - JDK 6 or higher - http://java.sun.com/javase/downloads

- and internet connection :-)


In order to run and use bible4j you need:

- Java SE - JRE 6 or higher - http://java.sun.com/javase/downloads



How To Build From Sources With Apache Maven
=============================================

If you don't know how to use Apache Maven, simply execute these commands from
command prompt:

- To run only tests (reports will be in 'target/surefire-reports/'):

    mvn test


- To run tests and build jar package (will be in 'target/' directory):

    mvn clean package


- To create jar with sources (in 'target/'):

    mvn source:jar


- To create javadocs (in 'target/apidocs/') and jar with them (in 'target/'):

    mvn javadoc:jar



For more Maven documentation see: http://maven.apache.org/guides



How to run
============

Maven will create two files in the 'target/' directory:
    - bible4j-VERSION                  - app without external dependencies
    - bible4j-VERSION-with-depends.jar - with external dependencies, ready to run


Bible4j doesn't need to be installed and is ready to be used. It can be launched
from command line in a standard way like all Java applications, with the command:

	java -jar bible4j-VERSION-with-depends.jar



Database
==========
Bible4j uses the H2 database (http://h2database.com) as its backing storage.
The database file named 'bible4j.h.db' is created in the user home directory
by default.

It is possible to use alternative database settings, which can be set using these
Java system properties at the run time:

    db.url  - database url (dafault is: jdbc:h2:~/bible4j;MVCC=TRUE)
    db.user - database login (default is 'bible4j')
    db.pwd  - database password (not used by default)

    Example of use:

    java -Ddb.user="john" -jar bible4j-VERSION-with-depends.jar



Logging
=========

Bible4j uses a SLF4J (http://slf4j.org) as a logging abstraction and currently
uses java.util.logging as a logging framework. The logging is disabled by default,
but can be enabled using these Java system properties at the run time:

    log.level - sets the logging level
    log.file  - path to xml log file - if not set, Bible4j will log to the console

Values allowed for 'log.level' are ALL, FINEST, FINER, FINE, CONFIG, INFO,
WARNING, SEVERE, OFF (from the most verbose one to the least).

    Example of use:

    java -Dlog.level="WARNING" -Dlog.file="bible4j.log" -jar bible4j-VERSION-with-depends.jar


H2 database also uses its own logging system when error occurs on its side.
The logfile is named 'bible4j.trace.db' and it is created in the same directory
as the storage file 'bible4j.h2.db' (user home directory by default).



Where To Get Bibles
=====================

Some Bibles in OSIS XML format for import can be found on page:
http://sourceforge.net/projects/zefania-sharp/files/Osis%20XML%20Modules%20(raw)

You can also use some of the existing tools to convert Bible modules from other
formats (e.g. Sword modules) to OSIS.
