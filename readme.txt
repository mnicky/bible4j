
About
=======
Bible4j is a simple Bible tool for Java


Information
=============

- Author: Marek Srank (aka Mnicky)
- E-mail: xmnicky [at] gmail.com
- Web:	  http://mnicky.github.com

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

