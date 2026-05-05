@echo off
chcp 65001 > nul

echo ===================================
echo   INVENTORY MANAGEMENT SYSTEM
echo ===================================
echo.

echo Compiling Java files...

javac -cp ".;lib/*;javafx-sdk-22/lib/*" --module-path "javafx-sdk-22/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,java.sql com/inventory/LoginScreen.java com/inventory/InventoryApp.java com/inventory/Product.java com/inventory/DatabaseManager.java

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Please contact your instructor for assistance.
    echo.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Starting application...
echo.
echo LOGIN CREDENTIALS:
echo Username: admin
echo Password: admin123
echo.

java -cp ".;lib/*;javafx-sdk-22/lib/*" --module-path "javafx-sdk-22/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,java.sql com.inventory.LoginScreen

echo.
echo Application closed.
pause