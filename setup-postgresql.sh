#!/bin/bash

# PostgreSQL Setup Script for Medium Clone
# This script helps set up PostgreSQL for the project

echo "Setting up PostgreSQL for Medium Clone..."

# Check if PostgreSQL is installed
if ! command -v psql &> /dev/null; then
    echo "PostgreSQL is not installed. Please install it first:"
    echo "Ubuntu/Debian: sudo apt install postgresql postgresql-contrib"
    echo "macOS: brew install postgresql"
    echo "Windows: Download from https://www.postgresql.org/download/windows/"
    exit 1
fi

# Check if PostgreSQL service is running
if ! pg_isready -q; then
    echo "PostgreSQL service is not running. Starting it..."
    if command -v systemctl &> /dev/null; then
        sudo systemctl start postgresql
    elif command -v brew &> /dev/null; then
        brew services start postgresql
    else
        echo "Please start PostgreSQL service manually"
        exit 1
    fi
fi

# Create database
echo "Creating database 'mediumclone'..."
sudo -u postgres psql -c "CREATE DATABASE mediumclone;" 2>/dev/null || echo "Database 'mediumclone' already exists"

# Create user (optional)
read -p "Do you want to create a separate user for the application? (y/n): " create_user
if [[ $create_user == "y" || $create_user == "Y" ]]; then
    read -p "Enter username for the application: " app_user
    read -s -p "Enter password for the user: " app_password
    echo
    
    sudo -u postgres psql -c "CREATE USER $app_user WITH PASSWORD '$app_password';" 2>/dev/null || echo "User '$app_user' already exists"
    sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE mediumclone TO $app_user;"
    sudo -u postgres psql -d mediumclone -c "GRANT ALL ON SCHEMA public TO $app_user;"
    
    echo "User '$app_user' created successfully"
    echo "Update application.properties with:"
    echo "spring.datasource.username=$app_user"
    echo "spring.datasource.password=$app_password"
else
    echo "Using default 'postgres' user"
    echo "Make sure to update the password in application.properties"
fi

echo "PostgreSQL setup completed!"
echo "You can now run the application with: mvn spring-boot:run" 