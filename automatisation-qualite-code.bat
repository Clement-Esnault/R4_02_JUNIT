SET MAVEN_HOME=c:\data\apache-maven-3.9.12
SET PATH=%MAVEN_HOME%\bin;%PATH%
mvn clean
mvn test jacoco:report surefire-report:report site