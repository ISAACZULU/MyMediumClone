@echo off
echo Setting up Java 17 environment variables...

:: Set JAVA_HOME to your Java 17 installation
set "JAVA_HOME=C:\Program Files\Java\jdk-17"

:: Add Java bin directory to PATH
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Verify the setup
echo.
echo JAVA_HOME is set to: %JAVA_HOME%
echo.
echo Java version:
java -version
echo.
echo Java compiler version:
javac -version
echo.
echo Maven version:
mvn -version
echo.
echo Java 17 setup complete!
echo.
echo To make these changes permanent, add the following to your system environment variables:
echo JAVA_HOME = %JAVA_HOME%
echo Add %JAVA_HOME%\bin to your PATH variable
echo.
pause 