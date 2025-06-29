@echo off
echo Testing JDK 24 setup...

:: Set JAVA_HOME
set "JAVA_HOME=C:\Users\zisaa\Downloads\openjdk-24.0.1_windows-x64_bin"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo JAVA_HOME: %JAVA_HOME%
echo.

echo Testing Java version:
java -version
echo.

echo Testing Java compiler:
javac -version
echo.

echo Testing Maven:
mvn -version
echo.

echo Compiling the project:
mvn clean compile
echo.

echo Starting the application:
mvn spring-boot:run
echo.

pause 