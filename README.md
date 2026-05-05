# Inventory Management System

A desktop inventory management system built with JavaFX and SQLite.

## Features

- Login screen with a loading animation
- Add, update, delete, and search products
- Local SQLite database storage
- Dark-themed JavaFX UI

## Project Structure

- `com/inventory/` - Java source files
- `lib/` - external libraries such as SQLite JDBC
- `styles.css` - application styling
- `RUN_ME_FIXED.bat` - quick start script for Windows
- `build.xml` - Ant build file

## Requirements

- Java JDK 17 or later
- JavaFX SDK 22
- Windows for the provided batch script

## How To Run

1. Download JavaFX SDK 22 from [OpenJFX](https://openjfx.io/).
2. Extract it and place the folder as `javafx-sdk-22` in the project root, or update the paths in `RUN_ME_FIXED.bat` and `build.xml`.
3. Make sure the SQLite JDBC jar exists in `lib/`.
4. Run `RUN_ME_FIXED.bat`.

## Default Login

- Username: `admin`
- Password: `admin123`

## Notes

- The SQLite database file is created automatically on first run.
- Generated files and local runtime files are ignored in `.gitignore`.
- This repository does not include the JavaFX SDK folder because it is large and should be downloaded separately.

## Future Improvements

- Replace hardcoded login credentials with a secure auth flow
- Track products by database ID in the UI layer
- Add input validation for negative values and duplicate products
- Add tests and packaging instructions
