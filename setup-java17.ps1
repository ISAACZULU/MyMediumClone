Write-Host "Setting up Java 17 environment variables..." -ForegroundColor Green

# Set JAVA_HOME to your Java 17 installation
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

# Add Java bin directory to PATH
$env:PATH = "C:\Program Files\Java\jdk-17\bin;$env:PATH"

# Verify the setup
Write-Host ""
Write-Host "JAVA_HOME is set to: $env:JAVA_HOME" -ForegroundColor Yellow
Write-Host ""

Write-Host "Java version:" -ForegroundColor Cyan
java -version
Write-Host ""

Write-Host "Java compiler version:" -ForegroundColor Cyan
javac -version
Write-Host ""

Write-Host "Maven version:" -ForegroundColor Cyan
mvn -version
Write-Host ""

Write-Host "Java 17 setup complete!" -ForegroundColor Green
Write-Host ""
Write-Host "To make these changes permanent, add the following to your system environment variables:" -ForegroundColor Yellow
Write-Host "JAVA_HOME = $env:JAVA_HOME"
Write-Host "Add $env:JAVA_HOME\bin to your PATH variable"
Write-Host "" 