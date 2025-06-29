# JDK 24 Setup Script for Windows PowerShell
Write-Host "Setting up JDK 24 environment variables..." -ForegroundColor Green

# Set JAVA_HOME to your JDK 24 installation
$javaHome = "C:\Users\zisaa\Downloads\openjdk-24.0.1_windows-x64_bin"

# Set environment variables for current session
$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;$env:PATH"

Write-Host "`nJAVA_HOME is set to: $env:JAVA_HOME" -ForegroundColor Yellow

# Verify the setup
Write-Host "`nJava version:" -ForegroundColor Cyan
try {
    java -version
} catch {
    Write-Host "Java not found in PATH" -ForegroundColor Red
}

Write-Host "`nJava compiler version:" -ForegroundColor Cyan
try {
    javac -version
} catch {
    Write-Host "Java compiler not found in PATH" -ForegroundColor Red
}

Write-Host "`nMaven version:" -ForegroundColor Cyan
try {
    mvn -version
} catch {
    Write-Host "Maven not found in PATH" -ForegroundColor Red
}

Write-Host "`nJDK 24 setup complete!" -ForegroundColor Green
Write-Host "`nTo make these changes permanent:" -ForegroundColor Yellow
Write-Host "1. Open System Properties > Advanced > Environment Variables" -ForegroundColor White
Write-Host "2. Add new System Variable: JAVA_HOME = $javaHome" -ForegroundColor White
Write-Host "3. Edit PATH variable and add: $javaHome\bin" -ForegroundColor White

Read-Host "`nPress Enter to continue" 